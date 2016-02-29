package tszielin.qlab.component.filechooser;

import java.io.File;

import javax.swing.filechooser.*;

public class QFileFilter extends FileFilter {

  public boolean accept(File pathname) {
    return pathname.isDirectory() || pathname.getName().endsWith(".q");
  }

  public String getDescription() {
    return "q script";
  }
}
