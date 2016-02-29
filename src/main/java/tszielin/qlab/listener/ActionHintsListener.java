package tszielin.qlab.listener;

import java.awt.event.MouseEvent;

import javax.swing.Action;

import org.netbeans.editor.Utilities;

import tszielin.qlab.component.editor.Editor;
import tszielin.qlab.component.pane.EditorsTabbedPane;
import tszielin.qlab.util.listener.ActionHintListener;

public class ActionHintsListener extends ActionHintListener {
  private EditorsTabbedPane tabEditors;
  
  public ActionHintsListener(EditorsTabbedPane tabEditors, Action action) {
    super(action);
    this.tabEditors = tabEditors;
  }
  
  public ActionHintsListener(EditorsTabbedPane tabEditors, String hint) {
    super(hint);
    this.tabEditors = tabEditors;
  }
  
  @Override
  public void mouseEntered(MouseEvent event) {
    setText(hint);
  }
  
  @Override
  public void mouseExited(MouseEvent event) {
    setText(null);
  }

  private void setText(String text) {
    if (tabEditors != null && tabEditors.getTabCount() > 0) {
      Editor editor = tabEditors.getEditor();
      if (editor != null) {
        Utilities.setStatusText(editor, text);
      }
    }
  }
}
