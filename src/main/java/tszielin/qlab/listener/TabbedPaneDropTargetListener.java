package tszielin.qlab.listener;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.JTabbedPane;

import tszielin.qlab.component.pane.CloseTabbedPane;
import tszielin.qlab.config.data.KdbService;
import tszielin.qlab.event.FileChoosed;
import tszielin.qlab.event.KdbServiceChanged;
import tszielin.qlab.util.event.FireData;
import tszielin.qlab.util.listener.DataListener;

public class TabbedPaneDropTargetListener implements DropTargetListener {
  private FireData fireData;
  
  public TabbedPaneDropTargetListener(JTabbedPane tabPane) {
    if (tabPane instanceof DataListener) {
      this.fireData = new FireData();
      this.fireData.addDataListener((DataListener)tabPane);
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
          if (flavor.isFlavorJavaFileListType()) {
            event.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
            if (transferable.getTransferData(flavor) instanceof List<?>) {
              for (Object item : (List<?>)transferable.getTransferData(flavor)) {
                if (item instanceof File && fireData != null) {
                  fireData.onData(new FileChoosed(this, (File)item, null));
                }
              }
            }
            event.dropComplete(true);
            return;
          }
          else {
            if (flavor.isFlavorSerializedObjectType()) {
              event.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
              Object data = transferable.getTransferData(flavor);
              if (event.getDropTargetContext().getComponent() instanceof CloseTabbedPane) {
                if (data instanceof KdbService) {
                  if (fireData != null) {
                    fireData.onData(new KdbServiceChanged(this, (KdbService)data));
                  }
                }
                if (data instanceof List<?>) {
                  for (Object obj : (List<?>)data) {
                    if (obj instanceof File && ((File)obj).isFile()) {
                      if (fireData != null) {
                        fireData.onData(new FileChoosed(this, (File)obj, null));
                      }
                    }
                  }
                }
              }
              event.dropComplete(true);
              return;
            }
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
