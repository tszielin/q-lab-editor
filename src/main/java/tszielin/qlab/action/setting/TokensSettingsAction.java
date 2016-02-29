package tszielin.qlab.action.setting;

import java.awt.event.ActionEvent;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import studio.ui.Studio;
import tszielin.qlab.config.dialog.TokensDialog;
import tszielin.qlab.error.StudioException;
import tszielin.qlab.util.action.ActionBase;

public class TokensSettingsAction extends ActionBase {
  private static final long serialVersionUID = -5713459563810983051L;
  private Studio studio;
  
  public TokensSettingsAction(Studio studio) {
    super("Token settings...", 'T', null, null, "Token colors settings", "Set token colors");
    this.studio = studio;
  }

  public void actionPerformed(ActionEvent event) {
    try {
      JDialog dialog = new TokensDialog(studio);
      dialog.setVisible(true);
      dialog.dispose();
    }
    catch (StudioException ex) {
      JOptionPane.showMessageDialog(studio, ex.getMessage(), null,
          JOptionPane.ERROR_MESSAGE);
    }
  }
}
