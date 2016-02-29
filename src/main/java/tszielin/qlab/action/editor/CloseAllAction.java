package tszielin.qlab.action.editor;

import java.awt.event.ActionEvent;

import javax.swing.KeyStroke;

import tszielin.qlab.component.pane.CloseTabbedPane;
import tszielin.qlab.util.image.IconsItem;

public class CloseAllAction extends CloseAction {
  private static final long serialVersionUID = 1601105264558792731L;

  public CloseAllAction(CloseTabbedPane tabEditors) {
    super(tabEditors, "Close All", IconsItem.ICON_CLOSE_ALL_DOC, 
        KeyStroke.getKeyStroke("control shift W"), "Close all editors", "Close all editor pages");
  }
  
  
  @Override
  public void actionPerformed(ActionEvent event) {
    if (getTabPane() != null && getTabPane().canClose()) {
      while (getTabPane().getTabCount() > 0) {
        getTabPane().removeTabAt(getTabPane().getTabCount() - 1);
      }
    }
  }
}
