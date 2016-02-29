package tszielin.qlab.event;

import java.util.List;

import tszielin.qlab.config.data.EditorFile;
import tszielin.qlab.util.event.DataEvent;

public class FileListChanged extends DataEvent<List<EditorFile>> {
  private static final long serialVersionUID = -8587762481284193673L;

  public FileListChanged(Object source, List<EditorFile> filenames) {
    super(source, filenames);
  }  
}
