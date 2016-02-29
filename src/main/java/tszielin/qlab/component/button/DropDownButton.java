package tszielin.qlab.component.button;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Action;
import javax.swing.DefaultButtonModel;
import javax.swing.Icon;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import tszielin.qlab.action.editor.NewFileAction;
import tszielin.qlab.action.editor.OpenFileAction;
import tszielin.qlab.component.pane.EditorsTabbedPane;
import tszielin.qlab.config.data.EditorFile;
import tszielin.qlab.error.FileException;
import tszielin.qlab.event.FileListChanged;
import tszielin.qlab.util.component.button.IconButton;
import tszielin.qlab.util.event.DataEvent;
import tszielin.qlab.util.listener.DataListener;

public class DropDownButton extends IconButton implements DataListener {
  private static final long serialVersionUID = 6932069891793776871L;
  private boolean mouseInButton = false;
  private boolean mouseInArrowArea = false;

  private Map<String, Icon> regIcons;
  private Map<String, Icon> arrowIcons;

  private static final String ICON_NORMAL = "normal"; // NOI18N
  private static final String ICON_PRESSED = "pressed"; // NOI18N
  private static final String ICON_ROLLOVER = "rollover"; // NOI18N
  private static final String ICON_ROLLOVER_SELECTED = "rolloverSelected"; // NOI18N
  private static final String ICON_SELECTED = "selected"; // NOI18N
  private static final String ICON_DISABLED = "disabled"; // NOI18N
  private static final String ICON_DISABLED_SELECTED = "disabledSelected"; // NOI18N

  private static final String ICON_ROLLOVER_LINE = "rolloverLine"; // NOI18N
  private static final String ICON_ROLLOVER_SELECTED_LINE = "rolloverSelectedLine"; // NOI18N

  private static final String PROP_DROP_DOWN_MENU = "dropDownMenu";

  private PopupMenuListener menuListener;

  /** Creates a new instance of MenuToggleButton */
  public DropDownButton(Action action, JPopupMenu popup) {
    super(action);
    assert action != null && !(action.getValue(Action.SMALL_ICON) instanceof Icon) : "Action cannot be null or icon must be defined";
    
    setComponentPopupMenu(popup);
    resetIcons();

    addPropertyChangeListener(PROP_DROP_DOWN_MENU, new PropertyChangeListener() {
      public void propertyChange(PropertyChangeEvent e) {
        resetIcons();
      }
    });

    addMouseMotionListener(new MouseMotionAdapter() {
      public void mouseMoved(MouseEvent e) {
        if (null != getComponentPopupMenu()) {
          mouseInArrowArea = isInArrowArea(e.getPoint());
          updateRollover(_getRolloverIcon(), _getRolloverSelectedIcon());
        }
      }
    });

    addMouseListener(new MouseAdapter() {
      private boolean popupMenuOperation = false;

      public void mousePressed(MouseEvent e) {
        popupMenuOperation = false;
        JPopupMenu menu = getComponentPopupMenu();
        if (menu != null && getModel() instanceof Model) {
          Model model = (Model)getModel();
          if (!model._isPressed()) {
            if (isInArrowArea(e.getPoint()) && menu.getComponentCount() > 0) {
              model._press();
              menu.addPopupMenuListener(getMenuListener());
              menu.show(DropDownButton.this, 0, getHeight());
              popupMenuOperation = true;
            }
          }
          else {
            model._release();
            menu.removePopupMenuListener(getMenuListener());
            popupMenuOperation = true;
          }
        }
      }

      @Override
      public void mouseReleased(MouseEvent e) {
        // If we done something with the popup menu, we should consume
        // the event, otherwise the button's action will be triggered.
        if (popupMenuOperation) {
          popupMenuOperation = false;
          e.consume();
        }
      }

      public void mouseEntered(MouseEvent e) {
        mouseInButton = true;
        if (hasPopupMenu()) {
          mouseInArrowArea = isInArrowArea(e.getPoint());
          updateRollover(_getRolloverIcon(), _getRolloverSelectedIcon());
        }
      }

      public void mouseExited(MouseEvent e) {
        mouseInButton = false;
        mouseInArrowArea = false;
        if (hasPopupMenu()) {
          updateRollover(_getRolloverIcon(), _getRolloverSelectedIcon());
        }
      }
    });

    setModel(new Model());
  }

  private PopupMenuListener getMenuListener() {
    if (null == menuListener) {
      menuListener = new PopupMenuListener() {
        public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
        }

        public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
          // If inside the button let the button's mouse listener
          // deal with the state. The popup menu will be hidden and
          // we should not show it again.
          if (!mouseInButton) {
            if (getModel() instanceof Model) {
              ((Model)getModel())._release();
            }
            JPopupMenu menu = getComponentPopupMenu();
            if (null != menu) {
              menu.removePopupMenuListener(this);
            }
          }
        }

        public void popupMenuCanceled(PopupMenuEvent e) {
        }
      };
    }
    return menuListener;
  }

  private void updateRollover(Icon rollover, Icon rolloverSelected) {
    super.setRolloverIcon(rollover);
    super.setRolloverSelectedIcon(rolloverSelected);
  }

  private void resetIcons() {
    Icon icon = regIcons.get(ICON_NORMAL);
    if (null != icon)
      setIcon(icon);

    icon = regIcons.get(ICON_PRESSED);
    if (null != icon)
      setPressedIcon(icon);

    icon = regIcons.get(ICON_ROLLOVER);
    if (null != icon)
      setRolloverIcon(icon);

    icon = regIcons.get(ICON_ROLLOVER_SELECTED);
    if (null != icon)
      setRolloverSelectedIcon(icon);

    icon = regIcons.get(ICON_SELECTED);
    if (null != icon)
      setSelectedIcon(icon);

    icon = regIcons.get(ICON_DISABLED);
    if (null != icon)
      setDisabledIcon(icon);

    icon = regIcons.get(ICON_DISABLED_SELECTED);
    if (null != icon)
      setDisabledSelectedIcon(icon);
  }

  private Icon _getRolloverIcon() {
    Icon icon = null;
    icon = arrowIcons.get(mouseInArrowArea ? ICON_ROLLOVER : ICON_ROLLOVER_LINE);
    if (null == icon) {
      Icon orig = regIcons.get(ICON_ROLLOVER);
      if (null == orig)
        orig = regIcons.get(ICON_NORMAL);
      icon = new IconWithArrow(orig, !mouseInArrowArea);
      arrowIcons.put(mouseInArrowArea ? ICON_ROLLOVER : ICON_ROLLOVER_LINE, icon);
    }
    return icon;
  }

  private Icon _getRolloverSelectedIcon() {
    Icon icon = null;
    icon = arrowIcons.get(mouseInArrowArea ? ICON_ROLLOVER_SELECTED : ICON_ROLLOVER_SELECTED_LINE);
    if (null == icon) {
      Icon orig = regIcons.get(ICON_ROLLOVER_SELECTED);
      if (null == orig)
        orig = regIcons.get(ICON_ROLLOVER);
      if (null == orig)
        orig = regIcons.get(ICON_NORMAL);
      icon = new IconWithArrow(orig, !mouseInArrowArea);
      arrowIcons.put(mouseInArrowArea ? ICON_ROLLOVER_SELECTED : ICON_ROLLOVER_SELECTED_LINE, icon);
    }
    return icon;
  }
  
  @Override
  public void setComponentPopupMenu(JPopupMenu popup) {
    firePropertyChange(PROP_DROP_DOWN_MENU, getComponentPopupMenu(), popup);
    super.setComponentPopupMenu(popup);
    if (getComponentPopupMenu() != null && getComponentPopupMenu().getComponentCount() > 0) {
      for (int count = 0; count < getComponentPopupMenu().getComponentCount(); count++) {
        if (getComponentPopupMenu().getComponent(count) instanceof JMenuItem) {
          final String name = ((JMenuItem)getComponentPopupMenu().getComponent(count)).getText().replaceAll("\\d\\s", "");
          ((JMenuItem)getComponentPopupMenu().getComponent(count)).addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent event) {
              if (getAction() instanceof NewFileAction) {
                try {
                  if ("Script".equals(name)) {
                    ((NewFileAction)getAction()).initDocument((EditorFile)null);
                  }
                }
                catch (FileException ex) {
                  JOptionPane.showMessageDialog(null, ex.getMessage(), "File error", 
                      JOptionPane.ERROR_MESSAGE);
                }
              }
              else {
                if (getAction() instanceof OpenFileAction) {
                  try {
                    ((OpenFileAction)getAction()).initDocument(name, true);
                  }
                  catch (FileException ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage(), "File error",
                        JOptionPane.ERROR_MESSAGE);
                  }
                }
              }
            }
          });
        }
      }
    }
  }

  boolean hasPopupMenu() {
    return null != getComponentPopupMenu();
  }

  private boolean isInArrowArea(Point p) {
    return p.getLocation().x >= getWidth() - IconWithArrow.getArrowAreaWidth() - getInsets().right;
  }

  @Override
  public void setIcon(Icon icon) {
    assert null != icon;
    if (regIcons == null) {
      regIcons = new HashMap<String, Icon>(5);
    }
    if (arrowIcons == null) {
      arrowIcons = new HashMap<String, Icon>(5);
    }
    Icon arrow = updateIcons(icon, ICON_NORMAL);
    arrowIcons.remove(ICON_ROLLOVER_LINE);
    arrowIcons.remove(ICON_ROLLOVER_SELECTED_LINE);
    arrowIcons.remove(ICON_ROLLOVER);
    arrowIcons.remove(ICON_ROLLOVER_SELECTED);
    super.setIcon(hasPopupMenu() ? arrow : icon);
  }

  private Icon updateIcons(Icon orig, String iconType) {
    Icon arrow = null;
    if (null == orig) {
      regIcons.remove(iconType);
      arrowIcons.remove(iconType);
    }
    else {
      regIcons.put(iconType, orig);
      arrow = new IconWithArrow(orig, false);
      arrowIcons.put(iconType, arrow);
    }
    return arrow;
  }

  @Override
  public void setPressedIcon(Icon icon) {
    Icon arrow = updateIcons(icon, ICON_PRESSED);
    super.setPressedIcon(hasPopupMenu() ? arrow : icon);
  }

  @Override
  public void setSelectedIcon(Icon icon) {
    Icon arrow = updateIcons(icon, ICON_SELECTED);
    super.setSelectedIcon(hasPopupMenu() ? arrow : icon);
  }

  @Override
  public void setRolloverIcon(Icon icon) {
    Icon arrow = updateIcons(icon, ICON_ROLLOVER);
    arrowIcons.remove(ICON_ROLLOVER_LINE);
    arrowIcons.remove(ICON_ROLLOVER_SELECTED_LINE);
    super.setRolloverIcon(hasPopupMenu() ? arrow : icon);
  }

  @Override
  public void setRolloverSelectedIcon(Icon icon) {
    Icon arrow = updateIcons(icon, ICON_ROLLOVER_SELECTED);
    arrowIcons.remove(ICON_ROLLOVER_SELECTED_LINE);
    super.setRolloverSelectedIcon(hasPopupMenu() ? arrow : icon);
  }

  @Override
  public void setDisabledIcon(Icon icon) {
    Icon arrow = updateIcons(icon, ICON_DISABLED);
    super.setDisabledIcon(hasPopupMenu() ? arrow : icon);
  }

  @Override
  public void setDisabledSelectedIcon(Icon icon) {
    Icon arrow = updateIcons(icon, ICON_DISABLED_SELECTED);
    super.setDisabledSelectedIcon(hasPopupMenu() ? arrow : icon);
  }

  private class Model extends DefaultButtonModel {
    private static final long serialVersionUID = 704683116480968163L;
    private boolean _pressed = false;

    public void setPressed(boolean b) {
      if (mouseInArrowArea || _pressed)
        return;
      super.setPressed(b);
    }

    public void _press() {
      if ((isPressed()) || !isEnabled()) {
        return;
      }

      stateMask |= PRESSED + ARMED;

      fireStateChanged();
      _pressed = true;
    }

    public void _release() {
      _pressed = false;
      mouseInArrowArea = false;
      setArmed(false);
      setPressed(false);
      setRollover(false);
      setSelected(false);
    }

    public boolean _isPressed() {
      return _pressed;
    }

    @Override
    protected void fireStateChanged() {
      if (_pressed)
        return;
      super.fireStateChanged();
    }

    @Override
    public void setArmed(boolean b) {
      if (_pressed)
        return;
      super.setArmed(b);
    }

    @Override
    public void setEnabled(boolean b) {
      if (_pressed)
        return;
      super.setEnabled(b);
    }

    @Override
    public void setSelected(boolean b) {
      if (_pressed)
        return;
      super.setSelected(b);
    }

    @Override
    public void setRollover(boolean b) {
      if (_pressed)
        return;
      super.setRollover(b);
    }
  }

  public void onData(DataEvent<?> event) {
    if (event instanceof FileListChanged && getAction().getClass() == OpenFileAction.class) {
      if (event.getSource() instanceof EditorsTabbedPane) {
        List<EditorFile> list = ((FileListChanged)event).getData();
        if (list == null || list.isEmpty()) {
          setComponentPopupMenu(null);
        }
        else {
          JPopupMenu fileMenu = new JPopupMenu();
          int pos = 0;
          for (EditorFile file : list) {
            fileMenu.add(new JMenuItem((char)(pos + 48) + " " + file.getPath(), pos + 48));
            if (++pos > 9) {
              break;
            }
          }
          setComponentPopupMenu(fileMenu);
        }
      }
    }
    
  }
}