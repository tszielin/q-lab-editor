/*
 * Studio for kdb+ by Charles Skelton is licensed under a Creative Commons
 * Attribution-Noncommercial-Share Alike 3.0 Germany License
 * http://creativecommons.org/licenses/by-nc-sa/3.0 except for the netbeans components which retain
 * their original copyright notice
 */

package studio.kdb;

import java.awt.Component;
import java.awt.Font;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;

import com.kx.kdb.K;

import tszielin.qlab.util.image.IconsItem;

public class TableHeaderRenderer extends DefaultTableCellRenderer {
  private static final long serialVersionUID = -7260164520528829537L;

  public TableHeaderRenderer() {
    super();
    setHorizontalAlignment(SwingConstants.LEFT);
    setVerticalAlignment(SwingConstants.CENTER);
    setOpaque(true);
    setBorder(UIManager.getBorder("TableHeader.cellBorder"));
    setFont(UIManager.getFont("TableHeader.font"));
    setBackground(UIManager.getColor("TableHeader.background"));
    setForeground(UIManager.getColor("TableHeader.foreground"));
  }

  public void setFont(Font font) {
    super.setFont(font);
    invalidate();
  }

  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
      boolean hasFocus, int row, int column) {

    if (table.getModel() instanceof KTableModel) {
      setIcon(column == ((KTableModel)table.getModel()).getSortByColumn() && 
          ((KTableModel)table.getModel()).getColumn(table.convertColumnIndexToModel(column)) instanceof K.KBaseArray ? 
          ((KTableModel)table.getModel()).isSortedAsc() ? IconsItem.ICON_ASC : IconsItem.ICON_DESC : 
            IconsItem.ICON_BLANK);
    }
    setText(value != null ? value.toString() : "");
    return this;
  }
}