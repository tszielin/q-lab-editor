package tszielin.qlab.component.tree.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import tszielin.qlab.component.tree.comparator.ConnectionNodesComparator;

public class KdbServicesTreeModel extends DefaultTreeModel {
  private static final long serialVersionUID = 8963257453929310322L;

  public KdbServicesTreeModel(TreeNode root) {
    super(root);
  }

  public int getIndexOfChild(Object parent, Object child) {
    orderChildren(parent);
    return super.getIndexOfChild(parent, child);
  }

  public Object getChild(Object parent, int index) {
    orderChildren(parent);
    return super.getChild(parent, index);
  }

  /**
   * Orders the children of a DefaultMutableTreeNode
   * 
   * @param parent
   */
  @SuppressWarnings("unchecked")
  private void orderChildren(Object parent) {
    if (parent == null) {
      return;
    }

    if (parent instanceof DefaultMutableTreeNode) {
      ArrayList<DefaultMutableTreeNode> children = Collections.<DefaultMutableTreeNode> list(((DefaultMutableTreeNode)parent).children());
      Collections.sort(children, new ConnectionNodesComparator());
      ((DefaultMutableTreeNode)parent).removeAllChildren();
      Iterator<DefaultMutableTreeNode> childrenIterator = children.iterator();
      while (childrenIterator.hasNext()) {
        ((DefaultMutableTreeNode)parent).add((DefaultMutableTreeNode)childrenIterator.next());
      }
    }
  }

}
