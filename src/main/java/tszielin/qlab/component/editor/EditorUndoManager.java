package tszielin.qlab.component.editor;

import javax.swing.event.UndoableEditEvent;
import javax.swing.undo.*;

class EditorUndoManager extends UndoManager {
  private static final long serialVersionUID = 5591443417715211474L;
  private Editor editor;
  
  public EditorUndoManager(Editor editor) {
    super();
    this.editor = editor;
  }

  @Override
  public void undoableEditHappened(UndoableEditEvent e) {
    super.undoableEditHappened(e);
    editor.setActions();
  }

  @Override
  public synchronized void redo() throws CannotRedoException {
    super.redo();
    editor.setActions();
  }

  @Override
  public synchronized void undo() throws CannotUndoException {
    super.undo();
    editor.setActions();
  }
}
