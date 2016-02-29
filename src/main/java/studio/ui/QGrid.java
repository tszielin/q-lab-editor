/*
 * Studio for kdb+ by Charles Skelton is licensed under a Creative Commons
 * Attribution-Noncommercial-Share Alike 3.0 Germany License
 * http://creativecommons.org/licenses/by-nc-sa/3.0 except for the netbeans components which retain
 * their original copyright notice
 */

package studio.ui;

import java.awt.*;
import java.awt.Dialog.ModalityType;
import java.awt.event.*;
import java.awt.image.BufferedImage;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.*;

import studio.kdb.*;
import tszielin.qlab.component.console.GridConsole;
import tszielin.qlab.component.popup.GridPopup;
import tszielin.qlab.util.action.ActionBase;
import tszielin.qlab.util.image.IconsItem;
import tszielin.qlab.util.listener.PopupListener;

import com.kx.kdb.K;
import com.kx.kdb.K.KType;

public class QGrid extends JPanel {
  private static final long serialVersionUID = 11277315817745380L;

//  private float factor = 1;
  private boolean scrollToEnd = false;
  private JTable table;
  private final WidthAdjuster widthAdjuster;

  Color col = new Color(0xff, 0xff, 0xcc);
  Color bgSelCache = UIManager.getColor("Table.selectionBackground");
  Color fgSelCache = UIManager.getColor("Table.selectionForeground");
  Color bgCache = UIManager.getColor("Table.background");

  class GridTable extends JTable {
    private static final long serialVersionUID = -9210284669723096055L;

    public GridTable(TableModel model) {
      super(model);
      addMouseListener(new MouseAdapter() {
        public void mouseClicked(MouseEvent e) {
          if (e.getClickCount() == 2) {
            int row = table.rowAtPoint(e.getPoint());
            int column = table.columnAtPoint(e.getPoint()); // This is the view column!
            column = table.convertColumnIndexToModel(column);
            EscapeDialog dialog = null;
            if (table.getValueAt(row, column) instanceof K.KTable ||
                table.getValueAt(row, column) instanceof K.KBaseArray ||
                table.getValueAt(row, column) instanceof K.Function ||
                table.getValueAt(row, column) instanceof K.KDictionary) {
//              if (table.getModel() instanceof FlipTableModel && 
//                  (table.getValueAt(row, column) instanceof K.Function ||
//                  table.getValueAt(row, column) instanceof K.KCharacterVector)) {
              if (table.getValueAt(row, column) instanceof K.Function ||
                  table.getValueAt(row, column) instanceof K.KCharacterArray) {
                dialog = new EscapeDialog(new JFrame(), 
                    "Column: " + table.getColumnName(column) + "   Row: " + row,
                    ModalityType.APPLICATION_MODAL);
                dialog.setAlwaysOnTop(true);
                Object value = table.getValueAt(row, column) instanceof K.Function ? 
                    ((KTableTableModel)table.getModel()).getValue(row, column) :
                    ((K.KCharacterArray)table.getValueAt(row, column));
                
                String str = ((K.KType<?>)value).toString(true);                
                JLabel type = null;
                if (table.getValueAt(row, column) instanceof K.KBase) {
                  type = new JLabel("   Type: " + ((K.KType<?>)table.getValueAt(row, column)).getType() + "h");
                  type.setFont(new Font("Dialog", Font.PLAIN, 10));
                  type.setBackground(new java.awt.Color(102, 102, 145));
                  type.setForeground(Color.red);
                }
                JEditorPane txtPane = new JEditorPane("text/plain", str);
                if (type != null) {
                  JPanel panel = new JPanel(new BorderLayout(10, 0));
                  panel.add(new JScrollPane(txtPane), BorderLayout.CENTER);
                  panel.add(type, BorderLayout.SOUTH);
                  dialog.add(panel);
                }
                else {
                  dialog.add(new JScrollPane(txtPane));
                }
                txtPane.setEditable(false);
                txtPane.setCaretPosition(0);
                dialog.setSize(300, 300);
                dialog.validate();
              }
              else {
                GridConsole console = new GridConsole(new QGrid(
                    table.getValueAt(row, column) instanceof K.KTable ? (K.KTable)table.getValueAt(row, column) :
                      table.getValueAt(row, column) instanceof K.KBaseArray ? 
                          (K.KBaseArray)table.getValueAt(row, column) :
                            (K.KDictionary)table.getValueAt(row, column)), null, null, 
                        table.getValueAt(row, column) instanceof K.KType<?>?
                            ((K.KType<?>)table.getValueAt(row, column)).getType() : 0, -1);                
                dialog = new EscapeDialog(new JFrame(), 
                    "Column: " + table.getColumnName(column) + "   Row: " + row,
                    ModalityType.APPLICATION_MODAL);
                dialog.setIconImage(iconToImage(IconsItem.ICON_TABLE));
                dialog.add(console);
                dialog.setSize(new Dimension(dialog.getWidth(), dialog.getHeight() / 3));
                dialog.pack();
              }
            }
            if (dialog != null) { 
              dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
              int posY = e.getLocationOnScreen().y + dialog.getHeight() > Toolkit.getDefaultToolkit().getScreenSize().height
                  ? e.getLocationOnScreen().y - dialog.getHeight() : e.getLocationOnScreen().y;
              int posX = e.getLocationOnScreen().x + dialog.getWidth() > Toolkit.getDefaultToolkit().getScreenSize().width
                  ? Toolkit.getDefaultToolkit().getScreenSize().width - dialog.getWidth()
                  : e.getLocationOnScreen().x;
              dialog.setLocation(posX, posY);
              dialog.setVisible(true, false);
              dialog.dispose();
            }
          }
        }
      });
    }

    public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
      Component component = super.prepareRenderer(renderer, row, column);
      component.setFont(this.getFont());
      if (component instanceof JComponent) {
        ((JComponent)component).setToolTipText(
            "<html><table cellpadding=\"0\" cellspacing=\"0\" border=\"0\">" +
            (getValueAt(row, column) instanceof K.KType<?> && ((K.KType<?>)getValueAt(row, column)).isNull() ? "" :
              getValueAt(row, column) instanceof K.KBaseArray ? "" :
              "<tr><td align=\"right\">Value: </td><td> " + getValueAt(row, column) + "</td></tr>") + 
            (getValueAt(row, column) instanceof K.KType<?> ? 
                "<tr><td align=\"right\">Type: </td><td> " + ((K.KType<?>)getValueAt(row, column)).getType() + "h</td></tr>" : "") + 
                "</table></html>");
      }
      return component;
    }
    private Font originalFont;
    private int originalRowHeight;
    private float zoomFactor = 1.0f;

    public void setFont(Font font) {
      originalFont = font;
      // When setFont() is first called, zoomFactor is 0.
      if (zoomFactor != 0.0 && zoomFactor != 1.0) {
        float scaledSize = originalFont.getSize2D() * zoomFactor;
        font = originalFont.deriveFont(scaledSize);
      }

      super.setFont(font);
    }

    public void setRowHeight(int rowHeight) {
      originalRowHeight = rowHeight;
      // When setRowHeight() is first called, zoomFactor is 0.
      if (zoomFactor != 0.0 && zoomFactor != 1.0)
        rowHeight = (int)Math.ceil(originalRowHeight * zoomFactor);

      super.setRowHeight(rowHeight);
    }

    public float getZoom() {
      return zoomFactor;
    }

    public void setZoom(float zoomFactor) {
      if (this.zoomFactor == zoomFactor)
        return;

      if (originalFont == null)
        originalFont = getFont();
      if (originalRowHeight == 0)
        originalRowHeight = getRowHeight();

      float oldZoomFactor = this.zoomFactor;
      this.zoomFactor = zoomFactor;
      Font font = originalFont;
      if (zoomFactor != 1.0) {
        float scaledSize = originalFont.getSize2D() * zoomFactor;
        font = originalFont.deriveFont(scaledSize);
      }

      super.setFont(font);
      super.setRowHeight((int)Math.ceil(originalRowHeight * zoomFactor));
      ((TableHeaderRenderer)getTableHeader().getDefaultRenderer()).setFont(font);

      firePropertyChange("zoom", oldZoomFactor, zoomFactor);

      WidthAdjuster wa = new WidthAdjuster(this);
      wa.resizeAllColumns();
      invalidate();
    }

    public Component prepareEditor(TableCellEditor editor, int row, int column) {
      Component comp = super.prepareEditor(editor, row, column);
      comp.setFont(this.getFont());
      return comp;
    }

    private Image iconToImage(Icon icon) {
      if (icon instanceof ImageIcon) {
        return ((ImageIcon)icon).getImage();
      }
      else {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        GraphicsConfiguration gc = gd.getDefaultConfiguration();
        BufferedImage image = gc.createCompatibleImage(icon.getIconWidth(), icon.getIconHeight());
        Graphics2D grpahics = image.createGraphics();
        icon.paintIcon(null, grpahics, 0, 0);
        grpahics.dispose();
        return image;
      }
    }
  };
  
  public QGrid(K.KType<?> obj) {
    super();

    TableModel model = obj instanceof K.KTable ? 
        new KTableTableModel((K.KTable)obj) : obj instanceof K.KList ? 
            new KListTableModel((K.KList)obj) : obj instanceof K.KBaseArray ? 
                new KArrayTableModel((K.KBaseArray)obj) : new KDictionaryTableModel((K.KDictionary)obj);
    table = new GridTable(model);

    table.getTableHeader().setDefaultRenderer(new TableHeaderRenderer());
    table.setShowHorizontalLines(true);

//    table.setDragEnabled(true);
    table.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
    table.setCellSelectionEnabled(true);

    // table.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_C,Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()),
    // TransferHandler.getCopyAction().getValue(Action.NAME));

    // ToolTipManager.sharedInstance().unregisterComponent(table);

    table.setToolTipText(((KType<?>)obj).toString(true));
    ToolTipManager.sharedInstance().registerComponent(table);
    ToolTipManager.sharedInstance().unregisterComponent(table.getTableHeader());

    DefaultTableCellRenderer dcr = new GridCellRenderer(table);
    // dcr.setHorizontalAlignment(SwingConstants.RIGHT);
    // dcr.setVerticalAlignment(SwingConstants.CENTER);

    for (int i = 0; i < model.getColumnCount(); i++) {
      TableColumn col = table.getColumnModel().getColumn(i);
      col.setCellRenderer(dcr);
    }

    table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

    // AutoFitTableColumns.autoResizeTable(table, true);

    table.getTableHeader().setReorderingAllowed(true);
    final JScrollPane scrollPane = new JScrollPane(table);

    if (table.getRowCount() > 0) {
      TableRowHeader trh = new TableRowHeader(table);
      scrollPane.setRowHeaderView(trh);

      scrollPane.getRowHeader().addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent ev) {
          Point header_pt = ((JViewport)ev.getSource()).getViewPosition();
          Point main_pt = main.getViewPosition();
          if (header_pt.y != main_pt.y) {
            main_pt.y = header_pt.y;
            main.setViewPosition(main_pt);
          }
        }
        JViewport main = scrollPane.getViewport();
      });

    }
    widthAdjuster = new WidthAdjuster(table);
    widthAdjuster.resizeAllColumns();

    scrollPane.setWheelScrollingEnabled(true);
    scrollPane.getViewport().setBackground(UIManager.getColor("Table.background"));
    scrollPane.setBorder(null);
    scrollPane.setViewportBorder(null);
    JLabel rowCountLabel = new JLabel("");
    rowCountLabel.setHorizontalAlignment(SwingConstants.RIGHT);
    rowCountLabel.setVerticalAlignment(SwingConstants.CENTER);
    rowCountLabel.setOpaque(true);
    rowCountLabel.setBorder(UIManager.getBorder("TableHeader.cellBorder"));
    rowCountLabel.setFont(UIManager.getFont("Table.font"));
    rowCountLabel.setBackground(UIManager.getColor("TableHeader.background"));
    rowCountLabel.setForeground(UIManager.getColor("TableHeader.foreground"));
    scrollPane.setCorner(JScrollPane.UPPER_LEFT_CORNER, rowCountLabel);

    rowCountLabel = new JLabel("");
    rowCountLabel.setHorizontalAlignment(SwingConstants.RIGHT);
    rowCountLabel.setVerticalAlignment(SwingConstants.CENTER);
    rowCountLabel.setOpaque(true);
    rowCountLabel.setBorder(UIManager.getBorder("TableHeader.cellBorder"));
    rowCountLabel.setFont(UIManager.getFont("Table.font"));
    rowCountLabel.setBackground(UIManager.getColor("TableHeader.background"));
    rowCountLabel.setForeground(UIManager.getColor("TableHeader.foreground"));
    scrollPane.setCorner(JScrollPane.UPPER_RIGHT_CORNER, rowCountLabel);
    
    setLayout(new BorderLayout());
    this.add(scrollPane, BorderLayout.CENTER);
    
    setComponentPopupMenu(new GridPopup(table));
    table.addMouseListener(new PopupListener(getComponentPopupMenu()));
    Action action = new ActionBase() {
      private static final long serialVersionUID = 8457887429514117694L;

      public void actionPerformed(ActionEvent event) {
        Point point = MouseInfo.getPointerInfo().getLocation();
        SwingUtilities.convertPointFromScreen(point, table);
        getComponentPopupMenu().show(table, point.x, point.y);        
      }
    };
    registerKeyboardAction(action, KeyStroke.getKeyStroke("shift F10"), JComponent.WHEN_IN_FOCUSED_WINDOW);
  }


  public JTable getTable() {
    return table;
  }

  public WidthAdjuster getWidthAdjaster() {
    return widthAdjuster;
  }

  public int getRowCount() {
    return table.getModel() != null ? table.getModel().getRowCount() : 0;
  }

  public void append(K.KType<?> upd) {
    int rows = table.getRowCount();
    if (table.getModel() instanceof KTableTableModel) {
      K.KTable flip = (K.KTable)upd;
      ((KTableTableModel)table.getModel()).append(flip);
      ((AbstractTableModel)table.getModel()).fireTableRowsInserted(rows, table.getRowCount() - rows);
      if (scrollToEnd)
        table.scrollRectToVisible(new Rectangle(0, table.getRowHeight() * table.getRowCount(), 
            100, table.getRowHeight()));
    }
    else
      if (table.getModel() instanceof KDictionaryTableModel) {
        K.KDictionary dict = (K.KDictionary)upd;
        ((KDictionaryTableModel)table.getModel()).upsert(dict);
        ((AbstractTableModel)table.getModel()).fireTableRowsInserted(0, table.getRowCount());
        if (scrollToEnd)
          table.scrollRectToVisible(new Rectangle(0, table.getRowHeight() * table.getRowCount(),
              100, table.getRowHeight()));
      }
  }
}
