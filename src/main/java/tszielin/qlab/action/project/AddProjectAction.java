package tszielin.qlab.action.project;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.KeyStroke;

import studio.ui.EscapeDialog;
import tszielin.qlab.dialog.ProjectDialog;
import tszielin.qlab.error.ArgumentException;
import tszielin.qlab.error.StudioException;
import tszielin.qlab.util.error.ConfigException;
import tszielin.qlab.util.image.IconsItem;

public class AddProjectAction extends ProjectAction {
  private static final long serialVersionUID = -5177764534811080704L;

  public AddProjectAction(JTree tree) throws ConfigException, ArgumentException {
    super(tree, "New project", 'P', IconsItem.ICON_FOLDER_ADD, KeyStroke.getKeyStroke("control shift P"), 
        "New project", "Create new project");
  }

  public void actionPerformed(ActionEvent event) {
    try {
      EscapeDialog dialog = new ProjectDialog(getWindow(), getTree(), null);
      dialog.setVisible(true);
      dialog.dispose();
    }
    catch (StudioException ex) {
      JOptionPane.showMessageDialog(getWindow(), ex.getMessage(), null, JOptionPane.ERROR_MESSAGE);
    }
  }
}
