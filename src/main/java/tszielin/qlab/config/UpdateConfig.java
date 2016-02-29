package tszielin.qlab.config;

import java.net.*;

import tszielin.qlab.error.ArgumentException;
import tszielin.qlab.util.config.XMLConfig;
import tszielin.qlab.util.error.ConfigException;

public class UpdateConfig extends XMLConfig {
  public UpdateConfig(URL url) throws ConfigException, ArgumentException, MalformedURLException {
    super(url);
  }
  
  public boolean canReload() {
    return false;
  }

  public boolean isManual() {
    return false;
  }

  @Override
  protected String getFileName() {
    return super.getFile().getPath();
  }
  
  public String getBuild() {
    return configuration.getString("build");
  }
  
  public long getBuildId() {
    String build = getBuild();
    if (build == null) {
      return -1;
    }
    try {
      long date = Long.parseLong(build.substring(0, build.indexOf(":")));
      int time = Integer.parseInt(build.substring(build.indexOf(":") + 1));
      return date * 10000 + time;
    }
    catch(NumberFormatException ignored) {
    }
    return -1;
  }
  
  public String getFile(boolean jarFile) {
    return configuration.getString(jarFile ? "files.jar" : "files.zip");
  }
}
