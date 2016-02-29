package tszielin.qlab.action.setting;

import java.awt.event.ActionEvent;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import studio.ui.Studio;
import tszielin.qlab.config.dialog.ProjectDialog;
import tszielin.qlab.error.StudioException;
import tszielin.qlab.util.action.ActionBase;

public class ProjectSettingsAction extends ActionBase {
  private static final long serialVersionUID = 5628346340043293245L;
  private Studio studio;
  
  public ProjectSettingsAction(Studio studio) {
    super("Project settings...", 'P', null, null, "File extensions assigned to projects.", "Set file extensions assigned to projects (shown in Projects view).");
    this.studio = studio;
  }

  public void actionPerformed(ActionEvent event) {
    try {
      JDialog dialog = new ProjectDialog(studio);
      dialog.setVisible(true);
      dialog.dispose();
    }
    catch (StudioException ex) {
      JOptionPane.showMessageDialog(studio, ex.getMessage(), null,
          JOptionPane.ERROR_MESSAGE);
    }
  }
}
