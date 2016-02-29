package tszielin.qlab.listener;

import java.awt.dnd.*;

public class TreeDragSourceListener implements DragSourceListener {

  public TreeDragSourceListener() {
  }

  public void dragDropEnd(DragSourceDropEvent event) {
  }

  public void dragEnter(DragSourceDragEvent event) {
    DragSourceContext context = event.getDragSourceContext();
    int dropAction = event.getDropAction();
    if ((dropAction & DnDConstants.ACTION_COPY) != 0) {
      context.setCursor(DragSource.DefaultCopyDrop);
    }
    else {
      if ((dropAction & DnDConstants.ACTION_MOVE) != 0) {
        context.setCursor(DragSource.DefaultMoveDrop);
      }
      else {
        context.setCursor(DragSource.DefaultCopyNoDrop);
      }
    }
  }

  public void dragExit(DragSourceEvent event) {
  }

  public void dragOver(DragSourceDragEvent event) {
  }

  public void dropActionChanged(DragSourceDragEvent event) {
  }
}
