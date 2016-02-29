package tszielin.qlab.action.project;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.tree.TreePath;

import studio.ui.EscapeDialog;
import tszielin.qlab.component.tree.item.FileProject;
import tszielin.qlab.dialog.ProjectDialog;
import tszielin.qlab.error.ArgumentException;
import tszielin.qlab.error.StudioException;
import tszielin.qlab.util.error.ConfigException;

public class RenameProjectAction extends ProjectAction {
  private static final long serialVersionUID = 3913843794683739398L;

  public RenameProjectAction(JTree tree) throws ConfigException, ArgumentException {
    super(tree, "Rename", 'R', null, KeyStroke.getKeyStroke("contol shift R"), "Rename project", "Rename project");
  }

  public void actionPerformed(ActionEvent event) {
    JTree tree = getSource() instanceof JTree ? (JTree)getSource() : null;
    if (tree == null) {
      return;
    }

    TreePath[] paths = tree.getSelectionPaths();
    if (paths != null && paths.length != 1) {
      JOptionPane.showMessageDialog(getWindow(), "Rename of many projects is impossible...",
          "Rename project", JOptionPane.WARNING_MESSAGE);
    }
    TreePath path = getParent(paths != null ? paths[0] : null);
    if (path != null && path.getLastPathComponent() instanceof FileProject &&
        ((FileProject)path.getLastPathComponent()).getProject() != null) {
      try {
        EscapeDialog dialog = new ProjectDialog(getWindow(), getTree(),
            ((FileProject)path.getLastPathComponent()).getProject());
        dialog.setVisible(true);
        dialog.dispose();
      }
      catch (StudioException ex) {
        JOptionPane.showMessageDialog(getWindow(), ex.getMessage(), 
            "Rename project", JOptionPane.ERROR_MESSAGE);
      }
    }
  }
}
