package tszielin.qlab.component.popup;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JTree;

import tszielin.qlab.action.connection.AddServerAction;
import tszielin.qlab.action.connection.AssignServerAction;
import tszielin.qlab.action.connection.CloneServerAction;
import tszielin.qlab.action.connection.EditServerAction;
import tszielin.qlab.action.connection.LaunchQAction;
import tszielin.qlab.action.connection.QInfoAction;
import tszielin.qlab.action.connection.ReleaseServerAction;
import tszielin.qlab.action.connection.RemoveServerAction;
import tszielin.qlab.action.connection.RemoveServerGroupAction;
import tszielin.qlab.action.connection.ServerAction;
import tszielin.qlab.action.setting.ConnectionsSettingsAction;
import tszielin.qlab.component.pane.EditorsTabbedPane;
import tszielin.qlab.listener.ActionHintsListener;
import tszielin.qlab.listener.KdbServicesTreePopupListener;
import tszielin.qlab.util.component.menu.ActionMenuItem;
import tszielin.qlab.util.listener.DataListener;

public class KdbServicesPopup extends JPopupMenu {
  private static final long serialVersionUID = 3862094614365315388L;
  
  public KdbServicesPopup(JTree tree, EditorsTabbedPane tabEditors) {
    super("kdb+ services");
    if (tree != null) {
      addPopupMenuListener(new KdbServicesTreePopupListener(tree, tabEditors));
    }    
    
    ServerAction action = new AssignServerAction(tree, tabEditors);
    tree.registerKeyboardAction(action, action.getAccelerator(), JComponent.WHEN_FOCUSED);
    if (tabEditors instanceof DataListener) {
      action.addFireDataListener((DataListener)tabEditors);
    }
    add(new ActionMenuItem(action, new ActionHintsListener(tabEditors, action)));
    addSeparator();
    action = new AddServerAction(tree);
    add(new ActionMenuItem(action, new ActionHintsListener(tabEditors, action)));
    action = new EditServerAction(tree);
    tree.registerKeyboardAction(action, action.getAccelerator(), JComponent.WHEN_FOCUSED);
    if (tabEditors instanceof DataListener) {
      action.addFireDataListener((DataListener)tabEditors);
    }
    add(new ActionMenuItem(action, new ActionHintsListener(tabEditors, action)));
    addSeparator();
    action = new CloneServerAction(tree);
    if (tabEditors instanceof DataListener) {
      action.addFireDataListener((DataListener)tabEditors);
    }
    add(new ActionMenuItem(action, new ActionHintsListener(tabEditors, action)));
    addSeparator();
    action = new RemoveServerAction(tree);
    if (tabEditors instanceof DataListener) {
      action.addFireDataListener((DataListener)tabEditors);
    }
    tree.registerKeyboardAction(action, action.getAccelerator(), JComponent.WHEN_FOCUSED);
    add(new ActionMenuItem(action, new ActionHintsListener(tabEditors, action)));
    action = new RemoveServerGroupAction(tree);
    if (tabEditors instanceof DataListener) {
      action.addFireDataListener((DataListener)tabEditors);
    }
    add(new ActionMenuItem(action, new ActionHintsListener(tabEditors, action)));
    addSeparator();
    action = new ReleaseServerAction(tabEditors, tree);
    add(new ActionMenuItem(action, new ActionHintsListener(tabEditors, action)));
    tree.registerKeyboardAction(action, action.getAccelerator(), JComponent.WHEN_FOCUSED);
    addSeparator();
    action = new QInfoAction(tree, tabEditors);
    add(new ActionMenuItem(action, new ActionHintsListener(tabEditors, action)));
    tree.registerKeyboardAction(action, action.getAccelerator(), JComponent.WHEN_FOCUSED);
    addSeparator();
    action = new LaunchQAction(tree);
    add(new ActionMenuItem(action, new ActionHintsListener(tabEditors, action)));
    tree.registerKeyboardAction(action, action.getAccelerator(), JComponent.WHEN_FOCUSED);
    addSeparator();
    Action base = new ConnectionsSettingsAction(tree);
    add(new ActionMenuItem(base, new ActionHintsListener(tabEditors, base)));
  }
}
