package tszielin.qlab.action.help;

import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.html.HTMLEditorKit;

import tszielin.qlab.config.AppConfig;
import tszielin.qlab.config.AppInformation;
import tszielin.qlab.config.UpdateConfig;
import tszielin.qlab.util.action.ActionBase;
import tszielin.qlab.util.image.IconsItem;

public class CheckUpdate extends ActionBase {
  private static final long serialVersionUID = 2110464936394569810L;
  private final Window window;
  
  public CheckUpdate(Window window) {
    super("Software update...", 'U', IconsItem.ICON_BLANK, null, "Check software update", "Lookup software update");
    this.window = window;
  }

  @Override
  public void actionPerformed(ActionEvent event) {
    if (JOptionPane.showConfirmDialog(window, "Lookup software update?", "Update check",
        JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
      try {
        URL url = AppConfig.getConfig().getUpdateURL();
        UpdateConfig updateConfig = new UpdateConfig(url);
        if (AppInformation.getInformation().isNewVersion(updateConfig.getBuildId())) {
          JEditorPane editor = new JEditorPane();
          editor.setEditorKit(new HTMLEditorKit());
          editor.setEditable(false);
          editor.setBackground(UIManager.getColor("JPane.background"));
          editor.setFont(new Font("Dialog", Font.PLAIN, 10));
          String location = updateConfig.getFile(AppInformation.getInformation().isJarFile());
          editor.setText("<html><body><font color=\"#0000ff\">"
              + AppInformation.getInformation().getTitle() + " Build id:" + updateConfig.getBuild()
              + "</font><br>is ready for download from location<br><font color=\"red\">" + location
              + "</font><br>Do you want to download?</body></html>");
          if (JOptionPane.showOptionDialog(window, editor, "Update available",
              JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, new Object[]{
                  UIManager.get("OptionPane.yesButtonText"),
                  UIManager.get("OptionPane.noButtonText")}, UIManager
                  .get("OptionPane.yesButtonText")) == JOptionPane.YES_OPTION) {
            JFileChooser chooser = new JFileChooser(
                System.getProperty("java.io.tmpdir") != null ? new File(System
                    .getProperty("java.io.tmpdir")) : null);
            URL updateURL = new URL(location);
            chooser.setSelectedFile(new File(updateURL.getFile()));
            if (chooser.getSelectedFile().getName().endsWith(".jar")) {
              chooser.setFileFilter(new FileFilter() {
                public boolean accept(File file) {
                  return file.isDirectory() || file.getName().endsWith(".jar");
                }

                public String getDescription() {
                  return "*.jar";
                }
              });
            }
            else {
              chooser.setFileFilter(new FileFilter() {
                public boolean accept(File file) {
                  return file.isDirectory() || file.getName().endsWith(".zip");
                }

                public String getDescription() {
                  return "*.zip";
                }
              });
            }
            chooser.setDialogType(JFileChooser.SAVE_DIALOG);
            chooser.setDialogTitle("Save update '" + chooser.getSelectedFile() + "' in");
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            if (chooser.showSaveDialog(window) == JFileChooser.APPROVE_OPTION) {
              File destination = chooser.getSelectedFile();
              boolean overwrite = true;
              if (destination.exists()) {
                overwrite = JOptionPane.showConfirmDialog(window,
                    "Do you want to overwrite the file?", "Overwite file?",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_NO_OPTION;
              }
              if (overwrite) {
                BufferedInputStream input = null;
                BufferedOutputStream output = null;
                try {
                  output = new BufferedOutputStream(new FileOutputStream(destination), 1024);
                  input = new BufferedInputStream(updateURL.openStream());
                  byte[] data = new byte[1024];
                  int count = 0;
                  while ((count = input.read(data, 0, 1024)) >= 0) {
                    output.write(data, 0, count);
                  }
                  output.flush();
                }
                catch (IOException ex) {
                  JOptionPane.showMessageDialog(window, ex.getMessage(), "Download",
                      JOptionPane.ERROR_MESSAGE);
                }
                finally {
                  if (output != null) {
                    output.close();
                  }
                  if (input != null) {
                    input.close();
                  }
                }
              }
            }
          }
        }
      }
      catch (Exception ex) {
        JOptionPane.showMessageDialog(window, ex.getMessage(), "Update checker",
            JOptionPane.ERROR_MESSAGE);
      }
    }
  }
}