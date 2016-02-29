package com.kx;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.kx.error.KException;
import com.kx.kdb.K;

import tszielin.qlab.error.ArgumentException;
import tszielin.qlab.event.ConnectionClosed;
import tszielin.qlab.util.event.FireData;
import tszielin.qlab.util.listener.DataListener;

public class KdbConnection {
  private DataInputStream input;
  private OutputStream output;
  private byte[] inputArray;
  private int inputIndex;
  private int vs;
  boolean be;
  
  private BlockingQueue<Object> responses = new LinkedBlockingQueue<Object>();
  private boolean closed = true;

  private FireData fireData;
  
  private String host;
  private int port;
  private String credentials;
  
  private void open(Socket socket) throws IOException {
    socket.setTcpNoDelay(true);    
    this.input = new DataInputStream(socket.getInputStream());
    this.output = socket.getOutputStream();
  }

  public void close() {
    try {
      if (input != null) {
        try {
          input.close();
        }
        catch (IOException ignored) {
        }
        finally {
          input = null;
        }
      }
      if (output != null) {
        try {
          output.close();
        }
        catch (IOException ignored) {
        }
        finally {
          output = null;
        }
      }
    }
    finally {
      closed = true;
    }
  }

  public KdbConnection(Socket socket) throws IOException {
    open(socket);
    input.read(inputArray = new byte[99]);
    output.write(inputArray, 0, 1);
  }

  public KdbConnection(ServerSocket socket) throws IOException {
    this(socket.accept());
  }

  public class K4AccessException extends Exception {
    private static final long serialVersionUID = -462362139529732819L;

    K4AccessException(String message) {
      super(message);
    }

    K4AccessException(Throwable cause) {
      super(cause);
    }

    K4AccessException(String message, Throwable cause) {
      super(message, cause);
    }
  }

  public boolean isClosed() {
    return closed;
  }

  public K.KType<?> getResponse() throws Throwable {
    Object obj = responses.take();

    if (obj instanceof Throwable)
      throw (Throwable)obj;

    return obj instanceof K.KType ? (K.KType<?>)obj : null;
  }

  private void startReader() {
    new Thread(new Runnable() {
      @Override
      public void run() {
        while (!closed) {       
          Object obj = null;

          try {
            obj = read();
          }
          catch (KException ex) {
            obj = ex;
          }
          catch (Throwable cause) {
            obj = cause;
            if (!closed) {
              if (fireData != null && fireData.getDataListeners() != null && 
                  fireData.getDataListeners().length > 0 && host != null) {
                try {
                  fireData.onData(new ConnectionClosed(this, host, port));
                }
                catch (ArgumentException ignored) {
                }
              }
              close();
            }
          }
          responses.add(obj);
        }
      }
    }).start();
  }

  public void reconnect(boolean retry) throws IOException, KException {
    open(new Socket(host, port));
    java.io.ByteArrayOutputStream baos = new ByteArrayOutputStream();
    java.io.DataOutputStream dos = new DataOutputStream(baos);
    try {
      dos.write((credentials + (retry ? "\3" : "")).getBytes());
      dos.writeByte(0);
      dos.flush();
      output.write(baos.toByteArray());
      byte[] array = new byte[2 + credentials.getBytes().length];
      if (1 != input.read(array, 0, 1))
        if (retry)
          reconnect(false);
        else
          throw new KException("Authentication failed");
      closed = false;
      vs = Math.min(array[0], 3);
      startReader();
    }
    finally {
      if (dos != null) {
        try {
          dos.close();
        }
        catch(IOException ignored) {          
        }
      }
      if (baos != null) {
        try {
          baos.close();
        }
        catch(IOException ignored) {          
        }
      }
    }
  }

  public void testConnection(boolean retry) throws IOException, KException {
    open(new Socket(host, port));
    java.io.ByteArrayOutputStream baos = new ByteArrayOutputStream();
    java.io.DataOutputStream dos = new DataOutputStream(baos);
    try {
    dos.write((credentials + (retry ? "\3" : "")).getBytes());
    dos.writeByte(0);
    dos.flush();
    output.write(baos.toByteArray());
      byte[] array = new byte[2 + credentials.getBytes().length];
      if (1 != input.read(array, 0, 1)) {
        if (retry)
          testConnection(false);
        else
          throw new KException("Authentication failed");
      }
    }
    finally {
      if (dos != null) {
        try {
          dos.close();
        }
        catch(IOException ignored) {          
        }
      }
      if (baos != null) {
        try {
          baos.close();
        }
        catch(IOException ignored) {          
        }
      }
      close();
    }
  }
  
  public void addDataListener(DataListener listener) {
    if (fireData == null && listener != null) {
      fireData = new FireData();
    }
    if (listener != null) {
      fireData.addDataListener(listener);
    }
  }
  
  public void removeDataListener(DataListener listener) {
    if (fireData != null && listener != null) {
      fireData.removeDataListener(listener);
    }
  }

  public String getInfo() {
    return host + ":" + port;
  }

  public KdbConnection(String host, int port, String credentials) {
    this.host = host;
    this.port = port;
    this.credentials = credentials;
  } 
  
  private byte readByte() {
    return inputArray[inputIndex++];
  }

  private short readShort() {
    int x = inputArray[inputIndex++], y = inputArray[inputIndex++];
    return (short)(be ? x & 0xff | y << 8 : x << 8 | y & 0xff);
  }

  private int readInteger() {
    int x = readShort(), y = readShort();
    return be ? x & 0xffff | y << 16 : x << 16 | y & 0xffff;
  }

  private long readLong() {
    int x = readInteger(), y = readInteger();
    return be ? x & 0xffffffffL | (long)y << 32 : (long)x << 32 | y & 0xffffffffL;
  }

  private float readReal() {
    return Float.intBitsToFloat(readInteger());
  }

  private double readFloat() {
    return Double.longBitsToDouble(readLong());
  }
  
  private UUID readGuid() {
    boolean oa = be;
    be = false;
    UUID g = new UUID(readLong(), readLong());
    be = oa;
    return g;
  }

  private K.KSymbol readSymbol() {
    int k = inputIndex;
    for (; inputArray[k] != 0;)
      ++k;
    char[] s = new char[k - inputIndex];
    for (int i = 0; inputIndex < k;)
      s[i++] = (char)(0xFF & readByte());
    ++inputIndex;
    return K.KSymbol.valueOf(s);
  }

  private K.UnaryPrimitive readUnaryPrimitive() {
    return new K.UnaryPrimitive(inputArray[inputIndex++]);
  }

  private K.BinaryPrimitive readBinaryPrimitive() {
    return new K.BinaryPrimitive(inputArray[inputIndex++]);
  }

  private K.TernaryOperator readTernaryOperator() {
    return new K.TernaryOperator(inputArray[inputIndex++]);
  }

  private K.Function readFunction() {
    readSymbol();
    return new K.Function((K.KCharacterArray)streamToType());
  }

  private K.FEach readFEach() {
    return new K.FEach(streamToType());
  }

  private K.FOver readFOver() {
    return new K.FOver(streamToType());
  }

  private K.FScan readFScan() {
    return new K.FScan(streamToType());
  }

  private K.FComposition readFComposition() {
    int n = readInteger();
    Object[] objs = new Object[n];
    for (int i = 0; i < n; i++)
      objs[i] = streamToType();

    return new K.FComposition(objs);
  }

  private K.FPrior readFPrior() {
    return new K.FPrior(streamToType());
  }

  private K.FEachRight readFEachRight() {
    return new K.FEachRight(streamToType());
  }

  private K.FEachLeft readFEachLeft() {
    return new K.FEachLeft(streamToType());
  }

  private K.Projection readProjection() {
    int n = readInteger();
    K.KList list = new K.KList(n);
    K.KType<?>[] array = (K.KType<?>[])list.getArray();
    for (int i = 0; i < n; i++)
      array[i] = streamToType();

    return new K.Projection(list);
  }

  private K.KType<?> streamToType() {    
    byte type = readByte();
    if (type < 0) 
      switch (type) {
        case K.KBoolean.TYPE:
          return K.KBoolean.valueOf(inputArray[inputIndex++]==1);
        case K.KGuid.TYPE:
          return K.KGuid.valueOf(readGuid());
        case K.KByte.TYPE:
          return K.KByte.valueOf(inputArray[inputIndex++]);
        case K.KShort.TYPE:
          return K.KShort.valueOf(readShort());
        case K.KInteger.TYPE:
          return K.KInteger.valueOf(readInteger(), vs == 3);
        case K.KLong.TYPE:
          return K.KLong.valueOf(readLong(), vs == 3);
        case K.KReal.TYPE:
          return K.KReal.valueOf(readReal());
        case K.KFloat.TYPE:
          return K.KFloat.valueOf(readFloat());
        case K.KCharacter.TYPE:
          return K.KCharacter.valueOf(inputArray[inputIndex++] & 0xff);
        case K.KSymbol.TYPE:
          return readSymbol();
        case K.KTimestamp.TYPE:
          return K.KTimestamp.valueOf(readLong());
        case K.KMonth.TYPE:
          return K.KMonth.valueOf(readInteger());
        case K.KDate.TYPE:
          return K.KDate.valueOf(readInteger());
        case K.KDatetime.TYPE:
          return K.KDatetime.valueOf(readFloat());
        case K.KTimespan.TYPE:
          return K.KTimespan.valueOf(readLong());
        case K.KMinute.TYPE:
          return K.KMinute.valueOf(readInteger());
        case K.KSecond.TYPE:
          return K.KSecond.valueOf(readInteger());
        case K.KTime.TYPE:
          return K.KTime.valueOf(readInteger());
      }

    switch (type) {
      case K.Function.TYPE:
        return readFunction(); // fn - lambda
      case K.UnaryPrimitive.TYPE:
        return readUnaryPrimitive(); // unary primitive
      case K.BinaryPrimitive.TYPE:
        return readBinaryPrimitive(); // binary primitive
      case K.TernaryOperator.TYPE:
        return readTernaryOperator();
      case K.Projection.TYPE:
        return readProjection(); // fn projection
      case K.FComposition.TYPE:
        return readFComposition();
      case K.FEach.TYPE:
        return readFEach(); // f'
      case K.FOver.TYPE:
        return readFOver(); // f/
      case K.FScan.TYPE:
        return readFScan(); // f\
      case K.FPrior.TYPE:
        return readFPrior(); // f':
      case K.FEachRight.TYPE:
        return readFEachRight(); // f/:
      case K.FEachLeft.TYPE:
        return readFEachLeft(); // f\:
      case 112:
        // dynamic load
        inputIndex++;
        return null;
    }
    
    if (type == 127) 
      type = K.KDictionary.TYPE;
    
    if (type > K.KDictionary.TYPE) {
      inputIndex++;
      return null;
    }
    
    if (type == K.KDictionary.TYPE)
      return new K.KDictionary(streamToType(), streamToType());
    byte attr = inputArray[inputIndex++];
    if (type == K.KTable.TYPE)
      return new K.KTable((K.KDictionary)streamToType());
    
    int len = readInteger();
    int count = 0;
    K.KBaseArray vec = null;
    switch (type) {
      case K.KList.TYPE: {
        vec = new K.KList(len);
        vec.setAttribute(attr);
        K.KType<?>[] array = (K.KType[])vec.getArray();
        for (; count < len; count++)
          array[count] = streamToType();
        return vec;
      }
      case K.KBooleanArray.TYPE: {
        vec = new K.KBooleanArray(len);
        vec.setAttribute(attr);
        boolean[] array = (boolean[])vec.getArray();
        for (; count < len; count++)
          array[count] = readByte() == 1;
        return vec;
      }
      case K.KGuid.TYPE: {
        vec = new K.KGuidArray(len);
        vec.setAttribute(attr);
        UUID[] array = (UUID[])vec.getArray();
        for (; count < len; count++)
          array[count] = readGuid();
        return vec;
      }
      case K.KByteArray.TYPE: {
        vec = new K.KByteArray(len);
        vec.setAttribute(attr);
        byte[] array = (byte[])vec.getArray();
        for (; count < len; count++)
          array[count] = readByte();
        return vec;
      }
      case K.KShortArray.TYPE: {
        vec = new K.KShortArray(len);
        vec.setAttribute(attr);
        short[] array = (short[])vec.getArray();
        for (; count < len; count++)
          array[count] = readShort();
        return vec;
      }
      case K.KIntegerArray.TYPE:
      case K.KMonthArray.TYPE:
      case K.KDateArray.TYPE:
      case K.KMinuteArray.TYPE:
      case K.KSecondArray.TYPE:
      case K.KTimeArray.TYPE: {
        vec = type == K.KMinuteArray.TYPE ? new K.KMinuteArray(len) :
          type == K.KTimeArray.TYPE ? new K.KTimeArray(len) :
          type == K.KSecondArray.TYPE ? new K.KSecondArray(len) :
          type == K.KDateArray.TYPE ? new K.KDateArray(len) : 
          type == K.KMonthArray.TYPE ? new K.KMonthArray(len) : new K.KIntegerArray(len, vs >= 3);
        vec.setAttribute(attr);
        int[] array = (int[])vec.getArray();
        for (; count < len; count++)
          array[count] = readInteger();
        return vec;
      }
      case K.KLongArray.TYPE:
      case K.KTimestampArray.TYPE:
      case K.KTimespanArray.TYPE: {
        vec = type == K.KTimestampArray.TYPE ? new K.KTimestampArray(len) :
          type == K.KTimespanArray.TYPE ? new K.KTimespanArray(len) : new K.KLongArray(len, vs >= 3);
        vec.setAttribute(attr);
        long[] array = (long[])vec.getArray();
        for (; count < len; count++)
          array[count] = readLong();
        return vec;
      }
      case K.KRealArray.TYPE: {
        vec = new K.KRealArray(len);
        vec.setAttribute(attr);
        float[] array = (float[])vec.getArray();
        for (; count < len; count++)
          array[count] = readReal();
        return vec;
      }
      case K.KFloatArray.TYPE: 
      case K.KDatetimeArray.TYPE: {
        vec = type == K.KDatetimeArray.TYPE ? new K.KDatetimeArray(len) : new K.KFloatArray(len);
        vec.setAttribute(attr);
        double[] array = (double[])vec.getArray();
        for (; count < len; count++)
          array[count] = readFloat();
        return vec;
      }
      case K.KCharacterArray.TYPE: {
        vec = new K.KCharacterArray(len);
        vec.setAttribute(attr);
        char[] array = (char[])vec.getArray();
        for (; count < len; count++)
          array[count] = (char)(readByte() & 0xff);
        return vec;
      }
      case K.KSymbolArray.TYPE: {
        vec = new K.KSymbolArray(len);
        vec.setAttribute(attr);
        String[] array = (String[])vec.getArray();
        for (; count < len; count++)
          array[count] = readSymbol().getString();
        return vec;
      }
    }
    return null;
  }
  
  public void write(K.KType<?> x) throws IOException {
    write(true, x);
  }

  private void write(boolean sync, K.KType<?> value) throws IOException {
    java.io.ByteArrayOutputStream baosBody = new ByteArrayOutputStream();
    java.io.DataOutputStream dosBody = new DataOutputStream(baosBody);
    
    value.serialise(dosBody);

    java.io.ByteArrayOutputStream baosHeader = new ByteArrayOutputStream();
    java.io.DataOutputStream dosHeader = new DataOutputStream(baosHeader);
    dosHeader.writeByte(0);
    dosHeader.writeByte(sync ? 1 : 0);
    dosHeader.writeByte(0);
    dosHeader.writeByte(0);
    int msgSize = 8 + dosBody.size();
    K.write(dosHeader, msgSize);
    byte[] array = baosHeader.toByteArray();
    output.write(array);
    array = baosBody.toByteArray();
    output.write(array);
  }

  private Object read() throws KException, IOException {
    synchronized (input) {
      input.readFully(inputArray = new byte[8]);
      be = inputArray[0] == 1;
      boolean c = inputArray[2] == 1;
      inputIndex = 4;

      int msgLength = readInteger() - 8;

      inputArray = new byte[msgLength];
      int total = 0;
      int packetSize = 1 + msgLength / 100;
      if (packetSize < 8192)
        packetSize = 8192;

      while (total < msgLength) {
        int remainder = msgLength - total;
        if (remainder < packetSize)
          packetSize = remainder;
        total += input.read(inputArray, total, packetSize);
      }

      if (c) {
        uncompress();
      }
      else {
        inputIndex = 0;
      }

      if (inputArray[0] == -128) {
        inputIndex = 1;
        throw new KException(readSymbol().toString(true));
      }
      return streamToType();
    }
  }

  private void uncompress() {
    int n = 0, r = 0, f = 0, s = 8, p = s;
    short i = 0;
    inputIndex = 0;
    byte[] dst = new byte[readInteger()];
    int d = inputIndex;
    int[] aa = new int[256];
    while (s < dst.length) {
      if (i == 0) {
        f = 0xff & (int)inputArray[d++];
        i = 1;
      }
      if ((f & i) != 0) {
        r = aa[0xff & (int)inputArray[d++]];
        dst[s++] = dst[r++];
        dst[s++] = dst[r++];
        n = 0xff & (int)inputArray[d++];
        for (int m = 0; m < n; m++) {
          dst[s + m] = dst[r + m];
        }
      }
      else {
        dst[s++] = inputArray[d++];
      }
      while (p < s - 1) {
        aa[(0xff & (int)dst[p]) ^ (0xff & (int)dst[p + 1])] = p++;
      }
      if ((f & i) != 0) {
        p = s += n;
      }
      i *= 2;
      if (i == 256) {
        i = 0;
      }
    }
    inputArray = dst;
    inputIndex = 8;
  }  
}