package tszielin.qlab.config.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Collections;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;

import studio.ui.EscapeDialog;
import studio.ui.Studio;
import tszielin.qlab.config.ProjectConfig;
import tszielin.qlab.error.ArgumentException;
import tszielin.qlab.util.error.ConfigException;
import tszielin.qlab.util.image.IconsItem;

public class ProjectDialog extends EscapeDialog {
  private static final long serialVersionUID = 5788343898642841987L;
  private JPanel content;
  private JPanel pnlButtons;
  private JPanel pnlData;
  private JTextField txtExt;
  private JList<String> lstExt;
  
  private ProjectConfig config;
    
  public ProjectDialog(Studio studio) throws ConfigException, ArgumentException {
    super(studio, "Project settings...", ModalityType.MODELESS);
    this.config = ProjectConfig.getConfig();
    initialize();
    setIconImage(IconsItem.IMAGE_APP);
  }

  /**
   * This method initializes this
   */
  private void initialize() {
    try {
      this.setSize(new Dimension(300, 180));
      this.setContentPane(getContent());
      this.setResizable(false);
      this.setModal(true);
      this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
      this.pack();

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
        
        JLabel label = new JLabel("Extensions");
        pnlData.add(label, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(20, 20, 5, 0), 0, 0));
        txtExt = new JTextField();
        label.setLabelFor(txtExt);
        txtExt.setToolTipText("Press ENTER or INSERT to add extension to the list");
        txtExt.addKeyListener(new KeyListener() {
          public void keyTyped(KeyEvent event) {
          }
          public void keyReleased(KeyEvent event) {
            if ((event.getKeyCode() == KeyEvent.VK_ENTER || 
                event.getKeyCode() == KeyEvent.VK_INSERT) &&
                event.getSource() instanceof JTextField) {
              String text = ((JTextField)event.getSource()).getText();
              if (text != null && !text.trim().isEmpty()) {
                if (text.lastIndexOf(".") > -1) {
                  text = text.substring(text.lastIndexOf(".") + 1);
                }
                if (lstExt.getModel() instanceof DefaultListModel) {
                  if (!((DefaultListModel<String>)lstExt.getModel()).contains(text)) {
                    ((DefaultListModel<String>)lstExt.getModel()).addElement(text);
                  }
                }
                try {
                  config.setExtensions((java.util.List<String>)Collections.list(((DefaultListModel<String>)lstExt.getModel()).elements()));
                }
                catch (ConfigException ex) {
                  JOptionPane.showMessageDialog(getOwner(), ex.getMessage(), "Project settings", 
                      JOptionPane.ERROR_MESSAGE);
                }
                txtExt.setText("");
              }
              event.consume();
            }
          }
          public void keyPressed(KeyEvent event) {            
          }
        });
        pnlData.add(txtExt, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(20, 0, 5, 20), 50, 0));
        DefaultListModel<String> model = new DefaultListModel<String>();
        lstExt = new JList<String>(model);
        lstExt.setToolTipText("Press DELETE to remove extension from the list");
        java.util.List<String> exts = config.getExtensions();
        Collections.sort(exts);
        for (String ext : exts) {
          model.addElement(ext);
        }
        lstExt.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lstExt.addKeyListener(new KeyListener() {
          public void keyTyped(KeyEvent event) {
          }
          public void keyReleased(KeyEvent event) {
          }
          @SuppressWarnings("unchecked")
          public void keyPressed(KeyEvent event) {
            if (event.getSource() instanceof JList &&
                ((JList<String>)event.getSource()).getSelectedIndex() != -1) {
              int index = ((JList<String>)event.getSource()).getSelectedIndex();
              if (event.getKeyCode() == KeyEvent.VK_DELETE) {
                if (event.isControlDown()) {
                  ((DefaultListModel<String>)lstExt.getModel()).remove(index);
                  try {
                    config.setExtensions((java.util.List<String>)Collections.list(((DefaultListModel<String>)lstExt.getModel()).elements()));
                  }
                  catch (ConfigException ex) {
                    JOptionPane.showMessageDialog(getOwner(), ex.getMessage(), "Project settings", JOptionPane.ERROR_MESSAGE);
                  }
                }
                else {
                  if (JOptionPane.showConfirmDialog(getOwner(), 
                      "Do you want remove extension '" + 
                      lstExt.getSelectedValue() + "'?", "Confirm remove",
                      JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                    ((DefaultListModel<String>)lstExt.getModel()).remove(index);
                    try {
                      config.setExtensions((java.util.List<String>)Collections.list(((DefaultListModel<String>)lstExt.getModel()).elements()));
                    }
                    catch (ConfigException ex) {
                      JOptionPane.showMessageDialog(getOwner(), ex.getMessage(), "Project settings", JOptionPane.ERROR_MESSAGE);
                    }
                  }
                }
              }
            }
          }
        });
        pnlData.add(new JScrollPane(lstExt), new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
            GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 20, 20), 50, 0));
      }
      catch (java.lang.Throwable ignored) {
      }
    }
    return pnlData;
  }
}