package tszielin.qlab.util.listener;

import tszielin.qlab.util.event.DataEvent;

/**
 * Event listener on <code>FireDataEvent</code>
 */
public interface DataListener extends java.util.EventListener {
  /**
   * Invoked when an event occurs
   * @param event DataFireEvent event
   */
  public void onData(DataEvent<?> event);
}
