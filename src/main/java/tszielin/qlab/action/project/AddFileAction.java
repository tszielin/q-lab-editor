package tszielin.qlab.action.project;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeModelEvent;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.TreePath;

import tszielin.qlab.action.editor.OpenFileAction;
import tszielin.qlab.component.pane.ConsolesTabbedPane;
import tszielin.qlab.component.pane.EditorsTabbedPane;
import tszielin.qlab.component.tree.item.FileProject;
import tszielin.qlab.component.tree.model.FileTreeModel;
import tszielin.qlab.error.ArgumentException;
import tszielin.qlab.util.error.ConfigException;
import tszielin.qlab.util.image.IconsItem;

public class AddFileAction extends ProjectAction {
  private static final long serialVersionUID = -3889543907855433539L;
  private EditorsTabbedPane tabEditors;
  private ConsolesTabbedPane tabConsoles;

  public AddFileAction(JTree tree, EditorsTabbedPane tabEditors, ConsolesTabbedPane tabConsoles) throws ConfigException, ArgumentException {
    super(tree, "New...", 'N', IconsItem.ICON_FILE_NEW, KeyStroke.getKeyStroke("control shift N"), 
        "New file", "Create new file");
    this.tabConsoles = tabConsoles;
    this.tabEditors = tabEditors;
  }

  public void actionPerformed(ActionEvent event) {
    List<String> extensions = getConfig().getExtensions();
    if (getTree() != null && getTree().getModel() instanceof FileTreeModel && extensions != null && !extensions.isEmpty()) {
      TreePath[] paths = getTree().getSelectionPaths();
      if (paths == null || paths.length != 1) {
        return;
      }
      
      TreePath parent = getParent(paths[0]);
      if (parent == null || !(parent.getLastPathComponent() instanceof FileProject)) {
        return;
      }
      JFileChooser chooser = new JFileChooser();
      chooser.setDialogTitle("Open new file...");
      chooser.setFileHidingEnabled(true);
      chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
      
      for (String extension : extensions) {
        final String ext = extension;
        chooser.addChoosableFileFilter(new FileFilter() {
          public boolean accept(File file) {
            return file.isDirectory() || file.getName().toLowerCase().endsWith("." + ext);
          }

          public String getDescription() {
            return "*." + ext;
          }
        });
      }
      chooser.setAcceptAllFileFilterUsed(false);
      for (FileFilter fileFilter : chooser.getChoosableFileFilters()) {
        if (fileFilter.getDescription().equals("*.q")) {
          chooser.setFileFilter(fileFilter);
        }
      }
      chooser.setCurrentDirectory((((File)paths[0].getLastPathComponent()).isDirectory() ?
          (File)paths[0].getLastPathComponent() : (File)parent.getLastPathComponent()));
      if (chooser.showOpenDialog(SwingUtilities.windowForComponent(getTree())) == JFileChooser.APPROVE_OPTION) {
        FileFilter fileFilter = chooser.getFileFilter();
        File file = chooser.getSelectedFile();
        if (file.getName().lastIndexOf(".") == -1) {
          file = new File(file.getParent(), file.getName() + 
              fileFilter.getDescription().substring(fileFilter.getDescription().lastIndexOf('.')));
        }
        try {
          file.createNewFile();
          ((FileTreeModel)getTree().getModel()).fireTreeStructureChanged(new TreeModelEvent(this, parent));
          getTree().repaint();
          new OpenFileAction(tabEditors, tabConsoles).initDocument(file.getPath());
        }
        catch (Exception ex) {
          JOptionPane.showMessageDialog(getWindow(), ex.getMessage(), 
              "New file error", JOptionPane.ERROR_MESSAGE);
        }
      }
    }
  }
}
