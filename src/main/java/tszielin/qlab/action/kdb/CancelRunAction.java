package tszielin.qlab.action.kdb;

import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.swing.SwingWorker;

import com.kx.KdbConnection;
import com.kx.error.KException;

import tszielin.qlab.action.editor.EditorAction;
import tszielin.qlab.component.pane.EditorsTabbedPane;
import tszielin.qlab.event.WorkerChanged;
import tszielin.qlab.util.event.DataEvent;
import tszielin.qlab.util.image.IconsItem;
import tszielin.qlab.util.listener.DataListener;

public class CancelRunAction extends EditorAction implements DataListener {  
  private static final long serialVersionUID = -3596751170104618285L;
  private SwingWorker<Object, Object> worker;
  private KdbConnection server;

  public CancelRunAction(EditorsTabbedPane tabEditors) {
    super(tabEditors, "Cancel...", 'C', IconsItem.ICON_RUN_CANCEL, null, "Cancel", "Stop a running query.");
    setEnabled(false);
  }

  public void actionPerformed(ActionEvent event) {
    if (worker != null) {
      worker.cancel(true);
      if (server != null) {
//        server.k(new K.KByte((byte)3));
        server.close();
      }
      System.gc();
      worker = null;
      setEnabled(false);
      try {
        server.reconnect(true);
      }
      catch (IOException ignored) {
      }
      catch (KException ignored) {
      }
    }
  }

  @Override
  public void onData(DataEvent<?> event) {
    if (event instanceof WorkerChanged) {
      this.worker = ((WorkerChanged)event).getData();
      this.server = ((WorkerChanged)event).getServer();
      setEnabled(true);
    }
  }

  @Override
  public void setEnabled(boolean enable) {
    super.setEnabled(enable && this.worker != null);
  }
}
