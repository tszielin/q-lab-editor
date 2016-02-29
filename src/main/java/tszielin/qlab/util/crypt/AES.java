package tszielin.qlab.util.crypt;

import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import tszielin.qlab.util.error.AESException;

public class AES {
  private Cipher dcipher;
  private Cipher ecipher;

  public AES(String passPhrase) throws AESException {
    try {
      byte[] pass = new byte[16];
      Arrays.fill(pass, (byte)0xff);
      System.arraycopy(passPhrase.getBytes(), 0, pass, 0, passPhrase.getBytes().length);
      SecretKeySpec keySpec = new SecretKeySpec(pass, "AES");
      ecipher = Cipher.getInstance("AES");
      dcipher = Cipher.getInstance("AES");
      ecipher.init(Cipher.ENCRYPT_MODE, keySpec);
      dcipher.init(Cipher.DECRYPT_MODE, keySpec);
    }
    catch (Exception e) {
      throw new AESException(e);
    }
  }

  public String encrypt(String data) throws AESException {
    if (data == null || data.length() == 0) {
      return null;
    }

    try {
      byte bytes[] = data.getBytes();
      bytes = ecipher.doFinal(bytes);
      StringBuffer sb = new StringBuffer();
      for (int i = 0; i < bytes.length; i++) {
        String hex = Integer.toHexString(bytes[i] & 0xff);
        sb.append(hex.length() == 1 ? "0" + hex : hex);
      }
      return sb.toString();
    }
    catch (Exception e) {
      throw new AESException(e);
    }
  }

  /**
   * Decrypting data
   * 
   * @param str
   *          String data for decrypting
   * @return String decrypted information
   * @throws AESException
   *           any <code>BadPaddingException</code>, <code>IllegalBlockSizeException</code>,
   *           <code>UnsupportedEncodingException</code>, <code>IOException</code>
   */
  public String decrypt(String data) throws AESException {
    if (data == null || data.length() == 0 || data.length() % 2 != 0) {
      return null;
    }
    byte[] bytes = new byte[data.length() / 2];
    int counter = 0;
    for (int count = 0; count < data.length() / 2; count++) {
      counter = count * 2;
      Integer value = Integer.valueOf(data.substring(counter, counter + 2), 16);
      bytes[count] = value.byteValue();
    }
    try {
      bytes = dcipher.doFinal(bytes);
      return new String(bytes);
    }
    catch (Exception e) {
      throw new AESException(e);
    }
  }
}