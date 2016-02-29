package tszielin.qlab.action.editor;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.JTextComponent;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.SystemUtils;
import org.bounce.text.LineNumberMargin;
import org.bounce.text.xml.XMLFoldingMargin;
import org.netbeans.editor.BaseTextUI;
import org.netbeans.editor.EditorUI;
import org.netbeans.editor.Utilities;
import org.netbeans.editor.ext.ExtUtilities;

import tszielin.qlab.component.editor.Editor;
import tszielin.qlab.component.editor.PlainEditor;
import tszielin.qlab.component.editor.QEditor;
import tszielin.qlab.component.editor.XMLEditor;
import tszielin.qlab.component.filechooser.QFileFilter;
import tszielin.qlab.component.pane.ConsolesTabbedPane;
import tszielin.qlab.component.pane.EditorsTabbedPane;
import tszielin.qlab.config.AppConfig;
import tszielin.qlab.config.ProjectConfig;
import tszielin.qlab.config.data.ConnectionStatus;
import tszielin.qlab.config.data.EditorFile;
import tszielin.qlab.error.FileException;
import tszielin.qlab.error.StudioException;
import tszielin.qlab.util.action.ActionBase;
import tszielin.qlab.util.error.ConfigException;
import tszielin.qlab.util.image.IconsItem;

public class OpenFileAction extends ActionBase {
  private static final long serialVersionUID = 8296770312518618444L;
  private EditorsTabbedPane tabEditors;
  private ConsolesTabbedPane tabConsoles;
  private AppConfig config;
  private static int id;
  
  protected OpenFileAction(String caption, char mnemonic, Icon icon, KeyStroke key,
      String toolTip, String hint) {
    super(caption, mnemonic, icon, key, toolTip, hint);
  }
  
  protected OpenFileAction(EditorsTabbedPane tabEditors, ConsolesTabbedPane tabConsoles,
      String caption, char mnemonic, Icon icon, KeyStroke key, String toolTip, String hint) {
    this(caption, mnemonic, icon, key, toolTip, hint);
    this.tabEditors = tabEditors;
    this.tabConsoles = tabConsoles;
    try {
      this.config = AppConfig.getConfig();
    }
    catch (StudioException ignored) {
    }
  }

  public OpenFileAction(EditorsTabbedPane tabEditors, ConsolesTabbedPane tabConsoles) {
    this(tabEditors, tabConsoles, "Open", 'O', IconsItem.ICON_FILE_OPEN, 
        KeyStroke.getKeyStroke("control O"), "Open file", "Open file and show it in new editor window.");
  }

  public void actionPerformed(ActionEvent event) {
    JFileChooser chooser = new JFileChooser();
    chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    chooser.setMultiSelectionEnabled(true);
    FileFilter qfilter = new QFileFilter();
    chooser.addChoosableFileFilter(qfilter);
    chooser.setFileFilter(qfilter);

    String filename = config.getCurrentPath();
    chooser.setCurrentDirectory(new File(filename != null ? filename : System.getProperty("user.home")));
    if (chooser.showOpenDialog(SwingUtilities.windowForComponent(tabEditors)) == JFileChooser.APPROVE_OPTION) {
      File files[] = chooser.getSelectedFiles();
      if (files != null && files.length > 0) {
        try {
          config.setCurrentPath(files[0].getParent());
        }
        catch (ConfigException ignored) {
        }
        try {
          for (File file : files) {
            initDocument(file.getPath());
          }
        }
        catch (FileException ex) {
          JOptionPane.showMessageDialog(getComponent(), ex.getMessage(), "Open file error",
              JOptionPane.ERROR_MESSAGE);
        }
      }
    }
  }
  
  public void initDocument(String pathname) throws FileException {
    initDocument(pathname, false);
  }
  
  public void initDocument(String pathname, boolean opened) throws FileException {
    if (pathname == null || pathname.trim().length() == 0) {
//      JOptionPane.showMessageDialog(this.getComponent(), "File name cannot be null or empty");
      return;
    }
    initDocument(opened ? config.getOpenedFile(pathname) : new EditorFile(new File(pathname)));
  }
  
  public void initDocument(EditorFile file) throws FileException {
    EditorFile editorFile = file;
    if (tabEditors != null) {
      if (tabEditors.getTabCount() > 0) {
        if (editorFile != null) {
          for (int count = 0; count < tabEditors.getTabCount(); count++) {
            Editor editor = tabEditors.getEditor(count);
            if (editor != null && editor.getFile() != null &&             
                FilenameUtils.separatorsToUnix(editor.getFile().getPath()).equalsIgnoreCase(
                    FilenameUtils.separatorsToUnix(editorFile.getFile().getPath()))) {
              tabEditors.setSelectedIndex(count);
              if (editor instanceof QEditor && file.getConnection() != null && 
                  (editor.getConnection() == null || 
                      editor.getConnection().getStatus() != ConnectionStatus.CONNECTED)) {
                ((QEditor)editor).setConnection(file.getConnection());
              }
              return;
            }
          }
        }
      }

      if (editorFile == null || editorFile.getPath().trim().length() == 0) {
        if (tabEditors.getTabCount() == 0) {
          editorFile = new EditorFile(new File("script0.q"));
        }
        else {
          editorFile = new EditorFile(new File("script" + String.valueOf(++id) + ".q"));
        }
      }
      
      
      java.util.List<String> extensions = null; 
      try {
        extensions = ProjectConfig.getConfig().getExtensions();
      }
      catch (StudioException ignored) {
      }
      
      String extension = editorFile != null && editorFile.getFile() != null ?
          FilenameUtils.getExtension(editorFile.getName()) : null;
      if (extensions == null || extension == null || !extensions.contains(extension)) {
        JOptionPane.showMessageDialog(getComponent(), 
            "Cannot open file which is not defined on extension list", 
            "Open file error",
            JOptionPane.INFORMATION_MESSAGE);
        return;
      }
      

      if (extension.equalsIgnoreCase("xls") || extension.equalsIgnoreCase(".xlsx") ||
          extension.equalsIgnoreCase("xlsm") || extension.equalsIgnoreCase("xlsb") ||
          extension.equalsIgnoreCase("mht") || extension.equalsIgnoreCase("mhtml") ||
          extension.equalsIgnoreCase("doc") || extension.equalsIgnoreCase("docx") ||
          extension.equalsIgnoreCase("docm") || extension.equalsIgnoreCase("doc") ||
          extension.equalsIgnoreCase("dot") || extension.equalsIgnoreCase("dotx") ||
          extension.equalsIgnoreCase("docm")) {
        try {
          Desktop.getDesktop().open(editorFile.getFile());
        }
        catch (IOException ex) {
          JOptionPane.showMessageDialog(getComponent(), ex.getMessage(), "Open file error",
              JOptionPane.ERROR_MESSAGE);
        }
        return;
      }
      
      if ((SystemUtils.IS_OS_UNIX && editorFile.getFile().canExecute()) || ( 
          SystemUtils.IS_OS_WINDOWS && (extension.equalsIgnoreCase("exe") || 
          extension.equalsIgnoreCase("com") || extension.equalsIgnoreCase("dll")))) {
        try {
          Desktop.getDesktop().open(editorFile.getFile());
        }
        catch (IOException ex) {
          JOptionPane.showMessageDialog(getComponent(), ex.getMessage(), "Open file error",
              JOptionPane.ERROR_MESSAGE);
        }
        return;
      }
      
      final Editor editor = 
        extension.equalsIgnoreCase("q") ||extension.equalsIgnoreCase("k") ? 
          new QEditor(editorFile, tabEditors, tabConsoles) :
            extension.equalsIgnoreCase("xml") ?
                new XMLEditor(editorFile, tabEditors, tabConsoles) :
                  new PlainEditor(editorFile, tabEditors, tabConsoles);        
      final Component component = editor.getUI() instanceof BaseTextUI ? 
          ExtUtilities.getExtEditorUI(editor).getExtComponent() : new JScrollPane(editor);
      if (component instanceof JScrollPane) {
        ((JScrollPane)component).putClientProperty(JTextComponent.class, editor);
        if (editor instanceof XMLEditor || editor instanceof PlainEditor) {
          if (editor instanceof PlainEditor) {
            ((JScrollPane)component).addComponentListener(new ComponentAdapter() {
              @Override
              public void componentResized(ComponentEvent event) {
                editor.setSize(new Dimension(((JScrollPane)component).getWidth() - 15,
                    ((JScrollPane)component).getHeight() - 15));
              }
            });
            ((JScrollPane)component).setSize(tabEditors.getSize());
          }
          
          try {            
            JPanel rowHeader = new JPanel(new BorderLayout());
            if (editor instanceof XMLEditor) {
              rowHeader.add(new XMLFoldingMargin(editor), BorderLayout.EAST);
            }
            rowHeader.add(new LineNumberMargin(editor), BorderLayout.WEST);
            ((JScrollPane)component).setRowHeaderView(rowHeader);
          }
          catch (IOException ignored) {
          }
        }
      }
      if (ExtUtilities.getExtEditorUI(editor) instanceof EditorUI) {
        ((EditorUI)ExtUtilities.getExtEditorUI(editor)).installUI(editor);
      }
      if (editor instanceof QEditor) {
        if (Utilities.getEditorUI(editor) != null && Utilities.getEditorUI(editor).getGlyphGutter() != null) {
          Utilities.getEditorUI(editor).getGlyphGutter().setDebuger(false);
        }
      }
      tabEditors.addTab(editor.getFile().getName() + (editor instanceof QEditor ? " (not connected)" : ""), editor.getIcon(),
          component);
      tabEditors.setSelectedIndex(tabEditors.getTabCount() - 1);
      if (tabEditors.getTabCount() > 9) {
        // SwingUtilities.invokeLater(new Runnable() {
        // public void run() {
        Action action = tabEditors.getActionMap().get("scrollTabsForwardAction");
        if (action != null) {
          action.actionPerformed(new ActionEvent(tabEditors, ActionEvent.ACTION_PERFORMED, ""));
        }
        // }
        // });
      }
      if (component != null) {
        component.requestFocus();
      }
    }
  }
  
  protected Component getComponent() {
    return tabEditors;
  }
}
