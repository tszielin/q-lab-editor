package tszielin.qlab.util.listener;

import java.awt.event.*;

import javax.swing.JPopupMenu;

public class PopupListener extends MouseAdapter {
  /**
   * Popup owner
   */
  protected JPopupMenu popupMenu;

  /**
   * Constructor
   * @param source Object component owner
   */
  public PopupListener(JPopupMenu popupMenu) {
    this.popupMenu = popupMenu;
  }

  /**
   * Invoked when a mouse button has been pressed on a component
   * @param e MouseEvent an event which indicates that a mouse action occurred in a component
   */
  @Override
  public void mousePressed(MouseEvent e) {
    showPopup(e);
  }
  
  @Override
  public void mouseClicked(MouseEvent e) {
    showPopup(e);
  }

  /**
   * Invoked when a mouse button has been released on a component
   * @param e MouseEvent an event which indicates that a mouse action occurred in a component
   */
  @Override
  public void mouseReleased(MouseEvent e) {
    showPopup(e);
  }

  /**
   * Is popup trigger marked
   * @param e MouseEvent an event which indicates that a mouse action occurred in a component
   */
  protected void showPopup(MouseEvent e) {
    if (e.isPopupTrigger()) {
      if (!popupMenu.isVisible()) {
        popupMenu.show(e.getComponent(), e.getX(), e.getY());
      }
    }
  }

  /**
   * Get component owner
   * @return Object owner
   */
  public JPopupMenu getPopupMenu() {
    return popupMenu;
  }
}
