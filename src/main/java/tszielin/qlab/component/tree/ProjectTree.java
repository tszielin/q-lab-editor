package tszielin.qlab.component.tree;

import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragSource;

import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.ExpandVetoException;

import tszielin.qlab.adapter.ProjectTreeMouseAdapter;
import tszielin.qlab.component.pane.ConsolesTabbedPane;
import tszielin.qlab.component.pane.EditorsTabbedPane;
import tszielin.qlab.component.popup.ProjectsPopup;
import tszielin.qlab.component.tree.item.FileItem;
import tszielin.qlab.component.tree.item.FileProject;
import tszielin.qlab.component.tree.model.FileTreeModel;
import tszielin.qlab.config.AppInformation;
import tszielin.qlab.config.ProjectConfig;
import tszielin.qlab.config.data.KdbService;
import tszielin.qlab.config.data.Project;
import tszielin.qlab.error.StudioException;
import tszielin.qlab.event.ProjectConnectionChanged;
import tszielin.qlab.listener.ProjectDropTargetListener;
import tszielin.qlab.listener.ProjectTreeDragGestureListener;
import tszielin.qlab.util.event.DataEvent;
import tszielin.qlab.util.listener.DataListener;
import tszielin.qlab.util.listener.PopupListener;

public class ProjectTree extends JTree implements DataListener, TreeWillExpandListener {
  private static final long serialVersionUID = 33273697715468568L;

  public ProjectTree(EditorsTabbedPane tabEditors, ConsolesTabbedPane tabConsoles) {
    super();
    setRootVisible(false);
    DragSource dragSource = DragSource.getDefaultDragSource();
    dragSource.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_COPY,
        new ProjectTreeDragGestureListener());
    ToolTipManager.sharedInstance().registerComponent(this);
    setComponentPopupMenu(new ProjectsPopup(this, tabEditors, tabConsoles));
    addMouseListener(new PopupListener(this.getComponentPopupMenu()));
    addMouseListener(new ProjectTreeMouseAdapter(this, tabEditors));
    addTreeWillExpandListener(this);
  }

  @Override
  public String convertValueToText(Object value, boolean selected, boolean expanded, boolean leaf,
      int row, boolean hasFocus) {
    return value instanceof FileProject ?
        ((FileProject)value).getProject() != null ? ((FileProject)value).getProject().getName() : 
          "Unknown" : value instanceof FileItem ? ((FileItem)value).getName() : String.valueOf(value);
  }

  public void onData(DataEvent<?> event) {
    if (event instanceof ProjectConnectionChanged && 
        event.getSource() instanceof ProjectDropTargetListener && 
        ((ProjectConnectionChanged)event).getData() != null && 
        ((ProjectConnectionChanged)event).getTreePath() != null) {
      if ((((ProjectConnectionChanged)event).getTreePath()).getLastPathComponent() instanceof FileProject) {
        Project project = ((FileProject)(((ProjectConnectionChanged)event).getTreePath()).getLastPathComponent()).getProject();
        if (project != null) {
          try {
            KdbService connection = project.getConnection();
            if (connection == null) {
              project.setConnection(((ProjectConnectionChanged)event).getData());
            }
            else {
              if (!connection.equals(((ProjectConnectionChanged)event).getData())) {
                if (JOptionPane.showOptionDialog(SwingUtilities.windowForComponent(this),
                    "Do you want change default server\nto " + 
                    ((ProjectConnectionChanged)event).getData().getUsername() + "@" + 
                    ((ProjectConnectionChanged)event).getData().getHost() + ":" + 
                    ((ProjectConnectionChanged)event).getData().getPort() + "\nfrom " + 
                    connection.getUsername() + "@" + connection.getHost() + ":" + 
                    connection.getPort() + "\nin project '" + project.getName() + "'?",
                    "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                    new Object[]{UIManager.get("OptionPane.yesButtonText"), 
                      UIManager.get("OptionPane.noButtonText")}, 
                      UIManager.get("OptionPane.noButtonText")) == JOptionPane.YES_OPTION) {
                  project.setConnection(((ProjectConnectionChanged)event).getData());
                }
                else {
                  project.setConnection(((ProjectConnectionChanged)event).getData());
                }
              }
              else {
                project = null;
              }
            }
            if (project != null) {
              ProjectConfig config = ProjectConfig.getConfig();
              config.setProject(project);
              if (getModel() instanceof FileTreeModel) {
                ((FileTreeModel)getModel()).fireTreeStructureChanged(new TreeModelEvent(this, ((ProjectConnectionChanged)event).getTreePath()));
              }
            }
          }
          catch (StudioException ex) {
            JOptionPane.showMessageDialog(SwingUtilities.windowForComponent(this), ex.getMessage(),
                AppInformation.getInformation().getTitle(), JOptionPane.ERROR_MESSAGE);
          }
        }
      }
    }
  }

  public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {
  }

  public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
    if (event != null && event.getPath() != null && 
        event.getPath().getLastPathComponent() instanceof FileProject && 
        ((FileProject)event.getPath().getLastPathComponent()).isClosed()) {
      throw new ExpandVetoException(event);
    }    
  }
}
