/*
 * Studio for kdb+ by Charles Skelton is licensed under a Creative Commons
 * Attribution-Noncommercial-Share Alike 3.0 Germany License
 * http://creativecommons.org/licenses/by-nc-sa/3.0 except for the netbeans components which retain
 * their original copyright notice
 */

package studio.ui;

import javax.swing.JDialog;

import tszielin.qlab.dialog.AboutDialog;

public class AboutHandler {

  public AboutHandler() {
    super();
  }

  public void about() {
    JDialog dialog = new AboutDialog(null);
    dialog.setVisible(true);
    dialog.dispose();
    dialog = null;
  }
}
