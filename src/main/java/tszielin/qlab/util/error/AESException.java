package tszielin.qlab.util.error;

import tszielin.qlab.error.StudioException;

public class AESException extends StudioException {
  private static final long serialVersionUID = -4250768715197814826L;

  public AESException() {
    super("Unsupported exception in AES algorithm");
  }

  public AESException(String message) {
    super(message);
  }

  public AESException(Throwable cause) {
    super(cause);
  }

  public AESException(String message, Throwable cause) {
    super(message, cause);
  }
}
