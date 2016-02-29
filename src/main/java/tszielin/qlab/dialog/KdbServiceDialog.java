package tszielin.qlab.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Pattern;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;
import javax.swing.text.Position.Bias;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import com.kx.KdbConnection;

import studio.core.AuthenticationManager;
import studio.ui.EscapeDialog;
import tszielin.qlab.component.tree.node.HostTreeNode;
import tszielin.qlab.component.tree.node.KdbServicesTreeNode;
import tszielin.qlab.config.AppConfig;
import tszielin.qlab.config.data.KdbService;
import tszielin.qlab.error.ArgumentException;
import tszielin.qlab.error.StudioException;
import tszielin.qlab.kdb.AuthenticationType;
import tszielin.qlab.util.image.IconsItem;

public class KdbServiceDialog extends EscapeDialog {
  private static final long serialVersionUID = 7509981584705415599L;
  
  private JPanel contentPane;
  private JPanel pnlButtons;
  private JButton btnTest;
  private JButton btnAccept;
  private JButton btnCancel;
  private JPanel pnlConnection;
  private JTextField txtHost;
  private JSpinner spPort;
  private JTextField txtName;
  private JTextField txtUsername;
  private JPasswordField txtPassword;
  private JComboBox<String> cbAuthType;
  private JTextField txtQParam;
  private JTextField txtColor;
  
  private KdbService connection;
  private JTree tree;
  private Color color;
  private DataOperation operation;

  protected KdbServiceDialog(Frame owner) {
    super(owner, true);
    initialize();
    setIconImage(IconsItem.IMAGE_APP);
    pack();
  }
  
  public KdbServiceDialog(Component parent, String host, JTree tree) throws ArgumentException {
    this(parent, null, host, tree, DataOperation.ADD);
  }
  
  public KdbServiceDialog(Component parent, KdbService connection, JTree tree) throws ArgumentException {
    this(parent, connection, tree, DataOperation.EDIT);
  }
  
  public KdbServiceDialog(Component parent, KdbService connection, JTree tree, DataOperation operation) throws ArgumentException {
    this(parent, connection, null, tree, operation);
  }
  
  protected KdbServiceDialog(Component parent, KdbService connection, String host, JTree tree, DataOperation operation) throws ArgumentException {
    this(parent instanceof JFrame ? (JFrame)parent : null);
    
    this.tree = tree;
    this.operation = operation;
    setTitle(operation == DataOperation.ADD ? "Add a new kdb+ service" : 
      operation == DataOperation.EDIT ? "Edit kdb+ service details" : "Clone kdb+ service details");
    if (connection != null) {
      this.connection = (KdbService)connection.clone();      
    }
    txtName.setText(this.connection != null ? this.connection.getName() : "");
    txtHost.setText(this.connection != null ? this.connection.getHost() : host);
    String user = this.connection != null ? this.connection.getUsername() : null;
    txtUsername.setText(user != null && user.trim().length() > 0 ? user : System.getProperty("user.name"));
    spPort.setValue(operation == DataOperation.CLONE ? 1 : 
      Integer.valueOf(this.connection != null ? this.connection.getPort(): 1));
    String pwd = this.connection != null ? this.connection.getCredentials() : null;
    pwd = pwd == null ? null : pwd.indexOf(":") > -1 ? pwd.substring(pwd.indexOf(":") + 1) : null;
    txtPassword.setText(pwd == null ? "" : pwd);
    String qApp = null;
    try {
      qApp = AppConfig.getConfig().getQApp();
    }
    catch(StudioException ex) {      
    }
    if (qApp != null) {
      txtQParam.setText(this.connection != null && this.connection.getParams() != null ? this.connection.getParams() : "");
    }
    else {
      txtQParam.setEditable(false);
      txtQParam.setEnabled(false);
    }
    DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>)cbAuthType.getModel();
    try {
      String[] managers = AuthenticationManager.getInstance().getAuthenticationMechanisms();
      String authType = this.connection != null ? this.connection.getAuthenticationType() : 
        AuthenticationType.USERNAME_AND_PASSWORD.type();
      for (int count = 0; count < managers.length; count++) {
        model.addElement(managers[count]);        
      }
      model.setSelectedItem(authType);
      if (model.getSize() == 1) {
        cbAuthType.setEnabled(false);
      }
    }
    catch (Exception ex) {
    }

    txtName.setToolTipText("The logical name for the kdb+ service");
    txtHost.setToolTipText("The hostname or ip address for the kdb+ service");
    spPort.setToolTipText("The port for the kdb+ service");
    txtUsername.setToolTipText("The username used to connect to the kdb+ service");
    txtPassword.setToolTipText("The password used to connect to the kdb+ service");
    cbAuthType.setToolTipText("The authentication mechanism to use");

    txtColor.setForeground(this.connection != null ? this.connection.getTitleColor() : Color.black);
    addWindowListener(new WindowAdapter() {
      public void windowOpened(WindowEvent event) {
        txtName.requestFocus();
      }
    });
    btnTest.setEnabled(txtHost.getText().trim().length() != 0);
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
    this.setPreferredSize(new Dimension(420, 340));
    this.setContentPane(getContent());
  }

  /**
   * This method initializes jContentPane
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getContent() {
    if (contentPane == null) {
      contentPane = new JPanel();
      contentPane.setLayout(new BorderLayout());
      contentPane.add(getPnlButtons(), BorderLayout.SOUTH);
      contentPane.add(getPanelConnection(), BorderLayout.CENTER);
    }
    return contentPane;
  }

  /**
   * This method initializes pnlButtons	
   * 	
   * @return javax.swing.JPanel	
   */
  private JPanel getPnlButtons() {
    if (pnlButtons == null) {
      try {
        pnlButtons = new JPanel();
        pnlButtons.setLayout(new BorderLayout());        
        JPanel pnlTest = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        pnlTest.add(getButtonTest(), null);
        pnlButtons.add(pnlTest, BorderLayout.WEST);
        JPanel pnlOperation = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 5));
        pnlOperation.add(getButtonAccept(), null);
        pnlOperation.add(getButtonCancel(), null);
        pnlButtons.add(pnlOperation, BorderLayout.CENTER);
      }
      catch (java.lang.Throwable ignored) {
      }
    }
    return pnlButtons;
  }

  /**
   * This method initializes btnTest	
   * 	
   * @return javax.swing.JButton	
   */
  private JButton getButtonTest() {
    if (btnTest == null) {
      try {
        btnTest = new JButton("Test");
        btnTest.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent event) {
            String user = txtUsername.getText();
            String pwd = String.valueOf(txtPassword.getPassword());
            user = user.trim().length() == 0 ? System.getProperty("user.name") : user +
                (pwd.trim().length() == 0 ? "" : ":" + pwd);
            KdbConnection server = new KdbConnection(txtHost.getText(), ((Integer)spPort.getValue()).intValue(), user);
            try {
              server.testConnection(true);
              JOptionPane.showMessageDialog(getParent(), "Connection to " + txtHost.getText() + ":" +
                  String.valueOf(spPort.getValue()) + " is valid", "kdb+ Server", JOptionPane.INFORMATION_MESSAGE);
            }
            catch (Exception ex) {
              JOptionPane.showMessageDialog(getParent(), ex.getMessage(), "kdb+ Server", JOptionPane.ERROR_MESSAGE);
            }
            finally {
              server.close();
            }
          }
        });
      }
      catch (java.lang.Throwable ignored) {
      }
    }
    return btnTest;
  }

  /**
   * This method initializes btnAccept	
   * 	
   * @return javax.swing.JButton	
   */
  private JButton getButtonAccept() {
    if (btnAccept == null) {
      try {
        btnAccept = new JButton("Accept");
        btnAccept.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(ActionEvent event) {
            btnAcceptClicked(event);
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
  private JButton getButtonCancel() {
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

  /**
   * This method initializes pnlConnection	
   * 	
   * @return javax.swing.JPanel	
   */
  private JPanel getPanelConnection() {
    if (pnlConnection == null) {
      try {
        pnlConnection = new JPanel(new GridBagLayout());
        JLabel label = new JLabel("Name");
        pnlConnection.add(label, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(20, 20, 15, 0), 0, 0));
        txtName = new JTextField();
        label.setLabelFor(txtName);
        pnlConnection.add(txtName, new GridBagConstraints(1, 0, 1, 1, 1.0, 1.0,
            GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(20, 5, 15, 20), 0, 0));
        
        label = new JLabel("Host");
        pnlConnection.add(label, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
            GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 20, 0, 0), 0, 0));
        txtHost = new JTextField();
        label.setLabelFor(txtHost);
        pnlConnection.add(txtHost, new GridBagConstraints(1, 1, 1, 1, 1.0, 1.0,
            GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 20), 0, 0));
        txtHost.addFocusListener(new FocusListener() {
          public void focusGained(FocusEvent event) {
            txtHost.selectAll();
          }
          public void focusLost(FocusEvent event) {
            if (!txtHost.getText().trim().isEmpty()) {
              if (Pattern.matches("^(([a-zA-Z]|[a-zA-Z][a-zA-Z0-9\\-]*[a-zA-Z0-9])\\.)*([A-Za-z]|[A-Za-z][A-Za-z0-9\\-]*[A-Za-z0-9])$", txtHost.getText()) ||
                Pattern.matches("(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)", txtHost.getText())) {
                try {
                  InetAddress.getByName(txtHost.getText());
                }
                catch (UnknownHostException ex) {
                  if (JOptionPane.showOptionDialog(getParent(),
                    "Host " + txtHost.getText() + " cannot be found.\nDo you want to progress?",
                    "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                    new Object[]{UIManager.get("OptionPane.yesButtonText"),
                      UIManager.get("OptionPane.noButtonText")},
                    UIManager.get("OptionPane.noButtonText")) == JOptionPane.NO_OPTION) {

                    final JTextComponent textComponent = ((JTextComponent)event.getSource());
                    SwingUtilities.invokeLater(new Runnable() {
                      @Override
                      public void run() {
                        textComponent.selectAll();
                        textComponent.requestFocus();
                      }
                    });
                  }
                }
              }
            }
          }
        });
        
        txtHost.getDocument().addDocumentListener(new DocumentListener() {
          public void changedUpdate(DocumentEvent event) {
          }
          public void removeUpdate(DocumentEvent event) {
            btnTest.setEnabled(!txtHost.getText().trim().isEmpty());
            txtQParam.setEnabled(txtQParam.isEditable() && isLocalhost(txtHost.getText()));
          }
          public void insertUpdate(DocumentEvent event) {
            btnTest.setEnabled(!txtHost.getText().trim().isEmpty());
            txtQParam.setEnabled(txtQParam.isEditable() && isLocalhost(txtHost.getText()));
          }
        });
        
        label = new JLabel("Port");
        pnlConnection.add(label, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
            GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 20, 15, 0), 0, 0));
        spPort = new JSpinner(new SpinnerNumberModel(1, 1, 65535, 1));
        label.setLabelFor(spPort);
        pnlConnection.add(spPort, new GridBagConstraints(1, 2, 1, 1, 1.0, 1.0,
            GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 15, 0), 0, 0));
        ((JSpinner.DefaultEditor)spPort.getEditor()).getTextField().addFocusListener(new FocusListener() {
          public void focusGained(FocusEvent event) {
            if (event.getSource() instanceof JTextComponent) {
              final JTextComponent textComponent=((JTextComponent)event.getSource());
              SwingUtilities.invokeLater(new Runnable(){
                public void run() {
                  textComponent.selectAll();
                }
              });
            }
          }

          public void focusLost(FocusEvent event) {        
          }
        });

        label = new JLabel("Username");
        pnlConnection.add(label, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
            GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 20, 0, 0), 0, 0));
        txtUsername = new JTextField();
        label.setLabelFor(txtUsername);
        pnlConnection.add(txtUsername, new GridBagConstraints(1, 3, 1, 1, 1.0, 1.0,
            GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 0, 0), 150, 0));
        txtUsername.addFocusListener(new FocusListener() {
          public void focusGained(FocusEvent event) {
            txtUsername.selectAll();
          }
          public void focusLost(FocusEvent event) {
          }      
        });
        
        label = new JLabel("Password");
        pnlConnection.add(label, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0,
            GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 20, 0, 0), 0, 0));
        txtPassword = new JPasswordField();
        label.setLabelFor(txtPassword);
        pnlConnection.add(txtPassword, new GridBagConstraints(1, 4, 1, 1, 1.0, 1.0,
            GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 0, 0), 250, 0));
        txtPassword.addFocusListener(new FocusListener() {
          public void focusGained(FocusEvent event) {
            txtPassword.selectAll();
          }
          public void focusLost(FocusEvent event) {
          }      
        });
        
        label = new JLabel("Auth. method");
        pnlConnection.add(label, new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0,
            GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 20, 15, 0), 0, 0));
        cbAuthType = new JComboBox<String>();
        label.setLabelFor(cbAuthType);
        pnlConnection.add(cbAuthType, new GridBagConstraints(1, 5, 1, 1, 1.0, 1.0,
            GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 15, 0), 0, 0));
        
        label = new JLabel("Tab color");
        pnlConnection.add(label, new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0,
            GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 20, 15, 0), 0, 0));
        txtColor = new JTextField("  Sample text on foreground  ");
        txtColor.setEditable(false);
        label.setLabelFor(txtColor);
        pnlConnection.add(txtColor, new GridBagConstraints(1, 6, 1, 1, 1.0, 1.0,
            GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 15, 0), 0, 0));
        txtColor.addMouseListener(new MouseListener() {          
          @Override
          public void mouseReleased(MouseEvent event) {
          }
          @Override
          public void mousePressed(MouseEvent e) {
          }
          @Override
          public void mouseExited(MouseEvent e) {
          }
          @Override
          public void mouseEntered(MouseEvent e) {
          }
          @Override
          public void mouseClicked(MouseEvent event) {
            txtColorClicked(event);            
          }
        });
        
        label = new JLabel("Q parameters");
        pnlConnection.add(label, new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0,
            GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 20, 15, 0), 0, 0));
        txtQParam = new JTextField();
        label.setLabelFor(txtQParam);
        pnlConnection.add(txtQParam, new GridBagConstraints(1, 7, 1, 1, 1.0, 1.0,
            GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 15, 20), 0, 0));
      }
      catch (java.lang.Throwable ignored) {
      }
    }
    return pnlConnection;
  }
  
  private void btnAcceptClicked(ActionEvent event) {
    txtName.setText(txtName.getText().trim());
    txtHost.setText(txtHost.getText().trim());
    txtUsername.setText(txtUsername.getText().trim());
    txtPassword.setText(new String(txtPassword.getPassword()).trim());
    
    if (txtHost.getText().trim().length() == 0) {
      JOptionPane.showMessageDialog(getParent(), "The server's host name cannot be empty",
          "kdb+ Server", JOptionPane.ERROR_MESSAGE);
      txtHost.requestFocus();
      return;
    }
    
    if (txtUsername.getText().trim().length() == 0) {
      txtUsername.setText(System.getProperty("user.name"));
    }
    try {
      KdbService add = new KdbService(txtName.getText().trim(), txtHost.getText().trim(),
          ((Integer)spPort.getValue()).intValue(), txtUsername.getText().trim(), 
          String.valueOf(txtPassword.getPassword()).trim());
      add.setType((String)cbAuthType.getModel().getSelectedItem());
      add.setParams(txtQParam.getText());
      add.setTitleColor(color);
      
      switch(operation) {
        case ADD:
        case CLONE:
          try {
            AppConfig config = AppConfig.getConfig(); 
            if (config.getKdbService(add.getHost(), add.getPort(), add.getUsername()) == null) {
              if (tree.getModel() instanceof DefaultTreeModel) {
                TreePath path = null;
                try {
                  path = tree.getNextMatch(add.getHost(), 0, Bias.Forward);
                }
                catch(IllegalArgumentException ignored) {              
                }
                DefaultMutableTreeNode node = null;
                if (path != null) {
                  node = path.getLastPathComponent() instanceof DefaultMutableTreeNode ? 
                      (DefaultMutableTreeNode)path.getLastPathComponent() : null;
                  if (node != null) {
                    ((DefaultTreeModel)tree.getModel()).insertNodeInto(
                        new KdbServicesTreeNode(add),  node, node.getChildCount());
                  }
                }
                else {
                  if (((DefaultTreeModel)tree.getModel()).getRoot() instanceof DefaultMutableTreeNode) {
                    node = new HostTreeNode(add.getHost());
                    node.add(new KdbServicesTreeNode(add));                
                    ((DefaultTreeModel)tree.getModel()).insertNodeInto(node, 
                        (DefaultMutableTreeNode)((DefaultTreeModel)tree.getModel()).getRoot(), 0);                
                  }
                }
                ((DefaultTreeModel)tree.getModel()).reload();
                path = tree.getNextMatch(add.getHost(), 0, Bias.Forward);
                tree.expandPath(path);
                tree.setSelectionPath(path);
                
                config.setKdbService(add);
              }
            }
            else {
              if (JOptionPane.showOptionDialog(getParent(),
                  "Connection's information for server " + connection.getUsername() + "@"  +
                  connection.getHost() + ":" + connection.getPort() + " exists.\nDo you want to overwrite?",
                  "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                  new Object[]{UIManager.get("OptionPane.yesButtonText"),
                      UIManager.get("OptionPane.noButtonText")}, 
                      UIManager.get("OptionPane.noButtonText")) == JOptionPane.YES_OPTION) {
                config.setKdbService(add);
              }
            }        
          }
          catch (StudioException ex) {
            JOptionPane.showMessageDialog(getParent(), ex.getMessage(), "kdb+ Server", JOptionPane.ERROR_MESSAGE);
          }
          break;
        case EDIT:
          try {
            AppConfig config = AppConfig.getConfig();
            if (JOptionPane.showOptionDialog(getOwner(), "Connection's information for server\n" +
                connection.getUsername() + "@" + connection.getHost() + ":" + connection.getPort() +
                "\nchanged.\nDo you want to overwrite?", "Confirmation", JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, new Object[]{
                    UIManager.get("OptionPane.yesButtonText"),
                    UIManager.get("OptionPane.noButtonText")},
                UIManager.get("OptionPane.noButtonText")) == JOptionPane.YES_OPTION) {
              config.removeKdbService(connection);
              config.setKdbService(add);

              TreePath[] paths = tree.getSelectionPaths();
              TreePath path = paths != null && paths.length > 0 ? paths[0] : null;
              DefaultMutableTreeNode node = path.getLastPathComponent() instanceof DefaultMutableTreeNode
                  ? (DefaultMutableTreeNode)path.getLastPathComponent() : null;
              if (node.getUserObject() instanceof KdbService) {
                node.setUserObject(add);
                ((DefaultTreeModel)tree.getModel()).reload();
                tree.expandPath(path);
                tree.setSelectionPath(path);
              }
            }
        }
        catch (StudioException ex) {
          JOptionPane.showMessageDialog(getParent(), ex.getMessage(), "kdb+ Server", JOptionPane.ERROR_MESSAGE);
        }
        break;
      }
    }
    catch(StudioException ex) {
      JOptionPane.showMessageDialog(getParent(), ex.getMessage(), "kdb+ Server", JOptionPane.ERROR_MESSAGE);
      return;
    } 
    setVisible(false);
    
  }
  
  private void txtColorClicked(MouseEvent event) {
    if (event.getButton() == MouseEvent.BUTTON1 && event.getClickCount() == 2) {
      final JColorChooser chooser = new JColorChooser();
      color = txtColor.getForeground();
      chooser.setColor(color);

      JDialog dialog = JColorChooser.createDialog(getParent(), "Select title color for editor", true,
          chooser, new ActionListener() {
            public void actionPerformed(ActionEvent event) {
              color = chooser.getColor();
            }
          }, null);

      dialog.setVisible(true);

      txtColor.setForeground(color);      
    }
  }

  public static boolean isLocalhost(String hostname) {
    String host = null;
    try {
      host = InetAddress.getLocalHost().getHostName();
    }
    catch (UnknownHostException ignored) {
    }
    return hostname.equals("localhost") || hostname.equals("127.0.0.1") ||
      hostname.equals("0.0.0.0") || hostname.equals("::1") ||
      hostname.matches("^" + host + ".*");
  }
}
