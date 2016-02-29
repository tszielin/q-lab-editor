package tszielin.qlab.action.grid;

import java.awt.Cursor;
import java.awt.Window;
import java.awt.event.ActionEvent;

import javax.swing.*;

import studio.ui.LineChart;
import tszielin.qlab.util.image.IconsItem;

public class ChartAction extends GridAction {
  private static final long serialVersionUID = 8830308881450067510L;

  public ChartAction(JTable table) {
    super(table, "Chart", 'h', IconsItem.ICON_CHART, KeyStroke.getKeyStroke("control H"), 
        "Chart", "Result as chart");
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    Window window = SwingUtilities.windowForComponent(getTable());
    if (window != null) {
      window.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    }
    new LineChart(getTable());
  }
}
