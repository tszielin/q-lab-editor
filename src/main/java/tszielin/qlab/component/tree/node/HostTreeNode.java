package tszielin.qlab.component.tree.node;

import java.util.Collections;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

import tszielin.qlab.component.Iconable;
import tszielin.qlab.component.Tooltipable;
import tszielin.qlab.config.AppConfig;
import tszielin.qlab.config.data.KdbService;
import tszielin.qlab.config.data.Sort;
import tszielin.qlab.util.image.IconsItem;

public class HostTreeNode extends DefaultMutableTreeNode implements Comparable<Object>, Tooltipable, Iconable {
  private static final long serialVersionUID = -3031450482598125707L;
  
  public HostTreeNode(String hostname) {
    super(hostname);
  }
  
  public HostTreeNode(String hostname, Set<KdbService> connections) {
    super(hostname);    
    if (connections != null && !connections.isEmpty()) {
      for (KdbService connection : connections) {
        add(new KdbServicesTreeNode(connection));
      }
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public void insert(MutableTreeNode newChild, int childIndex) {
    super.insert(newChild, childIndex);
    Collections.sort(this.children);
  }

  @SuppressWarnings("unchecked")
  @Override
  public void add(MutableTreeNode newChild) {
    super.add(newChild);
    Collections.sort(this.children);
  }

  public int compareTo(Object obj) {
    if (getUserObject() instanceof KdbService && obj instanceof DefaultMutableTreeNode &&
        ((DefaultMutableTreeNode)obj).getUserObject() instanceof KdbService) {
      KdbService c1 = (KdbService)getUserObject();
      KdbService c2 = (KdbService)((DefaultMutableTreeNode)obj).getUserObject();
      if (c1 == null && c2 == null) {
        return 0;
      }
      if (c1 == null && c2 != null) {
        return 1;
      }
      if (c1 != null && c2 == null) {
        return -1;
      }
      int name = c1.getName() == null ? "".compareTo(c2.getName() == null ? "" : c2.getName())
          : c1.getName().compareTo(c2.getName() == null ? "" : c2.getName());
      int host = c1.getHost().compareTo(c2.getHost());
      int user = c1.getUsername().compareTo(c2.getUsername());
      Sort sort = null;
      try {
        sort = AppConfig.getConfig().getSortType();
      }
      catch (Exception ex) {
        sort = Sort.PORT;
      }
      switch (sort) {
        case PORT:
          return host == 0 ? c1.getPort() - c2.getPort() : host;
        case NAME:
          return host == 0 ? name : host;
        case USER:
          return host == 0 ? user == 0 ? c1.getPort() - c2.getPort() : user : host;
        default:
          return host == 0 ? user == 0 ? c1.getPort() - c2.getPort() : user : host;
      }
    }
    else {
      return String.valueOf(this).compareToIgnoreCase(String.valueOf(obj));
    }
  }

  public String getTooltip() {
    StringBuffer strBuf = new StringBuffer("<html><table cellspacing=\"0\" border=\"0\"><tr><td align=\"right\">kdb+ server:</td><td>");
    strBuf.append(getUserObject().toString()).append("</td></tr>"); 
    for (int count = 0; count < getChildCount(); count++) {
      if (getChildAt(count) instanceof KdbServicesTreeNode &&
          ((KdbServicesTreeNode)getChildAt(count)).getUserObject() instanceof KdbService) {
        strBuf.append(count == 0 ?
            "<tr><td align=\"right\">Processes:</td>" : "<tr><td align=\"right\"></td>");
        strBuf.append(((KdbService)((KdbServicesTreeNode)getChildAt(count)).getUserObject()).getServiceInfo());
        strBuf.append("</td></tr>");
      }
    }
    strBuf.append("</table></html>");    
    return strBuf.toString();
  }

  @Override
  public Icon getIcon() {
    return IconsItem.ICON_COMPUTER;
  }
}
