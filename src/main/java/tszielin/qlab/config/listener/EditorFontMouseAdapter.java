package tszielin.qlab.config.listener;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import net.mariottini.swing.JFontChooser;

public class EditorFontMouseAdapter extends MouseAdapter {
  private Component component;  
  
  public EditorFontMouseAdapter(Component component) {
    this.component = component;
  }

  public void mouseClicked(MouseEvent event) {
    if (event.getButton() == MouseEvent.BUTTON1 && event.getClickCount() == 2) {
      if (event.getComponent() instanceof JLabel) {
        JFontChooser chooser = new JFontChooser();
        chooser.setSampleText("Editor font");
        chooser.setSelectedFont(event.getComponent().getFont());
        if (chooser.showDialog(component) == JFontChooser.APPROVE_OPTION) {
          Font font = chooser.getSelectedFont();
          event.getComponent().setFont(new Font(font.getFontName(), Font.PLAIN, font.getSize()));
        }
        chooser = null;
      }
    }
  }
}
