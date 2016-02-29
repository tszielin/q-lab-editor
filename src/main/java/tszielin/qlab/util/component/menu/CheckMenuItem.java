package tszielin.qlab.util.component.menu;

import java.awt.event.MouseAdapter;

import javax.swing.*;

import tszielin.qlab.util.listener.DefaultActionHintListener;

public class CheckMenuItem extends JCheckBoxMenuItem {
  private static final long serialVersionUID = -6713104899396373865L;

  public CheckMenuItem(Action action) {
    this(action, null);
  }
  
  public CheckMenuItem(Action action, MouseAdapter adapter) {
    super(action);
    if (((String)action.getValue(Action.NAME)).equals(UIManager.getLookAndFeel().getName())) {
      this.setSelected(true);
    }
    this.addMouseListener(adapter != null ? adapter :
        new DefaultActionHintListener(this.getAction()));
  }
}
