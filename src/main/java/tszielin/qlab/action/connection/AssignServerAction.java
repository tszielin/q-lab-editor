package tszielin.qlab.action.connection;

import java.awt.event.ActionEvent;

import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import tszielin.qlab.component.pane.EditorsTabbedPane;
import tszielin.qlab.config.data.KdbService;
import tszielin.qlab.event.KdbServiceChanged;
import tszielin.qlab.util.image.IconsItem;

public class AssignServerAction extends ServerAction {
  private static final long serialVersionUID = -5923858240529694946L;
  private EditorsTabbedPane tabEditors;

  public AssignServerAction(JTree tree, EditorsTabbedPane tabEditors) {
    super(tree, "Assign...", (char)0, IconsItem.ICON_DB_ASSIGN, KeyStroke.getKeyStroke("ctrl ENTER"),
        "Assign KDB+ server to editor", "Assign selected KDB+ server to current editor");
    this.tabEditors = tabEditors;
  }

  public void actionPerformed(ActionEvent event) {
    JTree tree = getSource() instanceof JTree ? (JTree)getSource() : null;

    if (tree == null) {
      return;
    }
    TreePath[] paths = tree.getSelectionPaths();
    TreePath path = paths != null && paths.length > 0 ? paths[0] : null;
    if (path != null  && path.getLastPathComponent() instanceof DefaultMutableTreeNode) {
      DefaultMutableTreeNode node = (DefaultMutableTreeNode)path.getLastPathComponent();
      if (node.getUserObject() instanceof KdbService) {
        onData(new KdbServiceChanged(this, (KdbService)node.getUserObject()));
        if (this.tabEditors.getTabCount() > 0) {
          this.tabEditors.getEditor().requestFocus();
        }
      }
    }
  }
}
