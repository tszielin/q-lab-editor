package tszielin.qlab.config;

import java.text.*;

import studio.ui.Studio;

public class AppInformation {
  private static AppInformation instance;
  
  private AppInformation() {
  }
  
  public static AppInformation getInformation() {
    if (instance == null) {
      instance = new AppInformation();
    }
    return instance;
  }
  
  public boolean isJarFile() {
    return Studio.class.getProtectionDomain().getCodeSource().getLocation() != null;
  }

  public String getTitle() {
    return "q-lab";
  }

  public long getBuildDate() {
    return 20130102L;
  }
  
  private int getBuildTime() {
    return 2019;
  }
  
  public boolean isNewVersion(long build) {
    return getBuildDate() * 10000 + getBuildTime() < build;  
  }
  
  public String buildString() {
    return "Build id: " + getBuildDate() + ":" + new DecimalFormat("0000").format(getBuildTime());
  }  
}