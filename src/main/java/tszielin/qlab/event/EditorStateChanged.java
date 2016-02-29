package tszielin.qlab.event;

import tszielin.qlab.util.event.DataEvent;

public class EditorStateChanged extends DataEvent<Boolean> {
  private static final long serialVersionUID = 8799932699627848439L;

  public EditorStateChanged(Object source, boolean modified) {
    super(source, modified);
  }
  
  public EditorStateChanged(Object source) {
    super(source, Boolean.TRUE);
  }
  
  public boolean isModified() {
    return getData();
  }
}
