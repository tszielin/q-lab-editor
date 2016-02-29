package studio.kdb;

import com.kx.kdb.*;

public class KListTableModel extends KTableModel {
  private static final long serialVersionUID = 6336742729758993145L;
  private K.KList list;

  public void setData(K.KList obj) {
    list = obj;
  }
  
  public KListTableModel() {
    this(null);
  }
  
  public KListTableModel(K.KList obj) {
    setData(obj);
  }

  public void upsert(K.KList upd) {
    setData(upd);
//     dict.upsert(upd);
    if (isSortedAsc()) {
      asc(sortedByColumn);
    }
    else
      if (isSortedDesc()) {
        desc(sortedByColumn);
      }
  }

  public boolean isKey(int column) {
    return false;
  }

  private void sort(int col, boolean desc) {
    if (list.getArray() != null) {
      sortIndex = null;
      sortedByColumn = col;
      
      sortIndex = desc ? list.gradeDown() : list.gradeUp();
      sorted = desc ? -1 : 1;
    }
  }

  public int getColumnCount() {
    return list != null ? 1 : 0;
  }

  public int getRowCount() {
    return list != null ? list.getLength() : 0;
  }

  public K.KType<?> getValueAt(int row, int col) {
    row = (sortIndex == null) ? row : sortIndex[row];
    Object array = list != null ? list.getArray() : null;
    if (array instanceof K.KType[]) {
      return ((K.KType[])array)[row];
    }
    return null;
  }

  public String getColumnName(int col) {
    return "values";
  }

  public Class<?> getColumnClass(int col) {
    return getColumn(col) == null ? null : getColumn(col).getClass();
  }

  public K.KBaseArray getColumn(int col) {
    return list == null ? null : null;
  }

  @Override
  public void asc(int col) {
    sort(col, false);
    
  }

  @Override
  public void desc(int col) {
    sort(col, true);    
  }
}