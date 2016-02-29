package tszielin.qlab.config.listener;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class EditorBackgroundMouseAdapter extends MouseAdapter {
  private Component component;  
  
  public EditorBackgroundMouseAdapter(Component component) {
    this.component = component;
  }

  public void mouseClicked(MouseEvent event) {
    if (event.getButton() == MouseEvent.BUTTON1 && event.getClickCount() == 2) {
      if (event.getComponent() instanceof JLabel) {
        Color color = JColorChooser.showDialog(component, "Select background color", 
            event.getComponent().getBackground());
        if (color != null) {
          event.getComponent().setBackground(color);
        }
      }
    }
  }
}
