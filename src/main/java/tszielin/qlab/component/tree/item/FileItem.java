package tszielin.qlab.component.tree.item;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.apache.commons.io.FilenameUtils;

import tszielin.qlab.component.Iconable;
import tszielin.qlab.component.Tooltipable;
import tszielin.qlab.config.data.KdbService;
import tszielin.qlab.util.image.IconsItem;

public class FileItem extends File implements Tooltipable, Iconable {
  private static final long serialVersionUID = 982141883318299486L;
  private KdbService connection;
  
  public FileItem(String pathname, KdbService connection) {
    super(pathname);
    if (getName().endsWith(".q") || getName().endsWith(".k")) {
      this.connection = connection;
    }
  }
  
  public FileItem(String parent, String child) {
    super(parent, child);
  }
  
  public String getTooltip() {
    return "<html><table cellpadding=\"0\" cellspacing=\"0\" border=\"0\">" +
        "<tr><td align=\"right\">File path:</td><td>" + getPath() + "</td></tr>" +
        "<tr><td align=\"right\">Modified:</td><td>" +
          new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(
            getAbsoluteFile().lastModified())) + "</td></tr>" +
            (!exists() ? "<tr><td align=\"right\">Status: </td><td><font color=\"red\">Not exists</font></td></tr>" : 
              (!canWrite() ? "<tr><td align=\"right\">Status: </td><td>Read Only</td></tr>" : "")) +
         getConnectionInfo() +  "</table></html>";
  }

  public Icon getIcon() {
    String extension = FilenameUtils.getExtension(getName());
    return isDirectory() ? null :
      !exists() ? IconsItem.ICON_RED_DOT :
        extension.equalsIgnoreCase("q") || extension.equalsIgnoreCase("k") ? 
            new ImageIcon(IconsItem.IMAGE_Q_FILE) : 
              extension.equalsIgnoreCase("xml") ? new ImageIcon(IconsItem.IMAGE_XML_FILE) :
                extension.equalsIgnoreCase("xls") ? new ImageIcon(IconsItem.IMAGE_XLS_FILE) :
                  extension.equalsIgnoreCase("csv") ? new ImageIcon(IconsItem.IMAGE_CSV_FILE) :
                    extension.equalsIgnoreCase("txt") ? new ImageIcon(IconsItem.IMAGE_TXT_FILE) :
                      extension.equalsIgnoreCase("ini") || extension.equalsIgnoreCase("init") || 
                      extension.equalsIgnoreCase("properties") || extension.equalsIgnoreCase("conf") || 
                      extension.equalsIgnoreCase("config")? new ImageIcon(IconsItem.IMAGE_CONF_FILE) :
                        new ImageIcon(IconsItem.IMAGE_FILE);
  }
  
  public KdbService getConnection() {
    return connection;
  }
  
  protected String getConnectionInfo() {
    StringBuffer strBuf = new StringBuffer();
    if (connection != null) {
      strBuf.append("<tr><td align=\"right\">Default server:</td><td></td></tr>");
      strBuf.append("<tr><td align=\"right\">Host:</td><td>").append(connection.getHost()).append("</td></tr>");
      strBuf.append("<tr><td align=\"right\">Port:</td><td>").append(connection.getPort()).append("</td></tr>");
      if (connection.getUsername() != null && !connection.getUsername().trim().isEmpty()) {
        strBuf.append("<tr><td align=\"right\">User name:</td><td>").append(connection.getUsername()).append("</td></tr>");
      }
    }
    return strBuf.toString();
  }
}
