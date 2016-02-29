package tszielin.qlab.component;

import javax.swing.JMenuBar;

import tszielin.qlab.component.menu.ActionMenu;
import tszielin.qlab.util.event.DataEvent;
import tszielin.qlab.util.listener.DataListener;

public class AppMenuBar extends JMenuBar implements DataListener {
  private static final long serialVersionUID = -45384067219291402L;

  public AppMenuBar() {
    super();
  }
  
  public ActionMenu getMenu(String name) {
    if (name == null || name.trim().length() == 0) {
      return null;
    }
    for (int count = 0; count < getMenuCount(); count++) { 
      if (name.equals(getMenu(count).getName())) {
        return getMenu(count) instanceof ActionMenu ? (ActionMenu)getMenu(count) : null;
      }
    }
    return null;
  }

  public void onData(DataEvent<?> event) {   
  }
}
