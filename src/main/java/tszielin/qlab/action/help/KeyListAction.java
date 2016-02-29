package tszielin.qlab.action.help;

import java.awt.event.ActionEvent;

import studio.ui.EscapeDialog;
import tszielin.qlab.component.pane.EditorsTabbedPane;
import tszielin.qlab.dialog.ShortcutsDialog;
import tszielin.qlab.util.action.ActionBase;
import tszielin.qlab.util.image.IconsItem;

public class KeyListAction extends ActionBase {
  private static final long serialVersionUID = -808765125959848355L;
  private EditorsTabbedPane tabEditors;

  public KeyListAction(EditorsTabbedPane tabEditors) {
    super("Key strokes", 'K', IconsItem.ICON_BLANK, null, "Shortcuts in q-lab", "Defined keys (shortcuts) in q-lab");
    this.tabEditors = tabEditors;
  }

  public void actionPerformed(ActionEvent event) {
    EscapeDialog dialog = new ShortcutsDialog(tabEditors);
    dialog.validate();
    dialog.setVisible(true);
    dialog.dispose();
    
  }
}
