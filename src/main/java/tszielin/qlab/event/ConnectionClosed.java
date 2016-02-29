package tszielin.qlab.event;

import tszielin.qlab.config.data.KdbService;
import tszielin.qlab.error.ArgumentException;
import tszielin.qlab.util.event.DataEvent;

public class ConnectionClosed extends DataEvent<KdbService> {
  private static final long serialVersionUID = -820348381180121609L;

  public ConnectionClosed(Object source, String host, int port) throws ArgumentException {
    this(source, new KdbService(host, port));
  }
  
  public ConnectionClosed(Object source, KdbService connection) {
    super(source, connection);
  }  
}
