package tszielin.qlab.config.data;

import java.io.Serializable;

import tszielin.qlab.error.ArgumentException;

class Data implements Serializable {
  private static final long serialVersionUID = -8851041641255740212L;
  private String name;
  protected String host;
  protected int port;
  protected String username;
  
  protected Data() {    
  }

  public Data(String name, String host, int port, String username) throws ArgumentException {
    setName(name);
    setHost(host);
    setPort(port);
    setUsername(username);
  }
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name != null ? name.trim().isEmpty() ? null : name : null;
  }
  
  public String getHost() {
    return host;
  }

  public void setHost(String host) throws ArgumentException {
    this.host = host;
    if (this.host == null) {
      throw new ArgumentException("Host name or IP address cannot be null.");
    }
    if (this.host.trim().isEmpty()) {
      this.host = null;
      throw new ArgumentException("Host name or IP address cannot be empty.");
    }
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) throws ArgumentException {
    this.port = port;
    if (this.port <= 0 || this.port >= 65536) {
      this.port = -1;
      throw new ArgumentException("Port must be from range [1..65535]");
    }
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username == null || username.trim().length() == 0 ?
        System.getProperty("user.name") : username;
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 17;
    result = prime * result + (this.name == null ? 0 : this.name.hashCode());
    result = prime * result + (this.host == null ? 0 : this.host.hashCode());
    result = prime * result + this.port;
    result = prime * result + (this.username == null ? 0 : this.username.hashCode());
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
    KdbService other = (KdbService)obj;
    if (this.name == null) {
      if (other.getName() != null) {
        return false;
      }
    }
    else
      if (!this.name.equals(other.getName())) {
        return false;
      }
    if (this.host == null) {
      if (other.getHost() != null) {
        return false;
      }
    }
    else
      if (!this.host.equals(other.getHost())) {
        return false;
      }
    if (this.port != other.getPort()) {
      return false;
    }
    if (this.username == null) {
      if (other.getUsername() != null) {
        return false;
      }
    }
    else
      if (!this.username.equals(other.getUsername())) {
        return false;
      }
    return true;
  }
}
