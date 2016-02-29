package tszielin.qlab.listener;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import com.kx.kdb.K;

import tszielin.qlab.action.grid.ChartAction;
import tszielin.qlab.action.grid.CopyResultAction;

public class GridPopupListener implements PopupMenuListener {
  private final JTable table;
  
  public GridPopupListener(JTable table) {
    this.table = table;
  }

  public void popupMenuCanceled(PopupMenuEvent event) {
  }

  public void popupMenuWillBecomeInvisible(PopupMenuEvent event) {
  }

  public void popupMenuWillBecomeVisible(PopupMenuEvent event) {
    if (event == null || !(event.getSource() instanceof JPopupMenu)) {
      return;
    }
    JPopupMenu popupMenu = (JPopupMenu)event.getSource();
    if (popupMenu.getComponentCount() > 0) {
      for (int count = 0; count < popupMenu.getComponentCount(); count++) {
        if (popupMenu.getComponent(count) instanceof JMenuItem) {
          Action action = ((JMenuItem)popupMenu.getComponent(count)).getAction();
          if (action instanceof CopyResultAction) {
            ((JMenuItem)popupMenu.getComponent(count)).setEnabled(table.getToolTipText() != null && !table.getToolTipText().trim().isEmpty());
          }
          else {
            if (action instanceof ChartAction) {
              if (table.getColumnCount() > 0) {
                Class<?> cls = table.getColumnClass(0);
                ((JMenuItem)popupMenu.getComponent(count)).setEnabled(
                    cls == K.KDateArray.class || cls == K.KTimeArray.class ||
                    cls == K.KMonthArray.class || cls == K.KMinuteArray.class ||
                    cls == K.KSecondArray.class || cls == K.KDatetimeArray.class);
              }                  
            }
          }
        }
      }
    }
  }
}
