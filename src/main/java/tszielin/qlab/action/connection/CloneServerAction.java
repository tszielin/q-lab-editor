package tszielin.qlab.action.connection;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import tszielin.qlab.config.data.KdbService;
import tszielin.qlab.dialog.DataOperation;
import tszielin.qlab.dialog.KdbServiceDialog;
import tszielin.qlab.error.ArgumentException;
import tszielin.qlab.util.image.IconsItem;

public class CloneServerAction extends ServerAction {
  private static final long serialVersionUID = -4201550937883963299L;

  public CloneServerAction(JTree tree) {
    super(tree, "Clone...", 'l', IconsItem.ICON_BLANK, null,
        "Clone KDB+ data server information", "Clone existing KDB+ data server information");
  }

  public void actionPerformed(ActionEvent event) {
    JTree tree = getSource() instanceof JTree ? (JTree)getSource() : null;

    if (tree == null) {
      return;
    }
    TreePath[] paths = tree.getSelectionPaths();
    TreePath path = paths != null && paths.length > 0 ? paths[0] : null;
    DefaultMutableTreeNode node = path.getLastPathComponent() instanceof DefaultMutableTreeNode ? 
        (DefaultMutableTreeNode)path.getLastPathComponent() : null;
    KdbService connection = node != null ? node.getUserObject() instanceof KdbService ? 
        (KdbService)node.getUserObject() : null : null;

    if (connection != null) {
      try {
        KdbServiceDialog dialog = new KdbServiceDialog(getWindow(), connection, tree, DataOperation.CLONE);
        dialog.setVisible(true);
        dialog.dispose();
      }
      catch (ArgumentException ex) {
        JOptionPane.showMessageDialog(SwingUtilities.windowForComponent(tree), ex.getMessage(),
            "kdb+ Server", JOptionPane.ERROR_MESSAGE);
      }
    }
  }
}
