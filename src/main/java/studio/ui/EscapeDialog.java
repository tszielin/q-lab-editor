/*
 * Studio for kdb+ by Charles Skelton is licensed under a Creative Commons
 * Attribution-Noncommercial-Share Alike 3.0 Germany License
 * http://creativecommons.org/licenses/by-nc-sa/3.0 except for the netbeans components which retain
 * their original copyright notice
 * 
 * Thomas Zielinski
 * Added center possibility
 */

package studio.ui;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class EscapeDialog extends JDialog {
  private static final long serialVersionUID = 3706596407754413202L;

  public EscapeDialog() {
    this((Frame)null, false);
  }

  public EscapeDialog(Frame owner) {
    this(owner, false);
  }

  public EscapeDialog(Frame owner, boolean modal) {
    this(owner, null, modal);
  }

  public EscapeDialog(Frame owner, String title) {
    this(owner, title, false);
  }

  public EscapeDialog(Frame owner, String title, boolean modal) {
    super(owner, title, modal);
  }

  public EscapeDialog(Dialog owner) {
    this(owner, false);
  }

  public EscapeDialog(Dialog owner, boolean modal) {
    this(owner, null, modal);
  }

  public EscapeDialog(Dialog owner, String title) {
    this(owner, title, false);
  }

  public EscapeDialog(Dialog owner, String title, boolean modal) {
    super(owner, title, modal);
  }

  public EscapeDialog(Dialog owner, String title, boolean modal, GraphicsConfiguration gc) {
    super(owner, title, modal, gc);
  }

  public EscapeDialog(Frame owner, String title, boolean modal, GraphicsConfiguration gc) {
    super(owner, title, modal, gc);
  }

  public EscapeDialog(Window owner, ModalityType modalityType) {
    super(owner, modalityType);
  }

  public EscapeDialog(Window owner, String title, ModalityType modalityType,
      GraphicsConfiguration gc) {
    super(owner, title, modalityType, gc);
  }

  public EscapeDialog(Window owner, String title, ModalityType modalityType) {
    super(owner, title, modalityType);
  }

  public EscapeDialog(Window owner, String title) {
    super(owner, title);
  }

  public EscapeDialog(Window owner) {
    super(owner);
  }

  @Override
  public void setVisible(boolean show) {
    setVisible(show, true);
  }

  public void setVisible(boolean show, boolean center) {
    if (show) {
      center(center);
    }
    super.setVisible(show);
  }

  @Override
  protected JRootPane createRootPane() {
    ActionListener actionListener = new ActionListener() {
      public void actionPerformed(ActionEvent actionEvent) {
        setVisible(false);
      }
    };
    JRootPane rootPane = new JRootPane();
    rootPane.registerKeyboardAction(actionListener, KeyStroke.getKeyStroke("ESCAPE"),
        JComponent.WHEN_IN_FOCUSED_WINDOW);
    return rootPane;
  }

  private void center(boolean center) {
    if (getOwner() != null && center) {
      Point location = getOwner().getLocation();

      int x = location.x + (getOwner().getSize().width - getPreferredSize().width) / 2;
      int y = location.y + (getOwner().getSize().height - getPreferredSize().height) / 2;
      setLocation(Math.max(0, x), Math.max(0, y));
    }
  }
}
