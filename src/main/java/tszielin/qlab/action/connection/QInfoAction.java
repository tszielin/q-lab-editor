package tszielin.qlab.action.connection;

import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import tszielin.qlab.component.pane.EditorsTabbedPane;
import tszielin.qlab.component.pane.KdbEnvironment;
import tszielin.qlab.config.data.KdbService;
import tszielin.qlab.util.image.IconsItem;

public class QInfoAction extends ServerAction {
  private static final long serialVersionUID = -254757699360137973L;
  private EditorsTabbedPane editorPane;
  public QInfoAction(JTree tree, EditorsTabbedPane editorPane) {    
    super(tree, "Info...", 'I', IconsItem.ICON_DBS, KeyStroke.getKeyStroke("control shift I"), 
        "kdb+ info", "Information about kdb+ service");
    this.editorPane = editorPane;
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
        try {
          editorPane.addTab(connection.toString(), IconsItem.ICON_DBS, new KdbEnvironment(connection));
        }
        catch (IOException ex) {
          JOptionPane.showMessageDialog(SwingUtilities.windowForComponent(tree), ex.getMessage(), 
              "Error", JOptionPane.ERROR_MESSAGE);
        }
      }
    } 
  }
}
