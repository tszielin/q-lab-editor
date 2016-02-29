package tszielin.qlab.action.connection;

import java.awt.event.ActionEvent;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import com.kx.KdbConnection;

import tszielin.qlab.component.editor.QEditor;
import tszielin.qlab.component.pane.EditorsTabbedPane;
import tszielin.qlab.config.data.ConnectionStatus;
import tszielin.qlab.config.data.KdbService;
import tszielin.qlab.util.image.IconsItem;
import tszielin.qlab.util.listener.DataListener;

public class ReleaseServerAction extends ServerAction {
  private static final long serialVersionUID = 610549067345944884L;
  private EditorsTabbedPane tabEditors;

  public ReleaseServerAction(EditorsTabbedPane tabEditors, JTree tree) {
    super(tree, "Release", 'l', IconsItem.ICON_DB_OUT, null, 
        "Release active connection.", "Release (close) active connection to the server.");
    this.tabEditors = tabEditors;
  }

  @Override
  public void actionPerformed(ActionEvent event) {
    JTree tree = getSource() instanceof JTree ? (JTree)getSource() : null;

    if (tree == null) {
      return;
    }
    TreePath[] paths = tree.getSelectionPaths();
    TreePath path = paths != null && paths.length > 0 ? paths[0] : null;
    if (path != null) {
      DefaultMutableTreeNode point = 
        path.getPathComponent(path.getPathCount() - 1) instanceof DefaultMutableTreeNode
          ? (DefaultMutableTreeNode)path.getPathComponent(path.getPathCount() - 1) : null;
      KdbService connection = point != null ? 
          point.getUserObject() instanceof KdbService ? (KdbService)point.getUserObject() : null : null;
      if (connection != null) {
        if (tabEditors.getTabCount() > 0) {
          for (int count = 0; count < tabEditors.getTabCount(); count++) {
            if (tabEditors.getEditor(count) instanceof QEditor) {
              QEditor qEditor = (QEditor)tabEditors.getEditor(count);
              if (connection.equals(qEditor.getConnection())) {
                KdbConnection server = qEditor.getKDBServer();
                if (server != null) {
                  server.close();
                  server.removeDataListener((DataListener)tabEditors.getEditor(count));
                  server = null;
                                    
                  qEditor.getConnection().setStatus(ConnectionStatus.NOT_CONNECTED);
                  qEditor.setConnectionStatus();
                  qEditor.setConnection(null);
                }
              }
            }
          }                     
        }
      }
    } 
  }
}
