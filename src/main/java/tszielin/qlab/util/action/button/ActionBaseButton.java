package tszielin.qlab.util.action.button;

import java.awt.Container;
import java.awt.event.ActionEvent;

import javax.swing.Icon;

import tszielin.qlab.util.action.ActionBase;

/**
 * Action
 *
 * @author <b><code>Thomas
 *   Zielinski</code></b><small>(thomas.zielinski@nagler-company.com)</small><br><i><code>Nagler
 *   & Company GmbH<code></i><br><small>2006</small>
 * @version 1.0
 */

abstract public class ActionBaseButton extends ActionBase {
  private static final long serialVersionUID = -576798796872223848L;
  private final Container parent;
  private Object obj;

  /**
   * Constructor
   * @param parent Container owner
   * @param obj Object transfer object (additional information - tag)
   * @param caption String caption
   * @param mnemonic char mnemonic char (shortcut letter)
   * @param icon Icon icon
   * @param toolTip String tool tip text
   * @param hint String hint text
   */
  public ActionBaseButton(Container parent, Object obj, String caption,
                          char mnemonic, Icon icon, String toolTip, String hint) {
    super(caption, mnemonic, icon, null, toolTip, hint);
    this.parent = parent;
    this.obj = obj;
  }

  /**
   * Constructor
   * @param parent Container owner
   * @param obj Object transfer object (additional information - tag)
   * @param caption String caption
   * @param mnemonic char mnemonic char (shortcut letter)
   * @param icon Icon icon
   * @param toolTip String tool tip text (the same for hint text)
   */
  public ActionBaseButton(Container parent, Object obj, String caption,
                          char mnemonic, Icon icon, String toolTip) {
    this(parent, obj, caption, mnemonic, icon, toolTip, toolTip);
  }


  /**
   * Constructor
   * @param parent Container owner
   * @param obj Object transfer object (additional information - tag)
   * @param caption String caption
   * @param mnemonic char mnemonic char (shortcut letter)
   * @param toolTip String tool tip text (the same for hint text)
   */
  public ActionBaseButton(Container parent, Object obj, String caption,
                          char mnemonic, String toolTip) {
    this(parent, obj, caption, mnemonic, null, toolTip);
  }

  /**
   * Constructor
   * @param parent Container owner
   * @param obj Object transfer object (additional information - tag)
   * @param caption String caption
   * @param toolTip String tool tip text (the same for hint text)
   */
  public ActionBaseButton(Container parent, Object obj, String caption,
                          String toolTip) {
    this(parent, obj, caption, (char)0, toolTip);
  }

  /**
   * Constructor
   * @param parent Container owner
   * @param obj Object transfer object (additional information - tag)
   * @param caption String caption
   * @param toolTip String tool tip text (the same for hint text)
   */
  public ActionBaseButton(Object obj, String caption, String toolTip) {
    this(null, obj, caption, toolTip);
  }

  /**
   * Constructor
   * @param caption String caption
   * @param toolTip String tool tip text (the same for hint text)
   */
  public ActionBaseButton(String caption, String toolTip) {
    this(null, caption, toolTip);
  }

  /**
   * Invoked when an action occurs
   * @param e ActionEvent a semantic event which indicates that a component-defined action occured
   */
  abstract public void actionPerformed(ActionEvent e);

  /**
   * Get additional information (tag)
   * @return Object tag object
   */
  public Object getObject() {
    return obj;
  }

  /**
   * Set additiona information
   * @param obj Object tah object
   */
  protected void setObject(Object obj) {
    this.obj = obj;
  }

  /**
   * Get parent
   * @return Container parent
   */
  public Container getParent() {
    return parent;
  }
}
