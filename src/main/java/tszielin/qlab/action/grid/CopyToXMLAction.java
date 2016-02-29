package tszielin.qlab.action.grid;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;

import javax.swing.JTable;
import javax.swing.KeyStroke;

import com.kx.kdb.K;

import tszielin.qlab.util.image.IconsItem;

public class CopyToXMLAction extends GridAction {
  private static final long serialVersionUID = 3160272082953782991L;
  
  public CopyToXMLAction(JTable table) {
    super(table, "Copy to XML", 'x', IconsItem.ICON_BLANK, KeyStroke.getKeyStroke("control shift X"), 
        "Copy in XML format", "Copy the selected cells to the clipboard using XML format");
  }

  @Override
  public void actionPerformed(ActionEvent event) {
    int[] rows = null;
    int[] columns = null;
    if (getTable().getSelectedColumnCount() > 1 || getTable().getSelectedRowCount() > 1) {
      rows = getTable().getSelectedRows();
      columns = getTable().getSelectedColumns();
    }
    else {
      columns = new int[getTable().getColumnCount()];
      for (int count = 0; count < getTable().getColumnCount(); count++) {
        columns[count] = count;
      }
      rows = new int[getTable().getRowCount()];
      for (int count = 0; count < getTable().getRowCount(); count++) {
        rows[count] = count;
      }
    }

    StringBuffer strBuf = new StringBuffer("<rows>").append(System.getProperty("line.separator"));
    for (int row = 0; row < rows.length; row++) {
      strBuf.append("  <row>").append(System.getProperty("line.separator"));      
      for (int col = 0; col < columns.length; col++) {
        Object value = getTable().getValueAt(rows[row], columns[col]);
        if (value instanceof K.KType<?> && !((K.KType<?>)value).isNull()) {
          String str = ((K.KType<?>)value).toString(false);
          if (str != null && !str.trim().isEmpty()) {
            strBuf.append("    <").append(getTable().getColumnName(columns[col])).append(">");
            strBuf.append(str);
            strBuf.append("</").append(getTable().getColumnName(columns[col])).append(">").append(System.getProperty("line.separator"));
          }
        }
      }
      strBuf.append("  </row>").append(System.getProperty("line.separator"));
    }
    strBuf.append("</rows>");
    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(strBuf.toString()), null);
  }
}
