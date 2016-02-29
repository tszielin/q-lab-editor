package tszielin.qlab.util.component.button;

import java.awt.event.MouseAdapter;

import javax.swing.Action;

public class IconButton extends ToolbarButton {
  private static final long serialVersionUID = -8816689144492728028L;

  public IconButton(Action action, MouseAdapter adapter) {
    super(action, adapter);
    setText(null);
  }

  public IconButton(Action action) {
    this(action, null);
  }
  
  @Override
  public void setAction(Action action) {
    super.setAction(action);
    setText(null);
  }
}
