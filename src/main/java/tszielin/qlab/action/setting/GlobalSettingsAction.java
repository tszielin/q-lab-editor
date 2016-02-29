package tszielin.qlab.action.setting;

import java.awt.event.ActionEvent;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import studio.ui.Studio;
import tszielin.qlab.config.dialog.GlobalDialog;
import tszielin.qlab.error.StudioException;
import tszielin.qlab.util.action.ActionBase;

public class GlobalSettingsAction extends ActionBase {
  private static final long serialVersionUID = -5370188334638966481L;
  private Studio studio;
  
  public GlobalSettingsAction(Studio studio) {
    super("Global settings...", 'G', null, null, "Global settings", "Global settings (q path, notification abiut connections)");
    this.studio = studio;
  }

  public void actionPerformed(ActionEvent event) {
    try {
      JDialog dialog = new GlobalDialog(studio);
      dialog.setVisible(true);
      dialog.dispose();
    }
    catch (StudioException ex) {
      JOptionPane.showMessageDialog(studio, ex.getMessage(), null,
          JOptionPane.ERROR_MESSAGE);
    }
  }
}
