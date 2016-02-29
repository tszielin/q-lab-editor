package tszielin.qlab.dialog;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import studio.ui.EscapeDialog;
import tszielin.qlab.component.pane.EditorsTabbedPane;

public class ShortcutsDialog extends EscapeDialog {
  private static final long serialVersionUID = -6348290895948368941L;
  private JPanel jContentPane = null;
  private JTable table = null;
  
  public ShortcutsDialog(EditorsTabbedPane tabEditors) {
    super(SwingUtilities.windowForComponent(tabEditors), "About..", ModalityType.APPLICATION_MODAL);
    setUndecorated(true);
    initialize();
  }

  /**
   * This method initializes this
   * 
   * @return void
   */
  private void initialize() {
    this.setSize(300, 200);
    this.setContentPane(getJContentPane());
  }

  /**
   * This method initializes jContentPane
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getJContentPane() {
    if (jContentPane == null) {
      jContentPane = new JPanel();
      jContentPane.setLayout(new BorderLayout());
      jContentPane.add(getTable(), BorderLayout.CENTER);
    }
    return jContentPane;
  }

  /**
   * This method initializes table	
   * 	
   * @return javax.swing.JTable	
   */
  private JScrollPane getTable() {
    if (table == null) {
      Object[][] data = {
          {KeyStroke.getKeyStroke("control shift W"), "Close all editors"},
          {KeyStroke.getKeyStroke("control W"), "Close editor"},
          {KeyStroke.getKeyStroke("control SLASH"), "Comment/Uncomment"},
          {KeyStroke.getKeyStroke("control N"), "Open new script in editor window"},
          {KeyStroke.getKeyStroke("control O"),"Open file and show it in new editor window"},
          {KeyStroke.getKeyStroke("ctrl S"), "Save changes in editor"}, 
          {KeyStroke.getKeyStroke("control shift S"), "Save changes in all modified editors"},  
          {KeyStroke.getKeyStroke("control E"), "Execute query/queries"},  
          {KeyStroke.getKeyStroke("control ENTER"), "Execute the current line as a query"} ,
          {KeyStroke.getKeyStroke("control F12"), "List of windows (editors)"},
          {KeyStroke.getKeyStroke("control shift PAGE_UP"), "Focus next editor"},
          {KeyStroke.getKeyStroke("control shift PAGE_DOWN"), "Focus previous editor"},
          {KeyStroke.getKeyStroke("control 1"), "Focus first editor"},
          {KeyStroke.getKeyStroke("control 9"), "Focus last editor"},
          {KeyStroke.getKeyStroke("control shift M"), "Maximize/Normal window/editor size"},
          {KeyStroke.getKeyStroke("shift F10"), "Popup menu"}
      };
      String[] columns = {"Keystroke","Operation"};
      try {        
        table = new JTable(data, columns);
        table.setAutoCreateRowSorter(true);
      }
      catch (java.lang.Throwable e) {
      }
    }
    return new JScrollPane(table);
  }
}
