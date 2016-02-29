package tszielin.qlab.event;

import javax.swing.tree.TreePath;

import tszielin.qlab.config.data.KdbService;

public class ProjectConnectionChanged extends KdbServiceChanged {
  private static final long serialVersionUID = -5373094286490673368L;
  private TreePath treePath;

  public ProjectConnectionChanged(Object source, KdbService connection, TreePath treePath) {
    super(source, connection);
    this.treePath = treePath;
  }
  
  public TreePath getTreePath() {
    return treePath;
  }  
}
