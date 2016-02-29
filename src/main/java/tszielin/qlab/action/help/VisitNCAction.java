package tszielin.qlab.action.help;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.net.URI;

import javax.swing.JOptionPane;

import tszielin.qlab.util.action.ActionBase;
import tszielin.qlab.util.image.IconsItem;

public class VisitNCAction extends ActionBase {
  private static final long serialVersionUID = -808765125959848355L;

  public VisitNCAction() {
    super("Visit Nagler&Company", 'N', IconsItem.ICON_BLANK, null, "Visit Nagler&Company", "Visit Nagler&Company web page");
  }

  public void actionPerformed(ActionEvent e) {
    if (Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
      try {
        Desktop.getDesktop().browse(new URI("http://www.nagler-company.com/en/company.html"));
      }
      catch (Exception ex) {
        JOptionPane.showMessageDialog(null, "Error attempting to launch web browser:\n" +
            ex.getMessage(), "Web Browser error", JOptionPane.ERROR_MESSAGE);
      }
    }    
    else {
      JOptionPane.showMessageDialog(null, "Web browser cannot be launch (is not supported).", 
          "Web Browser not supported", JOptionPane.INFORMATION_MESSAGE);
    }
  }
}
