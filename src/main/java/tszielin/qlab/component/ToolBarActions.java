package tszielin.qlab.component;

import java.awt.FlowLayout;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.JSeparator;
import javax.swing.JToolBar;

import tszielin.qlab.action.editor.NewFileAction;
import tszielin.qlab.action.editor.OpenFileAction;
import tszielin.qlab.component.pane.EditorsTabbedPane;
import tszielin.qlab.listener.ActionHintsListener;
import tszielin.qlab.util.component.button.IconButton;

public class ToolBarActions extends JToolBar {
  private static final long serialVersionUID = 2461059143883324352L;

  public ToolBarActions(String name) {
    super(name);
    setFloatable(false);
    setRollover(true);
    setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
    setBorderPainted(false);
  }
  
  public ToolBarActions() {
    this(null);
  }
  
  public boolean replace(Action action) {
    for (int count = 0; count < getComponentCount(); count++) {
      if (getComponent(count) instanceof AbstractButton) {
        AbstractButton button = (AbstractButton)getComponent(count);
        if (button.getAction() != null && button.getAction().getClass() == action.getClass()) {
          button.setAction(action);
          return true;
        }
      }
    }
    return false;
  }
  
  public void update(EditorsTabbedPane tabEditors, Action action, boolean separator) {
    if (!replace(action)) {
      if (separator) {
        addSeparator();
      }      
      add(new IconButton(action, new ActionHintsListener(tabEditors, action)));
    }
  }
  
  public void update(EditorsTabbedPane tabEditors, Action action) {
    update(tabEditors, action, false);
  }
  
  public ToolBarActions getToolBar(String name) {
    if (name == null || name.trim().length() == 0) {
      return null;
    }
    for (int count = 0; count < getComponentCount(); count++) {
      if (getComponent(count) instanceof ToolBarActions && name.equals(getComponent(count).getName())) {
        return (ToolBarActions)getComponent(count);
      }
    }
    return null;
  }
  
  @Override
  public void setVisible(boolean visible) {
    if (getName() == null || !getName().equals("File")) {
      super.setVisible(visible);
    }
    else {
      for (int count = 0; count < getComponentCount(); count++) {
        if (getComponent(count) instanceof AbstractButton && 
            ((AbstractButton)getComponent(count)).getAction() != null) {
          if (!(((AbstractButton)getComponent(count)).getAction().getClass() == OpenFileAction.class ||
              ((AbstractButton)getComponent(count)).getAction().getClass() == NewFileAction.class)) {
            getComponent(count).setVisible(visible);
          }
        }
        else {
          if (getComponent(count) instanceof JSeparator) {
            getComponent(count).setVisible(visible);
          }
        }
      }
    }
  }
}
