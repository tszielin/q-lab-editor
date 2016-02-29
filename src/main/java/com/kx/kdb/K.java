/*
 * Studio for kdb+ by Charles Skelton is licensed under a Creative Commons
 * Attribution-Noncommercial-Share Alike 3.0 Germany License
 * http://creativecommons.org/licenses/by-nc-sa/3.0 except for the netbeans components which retain
 * their original copyright notice
 */
package com.kx.kdb;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import studio.kdb.Sorter;

public class K {
  private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat();
  private static final DecimalFormat DEC_FORMAT = new DecimalFormat("00");
  private static final String ENLIST = "enlist ";
  private static final String FLIP = "flip ";

  public static void write(OutputStream output, byte value) throws IOException {
    output.write(value);
  }

  public static void write(OutputStream output, short value) throws IOException {
    write(output, (byte)(value >> 8));
    write(output, (byte)value);
  }

  public static void write(OutputStream output, int value) throws IOException {
    write(output, (short)(value >> 16));
    write(output, (short)value);
  }

  public static void write(OutputStream output, long value) throws IOException {
    write(output, (int)(value >> 32));
    write(output, (int)value);
  }

  private static synchronized String sd(String pattern, java.util.Date date) {
    DATE_FORMAT.applyPattern(pattern);
    return DATE_FORMAT.format(date);
  }
  
  abstract public static class KType<T> {
    private T value;
    
    protected KType() {
      this(null);
    }
    
    protected KType(T value) {
      this.value = value;
    }
    
    abstract public byte getType();
    abstract public String getDataType();
    
    public void serialise(OutputStream output) throws IOException {
      write(output, getType());
    }

    abstract public String toString(boolean showType);

    @Override
    public String toString() {
      return toString(true);
    }
        
    protected T getValue() {
      return value;
    }
    
    protected void setValue(T value) {
      this.value = value;
    }
    
    public boolean isNull() {
      return false;
    }
  }

  public abstract static class KBase<T> extends KType<T>{
    private String id;
    
    protected KBase() {
      super();
    }
    
    protected KBase(T value, char id) {
      super(value);
      this.id = Character.isAlphabetic(id) ? String.valueOf(id) : "";
    }
    
    protected KBase(T value) {
      this(value, '\0');
    }
    
    protected String getId() {
      return id;
    }
    
    public T getValue() {
      return super.getValue();
    }
  }  
  
  abstract protected static class Primitive extends KBase<Integer> {    
    // .:'(:;+;-;*;%;&;|;^;$;<;>;,;#;_;~;!;?;@;.;=)
    private static Map<Integer, Character> map = new HashMap<Integer, Character>();
    private char charVal = ' ';

    public static void init(char[] ops, int[] values) {
      for (int count = 0; count < values.length; count++)
        map.put(Integer.valueOf(values[count]), Character.valueOf(ops[count]));
    }

    static {
      init(":+-*%&|^$<>,#_~!?@.=".toCharArray(), 
          new int[]{0, 1, 2, 3, 4, 5, 6, 7, 11, 9, 10, 12, 13, 14, 15, 16, 17, 18, 19, 8});
    }

    protected Primitive(int value) {
      super(value);
      Character c = map.get(Integer.valueOf(getValue()));
      if (c != null)
        charVal = c.charValue();
    }
    
    @Override
    public String getDataType() {
      return "primitive";
    }
        
    public char charValue() {
      return charVal;
    }

    public int intValue() {
      return getValue();
    }
    
    @Override
    public String toString(boolean showType) {
      return String.valueOf(charValue());
    }
  }

  public static class BinaryPrimitive extends Primitive {
    public final static byte TYPE = 102; 
    
    public BinaryPrimitive(int value) {
      super(value);
    }

    @Override
    public String getDataType() {
      return "binary primitive";
    }
    
    @Override
    public byte getType() {
      return TYPE;
    }
  }
  
  public static class TernaryOperator extends Primitive {
    public final static byte TYPE = 103;
    
    static {
      init("'/\\".toCharArray(), new int[]{0, 1, 2});
    }
    
    public TernaryOperator(int value) {
      super(value);
    }
        
    @Override
    public String getDataType() {
      return "ternary operator";
    }  
    
    @Override
    public byte getType() {
      return TYPE;
    }
  }

  public static class UnaryPrimitive extends Primitive {
    public final static byte TYPE = 101;
    
    public UnaryPrimitive(int value) {
      super(value);
    }

    @Override
    public String toString(boolean showType) {
      if (intValue() == -1)
        return "";

      return super.toString(showType) + ":";
    }
    
    @Override
    public String getDataType() {
      return "unary primitive";
    }
    
    @Override
    public byte getType() {
      return TYPE;
    }
  }

  public static class FComposition extends KBase<Object[]> {
    public final static byte TYPE = 105; 
    
    public FComposition(Object[] value) {
      super(value);
    }
    
    public String getDataType() {
      return "function composition";
    }    

    @Override
    public String toString(boolean showType) {
      return getValue().toString();
    }
    
    @Override
    public byte getType() {
      return TYPE;
    }
    
    @Override
    public boolean isNull() {
      return getValue() == null;
    }
  }
  
  abstract protected static class KAdverb extends KType<KType<?>> {
    protected KAdverb(KType<?> base) {
      super(base);
    }
    
    @Override
    public String getDataType() {
      return "adverb";
    }
    
    public Object toObject() {
      return getValue();
    }
  }

  public static class FEachLeft extends KAdverb {
    public final static byte TYPE = 111;
    
    public FEachLeft(KType<?> base) {
      super(base);
    }

    @Override
    public String toString(boolean showType) {
      return getValue() != null ? getValue().toString(showType) + "\\:" : "";
    }
    
    @Override
    public String getDataType() {
      return "function each left";
    }
    
    @Override
    public byte getType() {
      return TYPE;
    }
  }

  public static class FEachRight extends KAdverb {
    public final static byte TYPE = 110;
    
    public FEachRight(KType<?> base) {
      super(base);
    }

    @Override
    public String toString(boolean showType) {
      return getValue() != null ? getValue().toString(showType) + "/:" : "";
    }
    
    @Override
    public String getDataType() {
      return "function each right";
    }
    
    @Override
    public byte getType() {
      return TYPE;
    }
  }

  public static class FPrior extends KAdverb {
    public final static byte TYPE = 109;
    
    public FPrior(KType<?> base) {
      super(base);
    }
    
    @Override
    public String toString(boolean showType) {
      return getValue() != null ? getValue().toString(showType) + "':" : "";
    }
    
    @Override
    public String getDataType() {
      return "function prior";
    }
    
    @Override
    public byte getType() {
      return TYPE;
    }
  }

  public static class FEach extends KAdverb {
    public final static byte TYPE = 106;
    
    public FEach(KType<?> base) {
      super(base);
    }

    @Override
    public String toString(boolean showType) {
      return getValue() != null ? getValue().toString(showType) + "'" : "";
    }
    
    @Override
    public String getDataType() {
      return "function each";
    }
    
    @Override
    public byte getType() {
      return TYPE;
    }
  }

  public static class FOver extends KAdverb {
    public final static byte TYPE = 107;
    
    public FOver(KType<?> base) {
      super(base);
    }

    @Override
    public String toString(boolean showType) {
      return getValue() != null ? getValue().toString(showType) + "/" : "";
    }
    
    @Override
    public String getDataType() {
      return "function over";
    }
    
    @Override
    public byte getType() {
      return TYPE;
    }
  }

  public static class FScan extends KAdverb {
    public final static byte TYPE = 108;

    public FScan(KType<?> base) {
      super(base);
    }

    @Override
    public String toString(boolean showType) {
      return getValue() != null ? getValue().toString(showType) + "\\" : "";
    }
    
    @Override
    public String getDataType() {
      return "function scan";
    }
    
    @Override
    public byte getType() {
      return TYPE;
    }
  }

  public static class Function extends KBase<String> {
    public final static byte TYPE = 100;
    
    public Function(KCharacterArray body) {
      super(String.valueOf((char[])body.getArray(), 0, body.getLength()));
    }

    @Override
    public String getDataType() {
      return "function";
    }    

    @Override
    public String toString(boolean showType) {
      return getValue();
    }
    
    @Override
    public byte getType() {
      return TYPE;
    }
  }
  
  public static class Projection extends KType<KList> {
    public final static byte TYPE = 104;
    
    public Projection(KList list) {
      super(list);
    }
    
    @Override
    public String getDataType() {
      return "projection";
    }

    @Override
    public String toString(boolean showType) {
      boolean listProjection = false;
      
      if ((getValue().getLength() > 0) && (getValue().get(0) instanceof UnaryPrimitive)) {
        UnaryPrimitive up = (UnaryPrimitive)getValue().get(0);
        if (up.intValue() == 41) // used to be 40 ?
          listProjection = true;
      }

      StringBuilder strBuilder = new StringBuilder();
      if (listProjection) {
        strBuilder.append("(");
        for (int count = 1; count < getValue().getLength(); count++) {
          if (count > 1)
            strBuilder.append(";");
          strBuilder.append(getValue().get(count).toString(showType));
        }
        strBuilder.append(")");
      }
      else {
        boolean isFunction = false;

        for (int count = 0; count < getValue().getLength(); count++) {
          if (count == 0)
            if ((getValue().get(0) instanceof Function) || (getValue().get(0) instanceof UnaryPrimitive) ||
                (getValue().get(0) instanceof BinaryPrimitive))
              isFunction = true;
            else
              strBuilder.append("(");

          if (count > 0)
            if (count == 1)
              if (isFunction)
                strBuilder.append("[");
              else
                strBuilder.append(";");
            else
              strBuilder.append(";");

          strBuilder.append(getValue().get(count).toString(showType));
        }

        if (isFunction)
          strBuilder.append("]");
        else
          strBuilder.append(")");
      }
      return strBuilder.toString();
    }
    
    @Override
    public byte getType() {
      return TYPE;
    }
  }

  public static class Variable extends KBase<String> {
    private String name;
    private byte type;
    
    public Variable() {
      super(null);
    }
    public String getDataType() {
      return "Variable";
    }

    public String getContext() {
      return getValue();
    }

    public void setContext(String context) {
      super.setValue(context);
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }    

    public void setType(byte type) {
      this.type = type;
    }
    
    @Override
    public byte getType() {
      return type;
    }
    
    @Override
    public String toString(boolean showType) {
      return "";
    }
  }

  public static class KBoolean extends KBase<Boolean> {
    public final static byte TYPE = -1;
    
    public KBoolean(boolean value) {
      super(value, 'b');
    }
    
    protected KBoolean(byte value) {
      this(value == 1);
    }
    
    public static KBoolean valueOf(boolean value) {
      return new KBoolean(value);
    }
    
    public static KBoolean valueOf(Boolean value) {
      return new KBoolean(value != null ? value.booleanValue() : false);
    }
    
    public static KBoolean valueOf(byte value) {
      return new KBoolean(value == 1);
    }
    
    public static KBoolean valueOf(Number value) {
      return new KBoolean(value == null ? 0 : value.byteValue());
    }

    @Override
    public String getDataType() {
      return boolean.class.getSimpleName();
    }

    @Override
    public String toString(boolean showType) {
      return (getValue() ? "1" : "0") + (showType ? getId() : "");
    }

    @Override
    public boolean isNull() {
      return !getValue().booleanValue();
    }
    
    @Override
    public void serialise(OutputStream output) throws IOException {
      super.serialise(output);
      output.write((byte)(getValue() ? 1 : 0));
    }
    
    @Override
    public byte getType() {
      return TYPE;
    }
  }

  public static class KByte extends KBase<Byte> {
    public final static byte TYPE = -4;
    
    public KByte(byte value) {
      super(value, 'x');
    }
    
    public static KByte valueOf(byte value) {
      return new KByte(value);
    }
    
    public static KByte valueOf(Number value) {
      return new KByte(value == null ? Byte.MIN_VALUE : value.byteValue());
    }

    @Override
    public String getDataType() {
      return byte.class.getSimpleName();
    }
    
    @Override
    public boolean isNull() {
      return getValue().byteValue() == Byte.MIN_VALUE;
    }

    @Override
    public String toString(boolean showType) {
      return "0" + getId() + Integer.toHexString((getValue() >> 4) & 0xf) + Integer.toHexString(getValue() & 0xf);
    }
    
    @Override
    public void serialise(OutputStream output) throws IOException {
      super.serialise(output);
      output.write(getValue());
    }
    
    @Override
    public byte getType() {
      return TYPE;
    }
  }

  public static class KShort extends KBase<Short> {
    public final static byte TYPE = -5;

    public KShort(short value) {
      super(value, 'h');
    }
    
    public static KShort valueOf(short value) {
      return new KShort(value);
    }
    
    public static KShort valueOf(Number value) {
      return new KShort(value == null ? Short.MIN_VALUE : value.shortValue());
    }
    
    @Override
    public String getDataType() {
      return short.class.getSimpleName();
    }

    @Override
    public boolean isNull() {
      return getValue().shortValue() == Short.MIN_VALUE;
    }

    @Override
    public String toString(boolean showType) {
      return getValue().shortValue() == Short.MIN_VALUE ? "0N" + getId() :
          getValue().shortValue() == Short.MAX_VALUE ? "0W" + getId() :
            getValue().shortValue() == -Short.MAX_VALUE ? "-0W" + getId() :
              getValue().toString() + (showType ? getId() : "");
    }
    
    @Override
    public void serialise(OutputStream output) throws IOException {
      super.serialise(output);
      output.write(getValue());
    }
    
    @Override
    public byte getType() {
      return TYPE;
    }
  }

  public static class KInteger extends KBase<Integer> {
    public final static byte TYPE = -6;
    
    protected KInteger(int value, char id) {
      super(value, id);
    }
    
    public KInteger(int value) {
      this(value, false);
    }
    
    public KInteger(int value, boolean v3) {
      this(value, v3 ? 'i' : '\0');
    }
    
    public static KInteger valueOf(int value, boolean v3) {
      return new KInteger(value, v3);
    }
    
    public static KInteger valueOf(int value) {
      return KInteger.valueOf(value, false);
    }
    
    public static KInteger valueOf(Number value, boolean v3) {
      return KInteger.valueOf(value != null ? value.intValue() : Integer.MIN_VALUE, v3);
    }
    
    public static KInteger valueOf(Number value) {
      return KInteger.valueOf(value, false);
    }

    @Override
    public String getDataType() {
      return int.class.getSimpleName();
    }

    @Override
    public boolean isNull() {
      return getValue().intValue() == Integer.MIN_VALUE;
    }

    @Override
    public String toString(boolean showType) {
      return isNull() ? "0N" + getId() :
          getValue().intValue() == Integer.MAX_VALUE ? "0W"  + getId() :
            getValue().intValue() == -Integer.MAX_VALUE ?  "-0W" + getId() :
                getValue().toString() + (showType ? getId() : "");
    }
    
    @Override
    public void serialise(OutputStream output) throws IOException {
      super.serialise(output);
      output.write(getValue());
    }
    
    @Override
    public byte getType() {
      return TYPE;
    }
  }

  public static class KGuid extends KBase<UUID> {
    public final static byte TYPE = -2;
    
    public KGuid(UUID value) {
      super(value, 'g');
    }
    
    public static KGuid valueOf(UUID value) {
      return new KGuid(value);
    }
    
    public static KGuid valueOf(long mostSigBits, long leastSigBits) {
      return new KGuid(new UUID(mostSigBits, leastSigBits));
    }
    
    public static KGuid valueOf(Number mostSigBits, Number leastSigBits) {
      return new KGuid(mostSigBits == null || leastSigBits == null ? new UUID(0l, 0l) : 
        new UUID(mostSigBits.longValue(), leastSigBits.longValue()));
    }

    @Override
    public String getDataType() {
      return "guid";
    }

    @Override
    public boolean isNull() {
      return new UUID(0l, 0l).equals(getValue());
    }

    @Override
    public String toString(boolean showType) {
      return isNull() ? "0N" + getId() : getValue().toString();
    }
    
    @Override
    public void serialise(OutputStream output) throws IOException {
      super.serialise(output);
      write(output, getValue().getMostSignificantBits());
      write(output, getValue().getLeastSignificantBits());
    }
    
    @Override
    public byte getType() {
      return TYPE;
    }
  }  

  public static class KSymbol extends KBase<String> {
    public final static byte TYPE = -11;
    
    public KSymbol(String text) {
      super(text);
    }
    
    public static KSymbol valueOf(String text) {
      return new KSymbol(text == null ? "" : text);
    }
    
    public static KSymbol valueOf(char[] text) {
      return new KSymbol(text == null || text.length == 0 ? "" : String.valueOf(text));
    }
    
    public String getString() {
      return getValue();
    }

    @Override
    public String getDataType() {
      return "Symbol";
    }

    @Override
    public String toString(boolean showType) {
      return (showType ? "`" : "") + getValue();
    }

    @Override
    public boolean isNull() {
      return getValue().isEmpty();
    }

    @Override
    public void serialise(OutputStream output) throws IOException {
      super.serialise(output);
      for (int count = 0; count < getValue().length(); count++)
        output.write((byte)getValue().charAt(count));
      output.write((byte)0);
    }
    
    @Override
    public byte getType() {
      return TYPE;
    }
  }

  public static class KLong extends KBase<Long> {
    public final static byte TYPE = -7;
    
    protected KLong(long value, char id) {
      super(value, id);
    }
        
    public KLong(long value, boolean v3) {
      this(value, v3 ? '\0' : 'j');
    }
    
    public KLong(long value) {
      this(value, false);
    }
    
    public static KLong valueOf(long value) {
      return new KLong(value);
    }
    
    public static KLong valueOf(long value, boolean v3) {
      return new KLong(value, v3);
    }
    
    public static KLong valueOf(Number value, boolean v3) {
      return new KLong(value == null ? Long.MIN_VALUE : value.longValue(), v3);
    }
    
    public static KLong valueOf(Number value) {
      return KLong.valueOf(value, false);
    }

    @Override
    public String getDataType() {
      return long.class.getSimpleName();
    }

    @Override
    public boolean isNull() {
      return getValue().longValue() == Long.MIN_VALUE;
    }

    @Override
    public String toString(boolean showType) {
      return isNull() ?  "0N" + getId() :
        getValue().longValue() == Long.MAX_VALUE ?  "0W" + getId() :
          getValue().longValue() == -Long.MAX_VALUE ? "-0W" + getId() :
            getValue().toString() + (showType ? getId() : "");
    }

    @Override
    public void serialise(OutputStream output) throws IOException {
      super.serialise(output);
      write(output, getValue().longValue());
    }
    
    @Override
    public byte getType() {
      return TYPE;
    }
  }

  public static class KCharacter extends KBase<Character> {
    public final static byte TYPE = -10;

    public KCharacter(char c) {
      super(c, 'c');
    }
    
    public static KCharacter valueOf(char c) {
      return new KCharacter(c);
    }
    
    public static KCharacter valueOf(Character c) {
      return new KCharacter(c == null ? ' ' : c.charValue());
    }
    
    public static KCharacter valueOf(int c) {
      return new KCharacter((char)c);
    }
    
    public static KCharacter valueOf(Number c) {
      return KCharacter.valueOf(c == null ? 32 : c.intValue());
    }

    @Override
    public String getDataType() {
      return char.class.getSimpleName();
    }

    @Override
    public boolean isNull() {
      return getValue().charValue() == ' ';
    }

    @Override
    public String toString(boolean showType) {
      return showType ? "\"" + getValue().charValue() + "\"" : getValue().toString();
    }

    @Override
    public void serialise(OutputStream output) throws IOException {
      super.serialise(output);
      write(output, (byte)getValue().charValue());
    }
    
    @Override
    public byte getType() {
      return TYPE;
    }
  }

  public static class KReal extends KBase<Float> {
    public final static byte TYPE = -8;

    public KReal(float value) {
      super(value, 'e');
    }

    public static KReal valueOf(float value) {
      return new KReal(value);
    }
    
    public static KReal valueOf(Number value) {
      return KReal.valueOf(value == null ? Float.NaN : value.floatValue());
    }
    
    @Override
    public String getDataType() {
      return "real";
    }

    @Override
    public boolean isNull() {
      return Float.isNaN(getValue());
    }

    @Override
    public String toString(boolean showType) {
      return isNull() ? "0n" + getId() : getValue().floatValue() == Float.POSITIVE_INFINITY ? "0w" +
          getId() : getValue().floatValue() == Float.NEGATIVE_INFINITY ? "-0w" + getId()
          : getValue().toString() + (showType ? getId() : "");
    }

    @Override
    public void serialise(OutputStream output) throws IOException {
      super.serialise(output);
      write(output, Float.floatToIntBits(getValue()));
    }
    
    @Override
    public byte getType() {
      return TYPE;
    }
  }

  public static class KFloat extends KBase<Double> {
    public final static byte TYPE = -9;
    
    protected KFloat(double value, char id) {
      super(value, id);
    }

    public KFloat(double value) {
      super(value, '\0');
    }
    
    public static KFloat valueOf(double value) {
      return new KFloat(value);
    }
    
    public static KFloat valueOf(Number value) {
      return KFloat.valueOf(value == null ? Double.NaN : value.doubleValue());
    }
    
    @Override
    public String getDataType() {
      return "float";
    }

    @Override
    public boolean isNull() {
      return Double.isNaN(getValue());
    }

    @Override
    public String toString(boolean showType) {
      return isNull() ?  "0n" :
        getValue().doubleValue() == Double.POSITIVE_INFINITY ? "0w" :
          getValue() == Double.NEGATIVE_INFINITY ? "-0w" :
            getValue().toString() + (showType ? getId() : "");
    }

    @Override
    public void serialise(OutputStream output) throws IOException {
      super.serialise(output);
      write(output, Double.doubleToLongBits(getValue().doubleValue()));
    }
    
    @Override
    public byte getType() {
      return TYPE;
    }
  }

  public static class KDate extends KInteger implements Dateable {
    public final static byte TYPE = -14;

    public KDate(int value) {
      super(value, 'd');
    }
    
    public static KDate valueOf(int value) {
      return new KDate(value);
    }
    
    public static KDate valueOf(Number value) {
      return KDate.valueOf(value == null ? Integer.MIN_VALUE : value.intValue());
    }
    
    @Override
    public String getDataType() {
      return "date";
    }

    @Override
    public String toString(boolean showType) {
      return isNull() ? "0N" + getId():
        getValue().intValue() == Integer.MAX_VALUE ?  "0W" + getId() :
          getValue().intValue() == -Integer.MAX_VALUE ? "-0W" + getId() :
              sd("yyyy.MM.dd", new Date(86400000L * (getValue().intValue() + 10957)));
    }

    @Override
    public Date toDate() {
      return new Date(86400000L * (getValue().intValue() + 10957));
    }
    
    @Override
    public byte getType() {
      return TYPE;
    }
  }

  public static class KTime extends KInteger implements Dateable {
    public final static byte TYPE = -19;
    
    public KTime(int value) {
      super(value, 't');
    }
    
    public static KTime valueOf(int value) {
      return new KTime(value);
    }
    
    public static KTime valueOf(Number value) {
      return KTime.valueOf(value == null ? Integer.MIN_VALUE : value.intValue());
    }

    @Override
    public String getDataType() {
      return Time.class.getSimpleName();
    }

    @Override
    public String toString(boolean showType) {
      return isNull() ? "0N" + getId():
        getValue().intValue() == Integer.MAX_VALUE ?  "0W" + getId() :
          getValue().intValue() == -Integer.MAX_VALUE ? "-0W" + getId() :
            sd("HH:mm:ss.SSS", new Time(getValue()));
    }

    @Override
    public Time toDate() {
      return new Time(getValue());
    }
    
    @Override
    public byte getType() {
      return TYPE;
    }
  }

  public static class KDatetime extends KFloat implements Dateable {
    public final static byte TYPE = -15;
    
    public KDatetime(double value) {
      super(value, 'z');
    }
    
    public static KDatetime valueOf(double value) {
      return new KDatetime(value);
    }
    
    public static KDatetime valueOf(Number value) {
      return KDatetime.valueOf(value == null ? Double.NaN : value.doubleValue());
    }

    @Override
    public String getDataType() {
      return "datetime";
    }

    @Override
    public String toString(boolean showType) {
      return isNull() ? "0n" + getId() :
        getValue().doubleValue() == Double.POSITIVE_INFINITY ? "0w" + getId() :
          getValue().doubleValue() == Double.NEGATIVE_INFINITY ? "-0w" + getId() :
            sd(showType ? "yyyy.MM.dd'T'HH:mm:ss.SSS" : "yyyy.MM.dd HH:mm:ss.SSS", toDate());
    }

    @Override
    public Date toDate() {
      return new Timestamp(((long)(.5 + 8.64e7 * (getValue() + 10957))));
    }
    
    @Override
    public byte getType() {
      return TYPE;
    }
  }

  public static class KTimestamp extends KLong implements Dateable {
    public final static byte TYPE = -12;
    
    public KTimestamp(long value) {
      super(value, 'p');
    }
    
    public static KTimestamp valueOf(long value) {
      return new KTimestamp(value);
    }
    
    public static KTimestamp valueOf(Number value) {
      return KTimestamp.valueOf(value == null ? Long.MIN_VALUE : value.longValue());
    }

    @Override
    public String getDataType() {
      return "timestamp";
    }

    @Override
    public String toString(boolean showType) {
      return isNull() ?  "0N" + getId() :
        getValue().longValue() == Long.MAX_VALUE ? "0W" + getId() : 
          getValue().longValue() == Long.MAX_VALUE ?  "-0W" + getId() :
              sd(showType ? "yyyy.MM.dd'D'HH:mm:ss" : "yyyy.MM.dd HH:mm:ss", toDate()) + "." +
                new DecimalFormat("000000000").format(toDate().getNanos());
    }

    @Override
    public Timestamp toDate() {
      long k = 86400000L * 10957;
      long n = 1000000000L;
      long d = getValue().longValue() < 0 ? (getValue().longValue() + 1) / n - 1 : getValue().longValue() / n;
      long ltime = getValue().longValue() == Long.MIN_VALUE ? getValue().longValue() : (k + 1000 * d);
      int nanos = (int)(getValue().longValue() - n * d);
      Timestamp ts = new Timestamp(ltime);
      ts.setNanos(nanos);
      return ts;
    }
    
    @Override
    public byte getType() {
      return TYPE;
    }
  }
  
  public static class KMonth extends KInteger implements Dateable {
    public final static byte TYPE = -13;
    
    public KMonth(int value) {
      super(value, 'm');
    }
    
    public static KMonth valueOf(int value) {
      return new KMonth(value);
    }
    
    public static KMonth valueOf(Number value) {
      return KMonth.valueOf(value == null ? Integer.MIN_VALUE : value.intValue());
    }

    @Override
    public String getDataType() {
      return "month";
    }
    
    @Override
    public String toString(boolean showType) {
      return isNull() ?  "0N" + getId() :
        getValue().intValue() == Integer.MAX_VALUE ? "0W" + getId() :
          getValue().intValue() == -Integer.MAX_VALUE ? "-0W" + getId() :
            i2(((getValue() + 24000) / 12) / 100) + i2(((getValue() + 24000) / 12) % 100) + "." + i2(1 + getValue() + 24000 % 12) + 
            (showType ? getId() : "");
    }

    @Override
    public Date toDate() {
      int m = getValue() + 24000, y = m / 12;
      Calendar cal = Calendar.getInstance();
      cal.set(y, m, 01);
      return cal.getTime();
    }

    @Override
    public byte getType() {
      return TYPE;
    }
  }

  public static class KMinute extends KInteger implements Dateable{
    public final static byte TYPE = -17;
    
    public KMinute(int value) {
      super(value, 'u');
    }
    
    public static KMinute valueOf(int value) {
      return new KMinute(value);
    }
    
    public static KMinute valueOf(Number value) {
      return KMinute.valueOf(value == null ? Integer.MIN_VALUE : value.intValue());
    }
    
    @Override
    public String getDataType() {
      return "minute";
    }

    @Override
    public String toString(boolean showType) {
      return isNull() ? "0N" + getId() : getValue().intValue() == Integer.MAX_VALUE ? "0W" + getId() : 
        getValue().intValue() == -Integer.MAX_VALUE ? "-0W" + getId() : 
          i2(getValue() / 60) + ":" + i2(getValue() % 60);
    }
    
    @Override
    public Date toDate() {
      Calendar cal = Calendar.getInstance();
      cal.set(Calendar.HOUR, getValue() / 60);
      cal.set(Calendar.MINUTE, getValue() % 60);
      return cal.getTime();
    }
    
    @Override
    public byte getType() {
      return TYPE;
    }
  }

  public static class KSecond extends KInteger implements Dateable {
    public final static byte TYPE = -18;
    
    public KSecond(int value) {
      super(value, 'v');
    }
    
    public static KSecond valueOf(int value) {
      return new KSecond(value);
    }
    
    public static KSecond valueOf(Number value) {
      return KSecond.valueOf(value == null ? Integer.MIN_VALUE : value.intValue());
    }

    @Override
    public String getDataType() {
      return "second";
    }

    @Override
    public String toString(boolean showType) {
      return isNull() ? "0N" + getId() :
        getValue().intValue() == Integer.MAX_VALUE ? "0W" + getId() :
          getValue().intValue() == -Integer.MAX_VALUE ? "-0W" + getId() :
            new KMinute(getValue() / 60).toString() + ':' + i2(getValue() % 60);
    }

    @Override
    public Date toDate() {
      Calendar cal = Calendar.getInstance();
      cal.set(Calendar.HOUR, getValue() / (60 * 60));
      cal.set(Calendar.MINUTE, (int)((getValue() % (60 * 60)) / 60));
      cal.set(Calendar.SECOND, getValue() % 60);
      return cal.getTime();
    }
    
    @Override
    public byte getType() {
      return TYPE;
    }
  }

  public static class KTimespan extends KLong {
    public final static byte TYPE = -16;
    
    public KTimespan(long value) {
      super(value, 'n');
    }

    @Override
    public String getDataType() {
      return "timespan";
    }

    public String toString(boolean showType) {
      if (isNull())
        return "0N" + getId();
      else
        if (getValue().longValue() == Long.MAX_VALUE)
          return "0W" + getId();
        else
          if (getValue().longValue() == -Long.MAX_VALUE)
            return "-0W" + getId();
          else {
            String s = "";
            long jj = getValue();
            if (jj < 0) {
              jj = -jj;
              s = "-";
            }
            int d = ((int)(jj / 86400000000000L));
            if (d != 0)
              s += d + (showType ? "D" : " ");
            synchronized (DEC_FORMAT) {
              return s + DEC_FORMAT.format((int)((jj % 86400000000000L) / 3600000000000L)) + ":" +
                  DEC_FORMAT.format((int)((jj % 3600000000000L) / 60000000000L)) + ":" +
                  DEC_FORMAT.format((int)((jj % 60000000000L) / 1000000000L)) + "." +
                  new DecimalFormat("000000000").format((int)(jj % 1000000000L));              
            }
          }
    }
    
    @Override
    public byte getType() {
      return TYPE;
    }
  }

  public static class KDictionary extends KType<KType<?>> implements Arrayable {
    public final static byte TYPE = 99;
    
    private KType<?> value;

    public KDictionary(KType<?> key, KType<?> value) {
      super(key);
      this.value = value;
    }
    
    public KType<?> getKeys() {
      return super.getValue();
    }
    
    public KType<?> getValues() {
      return value;
    }

    @Override
    public String getDataType() {
      return "dictionary";
    }

    @Override
    public String toString(boolean showType) {
      boolean useBrackets = getKeys() instanceof KTable;
      StringBuilder strBuilder = new StringBuilder();
      if (useBrackets)
        strBuilder.append("(");
      strBuilder.append(getKeys().toString(showType));
      if (useBrackets)
        strBuilder.append(")");
      strBuilder.append("!");
      strBuilder.append(getValue().toString(showType));
      return strBuilder.toString();
    }
    
    public int getColumnCount() {
      return getKeys() instanceof KTable ? 
          ((KTable)getKeys()).getKeys().getLength() + ((KTable)getValues()).getValue().getLength() :
            getValues() instanceof KArray ? 2 : 0;   
    }
    
    public int getRowCount() {
      return getKeys() instanceof KTable ? ((KTable)getKeys()).getValues().getLength() > 0 ?
          ((KTable)getKeys()).getValues().get(0) instanceof KArray<?> ?
              ((KArray<?>)((KTable)getKeys()).getValues().get(0)).getLength() : 0 : 0 : 
            getValues() instanceof KArray ? ((KArray<?>)getValues()).getLength() : 0;
    }
    
    @Override
    public Object[] toArray() {
      int rows = getRowCount();
      int columns = getColumnCount();
      KType<?>[][] array = new KType<?>[rows][];
      for (int row = 0; row < rows; row++) {
        List<KType<?>> list = new ArrayList<KType<?>>();
        for (int col = 0; col < columns; col++) {
          list.add(getElement(row, col));
        }
        array[row] = list.toArray(new KType<?>[0]);
      }
      return array;
    }
    
    private KType<?> getElement(int row, int col) {
      if (getKeys() instanceof KTable) {
        KTable table = (KTable)getKeys();

        if (col >= table.getKeys().getLength()) {
          col -= table.getKeys().getLength();
          table = (KTable)getValues();
        }
        return table.getValues() instanceof KArray<?> ?
            ((KArray<?>)table.getValues()).get(col) instanceof KArray<?> ? 
                ((KArray<?>)((KArray<?>)table.getValues()).get(col)).get(row) : null : null;
      }
      return getKeys() instanceof KArray<?> && getValues() instanceof KArray<?> ?
          col == 0 ? ((KArray<?>)getKeys()).get(row) : ((KArray<?>)getValues()).get(row) : null;
    }
    
    @Override
    public byte getType() {
      return TYPE;
    }
    
    @Override
    public boolean isNull() {
      return getKeys() == null;
    }
  }

  public static class KTable extends KBase<KSymbolArray> implements Arrayable {
    public final static byte TYPE = 98;
    
    public KArray<?> value;

    public KTable(KDictionary dict) {
      super(dict.getKeys() instanceof KSymbolArray ? (KSymbolArray)dict.getKeys() : null);
      if (getKeys() != null && dict.getValues() instanceof KArray) {
        this.value = (KArray<?>)dict.getValues();
      }
    }

    @Override
    public String getDataType() {
      return "table";
    }
    
    public KSymbolArray getKeys() {
      return super.getValue();
    }
    
    public KArray<?> getValues() {
      return value;
    }
    
    @Override
    public String toString(boolean showType) {
      boolean usebracket = getValue().getLength() == 1;
      StringBuilder strBuilder = new StringBuilder(FLIP);
      if (usebracket)
        strBuilder.append("(");
      strBuilder.append(getValue().toString(showType));
      if (usebracket)
        strBuilder.append(")");
      strBuilder.append("!");
      strBuilder.append(getValues().toString(showType));
      return strBuilder.toString();
    }

    public void append(KTable table) {
      for (int count = 0; count < getValues().getLength(); count++)
        if (getValues().get(count) instanceof KBaseArray && table.getValues().get(count) instanceof KBaseArray) {
          ((KBaseArray)getValues().get(count)).append((KBaseArray)table.getValues().get(count));
        }
    }
//    
    public Object[] toArray() {
      int rows = getRowCount();
      KType<?>[][] array = new KType[rows][];
      for (int rowId = 0; rowId < rows; rowId++) {
        int columns = getColumnCount();
        KType<?>[] row = new KType[columns];
        for (int colId = 0; colId < columns; colId++) {
          row[colId] = ((KArray<?>)getValues().get(colId)).get(rowId);
        }
        array[rowId] = row;
      }
      return array;
    }
    
    @Override
    public byte getType() {
      return TYPE;
    }

    public int getRowCount() {
      return getValues() instanceof KArray && getValues().get(0) instanceof KArray ? 
          ((KArray<?>)getValues().get(0)).getLength() : 0;
    }
    
    public int getColumnCount() {
      return getKeys() instanceof KArray ? ((KArray<?>)getValues()).getLength() : 0;
    }
    
    public List<String> getColumnNames() {
      KType<?>[] names = getKeys().toArray();
      List<String> columns = new ArrayList<String>();
      if (names != null && names.length > 0) {
        for (KType<?> name : names) {
          columns.add(name.toString(false));
        }
      }
      return columns;
    }
    
    public int getColumnIndex(String columnName) {
      KType<?>[] names = getKeys().toArray();
      if (names == null || names.length == 0 || columnName == null) 
        return -1;
      for (int count = 0; count < names.length; count++) {
        if (columnName.equals(names[count].toString(false)))
          return count;
      }
      return -1;
    }
    
    @Override
    public boolean isNull() {
      return getKeys() == null;
    }
  }

  static String i2(int i) {
    return new java.text.DecimalFormat("00").format(i);
  }
  
  public static abstract class KArray<T> extends KType<T> implements Arrayable {
    private int length;
    private byte attr;
    private static String[] sAttr = new String[]{"", "`s#", "`u#", "`p#", "`g#"};

    protected KArray(T value, int length) {
      super(value);
      this.length = length;
    }
    
    public abstract KType<?> get(int index);

    protected int getLength() {
      return length;
    }
    
    protected void setLength(int length) {
      this.length = length;
    }
    
    public byte getAttribute() {
      return attr;
    }

    public void setAttribute(byte attr) {
      this.attr = attr;
    }

    public String toString(boolean showType) {
      if (attr <= sAttr.length)
        return sAttr[attr];
      return "";
    }

    protected void toString(StringBuilder strBuilder, boolean showType, String type) {
      if (getLength() == 0)
        strBuilder.append("`" + type + "$()");
      else {
        if (getLength() == 1)
          strBuilder.append(ENLIST);
        for (int count = 0; count < getLength(); count++) {
          if (count > 0)
            strBuilder.append(" ");
          strBuilder.append(get(count).toString(showType));
        }
      }      
    }
    
    @Override
    public KType<?>[] toArray() {
      if (getValue() != null && getValue().getClass().isArray() && getLength() > 0) {
        KType<?>[] array = new KType<?>[getLength()];
        for (int count = 0; count < getLength(); count++) {
          array[count] = get(count);
        }
        return array;
      }
      return new KType<?>[0];
    }
  }

  public static abstract class KBaseArray extends KArray<Object> implements Arrayable {
    protected KBaseArray(Class<?> cls, int length) {
      super(Array.newInstance(cls, length), length);
    }
    
    public Object getArray() {
      return getValue();
    }
    
    @Override
    public int getLength() {
      return super.getLength();
    }
    
    public int[] gradeUp() {
      return Sorter.gradeUp(getArray(), getLength());
    }

    public int[] gradeDown() {
      return Sorter.gradeDown(getArray(), getLength());
    }

    private int calcCapacity(int length) {
      return (int)(1.1 * length);
    }
    
    public void append(KBaseArray array) {
      if ((array.getLength() + getLength()) > Array.getLength(getArray())) {
        int newLength = Array.getLength(getArray()) + array.getLength();
        Object tmp = Array.newInstance(getArray().getClass().getComponentType(),
            2 * calcCapacity(newLength));
        System.arraycopy(getArray(), 0, tmp, 0, getLength());
        setValue(tmp);
      }
      System.arraycopy(array.getArray(), 0, getArray(), getLength(), array.getLength());
      setLength(getLength() + array.getLength());
    }
    
    @Override
    public boolean isNull() {
      return getValue() == null;
    }
  }

  public static class KShortArray extends KBaseArray {
    public final static byte TYPE = -KShort.TYPE;
    
    public KShortArray(int length) {
      super(short.class, length);
    }

    @Override
    public String getDataType() {
      return Short.class.getSimpleName() + " array";
    }

    @Override    
    public KShort get(int index) {
      try {
        return KShort.valueOf(Array.getShort(getArray(), index));
      }
      catch(Throwable cause) {
        return KShort.valueOf(null);
      }
    }

    @Override
    public String toString(boolean showType) {
      StringBuilder strBuilder = new StringBuilder(super.toString(showType));
      toString(strBuilder, showType, short.class.getSimpleName());      
      return strBuilder.toString();
    }
    
    @Override
    public void serialise(OutputStream output) throws IOException {
      super.serialise(output);
      write(output, (byte)0);
      write(output, getLength());
      for (int count = 0; count < getLength(); count++)
        try {
          write(output, (byte)Array.getShort(getArray(), count));
        }
        catch (Throwable cause) {
          throw new IOException(cause);
        }
    }
    
    @Override
    public byte getType() {
      return TYPE;
    }
  }

  public static class KIntegerArray extends KBaseArray {
    public final static byte TYPE = -KInteger.TYPE;
    private boolean v3;
    
    public KIntegerArray(int length, boolean v3) {
      super(int.class, length);
      this.v3 = v3;
    }
    
    public KIntegerArray(int length) {
      this(length, false);
    }

    @Override
    public String getDataType() {
      return int.class.getSimpleName() + " array";
    }

    @Override
    public KInteger get(int index) {
      try {
        return KInteger.valueOf(Array.getInt(getArray(), index), v3);
      }
      catch(Throwable cause) {
        return KInteger.valueOf(null);
      }
    }

    @Override
    public String toString(boolean showType)  {
      StringBuilder strBuilder = new StringBuilder(super.toString(showType));
      toString(strBuilder, showType, int.class.getSimpleName());      
      return strBuilder.toString();
    }
    
    @Override
    public void serialise(OutputStream output) throws IOException {
      super.serialise(output);
      write(output, (byte)0);
      write(output, getLength());
      for (int count = 0; count < getLength(); count++)
        try {
          write(output, (byte)Array.getInt(getArray(), count));
        }
        catch (Throwable cause) {
          throw new IOException(cause);
        }
    }
   
    @Override
    public byte getType() {
      return TYPE;
    }
  }

  public static class KList extends KBaseArray {
    public final static byte TYPE = 0;
    
    public KList(int length) {
      super(KType.class, length);
    }
    
    @Override
    public String getDataType() {
      return List.class.getSimpleName().toLowerCase();
    }

    @Override
    public KType<?> get(int index) {
      Object obj = null;
      try {
        obj = Array.get(getArray(), index);
      }
      catch(Throwable ignored) {        
      }
      return obj instanceof KType ? (KType<?>)obj : null;
    }
    
    @Override
    protected void toString(StringBuilder strBuilder, boolean showType, String type) {
      if (getLength() == 1)
        strBuilder.append(ENLIST);
      else
        strBuilder.append("(");
      for (int count = 0; count < getLength(); count++) {
        if (count > 0)
          strBuilder.append(";");
        KType<?> value = get(count);
        if (value != null) {
          strBuilder.append(value.toString(showType));
        }
      }
      if (getLength() != 1)
        strBuilder.append(")");      
    }

    @Override
    public String toString(boolean showType) {
      StringBuilder strBuilder = new StringBuilder(super.toString(showType));
      toString(strBuilder, showType, null);      
      return strBuilder.toString();
    }
    
    @Override
    public void serialise(OutputStream output) throws IOException {
      super.serialise(output);
      write(output, (byte)0);
      write(output, getLength());
      for (int count = 0; count < getLength(); count++)
        try {
          get(count).serialise(output);
        }
        catch (Throwable cause) {
          throw new IOException(cause);
        }
    }
    
    @Override
    public byte getType() {
      return TYPE;
    }
    
    @Override
    public boolean isNull() {
      return getValue() == null;
    }
  }

  public static class KFloatArray extends KBaseArray {
    public final static byte TYPE = -KFloat.TYPE;

    public KFloatArray(int length) {
      super(double.class, length);
    }
    
    @Override
    public String getDataType() {
      return float.class.getSimpleName() + " array";
    }

    @Override
    public KFloat get(int index) {
      try {
        return KFloat.valueOf(Array.getDouble(getArray(), index));
      }
      catch(Throwable ignored) {
        return KFloat.valueOf(null);
      }
    }

    @Override
    public String toString(boolean showType) {
      StringBuilder strBuilder = new StringBuilder(super.toString(showType));
      toString(strBuilder, showType, float.class.getSimpleName());      
      return strBuilder.toString();
    }
    
    @Override
    public void serialise(OutputStream output) throws IOException {
      super.serialise(output);
      write(output, (byte)0);
      write(output, getLength());
      for (int count = 0; count < getLength(); count++)
        try {
          write(output, Double.doubleToLongBits(Array.getDouble(getArray(), count)));
        }
        catch (Throwable cause) {
          throw new IOException(cause);
        }
    }
    
    @Override
    public byte getType() {
      return TYPE;
    }
  }

  public static class KRealArray extends KBaseArray {
    public final static byte TYPE = -KReal.TYPE;

    public KRealArray(int length) {
      super(float.class, length);
    }
    
    @Override
    public String getDataType() {
      return "real array";
    }

    @Override
    public KReal get(int index) {
      try {
        return KReal.valueOf(Array.getFloat(getArray(), index));
      }
      catch(Throwable ignored) {
        return KReal.valueOf(null);
      }
    }
    
    @Override
    public void serialise(OutputStream output) throws IOException {
      super.serialise(output);
      write(output, (byte)0);
      write(output, getLength());
      for (int count = 0; count < getLength(); count++)
        try {
          write(output, Float.floatToIntBits(Array.getFloat(getArray(), count)));
        }
        catch (Throwable cause) {
          throw new IOException(cause);
        }
    }

    @Override
    public String toString(boolean showType) {
      StringBuilder strBuilder = new StringBuilder(super.toString(showType));
      toString(strBuilder, showType, "real");      
      return strBuilder.toString();
    }
   
    @Override
    public byte getType() {
      return TYPE;
    }
  }

  public static class KLongArray extends KBaseArray {
    public final static byte TYPE = -KLong.TYPE;
    private boolean v3;
    
    public KLongArray(int length, boolean v3) {
      super(long.class, length);
      this.v3 = v3;
    }
    
    public KLongArray(int length) {
      this(length, false);
    }

    @Override
    public String getDataType() {
      return long.class.getSimpleName() + " array";
    }

    @Override
    public KLong get(int index) {
      try {
        return KLong.valueOf(Array.getLong(getArray(), index), v3);
      }
      catch(Throwable ignored) {
        return KLong.valueOf(null);
      }
    }
    
    @Override
    public void serialise(OutputStream output) throws IOException {
      super.serialise(output);
      write(output, (byte)0);
      write(output, getLength());
      for (int count = 0; count < getLength(); count++)
        try {
          write(output, Array.getLong(getArray(), count));
        }
        catch (Throwable cause) {
          throw new IOException(cause);
        }
    }
    
    @Override
    public String toString(boolean showType) {
      StringBuilder strBuilder = new StringBuilder(super.toString(showType));
      toString(strBuilder, showType, long.class.getSimpleName());
      return strBuilder.toString();
    }

    @Override
    public byte getType() {
      return TYPE;
    }
  }

  public static class KMonthArray extends KIntegerArray {
    public final static byte TYPE = -KMonth.TYPE;
    
    public KMonthArray(int length) {
      super(length);
    }
    
    @Override
    public String getDataType() {
      return "month array";
    }

    @Override
    public KMonth get(int index) {
      try {
        return KMonth.valueOf(Array.getInt(getArray(), index));
      }
      catch (Throwable ignored) {
        return KMonth.valueOf(null);
      }
    }    

    @Override
    public String toString(boolean showType) {
      StringBuilder strBuilder = new StringBuilder(super.toString(showType));
      toString(strBuilder, showType, "month");
      return strBuilder.toString();
    }
   
    @Override
    public byte getType() {
      return TYPE;
    }
  }

  public static class KDateArray extends KIntegerArray {
    public final static byte TYPE = -KDate.TYPE;

    public KDateArray(int length) {
      super(length);
    }
    
    @Override
    public String getDataType() {
      return "date array";
    }

    @Override
    public KDate get(int index) {
      try {
        return KDate.valueOf(Array.getInt(getArray(), index));
      }
      catch (Throwable ignored) {
        return KDate.valueOf(null);
      }
    }
    
    @Override
    public String toString(boolean showType) {
      StringBuilder strBuilder = new StringBuilder(super.toString(showType));
      toString(strBuilder, showType, "date");
      return strBuilder.toString();
    }
  
    @Override
    public byte getType() {
      return TYPE;
    }
  }

  public static class KMinuteArray extends KIntegerArray {
    public final static byte TYPE = -KMinute.TYPE;

    public KMinuteArray(int length) {
      super(length);
    }
    
    @Override
    public String getDataType() {
      return "minute array";
    }

    @Override
    public KMinute get(int index) {
      try {
        return KMinute.valueOf(Array.getInt(getArray(), index));
      }
      catch (Throwable ignored) {
        return KMinute.valueOf(null);
      }
    }

    @Override
    public String toString(boolean showType) {
      StringBuilder strBuilder = new StringBuilder(super.toString(showType));
      toString(strBuilder, showType, "minute");
      return strBuilder.toString();
    }    
  
    @Override
    public byte getType() {
      return TYPE;
    }
  }

  public static class KDatetimeArray extends KFloatArray {
    public final static byte TYPE = -KDatetime.TYPE;
    
    public KDatetimeArray(int length) {
      super(length);
    }
    
    public String getDataType() {
      return "datetime array";
    }

    @Override
    public KDatetime get(int index) {
      try {
        return KDatetime.valueOf(Array.getDouble(getArray(), index));
      }
      catch (Throwable ignored) {
        return KDatetime.valueOf(null);
      }
    }
   
    @Override
    public String toString(boolean showType) {
      StringBuilder strBuilder = new StringBuilder(super.toString(showType));
      toString(strBuilder, showType, "datetime");
      return strBuilder.toString();
    }
      
    @Override
    public byte getType() {
      return TYPE;
    }
  }

  public static class KTimestampArray extends KLongArray {
    public final static byte TYPE = -KTimestamp.TYPE;
    
    public KTimestampArray(int length) {
      super(length);
    }

    @Override
    public String getDataType() {
      return "timestamp array";
    }

    @Override
    public KTimestamp get(int index) {
      try {
      return KTimestamp.valueOf(Array.getLong(getArray(), index));
      }
      catch(Throwable ignored) {
        return KTimestamp.valueOf(null);
      }
    }

    @Override
    public String toString(boolean showType) {
      StringBuilder strBuilder = new StringBuilder(super.toString(showType));
      toString(strBuilder, showType, "timestamp");
      return strBuilder.toString();
    }    

    @Override
    public byte getType() {
      return TYPE;
    }
  }

  public static class KTimespanArray extends KLongArray {
    public final static byte TYPE = -KTimespan.TYPE;
    
    public KTimespanArray(int length) {
      super(length);
    }
    
    @Override
    public String getDataType() {
      return "timespan array";
    }

    @Override
    public KTimespan get(int i) {
      return new KTimespan(Array.getLong(getArray(), i));
    }

    @Override
    public String toString(boolean showType) {
      StringBuilder strBuilder = new StringBuilder(super.toString(showType));
      toString(strBuilder, showType, "timespan");
      return strBuilder.toString();
    }
    
    @Override
    public void serialise(OutputStream output) throws IOException {
      super.serialise(output);
      write(output, (byte)0);
      write(output, getLength());
      for (int count = 0; count < getLength(); count++)
        try {
          write(output, Array.getLong(getArray(), count));
        }
        catch (Throwable cause) {
          throw new IOException(cause);
        }
    }

    @Override
    public byte getType() {
      return TYPE;
    }
  }

  public static class KSecondArray extends KIntegerArray {
    public final static byte TYPE = -KSecond.TYPE;

    public KSecondArray(int length) {
      super(length);
    }

    @Override
    public String getDataType() {
      return "second array";
    }
    
    @Override
    public KSecond get(int index) {
      try {
        return KSecond.valueOf(Array.getInt(getArray(), index));
      }
      catch (Throwable ignored) {
        return KSecond.valueOf(null);
      }
    }    

    @Override
    public String toString(boolean showType) {
      StringBuilder strBuilder = new StringBuilder(super.toString(showType));
      toString(strBuilder, showType, "second");
      return strBuilder.toString();
    }

    @Override
    public byte getType() {
      return TYPE;
    }
  }

  public static class KTimeArray extends KIntegerArray {
    public final static byte TYPE = -KTime.TYPE;

    public KTimeArray(int length) {
      super(length);
    }

    @Override
    public String getDataType() {
      return "time array";
    }

    @Override
    public KTime get(int index) {
      try {
        return KTime.valueOf(Array.getInt(getArray(), index));
      }
      catch (Throwable ignored) {
        return KTime.valueOf(null);
      }
    }

    @Override
    public void serialise(OutputStream output) throws IOException {
      super.serialise(output);
      write(output, (byte)0);
      write(output, getLength());
      for (int count = 0; count < getLength(); count++)
        try {
          write(output, Array.getInt(getArray(), count));
        }
        catch (Throwable cause) {
          throw new IOException(cause);
        }
    }

    @Override
    public String toString(boolean showType) {
      StringBuilder strBuilder = new StringBuilder(super.toString(showType));
      toString(strBuilder, showType, "time");
      return strBuilder.toString();
    }

    @Override
    public byte getType() {
      return TYPE;
    }
  }

  public static class KBooleanArray extends KBaseArray {
    public final static byte TYPE = -KBoolean.TYPE;

    public KBooleanArray(int length) {
      super(boolean.class, length);
    }
    
    @Override
    public String getDataType() {
      return "Boolean array";
    }

    @Override
    public KBoolean get(int index) {
      try {
        return KBoolean.valueOf(Array.getBoolean(getArray(), index));
      }
      catch(Throwable ignored) {
        return KBoolean.valueOf((Boolean)null);
      }
    }

    @Override
    public void serialise(OutputStream output) throws IOException {
      super.serialise(output);
      write(output, (byte)0);
      write(output, getLength());
      for (int count = 0; count < getLength(); count++)
        try {
          write(output, (byte)(Array.getBoolean(getArray(), count) ? 1 : 0));
        }
        catch (Throwable cause) {
          throw new IOException(cause);
        }
    }
    
    @Override
    public String toString(boolean showType) {
      StringBuilder strBuilder = new StringBuilder(super.toString(showType));
      toString(strBuilder, showType, boolean.class.getSimpleName());
      return strBuilder.toString();
    }

    @Override
    public byte getType() {
      return TYPE;
    }
  }

  public static class KByteArray extends KBaseArray {
    public final static byte TYPE = -KByte.TYPE;

    public KByteArray(int length) {
      super(byte.class, length);
    }
    
    @Override
    public String getDataType() {
      return byte.class.getSimpleName() + " array";
    }

    @Override
    public KByte get(int index) {
      try {
        return KByte.valueOf(Array.getByte(getArray(), index));
      }
      catch(Throwable ignored) {
        return KByte.valueOf(null);
      }
    }

    @Override
    public void serialise(OutputStream output) throws IOException {
      super.serialise(output);
      write(output, (byte)0);
      write(output, getLength());
      for (int count = 0; count < getLength(); count++)
        try {
          write(output, Array.getByte(getArray(), count));
        }
        catch (Throwable cause) {
          throw new IOException(cause);
        }
    }

    @Override
    public String toString(boolean showType) {
      StringBuilder strBuilder = new StringBuilder(super.toString(showType));
      toString(strBuilder, showType, byte.class.getSimpleName());
      return strBuilder.toString();
    }

    @Override
    public byte getType() {
      return TYPE;
    }
  }
  
  public static class KGuidArray extends KBaseArray {
    public final static byte TYPE = -KGuid.TYPE;
    

    public KGuidArray(int length) {
      super(UUID.class, length);
    }

    @Override
    public String getDataType() {
      return "guid array";
    }

    @Override
    public KGuid get(int index) {
      return new KGuid((UUID)Array.get(getArray(), index) instanceof UUID ? (UUID)Array.get(getArray(), index) : new UUID(0l, 0l));
    }
    
    protected void toString(StringBuilder strBuilder, boolean showType, String type) {
      if (getLength() == 0)
        strBuilder.append("`" + type + "$()");
      else {
        if (getLength() == 1)
          strBuilder.append(ENLIST);
        for (int count = 0; count < getLength(); count++) {
          if (count > 0)
            strBuilder.append(" ");
          strBuilder.append(get(count).toString(showType));
        }
      }      
    }
    
    @Override
    public String toString(boolean showType) {
      StringBuilder strBuilder = new StringBuilder(super.toString(showType));
      toString(strBuilder, showType, "guid");
      return strBuilder.toString();
    }

    @Override
    public KGuid[] toArray() {
      if (getArray() != null && getArray().getClass().isArray() && getLength() > 0) {
        KGuid[] array = new KGuid[getLength()];
        for (int count = 0; count < getLength(); count++) {
          array[count] = get(count);
        }
        return array;
      }
      return new KGuid[0];
    }
    
    @Override
    public byte getType() {
      return TYPE;
    }
  }

  public static class KSymbolArray extends KBaseArray {
    public final static byte TYPE = -KSymbol.TYPE;
   
    public KSymbolArray(int length) {
      super(String.class, length);
    }

    @Override
    public String getDataType() {
      return "symbol array";
    }
    
    @Override
    public KSymbol get(int index) {
      try {
        return KSymbol.valueOf((String)Array.get(getArray(), index));
      }
      catch (Throwable cause) {
        return KSymbol.valueOf((String)null);
      }
    }

    @Override
    protected void toString(StringBuilder strBuilder, boolean showType, String type) {
      if (getLength() == 0)
        strBuilder.append("0#`");
      else {
        if (getLength() == 1)
          strBuilder.append(ENLIST);
        for (int count = 0; count < getLength(); count++) {
          if (count > 0)
            strBuilder.append(" ");
          strBuilder.append(get(count));
        }
      }      
    }
    
    @Override
    public void serialise(OutputStream output) throws IOException {
      super.serialise(output);
      write(output, (byte)0);
      write(output, getLength());
      for (int count = 0; count < getLength(); count++)
        get(count).serialise(output);
    }
    
    @Override
    public String toString(boolean showType) {
      StringBuilder strBuilder = new StringBuilder(super.toString(showType));
      toString(strBuilder, showType, byte.class.getSimpleName());
      return strBuilder.toString();
    }
    
    @Override
    public byte getType() {
      return TYPE;
    }    
  }

  public static class KCharacterArray extends KBaseArray {
    public final static byte TYPE = -KCharacter.TYPE;
    
    public KCharacterArray(int length) {
      super(char.class, length);
    }

    @Override
    public String getDataType() {
      return char.class.getSimpleName() + " array";
    }

    public KCharacterArray(char[] array) {
      this(array.length);      
      System.arraycopy(array, 0, getArray(), 0, array.length);
    }

    public KCharacterArray(String text) {
      this(text.toCharArray().length);
      System.arraycopy(text.toCharArray(), 0, getArray(), 0, text.toCharArray().length);
    }

    @Override
    public KCharacter get(int index) {
      try {
        return KCharacter.valueOf(Array.getChar(getArray(), index));
      }
      catch(Throwable cause) {
        return KCharacter.valueOf((Character)null);
      }
    }

    @Override
    public void serialise(OutputStream output) throws IOException {
      super.serialise(output);
      write(output, (byte)0);
      write(output, getLength());
      for (int count = 0; count < getLength(); count++)
        try {
          write(output, (byte)Array.getChar(getArray(), count));
        }
        catch (Throwable cause) {
          throw new IOException(cause);
        }
    }

    @Override
    protected void toString(StringBuilder strBuilder, boolean showType, String type) {
      if (getLength() == 1)
        strBuilder.append(ENLIST);

      if (showType)
        strBuilder.append("\"");
      for (int count = 0; count < getLength(); count++)
        strBuilder.append(get(count).toString(false));
      if (showType)
        strBuilder.append("\"");
    }

    @Override
    public String toString(boolean showType) {
      StringBuilder strBuilder = new StringBuilder(super.toString(showType));
      toString(strBuilder, showType, byte.class.getSimpleName());
      return strBuilder.toString();
    }

    
    @Override
    public byte getType() {
      return TYPE;
    }
  }

  static {
    DATE_FORMAT.setTimeZone(java.util.TimeZone.getTimeZone("GMT"));
  }
}