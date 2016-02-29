/*
 * Studio for kdb+ by Charles Skelton is licensed under a Creative Commons
 * Attribution-Noncommercial-Share Alike 3.0 Germany License
 * http://creativecommons.org/licenses/by-nc-sa/3.0 except for the netbeans components which retain
 * their original copyright notice
 */

package studio.kdb;

import java.awt.Component;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.MouseInputAdapter;

import tszielin.qlab.config.AppConfig;
import tszielin.qlab.error.StudioException;

public class TableRowHeader extends JList {
  private static final long serialVersionUID = 8221494805835870055L;
  private JTable table;

  public void recalcWidth() {
    Insets i = new RowHeaderRenderer().getInsets();
    int w = i.left + i.right;
    int width = SwingUtilities.computeStringWidth(table.getFontMetrics(getFont()),
        (table.getRowCount() < 99999 ? "99999" : "" + (table.getRowCount() - 1)));
    // used to be rowcount - 1 as 0 based index
    setFixedCellWidth(w + width);
  }

  public TableRowHeader(final JTable table) {
    super();
    this.table = table;
    table.addPropertyChangeListener(new PropertyChangeListener() {
      public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        if ("zoom".equals(propertyChangeEvent.getPropertyName())) {
          setFont(table.getFont());
          setFixedCellHeight(table.getRowHeight());
          recalcWidth();
          setCellRenderer(new RowHeaderRenderer());
        }
      }
    });
    setAutoscrolls(false);
    setCellRenderer(new RowHeaderRenderer());
    setFixedCellHeight(table.getRowHeight());
    setFont(table.getFont());
    recalcWidth();

    // setPreferredSize(new Dimension(w+width,table.getRowHeight()));
    // setPreferredSize(new Dimension(width, 0));
    setFocusable(false);
    setModel(new TableListModel());
    setOpaque(false);
    setSelectionModel(table.getSelectionModel());
    if (table.getRowCount() > 0) {
      MouseInputAdapter mia = new MouseInputAdapter() {
        int startIndex = 0;

        public void mousePressed(MouseEvent e) {
          int index = locationToIndex(e.getPoint());
          startIndex = index;
          table.setColumnSelectionInterval(0, table.getColumnCount() - 1);
          table.setRowSelectionInterval(index, index);
          table.requestFocus();
        }

        public void mouseReleased(MouseEvent e) {
          int index = locationToIndex(e.getPoint());
          table.setColumnSelectionInterval(0, table.getColumnCount() - 1);
          table.setRowSelectionInterval(startIndex, index);
          table.requestFocus();
        }

        public void mouseDragged(MouseEvent e) {
          int index = locationToIndex(e.getPoint());
          table.setColumnSelectionInterval(0, table.getColumnCount() - 1);
          table.setRowSelectionInterval(startIndex, index);
          table.requestFocus();
        }
      };
      addMouseListener(mia);
      addMouseMotionListener(mia);
    }
  }

  /*
   * public void updateUI() { super.updateUI(); setCellRenderer(new RowHeaderRenderer()); //
   * setHeight(getFontMetrics(UIManager.getFont("TableHeader.font")).getHeight()); if(table != null)
   * setFixedCellHeight( table.getRowHeight()); }
   */
  class TableListModel extends AbstractListModel {
    private static final long serialVersionUID = 7586429512756758791L;
    private AppConfig config;

    TableListModel() {
      try {
        config = AppConfig.getConfig();
      }
      catch(StudioException ignored) {        
      }
    }
    public int getSize() {
      return table.getRowCount();
    }

    public Object getElementAt(int index) {
      return String.valueOf(config != null && config.isZeroNumeration() ? index : index + 1);
    }
  }

  class RowHeaderRenderer extends JLabel implements ListCellRenderer {
    private static final long serialVersionUID = 3195790093221775612L;

    RowHeaderRenderer() {
      super();
      setHorizontalAlignment(RIGHT);
      setVerticalAlignment(CENTER);
      setOpaque(true);
      setBorder(UIManager.getBorder("TableHeader.cellBorder"));
      setFont(table.getFont());
      setBackground(UIManager.getColor("TableHeader.background"));
      setForeground(UIManager.getColor("TableHeader.foreground"));
    }

    public Component getListCellRendererComponent(JList list, Object value, int index,
        boolean isSelected, boolean cellHasFocus) {
      setText((value == null) ? "" : value.toString());
      return this;
    }
  }
}