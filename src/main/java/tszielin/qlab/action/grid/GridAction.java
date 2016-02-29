package tszielin.qlab.action.grid;

import javax.swing.*;

import tszielin.qlab.util.action.ActionBase;

abstract class GridAction extends ActionBase {
  private static final long serialVersionUID = -4646528121223010368L;
  
  private JTable table;
  
  public GridAction(JTable table, String caption, char mnemonic, Icon icon, KeyStroke key, String toolTip,
      String hint) {
    super(caption, mnemonic, icon, key, toolTip, hint);
    this.table = table;
  }
  
  protected JTable getTable() {
    return table;
  }
}
