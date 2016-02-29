package tszielin.qlab.action.component;

import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;

import tszielin.qlab.util.action.ActionBase;

abstract public class ChangeTabAction extends ActionBase {
  private static final long serialVersionUID = -4857390644947954711L;
  private JTabbedPane tabPane;
  
  protected ChangeTabAction(JTabbedPane tabPane, KeyStroke key) {
    super(null, (char)0, null, key, null, null);
    this.tabPane = tabPane;
  }
  
  protected JTabbedPane getTabPane() {
    return tabPane;
  }
}