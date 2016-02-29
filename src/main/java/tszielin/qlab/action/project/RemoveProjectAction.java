package tszielin.qlab.action.project;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.tree.TreePath;

import tszielin.qlab.component.tree.item.FileProject;
import tszielin.qlab.component.tree.model.FileTreeModel;
import tszielin.qlab.config.data.Project;
import tszielin.qlab.error.ArgumentException;
import tszielin.qlab.util.error.ConfigException;
import tszielin.qlab.util.image.IconsItem;

public class RemoveProjectAction extends ProjectAction {
  private static final long serialVersionUID = -3531166949285593137L;

  public RemoveProjectAction(JTree tree) throws ConfigException, ArgumentException {
    super(tree, "Remove", (char)0, IconsItem.ICON_FOLDER_DELETE, 
        KeyStroke.getKeyStroke("control shift v"), "Remove project", "Remove project from view");
  }

  public void actionPerformed(ActionEvent event) {
    JTree tree = getSource() instanceof JTree ? (JTree)getSource() : null;
    if (tree == null) {
      return;
    }
    
    TreePath[] paths = tree.getSelectionPaths();
    if (paths != null && paths.length > 0) {
      for (TreePath path : paths) {
        TreePath treePath = getParent(path);
        if (treePath != null && treePath.getLastPathComponent() instanceof FileProject) {
          Project project = ((FileProject)treePath.getLastPathComponent()).getProject();
          if (project != null) {
            if (JOptionPane.showOptionDialog(getWindow(), "Remove project " + project.getName() +
                " from list?", "Remove project", JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, new Object[]{
                    UIManager.get("OptionPane.yesButtonText"),
                    UIManager.get("OptionPane.noButtonText")},
                UIManager.get("OptionPane.noButtonText")) == JOptionPane.YES_OPTION) {
              if (JOptionPane.showOptionDialog(getWindow(),
                  "You're about to remove project.\nAre you really sure?", "Remove project",
                  JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, new Object[]{
                      UIManager.get("OptionPane.yesButtonText"),
                      UIManager.get("OptionPane.noButtonText")},
                  UIManager.get("OptionPane.noButtonText")) == JOptionPane.YES_OPTION) {
                try {
                  getConfig().remove(project);
                  if (tree.getModel() instanceof FileTreeModel) {
                    ((FileTreeModel)tree.getModel()).removeRoot(project);
                  }
                }
                catch (ConfigException ex) {
                  JOptionPane.showMessageDialog(getWindow(), ex.getMessage(), null,
                      JOptionPane.ERROR_MESSAGE);
                }
              }
            }
          }
        }
      }
    }
  }
}