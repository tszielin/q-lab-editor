package tszielin.qlab.renderer;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class WindowsListCellRender extends DefaultTableCellRenderer {
  private static final long serialVersionUID = -7995108982557225081L;

  @Override
  public Component getTableCellRendererComponent(JTable table, Object value,
      boolean isSelected, boolean hasFocus, int row, int column) {
    JLabel label = (JLabel)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    if (value instanceof JLabel) {
      label.setText(((JLabel)value).getText());
      label.setIcon(((JLabel)value).getIcon());
    }
    return label;
  }
}
