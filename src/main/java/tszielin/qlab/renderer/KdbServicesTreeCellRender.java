package tszielin.qlab.renderer;

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import tszielin.qlab.component.Iconable;
import tszielin.qlab.component.Tooltipable;

public class KdbServicesTreeCellRender extends DefaultTreeCellRenderer {
  private static final long serialVersionUID = 6323615511668932609L;
 
  public KdbServicesTreeCellRender() {
    super();        
  }

  public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel,
      boolean expanded, boolean leaf, int row, boolean hasFocus) {
    Component component = super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
    if (component instanceof DefaultTreeCellRenderer) {
      if (value instanceof Tooltipable) {
        setToolTipText(((Tooltipable)value).getTooltip());
      }
      if (value instanceof Iconable) {
        setIcon(((Iconable)value).getIcon());
      }
      setBackground(selected ?
          ((DefaultTreeCellRenderer)component).getBackgroundSelectionColor() :
            ((DefaultTreeCellRenderer)component).getBackgroundNonSelectionColor());
    }
    return component;
  }
}
