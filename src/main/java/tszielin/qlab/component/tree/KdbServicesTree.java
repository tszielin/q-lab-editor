package tszielin.qlab.component.tree;

import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragSource;
import java.util.Enumeration;

import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import tszielin.qlab.adapter.ConnectionTreeMouseAdapter;
import tszielin.qlab.component.editor.QEditor;
import tszielin.qlab.component.pane.EditorsTabbedPane;
import tszielin.qlab.component.popup.KdbServicesPopup;
import tszielin.qlab.component.tree.node.KdbServicesTreeNode;
import tszielin.qlab.config.data.KdbService;
import tszielin.qlab.event.ConnectionClosed;
import tszielin.qlab.event.EditorClosed;
import tszielin.qlab.event.KdbServiceChanged;
import tszielin.qlab.event.KdbServiceReplaced;
import tszielin.qlab.listener.KdbServicesTreeDragGestureListener;
import tszielin.qlab.util.event.DataEvent;
import tszielin.qlab.util.listener.DataListener;
import tszielin.qlab.util.listener.PopupListener;

public class KdbServicesTree extends JTree implements DataListener {
  private static final long serialVersionUID = 7661910780868271448L;

  public KdbServicesTree(EditorsTabbedPane tabEditors) {
    super();
    setRootVisible(false);
    DragSource dragSource = DragSource.getDefaultDragSource();
    dragSource.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_COPY,
        new KdbServicesTreeDragGestureListener());
    ToolTipManager.sharedInstance().registerComponent(this);
    setComponentPopupMenu(new KdbServicesPopup(this, tabEditors));
    addMouseListener(new PopupListener(getComponentPopupMenu()));
    addMouseListener(new ConnectionTreeMouseAdapter(this, tabEditors));
  }
  
  @Override
  public void onData(DataEvent<?> event) {
    if (event.getSource() instanceof EditorsTabbedPane) {
      if (event instanceof EditorClosed && ((EditorClosed)event).getData() instanceof QEditor) {
        TreeNode node = find(((QEditor)((EditorClosed)event).getData()).getConnection());
        if (node instanceof KdbServicesTreeNode) {
          ((KdbServicesTreeNode)node).remove((QEditor)((EditorClosed)event).getData());
          repaint();
        }
      }
    }
    else {
      if (event.getSource() instanceof QEditor) {
        if (event instanceof KdbServiceChanged && ((KdbServiceChanged)event).getData() != null) {
          TreeNode node = find(((KdbServiceChanged)event).getData());
          if (node instanceof DefaultMutableTreeNode) {
            DefaultMutableTreeNode point = (DefaultMutableTreeNode)node;
            if (point instanceof KdbServicesTreeNode && point.getUserObject() instanceof KdbService) {
              switch (((KdbServiceChanged)event).getData().getStatus()) {
                case NOT_CONNECTED:
                  ((KdbServicesTreeNode)point).remove((QEditor)event.getSource());
                  break;
                default:
                  ((KdbServicesTreeNode)point).add((QEditor)event.getSource());
                  break;
              }
              repaint();
            }
          }
        }
        else {
          if (event instanceof ConnectionClosed && event.getSource() instanceof QEditor
              && ((ConnectionClosed)event).getData() != null) {
            TreeNode node = find(((ConnectionClosed)event).getData());
            if (node instanceof DefaultMutableTreeNode) {
              DefaultMutableTreeNode point = (DefaultMutableTreeNode)node;
              if (point instanceof KdbServicesTreeNode
                  && point.getUserObject() instanceof KdbService) {
                ((KdbServicesTreeNode)point).add((QEditor)event.getSource());
                repaint();
              }
            }
          }
          else {
            if (event instanceof KdbServiceReplaced
                && ((KdbServiceReplaced)event).getPrevious() != null) {
              TreeNode node = find(((KdbServiceReplaced)event).getPrevious());
              if (node instanceof DefaultMutableTreeNode) {
                DefaultMutableTreeNode point = (DefaultMutableTreeNode)node;
                if (point instanceof KdbServicesTreeNode && point.getUserObject() instanceof KdbService) {
                  ((KdbServicesTreeNode)point).remove((QEditor)event.getSource());
                }
              }
            }
          }
        }
      }
    }
  }
  
  protected JTree getTree() {
    return this;
  }
  
  private TreeNode find(KdbService connection) {
    TreeNode root = this.getModel().getRoot() instanceof TreeNode ?
        (TreeNode)getModel().getRoot() : null;
    return find(root, connection);
  }

  @SuppressWarnings("unchecked")
  private TreeNode find(TreeNode node, KdbService connection) { // node is visited exactly once
    if (connection != null && node != null) {
      if (node.getChildCount() >= 0) {
        for (Enumeration<TreeNode> e = node.children(); e.hasMoreElements();) {
          TreeNode treeNode = e.nextElement();
          if (treeNode instanceof KdbServicesTreeNode) {
            if (((KdbServicesTreeNode)treeNode).getUserObject().equals(connection)) {
              return treeNode;
            }
          }
          TreeNode result = find(treeNode, connection);
          if (result != null) {
            return result;
          }
        }
      }
    }
    return null;
  }
}
