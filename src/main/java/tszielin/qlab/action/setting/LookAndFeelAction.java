package tszielin.qlab.action.setting;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import tszielin.qlab.config.AppConfig;
import tszielin.qlab.error.StudioException;
import tszielin.qlab.util.action.popup.ActionPopup;
import tszielin.qlab.util.component.menu.CheckMenuItem;
import tszielin.qlab.util.image.IconsItem;

public class LookAndFeelAction extends ActionPopup {
  private static final long serialVersionUID = -2312249106871642892L;

  private static List<LookAndFeelAction> actions = null;
  private String driver;
  private AppConfig config;
  private static ButtonGroup btnGroup;

  protected LookAndFeelAction(Window source, String name, char mnemonic, Icon icon, KeyStroke key,
      String toolTip, String hint, String driver, AppConfig config) {
    super(source, name, mnemonic, icon, key, toolTip, hint);
    this.driver = driver;
    try {
      this.config = AppConfig.getConfig();
    }
    catch (StudioException ignored) {
    }
  }

  public void actionPerformed(ActionEvent e) {
    try {
      if (driver != null) {
        UIManager.setLookAndFeel(driver);
        updateComponentTreeUI((Window)getSource());
        if (config != null) { 
          config.setLookAndFeel(driver);
        }

      }
    }
    catch (Exception ignored) {
    }
  }

  public static List<LookAndFeelAction> getActionLookAndFeel(Window window, JComponent menu,
      AppConfig config) {
    if (actions == null) {
      actions = new Vector<LookAndFeelAction>();
      if (btnGroup == null) {
        btnGroup = new ButtonGroup();
      }
      UIManager.LookAndFeelInfo[] info = UIManager.getInstalledLookAndFeels();
      for (int count = 0; count < info.length; count++) {
        try {
          Class<?> cl = Class.forName(info[count].getClassName());

          LookAndFeel lnf = (LookAndFeel)cl.newInstance();
          if (lnf.isSupportedLookAndFeel()) {
            String name = lnf.getName();
            if (info[count].getClassName().indexOf("WindowsClassic") > -1) {
              name = "Classic Windows";
            }
            LookAndFeelAction action = new LookAndFeelAction(window, name, name.charAt(0),
                IconsItem.ICON_BLANK, null, lnf.getDescription(),
                "Change look and feel to " + name, info[count].getClassName(), config);
            actions.add(action);
            CheckMenuItem item = new CheckMenuItem(action);
            btnGroup.add(item);
            menu.add(item);
          }
        }
        catch (Exception ignored) {
        }
      }
    }
    return actions;
  }

  private static void updateComponentTreeUI(Window window) {
    SwingUtilities.updateComponentTreeUI(window);
    window.validate();
//    updateComponentTree(c);
//    c.invalidate();
//    c.validate();
//    c.repaint();
  }

//  private static void updateComponentTree(Component c) {
//    if (c instanceof JComponent) {
//      JComponent jc = (JComponent)c;
//      if (!(jc instanceof JTextComponent)) {
//        jc.updateUI();
//      }
//      JPopupMenu jpm = jc.getComponentPopupMenu();
//      if (jpm != null && jpm.isVisible() && jpm.getInvoker() == jc) {
//        updateComponentTreeUI(jpm);
//      }
//    }
//    Component[] children = null;
//    if (c instanceof JMenu) {
//      children = ((JMenu)c).getMenuComponents();
//    }
//    else
//      if (c instanceof Container) {
//        children = ((Container)c).getComponents();
//      }
//    if (children != null) {
//      for (int i = 0; i < children.length; i++) {
//        updateComponentTree(children[i]);
//      }
//    }
//  }
}
