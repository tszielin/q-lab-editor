package tszielin.qlab.component.tree.comparator;

import java.util.Comparator;

import javax.swing.tree.DefaultMutableTreeNode;

import tszielin.qlab.component.tree.node.HostTreeNode;
import tszielin.qlab.component.tree.node.KdbServicesTreeNode;

public class ConnectionNodesComparator implements Comparator<DefaultMutableTreeNode> {

  public int compare(DefaultMutableTreeNode node1, DefaultMutableTreeNode node2) {
    if (node1 == null && node2 == null) {
      return 0;
    }
    if (node1 == null && node2 != null) {
      return 1;
    }
    if (node1 != null && node2 == null) {
      return -1;
    }
    if (node1 instanceof HostTreeNode && node2 instanceof HostTreeNode) {
      return ((HostTreeNode)node1).compareTo((HostTreeNode)node2);
    }
    if (node1 instanceof KdbServicesTreeNode && node2 instanceof KdbServicesTreeNode) {
      return ((KdbServicesTreeNode)node1).compareTo((KdbServicesTreeNode)node2);
    }
    return String.valueOf(node1).compareToIgnoreCase(String.valueOf(node2));
  }
}
