package tszielin.qlab.util.component.menu;

import java.awt.event.MouseAdapter;

import javax.swing.*;

/**
 * JMenu extension
 * @author <b><code>Thomas
 *   Zielinski</code></b><small>(thomas.zielinski@nagler-company.com)</small><br><i><code>Nagler
 *   & Company GmbH<code></i><br><small>2006</small>
 * @version 1.0
 */
public class Menu extends JMenu {
  private static final long serialVersionUID = 1225239309345758601L;

  public Menu(String name) {
    super(name);
  }

  public Menu(Action action) {
    super(action);
  }
  
  public Menu(Action action, MouseAdapter adapter) {
    this(action);
    this.addMouseListener(adapter);
  }

  public Menu(String name, int mnemonic, String hint, Icon icon) {
    this(name);
    this.setMnemonic(mnemonic);
    this.setIcon(icon);
    this.setToolTipText(hint);
  }

  public Menu(String name, int mnemonic, String hint, Icon icon, MouseAdapter adapter) {
    this(name, mnemonic, hint, icon);
    this.addMouseListener(adapter);
  }

  public Menu(String name, int mnemonic, String hint, MouseAdapter adapter) {
    this(name, mnemonic, hint, (Icon)null);
    this.addMouseListener(adapter);
  }
}
