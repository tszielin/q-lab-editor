package tszielin.qlab.util.component.menu;

import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;

import javax.swing.Action;
import javax.swing.JMenuItem;

import tszielin.qlab.util.listener.DefaultActionHintListener;

public class ActionMenuItem extends JMenuItem {
  private static final long serialVersionUID = 3618253568473518775L;


  /**
   * Constructor
   * @param action Action action
   */
  public ActionMenuItem(Action action) {
    this(action, null);
  }

  public ActionMenuItem(Action action, MouseAdapter adapter) {
    super(action);
    this.addMouseListener(adapter != null ? adapter : 
      new DefaultActionHintListener(this.getAction()));
  }


  /**
   * Invoked when an action occurs
   * @param e ActionEvent a semantic event which indicates that a component-defined action occured
   */
  public void actionPerformed(ActionEvent e) {
    if (getAction() != null) {
      getAction().actionPerformed(e);
    }
  }
}
