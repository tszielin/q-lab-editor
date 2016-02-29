package tszielin.qlab.action.connection;

import java.awt.event.ActionEvent;

import tszielin.qlab.component.editor.QEditor;
import tszielin.qlab.component.pane.EditorsTabbedPane;

public class GotoEditorAction extends ServerAction {
  private static final long serialVersionUID = 249529282680149924L;
  private EditorsTabbedPane tabEditors;
  private QEditor editor;

  public GotoEditorAction(EditorsTabbedPane tabEditors, QEditor editor) {
    super(null, editor.getFile().getName(), (char)0, null, null, 
        "Go to editor " + editor.getFile().getPath(), "Go to editor with file " + editor.getFile().getPath());
    this.tabEditors = tabEditors;
    this.editor = editor;
    setEnabled(this.tabEditors != null && this.editor != null);
  }

  @Override
  public void actionPerformed(ActionEvent event) {
    if (editor != null && tabEditors != null) {
      for (int count = 0; count < tabEditors.getTabCount(); count++) {
        if (tabEditors.getEditor(count) == this.editor) {
          tabEditors.setSelectedIndex(count);
        }
      }
    }
  }
}
