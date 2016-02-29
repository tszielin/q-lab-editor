package tszielin.qlab.event;

import tszielin.qlab.component.editor.Editor;
import tszielin.qlab.util.event.DataEvent;

public class EditorSelected extends DataEvent<Editor> {
  private static final long serialVersionUID = -4075289813968015596L;

  public EditorSelected(Object source, Editor editor) {
    super(source, editor);
  }
}
