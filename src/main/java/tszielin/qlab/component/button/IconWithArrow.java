package tszielin.qlab.component.button;

import java.awt.*;

import javax.swing.Icon;
import javax.swing.UIManager;

import tszielin.qlab.util.image.IconsItem;

class IconWithArrow implements Icon {
  private Icon orig;
  private Icon arrow = IconsItem.ICON_DOWN;
  private boolean paintRollOver;

  private static final int GAP = 6;

  /** Creates a new instance of IconWithArrow */
  public IconWithArrow(Icon orig, boolean paintRollOver) {
    this.orig = orig;
    this.paintRollOver = paintRollOver;
  }

  public void paintIcon(Component c, Graphics g, int x, int y) {
    int height = getIconHeight();
    orig.paintIcon(c, g, x, y + (height - orig.getIconHeight()) / 2);

    arrow.paintIcon(c, g, x + GAP + orig.getIconWidth(), y + (height - arrow.getIconHeight()) / 2);

    if (paintRollOver) {
      Color brighter = UIManager.getColor("controlHighlight"); // NOI18N
      Color darker = UIManager.getColor("controlShadow"); // NOI18N
      if (null == brighter || null == darker) {
        brighter = c.getBackground().brighter();
        darker = c.getBackground().darker();
      }
      if (null != brighter && null != darker) {
        g.setColor(brighter);
        g.drawLine(x + orig.getIconWidth() + 1, y, x + orig.getIconWidth() + 1, y + getIconHeight());
        g.setColor(darker);
        g.drawLine(x + orig.getIconWidth() + 2, y, x + orig.getIconWidth() + 2, y + getIconHeight());
      }
    }
  }

  public int getIconWidth() {
    return orig.getIconWidth() + GAP + arrow.getIconWidth();
  }

  public int getIconHeight() {
    return Math.max(orig.getIconHeight(), arrow.getIconHeight());
  }

  public static int getArrowAreaWidth() {
    return GAP / 2 + 5;
  }
}
