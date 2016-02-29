package tszielin.qlab.config.listener;

import java.awt.*;
import java.awt.event.*;
import java.util.Map;

import javax.swing.*;

public class TreeTokensMouseAdapter extends MouseAdapter {
  private JList<String> list;
  private Component component;  
  private Map<String, Color> map;
  
  public TreeTokensMouseAdapter(Component component, JList<String> list, Map<String, Color> map) {
    this.list = list;
    this.map = map;
    this.component = component;
  }

  public void mouseClicked(MouseEvent event) {
    if (event.getClickCount() == 2) {
      int index = list.locationToIndex(event.getPoint());
      ListModel<String> dlm = list.getModel();
      Object item = dlm.getElementAt(index);
      list.ensureIndexIsVisible(index);      
      if (item instanceof String) {
        Color color = JColorChooser.showDialog(component, "Select token's color", map.get((String)item));
        if (color != null) {
          map.put((String)item, color);
          if ("BACKGROUND".equals(item)) {
            list.setBackground(map.get((String)item));
          }
          list.repaint();
        }          
      }
    }
  }
}
