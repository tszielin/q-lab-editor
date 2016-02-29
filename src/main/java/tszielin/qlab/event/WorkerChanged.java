package tszielin.qlab.event;

import javax.swing.SwingWorker;

import com.kx.KdbConnection;

import tszielin.qlab.util.event.DataEvent;

public class WorkerChanged extends DataEvent<SwingWorker<Object, Object>> {
  private static final long serialVersionUID = 897509599379706322L;
  private KdbConnection server;
  
  public WorkerChanged(Object source) {
    this(source, null, null);
  }
  
  public WorkerChanged(Object source, SwingWorker<Object, Object> worker, KdbConnection server) {
    super(source, worker);
    this.server = server;
  }
  
  public KdbConnection getServer() {
    return server;
  }
}
