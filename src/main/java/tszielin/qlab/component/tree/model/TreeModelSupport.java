package tszielin.qlab.component.tree.model;

import java.util.*;

import javax.swing.event.*;

public class TreeModelSupport {
  protected EventListenerList listeners;

  protected TreeModelSupport() {
    listeners = new EventListenerList();
  }

  public void addTreeModelListener(TreeModelListener listener) {
    if (listener != null && !contains(listener)) {
      listeners.add(TreeModelListener.class, listener);
    }
  }

  public void removeTreeModelListener(TreeModelListener listener) {
    if (listener != null && contains(listener)) {
      listeners.remove(TreeModelListener.class, listener);
    }
  }

  public void fireTreeNodesChanged(TreeModelEvent event) {
    if (listeners != null) {
      for (TreeModelListener listener : listeners.getListeners(TreeModelListener.class)) {        
        listener.treeNodesChanged(event);
      }
    }
  }

  public void fireTreeNodesInserted(TreeModelEvent event) {
    if (listeners != null) {
      for (TreeModelListener listener : listeners.getListeners(TreeModelListener.class)) {        
        listener.treeNodesInserted(event);
      }
    }
  }

  public void fireTreeNodesRemoved(TreeModelEvent event) {
    if (listeners != null) {
      for (TreeModelListener listener : listeners.getListeners(TreeModelListener.class)) {        
        listener.treeNodesRemoved(event);
      }
    }
  }

  public void fireTreeStructureChanged(TreeModelEvent event) {
    if (listeners != null) {
      for (TreeModelListener listener : listeners.getListeners(TreeModelListener.class)) {
        listener.treeStructureChanged(event);
      }
    }
  }
  
  protected boolean contains(TreeModelListener listener) {
    return listeners != null? 
        Arrays.asList(listeners.getListeners(TreeModelListener.class)).contains(listener) : false;
  }
}
