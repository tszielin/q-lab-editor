package tszielin.qlab.util.image;

import java.awt.*;
import java.awt.image.ImageObserver;

import javax.swing.*;
import javax.swing.plaf.UIResource;

public class IconsList {
  private IconsList() {
  }

  private static class IconsImage implements Icon {
    private int e;
    private int c;
    private int a;
    private int b;
    private Image d;

    public IconsImage(Image image, int i, int j, int k, int l) {
      d = image;
      b = i;
      a = j;
      c = k;
      e = l;
    }
    
    public int getIconHeight() {
      return e;
    }

    public int getIconWidth() {
      return c;
    }
    
    public void paintIcon(Component component, Graphics g, int i, int j) {
      g.drawImage(d, i, j, i + c, j + e, b, a, b + c, a + e, null);    
    }
  }

  public static class IconFactory implements UIResource{
    private int c;
    private int a;
    private Image b;

    public IconFactory(Image image, int i, int j) {
      b = image;
      a = i;
      c = j;
    }

    public IconFactory(Image image, int i) {
      this(image, i, i);
    }

    public Icon getIcon(int i, int j) {
      return new IconsImage(b, i * a, j * c, a, c);
    }
    
    public Icon getIcon(int i) {
      return getIcon(i, 0);
    }       
  }

  public static IconFactory getIconFactory(Image image, int i, int j) {
    return new IconFactory(image, i, j);
  }

  public static IconFactory getIconFactory(Image image, int i) {
    return new IconFactory(image, i, i);
  }

  public static Icon getIcon(Image image, int i, int j, int k, int l) {
    return new IconsImage(image, i, j, k, l);
  }

  public static Icon getIcon(Image image) {
    return new IconsImage(image, 0, 0, image.getWidth(null), image.getHeight(null));
  }
  
  public static Icon getIcon(Image image, ImageObserver observer) {
    return new IconsImage(image, 0, 0, image.getWidth(observer), image.getHeight(observer));
  }

  public static Icon getBlankIcon(int i, int j) {
    return new IconBlank(i, j);
  }

  public static Icon getBlankIcon() {
    return new IconBlank();
  }

  static class IconBlank implements Icon, UIResource {
    private int width;
    private int height;

    IconBlank(int width, int height) {
      this.width = width;
      this.height = height;
    }

    IconBlank() {
      this.width = 16;
      this.height = 16;
    }

    public void paintIcon(Component component, Graphics g, int i, int j) {
    }

    public int getIconHeight() {
      return height;
    }

    public int getIconWidth() {
      return width;
    }
  }
}