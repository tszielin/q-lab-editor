package tszielin.qlab.action.editor;

import java.awt.event.ActionEvent;

import javax.swing.Icon;
import javax.swing.KeyStroke;

import tszielin.qlab.component.pane.CloseTabbedPane;
import tszielin.qlab.util.action.ActionBase;
import tszielin.qlab.util.image.IconsItem;

public class CloseAction extends ActionBase {
  private static final long serialVersionUID = -6859567080847806361L;
  private CloseTabbedPane tabPane;
  
  protected CloseAction(CloseTabbedPane tabPane, String caption, Icon icon, KeyStroke key, String toolTip, String hint) {
    super(caption, (char)0, icon, key, toolTip, hint);
    this.tabPane = tabPane;
  }
  
  public CloseAction(CloseTabbedPane tabPane) {
    this(tabPane, "Close", IconsItem.ICON_CLOSE_DOC, KeyStroke.getKeyStroke("control W"), "Close editor", "Close editor page");
  }
  
  protected CloseTabbedPane getTabPane() {
    return tabPane;
  }

  public void actionPerformed(ActionEvent event) {
    if (tabPane != null && tabPane.getTabCount() > 0 && tabPane.canClose()) {
      tabPane.removeTabAt(tabPane.getSelectedIndex());
    }
  }

}
