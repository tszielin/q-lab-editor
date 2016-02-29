package tszielin.qlab.event;

import tszielin.qlab.component.editor.Editor;
import tszielin.qlab.util.event.DataEvent;

public class EditorClosing extends DataEvent<Editor> {
  private static final long serialVersionUID = -6338186833313126283L;

  public EditorClosing(Object source, Editor editor) {
    super(source, editor);
  }  
}
