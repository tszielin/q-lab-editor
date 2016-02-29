package tszielin.qlab.renderer;

import java.awt.Component;
import java.io.File;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import tszielin.qlab.component.Iconable;
import tszielin.qlab.component.Tooltipable;
import tszielin.qlab.util.image.IconsItem;

public class ProjectTreeCellRender extends DefaultTreeCellRenderer {
  private static final long serialVersionUID = -7953180220689227249L;

  public ProjectTreeCellRender() {
    super();
  }

  public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected,
      boolean expanded, boolean leaf, int row, boolean hasFocus) {
    Component component = super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
    if (component instanceof DefaultTreeCellRenderer) {
      DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer)component;
      if ((value != null) && (value instanceof File)) {
        if (value instanceof Iconable) {
          Icon icon = ((Iconable)value).getIcon();
          if (icon == null) {
            renderer.setOpenIcon(IconsItem.ICON_FOLDER);
            renderer.setClosedIcon(IconsItem.ICON_FOLDER);
          }
          else {
            renderer.setIcon(icon);
          }
        }
        if (value instanceof Tooltipable) {
          renderer.setToolTipText(((Tooltipable)value).getTooltip());
        }
      }
    }
    return component;
//    
//    return result;
//    if (result == null) {
//      return super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
//    if (component instanceof DefaultTreeCellRenderer) {
//      DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer)component;
//      DefaultMutableTreeNode node = value instanceof DefaultMutableTreeNode
//          ? (DefaultMutableTreeNode)value : null;
//      if (node != null) {
//        if (node instanceof ProjectTreeNode) {
//          if (node.getUserObject() instanceof Project) {
//            renderer.setIcon(((Project)node.getUserObject()).isClosed()
//                ? IconsItem.ICON_FOLDER_DISABLE : IconsItem.ICON_FOLDER_CLOSE);
//          }
//        }
//        else {
//          if (node instanceof ProjectReferenceTreeNode) {
//            if (!node.isLeaf() && node.getUserObject() instanceof String) {
//              renderer.setIcon(IconsItem.ICON_FOLDER_REF);
//            }
//            else {
//              if (node.getUserObject() instanceof File) {
//                renderer.setLeafIcon(((File)node.getUserObject()).exists() ? IconsItem.ICON_FILE_REF
//                    : IconsItem.ICON_RED_DOT);
//                renderer.setToolTipText(((File)node.getUserObject()).getPath() +
//                    (((File)node.getUserObject()).exists() ? "" : " not exists"));
//              }
//            }
//          }
//          else {
//            if (node instanceof ProjectFileTreeNode) {
//              if (node.getUserObject() instanceof String) {
//                renderer.setIcon(IconsItem.ICON_FOLDER_OPEN);
//              }
//              else {
//                if (node.getUserObject() instanceof File) {
//                  renderer.setLeafIcon(((File)node.getUserObject()).exists()
//                      ? IconsItem.ICON_EDITOR : IconsItem.ICON_RED_DOT);
//                  renderer.setToolTipText(((File)node.getUserObject()).getPath() +
//                      (((File)node.getUserObject()).exists() ? "" : " not exists"));
//                }
//                else {
//                  renderer.setLeafIcon(null);
//                  renderer.setToolTipText(null);
//                }
//              }
//            }
////            if (node.getUserObject() instanceof String) {
////              renderer.setToolTipText(null);
////              renderer.setToolTipText(null);
////            }
//          }
//        }
//      }
//      else {
//        renderer.setLeafIcon(null);
//        renderer.setToolTipText(null);
//      }
////      return renderer;
//    }
//    return component;
  }
}
