package tszielin.qlab.util.config.strategy;

import org.apache.commons.configuration.FileConfiguration;
import org.apache.commons.configuration.reloading.ReloadingStrategy;


public class ManualReloadStrategy implements ReloadingStrategy {
  /** Stores a reference to the associated configuration. */
  private FileConfiguration configuration;

  /** A flag whether a reload is required. */
  private boolean reloadingRequired;

  public ManualReloadStrategy() {
  }
  
   public void init() {
  }

  public void reloadingPerformed() {
    reloadingRequired = false;
  }

  public boolean reloadingRequired() {
    return reloadingRequired;
  }

  public void setConfiguration(FileConfiguration configuration) {
    this.configuration = configuration;
  }

  public void reload() {
    this.reloadingRequired = true;
    configuration.isEmpty();
  }
}
