package tszielin.qlab.action.grid;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;

import javax.swing.JTable;
import javax.swing.KeyStroke;

import tszielin.qlab.util.image.IconsItem;

public class CopyResultAction extends GridAction {
  private static final long serialVersionUID = -5515684992795478500L;

  public CopyResultAction(JTable table) {
    super(table, "Copy result", 'c', IconsItem.ICON_COPY, KeyStroke.getKeyStroke("control C"), 
        "Copy result", "Copy result to the clipboard");
  }
  @Override
  public void actionPerformed(ActionEvent event) {
    if (getTable().getToolTipText() != null &&
        !getTable().getToolTipText().trim().isEmpty()) {
      Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
          new StringSelection(getTable().getToolTipText()), null);
    }
  }
}
