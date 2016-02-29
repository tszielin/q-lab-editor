package tszielin.qlab.config.dialog;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import studio.ui.EscapeDialog;
import tszielin.qlab.config.AppConfig;
import tszielin.qlab.config.data.Sort;
import tszielin.qlab.error.ArgumentException;
import tszielin.qlab.util.error.ConfigException;
import tszielin.qlab.util.image.IconsItem;

public class KdbServiceSortDialog extends EscapeDialog {
  private static final long serialVersionUID = 112175220986162813L;
  private JPanel contentPane;
  private JPanel pnlButtons;
  private JButton btnAccept;
  private JButton btnCancel;
  private JPanel pnlData;
  private JComboBox<Sort> cbSort;
  private JLabel lbSort;
  
  private AppConfig config;
  private JTree tree;
  
  public KdbServiceSortDialog(Window window, JTree tree) throws ConfigException, ArgumentException {
    super(window, "Sort kdb+ services", ModalityType.APPLICATION_MODAL);
    setIconImage(IconsItem.IMAGE_APP);
    this.config = AppConfig.getConfig();
    this.tree = tree;
    initialize();
    this.setResizable(false);
    if (this.getRootPane() != null) {
      this.getRootPane().setDefaultButton(btnAccept);
    }
  }

  /**
   * This method initializes this
   * 
   * @return void
   */
  private void initialize() {
    this.setSize(260, 100);
    this.setContentPane(getContent());
  }

  /**
   * This method initializes jContentPane
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getContent() {
    if (contentPane == null) {
      contentPane = new JPanel(new BorderLayout());
      contentPane.add(getPanelButtons(), BorderLayout.SOUTH);
      contentPane.add(getDataPanel(), BorderLayout.CENTER);
    }
    return contentPane;
  }

  /**
   * This method initializes pnlButtons	
   * 	
   * @return javax.swing.JPanel	
   */
  private JPanel getPanelButtons() {
    if (pnlButtons == null) {
      try {
        pnlButtons = new JPanel();
        pnlButtons.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        btnAccept = new JButton("Accept");
        btnAccept.addActionListener(new ActionListener() {          
          public void actionPerformed(ActionEvent event) {
            if (cbSort.getSelectedItem() instanceof Sort && ((Sort)cbSort.getSelectedItem()) != config.getSortType()) {
              try {
                config.setSortType((Sort)cbSort.getSelectedItem());
                if (tree != null && tree.getModel() instanceof DefaultTreeModel) {
                  TreePath[] paths = tree.getSelectionPaths();
                  TreePath path = paths != null && paths.length > 0 ? paths[0] : null;
                  ((DefaultTreeModel)tree.getModel()).reload();
                  if (path != null) {
                    tree.expandPath(path);
                    tree.setSelectionPath(path);
                  }
                }
              }
              catch (ConfigException ex) {
                JOptionPane.showMessageDialog(getOwner(), ex.getMessage(), "Connection settings", JOptionPane.WARNING_MESSAGE);
              }
            }
            setVisible(false);
          }
        });
        pnlButtons.add(btnAccept);
        btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent event) {
            setVisible(false);
          }
        });
        pnlButtons.add(btnCancel);
      }
      catch (java.lang.Throwable ignored) {
      }
    }
    return pnlButtons;
  }

  private JPanel getDataPanel() {
    if (pnlData == null) {
      try {
        pnlData = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        lbSort = new JLabel("Sort by");
        pnlData.add(lbSort);
        cbSort = new JComboBox<Sort>(Sort.values());
        cbSort.setSelectedItem(config.getSortType());
        pnlData.add(cbSort);
        lbSort.setLabelFor(cbSort);
      }
      catch (java.lang.Throwable ignored) {
      }
    }
    return pnlData;
  }
}
