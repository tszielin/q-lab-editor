package tszielin.qlab.component.console;

import java.awt.Component;

import javax.swing.JEditorPane;
import javax.swing.JScrollPane;

import tszielin.qlab.component.editor.QEditor;

public class ErrorConsole extends Console {
  private static final long serialVersionUID = 6634314255904316055L;
  private JEditorPane errorPane;
  private JScrollPane scrollPane;
  
  public ErrorConsole(QEditor editor) {
    super(editor);
    setComponent();    
  }
  
  protected void setComponent() {
    if (scrollPane == null) {
      scrollPane = new JScrollPane();
    }
    if (errorPane == null) {
      errorPane = new JEditorPane("text/html", null);
      errorPane.setEditable(false);
      scrollPane.getViewport().setView(errorPane);
    }
    super.setComponent(scrollPane);
  }
  
  public void setComponent(Component component) {
    setComponent();
  }
  
  public void setError(String error) {
    errorPane.setText(error);
  }
  
  public void setError(String error, String query) {
    String result = query;
    query = query.replaceAll("&", "&amp;");
    query = query.replaceAll("\\<", "&lt;");
    query = query.replaceAll("\\>", "&gt;");
    query = query.replaceAll("/", "&frasl;");
    query = query.replaceAll("\"","&quot;");
    query = query.replaceAll("\'", "&apos;");
    error += "<br><br><font color=gray size=3 face=\"Dialog\">Query: " + query + "</font>";
    setError("<html><head></head><body>" + error + "</body></html>");
    errorPane.setToolTipText(result != null && result.trim().length() > 0 ? 
        "Query '" + result + "' causes an error." : null);
  }  
}
