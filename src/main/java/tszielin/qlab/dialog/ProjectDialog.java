package tszielin.qlab.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Position.Bias;
import javax.swing.tree.TreePath;

import org.apache.commons.io.FilenameUtils;

import studio.ui.EscapeDialog;
import tszielin.qlab.component.tree.model.FileTreeModel;
import tszielin.qlab.config.AppInformation;
import tszielin.qlab.config.ProjectConfig;
import tszielin.qlab.config.data.Project;
import tszielin.qlab.error.ArgumentException;
import tszielin.qlab.error.StudioException;
import tszielin.qlab.util.error.ConfigException;


public class ProjectDialog extends EscapeDialog {
  private static final long serialVersionUID = 8978396443400490508L;
  private JPanel content;
  private JPanel pnlButtons;
  private JPanel pnlProject;
  private JTextField txtName;
  private JTextField txtPath;
  
  private JButton btnAccept;
  private JButton btnChooser;
  
  private JFileChooser chooser;
  private ProjectConfig config;
  
  private JTree tree;
  private DataOperation operation;
  private Project project;

  /**
   * This method initializes
   * @throws ArgumentException 
   * @throws ConfigException 
   */
  public ProjectDialog(Window window, JTree tree, Project project) throws ConfigException, ArgumentException {
    super(window, ModalityType.APPLICATION_MODAL);
    this.project = project;
    this.operation = this.project == null ? DataOperation.ADD : DataOperation.EDIT;
    setTitle(this.operation == DataOperation.ADD ? "New project" : "Rename project");
    initialize();
    config = ProjectConfig.getConfig();
    this.tree = tree;
    this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    this.validate();
   
    if (this.getRootPane() != null) {
      this.getRootPane().setDefaultButton(btnAccept);
    }
    if (this.project != null) {
      txtName.setText(project.getName());
      txtPath.setText(project.getPath() != null ? project.getPath().getPath() : null);
      txtPath.setEnabled(false);
    }
    else {
      txtPath.setEnabled(!txtName.getText().trim().isEmpty());
    }
    btnChooser.setEnabled(txtPath.isEnabled());
    btnAccept.setEnabled(txtPath.isEnabled());
  }
  
  /**
   * This method initializes this
   */
  private void initialize() {
    try {
      this.setSize(new Dimension(320, 160));
      this.setContentPane(getContent());      
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
        content.add(getButtons(), BorderLayout.SOUTH);
        content.add(getProjectInfo(), BorderLayout.CENTER);
      }
      catch (java.lang.Throwable ignored) {
      }
    }
    return content;
  }

  /**
   * This method initializes pnlButtons
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getButtons() {
    if (pnlButtons == null) {
      try {
        pnlButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnAccept = new JButton("Accept");
        btnAccept.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent event) {
            if (txtPath.getText().trim().isEmpty()) {
              JOptionPane.showMessageDialog(getOwner(), "Project path must be given", "Project", JOptionPane.INFORMATION_MESSAGE);
              txtPath.requestFocus();
              return;
            }
            else {
              File file = new File(txtPath.getText());
              if (!file.exists()) {
                JOptionPane.showMessageDialog(getOwner(), "Project path '" + txtPath.getText() + " not exists.", "Project", JOptionPane.INFORMATION_MESSAGE);
                txtPath.requestFocus();
                return;
              }
              if (!file.isDirectory()) {
                JOptionPane.showMessageDialog(getOwner(), "Project path '" + txtPath.getText() + " is not a directory.", "Project", JOptionPane.INFORMATION_MESSAGE);
                txtPath.requestFocus();
                return;
              }
            }
            switch (operation) {
              case ADD:
                try {
                  project = new Project(txtName.getText(),
                      FilenameUtils.separatorsToUnix(txtPath.getText()));
                  java.util.List<Project> projects = config.getProjects();
                  if (projects != null && !projects.isEmpty()) {
                    for (Project prj : projects) {
                      if (prj.getName().equals(project.getName())) {
                        JOptionPane.showMessageDialog(getOwner(), "Project '" + project.getName() +
                            "' already exists.\nDo you want to rename existing project?",
                            AppInformation.getInformation().getTitle(), JOptionPane.WARNING_MESSAGE);
                        return;
                      }
                      if (prj.getPath().getPath().equalsIgnoreCase(project.getPath().getPath())) {
                        JOptionPane.showMessageDialog(getOwner(), "Project on working directory '" +
                            project.getPath().getPath() + "' named '" + prj.getName() +
                            "' already exists.\nYou can rename the project name...",
                            AppInformation.getInformation().getTitle(), JOptionPane.WARNING_MESSAGE);
                        return;
                      }
                    }
                  }
                  config.setProject(project);
                  if (tree.getModel() instanceof FileTreeModel) {
                    ((FileTreeModel)tree.getModel()).addRoot(project);
                    TreePath path = tree.getNextMatch(project.getName(), 0, Bias.Forward);
                    if (path != null) {
                      tree.setSelectionPath(path);
                    }
                  }
                }
                catch (StudioException ex) {
                  JOptionPane.showMessageDialog(getOwner(), ex.getMessage(), AppInformation.getInformation().getTitle(),
                      JOptionPane.ERROR_MESSAGE);
                }
                break;
              case EDIT:                
                try {
                  if (tree.getModel() instanceof FileTreeModel) {
                    config.remove(project);
                    ((FileTreeModel)tree.getModel()).removeRoot(project);
                    project.setName(txtName.getText());
                    config.setProject(project);
                    ((FileTreeModel)tree.getModel()).addRoot(project);
                    TreePath path = tree.getNextMatch(project.getName(), 0, Bias.Forward);
                    if (path != null) {
                      tree.setSelectionPath(path);
                    }
                  }
                }
                catch (StudioException ex) {
                  JOptionPane.showMessageDialog(getOwner(), ex.getMessage(), AppInformation.getInformation().getTitle(),
                      JOptionPane.ERROR_MESSAGE);
                }
                break;
              default:
                break;
            }
            setVisible(false);
          }
        });
        pnlButtons.add(btnAccept);
        JButton button = new JButton("Cancel");
        button.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent event) {
            setVisible(false);
          }
        });
        pnlButtons.add(button);
      }
      catch (java.lang.Throwable ignored) {
      }
    }
    return pnlButtons;
  }

  private JPanel getProjectInfo() {
    if (pnlProject == null) {
      try {
        pnlProject = new JPanel(new GridBagLayout());
        JLabel label = new JLabel("Project name");
        pnlProject.add(label, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(20, 20, 5, 0), 0, 0));
        txtName = new JTextField();
        txtName.getDocument().addDocumentListener(new DocumentListener() {
          public void changedUpdate(DocumentEvent event) {
          }
          public void removeUpdate(DocumentEvent event) {
            btnAccept.setEnabled(project == null ? 
                !(txtName.getText().trim().isEmpty() || txtPath.getText().isEmpty()) :
                  !project.getName().equals(txtName.getText()) && !txtName.getText().trim().isEmpty());
            txtPath.setEnabled(project == null && !txtName.getText().trim().isEmpty());
            btnChooser.setEnabled(txtPath.isEnabled());
          }
          public void insertUpdate(DocumentEvent event) {
            btnAccept.setEnabled(project == null ? 
                !(txtName.getText().trim().isEmpty() || txtPath.getText().isEmpty()) :
                  !project.getName().equals(txtName.getText()) && !txtName.getText().trim().isEmpty());
            txtPath.setEnabled(project == null && !txtName.getText().trim().isEmpty());
            btnChooser.setEnabled(txtPath.isEnabled());
          }
        });
        txtName.addFocusListener(new FocusListener() {
          public void focusGained(FocusEvent event) {
            txtName.selectAll();
          }
          public void focusLost(FocusEvent event) {
          }      
        });
        label.setLabelFor(txtName);
        pnlProject.add(txtName, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
            GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(20, 5, 5, 0), 150, 0));
        label = new JLabel("Project path");
        pnlProject.add(label, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
            GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 20, 20, 0), 0, 0));
        txtPath = new JTextField();
        txtPath.addFocusListener(new FocusListener() {
          public void focusGained(FocusEvent event) {
            txtPath.selectAll();
          }
          public void focusLost(FocusEvent event) {
          }      
        });
        txtPath.getDocument().addDocumentListener(new DocumentListener() {
          public void changedUpdate(DocumentEvent event) {
          }
          public void removeUpdate(DocumentEvent event) {
            btnAccept.setEnabled(!(txtName.getText().trim().isEmpty() || txtPath.getText().isEmpty()));
          }
          public void insertUpdate(DocumentEvent event) {
            btnAccept.setEnabled(!(txtName.getText().trim().isEmpty() || txtPath.getText().isEmpty()));
          }
        });
        label.setLabelFor(txtPath);
        pnlProject.add(txtPath, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
            GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 20, 0), 150, 0));
        btnChooser = new JButton("...");
        btnChooser.setBorder(null);
        btnChooser.setSize(22, 22);
        btnChooser.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent event) {
            if (chooser == null) {
              chooser = new JFileChooser();
              chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
              if (config.getPath() != null && config.getPath().exists()) {
                chooser.setCurrentDirectory(config.getPath());
              }
            }            
            if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
              txtPath.setText(chooser.getSelectedFile().getPath());
            }
          }          
        });
        pnlProject.add(btnChooser, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0,
            GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 20, 20), 0, 0));        
      }
      catch (java.lang.Throwable ignored) {
      }
    }
    return pnlProject;
  }  
}