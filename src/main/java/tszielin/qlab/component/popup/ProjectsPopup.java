package tszielin.qlab.component.popup;

import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.JTree;

import tszielin.qlab.action.project.AddFileAction;
import tszielin.qlab.action.project.AddProjectAction;
import tszielin.qlab.action.project.DeleteFileAction;
import tszielin.qlab.action.project.OpenCloseProjectAction;
import tszielin.qlab.action.project.ProjectAction;
import tszielin.qlab.action.project.ReadFileAction;
import tszielin.qlab.action.project.RefreshProjectAction;
import tszielin.qlab.action.project.RemoveProjectAction;
import tszielin.qlab.action.project.RenameFileAction;
import tszielin.qlab.action.project.RenameProjectAction;
import tszielin.qlab.component.pane.ConsolesTabbedPane;
import tszielin.qlab.component.pane.EditorsTabbedPane;
import tszielin.qlab.error.StudioException;
import tszielin.qlab.listener.ActionHintsListener;
import tszielin.qlab.listener.ProjectTreePopupListener;
import tszielin.qlab.util.component.menu.ActionMenuItem;

public class ProjectsPopup extends JPopupMenu {
  private static final long serialVersionUID = 3757843047113670043L;

  public ProjectsPopup(JTree tree, EditorsTabbedPane tabEditors, ConsolesTabbedPane tabConsoles) {
    super("Connections");
    if (tree != null) {
      addPopupMenuListener(new ProjectTreePopupListener(tree));
    }
    
    ProjectAction action = null;
    try {
      action = new AddProjectAction(tree);
      add(new ActionMenuItem(action, new ActionHintsListener(tabEditors, action)));
      addSeparator();
      action = new RenameProjectAction(tree);
      add(new ActionMenuItem(action, new ActionHintsListener(tabEditors, action)));
      action = new RemoveProjectAction(tree);
      add(new ActionMenuItem(action, new ActionHintsListener(tabEditors, action)));
      addSeparator();
      action = new RefreshProjectAction(tree);
      add(new ActionMenuItem(action, new ActionHintsListener(tabEditors, action)));
      action = new OpenCloseProjectAction(tree, tabEditors);
      add(new ActionMenuItem(action, new ActionHintsListener(tabEditors, action)));
      addSeparator();
      JMenu menu = new JMenu("File...");
      action = new AddFileAction(tree, tabEditors, tabConsoles);
      menu.add(new ActionMenuItem(action, new ActionHintsListener(tabEditors, action)));
      menu.addSeparator();
      action = new ReadFileAction(tree, tabEditors, tabConsoles);
      menu.add(new ActionMenuItem(action, new ActionHintsListener(tabEditors, action)));
      action = new RenameFileAction(tree, tabEditors);
      menu.add(new ActionMenuItem(action, new ActionHintsListener(tabEditors, action)));
      menu.addSeparator();
      action = new DeleteFileAction(tree, tabEditors);
      menu.add(new ActionMenuItem(action, new ActionHintsListener(tabEditors, action)));
      add(menu);
    }
    catch(StudioException ignored) {     
    }
  }
}
