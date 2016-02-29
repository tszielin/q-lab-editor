package tszielin.qlab.component.pane;

import java.awt.*;
import java.awt.dnd.DropTarget;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.JTextComponent;

import org.netbeans.editor.StatusBar;
import org.netbeans.editor.Utilities;

import studio.ui.Studio;
import tszielin.qlab.action.component.ChangeTabAction;
import tszielin.qlab.action.connection.AssignServerAction;
import tszielin.qlab.action.editor.*;
import tszielin.qlab.adapter.ConnectionTreeMouseAdapter;
import tszielin.qlab.component.editor.Editor;
import tszielin.qlab.component.editor.QEditor;
import tszielin.qlab.config.AppConfig;
import tszielin.qlab.config.data.EditorFile;
import tszielin.qlab.config.data.KdbService;
import tszielin.qlab.error.*;
import tszielin.qlab.event.*;
import tszielin.qlab.listener.TabbedPaneDropTargetListener;
import tszielin.qlab.util.action.ActionBase;
import tszielin.qlab.util.component.menu.ActionMenuItem;
import tszielin.qlab.util.error.ConfigException;
import tszielin.qlab.util.event.DataEvent;
import tszielin.qlab.util.listener.DataListener;

import com.kx.KdbConnection;

public class EditorsTabbedPane extends CloseTabbedPane implements DataListener, ChangeListener {  
  private static final long serialVersionUID = 6103864033188084074L;
  private AppConfig config;
  private final ConsolesTabbedPane tabConsoles;
  
  private KdbService lostConnection;
  private int windowCount = -1;
  
  public EditorsTabbedPane(ConsolesTabbedPane consoles) {
    super();    
    this.tabConsoles = consoles;
    new DropTarget(this, new TabbedPaneDropTargetListener(this));
    addChangeListener(this);    
    
    
    try {
      this.config = AppConfig.getConfig();
    }
    catch(StudioException ignored) {      
    }
    
    ActionBase action = new ChangeTabAction(this, KeyStroke.getKeyStroke("control shift PAGE_UP")) {
      private static final long serialVersionUID = 1896676043244551260L;

      @Override
      public void actionPerformed(ActionEvent e) {
        getTabPane().setSelectedIndex(getTabPane().getSelectedIndex() + 1 < getTabPane().getTabCount() ? 
            getTabPane().getSelectedIndex() + 1 : 0);        
      }
    };    
    registerKeyboardAction(action, action.getAccelerator(), JComponent.WHEN_IN_FOCUSED_WINDOW);
    
    action = new ChangeTabAction(this, KeyStroke.getKeyStroke("control shift PAGE_DOWN")) {
      private static final long serialVersionUID = 3348724058943957506L;

      @Override
      public void actionPerformed(ActionEvent e) {
        getTabPane().setSelectedIndex(getTabPane().getSelectedIndex() - 1 >= 0 ? 
            getTabPane().getSelectedIndex() - 1 : getTabPane().getTabCount() - 1);
        
      }
    };
    registerKeyboardAction(action, action.getAccelerator(), JComponent.WHEN_IN_FOCUSED_WINDOW);
    
    action = new ChangeTabAction(this, KeyStroke.getKeyStroke("control 1")) {
      private static final long serialVersionUID = -8545723007584309747L;

      @Override
      public void actionPerformed(ActionEvent e) {
        if (getTabPane().getTabCount() != 0) {
          getTabPane().setSelectedIndex(0);
        }        
      }
    };    
    registerKeyboardAction(action, action.getAccelerator(), JComponent.WHEN_IN_FOCUSED_WINDOW);
    
    action = new ChangeTabAction(this, KeyStroke.getKeyStroke("control 2")) {
      private static final long serialVersionUID = 2658216844129673842L;

      @Override
      public void actionPerformed(ActionEvent e) {
        if (getTabPane().getTabCount() != 0) {
          getTabPane().setSelectedIndex(
              getTabPane().getTabCount() <= 2 ? getTabPane().getTabCount() - 1 :
              getTabPane().getSelectedIndex() > 9 &&  
              getTabPane().getSelectedIndex() >= getTabPane().getTabCount() - 7 ? 
                  getTabPane().getTabCount() - 8 : 1);
        } 
      }
    };    
    registerKeyboardAction(action, action.getAccelerator(), JComponent.WHEN_IN_FOCUSED_WINDOW);
    
    action = new ChangeTabAction(this, KeyStroke.getKeyStroke("control 3")) {
      private static final long serialVersionUID = 7606864263517627066L;

      @Override
      public void actionPerformed(ActionEvent e) {
        if (getTabPane().getTabCount() != 0) {
          getTabPane().setSelectedIndex(
              getTabPane().getTabCount() <= 3 ? getTabPane().getTabCount() - 1 :
              getTabPane().getSelectedIndex() > 9 &&  
              getTabPane().getSelectedIndex() >= getTabPane().getTabCount() - 6 ? 
                  getTabPane().getTabCount() - 7 : 2);
        }        
      }
    };    
    registerKeyboardAction(action, action.getAccelerator(), JComponent.WHEN_IN_FOCUSED_WINDOW);
    
    action = new ChangeTabAction(this, KeyStroke.getKeyStroke("control 4")) {
      private static final long serialVersionUID = 2279899704801011236L;

      @Override
      public void actionPerformed(ActionEvent e) {
        if (getTabPane().getTabCount() != 0) {
          getTabPane().setSelectedIndex(
              getTabPane().getTabCount() <= 4 ? getTabPane().getTabCount() - 1 :
              getTabPane().getSelectedIndex() > 9 &&  
              getTabPane().getSelectedIndex() >= getTabPane().getTabCount() - 5 ? 
                  getTabPane().getTabCount() - 6 : 3);
        } 
      }
    };    
    registerKeyboardAction(action, action.getAccelerator(), JComponent.WHEN_IN_FOCUSED_WINDOW);
    
    action = new ChangeTabAction(this, KeyStroke.getKeyStroke("control 5")) {
      private static final long serialVersionUID = 2902087931929772603L;

      @Override
      public void actionPerformed(ActionEvent e) {
        if (getTabPane().getTabCount() != 0) {
          getTabPane().setSelectedIndex(
              getTabPane().getTabCount() <= 5 ? getTabPane().getTabCount() - 1 :
              getTabPane().getSelectedIndex() > 9 &&  
              getTabPane().getSelectedIndex() >= getTabPane().getTabCount() - 4 ? 
                  getTabPane().getTabCount() - 5 : 4);
        }
      }
    };    
    registerKeyboardAction(action, action.getAccelerator(), JComponent.WHEN_IN_FOCUSED_WINDOW);
    
    action = new ChangeTabAction(this, KeyStroke.getKeyStroke("control 6")) {
      private static final long serialVersionUID = 4542542551428273937L;

      @Override
      public void actionPerformed(ActionEvent e) {
        if (getTabPane().getTabCount() != 0) {
          getTabPane().setSelectedIndex(
              getTabPane().getTabCount() <= 6 ? getTabPane().getTabCount() - 1 :
              getTabPane().getSelectedIndex() > 9 &&  
              getTabPane().getSelectedIndex() >= getTabPane().getTabCount() - 3 ? 
                  getTabPane().getTabCount() - 4 : 5);
        }
      }
    };    
    registerKeyboardAction(action, action.getAccelerator(), JComponent.WHEN_IN_FOCUSED_WINDOW);
    
    action = new ChangeTabAction(this, KeyStroke.getKeyStroke("control 7")) {
      private static final long serialVersionUID = -1639793117044663192L;

      @Override
      public void actionPerformed(ActionEvent e) {
        if (getTabPane().getTabCount() != 0) {
          getTabPane().setSelectedIndex(
              getTabPane().getTabCount() <= 7 ? getTabPane().getTabCount() - 1 :
              getTabPane().getSelectedIndex() > 9 &&  
              getTabPane().getSelectedIndex() >= getTabPane().getTabCount() - 2 ? 
                  getTabPane().getTabCount() - 3 : 6);
        }
      }
    };    
    registerKeyboardAction(action, action.getAccelerator(), JComponent.WHEN_IN_FOCUSED_WINDOW);
    
    action = new ChangeTabAction(this, KeyStroke.getKeyStroke("control 8")) {
      private static final long serialVersionUID = -5227414654176776276L;

      @Override
      public void actionPerformed(ActionEvent e) {
        if (getTabPane().getTabCount() != 0) {
          getTabPane().setSelectedIndex(
              getTabPane().getTabCount() <= 8 ? getTabPane().getTabCount() - 1 :
              getTabPane().getSelectedIndex() > 9 &&  
              getTabPane().getSelectedIndex() >= getTabPane().getTabCount() - 1 ? 
                  getTabPane().getTabCount() - 2 : 7);
        } 
      }
    };    
    registerKeyboardAction(action, action.getAccelerator(), JComponent.WHEN_IN_FOCUSED_WINDOW);
    
    action = new ChangeTabAction(this, KeyStroke.getKeyStroke("control 9")) {
      private static final long serialVersionUID = 2658216844129673842L;

      @Override
      public void actionPerformed(ActionEvent e) {
        if (getTabPane().getTabCount() != 0) {
          getTabPane().setSelectedIndex(getTabPane().getTabCount() - 1);
        }        
      }
    };    
    registerKeyboardAction(action, action.getAccelerator(), JComponent.WHEN_IN_FOCUSED_WINDOW);
    
    action = new ActionBase(null, (char)0, null, KeyStroke.getKeyStroke("control shift M")) {
      private static final long serialVersionUID = 5653135426433448071L;

      public void actionPerformed(ActionEvent e) {
        maximize(); 
      }
    };    
    registerKeyboardAction(action, action.getAccelerator(), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
  }

  public Editor getEditor(int index) {
    if (index == -1 || index >= getTabCount()) {
      return null;
    }
    JComponent component = getComponentAt(index) instanceof JComponent ?
        (JComponent)getComponentAt(index) : null;
    if (component != null && component.getClientProperty(JTextComponent.class) instanceof Editor) {
      return (Editor)component.getClientProperty(JTextComponent.class);
    }
    return null;
  }
  
  public Editor getEditor() {
    return getEditor(getSelectedIndex());
  }
  
  public String getEditorTitle() {
    return getTitleAt(getSelectedIndex()).replaceAll("\\*", "");
  }
  
    public void addTab(String title, Icon icon, Component component) {
    if (component == null) {
      return;
    }
    super.addTab(title, icon, component);
    component.addMouseMotionListener(this);
    stateChanged();
    if (getEditor(getTabCount() - 1) instanceof DataListener) {      
      addFireDataListener((DataListener)getEditor(getTabCount() - 1));
    }
    fireData(new TabCountChanged(this, getTabCount()));
    setSelectedIndex(getTabCount() - 1);
    fireData(new TabIndexChanged(this, getSelectedIndex()));
  }
  
  public void insertTab(String title, Icon icon, Component component, int index) {
    super.insertTab(title, icon, component, null, index);
    stateChanged();
    if (getEditor(index) instanceof DataListener) {      
      addFireDataListener((DataListener)getEditor(index));
    }
    fireData(new TabCountChanged(this, getTabCount()));
    setSelectedIndex(index);
    fireData(new TabIndexChanged(this, index));
  }
  
  public void onData(DataEvent<?> event) {
    if (event instanceof EditorStateChanged) {
      if (event.getSource() instanceof Editor) {
        if (getEditor() == event.getSource()) {                 
          fireData(new TabIndexChanged(this, getSelectedIndex()));
        }
      }
      stateChanged();
    }
    else {
      if (event instanceof KdbServiceChanged) {
        fireData(event);
        if (getTabCount() > 0 &&
            (event.getSource() instanceof AssignServerAction || 
            event.getSource() instanceof TabbedPaneDropTargetListener ||
            event.getSource() instanceof ConnectionTreeMouseAdapter)) {
          Editor editor = getEditor(getSelectedIndex());
          if (editor instanceof QEditor) {
            if (((KdbServiceChanged)event).getData() != null) {              
              KdbService connection = ((KdbServiceChanged)event).getData();
              boolean change = true;
              if (editor.getConnection() != null && 
                  !editor.getConnection().equals(((KdbServiceChanged)event).getData())) {
                Utilities.setStatusBarText(getEditor(), StatusBar.CELL_SERVER, "Checking connection...", true, Color.MAGENTA);
                KdbConnection server = new KdbConnection(connection.getHost(), connection.getPort(), connection.getCredentials());
                try {                  
                  server.testConnection(true);
                  Utilities.setStatusBarText(getEditor(), StatusBar.CELL_SERVER, 
                      editor.getConnection() != null ? 
                          editor.getConnection().getServiceInfo() : "<not connected>", true, 
                          editor.getConnection() != null ? editor.getConnection().getTitleColor() : Color.RED);
                  change = true;
                  if (editor.getConnection() != null) {
                    if (getTabCount() > getSelectedIndex() && editor.getConnection().getTitleColor() != null) {
                      setForegroundAt(getSelectedIndex(), editor.getConnection().getTitleColor());
                    }
                  }
                  if (config.isChangeConnectionNofication()) {
                    change = JOptionPane.showOptionDialog(this,
                        "Are you sure you want to change server\nfrom " +
                            editor.getConnection().getServiceInfo() + "\nto " +
                            connection.getServiceInfo() + "?", "Assign server",
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                        new Object[]{UIManager.get("OptionPane.yesButtonText"),
                            UIManager.get("OptionPane.noButtonText")},
                        UIManager.get("OptionPane.yesButtonText")) == JOptionPane.YES_OPTION;
                  }
//                  fireData(new EditorClosing(this, editor));
                  fireData(new KdbServiceReplaced(editor, connection, editor.getConnection()));
                }
                catch (Throwable cause) {
                  Utilities.setStatusBarText(editor, StatusBar.CELL_SERVER, 
                      editor.getConnection() != null ? 
                          editor.getConnection().getServiceInfo() : "<not connected>", true, 
                          editor.getConnection() != null ? editor.getConnection().getTitleColor() : Color.RED);
                  change = false;
                  JOptionPane.showMessageDialog(this, connection.getServiceInfo() + 
                      " is not available.\nOperation cancelled.", "kdb+ Server", JOptionPane.INFORMATION_MESSAGE);
                }
                finally {
                  if (server != null) {
                    server.close();
                    server = null;
                  }
                }
              }
              if (change) {
                ((QEditor)editor).setConnection(((KdbServiceChanged)event).getData());                              
              }
            }
            else {
              ((QEditor)editor).setConnection(null);
              setForegroundAt(getSelectedIndex(), null);
            }            
          }
        }
        else {
          if (event.getSource() instanceof QEditor) {
            QEditor editor = (QEditor)event.getSource();
            int current = -1;
            if (getTabCount() > 0) {
              for (int count = 0; count < getTabCount(); count++) {
                if (getEditor(count) == editor) {
                  current = count;
                  break;
                }
              }
            }
            fireData(new TabIndexChanged(this, getSelectedIndex()));
            revalidate();
            if (current != -1 && current != getTabCount()) {
              setForegroundAt(current, editor.getConnection() != null
                  ? editor.getConnection().getTitleColor() : null);
            }
          }
        }
      }
      else {
        if (event instanceof FilenameChanged) {
          if (((FilenameChanged)event).getFilename() != null && getEditor() != null) {
            fireData(new TabIndexChanged(this, getSelectedIndex()));
          }
        }
        else {
          if (event instanceof FileChoosed) {
            if (((FileChoosed)event).getData() != null) {
              try {
                new OpenFileAction(this, tabConsoles).initDocument(
                    new EditorFile(((FileChoosed)event).getData(), ((FileChoosed)event).getConnection()));
              }
              catch (FileException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(),
                  "File error", JOptionPane.ERROR_MESSAGE);
              }  
            }
          }
          else {
            if (event instanceof ConnectionClosed) {
              if (event.getSource() instanceof QEditor) {
                fireData(event);
                if (config.isLostConnectionMessage()) {
                  KdbService connection = ((ConnectionClosed)event).getData();
                  if (connection != null) {
                    if (lostConnection == null || 
                        (!lostConnection.getHost().equals(connection.getHost()) && 
                            lostConnection.getPort() != connection.getPort())) {
                      try {
                        lostConnection = new KdbService(connection.getHost(), connection.getPort());
                      }
                      catch (ArgumentException ignored) {
                      }
                      if (lostConnection == null) {
                        return;
                      }
                      if (windowCount == -1) {
                        for (int count = 0; count < getTabCount(); count++) {
                          if (getEditor(count).getConnection() != null && 
                              getEditor(count).getConnection().getHost().equals(lostConnection.getHost()) && 
                              getEditor(count).getConnection().getPort() == lostConnection.getPort()) {
                            windowCount++;
                          }
                        }
                      }
                    }
                    else {
                      windowCount--;
                    }
                    if (windowCount == 0) {
                      JOptionPane.showMessageDialog(this, "Connection to " + 
                          lostConnection.getServiceInfo(false) + " lost!", 
                          "kdb+ Server", JOptionPane.ERROR_MESSAGE);
                      windowCount = -1;
                      lostConnection = null;
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
  
  @Override
  public void removeTabAt(int index) {
    Editor editor = getEditor(index);
    if (editor != null && closeEditor(editor)) {
      fireData(new EditorClosing(this, editor));
      if (editor instanceof DataListener) {
        removeFireDataListener((DataListener)editor);
      }
      if (!editor.isEmpty() && editor.isEnabled() && editor.isEditable() && !editor.isModified() &&
          !editor.getPath().startsWith("script")) {
        try {
          config.addOpenedFile(editor.getFile());
          fireData(new FileListChanged(this, config.getOpenedFiles()));
        }
        catch (ConfigException ignored) {
        }
      }
      editor.dispose();
      super.removeTabAt(index);
      stateChanged();
      fireData(new EditorClosed(this, editor));
      return;
    }
    JComponent component = index == -1 || index >= getTabCount() ? null :
      getComponentAt(index) instanceof JComponent ?
        (JComponent)getComponentAt(index) : null;
    if (component instanceof KdbEnvironment) {
      ((KdbEnvironment)component).removeAll();
      super.removeTabAt(index);
    }    
  }
  
  public boolean closeEditor(Editor editor) {
    if (editor != null && editor.isModified() && !editor.isEmpty()) {
      JPopupMenu popup = editor.getComponentPopupMenu();
      if (popup != null && popup.getComponentCount() > 0) {
        int choice = JOptionPane.showConfirmDialog(this, "'" + editor.getPath() +
            "' has been modified. Save changes?",
            editor.getFile().getName().endsWith(".q") || editor.getFile().getName().endsWith(".k") ? 
            "Save Script" : "Save File", JOptionPane.YES_NO_CANCEL_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        switch (choice) {
          case JOptionPane.YES_OPTION:
            for (int count = 0; count < popup.getComponentCount(); count++) {
              if (popup.getComponent(count) instanceof ActionMenuItem &&
                  ((ActionMenuItem)popup.getComponent(count)).getAction().getClass() == SaveAction.class) {
                ((ActionMenuItem)popup.getComponent(count)).actionPerformed(null);
              }
            }
            return true;
          case JOptionPane.NO_OPTION:
            return true;
          default:
            return false;
        }
      }
    }
    return true;
  }
  
  public List<EditorFile> saveFiles() {
    List<EditorFile> files = new ArrayList<EditorFile>();
    for (int index = 0; index < getTabCount(); index++) {
      Editor editor = getEditor(index);
      if (editor != null && editor.isModified() && !editor.isEmpty()) {
        JPopupMenu popup = editor.getComponentPopupMenu();
        if (popup != null && popup.getComponentCount() > 0) {
          int choice = JOptionPane.showConfirmDialog(this, "'" + editor.getPath() +
            "' has been modified. Save changes?", 
            editor.getFile().getName().endsWith(".q") || editor.getFile().getName().endsWith(".k") ? 
                "Save Script" : "Save File", JOptionPane.YES_NO_CANCEL_OPTION,
            JOptionPane.QUESTION_MESSAGE);
          switch (choice) {
            case JOptionPane.YES_OPTION:
              for (int count = 0; count < popup.getComponentCount(); count++) {
                if (popup.getComponent(count) instanceof ActionMenuItem &&
                  ((ActionMenuItem)popup.getComponent(count)).getAction().getClass() == SaveAction.class) {
                  ((ActionMenuItem)popup.getComponent(count)).actionPerformed(null);
                }
              }
              files.add(editor.getFile());
              break;
            case JOptionPane.NO_OPTION:              
              break;
            default:
              return null;
          }
        }
      }
      else {
        if (editor != null && editor.getPath() != null && !editor.getPath().startsWith("script")) {
          files.add(editor.getFile());
        }
      }
    }
    return files;
  }
  
  public void stateChanged(ChangeEvent event) {
    if (event.getSource() instanceof EditorsTabbedPane) {
      if (getTabCount() > 0 && getSelectedIndex() != -1) {
        for (int count = 0; count < getTabCount(); count++) {
          try {
            setBackgroundAt(count, null);
          }
          catch(Exception ignored) {            
          }
        }
        try {
          setBackgroundAt(getSelectedIndex(), UIManager.getColor("TabbedPane.highlight"));
        }
        catch(Exception ignored) {            
        }
      }
      fireData(new TabIndexChanged(this, getSelectedIndex()));
      fireData(new EditorSelected(this, getEditor()));
    }
  }
  
  private void stateChanged() {
    boolean edited = false;
    for (int tab = 0; tab < getComponentCount(); tab++) {
      Editor editor = getEditor(tab);
      edited |= editor != null ? editor.isModified() : false;
    }
    for (int tab = 0; tab < getComponentCount(); tab++) {
      Editor editor = getEditor(tab);
      if (editor != null) {
        JPopupMenu popupMenu = editor.getComponentPopupMenu();
        if (popupMenu != null && popupMenu.getComponentCount() > 0) {
          for (int count = 0; count < popupMenu.getComponentCount(); count++) {
            if (popupMenu.getComponent(count) instanceof ActionMenuItem) {
              ActionMenuItem item = (ActionMenuItem)popupMenu.getComponent(count);
              if (item.getAction() instanceof CloseAllAction) {
                item.getAction().setEnabled(getTabCount() > 1);
              }
              if (item.getAction().getClass() == SaveAllAction.class) {
                item.getAction().setEnabled(edited && getTabCount() > 1);
              }
            }
          }
        }
      }
    }
  }

  public boolean insertMatchingBrackets() {
    return config != null ? config.isMatchingBrackets() : false;
  }

  @Override
  protected Action getAction() {
    return new NewFileAction(this, tabConsoles);
  }

  @Override
  protected JSplitPane getSplitPane() {
    Window window = SwingUtilities.windowForComponent(this);
    return window instanceof Studio ? ((Studio)window).getHorizontalSplit() : null;
  }
  
  protected void maximize() {
    if (getSplitPane() != null) {
      if (getSplitPane().getDividerLocation() > 1) {
        getSplitPane().setDividerLocation(0);
      }
      else {
        getSplitPane().setDividerLocation(getSplitPane().getLastDividerLocation());
      }
    }
    if (tabConsoles != null && tabConsoles.getSplitPane() != null) {
      if (tabConsoles.getSplitPane().getDividerLocation() < tabConsoles.getSplitPane().getParent().getSize().height - 45) {
        tabConsoles.getSplitPane().setDividerLocation(
            tabConsoles.getSplitPane().getParent().getSize().height);
      }
      else {
        tabConsoles.getSplitPane().setDividerLocation(
            tabConsoles.getSplitPane().getLastDividerLocation());
      }
    }
  }
}
