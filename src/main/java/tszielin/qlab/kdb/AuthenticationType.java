package tszielin.qlab.kdb;

public enum AuthenticationType {
  USERNAME_AND_PASSWORD("Username and password");
  
  private final String type;
  
  AuthenticationType(String type) {
    this.type = type;
  }
  
  public String type() {
    return this.type;
  }
}
