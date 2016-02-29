package tszielin.qlab.component.pane;

import java.awt.*;

import javax.swing.*;

import tszielin.qlab.util.image.IconsItem;

class CloseTabIcon implements Icon {
  private Icon closeIcon;  
  
  private int xPos;
  private int yPos;

  public CloseTabIcon() {
    this.closeIcon = IconsItem.ICON_CLOSE_APP_DISABLE;
  }

  public int getIconHeight() {
    return 16;
  }

  public int getIconWidth() {
    return 16;
  }

  public void paintIcon(Component component, Graphics graphics, int x, int y) {    
    this.xPos = x;
    this.yPos = y;

    if (closeIcon != null) {
      closeIcon.paintIcon(component, graphics, xPos, yPos);
    }
  }

  public Rectangle getBounds() {
    return new Rectangle(xPos, yPos, 
        closeIcon != null ? closeIcon.getIconWidth() : 0, 
            closeIcon != null ? closeIcon.getIconHeight() : 0);
  }
  
  public void setCanClose(boolean canClose) {
    closeIcon = canClose ?
        closeIcon != null ? IconsItem.ICON_CLOSE_APP_DISABLE : null :
          closeIcon != null ? IconsItem.ICON_BLANK : null;
  }
  
  public boolean canClose() {
    return !(closeIcon == null || closeIcon == IconsItem.ICON_BLANK); 
  }
  
  public void setCloseIcon(Icon closeIcon) {
    this.closeIcon = closeIcon;
  }
  
  public Icon getCloseIcon() {
    return this.closeIcon;
  }
}
