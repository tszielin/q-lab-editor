package tszielin.qlab.component.pane;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;

import tszielin.qlab.component.tree.KdbServicesTree;
import tszielin.qlab.component.tree.ProjectTree;
import tszielin.qlab.util.action.ActionBase;
import tszielin.qlab.util.image.IconsItem;

public class ToolsPanel extends JPanel {  
  private static final long serialVersionUID = -1159383622655349768L;

  private JSplitPane splitPane;
  private double divider = .5d;
  
  public ToolsPanel() {
    super(new BorderLayout());
    splitPane = new JSplitPane();
    add(splitPane, BorderLayout.CENTER);
    splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
    splitPane.setDividerSize(9);
    splitPane.setDividerLocation(divider);
    splitPane.setOneTouchExpandable(true);
    
    this.addComponentListener(new ComponentAdapter() {
      @Override
      public void componentResized(ComponentEvent event) {
        if (event.getComponent() instanceof ToolsPanel) {
          if (splitPane != null && divider != 0) {
            splitPane.setDividerLocation(divider);
          }
        }
      }
    });
    
    Action action = new ActionBase(null, (char)0, null, KeyStroke.getKeyStroke("control shift M")) {
      private static final long serialVersionUID = 6489474785348068954L;

      public void actionPerformed(ActionEvent e) {
        if (splitPane != null) {
          if (splitPane.getDividerLocation() > 1) {
            splitPane.setDividerLocation(0);
          }
          else {
            splitPane.setDividerLocation(splitPane.getLastDividerLocation());
          }
        }        
      }
    };    
    registerKeyboardAction(action, ((ActionBase)action).getAccelerator(), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
  }
  
  public void setProjectView(ProjectTree projects) {
    if (!(splitPane.getBottomComponent() instanceof JTabbedPane)) {
      splitPane.setBottomComponent(addProjects(projects, IconsItem.ICON_CONFIG));
    }
  }
  
  public void setConnectionsView(KdbServicesTree connections) {
    if (!(splitPane.getTopComponent() instanceof JTabbedPane)) {
      splitPane.setTopComponent(addServers(connections, IconsItem.ICON_COMPUTER));
      connections.addComponentListener(new ComponentAdapter() {
        @Override
        public void componentResized(ComponentEvent event) {
          if (event.getComponent() instanceof KdbServicesTree) {
            if (splitPane.getDividerLocation() > 0) {
              divider = splitPane.getDividerLocation() * 1d /
                  splitPane.getParent().getHeight();
            }
          }
        }
      });
    }
  }
  
  private JTabbedPane addServers(Component component, Icon icon) {
    return new KdbServicesTabbedPane(component, icon);
  }
  
  private JTabbedPane addProjects(Component component, Icon icon) {
    return new ProjectsTabbedPane(component, icon);
  }
}
