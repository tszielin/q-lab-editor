package tszielin.qlab.error;

public class FileException extends StudioException {
  private static final long serialVersionUID = 3895233111375342654L;

  public FileException() {
    super("Unexepected I/O operation.");
  }

  public FileException(String message) {
    super(message);
  }

  public FileException(Throwable cause) {
    super(cause);
  }

  public FileException(String message, Throwable cause) {
    super(message, cause);
  }
}
