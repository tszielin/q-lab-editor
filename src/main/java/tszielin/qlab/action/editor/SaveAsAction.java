package tszielin.qlab.action.editor;

import java.awt.event.ActionEvent;

import tszielin.qlab.component.pane.EditorsTabbedPane;
import tszielin.qlab.util.image.IconsItem;

public class SaveAsAction extends SaveAction {
  private static final long serialVersionUID = -6859567080847806361L;
  
  public SaveAsAction(EditorsTabbedPane tabEditors) {
    super(tabEditors, "Save as..", (char)0, IconsItem.ICON_SAVE_AS, null, 
        "Save as...", "Save changes as new q script.");
  }

  @Override
  public void actionPerformed(ActionEvent event) {
    if (getEditor() != null) {
      saveAs();
    }
  }
}
