package tszielin.qlab.config.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileFilter;

import studio.ui.EscapeDialog;
import studio.ui.Studio;
import tszielin.qlab.config.AppConfig;
import tszielin.qlab.error.ArgumentException;
import tszielin.qlab.util.error.ConfigException;
import tszielin.qlab.util.image.IconsItem;

public class GlobalDialog extends EscapeDialog {
  private static final long serialVersionUID = -9068432234110059424L;
  private JPanel content;
  private JPanel pnlButtons;
  private JPanel pnlData;
  private JTextField txtQPath;
  private JCheckBox chbLost;
  private JCheckBox chbChange;
  
  private JFileChooser chooser;
  
  private AppConfig config;
  private JTextField txtQHome;
    
  class QAppFilter extends FileFilter {
    @Override
    public boolean accept(File file) {
      return file != null && (file.isDirectory() || (file.isFile() && file.canRead() && 
        ("q".equals(file.getName()) || "q.exe".equals(file.getName()))));
    }

    @Override
    public String getDescription() {
      return "Q interpreter";
    }
  }

  public GlobalDialog(Studio studio) throws ConfigException, ArgumentException {
    super(studio, "Global settings...", ModalityType.MODELESS);
    this.config = AppConfig.getConfig();
    initialize();
    setIconImage(IconsItem.IMAGE_APP);
  }

  /**
   * This method initializes this
   */
  private void initialize() {
    try {
      this.setSize(new Dimension(380, 210));
      this.setContentPane(getContent());
      this.setResizable(false);
      this.setModal(true);
      this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
      this.validate();

    }
    catch (java.lang.Throwable ignored) {
    }
  }

  /**
   * This method initializes content
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getContent() {
    if (content == null) {
      try {
        content = new JPanel(new BorderLayout());
        content.add(getPanelData(), BorderLayout.CENTER);
        content.add(getPanelButtons(), BorderLayout.SOUTH);
      }
      catch (java.lang.Throwable ignored) {
      }
    }
    return content;
  }
  
  private JPanel getPanelButtons() {
    if (pnlButtons == null) {
      try {
        pnlButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton button = new JButton("Accept");
        pnlButtons.add(button);
        if (getRootPane() != null) {
          getRootPane().setDefaultButton(button);
        }
        button.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent event) {
            if (!txtQPath.getText().equals(config.getQApp())) {
              try {
                config.setQApp(txtQPath.getText());
              }
              catch (ConfigException ignored) {
              }
            }
            if (!txtQHome.getText().equals(config.getQHome())) {
              try {
                config.setQHome(txtQHome.getText());
              }
              catch (ConfigException ignored) {
              }
            }
            if (chbChange.isSelected() != config.isChangeConnectionNofication()) {
              try {
                config.setChangeConnectionNofication(chbChange.isSelected());
              }
              catch (ConfigException ignored) {
              }
            }
            if (chbLost.isSelected() != config.isLostConnectionMessage()) {
              try {
                config.setLostConnectionMessage(chbLost.isSelected());
              }
              catch (ConfigException ignored) {
              }
            }
            setVisible(false);
          }          
        });
        button = new JButton("Cancel");
        pnlButtons.add(button);
        button.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent event) {
            setVisible(false);
          }          
        });
      }
      catch (java.lang.Throwable ignored) {
      }
    }
    return pnlButtons;
  }

  private JPanel getPanelData() {
    if (pnlData == null) {
      try {
        pnlData = new JPanel(new GridBagLayout());               
                       
        JLabel label = new JLabel("QHome");
        pnlData.add(label, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(20, 20, 0, 5), 0, 0));
        label.setLabelFor(txtQHome);
        txtQHome = new JTextField();
        pnlData.add(txtQHome, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(20, 0, 0, 5), 150, 0));
        
        JButton button = new JButton("...");
        button.setSize(22, 22);
        button.setBorder(null);
        button.addActionListener(new ActionListener() {

          public void actionPerformed(ActionEvent event) {
            chooser = new JFileChooser();
            chooser.setFileHidingEnabled(true);
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            chooser.setCurrentDirectory(new File(txtQHome.getText()));           
            if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
              txtQPath.setText(chooser.getSelectedFile().getPath());
            }
          }          
        });
        pnlData.add(button, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(20, 0, 0, 5), 0, 0));

        label = new JLabel("Q path:");
        pnlData.add(label, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
            GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 20, 0, 5), 0, 0));
        label.setLabelFor(txtQPath);
        txtQPath = new JTextField();
        pnlData.add(txtQPath, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
            GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 0, 0, 5), 150, 0));
        button = new JButton("...");
        button.setSize(22, 22);
        button.setBorder(null);
        button.addActionListener(new ActionListener() {

          public void actionPerformed(ActionEvent event) {
            chooser = new JFileChooser();
            chooser.setFileHidingEnabled(true);
            chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            chooser.setFileFilter(new QAppFilter());
            chooser.setAcceptAllFileFilterUsed(false);
            chooser.setCurrentDirectory(new File(txtQHome.getText()));
            if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
              txtQPath.setText(chooser.getSelectedFile().getPath());
            }
          }          
        });
        pnlData.add(button, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0,
            GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 0, 0, 20), 0, 0));

        
        label = new JLabel("Notifications:");
        pnlData.add(label, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
            GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 20, 0, 5), 0, 0));
        
        chbChange = new JCheckBox("Connection changed");
        chbChange.setSelected(config.isChangeConnectionNofication());
        pnlData.add(chbChange, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0,
            GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 0, 0, 20), 0, 0));
        chbLost = new JCheckBox("Connection lost"); 
        chbLost.setSelected(config.isLostConnectionMessage());
        pnlData.add(chbLost, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0,
            GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 0, 20, 20), 0, 0));
        
        if (config.getQApp() != null && !config.getQApp().trim().isEmpty()) {
          txtQPath.setText(config.getQApp());
        }
        txtQHome.setText(config.getQHome() != null && !config.getQHome().trim().isEmpty() ?
            config.getQHome() : System.getenv("QHOME"));
      }
      catch (java.lang.Throwable ignored) {
      }
    }
    return pnlData;
  }
}