package tszielin.qlab.util.action.popup;

import javax.swing.Icon;
import javax.swing.KeyStroke;

import tszielin.qlab.util.action.ActionBase;

/**
 * Popup action
 * @author <b><code>Thomas
 *   Zielinski</code></b><small>(thomas.zielinski@nagler-company.com)</small><br><i><code>Nagler
 *   & Company GmbH<code></i><br><small>2006</small>
 * @version 1.0
 */
public abstract class ActionPopup extends ActionBase {
  private static final long serialVersionUID = 465040660100632826L;
  private Object source;

  /**
   * Constructor
   * @param source Object source (owner)
   * @param name String popup caption
   * @param mnemonic char shortcut
   * @param icon Icon icon
   * @param key KeyStroke key stroke
   * @param toolTip String tooltip text
   * @param hint String hint text
   */
  public ActionPopup(Object source, String name, char mnemonic, Icon icon,
                     KeyStroke key, String toolTip, String hint) {
    super(name, mnemonic, icon, key, toolTip, hint);
    this.source = source;
  }

  /**
   * Get source
   * @return Object source object (component)
   */
  public Object getSource() {
    return source;
  }
}
