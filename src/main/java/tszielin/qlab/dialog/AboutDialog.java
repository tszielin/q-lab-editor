package tszielin.qlab.dialog;

import java.awt.*;
import java.awt.event.*;
import java.net.URI;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLEditorKit;

import studio.ui.EscapeDialog;
import tszielin.qlab.component.pane.EditorsTabbedPane;
import tszielin.qlab.config.AppInformation;

public class AboutDialog extends EscapeDialog implements ActionListener {
  private static final long serialVersionUID = -6370801595849649942L;
  private JPanel contentPane;
  private JPanel pnlVersion;
  private JPanel pnlInfo;
  private JPanel pnlDialog;
  private JPanel pnlTitle;
  private JLabel lbTitle;
  private JLabel lbVersion;
  private JEditorPane txtThanks;
  private JScrollPane scrollPane;
  
  private final String E_MAIL = "tszielin@gmail.com";
  
  private Timer timer;
  private int value;
  private Color color;
  
  public AboutDialog(EditorsTabbedPane tabEditors) {
    super(SwingUtilities.windowForComponent(tabEditors), "About..", ModalityType.APPLICATION_MODAL);
    setUndecorated(true);
    color = tabEditors == null ? Color.WHITE :
      tabEditors.getEditor() != null ? tabEditors.getEditor().getBackground() : tabEditors.getBackground();
    initialize();        
  }

  /**
   * This method initializes this
   * 
   * @return void
   */
  private void initialize() {
    this.setSize(500, 270);
    this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    this.setContentPane(getContent());
//    validate();
    
    this.addMouseListener(new MouseListener() {      
      public void mouseReleased(MouseEvent event) {
      }      
      public void mousePressed(MouseEvent event) {
      }
      public void mouseExited(MouseEvent event) {
      }
      public void mouseEntered(MouseEvent event) {
      }
      public void mouseClicked(MouseEvent event) {
        if (event.getButton() == MouseEvent.BUTTON1) {
          setVisible(false);
        }        
      }
    });
  }

  private JPanel getContent() {
    if (contentPane == null) {
      contentPane = new JPanel(new BorderLayout());
      contentPane.add(getPanelInfo(), BorderLayout.CENTER);
    }
    return contentPane;
  }

  private JPanel getPanelInfo() {
    if (pnlDialog == null) {
      try {        
        pnlDialog = new JPanel(new BorderLayout());
        pnlDialog.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        pnlDialog.setBackground(color);
        pnlInfo = new JPanel(new BorderLayout());
        pnlInfo.setBackground(color);
        pnlVersion = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        pnlVersion.setBackground(Color.black);
        pnlTitle = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        pnlTitle.setBackground(color);
        
        pnlDialog.add(pnlTitle, BorderLayout.NORTH);
        lbTitle = new JLabel("<html><font size=8 color=\"#0000ff\">" + AppInformation.getInformation().getTitle() + "</font></html>");
        pnlTitle.add(lbTitle);
        
        pnlDialog.add(pnlVersion, BorderLayout.SOUTH);
        lbVersion = new JLabel(AppInformation.getInformation().buildString());
        lbVersion.setToolTipText("Version");
        lbVersion.setForeground(Color.white);
        pnlVersion.add(lbVersion);

        JPanel pnlItem = null;
        pnlDialog.add(pnlInfo, BorderLayout.CENTER);
                
        txtThanks = new JEditorPane();
        txtThanks.setEditorKit(new HTMLEditorKit());
        txtThanks.setEditable(false);
        txtThanks.setBorder(null);
        txtThanks.setPreferredSize(new Dimension(300, 150));        
        txtThanks.setBackground(color);
        txtThanks.addHyperlinkListener(new HyperlinkListener() {          
          public void hyperlinkUpdate(HyperlinkEvent event) {
            if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
              if (Desktop.getDesktop().isSupported(Desktop.Action.MAIL)) {
                try {
                  Desktop.getDesktop().mail(new URI(event.getDescription().replaceAll(" ", "%20")));
                }
                catch (Exception ex) {
                  JOptionPane.showMessageDialog(getOwner(),
                      "Error attempting to launch mail client\nand send message to\n" + E_MAIL +
                          "\n" + ex.getMessage(), "About", JOptionPane.ERROR_MESSAGE);
                }
              }
              else {
                JOptionPane.showMessageDialog(getOwner(),
                    "Cannot send message to " + E_MAIL +
                    ".\nE-mail client cannot be launch (is not supported).", 
                    "About", JOptionPane.INFORMATION_MESSAGE);
              }
              
              if (Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                try {
                  Desktop.getDesktop().browse(new URI(event.getDescription().replaceAll(" ", "%20")));
                }
                catch (Exception ex) {
                  JOptionPane.showMessageDialog(getOwner(),
                      "Error attempting to launch web browser application\nand browse page\n" + event.getDescription() +
                          "\n" + ex.getMessage(), "About", JOptionPane.ERROR_MESSAGE);
                }
              }
              else {
                JOptionPane.showMessageDialog(getOwner(),
                    "Cannot browse web page " + event.getDescription() +
                    ".\nWeb browser application cannot be launch (is not supported).", 
                    "About", JOptionPane.INFORMATION_MESSAGE);
              }
            }
          }
        });
        
        txtThanks.setText("<html><body><br><br><br><br><br>" +
//        		"<p><font color=\"#0000ff\">" + AppInformation.getInformation().getTitle() + 
//        		"</font> is based on <code>Netbeans OpenIDE</code> and licensed under a " +
//            "<kbd>Creative Commons Attribution-Noncommercial-Share Alike 3.0 Germany License</kdb>.</p>" +
            "<p><h3><font color=\"#0000ff\">" + AppInformation.getInformation().getTitle() + "</font> Developers</h3>" +
//            "Charles Skelton, 2003-2010<br>" + 
            "Thomas Zielinski, 2010-2013</p>" +
            "<p><h3><font color=\"#0000ff\">" + AppInformation.getInformation().getTitle() + "</font> Quality Team</h3>" + 
            "Thomas Zielinski, Rafal Sytek, Wieslaw Nosal, Pawel Hudak, Joanna Jarmulska, Katarzyna Smaga, Bartosz Kaliszuk, Patryk Bukowinski</p>" +
            "<p><h3>kdb+ & q Consultancy</h3>" + 
            "Pawel Hudak, Wieslaw Nosal</p>" +
            "<p><h3>Thanks</h3>" + 
            "Many thanks to all people who have supported me during development.</p>" +
            "<p><h3><a href=\"mailto:" + E_MAIL + "?subject=q-lab-standalone+\">Feedback</a></h3>" + 
            "Please, report any bug. Contact with us if you have any suggestion.</p>" +
            "<p><h3>Visit Wroclaw, Poland</h3>" +
            "<a href=\"http://www.wroclaw.pl\">Wroclaw - the meeting</a><br>" +
            "<a href=\"http://www.google.de/search?q=wroclaw\">Google search: Wroclaw</a></p>" +
            "</body></html>");   
        
        scrollPane = new JScrollPane(txtThanks,
            JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);
        pnlItem = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        pnlItem.setBackground(color);
        pnlItem.add(scrollPane);
        pnlInfo.add(pnlItem, BorderLayout.CENTER);
        
        txtThanks.setCaretPosition(0);
        this.value = this.scrollPane.getVerticalScrollBar().getMinimum(); 
        
        timer = new Timer(100, this);
        timer.start();
      }
      catch (java.lang.Throwable ignored) {
      }
    }
    return pnlDialog;
  }
  
  public void actionPerformed(ActionEvent event) {
    value %= this.scrollPane.getVerticalScrollBar().getMaximum();
    scrollPane.getVerticalScrollBar().setValue(++value);
  }
  
  @Override
  public void setVisible(boolean show) {
    if (!show) {
      if (timer != null) {
        timer.stop();
        timer = null;
      }
    }
    super.setVisible(show);
  }
}