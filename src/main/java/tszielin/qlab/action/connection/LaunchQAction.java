package tszielin.qlab.action.connection;

import java.awt.event.ActionEvent;
import java.util.*;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import tszielin.qlab.config.AppConfig;
import tszielin.qlab.config.data.KdbService;
import tszielin.qlab.error.StudioException;
import tszielin.qlab.util.image.IconsItem;

public class LaunchQAction extends ServerAction {
  private static final long serialVersionUID = 249529282680149924L;

  public LaunchQAction(JTree tree) {
    super(tree, "Launch Q", 'q', IconsItem.ICON_BLANK, KeyStroke.getKeyStroke("control shift Q"), 
        "Launch q application", "Launch q application hosted on localhost");
  }

  @Override
  public void actionPerformed(ActionEvent event) {
    String qApp = null;
    String qHome = System.getenv("QHOME");    
    try {
      qApp = AppConfig.getConfig().getQApp();
      if (qHome == null || qHome.trim().isEmpty()) {
        qHome = AppConfig.getConfig().getQHome();
      }
    }
    catch(StudioException ex) {      
    }
    if (qApp == null) {
      return;
    }
    JTree tree = getSource() instanceof JTree ? (JTree)getSource() : null;

    if (tree == null) {
      return;
    }
    TreePath[] paths = tree.getSelectionPaths();
    TreePath path = paths != null && paths.length > 0 ? paths[0] : null;
    if (path != null) {
      DefaultMutableTreeNode point = 
        path.getPathComponent(path.getPathCount() - 1) instanceof DefaultMutableTreeNode
          ? (DefaultMutableTreeNode)path.getPathComponent(path.getPathCount() - 1) : null;
      KdbService connection = point != null ? 
          point.getUserObject() instanceof KdbService ? (KdbService)point.getUserObject() : null : null;
      
      List<String> commands = new ArrayList<String>(
          System.getProperty("os.name").toLowerCase().indexOf("windows") > -1 ?
              Arrays.<String>asList("cmd","/c","start",qApp) :
                System.getProperty("os.name").toLowerCase().indexOf("linux") > -1 ?
                    Arrays.<String>asList("xterm","-e",qApp) :
                      System.getProperty("os.name").toLowerCase().indexOf("mac os") > -1 ?
                          Arrays.<String>asList("/usr/X11/bin/xterm", "-e", qApp) : 
                            Arrays.<String>asList(qApp));
      
      if (connection.getParams() != null && connection.getParams().trim().length() > 0) {
        StringTokenizer tokenizer = new StringTokenizer(connection.getParams().trim(), " ,;");
        while (tokenizer.hasMoreTokens()) {
          if (System.getProperty("os.name").toLowerCase().indexOf("mac os") > -1) {
            String value = commands.get(commands.size() - 1);
            value += " " + tokenizer.nextToken();
            commands.set(commands.size() - 1, value);
          }
          else {
            commands.add(tokenizer.nextToken());
          }
        }
      }
      
      if (System.getProperty("os.name").toLowerCase().indexOf("mac os") > -1) {
        String value = commands.get(commands.size() - 1);
        value += " -p " + String.valueOf(connection.getPort());
        commands.set(commands.size() - 1, value);
      }
      else {
        commands.add("-p");
        commands.add(String.valueOf(connection.getPort()));
      }
            
//      String[] command = null;
//      if (connection != null) {
//        if (System.getProperty("os.name").toLowerCase().indexOf("windows") > -1) {
//          command = new String[] {"cmd /c start " + qApp + " " + params};
//        }
//        else {
//          if (System.getProperty("os.name").toLowerCase().indexOf("linux=") > -1) {
//            command = new String[] {"xterm -e " + qApp + " " + params};
//          }
//          else {
//            if (System.getProperty("os.name").toLowerCase().indexOf("linux=") > -1) {
//              command = new String[] {"usr/X11/bin/xterm", "-e",qApp + " " + params};
//            }
//            else {
//              command = new String[] {qApp + " " + params};
//            }
//          }
//        }
        try {
          ProcessBuilder pb = new ProcessBuilder(commands);
          pb.environment().put("QHOME", qHome);
          pb.start();
        }
        catch (Throwable cause) {
          JOptionPane.showMessageDialog(SwingUtilities.windowForComponent(tree), cause.getMessage(),
              "kdb+ Server", JOptionPane.ERROR_MESSAGE);
        }
      }
    } 
}
