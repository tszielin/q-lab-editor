package tszielin.qlab.action.project;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.event.TreeModelEvent;
import javax.swing.tree.TreePath;

import org.apache.commons.io.FilenameUtils;

import tszielin.qlab.component.pane.EditorsTabbedPane;
import tszielin.qlab.component.tree.item.FileItem;
import tszielin.qlab.component.tree.model.FileTreeModel;
import tszielin.qlab.config.data.EditorFile;
import tszielin.qlab.error.ArgumentException;
import tszielin.qlab.event.TabIndexChanged;
import tszielin.qlab.util.error.ConfigException;
import tszielin.qlab.util.image.IconsItem;

public class RenameFileAction extends ProjectAction {
  private static final long serialVersionUID = -4438564012049472245L;
  private EditorsTabbedPane tabEditors;

  public RenameFileAction(JTree tree, EditorsTabbedPane tabEditors) throws ConfigException, ArgumentException {
    super(tree, "Rename...", 'R', IconsItem.ICON_BLANK, null, 
        "Rename file name", "Rename file name.");
    this.tabEditors = tabEditors;
  }

  public void actionPerformed(ActionEvent event) {
    if (getTree() != null && getTree().getModel() instanceof FileTreeModel) {
      TreePath[] paths = getTree().getSelectionPaths();
      if (paths == null || paths.length != 1) {
        return;
      }
      
      if (!(paths[0].getLastPathComponent() instanceof FileItem) && 
          !((FileItem)paths[0].getLastPathComponent()).isFile()) {
        return;
      }            
      
      int tabIndex = -1;
      if (tabEditors != null && tabEditors.getTabCount() > 0) {
        for (int tab = 0; tab < tabEditors.getTabCount(); tab++) {
          if (tabEditors.getEditor(tab) != null && 
              tabEditors.getEditor(tab).getFile() != null && 
              FilenameUtils.separatorsToUnix(tabEditors.getEditor(tab).getFile().getPath()).equals(
                  FilenameUtils.separatorsToUnix(((FileItem)paths[0].getLastPathComponent()).getPath()))) {
            tabIndex = tab;
            break;
          }
        }
      }      
      if (tabIndex != -1 && tabEditors.getEditor(tabIndex) != null) {
        if (tabEditors.getEditor(tabIndex).isModified()) {
          return;
        }
      }
      
      Object fileName = JOptionPane.showInputDialog(getWindow(), 
          "New name", "Rename file name", JOptionPane.PLAIN_MESSAGE, null, null,        
          ((FileItem)paths[0].getLastPathComponent()).getName());
      if (fileName instanceof String && ((String)fileName).trim().length() > 0) {
        File file = new File(((FileItem)paths[0].getLastPathComponent()).getParent(), (String)fileName);
        if (FilenameUtils.separatorsToUnix(file.getPath()).equals(
            FilenameUtils.separatorsToUnix(((FileItem)paths[0].getLastPathComponent()).getPath()))) {          
          return;
        }
        if (((FileItem)paths[0].getLastPathComponent()).renameTo(file)) {
          if (tabIndex != -1) {
            tabEditors.setTitleAt(tabIndex, file.getName());
            if (tabEditors.getEditor(tabIndex) != null && tabEditors.getEditor(tabIndex).getFile() != null) {
              EditorFile editorFile = tabEditors.getEditor(tabIndex).getFile();
              editorFile.setFile(file);
            }
            if (tabIndex == tabEditors.getSelectedIndex()) {
              tabEditors.fireData(new TabIndexChanged(tabEditors, tabIndex));
            }
          }
          ((FileTreeModel)getTree().getModel()).fireTreeStructureChanged(
              new TreeModelEvent(this, getParent(paths[0])));
          getTree().repaint();
        }
      }
    }
  }
}
