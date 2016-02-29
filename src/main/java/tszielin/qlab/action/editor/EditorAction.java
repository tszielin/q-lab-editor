package tszielin.qlab.action.editor;

import javax.swing.Icon;
import javax.swing.KeyStroke;

import tszielin.qlab.component.editor.Editor;
import tszielin.qlab.component.pane.EditorsTabbedPane;
import tszielin.qlab.util.action.ActionBase;

abstract public class EditorAction extends ActionBase {
  private static final long serialVersionUID = -6859567080847806361L;
  
  private EditorsTabbedPane tabEditors;
  
  protected EditorAction(EditorsTabbedPane tabEditors,  String caption, char mnemonic, 
      Icon icon, KeyStroke key, String toolTip, String hint) {
    super(caption, mnemonic, icon, key, toolTip, hint);
    this.tabEditors = tabEditors;
  }
  
  protected EditorsTabbedPane getEditors() {
    return tabEditors;
  }
  
  protected Editor getEditor() {
    return tabEditors != null && tabEditors.getTabCount() > 0 ? tabEditors.getEditor() : null;
  }
}
