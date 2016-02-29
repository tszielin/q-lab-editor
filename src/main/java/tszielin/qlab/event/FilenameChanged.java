package tszielin.qlab.event;

import tszielin.qlab.util.event.DataEvent;

public class FilenameChanged extends DataEvent<String> {
  private static final long serialVersionUID = -3435844269389252544L;

  public FilenameChanged(Object source, String filename) {
    super(source, filename);
  }

  public String getFilename() {
    return getData() != null && !getData().trim().isEmpty() ? getData() : null;
  }
}
