package tszielin.qlab.util.config;

import java.io.File;
import java.net.URL;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;

import tszielin.qlab.util.config.strategy.ManualReloadStrategy;
import tszielin.qlab.util.error.ConfigException;

public abstract class XMLConfig implements Reloadable {
  protected XMLConfiguration configuration;
  
  protected XMLConfig(URL url) throws ConfigException {
    try {
      configuration = new XMLConfiguration(url);
      if (canReload()) {
        configuration.setReloadingStrategy(new ManualReloadStrategy());
      }
    }
    catch (ConfigurationException ex) {
      throw new ConfigException(ex);
    }
  }

  protected XMLConfig(String path, String fileName, boolean validate, boolean create, int refreshDelay) throws ConfigException {
    if (path == null) {
      throw new ConfigException("Path cannot be null.");
    }
    File file = null;
    if (!validate && create) {
      file = new File(path);
      if (!file.exists()) {
        file.mkdirs();
      }
    }
    file = new File(path, fileName != null ? fileName : getFileName());
    if (file.canRead()) {
      try {
        if (!validate) {
          configuration = new XMLConfiguration((new File(path != null ? path : 
            System.getProperty("com.nagler_company.config.path"), fileName != null ? 
                fileName : getFileName())).getPath());
        }
        else {
          configuration = new XMLConfiguration();
          configuration.setFile(file);
          configuration.setValidating(true);
          configuration.load();
        }
        if (canReload()) {
          if (isManual()) {
            configuration.setReloadingStrategy(new ManualReloadStrategy());
          }
          else {
            FileChangedReloadingStrategy strategy = new FileChangedReloadingStrategy();
            strategy.setRefreshDelay(refreshDelay);
            configuration.setReloadingStrategy(strategy);
          }
        }
      }
      catch (ConfigurationException ex) {
        throw new ConfigException(ex);
      }
    }
    else {
      if (create) {
        configuration = new XMLConfiguration();
        configuration.setRootElementName("studio");
        configuration.setFile(file);        
      }
      else {
        throw new ConfigException("Cannot read configuration file " + file.toString());
      }
    }
  }
  
  protected XMLConfig(String path, String fileName, boolean validate, boolean create) throws ConfigException {
    this(path, fileName, validate, create, 60000);
  }
  
  protected XMLConfig(String path, String fileName, boolean validate) throws ConfigException {
    this(path, fileName, validate, false);
  }
  
  protected XMLConfig(String path, boolean validate) throws ConfigException {
    this(path, null, validate);
  }
  
  protected XMLConfig(String path) throws ConfigException {
    this(path, false);
  }
  
  protected XMLConfig() throws ConfigException {
    this((String)null);
  }      
  
  abstract protected String getFileName();
  
  public void reload() {
    if (canReload() && configuration != null && 
        configuration.getReloadingStrategy() instanceof ManualReloadStrategy) {      
      ((ManualReloadStrategy)configuration.getReloadingStrategy()).reload();
    }
  }
  
  public File getFile() {
    return configuration.getFile();
  }
}
