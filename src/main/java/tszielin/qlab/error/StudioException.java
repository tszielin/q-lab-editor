package tszielin.qlab.error;

public class StudioException extends Exception {
  private static final long serialVersionUID = -3583027871638937268L;

  public StudioException() {
    super("Unexpected exception");
  }

  public StudioException(String message) {
    super(message);
  }

  public StudioException(Throwable cause) {
    super(cause);
  }

  public StudioException(String message, Throwable cause) {
    super(message, cause);
  }
}
