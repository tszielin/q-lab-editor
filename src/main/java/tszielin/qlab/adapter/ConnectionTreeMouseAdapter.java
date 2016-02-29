package tszielin.qlab.adapter;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

import tszielin.qlab.action.connection.LaunchQAction;
import tszielin.qlab.component.pane.EditorsTabbedPane;
import tszielin.qlab.component.tree.node.KdbServicesTreeNode;
import tszielin.qlab.config.data.KdbService;
import tszielin.qlab.dialog.KdbServiceDialog;
import tszielin.qlab.event.KdbServiceChanged;
import tszielin.qlab.util.event.FireData;
import tszielin.qlab.util.listener.DataListener;

public class ConnectionTreeMouseAdapter extends MouseAdapter {
  private JTree tree;
  private FireData fireData;
  private EditorsTabbedPane tabEditors;

  public ConnectionTreeMouseAdapter(JTree tree, EditorsTabbedPane tabEditors) {
    this.tree = tree;
    this.tabEditors = tabEditors;
    if (this.tabEditors instanceof DataListener) {
      fireData = new FireData();
      fireData.addDataListener((DataListener)this.tabEditors);
    }
  }

  @Override
  public void mousePressed(MouseEvent event) {
    if (event.getClickCount() == 2 && event.getButton() == MouseEvent.BUTTON1) {
      TreePath path = tree.getPathForLocation(event.getX(), event.getY());
      if (path != null && path.getLastPathComponent() instanceof KdbServicesTreeNode) {
        KdbService connection = ((KdbServicesTreeNode)path.getLastPathComponent()).getUserObject() instanceof KdbService ? 
            (KdbService)((KdbServicesTreeNode)path.getLastPathComponent()).getUserObject() : null;
        if (connection != null) {
          if (!event.isControlDown()) {
            if (fireData != null) {
              fireData.onData(new KdbServiceChanged(this, connection));
            }
            if (this.tabEditors.getTabCount() > 0) {
              this.tabEditors.getEditor().requestFocus();
            }
          }
          else {
            if (tree.getComponentPopupMenu() != null) {
              for (int count = 0; count < tree.getComponentPopupMenu().getComponentCount(); count++) {
                if (tree.getComponentPopupMenu().getComponent(count) instanceof JMenuItem
                    && ((JMenuItem)tree.getComponentPopupMenu().getComponent(count)).getAction() instanceof LaunchQAction
                    && KdbServiceDialog.isLocalhost(connection.getHost())) {
                  ((JMenuItem)tree.getComponentPopupMenu().getComponent(count)).getAction().actionPerformed(null);
                }
              }
            }
          }
        }
      }
    }
  }
}
