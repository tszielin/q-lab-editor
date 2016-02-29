package tszielin.qlab.config.data;

import java.awt.Color;
import java.io.Serializable;

import org.apache.commons.configuration.HierarchicalConfiguration;

import tszielin.qlab.config.AppConfig;
import tszielin.qlab.error.ArgumentException;
import tszielin.qlab.kdb.AuthenticationType;
import tszielin.qlab.util.crypt.AES;
import tszielin.qlab.util.error.AESException;

public class KdbService extends Data implements Cloneable, Serializable {
  private static final long serialVersionUID = -3318896078454790519L;
  private String password;
  private AuthenticationType type;
  private Color titleColor;
  private ConnectionStatus status;
  private String params;

  public KdbService(HierarchicalConfiguration hc) throws ArgumentException {
    super(hc.getString("[@name]"), hc.getString("[@host]"), hc.getInt("[@port]", -1), hc.getString("[@username]"));
    this.password = hc.getString("[@password]");
    setType(hc.getString("[@type]"));
    setTitleColor(hc.getInt("[@title]", Color.BLACK.getRGB()));
    setParams(hc.getString("[@params]"));
    setStatus(ConnectionStatus.NOT_CONNECTED);
  }

  public KdbService(String name, String host, int port, String username, String password, String type,
      int titleColor) throws ArgumentException {
    super(name, host, port, username);
    setPassword(password);
    setType(type);
    setTitleColor(titleColor);
    setStatus(ConnectionStatus.NOT_CONNECTED);
  } 
  
  public KdbService(String name, String host, int port, String username, String password, String type,
      Color titleColor) throws ArgumentException {
    this(name, host, port, username, password, type, titleColor.getRGB());
  }
  
  public KdbService(String name, String host, int port, String username, String password, AuthenticationType type,
      int titleColor) throws ArgumentException {
    this(name, host, port, username, password, type.type(), titleColor);
  }
  
  public KdbService(String name, String host, int port, String username, String password, AuthenticationType type,
      Color titleColor) throws ArgumentException {
    this(name, host, port, username, password, type.type(), titleColor);
  }
  
  public KdbService(String name, String host, int port, String username, String password, AuthenticationType type) throws ArgumentException {
    this(name, host, port, username, password, type.type(), Color.black);
  }
  
  public KdbService(String name, String host, int port, String username, String password, String type) throws ArgumentException {
    this(name, host, port, username, password, type, Color.black);
  }
  
  public KdbService(String name, String host, int port, String username, String password) throws ArgumentException {
    this(name, host, port, username, password, AuthenticationType.USERNAME_AND_PASSWORD);
  }
  
  public KdbService(String name, String host, int port, String username) throws ArgumentException {
    this(name, host, port, username, null);
  }
  
  public KdbService(String name, String host, int port) throws ArgumentException {
    this(name, host, port, null);
  }
  
  public KdbService(String host, int port) throws ArgumentException {
    this(null, host, port);
  }  
  
  public String getPassword() {
    return this.password;
  }
  
  public String getCredentials() {
    String pwd = null; 
    try {
      pwd = password != null && password.trim().length() > 0 ?
          (new AES(getUsername())).decrypt(this.password) : null;
    }
    catch (AESException ignored) {
      pwd = null;
    }
    return getUsername() + (pwd == null ? "" : ":" + pwd);
  }

  public void setPassword(String password) {
    try {
      this.password = password != null && password.trim().length() > 0 ?
          (new AES(getUsername())).encrypt(password) : null;
    }
    catch (AESException ex) {
      this.password = null;
    }
  }

  public AuthenticationType getType() {
    return type;
  }
  
  public String getAuthenticationType() {
    return type.type();
  }

  public void setType(AuthenticationType type) {
    this.type = type;
  }
  
  public void setType(String type) {
    this.type = AuthenticationType.USERNAME_AND_PASSWORD;
    if (type != null && type.trim().length() > 0) {
      for (AuthenticationType authType : AuthenticationType.values()) {
        if (type.equals(authType.type())) {
          this.type = authType;
          break;
        }
      }    
    }
  }

  public Color getTitleColor() {
    return titleColor;
  }

  public void setTitleColor(int titleColor) {
    this.titleColor = new Color(titleColor);
  }
  
  public void setTitleColor(Color titleColor) {
    this.titleColor = titleColor;
  }
//  
//  public static final Comparator<Connection> ORDER = new ConnectionComparator();
//
//  private static class ConnectionComparator implements Comparator<Connection> {
//    public int compare(Connection c1, Connection c2) {
//      if (c1 == null && c2 == null) {
//        return 0;
//      }
//      if (c1 == null && c2 != null) {
//        return 1;
//      }
//      if (c1 != null && c2 == null) {
//        return -1;
//      }
//      int name = c1.getName() == null ? "".compareTo(c2.getName() == null ? "" : c2.getName()) :
//        c1.getName().compareTo(c2.getName() == null ? "" : c2.getName());
//      int host = c1.getHost().compareTo(c2.getHost());
//      int user = c1.getUsername().compareTo(c2.getUsername());
//      Sort sort = null;
//      try {
//        sort = StudioConfig.getConfig().getSortType();
//      }
//      catch (Exception ex) {
//        sort = Sort.PORT;
//      }
//      switch(sort) {
//        case PORT:
//          return host == 0 ? c1.getPort() - c2.getPort() : host;
//        case NAME:
//          return host == 0 ? name : host;
//        case USER:
//          return host == 0 ? user : host;
//        default:
//          return host == 0 ? user == 0 ? c1.getPort() - c2.getPort() : user : host;
//      }
//    }
//  }

  @Override
  public Object clone() {
    try {
      return super.clone();
    }
    catch (CloneNotSupportedException e) {
      throw new Error("This should not occur since we implement Cloneable");
    }
  }
  
  @Override
  public String toString() {
    Sort sort = null;
    try {
      sort = AppConfig.getConfig().getSortType();
    }
    catch (Exception ex) {
      sort = Sort.PORT;
    }
    String name = getName();
    String port = String.valueOf(getPort());
    String user = getUsername();
    switch (sort) {
      case NAME:
        return (name == null ? "" : name + " ") + "(" + port + "," + user + ")";
      case USER:
        return user + " (" + port + (name != null ? ", " + name  : "") + ")";
      default:
        return port + " (" + (name != null ? name + ", " : "") + user + ")";
    }
  }
    
  public String getServiceInfo() {
    return getServiceInfo(true);
  }
  
  public String getServiceInfo(boolean userName) {
    return (userName ? getUsername() + "@" : "" ) + getHost() + ":" + String.valueOf(getPort()) + 
      (getName() != null ? " (" + getName() + ")" : "");
  }
  
  public String getInfo() {
    return getInfo(true, true);
  }
  
  public String getInfo(boolean userName, boolean brackets) {
    return (brackets ? "(" : "") +  (userName ? getUsername() + "@" : "") + 
      getHost() + ":" + String.valueOf(getPort()) + (brackets ? ")" : "");
  }

  public ConnectionStatus getStatus() {
    return this.status;
  }
  
  public void setStatus(ConnectionStatus status) {
    this.status = status;
  }
  
  public String getParams() {
    return this.params;
  }
  
  public void setParams(String params) {
    this.params = params != null && params.trim().length() > 0 ? params : null;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((this.params == null) ? 0 : this.params.hashCode());
    result = prime * result + ((this.password == null) ? 0 : this.password.hashCode());
    result = prime * result + ((this.status == null) ? 0 : this.status.hashCode());
    result = prime * result + ((this.titleColor == null) ? 0 : this.titleColor.hashCode());
    result = prime * result + ((this.type == null) ? 0 : this.type.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj))
      return false;
    if (!(obj instanceof KdbService))
      return false;
    KdbService other = (KdbService)obj;
    if (this.params == null) {
      if (other.params != null)
        return false;
    }
    else
      if (!this.params.equals(other.params))
        return false;
    if (this.password == null) {
      if (other.password != null)
        return false;
    }
    else
      if (!this.password.equals(other.password))
        return false;
    return true;
  }
}
