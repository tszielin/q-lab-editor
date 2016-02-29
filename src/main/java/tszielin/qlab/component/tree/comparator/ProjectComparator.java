package tszielin.qlab.component.tree.comparator;

import java.util.Comparator;

import tszielin.qlab.component.tree.item.FileProject;

public class ProjectComparator implements Comparator<FileProject> {
  public int compare(FileProject fileProject1, FileProject fileProject2) {
    return fileProject1.getProject().getName().compareTo(fileProject2.getProject().getName());
  }
}
