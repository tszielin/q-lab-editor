package tszielin.qlab.action.project;

import java.awt.event.ActionEvent;

import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.event.TreeModelEvent;
import javax.swing.tree.TreePath;

import tszielin.qlab.component.tree.item.FileItem;
import tszielin.qlab.component.tree.model.FileTreeModel;
import tszielin.qlab.error.ArgumentException;
import tszielin.qlab.util.error.ConfigException;

public class RefreshProjectAction extends ProjectAction {
  private static final long serialVersionUID = -9013676865591607409L;

  public RefreshProjectAction(JTree tree) throws ConfigException, ArgumentException {
    super(tree, "Refresh", 'e', null, KeyStroke.getKeyStroke("F5"), 
        "Refresh project view", "Refresh project files in view");
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (getTree() != null && getTree().getModel() instanceof FileTreeModel) {
      TreePath[] paths = getTree().getSelectionPaths();
      if (paths != null) {
        for (TreePath path : paths) {
          TreePath item = path;
          if (path.getLastPathComponent() instanceof FileItem) {
            item = path.getParentPath();  
          }
          if (item != null) {
            ((FileTreeModel)getTree().getModel()).fireTreeStructureChanged(new TreeModelEvent(this, item));
          }
        }
      }
    }
  }
}
