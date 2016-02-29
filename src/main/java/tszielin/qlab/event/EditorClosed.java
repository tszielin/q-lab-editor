package tszielin.qlab.event;

import tszielin.qlab.component.editor.Editor;
import tszielin.qlab.util.event.DataEvent;

public class EditorClosed extends DataEvent<Editor> {
  private static final long serialVersionUID = 5010784343947835734L;

  public EditorClosed(Object source, Editor editor) {
    super(source, editor);
  }  
}
