package tszielin.qlab.listener;

import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import tszielin.qlab.action.editor.ReleaseConnectionAction;
import tszielin.qlab.util.component.menu.ActionMenuItem;

public class EditorPopupListener implements PopupMenuListener {
  public EditorPopupListener() {
  }

  public void popupMenuCanceled(PopupMenuEvent e) {
  }

  public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
    if (e == null || !(e.getSource() instanceof JPopupMenu)) {
      return;
    }
    JPopupMenu popupMenu = (JPopupMenu)e.getSource();
    if (popupMenu.getComponentCount() > 0) {
      for (int count = 0; count < popupMenu.getComponentCount(); count++) {
        ActionMenuItem pmi = (ActionMenuItem)popupMenu.getComponent(count);        
        if (pmi.getMouseListeners() != null) {
          boolean done = false;
          for (int item = 0; item < pmi.getMouseListeners().length; item++) {
            if (pmi.getMouseListeners()[item] instanceof ActionHintsListener) {
              ((ActionHintsListener)pmi.getMouseListeners()[item]).mouseExited(null);
              done = true;
              break;
            }
          }
          if (done) {
            break;
          }
        }
      }
    }
  }

  public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
    if (e == null || !(e.getSource() instanceof JPopupMenu)) {
      return;
    }
    JPopupMenu popupMenu = (JPopupMenu)e.getSource();
    if (popupMenu.getComponentCount() > 0) {
      for (int count = 0; count < popupMenu.getComponentCount(); count++) {
        if (popupMenu.getComponent(count) instanceof ActionMenuItem) {
          ActionMenuItem pmi = (ActionMenuItem)popupMenu.getComponent(count);
          if (pmi.getAction() instanceof ReleaseConnectionAction) {
            ReleaseConnectionAction action = (ReleaseConnectionAction)pmi.getAction();
            action.setEnabled(action.getEditor() != null &&
                action.getEditor().getConnection() != null);
          }
        }
      }
    }
  }
}
