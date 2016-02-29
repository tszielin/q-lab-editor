package tszielin.qlab.action.editor;

import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;

import tszielin.qlab.component.editor.QEditor;
import tszielin.qlab.component.filechooser.QFileFilter;
import tszielin.qlab.component.pane.EditorsTabbedPane;
import tszielin.qlab.config.AppConfig;
import tszielin.qlab.config.data.EditorFile;
import tszielin.qlab.error.StudioException;
import tszielin.qlab.util.error.ConfigException;
import tszielin.qlab.util.image.IconsItem;

public class SaveAction extends EditorAction {
  private static final long serialVersionUID = -6859567080847806361L;
  private AppConfig config;
  
  protected SaveAction(EditorsTabbedPane tabEditors, String caption, char mnemonic, 
      Icon icon, KeyStroke key, String toolTip, String hint) {
    super(tabEditors, caption, mnemonic, icon, key, toolTip, hint);
    try {
      this.config = AppConfig.getConfig();
    }
    catch(StudioException ignored) {     
    }
  }
    
  public SaveAction(EditorsTabbedPane tabEditors) {
    this(tabEditors, "Save", 'S', IconsItem.ICON_SAVE, KeyStroke.getKeyStroke("ctrl S"), "Save", "Save changes");
  }

  public void actionPerformed(ActionEvent event) { 
    if (getEditor() != null && getEditor().isModified()) {
      if (!getEditor().isEmpty() && 
          (getEditor().getPath() == null || !getEditor().isEditable() || 
              getEditor().getPath().startsWith("script"))) {
        saveAs();
      }
      else {
        int result = JOptionPane.YES_OPTION;
        if (getEditor() instanceof QEditor && getEditor().isEmpty()) {
          return;
        }
        if (getEditor().getFile() != null && !getEditor().getFile().isSaved()) {
          result = JOptionPane.showConfirmDialog(getEditor(), 
              "The file '" + getEditor().getFile().getPath() + 
              "'\nhas been changed on the file system.\n" +
              "Do you want to overwrite changes made on file system?", 
              "Update confict",
              JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        }
        if (result == JOptionPane.YES_OPTION) {
          save();
        }
      }
    }
  }
  
  protected void save() {
    save(null);
  }
  
  protected void save(String filename) {
    try {
      BufferedWriter writer = new BufferedWriter(new FileWriter(filename == null ? getEditor().getPath() : filename));
      getEditor().write(writer);
      writer.flush();
      writer.close();
      getEditor().setModified(false);
      config.addOpenedFile(filename == null ? getEditor().getFile() : new EditorFile(new File(filename)));
      if (filename != null) {
        getEditor().setFilename(filename);
      }
      getEditor().getFile().setLastModified();
      getEditor().setActions();
    }
    catch (Exception ex) {
      JOptionPane.showMessageDialog(getEditor(), "Cannot store script in file " + 
          getEditor().getPath() + ".\n" + ex.getMessage(), "Save error", 
          JOptionPane.ERROR_MESSAGE);
    }
  }
  
  protected void saveAs() {
    JFileChooser chooser = new JFileChooser();
    chooser.setDialogType(JFileChooser.SAVE_DIALOG);
    chooser.setDialogTitle("Save script: '" + getEditor().getPath() + "' as");
    chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    
    FileFilter qfilter = new QFileFilter();
    chooser.addChoosableFileFilter(qfilter);
    chooser.setFileFilter(qfilter);
    
    String filename = getEditor().getPath();
    if (filename != null) {
      File file = new File(filename);
      File dir = new File(file.getParent() != null ? file.getParent()
          : config.getCurrentPath() != null ? config.getCurrentPath() : file.getPath());
      chooser.setCurrentDirectory(dir);
    }

    if (chooser.showSaveDialog(getEditor()) == JFileChooser.APPROVE_OPTION) {
      File file = chooser.getSelectedFile();
      try {
        config.setCurrentPath(file.getParent());
      }
      catch (ConfigException ignored) {
      }
      filename = file.getPath();
      if (!filename.endsWith(".q")) {
        filename += ".q";
      }
      boolean canSave = true;
      if ((new File(filename)).exists()) {
        canSave = JOptionPane.showConfirmDialog(getEditor(), filename + " already exists.\nOverwrite?",
            "Overwrite?", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
      }
      if (canSave) {
        save(filename);        
      }
    }
  }
}
