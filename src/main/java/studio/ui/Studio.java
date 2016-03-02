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

  public boolean quit() {
    return true;
  }

  public boolean quitWindow() {
    this.dispose();
    return true;
  }

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
}