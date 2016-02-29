package tszielin.qlab.action.setting;

import java.awt.event.ActionEvent;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import studio.ui.Studio;
import tszielin.qlab.config.dialog.EditorDialog;
import tszielin.qlab.error.StudioException;
import tszielin.qlab.util.action.ActionBase;

public class EditorSettingsAction extends ActionBase {
  private static final long serialVersionUID = 8941272558423716890L;
  private Studio studio;
  
  public EditorSettingsAction(Studio studio) {
    super("Editor settings...", 'E', null, null, "Editor settings", "Editor settings");
    this.studio = studio;
  }

  public void actionPerformed(ActionEvent event) {
    try {
      JDialog dialog = new EditorDialog(studio);
      dialog.setVisible(true);
      dialog.dispose();
    }
    catch (StudioException ex) {
      JOptionPane.showMessageDialog(studio, ex.getMessage(), null,
          JOptionPane.ERROR_MESSAGE);
    }
  }
}
