package tszielin.qlab.config.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

import org.apache.commons.io.FilenameUtils;

import studio.ui.EscapeDialog;
import studio.ui.Studio;
import tszielin.qlab.config.AppConfig;
import tszielin.qlab.config.listener.EditorBackgroundMouseAdapter;
import tszielin.qlab.config.listener.EditorFontMouseAdapter;
import tszielin.qlab.config.listener.EditorPathMouseAdapter;
import tszielin.qlab.error.ArgumentException;
import tszielin.qlab.util.error.ConfigException;
import tszielin.qlab.util.image.IconsItem;

public class EditorDialog extends EscapeDialog {
  private static final long serialVersionUID = -5177909328584819529L;
  private JPanel content;
  private JPanel pnlButtons;
  private JButton btnAccept;
  private JButton btnCancel;
  private JPanel pnlSpaces;
  private JCheckBox cbUseTabs;
  private JSpinner spSpaces;
  private JCheckBox cbMargin;
  private JSpinner spMargin;
  private JPanel pnlColors;
  private JLabel lbBackground;
  private JLabel lbFont;
  private JSpinner spAutoSave;
  private JCheckBox cbAutoSave;
  private JCheckBox cbUNIX;
  private JCheckBox cbBrackets;
  private JTextField txtPath;
  
  private AppConfig config;
  
  public EditorDialog(Studio studio) throws ConfigException, ArgumentException {
    super(studio, "Editor options", ModalityType.APPLICATION_MODAL);
    setIconImage(IconsItem.IMAGE_APP);
    this.config = AppConfig.getConfig();
    initialize();
    if (this.getRootPane() != null) {
      this.getRootPane().setDefaultButton(btnAccept);
    }
  }

  /**
   * This method initializes pnlButtons
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getPnlButtons() {
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
            if (!lbFont.getFont().equals(config.getFont())) {
              try {
                config.setFont(lbFont.getFont());
                changed = true;
              }
              catch (ConfigException ignored) {
              }
            }
            if (!lbBackground.getBackground().equals(config.getTokenColor("BACKGROUND"))) {
              try {
                config.setTokenColor("BACKGROUND", lbBackground.getBackground());
                changed = true;
              }
              catch (ConfigException ignored) {
              }
            }
            if (cbUseTabs.isSelected() != config.isExpandTabs()) {
              try {
                config.setExpandTabs(cbUseTabs.isSelected());
                changed = true;
              }
              catch (ConfigException ignored) {
              }
            }
            if (spSpaces.getValue() instanceof Integer &&
                ((Integer)spSpaces.getValue()).intValue() != config.getTabSize()) {
              try {
                config.setTabSize(((Integer)spSpaces.getValue()).intValue());
                changed = true;
              }
              catch (ConfigException ignored) {
              }
            }
            if (cbAutoSave.isSelected() != config.isAutoSave()) {
              try {
                config.setAutoSave(cbAutoSave.isSelected());
                changed = true;
              }
              catch (ConfigException ignored) {
              }
            }
            if (spAutoSave.getValue() instanceof Integer &&
                ((Integer)spAutoSave.getValue()).intValue() != config.getAutoSaveTime() / 60000) {
              try {
                config.setAutoSaveTime(((Integer)spAutoSave.getValue()).intValue());
                changed = true;
              }
              catch (ConfigException ignored) {
              }
            }
            if (!FilenameUtils.separatorsToUnix(txtPath.getText()).equals(config.getAutoSavePath())) {
              try {
                if (FilenameUtils.normalize(txtPath.getText()) != null) {
                  config.setAutoSavePath(FilenameUtils.separatorsToUnix(FilenameUtils.normalize(txtPath.getText())));
                  changed = true;
                }
              }
              catch (ConfigException ignored) {
              }
            }
            if (cbUNIX.isSelected() != config.isUnixEOL()) {
              try {
                config.setUnixEOL(cbUNIX.isSelected());
                changed = true;
              }
              catch (ConfigException ignored) {
              }
            }
            if (cbBrackets.isSelected() != config.isMatchingBrackets()) {
              try {
                config.setMatchingBrackets(cbBrackets.isSelected());
                changed = true;
              }
              catch (ConfigException ignored) {
              }
            }
            if (cbMargin.isSelected() != config.isMargin()) {
              try {
                config.setMargin(cbMargin.isSelected());
                changed = true;
              }
              catch (ConfigException ignored) {
              }
            }
            if (spMargin.getValue() instanceof Integer &&
                ((Integer)spMargin.getValue()).intValue() != config.getMargin()) {
              try {
                config.setMargin(((Integer)spMargin.getValue()).intValue());
                changed = true;
              }
              catch (ConfigException ignored) {
              }
            }
            setVisible(false);
            if (changed) {
              JOptionPane.showMessageDialog(getOwner(), 
                  "Changes will be available after application restart",
                  "Settings updated", JOptionPane.INFORMATION_MESSAGE);
            }
          }
        });
      }
      catch (java.lang.Throwable ignored) {
      }
    }
    return btnAccept;
  }

  /**
   * This method initializes btnCancel
   * 
   * @return javax.swing.JButton
   */
  private JButton getBtnCancel() {
    if (btnCancel == null) {
      try {
        btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent event) {
            setVisible(false);
          }
        });
      }
      catch (java.lang.Throwable ignored) {
      }
    }
    return btnCancel;
  }

  private JPanel getPnlSpaces() {
    if (pnlSpaces == null) {
      try {
        pnlSpaces = new JPanel();
        pnlSpaces.setLayout(new GridBagLayout());

        JLabel label = new JLabel("Insert spaces for tabs");
        pnlSpaces.add(label, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
        cbUseTabs = new JCheckBox();        
        label.setLabelFor(cbUseTabs);
        pnlSpaces.add(cbUseTabs, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
        
        label = new JLabel("Displayed tab with");
        pnlSpaces.add(label, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
            GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        spSpaces = new JSpinner(new SpinnerNumberModel(config.getTabSize(), 1, 8, 1));
        label.setLabelFor(spSpaces);
        cbUseTabs.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent event) {
            if (event.getSource() instanceof JCheckBox) {
              spSpaces.setEnabled(((JCheckBox)event.getSource()).isSelected());
            }
          }
        });
        pnlSpaces.add(spSpaces, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
            GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 4, 0, 0), 0, 0));
        cbUseTabs.setSelected(config.isExpandTabs());
        spSpaces.setEnabled(cbUseTabs.isEnabled());
        
        label = new JLabel("Show print margin");
        pnlSpaces.add(label, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
            GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        cbMargin = new JCheckBox();        
        label.setLabelFor(cbMargin);
        pnlSpaces.add(cbMargin, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0,
            GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

        label = new JLabel("Print margin column");
        pnlSpaces.add(label, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
            GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        spMargin = new JSpinner(new SpinnerNumberModel(config.getMargin().intValue(), 40, 180, 10));
        label.setLabelFor(spMargin);
        cbMargin.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent event) {
            if (event.getSource() instanceof JCheckBox) {
              spMargin.setEnabled(((JCheckBox)event.getSource()).isSelected());
            }
          }
        });
        pnlSpaces.add(spMargin, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0,
            GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 4, 0, 0), 0, 0));
        cbMargin.setSelected(config.isMargin());
        spMargin.setEnabled(cbMargin.isSelected()); 
        
        label = new JLabel("Auto save modified files");
        pnlSpaces.add(label, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0,
            GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        cbAutoSave = new JCheckBox();        
        label.setLabelFor(cbAutoSave);        
        pnlSpaces.add(cbAutoSave, new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0,
            GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));        
        
        label = new JLabel("Auto save interval [min]");
        pnlSpaces.add(label, new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0,
            GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        spAutoSave = new JSpinner(new SpinnerNumberModel(
            Long.valueOf(config.getAutoSaveTime() / 60000).intValue(), 1, 60, 1));
        label.setLabelFor(spAutoSave);
        pnlSpaces.add(spAutoSave, new GridBagConstraints(1, 5, 1, 1, 0.0, 0.0,
            GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 4, 0, 0), 0, 0));
        
        label = new JLabel("Path for auto save files");
        pnlSpaces.add(label, new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0,
            GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        txtPath = new JTextField();
        txtPath.setPreferredSize(new Dimension(200, 21));
        txtPath.setMinimumSize(new Dimension(150, txtPath.getHeight()));
        txtPath.setMaximumSize(new Dimension(300, txtPath.getHeight()));
        txtPath.getDocument().addDocumentListener(new DocumentListener() {
          public void changedUpdate(DocumentEvent event) {
            update(event);            
          }

          public void insertUpdate(DocumentEvent event) {
            update(event);
          }

          public void removeUpdate(DocumentEvent event) {
            update(event);
          }
          
          private void update(DocumentEvent event) {
            try {
              txtPath.setToolTipText(event.getDocument().getText(0, event.getDocument().getLength()));
            }
            catch (BadLocationException ignored) {
              txtPath.setToolTipText(null);
            }
          }
        });
        txtPath.addMouseListener(new EditorPathMouseAdapter(this));
        
        txtPath.setText(config.getAutoSavePath());
        txtPath.setCaretPosition(0);
        label.setLabelFor(txtPath);
        pnlSpaces.add(txtPath, new GridBagConstraints(1, 6, 1, 1, 0.0, 0.0,
            GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 4, 0, 0), 0, 0));
        cbAutoSave.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent event) {
            if (event.getSource() instanceof JCheckBox) {
              spAutoSave.setEnabled(((JCheckBox)event.getSource()).isSelected());
              txtPath.setEnabled(((JCheckBox)event.getSource()).isSelected());
            }
          }
        });
        cbAutoSave.setSelected(config.isAutoSave());
        spAutoSave.setEnabled(cbAutoSave.isSelected());
        txtPath.setEnabled(cbAutoSave.isSelected());
        
        label = new JLabel("UNIX EOL delimiters");
        pnlSpaces.add(label, new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0,
            GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        cbUNIX = new JCheckBox();
        cbUNIX.setSelected(config.isUnixEOL());
        label.setLabelFor(cbUNIX);
        pnlSpaces.add(cbUNIX, new GridBagConstraints(1, 7, 1, 1, 0.0, 0.0,
            GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        
        label = new JLabel("Insert matching brackets");
        pnlSpaces.add(label, new GridBagConstraints(0, 8, 1, 1, 0.0, 0.0,
            GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 10, 0), 0, 0));
        cbBrackets = new JCheckBox();
        cbBrackets.setSelected(config.isMatchingBrackets());
        label.setLabelFor(cbBrackets);
        pnlSpaces.add(cbBrackets, new GridBagConstraints(1, 8, 1, 1, 0.0, 0.0,
            GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 10, 0), 0, 0));
      }
      catch (java.lang.Throwable ignored) {
      }
    }
    return pnlSpaces;
  }

  private JPanel getPnlColors() {
    if (pnlColors == null) {
      try {
        pnlColors = new JPanel();
        pnlColors.setLayout(new GridBagLayout());

        JLabel label = new JLabel("Background color", SwingConstants.CENTER);
        pnlColors.add(label, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 2), 0, 0));

        lbBackground = new JLabel();
        lbBackground.setPreferredSize(new Dimension(150, 50));
        lbBackground.setMaximumSize(lbBackground.getPreferredSize());
        lbBackground.setMinimumSize(lbBackground.getPreferredSize());
        lbBackground.setBorder(new EtchedBorder());
        lbBackground.setOpaque(true);
        lbBackground.addPropertyChangeListener(new PropertyChangeListener() {
          public void propertyChange(PropertyChangeEvent event) {
            if ("background".equals(event.getPropertyName())) {
              if (event.getSource() instanceof JLabel && event.getNewValue() instanceof Color) {
                Color color = (Color)event.getNewValue();
                ((JLabel)event.getSource()).setToolTipText(
                    "R:" + color.getRed() + ", G:" + color.getGreen() + 
                    ", B:" + color.getBlue() + ", [A:" + color.getAlpha() + "]");
                lbBackground.setBackground(color);        
              }
            }            
          }}
        );
        lbBackground.setBackground(config.getTokenColor("BACKGROUND"));
        pnlColors.add(lbBackground, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 2, 0, 0), 0, 0));
        lbBackground.addMouseListener(new EditorBackgroundMouseAdapter(getParent()));

        label = new JLabel("Choose a Font", SwingConstants.CENTER);
        pnlColors.add(label, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 2, 0, 0), 0, 0));

        lbFont = new JLabel("Example", SwingConstants.CENTER);
        lbFont.setPreferredSize(new Dimension(150, 50));
        lbFont.setMaximumSize(lbFont.getPreferredSize());
        lbFont.setMinimumSize(lbFont.getPreferredSize());
        lbFont.setBorder(new EtchedBorder());
        lbFont.addPropertyChangeListener(new PropertyChangeListener() {
          public void propertyChange(PropertyChangeEvent event) {
            if ("font".equals(event.getPropertyName())) {
              if (event.getSource() instanceof JLabel && event.getNewValue() instanceof Font) {
                Font font = (Font)event.getNewValue();
                ((JLabel)event.getSource()).setToolTipText(
                    "Name:" + font.getFontName() + 
                      ", Style:" + (font.isBold() ? (font.isItalic() ? "bolditalic" : "bold") :
                          (font.isItalic() ? "italic" : "plain")) + ", Size:" + font.getSize());
                lbFont.setFont(font);        
              }
            }            
          }}
        );
        lbFont.setFont(config.getFont());
        lbFont.addMouseListener(new EditorFontMouseAdapter(getParent()));
        pnlColors.add(lbFont, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
            GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 2, 0, 0), 0, 0));
      }
      catch (java.lang.Throwable ignored) {
      }
    }
    return pnlColors;
  }

  private void initialize() {
    this.setSize(400, 340);
    this.setResizable(false);
    this.setLayout(new BorderLayout());
    this.setContentPane(getContent());
    this.validate();
    this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
  }

  private JPanel getContent() {
    if (content == null) {
      content = new JPanel();
      content.setLayout(new BorderLayout());
      content.add(getPnlButtons(), BorderLayout.SOUTH);
      content.add(getPnlSpaces(), BorderLayout.NORTH);
      content.add(getPnlColors(), BorderLayout.CENTER);
    }
    return content;
  }
}
