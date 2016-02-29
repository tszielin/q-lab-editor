package tszielin.qlab.action.connection;

import java.awt.event.ActionEvent;
import java.util.Set;

import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import tszielin.qlab.config.data.KdbService;
import tszielin.qlab.util.error.ConfigException;
import tszielin.qlab.util.image.IconsItem;

public class RemoveServerGroupAction extends ServerAction {
  private static final long serialVersionUID = -6718687734213166935L;

  public RemoveServerGroupAction(JTree tree) {
    super(tree, "Remove all...", (char)0, IconsItem.ICON_BLANK, KeyStroke.getKeyStroke("control alt R"),
        "Remove all connections assigned to server", "Remove all connections assigned to selected server");
  }

  public void actionPerformed(ActionEvent event) {
    JTree tree = getSource() instanceof JTree ? (JTree)getSource() : null;

    if (tree == null) {
      return;
    }
    TreePath[] paths = tree.getSelectionPaths();
    TreePath path = paths != null && paths.length > 0 ? paths[0] : null;
    if (path.getPathCount() == 2) {
      DefaultMutableTreeNode point = path.getPathComponent(path.getPathCount() - 1) instanceof DefaultMutableTreeNode
          ? (DefaultMutableTreeNode)path.getPathComponent(path.getPathCount() - 1) : null;
      String host = point != null && point.getUserObject() instanceof String
          ? (String)point.getUserObject() : null;

      if (host != null) {
        if (JOptionPane.showOptionDialog(getWindow(),
            "Remove all connection informations for server " + host + " from list?",
            "Remove servers", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
            new Object[]{UIManager.get("OptionPane.yesButtonText"),
                UIManager.get("OptionPane.noButtonText")}, 
                UIManager.get("OptionPane.noButtonText")) == JOptionPane.YES_OPTION) {
          if (JOptionPane.showOptionDialog(getWindow(),
              "You're about to remove entire group.\nAre you really sure?", "Remove servers",
              JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, new Object[]{
                  UIManager.get("OptionPane.yesButtonText"),
                  UIManager.get("OptionPane.noButtonText")},
              UIManager.get("OptionPane.noButtonText")) == JOptionPane.YES_OPTION) {
            try {
              Set<KdbService> connections = getConfig().getConnections(host);
              if (connections != null) {
                for (KdbService connection : connections) {
                  getConfig().removeKdbService(connection);
                }
                if (tree.getModel().getRoot() instanceof DefaultMutableTreeNode) {
                  if (tree.getModel() instanceof DefaultTreeModel) {
                    ((DefaultTreeModel)tree.getModel()).removeNodeFromParent(point);
                  }
                }
              }
            }
            catch (ConfigException ex) {
              JOptionPane.showMessageDialog(getWindow(), ex.getMessage(), "kdb+ Server",
                  JOptionPane.ERROR_MESSAGE);
            }
          }
        }
      }
    }
  }
}
