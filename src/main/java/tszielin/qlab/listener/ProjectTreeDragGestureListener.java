package tszielin.qlab.listener;

import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTree;
import javax.swing.tree.TreePath;

import tszielin.qlab.component.TransferableTreeNode;
import tszielin.qlab.component.tree.item.FileItem;

public class ProjectTreeDragGestureListener implements DragGestureListener {
  public ProjectTreeDragGestureListener() {
  }

  public void dragGestureRecognized(DragGestureEvent event) {
    JTree tree = event.getComponent() instanceof JTree ?
        (JTree)event.getComponent() : null;
    if (tree != null && tree.getSelectionPaths() != null) {
      List<File> selections = null;
      for (TreePath path : tree.getSelectionPaths()) {
        File selection = path.getLastPathComponent() instanceof FileItem ? 
            (FileItem)path.getLastPathComponent() : null;
        if (selection != null && selection.isFile()) {
          if (selections == null) {
            selections = new ArrayList<File>();
          }
          selections.add(selection);
        }
      }
      if (selections != null && !selections.isEmpty()) {
        TransferableTreeNode node = new TransferableTreeNode(selections);
        event.startDrag(DragSource.DefaultCopyDrop, node, new TreeDragSourceListener());
      }
    }
  }
}
