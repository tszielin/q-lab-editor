package tszielin.qlab.config.listener;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JTextField;

import net.mariottini.swing.JFontChooser;

public class EditorPathMouseAdapter extends MouseAdapter {
  private Component component;  
  
  public EditorPathMouseAdapter(Component component) {
    this.component = component;
  }

  public void mouseClicked(MouseEvent event) {
    if (event.getButton() == MouseEvent.BUTTON1 && event.getClickCount() == 2) {
      if (event.getComponent() instanceof JTextField) {
        JFileChooser chooser = new JFileChooser(new File(((JTextField)event.getComponent()).getText()));
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setFileHidingEnabled(false);
        if (chooser.showOpenDialog(component) == JFontChooser.APPROVE_OPTION) {
          ((JTextField)event.getComponent()).setText(chooser.getSelectedFile().getPath());
        }
        chooser = null;
      }
    }
  }
}
