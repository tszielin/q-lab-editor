/*
 * Studio for kdb+ by Charles Skelton is licensed under a Creative Commons
 * Attribution-Noncommercial-Share Alike 3.0 Germany License
 * http://creativecommons.org/licenses/by-nc-sa/3.0 except for the netbeans components which retain
 * their original copyright notice
 */

package studio.kdb;

import com.kx.kdb.K;

import tszielin.qlab.config.AppConfig;
import tszielin.qlab.error.StudioException;

public class KTableTableModel extends KTableModel {
  private static final long serialVersionUID = -7200555731236652967L;
  private K.KTable table;

  public void append(K.KTable table) {
    this.table.append(table);
    if (isSortedAsc())
      asc(sortedByColumn);
    else
      if (isSortedDesc())
        desc(sortedByColumn);
  }

  public void asc(int col) {
    sortIndex = null;
    K.KBaseArray v = (K.KBaseArray)table.getValues().get(col);
    sortIndex = v.gradeUp();
    sorted = 1;
    sortedByColumn = col;
  }

  public void desc(int col) {
    sortIndex = null;
    K.KBaseArray v = (K.KBaseArray)table.getValues().get(col);

    sortIndex = v.gradeDown();
    sorted = -1;
    sortedByColumn = col;
  }

 public void setData(K.KTable table) {
    this.table = table;
  }

  public static boolean isTable(Object obj) {
    if (obj instanceof K.KTable)
      return true;
    else
      if (obj instanceof K.KDictionary) {
        K.KDictionary d = (K.KDictionary)obj;

        if ((d.getKeys() instanceof K.KTable) && (d.getValues() instanceof K.KTable))
          return true;
      }

    return false;
  }

  public KTableTableModel() {
    super();
  }

  public KTableTableModel(K.KTable table) {
    super();
    setData(table);
  }

  public boolean isKey(int column) {
    return false;
  }

  public int getColumnCount() {
    return table.getKeys().getLength();
  }

  public int getRowCount() {
    return ((K.KBaseArray)table.getValues().get(0)).getLength();
  }

  public K.KType<?> getValue(int row, int col) {
    row = (sortIndex == null) ? row : sortIndex[row];
    K.KBaseArray v = (K.KBaseArray)table.getValues().get(col);
    return v.get(row);
  }

  public Object getValueAt(int row, int col) {
    Object o = getValue(row, col);
    if (o instanceof K.Function) {
      int len = 15;
      try {
        len = AppConfig.getConfig().getFunctionLength();
      }
      catch (StudioException ignored) {
      }
      String fun = ((K.Function)o).toString(true);
      fun = fun.trim().replaceAll(" ", "").replaceAll("\n", "");
      fun = fun.substring(0, fun.length() > len ? len : fun.length()) +
          (fun.length() > len ? "..." : "");
      return new K.Function(new K.KCharacterArray(fun));
    }
    return o;
  }

  public String getColumnName(int i) {
    return table.getKeys().get(i).toString(false);
  }

  public Class<?> getColumnClass(int col) {
    return table.getValues().get(col).getClass();
  }

  @Override
  public K.KBaseArray getColumn(int col) {
    return (K.KBaseArray)table.getValues().get(col);
  }
};