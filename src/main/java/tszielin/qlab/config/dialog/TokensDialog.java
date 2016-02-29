package tszielin.qlab.config.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ToolTipManager;

import studio.ui.EscapeDialog;
import studio.ui.Studio;
import tszielin.qlab.config.AppConfig;
import tszielin.qlab.config.listener.TreeTokensMouseAdapter;
import tszielin.qlab.config.renderer.TreeTokensRenderer;
import tszielin.qlab.error.ArgumentException;
import tszielin.qlab.util.error.ConfigException;
import tszielin.qlab.util.image.IconsItem;

public class TokensDialog extends EscapeDialog {
  private static final long serialVersionUID = 2353802261830916527L;
  
  private JPanel content;
  private JPanel pnlButtons;
  private JList<String> lstTokens;
  private JButton btnAccept;
  private JButton btnCancel;  
  private AppConfig config;
  private Map<String, Color> map;
  
  public TokensDialog(Studio studio) throws ConfigException, ArgumentException {
    super(studio, "Token's color", true);
    setIconImage(IconsItem.IMAGE_APP);
    initialize();
    this.config = AppConfig.getConfig();
    java.util.List<String> list = config.getTokens();
    for (String item : list) {
      if (map == null) {
        map = new TreeMap<String, Color>(String.CASE_INSENSITIVE_ORDER);
      }
      map.put(item, config.getTokenColor(item));
      ((DefaultListModel<String>)lstTokens.getModel()).addElement(item);
    }
    lstTokens.setCellRenderer(new TreeTokensRenderer(map));
    lstTokens.addMouseListener(new TreeTokensMouseAdapter(this, lstTokens, map));
  }

  private void initialize() {
    this.setLayout(new BorderLayout());
    this.setSize(200, 300);
    this.setContentPane(getContent());
    this.setResizable(false);
    ToolTipManager.sharedInstance().registerComponent(lstTokens);
    this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    this.validate();
    if (this.getRootPane() != null) {
      this.getRootPane().setDefaultButton(btnAccept);
    }
  }

  private JPanel getContent() {
    if (content == null) {
      try {
        content = new JPanel(new BorderLayout(5, 10));
        content.add(new JScrollPane(getListTokens()), BorderLayout.CENTER);
        content.add(getPanelButtons(), BorderLayout.SOUTH);
      }
      catch (java.lang.Throwable ignored) {
      }
    }
    return content;
  }

  private JList<String> getListTokens() {
    if (lstTokens == null) {
      try {
        lstTokens = new JList<String>(new DefaultListModel<String>());        
      }
      catch (java.lang.Throwable ignored) {
      }
    }
    return lstTokens;
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
            if (map != null && !map.isEmpty()) {
              for(String item : map.keySet()) {
                if (!map.get(item).equals(config.getTokenColor(item))) {
                  try {                   
                    config.setTokenColor(item, map.get(item));
                    changed = true;
                  }
                  catch (ConfigException ignored) {
                  }
                }
              }
            }
            setVisible(false);
            if (changed) {
              JOptionPane.showMessageDialog(getOwner(), "Changes will be available after application restart",
                  "Settings updated", JOptionPane.INFORMATION_MESSAGE);
            }
          }});
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
}
