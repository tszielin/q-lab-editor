package tszielin.qlab.action.kdb;

import javax.swing.Action;
import javax.swing.KeyStroke;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;

import org.netbeans.editor.AnnotationDesc;
import org.netbeans.editor.AnnotationTypes;
import org.netbeans.editor.Annotations;
import org.netbeans.editor.BaseDocument;
import org.netbeans.editor.Utilities;

import tszielin.qlab.component.editor.QEditor;
import tszielin.qlab.component.pane.ConsolesTabbedPane;
import tszielin.qlab.component.pane.EditorsTabbedPane;
import tszielin.qlab.util.image.IconsItem;

public class RunAction extends RunLineAction {  
  private static final long serialVersionUID = -3596751170104618285L;  

  public RunAction(EditorsTabbedPane tabEditors, ConsolesTabbedPane tabConsoles, Action action) {
    super(tabEditors, tabConsoles, action, "Execute...", 'E', 
        IconsItem.ICON_RUN, KeyStroke.getKeyStroke("control E"), "Execute..", 
        "Execute the full or highlighted text as a query.");
  }  
  
  @Override
  protected String getQuery() {
    String query = null;
    if (getEditor() instanceof QEditor) {          
      Caret caret = getEditor().getCaret();
      BaseDocument doc = (BaseDocument)getEditor().getDocument();
      try {
        if (caret.isSelectionVisible()) {
          query = getEditor().getText(getEditor().getSelectionStart(), 
              getEditor().getSelectionEnd()- getEditor().getSelectionStart());          
        }
        else {
          if (((QEditor)getEditor()).isDebuger() && doc.getAnnotations() != null) {
            int rows = Utilities.getRowCount(doc);
            if (rows != 0 && Utilities.getFirstNonEmptyRow(doc, 0, true) <= rows) {            
              Annotations annotations = doc.getAnnotations();
              query = "";
              for (int count = 0; count < rows; count++) {
                int start = Utilities.getRowStartFromLineOffset(doc, count);
                int finish = Utilities.getRowEnd(doc, start);
                if (annotations.getAnnotation(count, AnnotationTypes.PROP_COMBINE_GLYPHS) != null) {
                  AnnotationDesc desc = annotations.getAnnotation(count, AnnotationTypes.PROP_COMBINE_GLYPHS);
                  if (desc.getLine() != 1) {
                    query += (desc.getLine() != 1 ? ".dbg.break[" + count + "]" : "") + getEditor().getText(start, finish - start);
                  }
                }
                else {
                  query += getEditor().getText(start, finish - start);
                }
              }
            }
          }
          else {
            query = getEditor().getText();
          }
        }
      }
      catch (BadLocationException e) {
        getEditor().getToolkit().beep();
      }
    }
    return query == null || query.trim().isEmpty() ? null : query;
  }
}
