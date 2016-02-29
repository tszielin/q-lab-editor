package tszielin.qlab.action.editor;

import java.awt.event.ActionEvent;

import javax.swing.Icon;
import javax.swing.KeyStroke;

import com.kx.KdbConnection;

import tszielin.qlab.component.editor.QEditor;
import tszielin.qlab.component.pane.EditorsTabbedPane;
import tszielin.qlab.config.data.ConnectionStatus;
import tszielin.qlab.config.data.KdbService;
import tszielin.qlab.util.action.ActionBase;
import tszielin.qlab.util.image.IconsItem;
import tszielin.qlab.util.listener.DataListener;

public class ReleaseConnectionAction extends ActionBase {
  private static final long serialVersionUID = 8899126145993758045L;
  private EditorsTabbedPane tabEditor;
  
  protected ReleaseConnectionAction(EditorsTabbedPane tabPane, String caption, Icon icon, KeyStroke key, String toolTip, String hint) {
    super(caption, (char)0, icon, key, toolTip, hint);
    this.tabEditor = tabPane;
  }
  
  public ReleaseConnectionAction(EditorsTabbedPane tabPane) {
    this(tabPane, "Release", IconsItem.ICON_DB_OUT, 
        KeyStroke.getKeyStroke("control shift L"), "Release connection", "Release (close) connection to kdb+ server.");
  }
  
  public QEditor getEditor() {
    return tabEditor == null ? null : 
        tabEditor.getEditor() instanceof QEditor ? (QEditor)tabEditor.getEditor(): null;
  }

  public void actionPerformed(ActionEvent event) {
    if (tabEditor != null && tabEditor.getTabCount() > 0 && getEditor() != null) {
      KdbService connection = getEditor().getConnection();
      if (connection != null) {
        KdbConnection server = getEditor().getKDBServer();
        if (server != null) {
          server.close();
          server.removeDataListener((DataListener)getEditor());
          server = null;
          getEditor().getConnection().setStatus(ConnectionStatus.NOT_CONNECTED);
          getEditor().setConnectionStatus();
          getEditor().setConnection(null);
        }
      }
    }
  }
}
