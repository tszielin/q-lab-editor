package tszielin.qlab.action.editor;

import java.awt.event.ActionEvent;

import javax.swing.KeyStroke;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;

import org.netbeans.editor.BaseDocument;
import org.netbeans.editor.Utilities;

import tszielin.qlab.component.pane.EditorsTabbedPane;

public class CoUncommentAction extends EditorAction {
  private static final long serialVersionUID = -5802561624926759992L;

  private final String COMMENT = "//";

  public CoUncommentAction(EditorsTabbedPane tabEditors) {
    super(tabEditors, "Comment/Uncomment", (char)0, null, KeyStroke.getKeyStroke("control SLASH"),
        "Comment text/Uncomment text", "Comment text or uncomment text");
  }

  public void actionPerformed(ActionEvent event) {
    if (getEditor() != null) {          
      if (!getEditor().isEditable() || !getEditor().isEnabled()) {
        getEditor().getToolkit().beep();
        return;
      }
      Caret caret = getEditor().getCaret();
      BaseDocument doc = (BaseDocument)getEditor().getDocument();
      try {
        if (caret.isSelectionVisible()) {
          int startPos = Utilities.getRowStart(doc, getEditor().getSelectionStart());
          int endPos = getEditor().getSelectionEnd();
          doc.atomicLock();
          try {

            if (endPos > 0 && Utilities.getRowStart(doc, endPos) == endPos) {
              endPos--;
            }

            int pos = startPos;
            for (int lineCnt = Utilities.getRowCount(doc, startPos, endPos); lineCnt > 0; lineCnt--) {
              if (Utilities.getRowEnd(doc, pos) - pos >= COMMENT.length()
                  && doc.getText(pos, COMMENT.length()).equals(COMMENT)) {
                doc.remove(pos, COMMENT.length());
              }
              else {
                doc.insertString(pos, COMMENT, null); // NOI18N
              }
              pos = Utilities.getRowStart(doc, pos, +1);
            }

          }
          finally {
            doc.atomicUnlock();
          }
        }
        else { // selection not visible
          int pos = Utilities.getRowStart(doc, caret.getDot());
          if (Utilities.getRowEnd(doc, pos) - pos >= COMMENT.length()
              && doc.getText(pos, COMMENT.length()).equals(COMMENT)) {
            doc.remove(pos, COMMENT.length());
          }
          else {
            doc.insertString(Utilities.getRowStart(doc, getEditor().getSelectionStart()), COMMENT,
                null); // NOI18N
          }
        }
      }
      catch (BadLocationException e) {
        getEditor().getToolkit().beep();
      }
    }
  }
}
