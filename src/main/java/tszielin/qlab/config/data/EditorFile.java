package tszielin.qlab.config.data;

import java.io.File;

import org.apache.commons.io.FilenameUtils;

public class EditorFile {
  private File file;
  private KdbService connection;
  private long lastModified;
  private boolean saved;
  private boolean active;
  
  public EditorFile(File file, KdbService connection, boolean active) {    
    setFile(file);
    setConnection(connection);
    setActive(active);
  }

  public EditorFile(File file, KdbService connection) {    
    this(file, connection, false);
  }
  
  public EditorFile(File file) {    
    this(file, null);    
  }
  
  public void setFile(File file) {
    this.file = file;
    setLastModified();
  }
  
  public void setFile(String pathname) {
    if (pathname != null) {
      setFile(new File(pathname));
    }
  }
  
  public void setFile(String path, String fileName) {
    if (fileName != null) {
      setFile(new File(path, fileName));
    }
  }
  
  public File getFile() {
    return file;
  }
  
  public void setLastModified() {
    this.lastModified = this.file != null ? this.file.lastModified() : Long.MAX_VALUE;
    this.saved = true;
  }
  
  public long getLastModified() {
    return this.lastModified;
  }
  
  public void setUnsaved() {
    this.saved = false;
  }
  
  public boolean isSaved() {
    return this.saved;
  }
    
  public String getPath() {
    return file != null ? FilenameUtils.separatorsToUnix(file.getPath()) : null;
  }
  
  public String getName() {
    return file != null ? file.getName() : null;
  }
  
  public String getParent() {
    return file != null ? FilenameUtils.separatorsToUnix(file.getParent()) : null;
  }
  
  public void setConnection(KdbService connection) {
    this.connection = connection;
  }
  
  public KdbService getConnection() {
    return connection;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public boolean isActive() {
    return active;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.file == null) ? 0 : this.file.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (!(obj instanceof EditorFile))
      return false;
    EditorFile other = (EditorFile)obj;
    if (this.file == null) {
      if (other.file != null)
        return false;
    }
    else
      if (!this.file.equals(other.file))
        return false;
    return true;
  }
}