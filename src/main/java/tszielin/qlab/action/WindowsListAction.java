package tszielin.qlab.action;

import java.awt.event.ActionEvent;

import javax.swing.KeyStroke;

import studio.ui.EscapeDialog;
import tszielin.qlab.component.pane.EditorsTabbedPane;
import tszielin.qlab.dialog.WindowsList;
import tszielin.qlab.util.action.ActionBase;

public class WindowsListAction extends ActionBase {
  private static final long serialVersionUID = 3452783612162153113L;
  private EditorsTabbedPane tabEditors;
  
  public WindowsListAction(EditorsTabbedPane tabEditors) {
    super("", (char)0, null, KeyStroke.getKeyStroke("control F12"));
    this.tabEditors = tabEditors;
  }

  @Override
  public void actionPerformed(ActionEvent event) {
    if (tabEditors.getTabCount() > 0) {
      EscapeDialog dialog = new WindowsList(tabEditors);
      dialog.pack();
      dialog.setVisible(true, false);
      dialog.dispose();
    }
  }
}
