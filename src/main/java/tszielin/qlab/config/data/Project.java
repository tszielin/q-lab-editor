package tszielin.qlab.config.data;

import java.io.File;
import java.io.Serializable;
import java.util.List;

import javax.swing.Icon;

import org.apache.commons.configuration.HierarchicalConfiguration;

import tszielin.qlab.component.Iconable;
import tszielin.qlab.component.Tooltipable;
import tszielin.qlab.config.AppConfig;
import tszielin.qlab.error.ArgumentException;
import tszielin.qlab.error.StudioException;
import tszielin.qlab.util.image.IconsItem;

public class Project extends Data implements Cloneable, Serializable, Tooltipable, Iconable {
  private static final long serialVersionUID = 1039160381051481212L;
  private boolean closed;
  private File path;

  public Project(HierarchicalConfiguration hc) throws ArgumentException {
    super(hc == null ? null : hc.getString("[@name]"),        
        hc == null ? null : hc.getString("[@host]"), 
        hc == null ? -1 : hc.getInt("[@port]", -1), 
        hc == null ? null : hc.getString("[@username]"));
    setPath(hc == null ? null : hc.getString("[@path]"));
    setClosed(hc == null ? false : hc.getBoolean("[@closed]", false));
    
  }

  public Project(String name, String path, String host, int port, String username, boolean closed, List<String> files, List<String> references) throws ArgumentException {
    super(name, host, port, username);
    setPath(path);
    setClosed(closed);
  }
  
  public Project(String name, String path, String host, int port, String username, boolean closed) throws ArgumentException {
    this(name, path, host, port, username, closed, null, null);
  }
  
  public Project(String name, String path, String host, int port, String username) throws ArgumentException {
    this(name, path, host, port, username, false);
  }
  
  public Project(String name, String path) throws ArgumentException {
    this(name, path, null, -1, null);
  }
  
  public boolean isClosed() {
    return this.closed;
  }
  
  public void setClosed(boolean closed) {
    this.closed = closed;  
  }
  
  public File getPath() {
    return path;
  }
  
  public void setPath(String path) {
    this.path = null;
    if (path != null && !path.trim().isEmpty()) {
      this.path = new File(path);      
      if (!this.path.isDirectory() || !this.path.canRead()) {
        this.path = null;
      }
    }
  }
  
  public void setPath(File path) {
    this.path = path;
    if (this.path != null && !this.path.isDirectory() || !this.path.canRead()) {
      this.path = null;
    }
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 17;
    result = prime * result + ((this.getName() == null) ? 0 : this.getName().hashCode());
    result = prime * result + ((this.getPath() == null) ? 0 : this.getPath().hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    Project other = (Project)obj;
    if (this.getName() == null) {
      if (other.getName() != null) {
        return false;
      }
    }
    else
      if (!this.getName().equals(other.getName())) {
        return false;
      }
    if (this.getPath() == null) {
      if (other.getPath() != null) {
        return false;
      }
    }
    else
      if (!this.getPath().equals(other.getPath())) {
        return false;
      }
    return true;
  }
  
  @Override
  public void setHost(String host) throws ArgumentException {
    this.host = host;
  }

  @Override
  public void setPort(int port) throws ArgumentException {
    this.port = port;
  }

  @Override
  public void setUsername(String username) {
    this.username = username;
  }
  
  @Override
  public String toString() {
    return getName();
  }

  public String getTooltip() {
    return "<html><table cellpadding=\"0\" cellspacing=\"0\" border=\"0\">" + 
      "<tr><td align=\"left\">Project:</td><td></td></tr>" +
      "<tr><td align=\"right\">Name:</td><td>" + getName() + "</td></tr>" +
      "<tr><td align=\"right\">Path:</td><td>" + getPath() + "</td></tr>" + 
      getConnectionInfo() + "</table></html>";
  }

  public Icon getIcon() {
    return isClosed() ? IconsItem.ICON_FOLDER_DISABLE : IconsItem.ICON_DB_EDIT;
  }
  
  public KdbService getConnection() {
    try {
      return AppConfig.getConfig().getKdbService(host, port, username);
    }
    catch (StudioException ignored) {
      return null;
    }
  }
  
  public void setConnection(KdbService connection) throws ArgumentException {
    if (connection != null) {
      setHost(connection.getHost());
      setPort(connection.getPort());
      setUsername(connection.getUsername());
    }
  }
  
  protected String getConnectionInfo() {
    StringBuffer strBuf = new StringBuffer();
    if (getHost() != null && !getHost().trim().isEmpty()) {
      strBuf.append("<tr><td align=\"right\">Default server:</td><td></td></tr>");
      strBuf.append("<tr><td align=\"right\">Host:</td><td>").append(getHost()).append("</td></tr>");
      strBuf.append("<tr><td align=\"right\">Port:</td><td>").append(getPort()).append("</td></tr>");
      if (getUsername() != null && !getUsername().trim().isEmpty()) {
        strBuf.append("<tr><td align=\"right\">User name:</td><td>").append(getUsername()).append("</td></tr>");
      }
    }
    return strBuf.toString();
  }
}
