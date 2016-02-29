package tszielin.qlab.event;

import java.io.File;

import tszielin.qlab.config.data.KdbService;
import tszielin.qlab.util.event.DataEvent;

public class FileChoosed extends DataEvent<File> {
  private static final long serialVersionUID = 7204190464665584026L;
  private final KdbService connection;

  public FileChoosed(Object source, File file, KdbService connection) {
    super(source, file);
    this.connection = connection;
  }

  public String getFilename() {
    return getData() != null && getData().exists() ? getData().getPath() : null;
  }
  
  public KdbService getConnection() {
    return connection;
  }
}
