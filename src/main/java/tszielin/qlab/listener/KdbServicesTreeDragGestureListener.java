package tszielin.qlab.listener;

import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;

import javax.swing.JTree;

import tszielin.qlab.component.TransferableTreeNode;
import tszielin.qlab.component.tree.node.KdbServicesTreeNode;
import tszielin.qlab.config.data.KdbService;

public class KdbServicesTreeDragGestureListener implements DragGestureListener {
  public KdbServicesTreeDragGestureListener() {
  }

  public void dragGestureRecognized(DragGestureEvent event) {
    JTree tree = event.getComponent() instanceof JTree ?
        (JTree)event.getComponent() : null;
    if (tree != null && tree.getSelectionPath() != null && 
        tree.getSelectionPath().getLastPathComponent() instanceof KdbServicesTreeNode &&
        ((KdbServicesTreeNode)tree.getSelectionPath().getLastPathComponent()).getUserObject() instanceof KdbService) {
      TransferableTreeNode node = new TransferableTreeNode(
          (KdbService)((KdbServicesTreeNode)tree.getSelectionPath().getLastPathComponent()).getUserObject());
      event.startDrag(DragSource.DefaultCopyDrop, node, new TreeDragSourceListener());
    }
  }
}
