package tszielin.qlab.action.project;

import java.awt.Window;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.tree.TreePath;

import tszielin.qlab.component.tree.item.FileItem;
import tszielin.qlab.component.tree.item.FileProject;
import tszielin.qlab.config.ProjectConfig;
import tszielin.qlab.error.ArgumentException;
import tszielin.qlab.util.action.popup.ActionPopup;
import tszielin.qlab.util.error.ConfigException;

abstract public class ProjectAction extends ActionPopup {
  private static final long serialVersionUID = 7488448575455548166L;  
  private ProjectConfig config;
  
  protected ProjectAction(JTree tree, String caption, char mnemonic, 
      Icon icon, KeyStroke key, String toolTip, String hint) throws ConfigException, ArgumentException {
    super(tree, caption, mnemonic, icon, key, toolTip, hint);
    config = ProjectConfig.getConfig();
  }

  public JTree getTree() {
    return getSource() instanceof JTree ? (JTree)getSource() : null;
  }
  
  protected Window getWindow() {
    return SwingUtilities.windowForComponent(getTree());
  }
  
  protected ProjectConfig getConfig() {
    return config;
  }
  
  protected TreePath getParent(TreePath path) {
    if (path == null) {
      return path; 
    }
    if (path.getLastPathComponent() instanceof FileProject) {
      return path;
    }
    if (path.getLastPathComponent() instanceof FileItem) {
      TreePath treePath = path.getParentPath();
      while (treePath != null && !(treePath.getLastPathComponent() instanceof FileProject)) {
        treePath = treePath.getParentPath();
      }
      return treePath;
    }
    return null;
  }
}
