package tszielin.qlab.component.popup;

import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.KeyStroke;

import tszielin.qlab.action.grid.ChartAction;
import tszielin.qlab.action.grid.CopyResultAction;
import tszielin.qlab.action.grid.CopyToExcelAction;
import tszielin.qlab.action.grid.CopyToXMLAction;
import tszielin.qlab.action.grid.ExportToAction;
import tszielin.qlab.listener.GridPopupListener;
import tszielin.qlab.util.action.ActionBase;

public class GridPopup extends JPopupMenu {
  private static final long serialVersionUID = 4476826984927311088L;

  public GridPopup(JTable table) {
    super("Grids");
    if (table != null) {
      addPopupMenuListener(new GridPopupListener(table));
    }
    
    ActionBase action = new CopyResultAction(table);
    table.getInputMap().put(action.getAccelerator(), action);
    add(action);
    addSeparator();
    action = new CopyToExcelAction(table);
    table.getInputMap().put(action.getAccelerator(), action);
    add(action);
    action = new CopyToXMLAction(table);
    table.getInputMap().put(action.getAccelerator(), action);
    add(action);
    addSeparator();
    action = new ExportToAction(table);
    table.getInputMap().put(action.getAccelerator(), action);
    add(action);
    addSeparator();
    action = new ChartAction(table);
    table.getInputMap().put(action.getAccelerator(), action);
    add(action);
    
    table.getInputMap().put(KeyStroke.getKeyStroke("shift F10"), null);
  }
}
