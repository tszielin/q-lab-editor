package tszielin.qlab.action.connection;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import tszielin.qlab.dialog.KdbServiceDialog;
import tszielin.qlab.error.ArgumentException;
import tszielin.qlab.util.image.IconsItem;

public class AddServerAction extends ServerAction {
  private static final long serialVersionUID = 582654510651758468L;

  public AddServerAction(JTree tree) {
    super(tree, "Add...", 'A', IconsItem.ICON_DB_ADD, KeyStroke.getKeyStroke("control A"),  
        "Add kdb+ data server", "Add new kdb+ data server");
  }

  public void actionPerformed(ActionEvent event) {
    JTree tree = getSource() instanceof JTree ? (JTree)getSource() : null;
    if (tree == null) {
      return;
    }
    TreePath[] paths = tree.getSelectionPaths();
    TreePath path = paths != null && paths.length > 0 ? paths[0] : null;
    
    String host = null;   
    if (path != null && path.getPathCount() >= 2) {
      DefaultMutableTreeNode point =  
        path.getPathComponent(1) instanceof DefaultMutableTreeNode ? 
            (DefaultMutableTreeNode)path.getPathComponent(1) : null;
      host = point != null ? 
          point.getUserObject() instanceof String ? (String)point.getUserObject() : null : null;      
    }
    
    try {
      KdbServiceDialog dialog = new KdbServiceDialog(getWindow(), host, tree);
      dialog.setVisible(true);
      dialog.dispose();
      dialog = null;
    }
    catch(ArgumentException ex) {
      JOptionPane.showMessageDialog(SwingUtilities.windowForComponent(tree), ex.getMessage(), 
          "kdb+ Server", JOptionPane.ERROR_MESSAGE);
    }
  }

}
