package tszielin.qlab.listener;

import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.tree.TreePath;

import tszielin.qlab.action.editor.OpenFileAction;
import tszielin.qlab.action.project.DeleteFileAction;
import tszielin.qlab.action.project.OpenCloseProjectAction;
import tszielin.qlab.action.project.ReadFileAction;
import tszielin.qlab.action.project.RefreshProjectAction;
import tszielin.qlab.action.project.RemoveProjectAction;
import tszielin.qlab.action.project.RenameProjectAction;
import tszielin.qlab.component.tree.item.FileItem;
import tszielin.qlab.component.tree.item.FileProject;
import tszielin.qlab.util.component.menu.ActionMenuItem;
import tszielin.qlab.util.image.IconsItem;

public class ProjectTreePopupListener implements PopupMenuListener {
  private final JTree tree;
  
  public ProjectTreePopupListener(JTree tree) {
    this.tree = tree;
  }

  public void popupMenuCanceled(PopupMenuEvent e) {
  }

  public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
    if (e == null || !(e.getSource() instanceof JPopupMenu)) {
      return;
    }
    JPopupMenu popupMenu = (JPopupMenu)e.getSource();
    if (popupMenu.getComponentCount() > 0) {
      for (int count = 0; count < popupMenu.getComponentCount(); count++) {
        if (popupMenu.getComponent(count) instanceof JMenu) {
          JMenu menu = (JMenu)popupMenu.getComponent(count);
          if (menu.getPopupMenu() != null) {
            for (int pos = 0; pos < menu.getPopupMenu().getComponentCount(); pos++) {
              if (menu.getPopupMenu().getComponent(pos) instanceof ActionMenuItem) {
                ActionMenuItem pmi = (ActionMenuItem)menu.getPopupMenu().getComponent(pos);
                if (pmi.getMouseListeners() != null) {
                  boolean done = false;
                  for (int item = 0; item < pmi.getMouseListeners().length; item++) {
                    if (pmi.getMouseListeners()[item] instanceof ActionHintsListener) {
                      ((ActionHintsListener)pmi.getMouseListeners()[item]).mouseExited(null);
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
      }
    }
  }

  public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
    if (e == null || !(e.getSource() instanceof JPopupMenu)) {
      return;
    }
    JPopupMenu popupMenu = (JPopupMenu)e.getSource();
    TreePath[] paths = tree.getSelectionPaths();
    
    if (popupMenu.getComponentCount() > 0) {
      for (int count = 0; count < popupMenu.getComponentCount(); count++) {
        if (popupMenu.getComponent(count) instanceof JMenu) {
          popupMenu.getComponent(count).setVisible(paths != null);
          if (popupMenu.getComponent(count).isVisible()) {
            JMenu menu = (JMenu)popupMenu.getComponent(count);
            if (menu.getMenuComponentCount() > 0) {
              for (int pos = 0; pos < menu.getMenuComponentCount(); pos++) {
                if (menu.getMenuComponent(pos) instanceof ActionMenuItem) {
                  ActionMenuItem actionMenuItem = (ActionMenuItem)menu.getMenuComponent(pos);
                  if (actionMenuItem.getAction() instanceof OpenFileAction) {
                    menu.getMenuComponent(pos).setVisible(paths != null && paths.length == 1);
                  }
                  if (actionMenuItem.getAction() instanceof ReadFileAction) {
                    menu.getMenuComponent(pos).setVisible(paths != null && paths.length != 0);
                    menu.getMenuComponent(pos - 1).setVisible(menu.getMenuComponent(pos).isVisible());
                  }
                  if (actionMenuItem.getAction() instanceof DeleteFileAction) {
                    menu.getMenuComponent(pos).setVisible(paths != null && paths.length == 1 && 
                        paths[0].getLastPathComponent() instanceof FileItem &&
                        ((FileItem)paths[0].getLastPathComponent()).isFile());
                    menu.getMenuComponent(pos - 1).setVisible(menu.getMenuComponent(pos).isVisible());
                  }
                }
              }
            }
          }
          popupMenu.getComponent(count - 1).setVisible(popupMenu.getComponent(count).isVisible());
        }
        else {
          if (popupMenu.getComponent(count) instanceof ActionMenuItem) {
            ActionMenuItem actionMenuItem = (ActionMenuItem)popupMenu.getComponent(count);
            if (actionMenuItem.getAction() instanceof RenameProjectAction ||
                actionMenuItem.getAction() instanceof RemoveProjectAction) {
              popupMenu.getComponent(count).setVisible(paths != null && paths.length == 1);
              if (popupMenu.getComponent(count).isVisible()) {
                popupMenu.getComponent(count).setVisible(paths[0].getLastPathComponent() instanceof FileProject);                
              }
              if (actionMenuItem.getAction() instanceof RemoveProjectAction) {
                popupMenu.getComponent(count-2).setVisible(popupMenu.getComponent(count).isVisible() || popupMenu.getComponent(count - 1).isVisible());
              }
            }
            if (actionMenuItem.getAction() instanceof RefreshProjectAction && 
                (paths == null || (paths != null && paths.length == 1))) {              
              popupMenu.getComponent(count).setVisible(paths == null ? paths != null :
                  paths[0].getLastPathComponent() instanceof FileProject && 
                  ((FileProject)paths[0].getLastPathComponent()).getProject() != null &&
                  !((FileProject)paths[0].getLastPathComponent()).getProject().isClosed());
              if (popupMenu.getComponent(count-1) instanceof JPopupMenu.Separator) { 
                popupMenu.getComponent(count-1).setVisible(popupMenu.getComponent(count).isVisible());
              }
            }
            if (actionMenuItem.getAction() instanceof OpenCloseProjectAction) {
              popupMenu.getComponent(count).setVisible(paths != null && paths.length == 1 && 
                  paths[0].getLastPathComponent() instanceof FileProject);
              if (popupMenu.getComponent(count).isVisible()) {
                if (paths[0].getLastPathComponent() instanceof FileProject && 
                    ((FileProject)paths[0].getLastPathComponent()).getProject() != null) {
                  if (((FileProject)paths[0].getLastPathComponent()).getProject().isClosed()) {
                    ((OpenCloseProjectAction)actionMenuItem.getAction()).setSmallIcon(
                        IconsItem.ICON_FOLDER_OPEN);
                    ((OpenCloseProjectAction)actionMenuItem.getAction()).setName("Open project");
                    ((OpenCloseProjectAction)actionMenuItem.getAction()).setToolTip("Open project");
                    ((OpenCloseProjectAction)actionMenuItem.getAction()).setHint(
                        "Open project (make it active)");
                  }
                  else {
                    ((OpenCloseProjectAction)actionMenuItem.getAction()).setSmallIcon(
                        IconsItem.ICON_FOLDER_CLOSE);
                    ((OpenCloseProjectAction)actionMenuItem.getAction()).setName("Close project");
                    ((OpenCloseProjectAction)actionMenuItem.getAction()).setToolTip(
                        "Close project");
                    ((OpenCloseProjectAction)actionMenuItem.getAction()).setHint(
                        "Close project (make it inactive)");
                  }
                  if (popupMenu.getComponent(count - 2) instanceof JPopupMenu.Separator) {
                    popupMenu.getComponent(count - 2).setVisible(
                        popupMenu.getComponent(count).isVisible());
                  }
                }
              }
            }            
          }
        }
      }
    }
  }
}