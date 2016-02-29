package tszielin.qlab.component.pane;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Hashtable;

import javax.swing.*;
import javax.swing.text.Position;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.tree.*;

import com.kx.KdbConnection;
import com.kx.kdb.K;

import tszielin.qlab.config.data.KdbService;
import tszielin.qlab.listener.*;
import tszielin.qlab.util.image.IconsItem;

public class KdbEnvironment extends JTabbedPane {
  private static final long serialVersionUID = 4041223893870026992L;

  private KdbConnection server;
  private JSplitPane panelSplitPane;
  private JSplitPane tableSplitPane;
  private JTree tableTree;
  private JPanel infoPanel;

  private JLabel lbAllocated;
  private JLabel lbHeapAvail;
  private JLabel lbHeapSize;
  private JLabel lbMax;
  private JLabel lbMapped;
  
  private JTree functionTree;
  private JEditorPane functionText;
  private JTree variableTree;
  private JEditorPane variableText;

  public KdbEnvironment(KdbService connection) throws IOException {
    super();
    if (connection != null) {
      server = new KdbConnection(connection.getHost(), connection.getPort(), connection.getCredentials());
      try {
        server.reconnect(true);
      }
      catch (Exception ex) {
        throw new IOException(ex);
      }
    }
    initialize();
  }

  private void initialize() {
    this.setSize(new Dimension(561, 383));
    JPanel panel = new JPanel(new BorderLayout());
    panelSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true);
    panelSplitPane.setDividerSize(9);
    panelSplitPane.setOneTouchExpandable(true);
    JLabel label = new JLabel();
    label.setHorizontalAlignment(SwingConstants.CENTER);
    JPanel tableView = new JPanel(new BorderLayout());
    tableView.add(label, BorderLayout.NORTH);
    tableView.add(panelSplitPane, BorderLayout.CENTER);
    panel.add(tableView, BorderLayout.CENTER);
    this.addTab("Tables", IconsItem.ICON_TABLE, panel);
    
    panel = new JPanel(new BorderLayout()); 
    infoPanel = new JPanel(new GridBagLayout());
    panel.add(infoPanel, BorderLayout.WEST);
    this.addTab("Environment", IconsItem.ICON_CONFIG, panel);
    
    final JSplitPane sp = new JSplitPane();
    sp.setDividerSize(0);
    panel.add(sp, BorderLayout.CENTER);
    
    JSplitPane dataPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
    
    variableTree = new JTree() {
      private static final long serialVersionUID = 8652442257243386308L;

      @Override
      public String getToolTipText(MouseEvent evt) {
        if (getRowForLocation(evt.getX(), evt.getY()) == -1)
          return null;
        
        TreePath curPath = getPathForLocation(evt.getX(), evt.getY());
        DefaultMutableTreeNode node = curPath.getLastPathComponent() instanceof DefaultMutableTreeNode ?
            (DefaultMutableTreeNode)curPath.getLastPathComponent() : null;
        if (node != null && node.isLeaf() && node.getUserObject() instanceof String) {            
          String command = null;
          if (!(node.getParent().isLeaf())) {
            command = (String)((DefaultMutableTreeNode)node.getParent()).getUserObject();
            command = command.equals("<default>") ? "" : command + ".";
          }
          command += (String)node.getUserObject();
          return "<html><body><small>Variable:</small> " + command + "</html></body>";
        }
        return null;
      }
    };
    variableTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
    renderer.setLeafIcon(IconsItem.ICON_BRACKETS);
    renderer.setOpenIcon(IconsItem.ICON_NAMESPACE);
    renderer.setClosedIcon(IconsItem.ICON_NAMESPACE);
    variableTree.setCellRenderer(renderer);
    ToolTipManager.sharedInstance().registerComponent(variableTree);
    dataPanel.setTopComponent(new JScrollPane(variableTree));
    dataPanel.setBottomComponent(new JScrollPane(variableText = new JEditorPane()));
    variableText.setEditorKit(new HTMLEditorKit());
    variableText.setEditable(false);
    dataPanel.setDividerSize(9);    
    sp.setLeftComponent(dataPanel);
    dataPanel.setDividerLocation(.8d);
    
    dataPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
    functionTree = new JTree() {
      private static final long serialVersionUID = 2342693197793789967L;

      @Override
      public String getToolTipText(MouseEvent evt) {
        if (getRowForLocation(evt.getX(), evt.getY()) == -1)
          return null;
        
        TreePath curPath = getPathForLocation(evt.getX(), evt.getY());
        DefaultMutableTreeNode node = curPath.getLastPathComponent() instanceof DefaultMutableTreeNode ?
            (DefaultMutableTreeNode)curPath.getLastPathComponent() : null;
        if (node != null && node.isLeaf() && node.getUserObject() instanceof String) {            
          String command = null;
          if (!(node.getParent().isLeaf())) {
            command = (String)((DefaultMutableTreeNode)node.getParent()).getUserObject();
            command = command.equals("<default>") ? "" : command + ".";
          }
          command += (String)node.getUserObject();
          return "<html><body><small>Function:</small> " + command + "</html></body>";
        }
        return null;
      }
    };
    renderer = new DefaultTreeCellRenderer();
    renderer.setLeafIcon(IconsItem.ICON_FUNCTION);
    renderer.setOpenIcon(IconsItem.ICON_NAMESPACE);
    renderer.setClosedIcon(IconsItem.ICON_NAMESPACE);
    functionTree.setCellRenderer(renderer);
    functionTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    ToolTipManager.sharedInstance().registerComponent(functionTree);
    dataPanel.setTopComponent(new JScrollPane(functionTree));
    dataPanel.setBottomComponent(new JScrollPane(functionText = new JEditorPane()));
    dataPanel.setDividerSize(9);        
    functionText.setEditorKit(new HTMLEditorKit());
    functionText.setEditable(false);    
    dataPanel.setDividerLocation(.8d);
    sp.setRightComponent(dataPanel);
    
    panel.addComponentListener(new ComponentListener() {
      @Override
      public void componentHidden(ComponentEvent event) {
      }

      @Override
      public void componentMoved(ComponentEvent event) {
      }

      @Override
      public void componentResized(ComponentEvent event) {
        if (event.getID() == ComponentEvent.COMPONENT_RESIZED) {
          sp.setDividerLocation(.5d);
        }
      }

      @Override
      public void componentShown(ComponentEvent event) {
      }
    });


    tableTree = new JTree() {
      private static final long serialVersionUID = 314498659506179330L;

      @Override
      public String getToolTipText(MouseEvent evt) {
        if (getRowForLocation(evt.getX(), evt.getY()) == -1)
          return null;
        
        TreePath curPath = getPathForLocation(evt.getX(), evt.getY());
        DefaultMutableTreeNode node = curPath.getLastPathComponent() instanceof DefaultMutableTreeNode ?
            (DefaultMutableTreeNode)curPath.getLastPathComponent() : null;
        if (node != null && node.isLeaf() && node.getUserObject() instanceof String) {            
          String command = null;
          if (!(node.getParent().isLeaf())) {
            command = (String)((DefaultMutableTreeNode)node.getParent()).getUserObject();
            command = command.equals("<default>") ? "" : command + ".";
          }
          command += (String)node.getUserObject();
          return "<html><body><small>Table:</small> " + command + "</html></body>";
        }
        return null;
      }
    };
    ToolTipManager.sharedInstance().registerComponent(tableTree);
    tableTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    tableTree.setRootVisible(false);
    renderer = new DefaultTreeCellRenderer();
    renderer.setLeafIcon(IconsItem.ICON_TABLE);
    renderer.setOpenIcon(IconsItem.ICON_NAMESPACE);
    renderer.setClosedIcon(IconsItem.ICON_NAMESPACE);
    tableTree.setCellRenderer(renderer);
    panelSplitPane.setLeftComponent(new JScrollPane(tableTree));    
    tableSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true);
    tableSplitPane.setDividerSize(9);
    tableSplitPane.setOneTouchExpandable(true);
    panelSplitPane.setRightComponent(tableSplitPane);
    
    panel = new JPanel(new BorderLayout());
    JPanel pnlNavig = new JPanel(new FlowLayout(FlowLayout.CENTER, 50, 10));
    pnlNavig.add(new JCheckBox("Last records"));
    pnlNavig.add(new JSpinner(new SpinnerNumberModel(1000, 1, 1000000, 10)));
    pnlNavig.add(new JLabel());
    panel.add(pnlNavig, BorderLayout.NORTH);
    tableSplitPane.setRightComponent(panel);
    
    if (server != null) {
      try {
        server.write(new K.KCharacterArray(
            "{(n;value''[(\"\\\\\",/:x),\\:/:n:\" \",\" .\",/:string key`])}[enlist\"a\"]"));
        Object result = server.getResponse();
        if (result instanceof K.KList && ((K.KList)result).getLength() == 2) {
          Hashtable<String, String[]> tables = getResult(result);
          if (tables != null && !tables.isEmpty()) {
            DefaultMutableTreeNode root = new DefaultMutableTreeNode("Tables");
            JTree.DynamicUtilTreeNode.createChildren(root, tables);
            tableTree.setModel(new DefaultTreeModel(root));
            expandAll(tableTree);
            tableTree.addTreeSelectionListener(new TableTreeSelectionListener(tableTree, server, label, tableSplitPane));
            
            TreePath path = tableTree.getNextMatch("<default>", 0, Position.Bias.Forward);
            if (path != null && path.getLastPathComponent() instanceof TreeNode &&
              ((TreeNode)path.getLastPathComponent()).getChildCount() > 0) {
              path = tableTree.getNextMatch(((TreeNode)path.getLastPathComponent()).getChildAt(0).toString(), 
                  tableTree.getRowForPath(path), Position.Bias.Forward);
              tableTree.setSelectionPath(path);
              tableTree.scrollPathToVisible(path);
            }
          }          
        }
        tableTree.setVisible(tableTree.getModel() instanceof DefaultTreeModel);
      }
      catch (Throwable cause) {
      } 
      try {
        server.write(new K.KCharacterArray(
            "{(n;value''[(\"\\\\\",/:x),\\:/:n:\" \",\" .\",/:string key`])}[enlist\"f\"]"));
        Object result = server.getResponse();
        if (result instanceof K.KList && ((K.KList)result).getLength() == 2) {
          Hashtable<String, String[]> tables = getResult(result);
          if (tables != null && !tables.isEmpty()) {
            DefaultMutableTreeNode root = new DefaultMutableTreeNode("Functions");
            JTree.DynamicUtilTreeNode.createChildren(root, tables);
            functionTree.setModel(new DefaultTreeModel(root));
            expandAll(functionTree);
            functionTree.addTreeSelectionListener(new FunctionTreeSelectionListener(
                functionTree, server, functionText));
            TreePath path = this.functionTree.getNextMatch("<default>", 0, Position.Bias.Forward);
            if (path != null && path.getLastPathComponent() instanceof TreeNode && 
                ((TreeNode)path.getLastPathComponent()).getChildCount() > 0) {
              path = functionTree.getNextMatch(((TreeNode)path.getLastPathComponent()).getChildAt(0).toString(), 
                  functionTree.getRowForPath(path), Position.Bias.Forward);
              functionTree.setSelectionPath(path);
              functionTree.scrollPathToVisible(path);
            }
          }
        }
        functionTree.setVisible(functionTree.getModel() instanceof DefaultTreeModel);
      }
      catch (Throwable cause) {
      }
      try {
        server.write(new K.KCharacterArray(
            "{(n;value''[(\"\\\\\",/:x),\\:/:n:\" \",\" .\",/:string key`])}[enlist\"v\"]"));
        Object result = server.getResponse();
        if (result instanceof K.KList && ((K.KList)result).getLength() == 2) {
          Hashtable<String, String[]> tables = getResult(result);
          if (tables != null && !tables.isEmpty()) {
            DefaultMutableTreeNode root = new DefaultMutableTreeNode("Variables");
            JTree.DynamicUtilTreeNode.createChildren(root, tables);
            variableTree.setModel(new DefaultTreeModel(root));
            expandAll(variableTree);
            variableTree.addTreeSelectionListener(new VariableTreeSelectionListener(
                variableTree, server, variableText));
           
            TreePath path = this.variableTree.getNextMatch("<default>", 0, Position.Bias.Forward);            
            if (path != null && path.getLastPathComponent() instanceof TreeNode && 
                ((TreeNode)path.getLastPathComponent()).getChildCount() > 0) {
              path = this.variableTree.getNextMatch(((TreeNode)path.getLastPathComponent()).getChildAt(0).toString(), variableTree.getRowForPath(path), Position.Bias.Forward);
              variableTree.setSelectionPath(path);
              variableTree.scrollPathToVisible(path);
            }
          }
        }
      }
      catch (Throwable cause) {         
      }
      variableTree.setVisible(variableTree.getModel() instanceof DefaultTreeModel);
    }

    int gridY = 0;
    try {
      server.write(new K.KCharacterArray(".z.h"));
      Object result = server.getResponse();
      if (result instanceof K.KSymbol) {
        String host = ((K.KSymbol)result).getValue();
        server.write(new K.KCharacterArray("\\p"));
        result = server.getResponse();
        if (result instanceof K.KInteger) {
          int port = ((K.KInteger)result).getValue();
          server.write(new K.KCharacterArray(".z.o"));
          result = server.getResponse();
          if (result instanceof K.KSymbol) {
            label = new JLabel("kdb+ server");
            label.setForeground(Color.GRAY);
            infoPanel.add(label, new GridBagConstraints(0, gridY, 1, 1, 0.0, 0.0,
                GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(20, 20, 5, 5), 0, 0));
            label = new JLabel(host + ":" + port + " (" + ((K.KSymbol)result).getValue() + ")");
            infoPanel.add(label, new GridBagConstraints(1, gridY, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(20, 5, 5, 20), 0, 0));
            gridY++;
          }
        }
      }
    }
    catch (Throwable cause) {
    }

    try {
      server.write(new K.KCharacterArray(".z.K"));
      Object result = server.getResponse();
      if (result instanceof K.KFloat) {
        double version = ((K.KFloat)result).getValue();
        server.write(new K.KCharacterArray(".z.k"));
        result = server.getResponse();
        if (result instanceof K.KDate) {
          label = new JLabel("version");
          label.setForeground(Color.GRAY);
          infoPanel.add(label, new GridBagConstraints(0, gridY, 1, 1, 0.0, 0.0,
              GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 20, 5, 5), 0, 0));
          label = new JLabel(version + " (" + (K.KDate)result + ")");
          infoPanel.add(label, new GridBagConstraints(1, gridY, 1, 1, 0.0, 0.0,
              GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 5, 20), 0, 0));
          gridY++;
        }
      }
    }
    catch (Throwable cause) {
    }

    try {
      server.write(new K.KCharacterArray(".z.i"));
      Object result = server.getResponse();
      if (result instanceof K.KInteger) {
        label = new JLabel("Process ID");
        label.setForeground(Color.GRAY);
        infoPanel.add(label, new GridBagConstraints(0, gridY, 1, 1, 0.0, 0.0,
            GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 20, 5, 5), 0, 0));
        label = new JLabel(Integer.toString(((K.KInteger)result).getValue()));
        infoPanel.add(label, new GridBagConstraints(1, gridY, 1, 1, 0.0, 0.0,
            GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 5, 20), 0, 0));
        gridY++;
      }
    }
    catch (Throwable cause) {
    }

    try {
      server.write(new K.KCharacterArray(".Q.host .z.a"));
      Object result = server.getResponse();
      if (result instanceof K.KSymbol) {
        label = new JLabel("Connected from");
        label.setForeground(Color.GRAY);
        infoPanel.add(label, new GridBagConstraints(0, gridY, 1, 1, 0.0, 0.0,
            GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 20, 5, 5), 0, 0));
        label = new JLabel((((K.KSymbol)result).getValue()));
        infoPanel.add(label, new GridBagConstraints(1, gridY, 1, 1, 0.0, 0.0,
            GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 5, 20), 0, 0));
        gridY++;
      }
    }
    catch (Throwable cause) {
    }

    try {
      server.write(new K.KCharacterArray(".z.l"));
      Object result = server.getResponse();
      if (result instanceof K.KList && ((K.KList)result).getLength() > 0) {
        label = new JLabel("License");
        label.setForeground(Color.DARK_GRAY);
        infoPanel.add(label, new GridBagConstraints(0, gridY, 1, 1, 0.0, 0.0,
            GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(30, 20, 5, 5), 0, 0));
        infoPanel.add(new JLabel(), new GridBagConstraints(1, gridY, 1, 1, 0.0, 0.0,
            GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(30, 5, 5, 20), 0, 0));
        gridY++;

        if (((K.KList)result).get(0) instanceof K.KCharacterArray) {
          label = new JLabel("Max cores");
          label.setForeground(Color.GRAY);
          infoPanel.add(label, new GridBagConstraints(0, gridY, 1, 1, 0.0, 0.0,
              GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 20, 5, 5), 0, 0));
          label = new JLabel(((K.KCharacterArray)((K.KList)result).get(0)).toString(false).replaceAll("enlist ", ""));
          infoPanel.add(label, new GridBagConstraints(1, gridY, 1, 1, 0.0, 0.0,
              GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 5, 20), 0, 0));
          gridY++;
        }
        if (((K.KList)result).get(1) instanceof K.KCharacterArray) {
          label = new JLabel("Expiry date");
          label.setForeground(Color.GRAY);
          infoPanel.add(label, new GridBagConstraints(0, gridY, 1, 1, 0.0, 0.0,
              GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 20, 5, 5), 0, 0));
          label = new JLabel(((K.KCharacterArray)((K.KList)result).get(1)).toString(false).toString().replaceAll("enlist ", ""));
          infoPanel.add(label, new GridBagConstraints(1, gridY, 1, 1, 0.0, 0.0,
              GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 5, 20), 0, 0));
          gridY++;
        }
        if (((K.KList)result).get(2) instanceof K.KCharacterArray) {
          label = new JLabel("Update date");
          label.setForeground(Color.GRAY);
          infoPanel.add(label, new GridBagConstraints(0, gridY, 1, 1, 0.0, 0.0,
              GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 20, 5, 5), 0, 0));
          label = new JLabel(((K.KCharacterArray)((K.KList)result).get(2)).toString(false).replaceAll("enlist ", ""));
          infoPanel.add(label, new GridBagConstraints(1, gridY, 1, 1, 0.0, 0.0,
              GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 5, 20), 0, 0));
          gridY++;
        }
        if (((K.KList)result).get(7) instanceof K.KCharacterArray) {
          label = new JLabel("For");
          label.setForeground(Color.GRAY);
          infoPanel.add(label, new GridBagConstraints(0, gridY, 1, 1, 0.0, 0.0,
              GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 20, 5, 5), 0, 0));
          label = new JLabel(((K.KCharacterArray)((K.KList)result).get(7)).toString(false).replaceAll("enlist ", ""));
          infoPanel.add(label, new GridBagConstraints(1, gridY, 1, 1, 0.0, 0.0,
              GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 5, 20), 0, 0));
          gridY++;
        }
      }
    }
    catch (Throwable cause) {
    }

    try {
      server.write(new K.KCharacterArray("\\w"));
      Object result = server.getResponse();
      if (result instanceof K.KLongArray && ((K.KLongArray)result).getLength() > 0) {
        label = new JLabel("Memory");
        label.setForeground(Color.DARK_GRAY);
        infoPanel.add(label, new GridBagConstraints(0, gridY, 1, 1, 0.0, 0.0,
            GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(30, 20, 5, 5), 0, 0));
        infoPanel.add(new JLabel(), new GridBagConstraints(1, gridY, 1, 1, 0.0, 0.0,
            GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(30, 5, 5, 20), 100, 0));
        gridY++;

        if (((K.KLongArray)result).get(0) instanceof K.KLong) {
          label = new JLabel("Allocated");
          label.setForeground(Color.GRAY);
          infoPanel.add(label, new GridBagConstraints(0, gridY, 1, 1, 0.0, 0.0,
              GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 20, 5, 5), 0, 0));
          lbAllocated = new JLabel(new DecimalFormat("###,###,###,##0").format(
              ((K.KLong)((K.KLongArray)result).get(0)).getValue() / 1024) + " KB");
          infoPanel.add(lbAllocated, new GridBagConstraints(1, gridY, 1, 1, 0.0, 0.0,
              GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 5, 5, 20), 0, 0));
          gridY++;
        }
        if (((K.KLongArray)result).get(1) instanceof K.KLong) {
          label = new JLabel("Available on heap");
          label.setForeground(Color.GRAY);
          infoPanel.add(label, new GridBagConstraints(0, gridY, 1, 1, 0.0, 0.0,
              GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 20, 5, 5), 0, 0));
          lbHeapAvail = new JLabel(new DecimalFormat("###,###,###,##0").format(
              ((K.KLong)((K.KLongArray)result).get(1)).getValue() / 1024) + " KB");
          infoPanel.add(lbHeapAvail, new GridBagConstraints(1, gridY, 1, 1, 0.0, 0.0,
              GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 5, 5, 20), 0, 0));
          gridY++;
        }
        if (((K.KLongArray)result).get(2) instanceof K.KLong) {
          label = new JLabel("Max heap size");
          label.setForeground(Color.GRAY);
          infoPanel.add(label, new GridBagConstraints(0, gridY, 1, 1, 0.0, 0.0,
              GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 20, 5, 5), 0, 0));
          lbHeapSize = new JLabel(new DecimalFormat("###,###,###,##0").format(
              ((K.KLong)((K.KLongArray)result).get(2)).getValue() / 1024) + " KB");
          infoPanel.add(lbHeapSize, new GridBagConstraints(1, gridY, 1, 1, 0.0, 0.0,
              GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 5, 5, 20), 0, 0));
          gridY++;
        }
        if (((K.KLongArray)result).get(3) instanceof K.KLong) {
          label = new JLabel("Max available");
          label.setForeground(Color.GRAY);
          infoPanel.add(label, new GridBagConstraints(0, gridY, 1, 1, 0.0, 0.0,
              GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 20, 5, 5), 0, 0));
          lbMax = new JLabel(
              new DecimalFormat("###,###,###,##0").format(
                  ((K.KLong)((K.KLongArray)result).get(3)).getValue() / 1024) + " KB");
          infoPanel.add(lbMax, new GridBagConstraints(1, gridY, 1, 1, 0.0, 0.0,
              GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 5, 5, 20), 0, 0));
          gridY++;
        }
        if (((K.KLongArray)result).get(4) instanceof K.KLong) {
          label = new JLabel("Mapped");
          label.setForeground(Color.GRAY);
          infoPanel.add(label, new GridBagConstraints(0, gridY, 1, 1, 0.0, 0.0,
              GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 20, 5, 5), 0, 0));
          lbMapped = new JLabel(
              new DecimalFormat("###,###,###,##0").format(
                  ((K.KLong)((K.KLongArray)result).get(4)).getValue() / 1024) + " KB");
          infoPanel.add(lbMapped, new GridBagConstraints(1, gridY, 1, 1, 0.0, 0.0,
              GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 5, 5, 20), 0, 0));
          gridY++;
        }

        infoPanel.add(new JLabel(), new GridBagConstraints(0, gridY, 1, 1, 0.0, 0.0,
            GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(30, 20, 5, 5), 0, 0));
        JButton button = new JButton("Refresh");
        button.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent event) {
            try {
              server.write(new K.KCharacterArray("\\w"));
              Object result = server.getResponse();
              if (result instanceof K.KLongArray) {
                if (((K.KLongArray)result).get(0) instanceof K.KLong) {
                  lbAllocated.setText(new DecimalFormat("###,###,###,##0").format(
                      ((K.KLong)((K.KLongArray)result).get(0)).getValue() / 1024) + " KB");
                }
                if (((K.KLongArray)result).get(1) instanceof K.KLong) {
                  lbHeapAvail.setText(new DecimalFormat("###,###,###,##0").format(
                      ((K.KLong)((K.KLongArray)result).get(1)).getValue() / 1024) + " KB");
                }
                if (((K.KLongArray)result).get(2) instanceof K.KLong) {
                  lbHeapSize.setText(new DecimalFormat("###,###,###,##0").format(
                      ((K.KLong)((K.KLongArray)result).get(2)).getValue() / 1024) + " KB");
                }
                if (((K.KLongArray)result).get(3) instanceof K.KLong) {
                  lbMax.setText(new DecimalFormat("###,###,###,##0").format(
                      ((K.KLong)((K.KLongArray)result).get(3)).getValue() / 1024) + " KB");
                }
                if (((K.KLongArray)result).get(4) instanceof K.KLong) {
                  lbMapped.setText(new DecimalFormat("###,###,###,##0").format(
                      ((K.KLong)((K.KLongArray)result).get(4)).getValue() / 1024) + " KB");
                }
              }
            }
            catch (Throwable cause) {
            }
          }
        });
        infoPanel.add(button, new GridBagConstraints(1, gridY, 1, 1, 0.0, 0.0,
            GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(30, 5, 5, 20), 0, 0));
      }
    }
    catch (Throwable cause) {
    } 
    
    panelSplitPane.setDividerLocation((int)tableTree.getPreferredSize().getWidth() + 20);
  }

  private Hashtable<String, String[]> getResult(Object result) {
    Hashtable<String, String[]> map = null;
    if (result instanceof K.KList && ((K.KList)result).getLength() == 2) {
      Object keys = ((K.KList)((K.KList)result).get(0)).getArray();
      Object values = ((K.KList)((K.KList)result).get(1)).getArray();
      if (keys instanceof K.KArray[] && values instanceof K.KBase[]) {
        for (int count = 0; count < ((K.KBase[])keys).length; count++) {
          if (((K.KArray[])values)[count] instanceof K.KList &&
              ((K.KList)((K.KArray[])values)[count]).getLength() > 0) {
            if (map == null) {
              map = new Hashtable<String, String[]>();
            }
            String key = ((K.KType<?>[])keys)[count].toString(false);
            if (key.trim().isEmpty()) {
              key = "<default>";
            }
            if (key != null && ((K.KSymbolArray)((K.KList)((K.KArray[])values)[count]).get(0)).getLength() > 0 &&
                ((K.KSymbolArray)((K.KList)((K.KArray[])values)[count]).get(0)).getArray() instanceof String[]) {
              map.put(key, (String[])((K.KSymbolArray)((K.KList)((K.KArray[])values)[count]).get(0)).getArray());
            }
          }
        }
      }
    }
    return map;
  }
  
  private void expandAll(JTree tree) {
    int row = 0;
    while (row < tree.getRowCount()) {
      tree.expandRow(row);
      row++;
    }
  }
  
  public void removeAll() {
    super.removeAll();
    if (server != null) {
      server.close();
      server = null;
    }
  }
}