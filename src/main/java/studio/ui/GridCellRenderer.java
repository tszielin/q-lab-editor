/*
 * Studio for kdb+ by Charles Skelton is licensed under a Creative Commons
 * Attribution-Noncommercial-Share Alike 3.0 Germany License
 * http://creativecommons.org/licenses/by-nc-sa/3.0 except for the netbeans components which retain
 * their original copyright notice
 */

package studio.ui;

import java.awt.Color;
import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.*;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;

import studio.kdb.KTableModel;
import tszielin.qlab.config.AppConfig;
import tszielin.qlab.config.data.DataType;
import tszielin.qlab.error.StudioException;

import com.kx.kdb.K;
import com.kx.kdb.K.KType;

class GridCellRenderer extends DefaultTableCellRenderer {
  private static final long serialVersionUID = -6726051225370431924L;
  
  private static Color keyColor = new Color(220, 255, 220);
  private static Color altColor = new Color(220, 220, 255);
  private static Color nullColor = new Color(255, 150, 150);
  private static Color selColor = UIManager.getColor("Table.selectionBackground");
  private Color fgColor;
  private JTable table = null;
  
  private final JLabel labelBox;
  private final JCheckBox checkBox;
  private Border noFocusBorder;
  private JComponent component;
  
  private DecimalFormat intFormatter;
  private DecimalFormat decFormatter;
  private SimpleDateFormat timeFormatter;
  private SimpleDateFormat dateFormatter;
  private SimpleDateFormat datetimeFormatter;
  private boolean booleanFormatted;
  
  private AppConfig config;

  public GridCellRenderer(JTable table) {
    super();    
    try {
      config = AppConfig.getConfig();
    }
    catch(StudioException ignored) {
      
    }
    labelBox = new JLabel();
    checkBox = new JCheckBox();
    component = labelBox;
    init();
    this.table = table;
    this.table.addPropertyChangeListener(new PropertyChangeListener() {
      public void propertyChange(PropertyChangeEvent event) {
        propertyChanged(event);
      }
    });
    setOpaque(true);    
    setFont(UIManager.getFont("Table.font"));
    setBackground(UIManager.getColor("Table.background"));
    fgColor = UIManager.getColor("Table.foreground");
    
    if (config != null) {      
      intFormatter = config.isFormatted(DataType.INTEGER) ?
          new DecimalFormat(config.getFormat(DataType.INTEGER)) : null;
      if (intFormatter != null && config.getFormat(DataType.INTEGER).indexOf(" ") > -1) {
        DecimalFormatSymbols dfs = intFormatter.getDecimalFormatSymbols();
        dfs.setGroupingSeparator(' ');
        intFormatter.setDecimalFormatSymbols(dfs);
        intFormatter.applyPattern(config.getFormat(DataType.INTEGER).replaceAll(" ", ","));
      }
      decFormatter = config.isFormatted(DataType.DECIMAL) ?
          new DecimalFormat(config.getFormat(DataType.DECIMAL)) : null;
      if (intFormatter != null && config.getFormat(DataType.DECIMAL).indexOf(" ") > -1) {
        DecimalFormatSymbols dfs = decFormatter.getDecimalFormatSymbols();
        dfs.setGroupingSeparator(' ');
        decFormatter.setDecimalFormatSymbols(dfs);
        decFormatter.applyPattern(config.getFormat(DataType.DECIMAL).replaceAll(" ", ","));
      }
      timeFormatter = config.isFormatted(DataType.TIME) ? 
          new SimpleDateFormat(config.getFormat(DataType.TIME)) : null;
      dateFormatter = config.isFormatted(DataType.DATE) ? 
          new SimpleDateFormat(config.getFormat(DataType.DATE)) : null;
      datetimeFormatter = config.isFormatted(DataType.DATETIME) ? 
          new SimpleDateFormat(config.getFormat(DataType.DATETIME)) : null;
      booleanFormatted = config.isFormatted(DataType.BOOLEAN);
    }
  }
  
  private void init() {
    noFocusBorder = BorderFactory.createEmptyBorder(1, 2, 1, 2);
    labelBox.setOpaque(true);
    labelBox.setBorder(noFocusBorder);
    checkBox.setOpaque(true);
    checkBox.setBorder(noFocusBorder);
    checkBox.setHorizontalAlignment(0);
  }

  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
      boolean hasFocus, int row, int column) {  

    component = labelBox;
    if (value == null) {
      labelBox.setHorizontalAlignment(0);
      labelBox.setText("");
    }
    if (value instanceof K.KType<?>) {
      if (value instanceof K.KBoolean && !((K.KBoolean)value).isNull()) {
        if (booleanFormatted) {
          checkBox.setSelected(((K.KBoolean)value).getValue());
          checkBox.setIcon(null);        
          component = checkBox;
        }
        else {
          labelBox.setText(((K.KBoolean)value).getValue() ? "1" : "0");
          labelBox.setHorizontalAlignment(SwingConstants.CENTER);          
        }
      }
      else {
        labelBox.setText(null);
        if (!((K.KType<?>)value).isNull()) {
          if ((value instanceof K.KInteger || value instanceof K.KLong) && intFormatter != null) {
            labelBox.setText(intFormatter.format(value instanceof K.KInteger ? 
                ((K.KInteger)value).getValue() : ((K.KLong)value).getValue()));
            labelBox.setHorizontalAlignment(SwingConstants.RIGHT);            
          }
          else {
            if ((value instanceof K.KFloat || value instanceof K.KReal) && decFormatter != null) {
              labelBox.setText(decFormatter.format(((K.KBase<?>)value).getValue()));
              labelBox.setHorizontalAlignment(SwingConstants.RIGHT);              
            }
            else {
              if (timeFormatter != null && value instanceof K.KTime) {
                labelBox.setText(timeFormatter.format(((K.KTime)value).toDate()));
                labelBox.setHorizontalAlignment(SwingConstants.CENTER);                
              }
              else {
                if (dateFormatter != null && value instanceof K.KDate) {
                  labelBox.setText(dateFormatter.format(((K.KDate)value).toDate()));
                  labelBox.setHorizontalAlignment(SwingConstants.CENTER);                  
                }
                else {
                  if (datetimeFormatter != null && value instanceof K.KDatetime) {
                    labelBox.setText(datetimeFormatter.format(((K.KDatetime)value).toDate()));
                    labelBox.setHorizontalAlignment(SwingConstants.CENTER);                    
                  }
                }
              }
            }
          }
          if (component instanceof JLabel && (labelBox.getText() == null ||
              labelBox.getText().trim().length() == 0)) {
            String str = ((KType<?>)value).toString(value instanceof K.KBaseArray); 
            labelBox.setHorizontalAlignment(value instanceof K.KSymbol ||
                value instanceof K.KSymbolArray || value instanceof K.KCharacter ||
                value instanceof K.KCharacterArray || value instanceof K.KRealArray ||
                value instanceof K.KFloatArray || value instanceof K.KIntegerArray ||
                value instanceof K.KLongArray || value instanceof K.KBooleanArray ||
                value instanceof K.KByteArray || value instanceof K.KDateArray ||
                value instanceof K.KTimeArray || value instanceof K.KDatetimeArray ||
                value instanceof K.KTimestampArray ? SwingConstants.LEFT
                : value instanceof K.KDate || value instanceof K.KTime ||
                    value instanceof K.KDatetime || value instanceof K.KTimestamp
                    ? SwingConstants.CENTER : SwingConstants.RIGHT);
            labelBox.setText(str);
            labelBox.setForeground(value instanceof K.KType<?> && ((K.KType<?>)value).isNull() ? nullColor : fgColor);
          }
          component = labelBox;
        }
      }
    }
    
    if (!isSelected) {
      KTableModel ktm = (KTableModel)table.getModel();
      column = table.convertColumnIndexToModel(column);
      if (ktm.isKey(column))
        component.setBackground(keyColor);
      else
        if (row % 2 == 0)
          component.setBackground(altColor);
        else
          component.setBackground(UIManager.getColor("Table.background"));
    }
    else {
      component.setBackground(selColor);
    }
    component.setForeground(isSelected ? UIManager.getColor("Table.selectionForeground") : 
      value instanceof K.KType<?> ? ((K.KType<?>)value).isNull() ? nullColor : 
        config.hasTokenColors() ? config.getTokenColor(((K.KType<?>)value).getType()) : 
          UIManager.getColor("Table.foreground") : UIManager.getColor("Table.foreground"));
    return component;
  }

  private void propertyChanged(PropertyChangeEvent event) {
    if ("zoom".equals(event.getPropertyName()))
      setFont(table.getFont());
  }
}
