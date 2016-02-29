package tszielin.qlab.action.editor;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import tszielin.qlab.component.pane.ConsolesTabbedPane;
import tszielin.qlab.component.pane.EditorsTabbedPane;
import tszielin.qlab.config.data.EditorFile;
import tszielin.qlab.error.FileException;
import tszielin.qlab.util.image.IconsItem;

public class NewFileAction extends OpenFileAction {  
  private static final long serialVersionUID = 8784499752267740044L;

  public NewFileAction(EditorsTabbedPane tabEditors, ConsolesTabbedPane tabConsoles) {
    super(tabEditors, tabConsoles, "New", 'N', IconsItem.ICON_FILE_NEW, 
        KeyStroke.getKeyStroke("control N"), "New scipt", "Open new script in editor window.");
  }

  @Override
  public void actionPerformed(ActionEvent event) {
    try {
      initDocument((EditorFile)null);
    }
    catch (FileException ex) {
      JOptionPane.showMessageDialog(getComponent(), ex.getMessage(), "New file error", 
          JOptionPane.ERROR_MESSAGE);
    }
  }
}
