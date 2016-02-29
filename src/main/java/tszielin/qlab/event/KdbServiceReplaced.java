package tszielin.qlab.event;

import tszielin.qlab.config.data.KdbService;
import tszielin.qlab.util.event.DataEvent;

public class KdbServiceReplaced extends DataEvent<KdbService> {
  private static final long serialVersionUID = 4191952884471711194L;
  private KdbService previous;

  public KdbServiceReplaced(Object source, KdbService connection, KdbService previous ) {
    super(source, connection);
    this.previous = previous; 
  }
  
  public KdbService getPrevious() {
    return previous;
  }
}
