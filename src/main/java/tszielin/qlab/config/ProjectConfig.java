package tszielin.qlab.config;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.OrFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;

import tszielin.qlab.config.data.Project;
import tszielin.qlab.error.ArgumentException;
import tszielin.qlab.util.config.XMLConfig;
import tszielin.qlab.util.error.ConfigException;

public class ProjectConfig extends XMLConfig {
  private static ProjectConfig instance;
  
  private final static String FILENAME = "projects.xml";

  protected ProjectConfig() throws ConfigException, ArgumentException {
    super(System.getProperty("user.home") + "/.q-lab", FILENAME, false, true, 60000);
  }
  
  public static ProjectConfig getConfig() throws ConfigException, ArgumentException {
    if (instance == null) {
      instance = new ProjectConfig();
    }
    return instance;
  }

  public boolean canReload() {
    return true;
  }

  public boolean isManual() {
    return false;
  }

  @Override
  protected String getFileName() {
    return FILENAME;
  }
  
  public List<Object> getProjectNames() {
    return configuration.getList("project[@name]");    
  }
  
  public List<Project> getProjects() {
    List<Project> list = null;
    List<HierarchicalConfiguration> fields = configuration.configurationsAt("project");
    if (fields != null && !fields.isEmpty()) {
      for (HierarchicalConfiguration field : fields) {
        if (list == null) {
          list = new ArrayList<>();
        }
        try {
          list.add(new Project(field));
        }
        catch (ArgumentException ignored) {
        }
      }
    }
    return list;
  }
  
  public Project getProject(String name) {
    try {
      return new Project(getData(name));
    }
    catch (ArgumentException ex) {
      return null;
    }
  }
  
  public boolean exists(String name) {
    return getData(name) != null;
  }

  private HierarchicalConfiguration getData(String name) {
    if (name == null || name.trim().isEmpty()) {
      return null;
    }
    List<HierarchicalConfiguration> fields = configuration.configurationsAt("project");
    if (fields != null && !fields.isEmpty()) {
      for (HierarchicalConfiguration field : fields) {
        if (name.equals(field.getString("[@name]"))) {
          return field;
        }
      }
    }
    return null;
  }
  
  public void remove(Project project) throws ConfigException {
    if (project != null && !project.getName().isEmpty()) {
      HierarchicalConfiguration field = getData(project.getName());
      if (field != null) {
        field.clear();
        try {
          configuration.save();
        }
        catch (ConfigurationException ex) {
          throw new ConfigException(ex);
        }
      }
    }
  }
    
  public void setProject(Project project) throws ConfigException {
    if (project != null) {      
      HierarchicalConfiguration field = getData(project.getName());
      if (field != null) {
        field.setProperty("[@name]", project.getName());
        field.setProperty("[@path]", FilenameUtils.separatorsToUnix(project.getPath().getPath()));
        if (project.getHost() != null && !project.getHost().isEmpty()) {
          field.setProperty("[@host]", project.getHost());
        }
        else {
          field.clearProperty("[@host]");
        }
        if (project.getPort() != -1) {
          field.setProperty("[@port]", project.getPort());
        }
        else {
          field.clearProperty("[@port]");
        }
        if (project.getUsername() != null && !project.getUsername().isEmpty()) {
          field.setProperty("[@username]", project.getUsername());
        }
        else {
          field.clearProperty("[@username]");
        }
        field.setProperty("[@closed]", project.isClosed());
      }
      else {
        configuration.setProperty("project(-1)[@name]", project.getName());
        configuration.setProperty("project[@path](-1)", FilenameUtils.separatorsToUnix(project.getPath().getPath()));
        if (project.getHost() != null && !project.getHost().isEmpty()) {
          configuration.setProperty("project[@host](-1)", project.getHost());
        }
        if (project.getPort() != -1) {
          configuration.setProperty("project[@port](-1)", project.getPort());
        }
        if (project.getUsername() == null || project.getUsername().isEmpty()) {
          configuration.setProperty("project[@username](-1)", project.getUsername());
        }
        configuration.setProperty("project[@closed](-1)", project.isClosed());
      }
      try {
        configuration.save();
      }
      catch (ConfigurationException ex) {
        throw new ConfigException(ex);
      }
    }
  }
  
  public File getPath() {
    String name = configuration.getString("global.path");
    File file = name != null ? new File(name) : null;
    return file != null ? file.exists() && file.isDirectory() && file.canRead() ? file : null : null;
  }
  
  public void setFilePath(File file) throws ConfigException {
    if (file != null && file.exists() && file.isDirectory()) {
      configuration.setProperty("global.path", FilenameUtils.separatorsToUnix(file.getPath()));
    }
    else {
      configuration.clearProperty("global.path");
    }
    try {
      configuration.save();
    }
    catch (ConfigurationException ex) {
      throw new ConfigException(ex);
    }
  }
  
  public File[] getFiles(File path) {
    if (path == null || !path.isDirectory()) {
      return null;
    }      
    return path.listFiles((FileFilter)getFilters());
  }
  
  public FileFilter getFilters() {
    List<String> list = getExtensions();
    if (list != null && !list.isEmpty()) {
      OrFileFilter orFileFilter = new OrFileFilter();
      orFileFilter.addFileFilter(new SuffixFileFilter(list));
      orFileFilter.addFileFilter(DirectoryFileFilter.INSTANCE);
      return orFileFilter;
    }
    return null;
  }
  
  public List<String> getExtensions() {
    List<String> list = new Vector<String>();
    List<Object> result = configuration.getList("global.files.ext");
    if (result != null) {
        for (Object obj : result) {
            if (obj instanceof String) {
                list.add((String)obj);
            }
        }
    }
    if (list.isEmpty()) {
      list.add("q");
      list.add("k");
    }
    Collections.sort(list);
    return list;
  }
  
  public void setExtensions(List<String> list) throws ConfigException {
    if (list == null) {
      list = new Vector<String>();
    }
    if (list.isEmpty()) {
      list.add("q");
      list.add("k");
    }
    configuration.setProperty("global.files.ext", list);
    try {
      configuration.save();
    }
    catch (ConfigurationException ex) {
      throw new ConfigException(ex);
    }
  }
}
