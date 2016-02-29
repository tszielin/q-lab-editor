/*
 * Studio for kdb+ by Charles Skelton is licensed under a Creative Commons
 * Attribution-Noncommercial-Share Alike 3.0 Germany License
 * http://creativecommons.org/licenses/by-nc-sa/3.0 except for the netbeans components which retain
 * their original copyright notice
 */

package studio.kdb;

import com.kx.kdb.K;

public class KDictionaryTableModel extends KTableModel {
  private static final long serialVersionUID = 8253574872651143503L;
  private K.KDictionary dict;

  public void setData(K.KDictionary obj) {
    dict = obj;
  }
  
  public KDictionaryTableModel() {
    this(null);
  }
  
  public KDictionaryTableModel(K.KDictionary obj) {
    setData(obj);
  }

  public void upsert(K.KDictionary upd) {
    setData(upd);
    // dict.upsert(upd);
    if (isSortedAsc()) {
      asc(sortedByColumn);
    }
    else
      if (isSortedDesc()) {
        desc(sortedByColumn);
      }
  }

  public boolean isKey(int column) {
    return dict.getKeys() instanceof K.KTable ?
        column < ((K.KTable)dict.getKeys()).getKeys().getLength() :
          dict.getKeys() instanceof K.KBaseArray ?
              column < 1 : false;
  }

  private void sort(int col, boolean desc) {
    sortIndex = null;
    sortedByColumn = col;

    if (dict.getKeys() instanceof K.KTable) {
      K.KTable f = (K.KTable)dict.getKeys();
      K.KBaseArray v = null;

      if (col >= f.getKeys().getLength()) {
        col -= f.getKeys().getLength();
        f = (K.KTable)dict.getValues();
      }
      v = (K.KBaseArray)f.getValues().get(col);
      sortIndex = desc ? v.gradeDown() : v.gradeUp();
    }
    else {
      if (dict.getKeys() instanceof K.KBaseArray && dict.getValues() instanceof K.KBaseArray) {
        sortIndex = col == 0 ? desc ? ((K.KBaseArray)dict.getKeys()).gradeDown() : ((K.KBaseArray)dict.getKeys()).gradeUp() :
          desc ? ((K.KBaseArray)dict.getValues()).gradeDown() : ((K.KBaseArray)dict.getValues()).gradeUp(); 
      }
      else {
        sortIndex = new int[0];
      }
    }
    sorted = desc ? -1 : 1;
  }

  public int getColumnCount() {
    return dict.getKeys() instanceof K.KTable ?
        ((K.KTable)dict.getKeys()).getKeys().getLength() + ((K.KTable)dict.getValues()).getKeys().getLength() :
          dict.getKeys() instanceof K.KBaseArray ? 2 : 0;             
  }

  public int getRowCount() {
    return dict.getKeys() instanceof K.KTable ?
        ((K.KBaseArray)((K.KTable)dict.getKeys()).getValues().get(0)).getLength() :
          dict.getValues() instanceof K.KBaseArray ? ((K.KBaseArray)dict.getValues()).getLength() : 0;
  }

  public Object getValueAt(int row, int col) {
    row = (sortIndex == null) ? row : sortIndex[row];
    
    if (dict.getKeys() instanceof K.KTable) {
      K.KTable f = (K.KTable)dict.getKeys();
      K.KBaseArray v = null;

      if (col >= f.getKeys().getLength()) {
        col -= f.getKeys().getLength();
        f = (K.KTable)dict.getValues();
      }

      v = (K.KBaseArray)f.getValues().get(col);
      return v.get(row);
    }
    else {
      if (dict.getKeys() instanceof K.KBaseArray && dict.getValues() instanceof K.KBaseArray) {
        return col == 0 ? ((K.KBaseArray)dict.getKeys()).get(row) : 
          ((K.KBaseArray)dict.getValues()).get(row);
      }
      else {
        return null;
      }
    }
  }

  public String getColumnName(int col) {
    if (dict.getKeys() instanceof K.KTable) {
      K.KSymbolArray v = ((K.KTable)dict.getKeys()).getKeys();

      if (col >= ((K.KTable)dict.getKeys()).getKeys().getLength()) {
        col -= ((K.KTable)dict.getKeys()).getKeys().getLength();
        v = ((K.KTable)dict.getValues()).getKeys();
      }
      return v.get(col).toString(false);
    }
    else {
      if (dict.getKeys() instanceof K.KBaseArray && dict.getValues() instanceof K.KBaseArray) {
        return col == 0 ? "key" : "value";
      }
      else {
        return "undefined";
      }
    }
  }

  public Class<?> getColumnClass(int col) {
    return getColumn(col).getClass();
  }

  public K.KBaseArray getColumn(int col) {
    if (dict.getKeys() instanceof K.KTable) {
      K.KTable f = (K.KTable)dict.getKeys();

      if (col >= f.getKeys().getLength()) {
        col -= f.getKeys().getLength();
        f = (K.KTable)dict.getValues();
      }

      return (K.KBaseArray)f.getValues().get(col);
    }
    else {
      if (dict.getKeys() instanceof K.KBaseArray) {
        return col == 0 ? (K.KBaseArray)dict.getKeys() : (K.KBaseArray)dict.getValues(); 
      }
      else {
        return null;
      }
    }
  }

  @Override
  public void asc(int col) {
    sort(col, false);
    
  }

  @Override
  public void desc(int col) {
    sort(col, true);    
  }
};