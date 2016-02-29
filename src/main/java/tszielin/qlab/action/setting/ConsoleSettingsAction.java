package tszielin.qlab.action.setting;

import java.awt.event.ActionEvent;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import studio.ui.Studio;
import tszielin.qlab.config.dialog.ConsoleDialog;
import tszielin.qlab.error.StudioException;
import tszielin.qlab.util.action.ActionBase;

public class ConsoleSettingsAction extends ActionBase {
  private static final long serialVersionUID = -5445239619945894726L;
  private Studio studio;
  
  public ConsoleSettingsAction(Studio studio) {
    super("Console settings...", 'C', null, null, "Console settings", "Multi-consoles, data type formats.");
    this.studio = studio;
  }

  public void actionPerformed(ActionEvent event) {
    try {
      JDialog dialog = new ConsoleDialog(studio);
      dialog.setVisible(true);
      dialog.dispose();
    }
    catch (StudioException ex) {
      JOptionPane.showMessageDialog(studio, ex.getMessage(), null,
          JOptionPane.ERROR_MESSAGE);
    }
  }
}
