package tszielin.qlab.listener;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import studio.ui.QGrid;

import com.kx.KdbConnection;
import com.kx.kdb.K;

public class TableTreeSelectionListener implements TreeSelectionListener {
  private final JTree tableTree;
  private final KdbConnection server;
  private final JSplitPane splitPane;
  private final JLabel tableName;
  
  private JSpinner spinner;
  private JCheckBox checkBox;
  private JLabel label;
  
  public TableTreeSelectionListener(JTree tree, KdbConnection server, JLabel tableName, JSplitPane splitPane) {
    this.tableTree = tree;
    this.server = server;
    this.tableName = tableName;
    this.splitPane = splitPane;
    this.splitPane.setDividerSize(0);   
    
    if (splitPane.getRightComponent() instanceof JPanel) {
      for (Component component : ((JPanel)splitPane.getRightComponent()).getComponents()) {
        if (component instanceof JPanel) {
          ((JPanel)component).setVisible(false);
          for (Component comp : ((JPanel)component).getComponents()) {
            if (comp instanceof JSpinner) {
              spinner = (JSpinner)comp;
              spinner.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent event) {
                  getTableData();                  
                }});
            }
            if (comp instanceof JCheckBox) {
              checkBox = (JCheckBox)comp;
              checkBox.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                  getTableData();
                }});
            }
            if (comp instanceof JLabel) {
              label = (JLabel)comp;
            }
          }
        }
      }
    }
  }

  @Override
  public void valueChanged(TreeSelectionEvent  event) {
    DefaultMutableTreeNode node = tableTree.getLastSelectedPathComponent() instanceof DefaultMutableTreeNode ?
        (DefaultMutableTreeNode)tableTree.getLastSelectedPathComponent() : null;
    if (node != null && node.isLeaf() && node.getUserObject() instanceof String) {            
      String tableName = null;
      if (!(node.getParent().isLeaf())) {
        tableName = (String)((DefaultMutableTreeNode)node.getParent()).getUserObject();
        tableName = tableName.equals("<default>") ? "" : tableName + ".";
      }
      tableName += (String)node.getUserObject();
      this.tableName.setText("Table: " + tableName);
      this.splitPane.setDividerSize(9);
      try {
        server.write(new K.KCharacterArray("meta " + tableName));
        Object result = server.getResponse();
        if (result instanceof K.KDictionary) {
          splitPane.setLeftComponent(new QGrid((K.KDictionary)result));
          int width = (int)((QGrid)splitPane.getLeftComponent()).getTable().getPreferredSize().getWidth() + 42;
          if (((QGrid)splitPane.getLeftComponent()).getTable().getPreferredSize().getHeight() > splitPane.getPreferredSize().getHeight()) {
            width += 15;
          }
          splitPane.setDividerLocation(width);
        }
      }
      catch (Throwable ex) {
        JEditorPane editorPane = new JEditorPane();
        editorPane.setEditorKit(new HTMLEditorKit());
        editorPane.setText(
            "<html><body><b><font color=\"red\">Error:</font></b><p>The server sent the response: " + 
            ex.getMessage() + "</p></html></body>");
        splitPane.setLeftComponent(editorPane);
        splitPane.setDividerLocation((int)editorPane.getPreferredSize().getWidth() + 10);
      }

      getTableData();
    }
    else {
      this.tableName.setText(" ");
      spinner.getParent().setVisible(false);
      if (splitPane.getLeftComponent() != null) {
        splitPane.getLeftComponent().setVisible(false);
      }
      if (splitPane.getRightComponent() != null) {
        splitPane.getRightComponent().setVisible(false);
      }
      splitPane.setDividerSize(0);
    }
    splitPane.validate();
  }
  
  private void getTableData() {
    if (tableTree.getLastSelectedPathComponent() instanceof DefaultMutableTreeNode &&
        ((DefaultMutableTreeNode)tableTree.getLastSelectedPathComponent()).isLeaf() &&
        ((DefaultMutableTreeNode)tableTree.getLastSelectedPathComponent()).getUserObject() instanceof String) {
      int count = spinner == null ? 1000 : 
        spinner.getValue() instanceof Integer ? ((Integer)spinner.getValue()).intValue() : 1000;
      String tableName = null;
      if (!((TreeNode)tableTree.getLastSelectedPathComponent()).getParent().isLeaf()) {
        tableName = (String)((DefaultMutableTreeNode)((TreeNode)tableTree.getLastSelectedPathComponent()).getParent()).getUserObject();
        tableName = tableName.equals("<default>") ? "" : tableName + ".";
      }
      tableName += (String)((DefaultMutableTreeNode)tableTree.getLastSelectedPathComponent()).getUserObject();
      try {
        server.write(new K.KCharacterArray("count " + tableName));
        Object result = server.getResponse();
        int records = 1000;
        if (result instanceof K.KInteger) {
          records = ((K.KInteger)result).getValue();
        }
        count = records > count ? count : records;
        server.write(new K.KCharacterArray(
            (checkBox == null ? count : !checkBox.isSelected() ? count : "(neg " + count + ")") + 
            "#select from " + tableName));
        result = server.getResponse();
        if (result instanceof K.KBase) {
          spinner.getParent().setVisible(true);
          if (splitPane.getRightComponent() instanceof JPanel) {
            for (Component component : ((JPanel)splitPane.getRightComponent()).getComponents()) {
              if (component instanceof QGrid) {
                ((JPanel)splitPane.getRightComponent()).remove(component);
              }
            }
            ((JPanel)splitPane.getRightComponent()).add(new QGrid((K.KType<?>)result), BorderLayout.CENTER);
            splitPane.getRightComponent().validate();
            label.setText("Records: " + count + "/" + records);
          }
          if (splitPane.getRightComponent() != null) {
            splitPane.getRightComponent().setVisible(true);
          }
        }
        spinner.getParent().setVisible(true);
        if (splitPane.getLeftComponent() != null) {
          splitPane.getLeftComponent().setVisible(true);
        }        
        splitPane.setDividerSize(9);
      }
      catch (Throwable ex) {
        JEditorPane editorPane = new JEditorPane();
        editorPane.setEditorKit(new HTMLEditorKit());
        editorPane.setText(
            "<html><body><b><font color=\"red\">Error:</font></b><p>The server sent the response: " + 
            ex.getMessage() + "</p></html></body>");
        splitPane.setLeftComponent(editorPane);
        splitPane.setDividerLocation((int)editorPane.getPreferredSize().getWidth() + 10);
      }
    }
    else {
      this.tableName.setText(null);
      spinner.getParent().setVisible(false);
      if (splitPane.getLeftComponent() != null) {
        splitPane.getLeftComponent().setVisible(false);
      }
      if (splitPane.getRightComponent() != null) {
        splitPane.getRightComponent().setVisible(false);
      }
      splitPane.setDividerSize(0);
    }
  }
}
