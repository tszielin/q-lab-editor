package tszielin.qlab.component.pane;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Event;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import javax.swing.text.View;

import sun.swing.SwingUtilities2;
import tszielin.qlab.action.editor.CloseAction;
import tszielin.qlab.action.editor.CloseAllAction;
import tszielin.qlab.util.action.ActionBase;
import tszielin.qlab.util.component.menu.ActionMenuItem;
import tszielin.qlab.util.event.DataEvent;
import tszielin.qlab.util.event.FireData;
import tszielin.qlab.util.image.IconsItem;
import tszielin.qlab.util.listener.DataListener;

abstract public class CloseTabbedPane extends JTabbedPane implements MouseListener,
    MouseMotionListener {
  private static final long serialVersionUID = 5533887626826323462L;

  private java.util.List<CloseTabIcon> closeIcons;
  private FireData fireData;
  private JPopupMenu popup;

  class CloseTabbedPaneUI extends BasicTabbedPaneUI {
    public CloseTabbedPaneUI() {
      super();
    }

    @Override
    protected int calculateTabWidth(int tabPlacement, int tabIndex, FontMetrics metrics) {
      Insets tabInsets = getTabInsets(tabPlacement, tabIndex);
      int width = tabInsets.left + tabInsets.right +
        (closeIcons != null && tabIndex < closeIcons.size() && closeIcons.get(tabIndex) != null  ? 5 : 0) + 3;
      Component tabComponent = tabPane.getTabComponentAt(tabIndex);
      if (tabComponent != null) {
        width += tabComponent.getPreferredSize().width;
      }
      else {
        Icon icon = getIconForTab(tabIndex);
        if (icon != null) {
          width += icon.getIconWidth() + textIconGap;
          if (closeIcons != null && tabIndex < closeIcons.size() && closeIcons.get(tabIndex) != null) {
            width += icon.getIconWidth() + textIconGap;
          }
        }
        View v = getTextViewForTab(tabIndex);
        if (v != null) {
          // html
          width += (int)v.getPreferredSpan(View.X_AXIS);
        }
        else {
          // plain text
          String title = tabPane.getTitleAt(tabIndex);
          width += SwingUtilities2.stringWidth(tabPane, metrics, title);
        }
      }
      return width;
    }

    @Override
    protected void layoutLabel(int tabPlacement, FontMetrics metrics, int tabIndex, String title,
        Icon icon, Rectangle tabRect, Rectangle iconRect, Rectangle textRect, boolean isSelected) {
      textRect.x = textRect.y = iconRect.x = iconRect.y = 0;

      View v = getTextViewForTab(tabIndex);
      if (v != null) {
        tabPane.putClientProperty("html", v);
      }

      SwingUtilities.layoutCompoundLabel((JComponent)tabPane, metrics, title, icon,
          SwingUtilities.CENTER, SwingUtilities.CENTER, SwingUtilities.CENTER,
          SwingUtilities.RIGHT, tabRect, iconRect, textRect, textIconGap);

      tabPane.putClientProperty("html", null);

      int xNudge = getTabLabelShiftX(tabPlacement, tabIndex, isSelected);
      int yNudge = getTabLabelShiftY(tabPlacement, tabIndex, isSelected);
      iconRect.x += xNudge - (closeIcons != null && tabIndex < closeIcons.size() && closeIcons.get(tabIndex) != null ? 10 : 0);
      iconRect.y += yNudge;
      textRect.x += xNudge - (closeIcons != null && tabIndex < closeIcons.size() && closeIcons.get(tabIndex) != null ? 10 : 0);
      textRect.y += yNudge;
    }

    @Override
    protected void paintTab(Graphics g, int tabPlacement, Rectangle[] rects, int tabIndex,
        Rectangle iconRect, Rectangle textRect) {
      super.paintTab(g, tabPlacement, rects, tabIndex, iconRect, textRect);
      if (closeIcons != null && tabIndex < closeIcons.size() && closeIcons.get(tabIndex) != null) {
        Rectangle rect = iconRect;
        rect.x = rects[tabIndex].x + rects[tabIndex].width - 20;
        paintIcon(g, tabPlacement, tabIndex, closeIcons.get(tabIndex), rect, tabPane.getSelectedIndex() == tabIndex);
      }
    }

  }

  protected CloseTabbedPane(boolean canClose) {
    super();
    if (canClose) {
      this.closeIcons = new ArrayList<CloseTabIcon>();
    }
    setLayout(new BorderLayout());
    setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
    setUI(new CloseTabbedPaneUI());
    addMouseListener(this);
    addMouseMotionListener(this);
    
    this.popup = new JPopupMenu("TabPane"); 
    final CloseTabbedPane tabPane = this;
    this.popup.addPopupMenuListener(new PopupMenuListener() {
      public void popupMenuCanceled(PopupMenuEvent event) {
      }
      public void popupMenuWillBecomeInvisible(PopupMenuEvent event) {
      }
      public void popupMenuWillBecomeVisible(PopupMenuEvent event) {
        if (event == null || !(event.getSource() instanceof JPopupMenu)) {
          return;
        }
        JPopupMenu popupMenu = (JPopupMenu)event.getSource();
        if (popupMenu.getComponentCount() > 0) {
          for (int count = 0; count < popupMenu.getComponentCount(); count++) {
            if (popupMenu.getComponent(count) instanceof ActionMenuItem) {
              ActionMenuItem action = (ActionMenuItem)popupMenu.getComponent(count);
              if (action.getAction().getValue(Action.NAME) instanceof String &&
                  "Close others".equals(action.getAction().getValue(Action.NAME))) {
                action.setEnabled(tabPane.getTabCount() > 1);
              }
            }
          }
        }
      }      
    });
    this.popup.add(new CloseAction(this));
    this.popup.add(new ActionMenuItem(new ActionBase("Close others", (char)0, null, null, "Close other editors", null) {    
      private static final long serialVersionUID = -6033343083851801737L;

      @Override
      public void actionPerformed(ActionEvent event) {
        if (tabPane != null && tabPane.getTabCount() > 1 && tabPane.canClose()) {
          int current = tabPane.getSelectedIndex();
          if (current != -1) {
            int index = current - 1;
            while(index > -1) {
              tabPane.remove(index--);
            }
            while (tabPane.getTabCount() > 1) {
              tabPane.remove(tabPane.getTabCount() - 1);
            }
          }
        }
      }
    }));
    this.popup.add(new CloseAllAction(this));
  }

  protected CloseTabbedPane() {
    this(true);
  }

  public void mouseClicked(MouseEvent event) {    
    if (event == null || !(event.getSource() instanceof CloseTabbedPane)) {
      return;
    }
    int tabIndex = getUI().tabForCoordinate(this, event.getX(), event.getY());
    if (event.getButton() == MouseEvent.BUTTON3 && this.closeIcons != null) {
      popup.show(event.getComponent(), event.getX(), event.getY());
    }
    if (event.getButton() == MouseEvent.BUTTON1) {
      switch (event.getClickCount()) {
        case 1:
          if (closeIcons != null && tabIndex > -1 && tabIndex < closeIcons.size() &&
              closeIcons.get(tabIndex).getBounds().contains(event.getX(), event.getY())) {
            removeTabAt(tabIndex);
          }
          break;
        case 2:
          if (getUI().tabForCoordinate(this, event.getX(), event.getY()) == -1
              && event.getButton() == MouseEvent.BUTTON1 && event.getClickCount() == 2) {
            switch (event.getModifiers() & (Event.CTRL_MASK | Event.SHIFT_MASK)) {
              case Event.CTRL_MASK | Event.SHIFT_MASK:
                maximize();
                break;
              case Event.CTRL_MASK:
                if (getSplitPane() != null) {
                  if (getSplitPane().getDividerLocation() > 1) {
                    getSplitPane().setDividerLocation(0);
                  }
                  else {
                    getSplitPane().setDividerLocation(getSplitPane().getLastDividerLocation());
                  }
                }
                break;
              default:
                if (getAction() != null) {
                  getAction().actionPerformed(null);
                }
                break;
            }
          }
          break;
      }
    }
  }

  public void mouseEntered(MouseEvent event) {
  }

  public void mouseExited(MouseEvent event) {
  }

  public void mousePressed(MouseEvent event) {
  }

  public void mouseReleased(MouseEvent event) {
  }

  public void mouseDragged(MouseEvent e) {
  }

  public void mouseMoved(MouseEvent event) {
    if (closeIcons != null && !closeIcons.isEmpty()) {
      int tabIndex = getUI().tabForCoordinate(this, event.getX(), event.getY());
      if (tabIndex > -1 && tabIndex < getTabCount() && getTabCount() > 0) {
        closeIcons.get(tabIndex).setCloseIcon(tabIndex > -1 ?
            closeIcons.get(tabIndex).getBounds().contains(event.getX(), event.getY()) ? 
                IconsItem.ICON_CLOSE_APP : IconsItem.ICON_CLOSE_APP_DISABLE : 
                  IconsItem.ICON_CLOSE_APP_DISABLE);
      }
      repaint();
    }
  }

  abstract protected JSplitPane getSplitPane();

  abstract protected Action getAction();
  
  abstract protected void maximize();

  protected Window getOwner() {
    return SwingUtilities.windowForComponent(this);
  }
  
  @Override
  public void addTab(String title, Component component) {
    super.addTab(title, component);
    if (closeIcons != null) {
      closeIcons.add(new CloseTabIcon());
    }
  }
  
  @Override
  public void addTab(String title, Icon icon, Component component) {
    super.addTab(title, icon, component);
    if (closeIcons != null) {
      closeIcons.add(new CloseTabIcon());
    }
  }
  
  @Override
  public void addTab(String title, Icon icon, Component component, String tip) {
    super.addTab(title, icon, component, tip);
    if (closeIcons != null) {
      closeIcons.add(new CloseTabIcon());
    }
  }
  
  public void insertTab(String title, Icon icon, JComponent component, String tip, int index) {
    super.insertTab(title, icon, component, tip, index);
    if (closeIcons != null) {
      closeIcons.add(new CloseTabIcon());
    }
  }
  
  public void remove(int index) {
    super.remove(index);
    if (closeIcons != null && !closeIcons.isEmpty() && index < closeIcons.size()) {
      closeIcons.remove(index);
    }
  }
  
  public void addFireDataListener(DataListener listener) {
    if (fireData == null) {
      fireData = new FireData();
    }
    fireData.addDataListener(listener);
  }
  
  public void removeFireDataListener(DataListener listener) {
    if (fireData != null) {
      fireData.removeDataListener(listener);
    }
  }
  
  public void fireData(DataEvent<?> event) {
    if (fireData != null) {
      fireData.onData(event);
    }
  }
  
  public boolean canClose() {
    return closeIcons != null;
  }
}