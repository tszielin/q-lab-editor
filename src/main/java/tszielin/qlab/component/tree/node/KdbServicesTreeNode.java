package tszielin.qlab.component.tree.node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.Icon;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

import tszielin.qlab.component.Iconable;
import tszielin.qlab.component.Tooltipable;
import tszielin.qlab.component.editor.QEditor;
import tszielin.qlab.config.AppConfig;
import tszielin.qlab.config.data.ConnectionStatus;
import tszielin.qlab.config.data.KdbService;
import tszielin.qlab.config.data.Sort;
import tszielin.qlab.util.image.IconsItem;

public class KdbServicesTreeNode extends DefaultMutableTreeNode implements Comparable<Object>, Tooltipable, Iconable {
  private static final long serialVersionUID = -3031450482598125707L;
  List<QEditor> list;
  
  public KdbServicesTreeNode(KdbService connection) {
    super(connection);
    list = new ArrayList<QEditor>();
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
  
  public void add(QEditor editor) {
    if (list.contains(editor)) {
      list.remove(editor);
    }
    list.add(editor);
  }
  
  public void remove(QEditor editor) {
    if (list.contains(editor)) {
      list.remove(editor);
    }
  }
  
  public void clear() {
    list.clear();    
  }
  
  public boolean assignedEditors() {
    return list == null ? false : !list.isEmpty();
  }
  
  public boolean connected() {
    if (list == null || list.isEmpty()) {
      return false;
    }
    for (QEditor editor : list) {
      if (editor.getConnection() != null && editor.getConnectionStatus() == ConnectionStatus.CONNECTED) {
        return true;
      }
    }
    return false;
  }
  
  public List<QEditor> getEditors() {
    return list;
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

  @Override
  public String getTooltip() {
    return getUserObject() instanceof KdbService ?
        "<html><table cellspacing=\"0\" border=\"0\">" +
        (((KdbService)getUserObject()).getName() != null && ((KdbService)getUserObject()).getName().trim().length() > 0
            ? "<tr><td align=\"right\">Name:</td><td>" + ((KdbService)getUserObject()).getName() + "</td></tr>" : "") +
        "<tr><td align=\"right\">Host:</td><td>" +
        ((KdbService)getUserObject()).getHost() +
        "</td></tr>" +
        "<tr><td align=\"right\">Port:</td><td>" +
        ((KdbService)getUserObject()).getPort() +
        "</td></tr>" +
        "<tr><td align=\"right\">User name:</td><td>" +
        ((KdbService)getUserObject()).getUsername() +
        "</td></tr>" +
        (((KdbService)getUserObject()).getParams() != null && ((KdbService)getUserObject()).getParams().trim().length() > 0
            ? "<tr><td align=\"right\">Startup parameters:</td><td>" + ((KdbService)getUserObject()).getParams() +
                "</td></tr>" : "") + getStatus() + "</table></html>" : null;
  }

  public Icon getIcon() {
    if (list == null || list.isEmpty()) {
      return IconsItem.ICON_DB;
    }
    else {
      Icon icon = IconsItem.ICON_DB;
      for (QEditor editor : list) {
        if (editor.getConnectionStatus() == ConnectionStatus.LOST_CONNECTION) { 
          return IconsItem.ICON_DB_OUT;
        }
        else {
          if (editor.getConnectionStatus() == ConnectionStatus.CONNECTED) {
            icon = IconsItem.ICON_DB_IN;
          }
        }
      }
      return icon;
    }
  }
  
  private String getStatus() {
    if (list != null && !list.isEmpty()) {
      StringBuffer strBuf = new StringBuffer();
      for (QEditor item : list) {
        if (item.getConnection() != null && item.getConnectionStatus() == ConnectionStatus.CONNECTED) {
          strBuf.append(strBuf.length() == 0 ? 
              ("<tr><td align=\"right\">Editors:</td><td>" + item.getFile().getPath() + "</td></tr>") :
                ("<tr><td></td><td>" + item.getFile().getPath() + "</td></tr>"));
        }
      }
      return strBuf.toString();
    }
    return "";
  }
}
