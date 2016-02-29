package tszielin.qlab.component;

import java.awt.datatransfer.*;
import java.io.IOException;

import javax.swing.tree.DefaultMutableTreeNode;

public class TransferableTreeNode extends DefaultMutableTreeNode implements Transferable {
  private static final long serialVersionUID = 7715805100997868659L;
  
  private final static DataFlavor flavors[] = {
      new DataFlavor(DefaultMutableTreeNode.class, "application/x-java-serialized-object"),
      new DataFlavor(DefaultMutableTreeNode.class, "application/x-java-file-list")};

  public TransferableTreeNode(DefaultMutableTreeNode data) {
    super();
    setUserObject(data != null ? data.getUserObject() : null);
  }
  
  public TransferableTreeNode(Object data) {
    super();
    setUserObject(data);
  }

  public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
    return getUserObject();
  }

  public DataFlavor[] getTransferDataFlavors() {
    return flavors;
  }

  public boolean isDataFlavorSupported(DataFlavor flavor) {
    boolean returnValue = false;
    for (int i = 0, n = flavors.length; i < n; i++) {
      if (flavor.equals(flavors[i])) {
        returnValue = true;
        break;
      }
    }
    return returnValue;
  }
}