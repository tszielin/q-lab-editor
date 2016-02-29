package tszielin.qlab.util.component.button;

import java.awt.*;

import javax.swing.JButton;
import javax.swing.SwingConstants;

class ArrowButton extends JButton implements SwingConstants {
  private static final long serialVersionUID = -25739164598123190L;

  public ArrowButton() {
    super();
    setRequestFocusEnabled(false);
  }

  public void paint(Graphics g) {
    super.paint(g);
    int w = getSize().width;
    int h = getSize().height;

    int size = Math.min((h - 4) / 3, (w - 4) / 3);
    size = Math.max(size, 2);
    paintTriangle(g, (w - size) / 2, (h - size) / 2, size);
    revalidate();
  }

  /**
   * Returns the preferred size of the {@code BasicArrowButton}.
   * 
   * @return the preferred size
   */
  public Dimension getPreferredSize() {
    return new Dimension(16, 16);
  }

  /**
   * Returns the minimum size of the {@code BasicArrowButton}.
   * 
   * @return the minimum size
   */
  public Dimension getMinimumSize() {
    return new Dimension(5, 5);
  }

  /**
   * Returns the maximum size of the {@code BasicArrowButton}.
   * 
   * @return the maximum size
   */
  public Dimension getMaximumSize() {
    return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
  }

  /**
   * Returns whether the arrow button should get the focus. {@code BasicArrowButton}s are used as a
   * child component of composite components such as {@code JScrollBar} and {@code JComboBox}. Since
   * the composite component typically gets the focus, this method is overriden to return {@code
   * false}.
   * 
   * @return {@code false}
   */
  public boolean isFocusTraversable() {
    return false;
  }

  public void paintTriangle(Graphics g, int x, int y, int size) {
    Color oldColor = g.getColor();
    int mid, i;

    size = Math.max(size, 2);
    mid = (size / 2) - 1;

    g.translate(x, y);

    int j = 0;
    for(i = size-1; i >= 0; i--)   {
        g.drawLine(mid-i, j, mid+i, j);
        j++;
    }
    g.translate(-x, -y);
    g.setColor(oldColor);
  }
}
