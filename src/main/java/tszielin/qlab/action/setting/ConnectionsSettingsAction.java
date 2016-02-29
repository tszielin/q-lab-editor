package tszielin.qlab.action.setting;

import java.awt.event.ActionEvent;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;

import tszielin.qlab.config.dialog.KdbServiceSortDialog;
import tszielin.qlab.error.StudioException;
import tszielin.qlab.util.action.ActionBase;

public class ConnectionsSettingsAction extends ActionBase {
  private static final long serialVersionUID = -7890980293485922188L;
  private JTree tree;
  
  public ConnectionsSettingsAction(JTree tree) {
    super("Sort connections...", 'S', null, null, "Sort connections", "Sort connections by name, user or port");
    this.tree = tree;
  }

  public void actionPerformed(ActionEvent event) {
    try {
      JDialog dialog = new KdbServiceSortDialog(SwingUtilities.windowForComponent(tree), tree);
      dialog.setVisible(true);
      dialog.dispose();
    }
    catch (StudioException ex) {
      JOptionPane.showMessageDialog(SwingUtilities.windowForComponent(tree), ex.getMessage(), null,
          JOptionPane.ERROR_MESSAGE);
    }
  }
}
