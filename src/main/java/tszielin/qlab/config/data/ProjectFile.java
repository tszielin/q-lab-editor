package tszielin.qlab.config.data;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.Icon;

import tszielin.qlab.component.Iconable;
import tszielin.qlab.component.Tooltipable;
import tszielin.qlab.util.image.IconsItem;

public class ProjectFile extends File implements Tooltipable, Iconable {
  private static final long serialVersionUID = -3805076252378374136L;

  public ProjectFile(String pathname) {
    super(pathname);
  }
  
  public ProjectFile(File path) {
    super(path.getPath());
  }

  public ProjectFile(String parent, String child) {
    super(parent, child);
  }

  public ProjectFile(File parent, String child) {
    super(parent, child);
  }
  
  @Override
  public String toString() {
    return getName();
  }

  @Override
  public String getTooltip() {
    return 
      "<html><table cellpadding=\"0\" cellspacing=\"0\" border=\"0\">" + 
      "<tr><td align=\"right\">Filename: </td><td>" + getPath() + "</td></tr>" +
      (exists() ?
          (isFile() ? 
              "<tr><td align=\"right\">Modified:</td><td>" + 
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(lastModified())) + "</td></tr>" : "") :
          "<tr><td align=\"right\">Status: </td><td><font color=\"red\">not exists</font></td></tr>") +
       (!canWrite() ?  "<tr><td align=\"right\">Status: </td><td><font color=\"red\">Read-only</font></td></tr>" : "") +
      "</table></html>" ;
  }

  @Override
  public Icon getIcon() {
    return exists() ?
        (isDirectory() ? null :
          !canWrite() ? IconsItem.ICON_RED_DOC :
            getName().endsWith(".q") || getName().endsWith(".k") ?
                IconsItem.ICON_EDITOR : IconsItem.ICON_BLANK_DOC) : IconsItem.ICON_RED_DOT;
  }

}
