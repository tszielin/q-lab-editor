package tszielin.qlab.component.console;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import tszielin.qlab.component.editor.QEditor;

public class Console extends JPanel {
  private static final long serialVersionUID = -1388176746346802453L;
  private JLabel execStatus;
  private JLabel infoStatus;
  private JLabel typeStatus;
  private JLabel queryStatus;
  private JPanel panelInfo;
  private QEditor editor;
  private Component component;

  private final SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss.SSS");

  protected Console(QEditor editor) {
    super(new BorderLayout());
    this.editor = editor;
    
    final JPanel statusBar = new JPanel(new BorderLayout(10, 0));
    execStatus = new JLabel();
    execStatus.setFont(new java.awt.Font("Dialog", 0, 10));
    execStatus.setBackground(new java.awt.Color(102, 102, 145));
    statusBar.add(execStatus, BorderLayout.EAST);
    
    infoStatus = new JLabel();
    infoStatus.setFont(new java.awt.Font("Dialog", 0, 10));
    infoStatus.setBackground(new java.awt.Color(102, 102, 145));
    statusBar.add(infoStatus, BorderLayout.WEST);
    
    panelInfo = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0)); 
    typeStatus = new JLabel();
    typeStatus.setFont(new java.awt.Font("Dialog", 0, 10));
    typeStatus.setBackground(new java.awt.Color(102, 102, 145));
    typeStatus.setForeground(Color.red);
    panelInfo.add(typeStatus, BorderLayout.WEST);       
    
    queryStatus = new JLabel();
    queryStatus.setFont(new java.awt.Font("Dialog", 0, 10));
    queryStatus.setBackground(new java.awt.Color(102, 102, 145));
    queryStatus.setForeground(Color.blue);
    panelInfo.add(queryStatus, BorderLayout.CENTER);
    queryStatus.addComponentListener(new ComponentListener() {
      public void componentShown(ComponentEvent event) {
      }
      public void componentResized(ComponentEvent event) {
        if (event.getSource() instanceof JLabel) {
          int width = statusBar.getWidth() - execStatus.getWidth() - infoStatus.getWidth() - typeStatus.getWidth() - 30;
          JLabel label = (JLabel)event.getSource();          
          String str = label.getText();
          if (str != null && !str.trim().isEmpty()) {
            int stringWidth = SwingUtilities.computeStringWidth(
                label.getFontMetrics(label.getFont()), str);
            if (stringWidth > width) {
              while (stringWidth > width) {
                str = str.substring(0, str.length() - 1);
                stringWidth = SwingUtilities.computeStringWidth(
                    label.getFontMetrics(label.getFont()), str + "...");
              }
              label.setText(str + "...");
            }
          }
        }
      }
      public void componentMoved(ComponentEvent event) {
      }
      public void componentHidden(ComponentEvent event) {
      }
    });    
    statusBar.add(panelInfo, BorderLayout.CENTER);    
    add(statusBar, BorderLayout.SOUTH);
  }

  public void setComponent(Component component) {
    if (this.component != null) {
      remove(this.component);
    }
    this.component = component;
    add(this.component, BorderLayout.CENTER);
    revalidate();
  }
  
  public void setComponent(Component component, String query, int type, long time) {
    setComponent(component);
    setTime(time);
    setType(type);
    setQuery(query);
  }
  
  public Component getComponent() {
    return this.component;
  }

  protected void setTime(long time) {
    execStatus.setText(time < 0 ? "" : "Execution time: " + formatter.format(new Date(time)) + "   ");
  }
  
  protected void setQuery(String query) {
    queryStatus.setText(query != null && !query.trim().isEmpty() ? "Query: " + query : null);
  }

  public boolean isAssigned(QEditor editor) {
    return this.editor == null ? false : this.editor.equals(editor);
  }

  public void clearEditor() {
    this.editor = null;
  }
  
  public void setStatus(String status) {
    infoStatus.setText(status);
  }
  
  protected void setType(int type) {
    typeStatus.setText("Type: " + type + "h");
  }
}