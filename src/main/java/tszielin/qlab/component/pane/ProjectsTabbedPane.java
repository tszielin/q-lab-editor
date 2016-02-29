package tszielin.qlab.component.pane;

import java.awt.Component;
import java.awt.dnd.DropTarget;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;

import tszielin.qlab.listener.ProjectDropTargetListener;

public class ProjectsTabbedPane extends CloseTabbedPane {
  private static final long serialVersionUID = 6559614779917665472L;

  public ProjectsTabbedPane(Component component, Icon icon) {
    super(false);
    addTab("Projects", new JScrollPane(component));
    setIconAt(0, icon);
    if (component instanceof JTree) {
      new DropTarget(this, new ProjectDropTargetListener((JTree)component));
    }
  }

  @Override
  protected Action getAction() {
    return null;
  }

  @Override
  protected JSplitPane getSplitPane() {
    return getParent() instanceof JSplitPane ? (JSplitPane)getParent() : null; 
  }
  
  protected void maximize() {    
  }
}
