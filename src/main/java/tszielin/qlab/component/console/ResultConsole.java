package tszielin.qlab.component.console;

import javax.swing.JEditorPane;
import javax.swing.JScrollPane;

import tszielin.qlab.component.editor.QEditor;

public class ResultConsole extends Console {
  private static final long serialVersionUID = -1451288885479096297L;

  public ResultConsole(JEditorPane textPane, QEditor editor, String query, int type, long time) {
    super(editor);    
    setComponent(textPane, query, type, time);
    validate();
  }
  
  public void setComponent(JEditorPane textPane, String query, int type, long time) {
    textPane.setEditable(false);
    super.setComponent(new JScrollPane(textPane), query, type, time);    
  }  
  
  public void setStatus() {
    setStatus("Result console");
  }
}
