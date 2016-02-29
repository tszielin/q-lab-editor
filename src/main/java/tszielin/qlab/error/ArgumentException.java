package tszielin.qlab.error;

public class ArgumentException extends StudioException {
  private static final long serialVersionUID = 4385231519356692808L;

  public ArgumentException() {
    super("Argument cannot be null.");
  }

  public ArgumentException(String message) {
    super(message);
  }

  public ArgumentException(Throwable cause) {
    super(cause);
  }

  public ArgumentException(String message, Throwable cause) {
    super(message, cause);
  }
}
