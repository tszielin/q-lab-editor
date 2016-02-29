package tszielin.qlab.action.project;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.tree.TreePath;

import tszielin.qlab.action.editor.OpenFileAction;
import tszielin.qlab.component.pane.ConsolesTabbedPane;
import tszielin.qlab.component.pane.EditorsTabbedPane;
import tszielin.qlab.component.tree.item.FileItem;
import tszielin.qlab.component.tree.item.FileProject;
import tszielin.qlab.config.data.EditorFile;
import tszielin.qlab.config.data.KdbService;
import tszielin.qlab.error.ArgumentException;
import tszielin.qlab.error.FileException;
import tszielin.qlab.util.error.ConfigException;
import tszielin.qlab.util.image.IconsItem;

public class ReadFileAction extends ProjectAction {
  private static final long serialVersionUID = -4499672765650485686L;
  private EditorsTabbedPane tabEditors;
  private ConsolesTabbedPane tabConsoles;

  public ReadFileAction(JTree tree, EditorsTabbedPane tabEditors, ConsolesTabbedPane tabConsoles) throws ConfigException, ArgumentException {
    super(tree, "Open...", 'O', IconsItem.ICON_FILE_OPEN, KeyStroke.getKeyStroke("control shift O"), 
        "Open file(s)", "Open selected file(s)");
    this.tabConsoles = tabConsoles;
    this.tabEditors = tabEditors;
  }

  public void actionPerformed(ActionEvent event) {
    if (getTree() != null) {
      TreePath[] paths = getTree().getSelectionPaths();
      if (paths == null) {
        return;
      }      
      for (TreePath path : paths) {
        if (path.getLastPathComponent() instanceof File) {
          try {
            TreePath parent = getParent(path);
            KdbService connection = null;
            if (parent.getLastPathComponent() instanceof FileProject) {
              connection = ((FileProject)parent.getLastPathComponent()).getProject() != null ?
                  ((FileProject)parent.getLastPathComponent()).getProject().getConnection() : null;
            }
            open((File)path.getLastPathComponent(), connection);
          }
          catch (FileException ex) {
            JOptionPane.showMessageDialog(getWindow(), ex.getMessage(), 
                "Open file error", JOptionPane.ERROR_MESSAGE);
          }
        }
      }
    }
  }

  private void open(File file, KdbService connection) throws FileException {
    if (file.exists()) {
      if (file.isDirectory()) {
        File[] files = getConfig().getFiles(file);
        for (int count = 0; count < files.length; count++) {
          if (files[count].isDirectory()) {
            open(files[count], connection);
          }
          else {
            new OpenFileAction(tabEditors, tabConsoles).initDocument(
                new EditorFile(files[count], connection));
          }
        }
      }
      if (file instanceof FileItem) {
        new OpenFileAction(tabEditors, tabConsoles).initDocument(new EditorFile(file, connection));
      }
    }
  }
}