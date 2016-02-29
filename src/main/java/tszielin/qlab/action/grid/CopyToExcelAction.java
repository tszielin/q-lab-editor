package tszielin.qlab.action.grid;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;

import javax.swing.JTable;
import javax.swing.KeyStroke;

import com.kx.kdb.K;

import tszielin.qlab.util.image.IconsItem;

public class CopyToExcelAction extends GridAction {
  private static final long serialVersionUID = 3160272082953782991L;
  
  public CopyToExcelAction(JTable table) {
    super(table, "Copy to Excel", 'e', IconsItem.ICON_BLANK, 
        KeyStroke.getKeyStroke("control shift E"), 
        "Copy in Excel format", "Copy the selected cells to the clipboard using Excel format");
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

    StringBuffer strBuf = new StringBuffer();
    if (rows.length == getTable().getRowCount()) {
      for (int count = 0; count < columns.length; count++) {
        strBuf.append("\"").append(getTable().getColumnName(columns[count])).append("\"").append(
            count < columns.length - 1 ? "\t" : "");
      }
      strBuf.append(System.getProperty("line.separator"));
    }
    
    for (int row = 0; row < rows.length; row++) {
      strBuf.append(row > 0 ? System.getProperty("line.separator") : "");
      for (int col = 0; col < columns.length; col++) {
        Object value = getTable().getValueAt(rows[row], columns[col]);
        if (value instanceof K.KType<?>) {
          if (!((K.KType<?>)value).isNull()) {
            String str = ((K.KType<?>)value).toString(false);
            boolean symColumn = getTable().getColumnClass(col) == K.KSymbolArray.class;
            strBuf.append(symColumn ? "\"" : "").append(str).append(symColumn ? "\"" : "").append(
                col < columns.length - 1 ? "\t" : "");
          }
          else {
            strBuf.append(((K.KType<?>)value).toString(false));
          }
        }
        else {
          strBuf.append("\t");
        }
      }
    }
    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(strBuf.toString()), null);
  }
}
