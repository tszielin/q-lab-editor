package tszielin.qlab.config.renderer;

import java.awt.*;
import java.util.Map;

import javax.swing.*;

public class TreeTokensRenderer extends DefaultListCellRenderer {
  private static final long serialVersionUID = -1025798965271642871L;

  private Map<String, Color> map;

  public TreeTokensRenderer(Map<String, Color> map) {
    this.map = map;
  }
  
  public Component getListCellRendererComponent(JList<?> list, Object value, int index,
      boolean isSelected, boolean cellHasFocus) {

    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
    if (value instanceof String) {
      setToolTipText("Token: " + (String)value);
      if (map != null) {
        setForeground(map.get((String)value) == null ? Color.black : map.get((String)value));
        setBackground(map.get("BACKGROUND"));
      }
    }
    return this;
  }

}
