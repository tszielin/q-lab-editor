package tszielin.qlab.component.console;

import java.awt.BorderLayout;
import java.text.DecimalFormat;

import studio.ui.QGrid;
import tszielin.qlab.component.editor.QEditor;

public class GridConsole extends Console {
  private static final long serialVersionUID = 2171996115005832442L;
  private static final DecimalFormat FORMATTER = new DecimalFormat("#,##0");

  public GridConsole(QGrid grid, QEditor editor, String query, int type, long time) {
    super(editor);    
    setComponent(grid, query, type, time);
    add(getComponent(), BorderLayout.CENTER);
    validate();
  }
  
  public void setStatus(DataType type) {
    int rows = ((QGrid)getComponent()).getRowCount();    
    setStatus("   " + type.name() + " [" + (rows == 1 ? rows + " row]" : FORMATTER.format(rows) + " rows]"));
  }
  
  public void setStatus() {
    setStatus(DataType.Unknown);
  }
}
