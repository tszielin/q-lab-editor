/*
 * Studio for kdb+ by Charles Skelton is licensed under a Creative Commons
 * Attribution-Noncommercial-Share Alike 3.0 Germany License
 * http://creativecommons.org/licenses/by-nc-sa/3.0 except for the netbeans components which retain
 * their original copyright notice
 */

package studio.ui;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.ResourceBundle;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.apache.commons.lang.SystemUtils;
import org.netbeans.editor.ActionFactory;
import org.netbeans.editor.BaseKit;
import org.netbeans.editor.BaseSettingsInitializer;
import org.netbeans.editor.LocaleSupport;
import org.netbeans.editor.Settings;
import org.netbeans.editor.example.QKit;
import org.netbeans.editor.ext.ExtKit;
import org.netbeans.editor.ext.ExtSettingsInitializer;
import org.netbeans.editor.ext.q.QSettingsInitializer;

import studio.utils.OSXAdapter;
import tszielin.qlab.action.WindowsListAction;
import tszielin.qlab.action.editor.CloseAction;
import tszielin.qlab.action.editor.CloseAllAction;
import tszielin.qlab.action.editor.NewFileAction;
import tszielin.qlab.action.editor.OpenFileAction;
import tszielin.qlab.action.editor.SaveAction;
import tszielin.qlab.action.editor.SaveAllAction;
import tszielin.qlab.action.editor.SaveAsAction;
import tszielin.qlab.action.help.AboutAction;
import tszielin.qlab.action.help.CheckUpdate;
import tszielin.qlab.action.help.KeyListAction;
import tszielin.qlab.action.help.VisitKxAction;
import tszielin.qlab.action.help.VisitWroclawAction;
import tszielin.qlab.action.kdb.CancelRunAction;
import tszielin.qlab.action.kdb.RunAction;
import tszielin.qlab.action.kdb.RunLineAction;
import tszielin.qlab.action.project.AddProjectAction;
import tszielin.qlab.action.setting.ConnectionsSettingsAction;
import tszielin.qlab.action.setting.ConsoleSettingsAction;
import tszielin.qlab.action.setting.EditorSettingsAction;
import tszielin.qlab.action.setting.GlobalSettingsAction;
import tszielin.qlab.action.setting.LookAndFeelAction;
import tszielin.qlab.action.setting.ProjectSettingsAction;
import tszielin.qlab.action.setting.TokensSettingsAction;
import tszielin.qlab.component.AppMenuBar;
import tszielin.qlab.component.ToolBarActions;
import tszielin.qlab.component.button.DropDownButton;
import tszielin.qlab.component.editor.Editor;
import tszielin.qlab.component.menu.ActionMenu;
import tszielin.qlab.component.pane.CloseTabbedPane;
import tszielin.qlab.component.pane.ConsolesTabbedPane;
import tszielin.qlab.component.pane.EditorsTabbedPane;
import tszielin.qlab.component.pane.ToolsPanel;
import tszielin.qlab.component.tree.KdbServicesTree;
import tszielin.qlab.component.tree.ProjectTree;
import tszielin.qlab.component.tree.model.FileTreeModel;
import tszielin.qlab.component.tree.model.KdbServicesTreeModel;
import tszielin.qlab.component.tree.model.TreeModelSupport;
import tszielin.qlab.component.tree.node.HostTreeNode;
import tszielin.qlab.config.AppConfig;
import tszielin.qlab.config.AppInformation;
import tszielin.qlab.config.ProjectConfig;
import tszielin.qlab.config.data.EditorFile;
import tszielin.qlab.error.FileException;
import tszielin.qlab.error.StudioException;
import tszielin.qlab.event.EditorClosed;
import tszielin.qlab.event.TabCountChanged;
import tszielin.qlab.event.TabIndexChanged;
import tszielin.qlab.listener.ActionHintsListener;
import tszielin.qlab.listener.ProjectTreeModelListener;
import tszielin.qlab.renderer.KdbServicesTreeCellRender;
import tszielin.qlab.renderer.ProjectTreeCellRender;
import tszielin.qlab.util.action.ActionBase;
import tszielin.qlab.util.component.menu.ActionMenuItem;
import tszielin.qlab.util.error.ConfigException;
import tszielin.qlab.util.event.DataEvent;
import tszielin.qlab.util.image.IconsItem;
import tszielin.qlab.util.listener.DataListener;

public class Studio extends JFrame implements DataListener, WindowStateListener {
  private static final long serialVersionUID = -7794624558358585664L;
  
  private static class StudioLocalizer implements LocaleSupport.Localizer {
    private ResourceBundle bundle;

    public StudioLocalizer(String bundleName) {
      bundle = ResourceBundle.getBundle(bundleName);
    }

    public String getString(String key) {
      return bundle.getString(key);
    }
  }

  private ToolBarActions appToolbar;
  private JSplitPane splitViewer;
  private JSplitPane splitTopViewer;
  private EditorsTabbedPane tabEditors;
  private ToolsPanel tools;
  private ConsolesTabbedPane tabConsoles;
  private JTree treeConnections;
  private JTree treeProjects;

  private AppConfig studioConfig;
  
  public Studio(AppConfig config, String... args) {
    super(AppInformation.getInformation().getTitle());
    setIconImage(IconsItem.IMAGE_APP);
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);        
    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    
    setSize((int)(0.9 * screenSize.width), (int)(0.9 * screenSize.height));
    setLocation(((int)Math.max(0, (screenSize.width - getWidth()) / 2.0)),
        (int)(Math.max(0, (screenSize.height - getHeight()) / 2.0)));    
    
    this.studioConfig = config;   
    
    if (studioConfig.isMaximized()) {
      setExtendedState(JFrame.MAXIMIZED_BOTH);  //Maximizing the frame
    }
    addWindowStateListener(this);
    
    registerForMacOSXEvents();    

    splitViewer = new JSplitPane();
    splitTopViewer = new JSplitPane();

    tabConsoles = new ConsolesTabbedPane();
    tabConsoles.addFireDataListener(this);
    tabEditors = new EditorsTabbedPane(tabConsoles);
    tabEditors.addFireDataListener(this);
    if (tabConsoles instanceof DataListener) {
      tabEditors.addFireDataListener((DataListener)tabConsoles);
    }
    tools = new ToolsPanel();

    splitViewer.setOrientation(JSplitPane.VERTICAL_SPLIT);
    splitViewer.setDividerSize(0);
    splitViewer.setTopComponent(splitTopViewer);
    splitViewer.setBottomComponent(tabConsoles);
    splitViewer.setOneTouchExpandable(true);

    splitTopViewer.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
    splitTopViewer.setDividerSize(9);
    splitTopViewer.setTopComponent(tools);
    splitTopViewer.setBottomComponent(tabEditors);
    splitTopViewer.setOneTouchExpandable(true);

    createConnectionsTree();
    createProjectsTree();
    rebuildMenus(null);
   
    getContentPane().add(splitViewer, BorderLayout.CENTER);    

    splitViewer.addComponentListener(new ComponentAdapter() {
      @Override
      public void componentResized(ComponentEvent event) {
        if (event.getComponent() instanceof JSplitPane) {
          if (((JSplitPane)event.getComponent()).getDividerSize() == 0) {
            ((JSplitPane)event.getComponent()).setDividerLocation(1d);
          }
        }
      }
    });
    
    this.setVisible(true);
    splitTopViewer.setDividerLocation(.2d);
    splitViewer.setDividerLocation(1d);
    
    NewFileAction newAction = null;
    OpenFileAction openAction = null;
    ToolBarActions toolbar = appToolbar != null ? appToolbar.getToolBar("File") : null;
    if (toolbar != null) {
      for (int count = 0; count < toolbar.getComponentCount(); count++) {
        if (toolbar.getComponent(count) instanceof JButton) {
          if (((JButton)toolbar.getComponent(count)).getAction().getClass() == OpenFileAction.class) {
            openAction = (OpenFileAction)((JButton)toolbar.getComponent(count)).getAction();
          }
          else {
            if (((JButton)toolbar.getComponent(count)).getAction().getClass() == NewFileAction.class) {
              newAction = (NewFileAction)((JButton)toolbar.getComponent(count)).getAction();
            }
          }
          if (openAction != null && newAction != null) {
            break;
          }
        }
      }
    }
    
    EditorFile last = null;
    if (config.getLastFiles() != null && !config.getLastFiles().isEmpty()) {
      for (EditorFile file : config.getLastFiles()) {
        if (file.isActive()) {
          last = file;
        }
        try {
          openAction.initDocument(file);
        }
        catch (FileException ex) {
          JOptionPane.showMessageDialog(this, ex.getMessage(), "File", JOptionPane.WARNING_MESSAGE);
        }
      }
    }
    else {
      if (args == null || args.length == 0) {
        newAction.actionPerformed(null);
      }
    }
    if (args != null && args.length > 0) {
      for (String arg : args) {
        File file = new File(arg);
        if (file.exists() && file.canRead()) {
          try {
            openAction.initDocument(new EditorFile(file));
          }
          catch (FileException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "File", JOptionPane.WARNING_MESSAGE);
          }
        }
      }
    }
    
    if (tabEditors.getTabCount() > 0) {
      if (last == null) {
        tabEditors.setSelectedIndex(0);
      }
      else {
        for (int count = 0; count < tabEditors.getTabCount(); count++) {
          if (tabEditors.getEditor(count).getFile().equals(last)) {
            tabEditors.setSelectedIndex(count);
          }
        }
      }
      tabEditors.getEditor().requestFocusInWindow();
    }
        
    ActionBase action = new ActionBase(null, (char)0, null, null, null, null) {
      private static final long serialVersionUID = -1752094455650906542L;

      @Override
      public void actionPerformed(ActionEvent event) {
        treeConnections.requestFocus();        
      }      
    };
    splitViewer.registerKeyboardAction(action, KeyStroke.getKeyStroke("F5"), JComponent.WHEN_IN_FOCUSED_WINDOW);
    splitViewer.registerKeyboardAction(action, KeyStroke.getKeyStroke("control F5"), JComponent.WHEN_IN_FOCUSED_WINDOW);
    
    action = new ActionBase(null, (char)0, null, null, null, null) {
      private static final long serialVersionUID = -1752094455650906542L;

      @Override
      public void actionPerformed(ActionEvent event) {
        if (tabEditors.getTabCount() > 0) {
          tabEditors.requestFocus();        
          tabEditors.getEditor().requestFocus();
        }
      }      
    };
    splitViewer.registerKeyboardAction(action, KeyStroke.getKeyStroke("F3"), JComponent.WHEN_IN_FOCUSED_WINDOW);
    splitViewer.registerKeyboardAction(action, KeyStroke.getKeyStroke("control F3"), JComponent.WHEN_IN_FOCUSED_WINDOW);
    
    action = new ActionBase(null, (char)0, null, null, null, null) {
      private static final long serialVersionUID = -7472441241172668338L;

      @Override
      public void actionPerformed(ActionEvent event) {
        treeProjects.requestFocus();        
      }      
    };
    splitViewer.registerKeyboardAction(action, KeyStroke.getKeyStroke("F6"), JComponent.WHEN_IN_FOCUSED_WINDOW);
    splitViewer.registerKeyboardAction(action, KeyStroke.getKeyStroke("control F6"), JComponent.WHEN_IN_FOCUSED_WINDOW);
    
    action = new ActionBase(null, (char)0, null, null, null, null) {
      private static final long serialVersionUID = -1752094455650906542L;

      @Override
      public void actionPerformed(ActionEvent event) {
        if (tabConsoles.getTabCount() > 0) {          
          tabConsoles.requestFocus();   
          if (tabConsoles.getSelectedIndex() != -1) {
            tabConsoles.getComponentAt(tabConsoles.getSelectedIndex()).requestFocus();
          }
        }
      }      
    };
    splitViewer.registerKeyboardAction(action, KeyStroke.getKeyStroke("F4"), JComponent.WHEN_IN_FOCUSED_WINDOW);
    splitViewer.registerKeyboardAction(action, KeyStroke.getKeyStroke("control F4"), JComponent.WHEN_IN_FOCUSED_WINDOW);
    
    action = new WindowsListAction(tabEditors);
    splitViewer.registerKeyboardAction(action, action.getAccelerator(), JComponent.WHEN_IN_FOCUSED_WINDOW);
  }

  @Override
  protected void processWindowEvent(WindowEvent event) {
    switch (event.getID()) {
      case WindowEvent.WINDOW_CLOSING:
        if (tabEditors != null && tabEditors.getTabCount() > 0) {
          int current = tabEditors.getSelectedIndex();
          boolean close = true;          
          for (int count = 0; close & count < tabEditors.getTabCount(); count++) {
            Editor editor = tabEditors.getEditor(count);
            if (editor != null) {
              if (editor.getFile() != null) {
                editor.getFile().setActive(count == current);
              }
              if (editor.isModified() && !editor.isEmpty()) {
                tabEditors.setSelectedIndex(count);
                close &= tabEditors.closeEditor(editor);
              }
            }
          }
          if (!close) {
            tabEditors.setSelectedIndex(current);
            tabEditors.getEditor(current).requestFocus();
            // getToolkit().getSystemEventQueue().postEvent(new WindowEvent(this,
            // WindowEvent.WINDOW_CLOSING));
            return;
          }
          else {
            for (int count = 0; count < tabEditors.getTabCount(); count++) {
              if (tabEditors.getEditor(count) != null) {
                tabEditors.getEditor(count).setModified(false);
              }
            }
          }
        }

        java.util.List<EditorFile> files = tabEditors.saveFiles();
        if (files == null) {
          return;
        }
        try {
          studioConfig.setLastFiles(files);
        }
        catch (ConfigException ex) {
          ex.printStackTrace();
        }
        super.processWindowEvent(event);
        System.exit(0);
        break;
      case WindowEvent.WINDOW_ACTIVATED:
        if (tabEditors != null && tabEditors.getTabCount() > 0 && tabEditors.getEditor() != null) {
          tabEditors.getEditor().requestFocusInWindow();
        }
        break;
    }
    super.processWindowEvent(event);
  }
  
  public void windowStateChanged(WindowEvent event) {
    if (event.getID() == WindowEvent.WINDOW_STATE_CHANGED) {
      try {
        studioConfig.setMaximized((event.getNewState() & JFrame.MAXIMIZED_BOTH) == JFrame.MAXIMIZED_BOTH);
      }
      catch (ConfigException ignored) {
      }
    }
  }

//  private void exportAsExcel(final String filename) {
//    new ExcelExporter().exportTableX(this, table, new File(filename), false);
//  }
//
//  private void exportAsDelimited(final TableModel model, final String filename, final char delimiter) {
//    final String message = "Exporting data to " + filename;
//
//    final String note = "0% complete";
//
//    UIManager.put("ProgressMonitor.progressText", Lm.APP_TITLE);
//
//    final int min = 0;
//    final int max = 100;
//    final ProgressMonitor pm = new ProgressMonitor(this, message, note, min, max);
//    pm.setMillisToDecideToPopup(100);
//    pm.setMillisToPopup(100);
//    pm.setProgress(0);
//
//    Runnable runner = new Runnable() {
//      public void run() {
//        if (filename != null) {
//          String lineSeparator = (String)java.security.AccessController.doPrivileged(new sun.security.action.GetPropertyAction(
//              "line.separator"));
//
//          BufferedWriter fw = null;
//
//          try {
//            fw = new BufferedWriter(new FileWriter(filename));
//
//            for (int col = 0; col < model.getColumnCount(); col++) {
//              if (col > 0)
//                fw.write(delimiter);
//
//              fw.write(model.getColumnName(col));
//            }
//            fw.write(lineSeparator);
//
//            int maxRow = model.getRowCount();
//            int lastProgress = 0;
//
//            for (int r = 1; r <= maxRow; r++) {
//              for (int col = 0; col < model.getColumnCount(); col++) {
//                if (col > 0)
//                  fw.write(delimiter);
//
//                K.KBase o = (K.KBase)model.getValueAt(r - 1, col);
//                if (!o.isNull())
//                  fw.write(o.toString(false));
//              }
//              fw.write(lineSeparator);
//
//              boolean cancelled = pm.isCanceled();
//
//              if (cancelled)
//                break;
//              else {
//                final int progress = (100 * r) / maxRow;
//                if (progress > lastProgress) {
//                  final String note = "" + progress + "% complete";
//                  SwingUtilities.invokeLater(new Runnable() {
//
//                    public void run() {
//                      pm.setProgress(progress);
//                      pm.setNote(note);
//                    }
//                  });
//
//                  Thread.yield();
//                }
//              }
//            }
//
//            fw.close();
//          }
//          catch (FileNotFoundException ex) {
//            ex.printStackTrace(); // To change body of catch statement use Options | File Templates.
//          }
//          catch (IOException ex) {
//            ex.printStackTrace(); // To change body of catch statement use Options | File Templates.
//          }
//          catch (Exception ex) {
//            ex.printStackTrace(); // To change body of catch statement use Options | File Templates.
//          }
//          finally {
//            pm.close();
//          }
//        }
//      }
//    };
//
//    Thread t = new Thread(runner);
//    t.setName("export");
//    t.setPriority(Thread.MIN_PRIORITY);
//    t.start();
//  }
//
//  private void exportAsXml(final TableModel model, final String filename) {
//    final String message = "Exporting data to " + filename;
//
//    final String note = "0% complete";
//
//    UIManager.put("ProgressMonitor.progressText", Lm.APP_TITLE);
//
//    final int min = 0;
//    final int max = 100;
//    final ProgressMonitor pm = new ProgressMonitor(this, message, note, min, max);
//    pm.setMillisToDecideToPopup(100);
//    pm.setMillisToPopup(100);
//    pm.setProgress(0);
//
//    Runnable runner = new Runnable() {
//      public void run() {
//        if (filename != null) {
//          String lineSeparator = (String)java.security.AccessController.doPrivileged(new sun.security.action.GetPropertyAction(
//              "line.separator"));
//
//          BufferedWriter fw = null;
//
//          try {
//            fw = new BufferedWriter(new FileWriter(filename));
//
//            fw.write("<R>");
//
//            int maxRow = model.getRowCount();
//            int lastProgress = 0;
//
//            fw.write(lineSeparator);
//
//            String[] columns = new String[model.getColumnCount()];
//            for (int col = 0; col < model.getColumnCount(); col++)
//              columns[col] = model.getColumnName(col);
//
//            for (int r = 1; r <= maxRow; r++) {
//              fw.write("<r>");
//              for (int col = 0; col < columns.length; col++) {
//                fw.write("<" + columns[col] + ">");
//
//                K.KBase o = (K.KBase)model.getValueAt(r - 1, col);
//                if (!o.isNull())
//                  fw.write(o.toString(false));
//
//                fw.write("</" + columns[col] + ">");
//              }
//              fw.write("</r>");
//              fw.write(lineSeparator);
//
//              boolean cancelled = pm.isCanceled();
//
//              if (cancelled)
//                break;
//              else {
//                final int progress = (100 * r) / maxRow;
//                if (progress > lastProgress) {
//                  final String note = "" + progress + "% complete";
//                  SwingUtilities.invokeLater(new Runnable() {
//
//                    public void run() {
//                      pm.setProgress(progress);
//                      pm.setNote(note);
//                    }
//                  });
//
//                  Thread.yield();
//                }
//              }
//            }
//            fw.write("</R>");
//
//            fw.close();
//          }
//          catch (FileNotFoundException ex) {
//            ex.printStackTrace(); // To change body of catch statement use Options | File Templates.
//          }
//          catch (IOException ex) {
//            ex.printStackTrace(); // To change body of catch statement use Options | File Templates.
//          }
//          catch (Exception ex) {
//            ex.printStackTrace(); // To change body of catch statement use Options | File Templates.
//          }
//          finally {
//            pm.close();
//          }
//        }
//      }
//    };
//
//    Thread t = new Thread(runner);
//    t.setName("export");
//    t.setPriority(Thread.MIN_PRIORITY);
//    t.start();
//  }
//
//  private void exportAsTxt(String filename) {
//    exportAsDelimited(table.getModel(), filename, '\t');
//  }
//
//  private void exportAsCSV(String filename) {
//    exportAsDelimited(table.getModel(), filename, ',');
//  }
//
//  private void export() {
//    JFileChooser chooser = new JFileChooser();
//    chooser.setDialogType(JFileChooser.SAVE_DIALOG);
//    chooser.setDialogTitle("Export result set as");
//    chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
//
//    FileFilter csvFilter = null;
//    FileFilter txtFilter = null;
//    FileFilter xmlFilter = null;
//    FileFilter xlsFilter = null;
//
//    if (table != null) {
//      csvFilter = new FileFilter() {
//        @Override
//        public String getDescription() {
//          return "csv (Comma delimited)";
//        }
//
//        @Override
//        public boolean accept(File file) {
//          if (file.isDirectory() || file.getName().endsWith(".csv"))
//            return true;
//          else
//            return false;
//        }
//      };
//
//      txtFilter = new FileFilter() {
//        @Override
//        public String getDescription() {
//          return "txt (Tab delimited)";
//        }
//
//        @Override
//        public boolean accept(File file) {
//          if (file.isDirectory() || file.getName().endsWith(".txt"))
//            return true;
//          else
//            return false;
//        }
//      };
//
//      xmlFilter = new FileFilter() {
//        @Override
//        public String getDescription() {
//          return "xml";
//        }
//
//        @Override
//        public boolean accept(File file) {
//          if (file.isDirectory() || file.getName().endsWith(".xml"))
//            return true;
//          else
//            return false;
//        }
//      };
//
//      xlsFilter = new FileFilter() {
//        @Override
//        public String getDescription() {
//          return "xls (Microsoft Excel)";
//        }
//
//        @Override
//        public boolean accept(File file) {
//          if (file.isDirectory() || file.getName().endsWith(".xls"))
//            return true;
//          else
//            return false;
//        }
//      };
//
//      chooser.addChoosableFileFilter(csvFilter);
//      chooser.addChoosableFileFilter(txtFilter);
//      chooser.addChoosableFileFilter(xmlFilter);
//      chooser.addChoosableFileFilter(xlsFilter);
//    }
//
//    if (exportFilename != null) {
//      File file = new File(exportFilename);
//      File dir = new File(file.getPath());
//      chooser.setCurrentDirectory(dir);
//      chooser.ensureFileIsVisible(file);
//      if (table != null)
//        if (exportFilename.endsWith(".xls"))
//          chooser.setFileFilter(xlsFilter);
//        else
//          if (exportFilename.endsWith(".csv"))
//            chooser.setFileFilter(csvFilter);
//          else
//            if (exportFilename.endsWith(".xml"))
//              chooser.setFileFilter(xmlFilter);
//            else
//              if (exportFilename.endsWith(".txt"))
//                chooser.setFileFilter(txtFilter);
//    }
//
//    int option = chooser.showSaveDialog(textArea);
//
//    if (option == JFileChooser.APPROVE_OPTION) {
//      File sf = chooser.getSelectedFile();
//      File f = chooser.getCurrentDirectory();
//      String dir = f.getAbsolutePath();
//
//      // Cursor cursor= frame.getCursor();
//
//      try {
//        // frame.setCursor(new java.awt.Cursor(java.awt.Cursor._CURSOR));
//        FileFilter ff = chooser.getFileFilter();
//
//        exportFilename = dir + "/" + sf.getName();
//
//        if (table != null)
//          if (exportFilename.endsWith(".xls"))
//            exportAsExcel(exportFilename);
//          else
//            if (exportFilename.endsWith(".csv"))
//              exportAsCSV(exportFilename);
//            else
//              if (exportFilename.endsWith(".txt"))
//                exportAsTxt(exportFilename);
//              else
//                if (exportFilename.endsWith(".xml"))
//                  exportAsXml(table.getModel(), exportFilename);
//                /*
//                 * else if (exportFilename.endsWith(".res")) { exportAsBin(exportFilename); }
//                 */
//                else
//                  if (ff == csvFilter)
//                    exportAsCSV(exportFilename);
//                  else
//                    if (ff == xlsFilter)
//                      exportAsExcel(exportFilename);
//                    else
//                      if (ff == txtFilter)
//                        exportAsTxt(exportFilename);
//                      else
//                        if (ff == xmlFilter)
//                          exportAsXml(table.getModel(), exportFilename);
//                        /*
//                         * else if( ff == binFilter){ exportAsBin(exportFilename); }
//                         */
//                        else
//                          JOptionPane.showMessageDialog(this,
//                              "You did not specify what format to export the file as.\n Cancelling data export",
//                              Lm.APP_TITLE, JOptionPane.WARNING_MESSAGE);
//        /*
//         * else { exportAsBin(exportFilename); }
//         */
//      }
//      catch (Exception e) {
//        JOptionPane.showMessageDialog(this, 
//            "An error occurred whilst writing the export file.\n Details are: " + e.getMessage(),
//            Lm.APP_TITLE, JOptionPane.ERROR_MESSAGE);
//      }
//      finally {
//        // frame.setCursor(cursor);
//      }
//    }
//  }

  // exportAction = new UserAction("Export...", getImage(Config.imageBase2 + "export2.png"),
  // "Export result set", new Integer(KeyEvent.VK_E), null) {
  // @Override
  // public void actionPerformed(ActionEvent e) {
  // export();
  // }
  // };
  //
  // chartAction = new UserAction("Chart", Util.getImage(Config.imageBase2 + "chart.png"),
  // "Chart current data set", new Integer(KeyEvent.VK_E), null) {
  // @Override
  // public void actionPerformed(ActionEvent e) {
  // new LineChart((KTableModel)table.getModel());
  // // new PriceVolumeChart(table);
  // }
  // };
  //
  // stopAction = new UserAction("Stop", getImage(Config.imageBase2 + "stop.png"), "Stop the query",
  // new Integer(KeyEvent.VK_S), null) {
  // @Override
  // public void actionPerformed(ActionEvent e) {
  // if (worker != null) {
  // // worker.interrupt();
  // worker.cancel(true);
  // stopAction.setEnabled(false);
  // textArea.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
  // }
  // }
  // };
  //
  // openInExcel = new UserAction("Open in Excel", getImage(Config.imageBase + "excel_icon.gif"),
  // "Open in Excel", new Integer(KeyEvent.VK_O), null) {
  // @Override
  // public void actionPerformed(ActionEvent e) {
  // try {
  // File file = File.createTempFile("studioExport", ".xls");
  // new ExcelExporter().exportTableX(frame, table, file, true);
  // }
  // catch (IOException ex) {
  // ex.printStackTrace();
  // }
  // }
  // };
  //
  // executeAction = new UserAction("Execute",
  // Util.getImage(Config.imageBase2 + "table_sql_run.png"),
  // "Execute the full or highlighted text as a query", new Integer(KeyEvent.VK_E),
  // KeyStroke.getKeyStroke(KeyEvent.VK_E, menuShortcutKeyMask)) {
  // @Override
  // public void actionPerformed(ActionEvent e) {
  // executeQuery();
  // }
  // };

  // subscribeAction = new UserAction("Subscribe", getImage(Config.imageBase2 + "feed.png"),
  // "Subscribe to realtime table", WIDTH, null) {
  //
  // @Override
  // public void actionPerformed(ActionEvent e) {
  //
  // if (server != null) {
  // final kx.c c = ConnectionPool.getInstance().leaseConnection(server);
  //
  // try {
  // ConnectionPool.getInstance().checkConnected(c);
  // final JDialog dialog = new JDialog(frame, true);
  // SwingWorker worker = new SwingWorker() {
  //
  // @Override
  // protected Object doInBackground() throws Exception {
  // try {
  // c.k(new K.KCharacterVector(".u.t"));
  // return c.getResponse();
  // }
  // catch (Throwable ex) {
  // throw new Exception(ex);
  // }
  // }
  //
  // @Override
  // protected void done() {
  // dialog.setVisible(false);
  // dialog.dispose();
  // try {
  // Object res = get();
  // if (res instanceof K.KSymbolVector) {
  // subscribeFeed(res, c);
  // }
  // }
  // catch (Throwable ex) {
  // JOptionPane.showMessageDialog(frame, "Nothing to subscribe for!");
  // }
  //
  // }
  // };
  // JProgressBar pb = new JProgressBar();
  // pb.setIndeterminate(true);
  // dialog.add(pb);
  // worker.execute();
  // dialog.pack();
  // dialog.setLocationRelativeTo(null);
  // // the dialog will be visible until the SwingWorker is done
  // dialog.setVisible(true);
  // }
  // catch (Throwable th) {
  //
  // if (c != null) {
  // ConnectionPool.getInstance().freeConnection(server, c);
  // }
  // JOptionPane.showMessageDialog(frame, "An Exception occurred\n\nDetails - \n\n" +
  // th.toString(), "Studio for kdb+", JOptionPane.ERROR_MESSAGE);
  // }
  // }
  // }
  //
  // private void subscribeFeed(Object res, final c c) throws Throwable {
  // K.KSymbolVector tables = (K.KSymbolVector)res;
  // final String table = (String)JOptionPane.showInputDialog(frame,
  // "Select table to subscribe", "Subscribe to table", JOptionPane.YES_NO_OPTION, null,
  // (Object[])tables.getArray(), null);
  // if (table == null) {
  // return;
  // }
  // final JDialog dialog = new JDialog(frame, true);
  // SwingWorker worker = new SwingWorker() {
  //
  // @Override
  // protected Object doInBackground() throws Exception {
  // try {
  // c.k(new K.KCharacterVector(".u.sub[`" + table + ";`]"));
  // K.KBase r = c.getResponse();
  // if (!(r instanceof K.KList)) {
  // System.out.println(r);
  // return null;
  // }
  // System.out.println("subscribed");
  // Object data = ((K.KList)r).at(1);
  // return data;
  // }
  // catch (Throwable ex) {
  // throw new Exception(ex);
  // }
  // }
  //
  // @Override
  // protected void done() {
  // dialog.setVisible(false);
  // dialog.dispose();
  // Object data = null;
  // try {
  // data = get();
  // }
  // catch (InterruptedException ex) {
  // JOptionPane.showMessageDialog(frame, "Error while subscribing!\n" + ex);
  // }
  // catch (ExecutionException ex) {
  // JOptionPane.showMessageDialog(frame, "Error while subscribing!\n" + ex);
  // }
  // if (data != null && FlipTableModel.isTable(data)) {
  // QGrid grid = new QGrid((KBase)data);
  // final SubscribeWorker worker = new SubscribeWorker(grid, c, table);
  // worker.execute();
  // JFrame frm = new JFrame();
  // WindowAdapter closeAdapter = new WindowAdapter() {
  //
  // @Override
  // public void windowClosing(WindowEvent e) {
  // try {
  // worker.cancel(true);
  // c c = worker.getC();
  // c.k(new K.KCharacterVector(".u.del[`" + table + ";.z.w]"));
  // System.out.println("unsubscribed:" + c.getResponse());
  // }
  // catch (Throwable ex) {
  // ex.printStackTrace();
  // }
  // }
  // };
  // frame.addWindowListener(closeAdapter);
  // frm.addWindowListener(closeAdapter);
  // frm.setTitle(table + "@" + server);
  // frm.setIconImage(getImage(Config.imageBase + "32x32/dot-chart.png").getImage());
  // frm.add(grid);
  // frm.pack();
  // frm.setVisible(true);
  // }
  // else {
  // JOptionPane.showMessageDialog(frame, "Error while subscribing!Invalid response.\n");
  // }
  //
  // }
  // };
  //
  // JProgressBar pb = new JProgressBar();
  // pb.setIndeterminate(true);
  // dialog.add(pb);
  // worker.execute();
  // dialog.pack();
  // dialog.setLocationRelativeTo(null);
  // // the dialog will be visible until the SwingWorker is done
  // dialog.setVisible(true);
  //
  // }
  // };
  //
  // aboutAction = new UserAction("About", Util.getImage(Config.imageBase2 + "about.png"),
  // "About Studio for kdb+", new Integer(KeyEvent.VK_E), null) {
  // @Override
  // public void actionPerformed(ActionEvent e) {
  // about();
  // }
  // };
  //
  // exitAction = new UserAction("Exit", getImage(Config.imageBase2 + "blank.png"),
  // "Close this window", new Integer(KeyEvent.VK_X), null) {
  // @Override
  // public void actionPerformed(ActionEvent e) {
  // if (quit())
  // System.exit(0);
  // }
  // };
  //
  // codeKxComAction = new UserAction("code.kx.com", Util.getImage(Config.imageBase2 + "text.png"),
  // "Open code.kx.com", new Integer(KeyEvent.VK_C), null) {
  // @Override
  // public void actionPerformed(ActionEvent e) {
  // try {
  // Desktop.getDesktop().browse(new URI("https://code.kx.com/trac/wiki/Reference/"));
  // }
  // catch (Exception ex) {
  // JOptionPane.showMessageDialog(null, "Error attempting to launch web browser:\n" +
  // ex.getLocalizedMessage());
  // }
  // }
  // };
  // }

//  public void about() {
//    HelpDialog help = new HelpDialog(this, true);
//    Util.centerChildOnParent(help, this);
//    // help.setTitle("About Studio for kdb+");
//    help.pack();
//    help.setVisible(true);
//  }

  public boolean quit() {
    boolean okToExit = true;

    // for (int i = 0; i < objs.length; i++) {
    // Object o = objs[i];
    //
    // if (o instanceof Studio) {
    // if (!((Studio)o).quitWindow())
    // okToExit = false;
    // }
    // else
    // if (o instanceof JFrame) {
    // JFrame f = (JFrame)o;
    // f.setVisible(false);
    //
    // f.dispose();
    // }
    // }

    return okToExit;
  }

  public boolean quitWindow() {
    // if (getModified()) {
    // int choice = JOptionPane.showOptionDialog(frame, "Changes not saved.\nSave now?",
    // "Save changes?", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
    // getImage(Config.imageBase + "32x32/question.png"), null, // use standard button titles
    // null); // no default selection
    //
    // if (choice == 0)
    // try {
    // String filename = (String)textArea.getDocument().getProperty("filename");
    // if (!saveFile(filename, false))
    // // was cancelled so return
    // return false;
    // }
    // catch (Exception e) {
    // return false;
    // }
    // else
    // if ((choice == 2) || (choice == JOptionPane.CLOSED_OPTION))
    // return false;
    // }

    // windowList.remove(this);
    // windowListMonitor.removeEventListener(windowListChangedEventListener);
    // windowListMonitor.fireMyEvent(new WindowListChangedEvent(this));
    this.dispose();
    return true;
  }

//  private void ensureDeiconified(JFrame f) {
//    int state = f.getExtendedState();
//    state = state & ~Frame.ICONIFIED;
//    f.setExtendedState(state);
//    f.show();
//  }
//
//
//  private static class Impl extends FileView implements LocaleSupport.Localizer {
//    // FileView implementation
//    @Override
//    public String getName(File f) {
//      return null;
//    }
//
//    @Override
//    public String getDescription(File f) {
//      return null;
//    }
//
//    @Override
//    public String getTypeDescription(File f) {
//      return null;
//    }
//
//    @Override
//    public Boolean isTraversable(File f) {
//      return null;
//    }
//
//    @Override
//    public Icon getIcon(File f) {
//      if (f.isDirectory())
//        return null;
//      // KitInfo ki = KitInfo.getKitInfoForFile(f);
//      // return ki == null ? null : ki.getIcon();
//      return null;
//    }
//    private ResourceBundle bundle;
//
//    public Impl(String bundleName) {
//      bundle = ResourceBundle.getBundle(bundleName);
//    }
//
//    // Localizer
//    public String getString(String key) {
//      return bundle.getString(key);
//    }
//  }
//  
//  
//  public void update(Observable obs, Object obj) {
//  }
  private static boolean registeredForMaxOSXEvents = false;

  public void registerForMacOSXEvents() {
    if (registeredForMaxOSXEvents)
      return;

    if (SystemUtils.IS_OS_MAC)
      try {
        // Generate and register the OSXAdapter, passing it a hash of all the methods we wish to
        // use as delegates for various com.apple.eawt.ApplicationListener methods
        OSXAdapter.setQuitHandler(new QuitHandler(this), QuitHandler.class.getDeclaredMethod(
            "quit", (Class[])null));
        OSXAdapter.setAboutHandler(new AboutHandler(), AboutHandler.class.getDeclaredMethod(
            "about", (Class[])null));
        registeredForMaxOSXEvents = true;
      }
      catch (Exception ex) {
        System.err.println("Error while loading the OSXAdapter:");
        ex.printStackTrace();
      }
  }

//  public void refreshQuery() {
//    table = null;
//    executeK4Query(lastQuery);
//  }
//
//  public void executeQueryCurrentLine() {
//    executeQuery(getCurrentLineEditorText(textArea));
//  }
//
//  public void executeQuery() {
//    executeQuery(getEditorText(textArea));
//  }

//  private void executeQuery(String text) {
//    table = null;
//
//    if (text == null) {
//      JOptionPane.showMessageDialog(this, "\nNo text available to submit to server.\n\n",
//          Lm.APP_TITLE, JOptionPane.INFORMATION_MESSAGE);
//
//      return;
//    }
//
//    refreshAction.setEnabled(false);
//    stopAction.setEnabled(true);
//    executeAction.setEnabled(false);
//    executeCurrentLineAction.setEnabled(false);
//    exportAction.setEnabled(false);
//    chartAction.setEnabled(false);
//    openInExcel.setEnabled(false);
//
//    executeK4Query(text);
//
//    lastQuery = text;
//  }

//  private String getEditorText(JEditorPane editor) {
//    String text = editor.getSelectedText();
//
//    if (text != null) {
//      if (text.length() > 0)
//        if (text.trim().length() == 0)
//          return null; // selected text is whitespace
//    }
//    else
//      text = editor.getText(); // get the full text then
//
//    if (text != null)
//      text = text.trim();
//
//    if (text.trim().length() == 0)
//      text = null;
//
//    return text;
//  }
//
//  private String getCurrentLineEditorText(JEditorPane editor) {
//    String newLine = "\n";
//    String text = null;
//
//    try {
//      int pos = editor.getCaretPosition();
//      int max = editor.getDocument().getLength();
//
//      if ((max > pos) && (!editor.getText(pos, 1).equals("\n"))) {
//        String toeol = editor.getText(pos, max - pos);
//        int eol = toeol.indexOf('\n');
//
//        if (eol > 0)
//          pos = pos + eol;
//        else
//          pos = max;
//      }
//
//      text = editor.getText(0, pos);
//
//      int lrPos = text.lastIndexOf(newLine);
//
//      if (lrPos >= 0) {
//        lrPos += newLine.length(); // found it so skip it
//        text = text.substring(lrPos, pos).trim();
//      }
//    }
//    catch (BadLocationException e) {
//    }
//
//    if (text != null) {
//      text = text.trim();
//
//      if (text.length() == 0)
//        text = null;
//    }
//
//    return text;
//  }

//  private void processK4Results(K.KBase r) throws c.K4Exception {
////    if (r != null) {
////      exportAction.setEnabled(true);
////
////      if (FlipTableModel.isTable(r)) {
////        QGrid grid = new QGrid(r);
////        table = grid.getTable();
////
////        openInExcel.setEnabled(true);
////        // if(grid.getRowCount()<50000)
////        chartAction.setEnabled(true);
////        // else
////        // chartAction.setEnabled(false);
////
////        TabPanel frame = new TabPanel("Table [" + grid.getRowCount() + " rows] ",
////            getImage(Config.imageBase2 + "table.png"), grid);
////        frame.setTitle("Table [" + grid.getRowCount() + " rows] ");
////        // frame.setBackground( Color.white);
////
////        tabConsoles.addTab(frame.getTitle(), frame.getIcon(), frame.getComponent());
////      }
////      else {
////        chartAction.setEnabled(false);
////        openInExcel.setEnabled(false);
////        LimitedWriter lm = new LimitedWriter(50000);
////        try {
////          r.toString(lm, true);
////        }
////        catch (IOException ex) {
////          ex.printStackTrace();
////        }
////        catch (LimitedWriter.LimitException ex) {
////        }
////
////        JEditorPane pane = new JEditorPane("text/plain", lm.toString());
////        pane.setFont(font);
////
////        // pane.setLineWrap( false);
////        // pane.setWrapStyleWord( false);
////
////        JScrollPane scrollpane = new JScrollPane(pane,
////            ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
////            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
////
////        TabPanel frame = new TabPanel("Console View ", getImage(Config.imageBase2 + "console.png"),
////            scrollpane);
////
////        frame.setTitle("Console View ");
////
////        tabConsoles.addTab(frame.getTitle(), frame.getIcon(), frame.getComponent());
////      }
////    }
////    else {
////      // Log that execute was successful
////    }
//  }
//  Server server = null;
//
//  public void executeK4Query(final String text) {
////    final Cursor cursor = textArea.getCursor();
////
////    textArea.setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));
////    tabConsoles.removeAll();
////    worker = new SwingWorker() {
////      Server s = null;
////      c c = null;
////      K.KBase r = null;
////      Throwable exception;
////      boolean cancelled = false;
////      long execTime = 0;
////
////      public void interrupt() {
////        super.cancel(true);
////
////        cancelled = true;
////
////        if (c != null)
////          c.close();
////        cleanup();
////      }
////
////      @Override
////      public Object doInBackground() {
////        try {
////          this.s = server;
////          c = ConnectionPool.getInstance().leaseConnection(s);
////          ConnectionPool.getInstance().checkConnected(c);
////          c.setFrame(frame);
////          long startTime = System.currentTimeMillis();
////          c.k(new K.KCharacterVector(text));
////          r = c.getResponse();
////          execTime = System.currentTimeMillis() - startTime;
////        }
////        catch (Throwable e) {
////          exception = e;
////        }
////
////        return null;
////      }
////
////      @Override
////      public void done() {
////        if (!cancelled) {
////          if (exception != null)
////            try {
////              throw exception;
////            }
////            catch (IOException ex) {
////              JOptionPane.showMessageDialog(
////                  frame,
////                  "\nA communications error occurred whilst sending the query.\n\nPlease check that the server is running on " +
////                      server.getHost() +
////                      ":" +
////                      server.getPort() +
////                      "\n\nError detail is\n\n" +
////                      ex.getMessage() + "\n\n", "Studio for kdb+", JOptionPane.ERROR_MESSAGE,
////                  getImage(Config.imageBase + "32x32/error.png"));
////            }
////            catch (c.K4Exception ex) {
////              JTextPane pane = new JTextPane();
////              String hint = QErrors.lookup(ex.getMessage());
////              if (hint != null)
////                hint = "\nStudio Hint: Possibly this error refers to " + hint;
////              else
////                hint = "";
////              pane.setText("An error occurred during execution of the query.\nThe server sent the response:\n" +
////                  ex.getMessage() + hint);
////              pane.setForeground(Color.RED);
////
////              JScrollPane scrollpane = new JScrollPane(pane);
////
////              TabPanel frame = new TabPanel("Error Details ", getImage(Config.imageBase2 +
////                  "error.png"), scrollpane);
////              frame.setTitle("Error Details ");
////
////              tabConsoles.addTab(frame.getTitle(), frame.getIcon(), frame.getComponent());
////
////              // tabbedPane.setSelectedComponent(resultsTabbedPane);
////            }
////            catch (java.lang.OutOfMemoryError ex) {
////              JOptionPane.showMessageDialog(
////                  frame,
////                  "\nOut of memory whilst communicating with " +
////                      server.getHost() +
////                      ":" +
////                      server.getPort() +
////                      "\n\nThe result set is probably too large.\n\nTry increasing the memory available to studio through the command line option -J -Xmx512m\n\n",
////                  "Studio for kdb+", JOptionPane.ERROR_MESSAGE, getImage(Config.imageBase +
////                      "32x32/error.png"));
////            }
////            catch (Throwable ex) {
////              String message = ex.getMessage();
////
////              if ((message == null) || (message.length() == 0))
////                message = "No message with exception. Exception is " + ex.toString();
////
////              JOptionPane.showMessageDialog(frame,
////                  "\nAn unexpected error occurred whilst communicating with " + server.getHost() +
////                      ":" + server.getPort() + "\n\nError detail is\n\n" + message + "\n\n",
////                  "Studio for kdb+", JOptionPane.ERROR_MESSAGE, getImage(Config.imageBase +
////                      "32x32/error.png"));
////            }
////          else
////            Utilities.setStatusText(textArea, "Last execution time:" +
////                (execTime > 0 ? execTime : "<1") + " mS");
////          try {
////            processK4Results(r);
////          }
////          catch (Exception e) {
////            JOptionPane.showMessageDialog(frame,
////                "\nAn unexpected error occurred whilst communicating with " + server.getHost() +
////                    ":" + server.getPort() + "\n\nError detail is\n\n" + e.getMessage() + "\n\n",
////                "Studio for kdb+", JOptionPane.ERROR_MESSAGE, getImage(Config.imageBase +
////                    "32x32/error.png"));
////          }
////
////          cleanup();
////        }
////      }
////
////      private void cleanup() {
////        if (c != null)
////          ConnectionPool.getInstance().freeConnection(s, c);
////        // if( c != null)
////        // c.close();
////        c = null;
////
////        textArea.setCursor(cursor);
////
////        stopAction.setEnabled(false);
////        executeAction.setEnabled(true);
////        executeCurrentLineAction.setEnabled(true);
////        refreshAction.setEnabled(true);
////
////        System.gc();
////
////        worker = null;
////      }
////    };
////
////    worker.execute();
//  }
//  private SwingWorker worker;

//  @Override
//  public void windowClosing(WindowEvent e) {
//    System.exit(0);
//  }
//
//  @Override
//  public void windowClosed(WindowEvent e) {
//  }
//
//  @Override
//  public void windowOpened(WindowEvent e) {
//  }
//
//  // ctrl-alt spacebar to minimize window
//  @Override
//  public void windowIconified(WindowEvent e) {
//  }
//
//  @Override
//  public void windowDeiconified(WindowEvent e) {
//  }
//
//  @Override
//  public void windowActivated(WindowEvent e) {
////    this.invalidate();
////    SwingUtilities.updateComponentTreeUI(this);
//  }
//
//  @Override
//  public void windowDeactivated(WindowEvent e) {
//  }

//  public static ImageIcon getImage(String strFilename) {
//    Class thisClass = Studio.class;
//
//    java.net.URL url = null;
//
//    if (strFilename.startsWith("/"))
//      url = thisClass.getResource(strFilename);
//    else
//      // Locate the desired image file and create a URL to it
//      url = thisClass.getResource("/toolbarButtonGraphics/" + strFilename);
//
//    // See if we successfully found the image
//    if (url == null)
//      // System.out.println("Unable to load the following image: " +
//      // strFilename);
//      return null;
//
//    Toolkit toolkit = Toolkit.getDefaultToolkit();
//    Image image = toolkit.getImage(url);
//    return new ImageIcon(image);
//  }

  private void createConnectionsTree() {
    if (treeConnections == null) {
      treeConnections = new KdbServicesTree(tabEditors); 
      tools.setConnectionsView((KdbServicesTree)treeConnections);      
      tabEditors.addFireDataListener((DataListener)treeConnections);
    }
    DefaultMutableTreeNode top = null;
    if (!(treeConnections.getModel() instanceof DefaultMutableTreeNode)) {
      top = new DefaultMutableTreeNode("KDB+ servers");
      treeConnections.setModel(new KdbServicesTreeModel(top));
      treeConnections.setCellRenderer(new KdbServicesTreeCellRender());

      Collection<String> hostnames = studioConfig.getHosts();
      if (hostnames != null && !hostnames.isEmpty()) {
        for (String hostname : hostnames) {
          try {
            InetAddress.getAllByName(hostname);
            top.add(new HostTreeNode(hostname, studioConfig.getConnections(hostname)));
          }
          catch(UnknownHostException ignored) {            
          }
        }
      }
    }
    treeConnections.expandPath(new TreePath(top));
    treeConnections.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);    
  }
  
  private void createProjectsTree() {
    if (treeProjects == null) {
      treeProjects = new ProjectTree(tabEditors, tabConsoles);
      tools.setProjectView((ProjectTree)treeProjects);
    }
    DefaultMutableTreeNode top = null;
    if (!(treeProjects.getModel() instanceof DefaultMutableTreeNode)) {
      top = new DefaultMutableTreeNode("q Projects");
      ProjectConfig projects = null;
      try {
        projects = ProjectConfig.getConfig();
      }
      catch (StudioException ignored) {
      }
//      if (projects != null) {
//        Collection<Project> list = projects != null ? projects.getProjects() : null;
//        if (list != null && !list.isEmpty()) {
//          for (Project item : list) {
//            if (!item.getName().trim().isEmpty()) {
//              try {
//                DefaultMutableTreeNode category = new ProjectTreeNode(item);
//                top.add(category);
//              }
//              catch(StudioException ignored) {                
//              }
//            }
//          }
//        }
//      }
      treeProjects.setModel(new FileTreeModel(projects));
      if (treeProjects.getModel() instanceof TreeModelSupport) {
        treeProjects.getModel().addTreeModelListener(new ProjectTreeModelListener());
      }
      treeProjects.setCellRenderer(new ProjectTreeCellRender());      
    }
    if (top != null) {
      treeProjects.expandPath(new TreePath(top));
    }    
  }

  public void onData(DataEvent<?> event) {
    if (event instanceof TabIndexChanged && event.getSource() instanceof CloseTabbedPane) {
      rebuildMenus((TabIndexChanged)event);
      if (event.getSource() instanceof EditorsTabbedPane && ((EditorsTabbedPane)event.getSource()).getTabCount() > 0) {
        if (((TabIndexChanged)event).getData() > -1) {
          Editor editor = ((EditorsTabbedPane)event.getSource()).getEditor(((TabIndexChanged)event).getData());
          if (editor != null) {
            setTitle(AppInformation.getInformation().getTitle() + editor.getTitle());
            ((EditorsTabbedPane)event.getSource()).setTitleAt(((TabIndexChanged)event).getData(), 
                editor.getName(true));
            ((EditorsTabbedPane)event.getSource()).setToolTipTextAt(((TabIndexChanged)event).getData(),
                editor.getTooltip());
          }
        }
        else {
          setTitle(AppInformation.getInformation().getTitle());
        }
      }
      else {
        setTitle(AppInformation.getInformation().getTitle());
      }
    }
    else {
      if (event instanceof TabCountChanged) {
        if (event.getSource() instanceof ConsolesTabbedPane) {
          splitViewer.setDividerSize(((TabCountChanged)event).getData() == 1 ? 9 : 0);
          splitViewer.setDividerLocation(((TabCountChanged)event).getData() == 1 ? .7d : 1d);
        }
      }
      else {
        if (event instanceof EditorClosed) {
          if (event.getSource() instanceof EditorsTabbedPane) {
            EditorsTabbedPane tabEditors = (EditorsTabbedPane)event.getSource();
            if (tabEditors.getTabCount() == 0) {
              ToolBarActions toolBar = appToolbar.getToolBar("File");
              for (int count = 0; count < toolBar.getComponentCount(); count++) {
                if (toolBar.getComponent(count) instanceof JButton) {                  
                  JButton button = (JButton)toolBar.getComponent(count);
                  if (button.getAction() instanceof CloseAction) {
                    button.getAction().setEnabled(false);
                  }
                  else {
                    if (button.getAction() instanceof SaveAction) {
                      button.getAction().setEnabled(false);
                    }
                  }
                }
              }
              toolBar = appToolbar.getToolBar("Edit");
              for (int count = 0; count < toolBar.getComponentCount(); count++) {
                if (toolBar.getComponent(count) instanceof JButton) {                  
                  JButton button = (JButton)toolBar.getComponent(count);
                  if (button.getAction().isEnabled()) {
                    button.getAction().setEnabled(false);
                  }
                }
              }
            }
          }
        }
      }
    }
  }

  private void rebuildMenus(TabIndexChanged event) {
    AppMenuBar appMenubar = getJMenuBar() instanceof AppMenuBar
        ? (AppMenuBar)getJMenuBar() : null;
    ToolBarActions toolBar = null;
    ActionMenu menuBar = null;
    if (appToolbar == null && appMenubar == null) {
      appToolbar = new ToolBarActions();
      getContentPane().add(appToolbar, BorderLayout.NORTH);
      appMenubar = new AppMenuBar();
      setJMenuBar(appMenubar);
      toolBar = new ToolBarActions("File");
      menuBar = new ActionMenu("File", 'f', "File operations", (MouseAdapter)null);
      Action action = new NewFileAction(tabEditors, tabConsoles);      
      
      JPopupMenu popupMenu = new JPopupMenu();
      
      JMenuItem item = new JMenuItem("Script");
      item.setToolTipText("New script");
      item.addMouseListener(new ActionHintsListener(tabEditors, "Create new script"));
      item.addActionListener(action);
      popupMenu.add(item);
      
      item = new JMenuItem("Project");
      item.setToolTipText("New project");
      item.addMouseListener(new ActionHintsListener(tabEditors, "Create new project"));
      try {
        item.addActionListener(new AddProjectAction(treeProjects));
        popupMenu.add(item);
      }
      catch (StudioException ex) {
      }
      
      JButton button = new DropDownButton(action, popupMenu);
      if (button instanceof DataListener) {
        tabEditors.addFireDataListener((DataListener)button);
      }
      button.addMouseListener(new ActionHintsListener(tabEditors, action));
      toolBar.add(button);
      menuBar.add(new ActionMenuItem(action, new ActionHintsListener(tabEditors, action)));
            
      action = new OpenFileAction(tabEditors, tabConsoles);
      menuBar.add(new ActionMenuItem(action, new ActionHintsListener(tabEditors, action)));
      popupMenu = null;
      if (studioConfig.getOpenedFiles() != null && !studioConfig.getOpenedFiles().isEmpty()) {
        popupMenu = new JPopupMenu();
        int pos = 0;
        for (EditorFile file : studioConfig.getOpenedFiles()) {
          item = new JMenuItem((char)(pos + 48) + " " + file.getPath(), pos + 48);
          item.addMouseListener(new ActionHintsListener(tabEditors, "Open file " + file));
         popupMenu.add(item);
          if (++pos > 9) {
            break;
          }
        }
      }
      button = new DropDownButton(action, popupMenu);
      if (button instanceof DataListener) {
        tabEditors.addFireDataListener((DataListener)button);
      }
      button.addMouseListener(new ActionHintsListener(tabEditors, action));
      toolBar.add(button);
      appToolbar.add(toolBar);
      appMenubar.add(menuBar);

//      appMenubar.add(new ActionMenu("Project", 'p', "Project operations", (MouseAdapter)null));
      appToolbar.add(new ToolBarActions("KDB+"));
      appMenubar.add(new ActionMenu("KDB+", 'k', "KDB+ databse operations", (MouseAdapter)null));
      appToolbar.add(new ToolBarActions("Edit"));
      appMenubar.add(new ActionMenu("Edit", 'E', "Editor operations", (MouseAdapter)null));
      toolBar = new ToolBarActions("Options");
      appToolbar.add(toolBar);
      menuBar = new ActionMenu("Tools", 'T', "Tools", (MouseAdapter)null);
      ActionMenu opMenuBar = new ActionMenu("Options", 'O', "Studio settings", (MouseAdapter)null);
      ActionMenu lfMenuBar = new ActionMenu("Look and feel", 'L', "L&F", (MouseAdapter)null);
      LookAndFeelAction.getActionLookAndFeel(this, lfMenuBar, studioConfig);
      opMenuBar.add(lfMenuBar);
      opMenuBar.addSeparator();
      action = new EditorSettingsAction(this);
      opMenuBar.add(new ActionMenuItem(action, new ActionHintsListener(tabEditors, action)));
      action = new TokensSettingsAction(this);      
      opMenuBar.add(new ActionMenuItem(action, new ActionHintsListener(tabEditors, action)));
      opMenuBar.addSeparator();
      action = new ConsoleSettingsAction(this);
      opMenuBar.add(new ActionMenuItem(action, new ActionHintsListener(tabEditors, action)));
      opMenuBar.addSeparator();
      action = new ConnectionsSettingsAction(treeConnections);
      opMenuBar.add(new ActionMenuItem(action, new ActionHintsListener(tabEditors, action)));
      menuBar.add(opMenuBar);
      opMenuBar.addSeparator();
      action = new GlobalSettingsAction(this);
      opMenuBar.add(new ActionMenuItem(action, new ActionHintsListener(tabEditors, action)));
      appMenubar.add(menuBar);
      opMenuBar.addSeparator();
      action = new ProjectSettingsAction(this);
      opMenuBar.add(new ActionMenuItem(action, new ActionHintsListener(tabEditors, action)));
      appMenubar.add(menuBar);
      menuBar = new ActionMenu("Help", 'H', "Help...", (MouseAdapter)null);
      action = new VisitKxAction();
      menuBar.add(new ActionMenuItem(action, new ActionHintsListener(tabEditors, action)));
      menuBar.addSeparator();
      action = new VisitWroclawAction();
      menuBar.add(new ActionMenuItem(action, new ActionHintsListener(tabEditors, action)));
      menuBar.addSeparator();
      action = new KeyListAction(tabEditors);
      menuBar.add(new ActionMenuItem(action, new ActionHintsListener(tabEditors, action)));
      menuBar.addSeparator();
      action = new AboutAction(tabEditors);
      menuBar.add(new ActionMenuItem(action, new ActionHintsListener(tabEditors, action)));
      appMenubar.add(menuBar);
      menuBar.addSeparator();
      action = new CheckUpdate(this);
      menuBar.add(new ActionMenuItem(action, new ActionHintsListener(tabEditors, action)));
      appMenubar.add(menuBar);
      toolBar = null;

      this.setJMenuBar(appMenubar);
    }

    appToolbar.getToolBar("Edit").setVisible(event != null && event.getData() != -1);
    appToolbar.getToolBar("Options").setVisible(event != null && event.getData() != -1);
    appToolbar.getToolBar("KDB+").setVisible(event != null && event.getData() != -1);
    appToolbar.getToolBar("File").setVisible(event != null && event.getData() != -1);

    appMenubar.getMenu("Edit").setVisible(event != null && event.getData() != -1);
    appMenubar.getMenu("Tools").setVisible(event != null && event.getData() != -1);
    appMenubar.getMenu("KDB+").setVisible(event != null && event.getData() != -1);
    appMenubar.getMenu("File").setVisible(event != null && event.getData() != -1);

    if (event != null && event.getSource() instanceof EditorsTabbedPane && event.getData() != -1) {
      Editor editor = ((EditorsTabbedPane)event.getSource()).getEditor(event.getData());
      if (editor != null) {
        JPopupMenu menu = editor.getComponentPopupMenu();
        if (menu != null) {
          for (int count = 0; count < menu.getComponentCount(); count++) {
            if (menu.getComponent(count) instanceof JMenuItem) {
              JMenuItem menuItem = (JMenuItem)menu.getComponent(count);
              if (menuItem.getAction().getClass() == SaveAction.class) {
                appToolbar.getToolBar("File").update(tabEditors, menuItem.getAction(), true);
                appMenubar.getMenu("File").update(tabEditors, menuItem.getAction(), true);
              }
              else {
                if (menuItem.getAction().getClass() == SaveAsAction.class ||
                    menuItem.getAction().getClass() == SaveAllAction.class) {
                  appToolbar.getToolBar("File").update(tabEditors, menuItem.getAction());
                  appMenubar.getMenu("File").update(tabEditors, menuItem.getAction());
                }
                else {
                  if (menuItem.getAction().getClass() == CloseAction.class) {
                    appToolbar.getToolBar("File").update(tabEditors, menuItem.getAction(), true);
                    appMenubar.getMenu("File").update(tabEditors, menuItem.getAction(), true);
                  }
                  else {
                    if (menuItem.getAction().getClass() == CloseAllAction.class) {
                      appToolbar.getToolBar("File").update(tabEditors, menuItem.getAction());
                      appMenubar.getMenu("File").update(tabEditors, menuItem.getAction());
                    }
                    else {
                      if (menuItem.getAction().getClass() == RunAction.class ||
                          menuItem.getAction().getClass() == RunLineAction.class ||
                          menuItem.getAction().getClass() == CancelRunAction.class) {
                        appToolbar.getToolBar("KDB+").update(tabEditors, menuItem.getAction());
                        appMenubar.getMenu("KDB+").update(tabEditors, menuItem.getAction());
                      }
                      else {
                        if (menuItem.getAction().getClass() == ExtKit.ReplaceAction.class ||
                            menuItem.getAction().getClass() == ActionFactory.RedoAction.class ||
                            menuItem.getAction().getClass() == ActionFactory.UndoAction.class ||
                            menuItem.getAction().getClass() == BaseKit.CopyAction.class ||
                            menuItem.getAction().getClass() == BaseKit.PasteAction.class ||
                            menuItem.getAction().getClass() == ExtKit.GotoAction.class ||
                            menuItem.getAction().getClass() == BaseKit.SelectAllAction.class ||
                            menuItem.getAction().getClass() == BaseKit.SelectLineAction.class ||
                            menuItem.getAction().getClass() == BaseKit.CutAction.class) {
                          appToolbar.getToolBar("Edit").update(tabEditors, menuItem.getAction());
                          if (menuItem.getAction().getClass() == ExtKit.GotoAction.class ||
                              menuItem.getAction().getClass() == BaseKit.CopyAction.class ||
                              menuItem.getAction().getClass() == BaseKit.SelectAllAction.class) {
                            appMenubar.getMenu("Edit").update(tabEditors, menuItem.getAction(),
                                true);
                          }
                          else {
                            appMenubar.getMenu("Edit").update(tabEditors, menuItem.getAction());
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
    if (event != null && event.getData() != -1 &&
        event.getSource() instanceof EditorsTabbedPane &&
        ((EditorsTabbedPane)event.getSource()).getEditor(event.getData()) != null) {
      ((EditorsTabbedPane)event.getSource()).getEditor(event.getData()).requestFocus();
    }
  }
  
  public JSplitPane getVerticalSplit() {
    return splitViewer;
  }
  
  public JSplitPane getHorizontalSplit() {
    return splitTopViewer;
  }
  
  static {
    LocaleSupport.addLocalizer(new StudioLocalizer("org.netbeans.editor.Bundle"));

    Settings.addInitializer(new BaseSettingsInitializer(), Settings.CORE_LEVEL);
    Settings.addInitializer(new ExtSettingsInitializer(), Settings.CORE_LEVEL);
    
    QKit editorKit = new QKit();
    JEditorPane.registerEditorKitForContentType(editorKit.getContentType(),
        editorKit.getClass().getName());
    Settings.addInitializer(new QSettingsInitializer());
    Settings.reset();
  }

//  @Override
//  public void windowStateChanged(WindowEvent event) {
//    if (event.getID() == WindowEvent.WINDOW_STATE_CHANGED) {
//      try {
//        studioConfig.setMaximized((event.getNewState() & JFrame.MAXIMIZED_BOTH) == JFrame.MAXIMIZED_BOTH);
//      }
//      catch (ConfigException ignored) {
//      }
//    }
//  }

//  @Override
//  public void windowActivated(WindowEvent event) {
//  }
//
//  @Override
//  public void windowClosed(WindowEvent event) {
//  }
//
//  @Override
//  public void windowClosing(WindowEvent event) {
//    if (tabEditors != null && tabEditors.getTabCount() > 0) {
//      int current = tabEditors.getSelectedIndex();
//      boolean close = true;
//      for (int count = 0; count < tabEditors.getTabCount(); count++) {
//        QEditor editor = tabEditors.getEditor(count);
//        if (editor.isModified() && !editor.isEmpty()) {
//          tabEditors.setSelectedIndex(count);
//          close &= tabEditors.closeEditor(editor);
//        }       
//      }
//      if (!close) {
//        tabEditors.setSelectedIndex(current);
//        getToolkit().getSystemEventQueue().postEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
//        return;
//      }
//    }
//    
//    java.util.List<EditorFile> names = tabEditors.saveFiles();
//    if (names == null) {
//      return;
//    }
//    try {
//      studioConfig.setLastFilenames(names);
//    }
//    catch (ConfigException ex) {
//      ex.printStackTrace();
//    }
//    System.exit(0);
//  }
//
//  @Override
//  public void windowDeactivated(WindowEvent event) {
//  }
//
//  @Override
//  public void windowDeiconified(WindowEvent event) {
//  }
//
//  @Override
//  public void windowIconified(WindowEvent event) {
//  }
//
//  @Override
//  public void windowOpened(WindowEvent event) {
//  }
}