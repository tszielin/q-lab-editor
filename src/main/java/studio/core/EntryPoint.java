/*
 * Studio for kdb+ by Charles Skelton is licensed under a Creative Commons
 * Attribution-Noncommercial-Share Alike 3.0 Germany License
 * http://creativecommons.org/licenses/by-nc-sa/3.0 except for the netbeans components which retain
 * their original copyright notice
 */

package studio.core;

import java.util.Locale;
import java.util.TimeZone;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import studio.ui.ExceptionGroup;
import studio.ui.Studio;
import tszielin.qlab.config.AppConfig;
import tszielin.qlab.config.AppInformation;

public class EntryPoint {
  
  public static void main(final String[] args) {
    
    try {
      final AppConfig config = AppConfig.getConfig();
      
      Locale.setDefault(Locale.US);
      TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
      
      if (config.isUnixEOL()) {
        System.setProperty("line.separator", "\n");
      }

      if (System.getProperty("mrj.version") != null) {
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("apple.awt.brushMetalLook", "true");
        System.setProperty("apple.awt.showGrowBox", "true");
        System.setProperty("com.apple.mrj.application.apple.menu.about.name", AppInformation.getInformation().getTitle());
        System.setProperty("com.apple.mrj.application.live-resize", "true");
        System.setProperty("com.apple.macos.smallTabs", "true");
        System.setProperty("com.apple.mrj.application.growbox.intrudes", "false");
      }
      
      if (args != null && args.length > 0) {
        
      }

      try {
        UIManager.setLookAndFeel(config.getLookAndFeel());
      }
      catch (Exception ex) {
        JOptionPane.showMessageDialog(null, ex.getMessage(), null, JOptionPane.ERROR_MESSAGE);      
      }
  
      new Thread(new ExceptionGroup(), AppInformation.getInformation().getTitle()) {
        public void run() {
          new Studio(config, args);
        }
      }.start();
    }
    catch (Exception ex) {
      JOptionPane.showMessageDialog(null, ex.getMessage(), null, JOptionPane.ERROR_MESSAGE);
      return;
    }        
  }
}
