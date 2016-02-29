package tszielin.qlab.adapter;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTree;
import javax.swing.tree.TreePath;

import tszielin.qlab.component.pane.EditorsTabbedPane;
import tszielin.qlab.component.tree.item.FileItem;
import tszielin.qlab.component.tree.item.FileProject;
import tszielin.qlab.event.FileChoosed;
import tszielin.qlab.util.event.FireData;
import tszielin.qlab.util.listener.DataListener;

public class ProjectTreeMouseAdapter extends MouseAdapter {
  private JTree tree;
  private FireData fireData;

  public ProjectTreeMouseAdapter(JTree tree, EditorsTabbedPane tabEditors) {
    this.tree = tree;
    if (tabEditors instanceof DataListener) {
      fireData = new FireData();
      fireData.addDataListener((DataListener)tabEditors);
    }
  }

  @Override
  public void mousePressed(MouseEvent e) {
    if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2) {
      TreePath path = tree.getPathForLocation(e.getX(), e.getY());
      if (path != null && path.getLastPathComponent() instanceof FileItem && fireData != null) {
        TreePath parent = path.getParentPath();
        while (parent != null && !(parent.getLastPathComponent() instanceof FileProject)) {
          if (parent.getParentPath() == null) {
            break;
          }
          parent = parent.getParentPath();
        }
        fireData.onData(new FileChoosed(this, (FileItem)path.getLastPathComponent(),
            parent == null ? null : 
              ((FileProject)parent.getLastPathComponent()).getProject().getConnection()));
      }
    }
  }
}
