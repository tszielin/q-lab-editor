package tszielin.qlab.util.component.button;

import java.awt.event.MouseAdapter;

import javax.swing.Action;
import javax.swing.JButton;

import tszielin.qlab.util.listener.DefaultActionHintListener;

/**
 * Button with action
 *
 * @author <b><code>Thomas
 *   Zielinski</code></b><small>(thomas.zielinski@nagler-company.com)</small><br><i><code>Nagler
 *   & Company GmbH<code></i><br><small>2006</small>
 * @version 1.0
 */

abstract public class ActionButton extends JButton {
  private static final long serialVersionUID = -6292765621709674584L;

  protected ActionButton() {
    super();
  }
  /**
   * Contruct button component. Creates a button where properties are taken from the Action supplied
   * @param action Action used to specify the new button
   * @param adapter MouseAdapter listenet for mouse events
   */
  public ActionButton(Action action, MouseAdapter adapter) {
    super(action);
    addMouseListener(adapter != null ? adapter : new DefaultActionHintListener(action));
  }

  public ActionButton(Action action) {
    this(action, null);
  }
}
