package tszielin.qlab.component.pane;

import java.awt.Component;
import java.awt.Event;
import java.awt.event.MouseEvent;

import javax.swing.*;

public class KdbServicesTabbedPane extends CloseTabbedPane {
  private static final long serialVersionUID = -1801878879652324102L;

  public KdbServicesTabbedPane(Component component, Icon icon) {
    super(false);
    addTab("kdb Servers", new JScrollPane(component));
    setIconAt(0, icon);
  }

  @Override
  protected Action getAction() {
    return null;
  }

  @Override
  protected JSplitPane getSplitPane() {
    return getParent() instanceof JSplitPane ? (JSplitPane)getParent() : null;
  }
  
  @Override
  public void mouseClicked(MouseEvent event) {
    if (event == null || !(event.getSource() instanceof CloseTabbedPane)) {
      return;
    }
    if (event.getButton() == MouseEvent.BUTTON1 && event.getClickCount() == 2 &&
        getUI().tabForCoordinate(this, event.getX(), event.getY()) == -1) {
      switch (event.getModifiers() & Event.CTRL_MASK) {
        case Event.CTRL_MASK:
          if (getSplitPane() != null) {
            if (getSplitPane().getDividerLocation() < getSplitPane().getParent().getSize().height - 11) {
              getSplitPane().setDividerLocation(getSplitPane().getParent().getSize().height);
            }
            else {
              getSplitPane().setDividerLocation(getSplitPane().getLastDividerLocation());
            }
          }
          break;
      }
    }
  }
  
  protected void maximize() {    
  }
}
