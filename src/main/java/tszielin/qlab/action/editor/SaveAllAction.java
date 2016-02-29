package tszielin.qlab.action.editor;

import java.awt.event.ActionEvent;

import javax.swing.KeyStroke;

import tszielin.qlab.component.pane.CloseTabbedPane;
import tszielin.qlab.util.image.IconsItem;

public class SaveAllAction extends SaveAction {
  private static final long serialVersionUID = 6439551531863012752L;
  private CloseTabbedPane tabEditors;
  
  public SaveAllAction(CloseTabbedPane tabEditors) {
    super(null, "Save All", (char)0, IconsItem.ICON_SAVE_ALL, KeyStroke.getKeyStroke("control shift S"), 
        "Save all scripts", "Save changes in all modified scripts.");
    this.tabEditors = tabEditors;
  }

  @Override
  public void actionPerformed(ActionEvent event) {
    if (tabEditors != null && tabEditors.getTabCount() > 0) {
      for (int count = 0; count < tabEditors.getTabCount(); count++) {
        super.actionPerformed(event);
      }
    }
  }
}
