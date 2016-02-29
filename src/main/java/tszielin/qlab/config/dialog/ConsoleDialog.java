package tszielin.qlab.config.dialog;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import studio.ui.EscapeDialog;
import studio.ui.Studio;
import tszielin.qlab.config.AppConfig;
import tszielin.qlab.config.data.DataType;
import tszielin.qlab.error.ArgumentException;
import tszielin.qlab.util.error.ConfigException;
import tszielin.qlab.util.image.IconsItem;

public class ConsoleDialog extends EscapeDialog {
  private static final long serialVersionUID = -6802502174087123972L;
  private JPanel content;
  private JPanel pnlButtons;
  private JButton btnAccept;
  private JButton btnCancel;
  private JPanel pnlMulti;  
  private JPanel pnlZero; 
  private JPanel pnlFormats;
  private JCheckBox chbMulti;
  private JCheckBox chbZero;
  private JCheckBox chbInteger;
  private JComboBox<String> cbInteger;
  private JCheckBox chbDecimal;
  private JComboBox<String> cbDecimal;
  private JCheckBox chbDate;
  private JComboBox<String> cbDate;
  private JCheckBox chbTime;
  private JComboBox<String> cbTime;
  private JCheckBox chbDateTime;
  private JComboBox<String> cbDateTime;
  private JCheckBox chbBoolean;
  private JCheckBox chbTokenColor;
  private JSpinner spFunction;
  
  private AppConfig config;
  
  public ConsoleDialog(Studio studio) throws ConfigException, ArgumentException {
    super(studio, "Console settings", true);
    config = AppConfig.getConfig();
    setIconImage(IconsItem.IMAGE_APP);
		initialize();
  }

  private void initialize() {
    this.setLayout(new BorderLayout());
    this.setSize(320, 270);
    this.setContentPane(getContent());
    this.setResizable(false);
    this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    this.pack();
    if (this.getRootPane() != null) {
      this.getRootPane().setDefaultButton(btnAccept);
    }
  }
  
  private JPanel getContent() {
    if (content == null) {
      try {
        content = new JPanel(new BorderLayout());
        content.add(getPanelButtons(), BorderLayout.SOUTH);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(getPanelMulti(), BorderLayout.NORTH);
        panel.add(getPanelZero(), BorderLayout.CENTER);
        content.add(panel, BorderLayout.NORTH);
        content.add(getPanelFormats(), BorderLayout.CENTER);
      }
      catch (java.lang.Throwable ignored) {
      }
    }
    return content;
  }
  
  private JPanel getPanelButtons() {
    if (pnlButtons == null) {
      try {
        pnlButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        pnlButtons.add(getBtnAccept());
        pnlButtons.add(getBtnCancel());
      }
      catch (java.lang.Throwable ignored) {
      }
    }
    return pnlButtons;
  }

  private JButton getBtnAccept() {
    if (btnAccept == null) {
      try {
        btnAccept = new JButton("Accept");
        btnAccept.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent event) {
            boolean changed = false;
            try {
              config.setZeroNumeration(chbZero.isSelected());
              changed |= chbMulti.isSelected() != config.hasMultiConsoles();
              config.setMultiConsoles(chbMulti.isSelected());
              if (!changed) {
                changed |= chbInteger.isSelected() != config.isFormatted(DataType.INTEGER) &&
                    !cbInteger.getSelectedItem().equals(config.getFormat(DataType.INTEGER));
              }
              config.setFormat(DataType.INTEGER, chbInteger.isSelected(),
                  cbInteger.getSelectedItem() instanceof String
                      ? (String)cbInteger.getSelectedItem() : null);
              if (!changed) {
                changed |= chbDecimal.isSelected() != config.isFormatted(DataType.DECIMAL) &&
                    !cbDecimal.getSelectedItem().equals(config.getFormat(DataType.DECIMAL));
              }
              config.setFormat(DataType.DECIMAL, chbDecimal.isSelected(),
                  cbDecimal.getSelectedItem() instanceof String
                      ? (String)cbDecimal.getSelectedItem() : null);
              if (!changed) {
                changed |= chbDate.isSelected() != config.isFormatted(DataType.DATE) &&
                    !cbDate.getSelectedItem().equals(config.getFormat(DataType.DATE));
              }
              config.setFormat(DataType.DATE, chbDate.isSelected(),
                  cbDate.getSelectedItem() instanceof String
                      ? (String)cbDate.getSelectedItem() : null);
              if (!changed) {
                changed |= chbTime.isSelected() != config.isFormatted(DataType.TIME) &&
                    !cbTime.getSelectedItem().equals(config.getFormat(DataType.TIME));
              }
              config.setFormat(DataType.TIME, chbDate.isSelected(),
                  cbTime.getSelectedItem() instanceof String
                      ? (String)cbTime.getSelectedItem() : null);
              if (!changed) {
                changed |= chbDateTime.isSelected() != config.isFormatted(DataType.DATETIME) &&
                    !cbDateTime.getSelectedItem().equals(config.getFormat(DataType.DATETIME));
              }
              config.setFormat(DataType.DATETIME, chbDateTime.isSelected(),
                  cbDateTime.getSelectedItem() instanceof String
                      ? (String)cbDateTime.getSelectedItem() : null);
              if (!changed) {
                changed |= chbBoolean.isSelected() != config.isFormatted(DataType.BOOLEAN);
              }
              config.setFormat(DataType.BOOLEAN, chbBoolean.isSelected(), null);
              if (!changed) {
                changed |= chbTokenColor.isSelected() != config.hasTokenColors();
              }
              config.setTokenColors(chbTokenColor.isSelected());
              if (!changed) {
                changed |= ((Integer)spFunction.getValue()).intValue() != config.getFunctionLength();
              }
              config.setFunctionLength(((Integer)spFunction.getValue()).intValue());
              setVisible(false);
              if (changed) {
                JOptionPane.showMessageDialog(getOwner(),
                    "Changes will be available with new result in console.", "Settings updated",
                    JOptionPane.INFORMATION_MESSAGE);
              }
            }
            catch (ConfigException ex) {
              JOptionPane.showMessageDialog(getOwner(), ex.getMessage(), "Console settings",
                  JOptionPane.ERROR_MESSAGE);
            }
          }
        });
      }
      catch (java.lang.Throwable ignored) {
      }
    }
    return btnAccept;
  }

  private JButton getBtnCancel() {
    if (btnCancel == null) {
      try {
        btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent event) {
            setVisible(false);
          }});
      }
      catch (java.lang.Throwable ignored) {
      }
    }
    return btnCancel;
  }

  /**
   * This method initializes jPanel	
   * 	
   * @return javax.swing.JPanel	
   */
  private JPanel getPanelMulti() {
    if (pnlMulti == null) {
      try {
        pnlMulti = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        chbMulti = new JCheckBox("Multi consoles?");
        chbMulti.setToolTipText("Show results in console assigned to editor?");
        chbMulti.setSelected(config.hasMultiConsoles());
        pnlMulti.add(chbMulti);
      }
      catch (java.lang.Throwable ignored) {
      }
    }
    return pnlMulti;
  }
  
  private JPanel getPanelZero() {
    if (pnlZero == null) {
      try {
        pnlZero = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        chbZero = new JCheckBox("Rows from zero?");
        chbZero.setToolTipText("Count result rows from 0?");
        chbZero.setSelected(config.isZeroNumeration());
        pnlZero.add(chbZero);
      }
      catch (java.lang.Throwable ignored) {
      }
    }
    return pnlZero;
  }

  /**
   * This method initializes pnlFormats	
   * 	
   * @return javax.swing.JPanel	
   */
  private JPanel getPanelFormats() {
    if (pnlFormats == null) {
      try {
        pnlFormats = new JPanel(new GridBagLayout());
        
        chbInteger = new JCheckBox("Format integers");
        chbInteger.setHorizontalTextPosition(SwingConstants.LEFT);
        chbInteger.setToolTipText("Format integer/long numbers");
        pnlFormats.add(chbInteger, new GridBagConstraints(0, 0, 1, 1, 0d, 0d, 
            GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(20, 20, 0, 0), 0, 0));
        cbInteger = new JComboBox<String>(new String[] {"# ##0", "#,##0"});
        cbInteger.setEditable(true);
        pnlFormats.add(cbInteger, new GridBagConstraints(1, 0, 1, 1, 0d, 0d, 
            GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(20, 0, 0, 20), 0, 0));
        chbInteger.addActionListener(new ActionListener() {          
          public void actionPerformed(ActionEvent event) {
            cbInteger.setEnabled(chbInteger.isSelected());
          }
        });
        chbInteger.setSelected(config.isFormatted(DataType.INTEGER));
        cbInteger.setEnabled(chbInteger.isSelected());
        
        chbDecimal = new JCheckBox("Format decimals");
        chbDecimal.setHorizontalTextPosition(SwingConstants.LEFT);
        chbDecimal.setToolTipText("Format real/double numbers");
        pnlFormats.add(chbDecimal, new GridBagConstraints(0, 1, 1, 1, 0d, 0d, 
            GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 20, 0, 0), 0, 0));
        cbDecimal = new JComboBox<String>(new String[] {"# ##0.0000", "#,##0.0000", "# #00.00", "#,#00.00"});
        cbDecimal.setEditable(true);
        pnlFormats.add(cbDecimal, new GridBagConstraints(1, 1, 1, 1, 0d, 0d, 
            GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 20), 0, 0));
        chbDecimal.addActionListener(new ActionListener() {          
          public void actionPerformed(ActionEvent event) {
            cbDecimal.setEnabled(chbDecimal.isSelected());
          }
        });
        chbDecimal.setSelected(config.isFormatted(DataType.DECIMAL));
        cbDecimal.setEnabled(chbDecimal.isSelected());
        
        chbTime = new JCheckBox("Format time");
        chbTime.setHorizontalTextPosition(SwingConstants.LEFT);
        chbTime.setToolTipText("Format time");
        pnlFormats.add(chbTime, new GridBagConstraints(0, 2, 1, 1, 0d, 0d, 
            GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 20, 0, 0), 0, 0));
        cbTime = new JComboBox<String>(new String[] {"HH:mm:ss.SSS", "HH:mm:ss"});
        cbTime.setEditable(true);
        pnlFormats.add(cbTime, new GridBagConstraints(1, 2, 1, 1, 0d, 0d, 
            GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 20), 0, 0));
        chbTime.addActionListener(new ActionListener() {          
          public void actionPerformed(ActionEvent event) {
            cbTime.setEnabled(chbTime.isSelected());
          }
        });
        chbTime.setSelected(config.isFormatted(DataType.TIME));
        cbTime.setEnabled(chbTime.isSelected());
        
        chbDate = new JCheckBox("Format date");
        chbDate.setHorizontalTextPosition(SwingConstants.LEFT);
        chbDate.setToolTipText("Format date");
        pnlFormats.add(chbDate, new GridBagConstraints(0, 3, 1, 1, 0d, 0d, 
            GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 20, 0, 0), 0, 0));
        cbDate = new JComboBox<String>(new String[] {"yyyy-MM-dd", "yy-MM-dd", "yy.MM.dd", "yyyy.MM.dd", "MM/dd/yyyy", "MM/dd/yy"});
        cbDate.setEditable(true);
        pnlFormats.add(cbDate, new GridBagConstraints(1, 3, 1, 1, 0d, 0d, 
            GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 20), 0, 0));
        chbDate.addActionListener(new ActionListener() {          
          public void actionPerformed(ActionEvent event) {
            cbDate.setEnabled(chbDate.isSelected());
          }
        });
        chbDate.setSelected(config.isFormatted(DataType.DATE));
        cbDate.setEnabled(chbDate.isSelected());
        
        chbDateTime = new JCheckBox("Format datetime");
        chbDateTime.setHorizontalTextPosition(SwingConstants.LEFT);
        chbDateTime.setToolTipText("Format datetime");
        pnlFormats.add(chbDateTime, new GridBagConstraints(0, 4, 1, 1, 0d, 0d, 
            GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 20, 0, 0), 0, 0));
        cbDateTime = new JComboBox<String>(new String[] {"yyyy-MM-dd HH:mm:ss.SSS", "yy-MM-dd HH:mm:ss.SSS", "MM/dd/yyyy HH:mm:ss.SSS", "yyyy-MM-dd HH:mm:ss", "yy-MM-dd HH:mm:ss", "MM/dd/yy HH:mm:ss", "yyyy-MM-dd'T'HH:mm:ss.SSS", "MM/dd/yyyy'T'HH:mm:ss.SSS"});
        cbDateTime.setEditable(true);
        pnlFormats.add(cbDateTime, new GridBagConstraints(1, 4, 1, 1, 0d, 0d, 
            GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 20), 0, 0));
        chbDateTime.addActionListener(new ActionListener() {          
          public void actionPerformed(ActionEvent event) {
            cbDateTime.setEnabled(chbDateTime.isSelected());
          }
        });
        chbDateTime.setSelected(config.isFormatted(DataType.DATETIME));
        cbDateTime.setEnabled(chbDateTime.isSelected());
        
        chbBoolean = new JCheckBox("Format boolean");
        chbBoolean.setHorizontalTextPosition(SwingConstants.LEFT);
        chbBoolean.setToolTipText("Show boolean value as checkbox");
        pnlFormats.add(chbBoolean, new GridBagConstraints(0, 5, 1, 1, 0d, 0d, 
            GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 20, 10, 0), 0, 0));
        chbBoolean.setSelected(config.isFormatted(DataType.BOOLEAN));
        
        chbTokenColor = new JCheckBox("Token colors");
        chbTokenColor.setHorizontalTextPosition(SwingConstants.LEFT);
        chbTokenColor.setToolTipText("Show boolean value as checkbox");
        pnlFormats.add(chbTokenColor, new GridBagConstraints(0, 6, 1, 1, 0d, 0d, 
            GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 20, 10, 0), 0, 0));
        chbTokenColor.setSelected(config.hasTokenColors());
                
        JLabel label = new JLabel("Function length");
        pnlFormats.add(label, new GridBagConstraints(0, 7, 1, 1, 0d, 0d, 
            GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 20, 20, 5), 0, 0));
        spFunction = new JSpinner(new SpinnerNumberModel(config.getFunctionLength(), 1, 100, 1));
        label.setLabelFor(spFunction);
        pnlFormats.add(spFunction, new GridBagConstraints(1, 7, 1, 1, 0d, 0d, 
            GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 20, 0), 0, 0));
      }
      catch (java.lang.Throwable ihnored) {
      }
    }
    return pnlFormats;
  }  
}
