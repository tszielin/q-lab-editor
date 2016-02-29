package tszielin.qlab.action.connection;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import tszielin.qlab.config.data.KdbService;
import tszielin.qlab.util.error.ConfigException;
import tszielin.qlab.util.image.IconsItem;

public class RemoveServerAction extends ServerAction {
  private static final long serialVersionUID = 8713877887915373315L;

  public RemoveServerAction(JTree tree) {
    super(tree, "Remove...", (char)0, IconsItem.ICON_DB_REMOVE, KeyStroke.getKeyStroke("control shift R"),
        "Remover KDB+ data server information", "Remove current KDB+ data server information");
  }

  public void actionPerformed(ActionEvent event) {
    JTree tree = getSource() instanceof JTree ? (JTree)getSource() : null;

    if (tree == null) {
      return;
    }
    TreePath[] paths = tree.getSelectionPaths();
    TreePath path = paths != null && paths.length > 0 ? paths[0] : null;
    if (path.getPathCount() > 2) {
      DefaultMutableTreeNode point = 
        path.getPathComponent(path.getPathCount() - 1) instanceof DefaultMutableTreeNode
          ? (DefaultMutableTreeNode)path.getPathComponent(path.getPathCount() - 1) : null;
      KdbService connection = point != null ? 
          point.getUserObject() instanceof KdbService ? (KdbService)point.getUserObject() : null : null;
      
      if (connection != null) {
        int choice = JOptionPane.showOptionDialog(getWindow(), 
            "Remove server " + connection.getHost() + ":" + String.valueOf(connection.getPort()) +
            (connection.getName() != null ? " (" + connection.getName() + ") from list?" : " from list?"),
            "Remove server", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, 
            new Object[] {UIManager.get("OptionPane.yesButtonText"), UIManager.get("OptionPane.noButtonText")},
            UIManager.get("OptionPane.noButtonText"));
        if (choice == JOptionPane.YES_OPTION) {
          try {
            if (tree.getModel() instanceof DefaultTreeModel) {
              getConfig().removeKdbService(connection);
              TreeNode parent = point.getParent();
              ((DefaultTreeModel)tree.getModel()).removeNodeFromParent(point);            
              if (parent.getChildCount() == 0 && parent instanceof DefaultMutableTreeNode) {                
                ((DefaultTreeModel)tree.getModel()).removeNodeFromParent((DefaultMutableTreeNode)parent);
              }
              else {
                tree.setSelectionPath(new TreePath(parent));
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
