package tszielin.qlab.dialog;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

import studio.ui.EscapeDialog;
import tszielin.qlab.component.pane.EditorsTabbedPane;
import tszielin.qlab.renderer.WindowsListCellRender;

public class WindowsList extends EscapeDialog implements KeyListener {
  private static final long serialVersionUID = 4427561149800168677L;
  private JPanel contentPane = null;
  private JTable table;
  private EditorsTabbedPane tabEditors;

  public WindowsList(EditorsTabbedPane tabEditors) {
    super(tabEditors != null ? SwingUtilities.windowForComponent(tabEditors) : null, ModalityType.APPLICATION_MODAL);
    if (tabEditors != null && tabEditors.getTabCount() > 0) {
      this.tabEditors = tabEditors;
    }
    initialize();
  }

  /**
   * This method initializes this
   * 
   * @return void
   */
  private void initialize() {
    this.setSize(200, 300);
    this.setLocation(tabEditors != null ? 
        tabEditors.getEditor() != null ? 
            new Point(tabEditors.getEditor().getLocationOnScreen().x,
                tabEditors.getEditor().getLocationOnScreen().y) : 
                  new Point(tabEditors.getLocationOnScreen().x, 
                      tabEditors.getLocationOnScreen().y) : 
                        new Point(0, 0));
    this.setContentPane(getContent());
    this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    this.setResizable(false);
    this.setUndecorated(true);
  }

  private JPanel getContent() {
    if (contentPane == null) {
      contentPane = new JPanel(new BorderLayout());   
      if (tabEditors != null) {
        Object[][] data = new Object[this.tabEditors.getTabCount()][1];      
        for (int count = 0; count < this.tabEditors.getTabCount(); count++) {
          data[count][0] = new JLabel(this.tabEditors.getTitleAt(count), 
              this.tabEditors.getIconAt(count), SwingUtilities.RIGHT);
        }
        table = new JTable(data, new String[] {" Editors "});
        table.setDefaultRenderer(Object.class, new WindowsListCellRender());
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().setSelectionInterval(0, tabEditors.getSelectedIndex());
        table.addComponentListener(new ComponentListener() {          
          public void componentShown(ComponentEvent event) {
            setSize(200, event.getComponent().getHeight() + table.getRowHeight() + 8);
          }
          
          public void componentResized(ComponentEvent event) {
            setSize(200, event.getComponent().getHeight() + table.getRowHeight() + 8);
          }
          
          public void componentMoved(ComponentEvent event) {
          }
          
          public void componentHidden(ComponentEvent event) {
          }
        });             
        contentPane.add(new JScrollPane(table, 
            JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER), 
            BorderLayout.CENTER);
        table.addKeyListener(this);
        table.requestFocus();
      }
    }
    return contentPane;
  }

  @Override
  public void keyPressed(KeyEvent event) {
    if (event.getKeyCode() == KeyEvent.VK_F12) {
      table.getSelectionModel().setSelectionInterval(0, 
          table.getSelectedRow() + 1 < table.getRowCount() ? 
              table.getSelectedRow() + 1 : 0);
    }
  }

  @Override
  public void keyReleased(KeyEvent event) {
    if (!event.isControlDown()) {
      setVisible(false);
      if (table.getSelectedRow() > -1) {
        tabEditors.setSelectedIndex(table.getSelectedRow());
        tabEditors.getEditor().requestFocus();
      }
    }    
  }

  @Override
  public void keyTyped(KeyEvent event) {
  }
}
