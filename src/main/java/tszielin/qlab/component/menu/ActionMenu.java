package tszielin.qlab.component.menu;

import java.awt.event.MouseAdapter;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;

import tszielin.qlab.action.editor.NewFileAction;
import tszielin.qlab.action.editor.OpenFileAction;
import tszielin.qlab.component.pane.EditorsTabbedPane;
import tszielin.qlab.listener.ActionHintsListener;
import tszielin.qlab.util.component.menu.ActionMenuItem;
import tszielin.qlab.util.component.menu.Menu;

public class ActionMenu extends Menu {
  private static final long serialVersionUID = -1164527463593408438L;

  public ActionMenu(String name) {
    super(name);
    setName(name);
  }

  public ActionMenu(Action action) {
    super(action);
    setName((String)action.getValue(Action.NAME));
  }
  
  public ActionMenu(Action action, MouseAdapter adapter) {
    this(action);
    this.addMouseListener(adapter);
  }

  public ActionMenu(String name, int mnemonic, String hint, Icon icon) {
    this(name);
    this.setMnemonic(mnemonic);
    this.setIcon(icon);
    this.setToolTipText(hint);
  }

  public ActionMenu(String name, int mnemonic, String hint, Icon icon, MouseAdapter adapter) {
    this(name, mnemonic, hint, icon);
    this.addMouseListener(adapter);
  }

  public ActionMenu(String name, int mnemonic, String hint, MouseAdapter adapter) {
    this(name, mnemonic, hint, (Icon)null);
    this.addMouseListener(adapter);
  }

  public boolean replace(Action action) {
    for (int count = 0; count < getMenuComponentCount(); count++) {
      if (getMenuComponent(count) instanceof JMenuItem) {
        JMenuItem item = (JMenuItem)getMenuComponent(count);
        if (item.getAction() != null && item.getAction().getClass() == action.getClass()) {
          item.setAction(action);
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
      add(new ActionMenuItem(action, new ActionHintsListener(tabEditors, action)));
    }
  }
  
  public void update(EditorsTabbedPane tabEditors, Action action) {
    update(tabEditors, action, false);
  }
  
  @Override
  public void setVisible(boolean visible) {
    if (getName() == null || !("File".equals(getName()) || "Tools".equals(getName()))) {
      super.setVisible(visible);
    }
    else {
      for (int count = 0; count < getMenuComponentCount(); count++) {
        if (getMenuComponent(count) instanceof JMenuItem) {
          if (((JMenuItem)getMenuComponent(count)).getAction() == null) {
            getMenuComponent(count).setVisible(true);
          }
          else {
            if (!(((JMenuItem)getMenuComponent(count)).getAction().getClass() == OpenFileAction.class || 
                ((JMenuItem)getMenuComponent(count)).getAction().getClass() == NewFileAction.class)) {
              getMenuComponent(count).setVisible(visible);
            }
          }
        }
        else {
          if (getMenuComponent(count) instanceof JSeparator) {
            getMenuComponent(count).setVisible(visible);
          }
        }
      }
    }
  }
}
