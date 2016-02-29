package tszielin.qlab.action.help;

import java.awt.event.ActionEvent;

import javax.swing.JDialog;

import tszielin.qlab.component.pane.EditorsTabbedPane;
import tszielin.qlab.dialog.AboutDialog;
import tszielin.qlab.util.action.ActionBase;
import tszielin.qlab.util.image.IconsItem;

public class AboutAction extends ActionBase {
  private static final long serialVersionUID = -5844542832019972678L;
  private EditorsTabbedPane tabEditors;
  
  public AboutAction(EditorsTabbedPane tabEditors) {
    super("About...", 'A', IconsItem.ICON_BLANK, null, "About", "About application");
    this.tabEditors = tabEditors;
  }

  public void actionPerformed(ActionEvent event) {
    JDialog dialog = new AboutDialog(tabEditors);
    dialog.setVisible(true);
    dialog.dispose();
    dialog = null;
  }
}
