package tszielin.qlab.listener;

import java.awt.Component;
import java.util.Collections;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import tszielin.qlab.action.connection.AddServerAction;
import tszielin.qlab.action.connection.AssignServerAction;
import tszielin.qlab.action.connection.CloneServerAction;
import tszielin.qlab.action.connection.EditServerAction;
import tszielin.qlab.action.connection.GotoEditorAction;
import tszielin.qlab.action.connection.LaunchQAction;
import tszielin.qlab.action.connection.QInfoAction;
import tszielin.qlab.action.connection.ReleaseServerAction;
import tszielin.qlab.action.connection.RemoveServerAction;
import tszielin.qlab.action.connection.RemoveServerGroupAction;
import tszielin.qlab.action.setting.ConnectionsSettingsAction;
import tszielin.qlab.component.editor.QEditor;
import tszielin.qlab.component.pane.EditorsTabbedPane;
import tszielin.qlab.component.tree.node.HostTreeNode;
import tszielin.qlab.component.tree.node.KdbServicesTreeNode;
import tszielin.qlab.config.AppConfig;
import tszielin.qlab.config.data.KdbService;
import tszielin.qlab.dialog.KdbServiceDialog;
import tszielin.qlab.error.StudioException;
import tszielin.qlab.util.action.popup.ActionPopup;
import tszielin.qlab.util.component.menu.ActionMenuItem;

public class KdbServicesTreePopupListener implements PopupMenuListener {
  private final JTree tree;
  private final EditorsTabbedPane tabEditors;

  public KdbServicesTreePopupListener(JTree tree, EditorsTabbedPane tabEditors) {
    this.tree = tree;
    this.tabEditors = tabEditors;
  }

  public void popupMenuCanceled(PopupMenuEvent e) {
  }

  public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
    if (e == null || !(e.getSource() instanceof JPopupMenu)) {
      return;
    }
    JPopupMenu popupMenu = (JPopupMenu)e.getSource();
    if (popupMenu.getComponentCount() > 0) {
      if (popupMenu.getComponent(0).getClass() == JMenu.class) {
        popupMenu.remove(0);
        popupMenu.remove(0);
      }
      for (int count = 0; count < popupMenu.getComponentCount(); count++) {
        if (popupMenu.getComponent(count) instanceof JMenuItem) {
          JMenuItem menuItem = (JMenuItem)popupMenu.getComponent(count);
          if (menuItem.getMouseListeners() != null) {
            boolean done = false;
            for (int item = 0; item < menuItem.getMouseListeners().length; item++) {
              if (menuItem.getMouseListeners()[item] instanceof ActionHintsListener) {
                ((ActionHintsListener)menuItem.getMouseListeners()[item]).mouseExited(null);
                done = true;
                break;
              }
            }
            if (done) {
              break;
            }
          }
        }
      }
    }
  }

  @SuppressWarnings("unchecked")
  public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
    if (e == null || !(e.getSource() instanceof JPopupMenu)) {
      return;
    }
    JPopupMenu popupMenu = (JPopupMenu)e.getSource();
    TreePath[] paths = tree.getSelectionPaths();
    TreePath path = paths != null && paths.length > 0 ? paths[0] : null;

    if (popupMenu.getComponentCount() > 0) {
      if (path == null) {
        for (Component component : popupMenu.getComponents()) {
          if (!(component instanceof ActionMenuItem && ((ActionMenuItem)component).getAction() instanceof AddServerAction)) {
            component.setVisible(path!=null);
          }
        }
        return;
      }
      
      if (path.getLastPathComponent() instanceof KdbServicesTreeNode) {
        List<QEditor> editors = ((KdbServicesTreeNode)path.getLastPathComponent()).getEditors();
        if (editors != null && !editors.isEmpty()) {
          JMenu menu = new JMenu("Go to editor...");          
          popupMenu.insert(menu, 0);
          popupMenu.insert(new JPopupMenu.Separator(), 1);
          for (QEditor editor : editors) {
            ActionPopup action = new GotoEditorAction(tabEditors, editor);
            menu.add(new ActionMenuItem(action, new ActionHintsListener(tabEditors, action)));
          }
          popupMenu.revalidate();
        }
      }
      
      for (int count = 0; count < popupMenu.getComponentCount(); count++) {
        
        if (popupMenu.getComponent(count) instanceof ActionMenuItem) {
          ActionMenuItem pmi = (ActionMenuItem)popupMenu.getComponent(count);
          
          if (pmi.getAction() instanceof AddServerAction ||
              pmi.getAction() instanceof ConnectionsSettingsAction) {
            continue;
          }
                   
          if (path != null && path.getLastPathComponent() instanceof DefaultMutableTreeNode) {
            if (pmi.getAction() instanceof CloneServerAction) {
              pmi.setVisible(!(path.getLastPathComponent() instanceof HostTreeNode));
              popupMenu.getComponent(count - 1).setVisible(pmi.isVisible());
            }
            if (pmi.getAction() instanceof QInfoAction) {
              pmi.setVisible(!(path.getLastPathComponent() instanceof HostTreeNode));
              popupMenu.getComponent(count - 1).setVisible(pmi.isVisible());
            }
            if (pmi.getAction() instanceof RemoveServerAction || pmi.getAction() instanceof EditServerAction ||
                pmi.getAction() instanceof ReleaseServerAction) {
              pmi.setVisible(!(path.getLastPathComponent() instanceof HostTreeNode));
              if (pmi.isVisible()) {
                pmi.setEnabled(pmi.getAction() instanceof ReleaseServerAction ?
                    ((KdbServicesTreeNode)path.getLastPathComponent()).assignedEditors() :
                    !((KdbServicesTreeNode)path.getLastPathComponent()).assignedEditors());
              }
              if (pmi.getAction() instanceof ReleaseServerAction) {
                popupMenu.getComponent(count - 1).setVisible(pmi.isVisible());
              }
            }
            if (pmi.getAction() instanceof RemoveServerGroupAction) {
              pmi.setVisible(!(path.getLastPathComponent() instanceof KdbServicesTreeNode));
              if (pmi.isVisible()) {
                List<DefaultMutableTreeNode> children = 
                  Collections.<DefaultMutableTreeNode>list(((DefaultMutableTreeNode)path.getLastPathComponent()).children());
                pmi.setEnabled(true);
                for (DefaultMutableTreeNode child : children) {
                  if (child instanceof KdbServicesTreeNode) {
                    if (((KdbServicesTreeNode)child).assignedEditors()) {
                      pmi.setEnabled(false);
                      continue;
                    }
                  }
                }
              }
            }
            
            if (pmi.getAction() instanceof AssignServerAction) {
              pmi.setVisible(tabEditors.getTabCount() > 0 && path.getLastPathComponent() instanceof KdbServicesTreeNode);
              popupMenu.getComponent(count + 1).setVisible(pmi.isVisible());
            }
            if (pmi.getAction() instanceof LaunchQAction) {
              String qApp = null;
              try {
                qApp = AppConfig.getConfig().getQApp();
              }
              catch (StudioException ex) {
              }
              if (path.getLastPathComponent() instanceof KdbServicesTreeNode) {
                boolean visible = qApp != null && !qApp.trim().isEmpty();
                if (visible) {
                  if (((KdbServicesTreeNode)path.getLastPathComponent()).getUserObject() instanceof KdbService) {
                    visible &= KdbServiceDialog.isLocalhost(
                        ((KdbService)((KdbServicesTreeNode)path.getLastPathComponent()).getUserObject()).getHost());
                  }
                  if (((KdbServicesTreeNode)path.getLastPathComponent()).assignedEditors()) {
                    visible &= !((KdbServicesTreeNode)path.getLastPathComponent()).connected();
                  }
                }
                pmi.setVisible(visible);                
              }
              else {
                pmi.setVisible(false);
              }
              popupMenu.getComponent(count - 1).setVisible(pmi.isVisible());
            }
          }
        }
      }
    }
  }
}
