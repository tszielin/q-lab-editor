package tszielin.qlab.component.pane;

import java.awt.Component;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import studio.ui.Studio;
import tszielin.qlab.component.console.Console;
import tszielin.qlab.component.console.ErrorConsole;
import tszielin.qlab.component.console.GridConsole;
import tszielin.qlab.component.editor.QEditor;
import tszielin.qlab.event.EditorClosing;
import tszielin.qlab.event.EditorSelected;
import tszielin.qlab.event.TabCountChanged;
import tszielin.qlab.util.action.ActionBase;
import tszielin.qlab.util.event.DataEvent;
import tszielin.qlab.util.listener.DataListener;

public class ConsolesTabbedPane extends CloseTabbedPane implements DataListener, ChangeListener {
  private static final long serialVersionUID = -8430468008016915417L;
  
  public ConsolesTabbedPane() {
    super();
    addChangeListener(this);
    
    Action action = new ActionBase(null, (char)0, null, KeyStroke.getKeyStroke("control shift M")) {
      private static final long serialVersionUID = 1798446697074255038L;

      public void actionPerformed(ActionEvent e) {
        maximize(); 
      }
    };    
    registerKeyboardAction(action, ((ActionBase)action).getAccelerator(), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

  }

  public void addTab(String title, Component component) {
    component.addMouseMotionListener(this);
    addTab(title, component);    
  }

  public void addTab(String title, Icon icon, Component component) {
    if (getTabCount() == 0) {
      fireData(new TabCountChanged(this, 1));
    }
    super.addTab(title, icon, component);
  }
  
  public void insertTab(String title, Component component, int index) {
    insertTab(title, component, index);    
  }
  
  public void insertTab(String title, Icon icon, Component component, int index) {
    if (getTabCount() == 0) {
      fireData(new TabCountChanged(this, 1));
    }
    index = index < 0 || index > getTabCount() ? getTabCount() : index; 
    Component comp = null;
    if (component instanceof ErrorConsole) {
      comp = component;
      index = 0;
    }
    else {
      if (component instanceof GridConsole) {
        comp = component;
      }
    }
    super.insertTab(title, icon, comp, null, index);
  }
  
  public Console getConsole(QEditor editor) {
    if (getTabCount() > 0) {
      for (int count = 0; count < getTabCount(); count++) {
        if (getComponentAt(count) instanceof Console) {
          if (((Console)getComponentAt(count)).isAssigned(editor)) {
            return (Console)getComponentAt(count);
          }
        }
      }
    }
    return null;
  }
  
  public int getConsoleID(QEditor editor) {
    if (editor == null) {
      return -1;
    }
    if (getTabCount() > 0) {
      for (int count = 0; count < getTabCount(); count++) {
        if (getComponentAt(count) instanceof Console) {
          if (((Console)getComponentAt(count)).isAssigned(editor)) {
            return count;
          }
        }
      }
    }
    return -1;
  }
  
  public int getConsoleID(Component component) {
    if (component == null) {
      return -1;
    }
    if (getTabCount() > 0) {
      for (int count = 0; count < getTabCount(); count++) {
        if (getComponentAt(count).equals(component)) {
          return count;
        }
      }
    }
    return -1;
  }
  
  @Override
  public void removeTabAt(int index) {
    super.removeTabAt(index);  
    if (getTabCount() == 0) {
      fireData(new TabCountChanged(this, 0));
    }
  }

  public void onData(DataEvent<?> event) {
    if (getTabCount() > 0) {
      if (event.getSource() instanceof EditorsTabbedPane) {
        if (event instanceof EditorClosing) {
          List<Integer> list = null;
          for (int count = getTabCount() - 1; count >= 0; count--) {
            if (getComponentAt(count) instanceof Console && ((EditorClosing)event).getData() instanceof QEditor) {
              if (((Console)getComponentAt(count)).isAssigned((QEditor)((EditorClosing)event).getData())) {
                if (list == null) {
                  list = new ArrayList<Integer>();
                }
                list.add(Integer.valueOf(count));
              }
            }
          }
          if (list != null && !list.isEmpty()) {
            for (Integer item : list) {
              removeTabAt(item.intValue());
            }
          }
        }
        else {
          if (event instanceof EditorSelected && ((EditorSelected)event).getData() instanceof QEditor) {
            int index = getConsoleID((QEditor)((EditorSelected)event).getData());
            if (index != -1) {
              setSelectedIndex(index);
            }            
          }
        }
      }
    }
  }

  @Override
  protected Action getAction() {
    return null;
  }

  @Override
  protected JSplitPane getSplitPane() {
    Window window = SwingUtilities.windowForComponent(this);
    return window instanceof Studio ? ((Studio)window).getVerticalSplit() : null;
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
  }
  
  public void stateChanged(ChangeEvent event) {
    if (event.getSource() instanceof ConsolesTabbedPane) {
      if (getTabCount() > 0 && getSelectedIndex() != -1) {
        for (int count = 0; count < getTabCount(); count++) {
          setBackgroundAt(count, null);
        }
        setBackgroundAt(getSelectedIndex(), UIManager.getColor("TabbedPane.highlight"));
      }
    }
  }
}
