package tszielin.qlab.action.project;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeModelEvent;
import javax.swing.tree.TreePath;

import org.apache.commons.io.FilenameUtils;

import tszielin.qlab.component.pane.EditorsTabbedPane;
import tszielin.qlab.component.tree.item.FileProject;
import tszielin.qlab.component.tree.model.FileTreeModel;
import tszielin.qlab.config.AppInformation;
import tszielin.qlab.error.ArgumentException;
import tszielin.qlab.util.error.ConfigException;
import tszielin.qlab.util.image.IconsItem;

public class OpenCloseProjectAction extends ProjectAction {
  private static final long serialVersionUID = -3611517341908091385L;
  private final EditorsTabbedPane tabEditors;

  public OpenCloseProjectAction(JTree tree, EditorsTabbedPane tabEditors) throws ConfigException, ArgumentException {
    super(tree, "Close project", (char)0, IconsItem.ICON_FOLDER_CLOSE, null, "Close project", "Close project (make it inactive)");
    this.tabEditors = tabEditors;
  }

  public void actionPerformed(ActionEvent event) {
    if (getTree() != null && getTree().getModel() instanceof FileTreeModel) {
      TreePath[] paths = getTree().getSelectionPaths();
      if (paths != null && paths.length == 1 && paths[0].getLastPathComponent() instanceof FileProject &&
          ((FileProject)paths[0].getLastPathComponent()).getProject() != null) {
        ((FileProject)paths[0].getLastPathComponent()).getProject().setClosed(
            !((FileProject)paths[0].getLastPathComponent()).getProject().isClosed());
        try {
          closeTabs(((FileProject)paths[0].getLastPathComponent()).getProject().getPath());          
          
          getConfig().setProject(((FileProject)paths[0].getLastPathComponent()).getProject());
          getTree().collapsePath(paths[0]);
          ((FileTreeModel)getTree().getModel()).fireTreeStructureChanged(new TreeModelEvent(this, paths[0]));
          getTree().repaint();
        }
        catch (ConfigException ex) {
          JOptionPane.showMessageDialog(SwingUtilities.windowForComponent(getTree()), ex.getMessage(), 
              AppInformation.getInformation().getTitle(), JOptionPane.ERROR_MESSAGE);
        }
      }
    }
  }
  
  private void closeTabs(File path) {
    if (path != null && path.exists() && path.isDirectory()) {
      File[] files = getConfig().getFiles(path);
      for (int count = 0; count < files.length; count++) {
        if (files[count].isDirectory()) {
          closeTabs(files[count]);
        }
        else {
          closeTab(files[count]);
        }
      }
    }
  }
  
  private void closeTab(File file) {
    if (file != null && file.exists() && file.isFile()) {
      if (tabEditors != null && tabEditors.getTabCount() > 0) {
        for (int tab = 0; tab < tabEditors.getTabCount(); tab++) {
          if (tabEditors.getEditor(tab) != null && 
              tabEditors.getEditor(tab).getFile() != null && 
              FilenameUtils.separatorsToUnix(tabEditors.getEditor(tab).getFile().getPath()).equals(
                  FilenameUtils.separatorsToUnix(file.getPath()))) {
            tabEditors.remove(tab);
            break;
          }
        }
      }
    }
  }
}