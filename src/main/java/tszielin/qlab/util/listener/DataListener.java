package tszielin.qlab.util.listener;

import tszielin.qlab.util.event.DataEvent;

/**
 * Event listener on <code>FireDataEvent</code>
 * @author <b><code>Thomas
 *   Zielinski</code></b><small>(thomas.zielinski@nagler-company.com)</small><br><i><code>Nagler
 *   & Company GmbH<code></i><br><small>2006</small>
 * @version 1.0
 */
public interface DataListener extends java.util.EventListener {
  /**
   * Invoked when an event occurs
   * @param event DataFireEvent event
   */
  public void onData(DataEvent<?> event);
}
