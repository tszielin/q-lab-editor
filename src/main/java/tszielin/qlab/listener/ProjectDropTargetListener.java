package tszielin.qlab.listener;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.IOException;

import javax.swing.JTree;
import javax.swing.tree.TreePath;

import tszielin.qlab.component.pane.CloseTabbedPane;
import tszielin.qlab.component.tree.item.FileProject;
import tszielin.qlab.config.data.KdbService;
import tszielin.qlab.event.ProjectConnectionChanged;
import tszielin.qlab.util.event.FireData;
import tszielin.qlab.util.listener.DataListener;

public class ProjectDropTargetListener implements DropTargetListener {
  private FireData fireData;
  private JTree tree;
  
  public ProjectDropTargetListener(JTree tree) {
    this.tree = tree;
    if (this.tree instanceof DataListener) {
      this.fireData = new FireData();
      this.fireData.addDataListener((DataListener)this.tree);
    }
  }

  public void dragEnter(DropTargetDragEvent event) {
  }

  public void dragExit(DropTargetEvent event) {
  }

  public void dragOver(DropTargetDragEvent event) {
  }

  public void drop(DropTargetDropEvent event) {
    try {
      Transferable transferable = event.getTransferable();
      DataFlavor[] flavors = transferable.getTransferDataFlavors();
      if (flavors != null) {
        for (DataFlavor flavor : flavors) {
          if (flavor.isFlavorSerializedObjectType()) {
            event.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
            Object data = transferable.getTransferData(flavor);
            if (event.getDropTargetContext().getComponent() instanceof CloseTabbedPane) {
              TreePath[] paths = tree.getSelectionPaths(); 
              if (paths != null && data instanceof KdbService) {
                for (TreePath path : paths) {
                  TreePath src = path.getLastPathComponent() instanceof FileProject ?
                      path : null;
                  while (src == null) {
                    if (path.getParentPath() == null) {
                      break; 
                    }
                    if (path.getParentPath().getLastPathComponent() instanceof FileProject) {
                      src = path.getParentPath();
                    }
                  }
                  if (fireData != null) {
                    fireData.onData(new ProjectConnectionChanged(this, (KdbService)data, path));
                  }
                }
              }
            }
            event.dropComplete(true);
            return;
          }
        }
      }
      else {
        event.rejectDrop();
      }
    }
    catch (IOException io) {
      event.rejectDrop();
    }
    catch (UnsupportedFlavorException ufe) {
      event.rejectDrop();
    }
  }

  public void dropActionChanged(DropTargetDragEvent event) {
  }
}
