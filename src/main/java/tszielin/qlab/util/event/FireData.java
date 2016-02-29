package tszielin.qlab.util.event;

import java.io.Serializable;

import javax.swing.event.EventListenerList;

import tszielin.qlab.util.listener.DataListener;

public class FireData implements Serializable {
  private static final long serialVersionUID = -4431510307289021344L;
  protected EventListenerList listeners = new EventListenerList();

  public FireData() {
  }
  
  public FireData(DataListener listener) {
    addDataListener(listener);
  }

  public void addDataListener(DataListener listener) {
    listeners.add(DataListener.class, listener);
  }

  public void removeDataListener(DataListener listener) {
    listeners.remove(DataListener.class, listener);
  }

  public DataListener[] getDataListeners() {
    return (DataListener[])listeners.getListeners(DataListener.class);
  }

  public void onData(DataEvent<?> event) {
    java.util.EventListener[] listeners = getDataListeners();
    for (java.util.EventListener listener : listeners) {
      if (listener instanceof DataListener) {
        ((DataListener)listener).onData(event);
      }
    }
  }  
}
