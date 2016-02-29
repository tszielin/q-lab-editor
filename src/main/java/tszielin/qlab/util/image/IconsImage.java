package tszielin.qlab.util.image;

import java.awt.*;

import javax.swing.Icon;

public class IconsImage implements Icon {
  private int offset;
  private int index;
  private int size;
  private Image image;

  /**
   * Constructor
   * @param image Image image resource
   * @param i int size (width)
   * @param j int position in resource
   */
  public IconsImage(Image image, int i, int j) {
    this.image = image;
    size = i;
    index = j;
    offset = i * j;
  }

  /**
   * Paint image
   * @param c Component component
   * @param g Graphics graphics context
   * @param x int width
   * @param y int heigth
   */
  public void paintIcon(Component c, Graphics g, int x, int y) {
    if (image != null) {
      g.drawImage(image, x, y, x + size, y + size, offset, 0, offset + size,
                  size, c);
    }
  }

  /**
   * Icon width
   * @return int width
   */
  public int getIconWidth() {
    return size;
  }

  /**
   * Icon height
   * @return int height
   */
  public int getIconHeight() {
    return size;
  }

  /**
   * Offset
   * @return int offset in resource
   */
  public int getOffset() {
    return offset;
  }

  /**
   * Position
   * @return int position
   */
  public int getIndex() {
    return index;
  }
}
