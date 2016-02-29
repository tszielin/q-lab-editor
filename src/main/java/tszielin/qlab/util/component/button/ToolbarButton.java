package tszielin.qlab.util.component.button;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.image.BufferedImage;

import javax.swing.*;

public class ToolbarButton extends ActionButton {
  private static final long serialVersionUID = 4276638785158492237L;

  protected ToolbarButton() {
    super();
  }
  
  public ToolbarButton(Action action) {
    this(action, null);
  }

  public ToolbarButton(Action action, MouseAdapter adapter) {
    super(action, adapter);
    setBorderPainted(false);    
    setText(null);
    setMargin(new Insets(2,2,2,2));
  }
  
  protected ImageIcon getImageIcon(Icon icon) {
    if (icon == null) {
      return null;
    }
    if (icon instanceof ImageIcon) {
      return (ImageIcon)icon;
    }
    BufferedImage img = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB); // you can change the type as needed
    Graphics2D g = img.createGraphics();
    icon.paintIcon(new JPanel(), g, 0,0);
    return new ImageIcon(img);
  }
  
  @Override
  public void setAction(Action action) {
    super.setAction(action);
    setIcon(getImageIcon(getIcon()));
  }
}
