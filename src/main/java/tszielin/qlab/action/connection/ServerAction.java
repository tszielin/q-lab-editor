package tszielin.qlab.action.connection;

import java.awt.Window;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import tszielin.qlab.config.AppConfig;
import tszielin.qlab.error.StudioException;
import tszielin.qlab.util.action.popup.ActionPopup;
import tszielin.qlab.util.event.DataEvent;
import tszielin.qlab.util.event.FireData;
import tszielin.qlab.util.listener.DataListener;

abstract public class ServerAction extends ActionPopup {
  private static final long serialVersionUID = 8946083305428413994L;
  private AppConfig config;
  private FireData fireData;
  
  public ServerAction(JTree tree, String caption, char mnemonic, Icon icon, KeyStroke key,
      String toolTip, String hint) {
    super(tree, caption, mnemonic, icon, key, toolTip, hint);
    try {
      this.config = AppConfig.getConfig();
    }
    catch(StudioException ignored) {      
    }
    this.fireData = new FireData();
  }
  
  protected Window getWindow() {
    return getSource() instanceof JTree ? SwingUtilities.windowForComponent((JTree)getSource()) : null;
  }
  
  protected AppConfig getConfig() {
    return config;
  }
  
  public void addFireDataListener(DataListener listener) {
    fireData.addDataListener(listener);
  }
  
  public void removeFireDataListener(DataListener listener) {
    fireData.removeDataListener(listener);
  }
  
  protected void onData(DataEvent<?> event) {
    fireData.onData(event);
  }
}
