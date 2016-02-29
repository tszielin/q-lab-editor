package tszielin.qlab.component.tree.comparator;

import java.io.File;
import java.util.Comparator;

public class FileComparator implements Comparator<File> {
  @Override
  public int compare(File file1, File file2) {
    return (file1.isDirectory() && file2.isDirectory()) || (file1.isFile() && file2.isFile()) ?
        file1.getName().compareTo(file1.getName()) : file1.isDirectory() ? -1 : 1;       
  }
}
