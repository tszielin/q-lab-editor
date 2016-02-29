package tszielin.qlab.listener;

import javax.swing.*;
import javax.swing.event.*;

public class TabPopupListener implements PopupMenuListener {
  private final JTabbedPane pane;
  
  public TabPopupListener(JTabbedPane pane) {
    this.pane = pane;
  }

  public void popupMenuCanceled(PopupMenuEvent e) {
  }

  public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
  }

  public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
    if (e == null || !(e.getSource() instanceof JPopupMenu)) {
      return;
    }
    JPopupMenu popupMenu = (JPopupMenu)e.getSource();
    if (popupMenu.getComponentCount() > 0) {
      for (int count = 0; count < popupMenu.getComponentCount(); count++) {
        popupMenu.getComponent(count).setEnabled(pane.getTabCount() > 0);
      }
    }    
  }
}
