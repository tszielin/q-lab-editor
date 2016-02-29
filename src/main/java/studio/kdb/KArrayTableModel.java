package studio.kdb;

import com.kx.kdb.K;

public class KArrayTableModel extends KTableModel {
  private static final long serialVersionUID = 6847211408368716994L;
  private K.KBaseArray array;

  public void setData(K.KBaseArray obj) {
    array = obj;
  }

  public KArrayTableModel() {
    this(null);
  }

  public KArrayTableModel(K.KBaseArray obj) {
    setData(obj);
  }

  public void upsert(K.KList upd) {
    setData(upd);
    // dict.upsert(upd);
    if (isSortedAsc()) {
      asc(sortedByColumn);
    }
    else if (isSortedDesc()) {
      desc(sortedByColumn);
    }
  }

  public boolean isKey(int column) {
    return false;
  }

  private void sort(int col, boolean desc) {
    if (array.getArray() != null) {
      sortIndex = null;
      sortedByColumn = col;

      sortIndex = desc ? array.gradeDown() : array.gradeUp();
      sorted = desc ? -1 : 1;
    }
  }

  public int getColumnCount() {
    return array != null ? 1 : 0;
  }

  public int getRowCount() {
    return array != null ? array.getLength() : 0;
  }

  public K.KType<?> getValueAt(int row, int col) {
    row = (sortIndex == null) ? row : sortIndex[row];
    return array == null ? null : array.get(row);
  }

  public String getColumnName(int col) {
    return "values";
  }

  public Class<?> getColumnClass(int col) {
    return getColumn(col) == null ? null : getColumn(col).getClass();
  }

  public K.KBaseArray getColumn(int col) {
    return array == null ? null : null;
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