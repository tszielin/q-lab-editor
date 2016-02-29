package tszielin.qlab.util.action;

import javax.swing.*;

public abstract class ActionBase extends AbstractAction {
  private static final long serialVersionUID = 3802695059469165555L;
  /**
   * empty object
   */
  public static final ActionBase EMPTY_ARRAY[] = new ActionBase[0];

  /**
   * Constructor
   * @param caption String action caption
   * @param mnemonic char action quick letter (shotcut)
   * @param icon Icon image
   * @param key KeyStroke quick shortcut
   * @param toolTip String tool tip
   * @param hint String hint message
   */
  public ActionBase(String caption, char mnemonic, Icon icon, KeyStroke key,
                    String toolTip, String hint) {
    this(caption, mnemonic, icon, key);
    this.setToolTip(toolTip);
    this.setHint(hint);
  }

  /**
   * Constructor
   * @param caption String action caption
   * @param mnemonic char action quick letter (shotcut)
   * @param icon Icon image
   * @param key KeyStroke quick shortcut
   */
  public ActionBase(String caption, char mnemonic, Icon icon, KeyStroke key) {
    this(caption, mnemonic, icon);
    this.setAccelerator(key);
  }

  /**
   * Constructor
   * @param caption String action caption
   * @param mnemonic char action quick letter (shotcut)
   * @param icon Icon image
   */
  public ActionBase(String caption, char mnemonic, Icon icon) {
    this(caption, mnemonic);
    this.setSmallIcon(icon);
  }

  /**
   * Constructor
   * @param caption String action caption
   * @param mnemonic char action quick letter (shotcut)
   */
  public ActionBase(String caption, char mnemonic) {
    this(caption);
    this.setMnemonic(mnemonic);
  }

  /**
   *Constructor
   * @param caption String action caption
   */
  public ActionBase(String caption) {
    super(caption);
  }

  /**
   * Empty constructor
   */
  public ActionBase() {
    super("");
  }

  /**
   * Get action caption (name)
   * @return String caption
   */
  public String getName() {
    Object obj = super.getValue("Name");
    if (obj instanceof String) {
      return (String)obj;
    }
    return null;
  }

  /**
   * Set caption
   * @param caption String new caption value
   */
  public void setName(String caption) {
    super.putValue("Name", caption);
  }

  /**
   * Get actions icon
   * @return Icon small icon image
   */
  public Icon getSmallIcon() {
    Object obj = getValue("SmallIcon");
    if (obj instanceof Icon) {
      return (Icon)obj;
    }
    return null;
  }

  /**
   * Set icon for action
   * @param icon Icon small icon image.
   */
  public void setSmallIcon(Icon icon) {
    putValue("SmallIcon", icon);
  }

  /**
   * Get actions hint text
   * @return hint Strinf hint text to be displayed over the object
   */
  public String getHint() {
    Object obj = super.getValue("LongDescription");
    if (obj instanceof String) {
      return (String)obj;
    }
    return null;
  }

  /**
   * Set hint text fro action
   * @param hintText text to be displayed over object
   */
  public void setHint(String hintText) {
    super.putValue("LongDescription", hintText);
  }

  /**
   * Get actions mnemonic key
   * @return int mnemonic key numger or '\0' when key is not set
   */
  public int getMnemonic() {
    Object obj = super.getValue("MnemonicKey");
    if (obj instanceof Integer) {
      return ((Integer)obj).intValue();
    }
    return '\0';
  }

  /**
   * Set mnemonic key for action
   * @param mnemonic character that should be used as mnemonic
   */
  public void setMnemonic(char mnemonic) {
    super.putValue("MnemonicKey", new Integer(mnemonic));
  }

  /**
   * Get actions tooltip text
   * @return String text being displayed as a tooltip
   */
  public String getToolTip() {
    Object obj = super.getValue("ShortDescription");
    if (obj instanceof String) {
      return (String)obj;
    }
    return null;
  }

  /**
   * Set tooltip text for action
   * @param toolTipText String text to be displayed as tool tip
   */
  public void setToolTip(String toolTipText) {
    super.putValue("ShortDescription", toolTipText);
  }

  /**
   * Get actions key stroke
   * @return KeyStroke key stroke connected with this object
   */
  public KeyStroke getAccelerator() {
    Object obj = super.getValue("AcceleratorKey");
    if (obj instanceof KeyStroke) {
      return (javax.swing.KeyStroke)obj;
    }
    return null;
  }

  /**
   * Set key stroke combination for action
   * @param key KeyStroke to be used with this object
   */
  public void setAccelerator(KeyStroke key) {
    super.putValue("AcceleratorKey", key);
  }
}
