package tszielin.qlab.action.project;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.event.TreeModelEvent;
import javax.swing.tree.TreePath;

import org.apache.commons.io.FilenameUtils;

import tszielin.qlab.component.pane.EditorsTabbedPane;
import tszielin.qlab.component.tree.item.FileItem;
import tszielin.qlab.component.tree.model.FileTreeModel;
import tszielin.qlab.error.ArgumentException;
import tszielin.qlab.util.error.ConfigException;
import tszielin.qlab.util.image.IconsItem;

public class DeleteFileAction extends ProjectAction {
  private static final long serialVersionUID = 6349816545077600524L;
  
  private EditorsTabbedPane tabEditors;

  public DeleteFileAction(JTree tree, EditorsTabbedPane tabEditors) throws ConfigException, ArgumentException {
    super(tree, "Delete...", 'D', IconsItem.ICON_BLANK, null, 
        "Delete file (permanently)", "Delete file permanently from file system.");
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
      
      if (JOptionPane.showOptionDialog(getWindow(), 
          "Delete parmanently the file " + ((FileItem)paths[0].getLastPathComponent()).getPath() + "?", 
          "Delete file", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, 
          new Object[]{
            UIManager.get("OptionPane.yesButtonText"), 
            UIManager.get("OptionPane.noButtonText")},        
          UIManager.get("OptionPane.noButtonText")) == JOptionPane.YES_OPTION) {
        if (JOptionPane.showOptionDialog(getWindow(),
                "You're about to delete parmanently the file.\nAre you really sure?",
                "Delete file", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                new Object[]{
                  UIManager.get("OptionPane.yesButtonText"),
                  UIManager.get("OptionPane.noButtonText")}, 
                  UIManager.get("OptionPane.noButtonText")) == JOptionPane.YES_OPTION) {
          try {
            TreePath parent = getParent(paths[0]);
            if (((FileItem)paths[0].getLastPathComponent()).delete()) {
              if (tabEditors != null && tabEditors.getTabCount() > 0) {
                for (int tab = 0; tab < tabEditors.getTabCount(); tab++) {
                  if (tabEditors.getEditor(tab) != null && 
                      tabEditors.getEditor(tab).getFile() != null && 
                      FilenameUtils.separatorsToUnix(tabEditors.getEditor(tab).getFile().getPath()).equals(
                          FilenameUtils.separatorsToUnix(((FileItem)paths[0].getLastPathComponent()).getPath()))) {
                      tabEditors.remove(tab);
                      break;
                    }
                  }
                }              
              ((FileTreeModel)getTree().getModel()).fireTreeStructureChanged(
                  new TreeModelEvent(this, parent));
              getTree().repaint();
            }
          }
          catch (Exception ex) {
            JOptionPane.showMessageDialog(getWindow(), ex.getMessage(), 
                "Delete file error", JOptionPane.ERROR_MESSAGE);
          }
        }
      }
    }
  }
//  
//  private void delete(File path) {
//    if (path.exists() && path.isDirectory()) {
//      File[] files = getConfig().getFiles(path);
//      for (int count = 0; count < files.length; count++) {
//        if (files[count].isDirectory()) {
//          delete(files[count]);
//        }
//        else {
//          files[count].delete();
//        }
//      }
//    }
//    path.delete();
//  }    
}
