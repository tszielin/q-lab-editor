package tszielin.qlab.event;

import tszielin.qlab.config.data.KdbService;
import tszielin.qlab.util.event.DataEvent;

public class KdbServiceChanged extends DataEvent<KdbService> {
  private static final long serialVersionUID = -5373094286490673368L;

  public KdbServiceChanged(Object source, KdbService connection) {
    super(source, connection);
  }   
}
