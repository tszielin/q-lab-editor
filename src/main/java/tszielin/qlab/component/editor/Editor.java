package tszielin.qlab.component.editor;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.undo.UndoManager;

import org.netbeans.editor.ActionFactory;
import org.netbeans.editor.BaseKit;
import org.netbeans.editor.StatusBar;
import org.netbeans.editor.Utilities;
import org.netbeans.editor.ext.ExtKit;

import tszielin.qlab.action.editor.CloseAllAction;
import tszielin.qlab.action.editor.SaveAction;
import tszielin.qlab.action.editor.SaveAllAction;
import tszielin.qlab.action.kdb.CancelRunAction;
import tszielin.qlab.action.kdb.RunAction;
import tszielin.qlab.action.kdb.RunLineAction;
import tszielin.qlab.component.Iconable;
import tszielin.qlab.component.Tooltipable;
import tszielin.qlab.component.pane.ConsolesTabbedPane;
import tszielin.qlab.component.pane.EditorsTabbedPane;
import tszielin.qlab.config.AppConfig;
import tszielin.qlab.config.data.EditorFile;
import tszielin.qlab.config.data.KdbService;
import tszielin.qlab.error.FileException;
import tszielin.qlab.error.StudioException;
import tszielin.qlab.event.EditorStateChanged;
import tszielin.qlab.event.FilenameChanged;
import tszielin.qlab.event.TabCountChanged;
import tszielin.qlab.util.component.menu.ActionMenuItem;
import tszielin.qlab.util.event.DataEvent;
import tszielin.qlab.util.event.FireData;
import tszielin.qlab.util.image.IconsItem;
import tszielin.qlab.util.listener.DataListener;

abstract public class Editor extends JEditorPane implements DataListener, DocumentListener, Tooltipable, Iconable {
  private static final long serialVersionUID = -2053470893195752519L;

  protected AppConfig config;
  protected EditorFile editorFile;
  protected Timer timer;
  protected EditorsTabbedPane tabEditors;  
  protected FireData fireData;
  protected UndoManager undoManager;
  
  protected int lineWidth;
  
  private boolean modified;
    
  private TimerTask autoSaveTask;

  private boolean changed;
  
  
  class CheckFileTask extends TimerTask {
    private Editor editor;
    
    public CheckFileTask(Editor editor) {
      this.editor = editor;      
    }
    
    public void run() {
      File file = new File(editor.editorFile.getPath());
      if (file != null && !file.exists()) {
        file = null;
      }
      editor.setEditable(file == null || file.canWrite());
      if (Utilities.getEditorUI(editor) != null && Utilities.getEditorUI(editor).getStatusBar() != null) {
        Utilities.setStatusBarText(editor, StatusBar.CELL_FILE_TYPE,
            editor.isEditable() ? StatusBar.WRITE : StatusBar.READ);
      }
    }
  }
  
  class AutoSaveTask extends TimerTask {
    private String path;
    private Editor editor;
    private SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddhhmmss");
    
    public AutoSaveTask(Editor editor, String path) {
      this.editor = editor;     
      this.path = path == null || path.trim().isEmpty() ?
          System.getProperty("user.home") + "/.studioforkdb/autoSave" : path;
    }
    
    public void run() {
      if (changed) {
        String filename = editor.editorFile.getPath();
        filename = filename.substring(0, filename.indexOf(".q")).replace(":", "");
        File file = new File(path, new File(filename) + "." + formatter.format(new Date()) + ".q");
        if (!file.canWrite()) {
          File path = new File(file.getParent());
          if (!path.exists()) {
            path.mkdirs();
          }
        }
        try {
          editor.write(new BufferedWriter(new FileWriter(file.getPath())));
          changed = false;
        }
        catch (IOException ignored) {
        }
      }
    }
  }
    
  protected Editor(EditorsTabbedPane tabEditors, ConsolesTabbedPane tabConsoles) throws FileException {
    this((String)null, tabEditors, tabConsoles);
  }

  protected Editor(EditorFile file, EditorsTabbedPane tabEditors, ConsolesTabbedPane tabConsoles) throws FileException {
    super();
    
    try {
      this.config = AppConfig.getConfig();
    }
    catch(StudioException ignored) {
    }
    
    this.tabEditors = tabEditors;
    this.fireData = new FireData();    
    fireData.addDataListener(this);
    if (tabEditors instanceof DataListener) {
      fireData.addDataListener((DataListener)tabEditors);
    }
        
    this.editorFile = file;    
    
    modified = false;
    
    timer = new Timer(true);
    timer.schedule(new CheckFileTask(this), 0, 1000L);
    
    addFocusListener(new FocusListener() {
      public void focusGained(FocusEvent event) {
        if (getFile().getFile() != null && getFile().isSaved() &&
            getFile().getFile().lastModified() > getFile().getLastModified()) {
          if (JOptionPane.showConfirmDialog(getEditors(), 
              "The file '" + getFile().getPath() + 
              "'\nhas been changed on the file system.\n" +
              "Do you want to replace editor contents with\n" +
              "these changes?", "File changed",
              JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
            try {
              loadFile();
              undoManager.discardAllEdits();              
              getFile().setLastModified();
              setActions();
            }
            catch (FileException ex) {
              JOptionPane.showMessageDialog(getEditors(), ex.getMessage(), 
                  "Recover error", JOptionPane.ERROR_MESSAGE);
            }
          }
          else {
            getFile().setUnsaved();
          }
        }
      }

      public void focusLost(FocusEvent event) {        
      }
    });    
  }
  
  protected Editor(File file, EditorsTabbedPane tabEditors, ConsolesTabbedPane tabConsoles) throws FileException {
    this(new EditorFile(file), tabEditors, tabConsoles);
  }
  
  protected Editor(String filename, EditorsTabbedPane tabEditors, ConsolesTabbedPane tabConsoles) throws FileException {
    this(filename != null ? new File(filename) : null, tabEditors, tabConsoles);
  }
  
  public void dispose() {
    if (timer != null) {
      timer.cancel();
      timer.purge();
      timer = null;
    }
  }
  
  protected void finalize() throws Throwable {
    dispose();
    super.finalize();
  }
    
//  protected void loadFile() throws FileException {
//    StringBuffer contents = new StringBuffer();
//    if (editorFile != null && editorFile.getFile() != null && 
//        !editorFile.getFile().getPath().startsWith("script")) {
//      if (!editorFile.getFile().exists()) {
//        throw new FileException("File " + editorFile.getFile().getName() + " not exists.");
//      }
//      if (!editorFile.getFile().canRead()) {
//        throw new FileException("Cannot read file " + editorFile.getFile().getName());
//      }
//      BufferedReader input = null;
//      try {
//        input = new BufferedReader(new FileReader(editorFile.getFile()));
//        String line = null;
//        while ((line = input.readLine()) != null) {
//          contents.append(line);
//          contents.append(System.getProperty("line.separator"));
//        }
//        setText(contents.toString().substring(0, 
//            contents.toString().lastIndexOf(System.getProperty("line.separator"))));
//        if (undoManager != null) {
//          undoManager.discardAllEdits();
//        }
//        setModified(false);
//      }
//      catch(IOException ex) {
//        throw new FileException(ex);
//      }
//      finally {
//        if (input != null) {
//          try {
//            input.close();
//          }
//          catch (IOException ignored) {
//          }
//        }
//      }
//    }
//  }
  
  protected void loadFile() throws FileException {
    if (editorFile != null && editorFile.getFile() != null) {
      if (editorFile.getFile().getPath().startsWith("script") && !editorFile.getFile().exists()) {
        return;
      }
      if (!editorFile.getFile().exists()) {
        throw new FileException("File " + editorFile.getFile().getName() + " not exists.");
      }
      if (!editorFile.getFile().canRead()) {
        throw new FileException("Cannot read file " + editorFile.getFile().getName());
      }
      Reader input = null;
      try {        
        read((input = new FileReader(editorFile.getFile())), editorFile.getFile());
        if (undoManager != null) {
          undoManager.discardAllEdits();
        }
        setModified(false);
      }
      catch(IOException ex) {
        throw new FileException(ex);
      }
      finally {
        if (input != null) {
          try {
            input.close();
          }
          catch (IOException ignored) {
          }
        }
      }
    }
  }
  
  public boolean isEmpty() {
    return getDocument().getLength() == 0;
  }
    
  public boolean isModified() {
    return modified;
  }
  
  public void setModified(boolean modified) {
    if (this.modified != modified) {
      this.modified = modified;
      if (config.isAutoSave()) {
        if (modified) {
          if (autoSaveTask == null) {
            autoSaveTask = new AutoSaveTask(this, config.getAutoSavePath());
            timer.schedule(autoSaveTask,  config.getAutoSaveTime(), config.getAutoSaveTime());
          }
        }
        else {
          if (autoSaveTask != null) {
            autoSaveTask.cancel();
            autoSaveTask = null;
          }
        }
      }
      Utilities.setStatusBarText(this, StatusBar.CELL_MODIFIED, modified ? "Modified" : "");
      fireData.onData(new EditorStateChanged(this, modified));
    }
    changed = modified;
  }
  
  protected EditorsTabbedPane getEditors() {
    return tabEditors;
  }
  
  public EditorFile getFile() {
    return editorFile;
  }
  
  public String getPath() {
    return getPath(false);
  }
  
  public String getPath(boolean show) {
    return editorFile != null ? (show && modified ? "*" : "" ) + editorFile.getPath() : "";
  }
  
  public String getTitle() {
    return " - " + getPath(true);            
  }
  
  public String getName() {
    return editorFile != null ? editorFile.getName() : "";
  }
  
  public String getName(boolean show) {
    return editorFile != null ? (show && modified ? "*" : "" ) + editorFile.getName() : "";
  }
  
  public String getTooltip() {
    return  editorFile != null ? 
        "<html><table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td align=\"right\">File:</td><td>" + editorFile.getPath() + "</td></tr>" + 
        (modified ? "<tr><td align=\"right\">Status:</td><td>Modified</td></tr>" : "") +
            "</table></html>" : null;
  }
  
  public void setFilename(String filename) {
    if (this.editorFile == null) {
      this.editorFile = new EditorFile(new File(filename));
    }
    else {
      this.editorFile.setFile(filename);
    }
    fireData.onData(new FilenameChanged(this, this.editorFile.getPath()));
  }

  public void onData(DataEvent<?> event) {
    if (event != null) {
      if (event.getSource() instanceof JPanel) {
        if (event instanceof TabCountChanged) {
          JPopupMenu popupMenu = getComponentPopupMenu();
          if (popupMenu.getComponentCount() > 0) {
            for (int count = 0; count < popupMenu.getComponentCount(); count++) {
              if (popupMenu.getComponent(count) instanceof ActionMenuItem) {
                ActionMenuItem item = (ActionMenuItem)popupMenu.getComponent(count);
                if (item.getAction() instanceof CloseAllAction) {
                  item.getAction().setEnabled(((TabCountChanged)event).getData() > 1);
                }
                if (item.getAction().getClass() == SaveAllAction.class) {
                  item.getAction().setEnabled(
                      item.getAction().isEnabled() && ((TabCountChanged)event).getData() > 1);
                }
              }
            }
          }
        }        
      }
    }
  }
  
  public void changedUpdate(DocumentEvent event) {    
  }

  public void insertUpdate(DocumentEvent event) {
    setModified(true);    
    setActions();    
  }

  public void removeUpdate(DocumentEvent event) {
    setModified(true);
    setActions();
  }
  
  public void setActions() {
    JPopupMenu popupMenu = getComponentPopupMenu();
    if (popupMenu != null && popupMenu.getComponentCount() > 0) {
      for (int count = 0; count < popupMenu.getComponentCount(); count++) {
        if (popupMenu.getComponent(count) instanceof ActionMenuItem) {
          ActionMenuItem item = (ActionMenuItem)popupMenu.getComponent(count);
          if (item.getAction() instanceof RunAction || 
              item.getAction() instanceof RunLineAction ||
              item.getAction() instanceof CancelRunAction) {
            item.getAction().setEnabled(getDocument().getLength() != 0 && getConnection() != null);
          }
          
          if (item.getAction() instanceof BaseKit.SelectAllAction || 
              item.getAction() instanceof BaseKit.SelectLineAction ||
              item.getAction() instanceof ExtKit.GotoAction ||
              item.getAction() instanceof ExtKit.ReplaceAction) {
            item.getAction().setEnabled(getDocument().getLength() != 0);        
          }
          if (item.getAction() instanceof ActionFactory.UndoAction) {
            item.getAction().setEnabled(undoManager.canUndo());
          }
          if (item.getAction() instanceof ActionFactory.RedoAction) {
            item.getAction().setEnabled(undoManager.canRedo());
          }
          if (item.getAction().getClass() == SaveAction.class) {
            item.getAction().setEnabled(isModified());
          }
          if (tabEditors != null && item.getAction().getClass() == SaveAllAction.class) {
            boolean edited = false;
            for (int tab = 0; tab < tabEditors.getTabCount(); tab++) {
              Editor editor = tabEditors.getEditor(tab);
              if (editor.isModified()) {
                edited = true;
                break;
              }
            }
            item.getAction().setEnabled(edited);
          }
          if (tabEditors != null && item.getAction() instanceof CloseAllAction) {
            item.getAction().setEnabled(tabEditors.getTabCount() != 0);
          }
        }
      }
    }
  }

  public Icon getIcon() {
    return editorFile == null || editorFile.getFile() == null ? IconsItem.ICON_BLANK :
      getName().endsWith(".q") || getName().endsWith(".k") ? new ImageIcon(IconsItem.IMAGE_SIG_FILE) : 
        getName().endsWith(".xml") ? new ImageIcon(IconsItem.IMAGE_XML_FILE) :
          getName().endsWith(".xls") ? new ImageIcon(IconsItem.IMAGE_XLS_FILE) :
            getName().endsWith(".csv") ? new ImageIcon(IconsItem.IMAGE_CSV_FILE) :
              getName().endsWith(".txt") ? new ImageIcon(IconsItem.IMAGE_TXT_FILE) :
                getName().endsWith(".ini") || getName().endsWith(".init") || getName().endsWith(".properties") ||
                getName().endsWith(".conf") || getName().endsWith(".config")? new ImageIcon(IconsItem.IMAGE_CONF_FILE) : 
                  new ImageIcon(IconsItem.IMAGE_FILE);
  }
  
  public KdbService getConnection() {
    return null;
  }
}
