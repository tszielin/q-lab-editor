package tszielin.qlab.util.error;

import tszielin.qlab.error.StudioException;

public class ConfigException extends StudioException {
  private static final long serialVersionUID = 1342053399128358263L;

  public ConfigException() {
    super("Exception during reading configuration file");
  }

  public ConfigException(String message) {
    super(message);
  }
  
  public ConfigException(Throwable cause) {
    super(cause);
  }
  
  public ConfigException(String message, Throwable cause) {
    super(message, cause);
  }
}
