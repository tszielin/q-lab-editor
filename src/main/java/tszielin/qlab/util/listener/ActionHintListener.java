package tszielin.qlab.util.listener;

import java.awt.event.MouseAdapter;

import javax.swing.Action;


/**
 * Mouse listener for action buttons (for displaying tool tips in status bar)
 */
public abstract class ActionHintListener extends MouseAdapter {
  protected String hint;

  public ActionHintListener(String hint) {
    setHint(hint);
  }

  /**
   * Constructor
   * @param action Action defined action for button
   */
  public ActionHintListener(Action action) {
    this(action != null && action.getValue("LongDescription") instanceof String &&
        !((String)action.getValue("LongDescription")).trim().isEmpty() ? 
            (String)action.getValue("LongDescription") : "");
  }

  public void setAction(Action action) {
    this.hint = action != null && action.getValue("LongDescription") instanceof String &&
      !((String)action.getValue("LongDescription")).trim().isEmpty() ? 
        (String)action.getValue("LongDescription") : "";
  }
  
  public void setHint(String hint) {
    this.hint = hint != null && !hint.trim().isEmpty() ? hint : "";
  }
}
