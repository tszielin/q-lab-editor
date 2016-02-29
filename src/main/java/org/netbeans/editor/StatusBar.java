/*
 * Sun Public License Notice The contents of this file are subject to the Sun Public License Version
 * 1.0 (the "License"). You may not use this file except in compliance with the License. A copy of
 * the License is available at http://www.sun.com/ The Original Code is NetBeans. The Initial
 * Developer of the Original Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.editor;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.SoftBevelBorder;
import javax.swing.event.*;
import javax.swing.text.*;

/**
 * Status bar support
 * 
 * @author Miloslav Metelka
 * @version 1.00
 */

public class StatusBar implements PropertyChangeListener, SettingsChangeListener, DocumentListener {

  public static final String CELL_MAIN = "main"; // NOI18N
  public static final String CELL_POSITION = "position"; // NOI18N
  public static final String CELL_TYPING_MODE = "typing-mode"; // NOI18N
  public static final String CELL_MODIFIED = "modified";
  public static final String CELL_SERVER = "server";
  public static final String CELL_FILE_TYPE = "file";

  // public static final String INSERT_LOCALE = "status-bar-insert"; // NOI18N
  // public static final String OVERWRITE_LOCALE = "status-bar-overwrite"; // NOI18N

  private static final String[] POS_MAX_STRINGS = new String[]{"99999:9999"}; // NOI18N

  private static final Insets NULL_INSETS = new Insets(0, 0, 0, 0);

//  static final Border CELL_BORDER = BorderFactory.createCompoundBorder(
//      BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(1, 0, 0, 0,
//          UIManager.getDefaults().getColor("control")), // NOI18N
//          BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 1, 1,
//              UIManager.getDefaults().getColor("controlHighlight")), // NOI18N
//              BorderFactory.createLineBorder(UIManager.getDefaults().getColor("controlDkShadow")) // NOI18N
//          )), BorderFactory.createEmptyBorder(0, 2, 0, 2));

  protected EditorUI editorUI;
  /** The status bar panel into which the cells are added. */
  private JPanel panel;
  private boolean visible;
  private List<JLabel> cellList = new ArrayList<JLabel>();
  private Caret caret;
  private CaretListener caretL;
  private int caretDelay;
  private boolean overwriteModeDisplayed;
  
  private final String INSERT = "Smart Insert";
  private final String OVERWRITE = "Overwrite";
  public static final String WRITE = "Writable";
  public static final String READ = "Read Only";

  static final long serialVersionUID = -6266183959929157349L;

  public StatusBar(EditorUI editorUI) {
    this.editorUI = editorUI;

    caretDelay = 10;
    caretL = new CaretListener(caretDelay);

    Settings.addSettingsChangeListener(this);

    synchronized (editorUI.getComponentLock()) {
      // if component already installed in EditorUI simulate installation
      JTextComponent component = editorUI.getComponent();
      if (component != null) {
        propertyChange(new PropertyChangeEvent(editorUI, EditorUI.COMPONENT_PROPERTY, null,
            component));
      }

      editorUI.addPropertyChangeListener(this);
    }
  }

  public void settingsChange(SettingsChangeEvent evt) {
    Class<?> kitClass = Utilities.getKitClass(editorUI.getComponent());
    String settingName = (evt != null) ? evt.getSettingName() : null;
    if (kitClass != null) {
      refreshPanel();

      if (settingName == null || SettingsNames.STATUS_BAR_CARET_DELAY.equals(settingName)) {
        caretDelay = SettingsUtil.getInteger(kitClass, SettingsNames.STATUS_BAR_CARET_DELAY,
            SettingsDefaults.defaultStatusBarCaretDelay);
        if (caretL != null) {
          caretL.setDelay(caretDelay);
        }
      }

      if (settingName == null || SettingsNames.STATUS_BAR_VISIBLE.equals(settingName)) {
        boolean wantVisible = SettingsUtil.getBoolean(kitClass, SettingsNames.STATUS_BAR_VISIBLE,
            SettingsDefaults.defaultStatusBarVisible);
        setVisible(wantVisible);
      }

    }
  }

  private void documentUndo(DocumentEvent evt) {
    Utilities.runInEventDispatchThread(new Runnable() {
      public void run() {
        // Clear the main cell
        setText(CELL_MAIN, "");
      }
    });
  }

  public void insertUpdate(DocumentEvent evt) {
    if (evt.getType() == DocumentEvent.EventType.REMOVE) { // undo
      documentUndo(evt);
    }
  }

  public void removeUpdate(DocumentEvent evt) {
    if (evt.getType() == DocumentEvent.EventType.INSERT) { // undo
      documentUndo(evt);
    }
  }

  public void changedUpdate(DocumentEvent evt) {
  }

  protected JPanel createPanel() {
    return new JPanel(new GridBagLayout());
  }

  public boolean isVisible() {
    return visible;
  }

  public void setVisible(boolean v) {
    if (v != visible) {
      visible = v;

      if (panel != null || visible) {
        if (visible) { // need to refresh first
          refreshPanel();
        }
        // fix for issue 13842
        if (SwingUtilities.isEventDispatchThread()) {
          getPanel().setVisible(visible);
        }
        else {
          SwingUtilities.invokeLater(new Runnable() {
            public void run() {
              getPanel().setVisible(visible);
            }
          });
        }
      }
    }
  }

  public final JPanel getPanel() {
    if (panel == null) {
      panel = createPanel();
      initPanel();
    }
    return panel;
  }

  protected void initPanel() {
    addCell(CELL_POSITION, POS_MAX_STRINGS).setHorizontalAlignment(SwingConstants.CENTER);
    addCell(CELL_TYPING_MODE, new String[]{INSERT, OVERWRITE}).setHorizontalAlignment(
        SwingConstants.CENTER);
    setText(CELL_TYPING_MODE, INSERT);
    addCell(CELL_FILE_TYPE, new String[]{WRITE, READ}).setHorizontalAlignment(
        SwingConstants.CENTER);
    setText(CELL_FILE_TYPE, WRITE);
    addCell(CELL_MODIFIED, new String[]{"Modified"}).setHorizontalAlignment(SwingConstants.CENTER);
    getCellByName(CELL_MODIFIED).setVisible(false);
    addCell(CELL_SERVER, null).setHorizontalAlignment(SwingConstants.CENTER);
    getCellByName(CELL_SERVER).setVisible(false);
    addCell(CELL_MAIN, null);
  }

  public void propertyChange(PropertyChangeEvent evt) {
    String propName = evt.getPropertyName();

    if (EditorUI.COMPONENT_PROPERTY.equals(propName)) {
      JTextComponent component = (JTextComponent)evt.getNewValue();
      if (component != null) { // just installed
        component.addPropertyChangeListener(this);

        caret = component.getCaret();
        if (caret != null) {
          caret.addChangeListener(caretL);
        }

        Document doc = component.getDocument();
        if (doc != null) {
          doc.addDocumentListener(this);
        }

        settingsChange(null);
        refreshPanel();

      }
      else { // just deinstalled
        component = (JTextComponent)evt.getOldValue();
        component.removePropertyChangeListener(this);
        caret = component.getCaret();
        if (caret != null) {
          caret.removeChangeListener(caretL);
        }
      }
    }
    else
      if ("caret".equals(propName)) {
        if (caret != null) {
          caret.removeChangeListener(caretL);
        }

        caret = (Caret)evt.getNewValue();
        if (caret != null) {
          caret.addChangeListener(caretL);
        }
      }
      else
        if ("document".equals(propName)) {
          Document old = (Document)evt.getOldValue();
          Document cur = (Document)evt.getNewValue();
          if (old != null) {
            old.removeDocumentListener(this);
          }
          if (cur != null) {
            cur.addDocumentListener(this);
          }
        }

    // Refresh the panel after each property-change
    if (EditorUI.OVERWRITE_MODE_PROPERTY.equals(propName)) {
      caretL.actionPerformed(null); // refresh immediately

    }
    else { // not overwrite mode change
      caretL.stateChanged(null);
    }
  }

  public int getCellCount() {
    return cellList.size();
  }

  public JLabel addCell(String name, String[] widestStrings) {
    return addCell(-1, name, widestStrings);
  }

  public JLabel addCell(int i, String name, String[] widestStrings) {
    JLabel cell = new Cell(name, widestStrings);
    addCellImpl(i, cell);
    return cell;
  }

  public void addCustomCell(int i, JLabel c) {
    addCellImpl(i, c);
  }

  private void addCellImpl(int index, JLabel label) {
    synchronized (cellList) {
      if (!cellList.contains(label)) {
        cellList.add(index < 0 || index > cellList.size() ? cellList.size() : index, label);
      }
    }
    refreshPanel();
  }

  public JLabel getCellByName(String name) {
    if (cellList == null || name == null) {
      return null;
    }
    synchronized (cellList) {      
      for (JLabel label : cellList) {
        if (name.equals(label.getName())) {
          return label;
        }
      }
    }
    return null;
  }

  public String getText(String cellName) {
    JLabel cell = getCellByName(cellName);
    return (cell != null) ? cell.getText() : null;
  }

  public void setText(String cellName, String text) {
    setText(cellName, text, false);
  }
  
  public void setText(String cellName, String text, boolean resize) {
    JLabel cell = getCellByName(cellName);
    if (cell != null) {
      cell.setText(text);
      if (resize && cell instanceof Cell) {
        ((Cell)cell).updateSize();
      }
    }
  }
  
  public void setBoldText(String cellName, String text) {
//    setText(cellName, text, boldColoring);
    setText(cellName, text);
  }

  /*
   * Refresh the whole panel by removing all the components and adding only those that appear in the
   * cell-list.
   */
  private void refreshPanel() {
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        if (isVisible()) { // refresh only if visible
          // Apply coloring to all cells
          if (cellList != null) {
            for (JLabel label : cellList) {
              if (label instanceof Cell) {
                ((Cell)label).setFont(new Font("Dialog", Font.PLAIN, 10));
              }
            }
          }

          // Layout cells
          GridBagConstraints gc = new GridBagConstraints();
          gc.gridx = GridBagConstraints.RELATIVE;
          gc.gridwidth = 1;
          gc.gridheight = 1;

          if (cellList != null) {
            for (JLabel label : cellList) {
              if (CELL_MAIN.equals(label.getName())) {
                gc.fill = GridBagConstraints.HORIZONTAL;
                gc.weightx = 1.0;
              }                        
              getPanel().add(label, gc);
              if (CELL_MAIN.equals(label.getName())) {
                gc.fill = GridBagConstraints.NONE;
                gc.weightx = 0;
              }
            }
          }
        }
      }
    });
  }

  class CaretListener implements ChangeListener, ActionListener {

    Timer timer;

    CaretListener(int delay) {
      timer = new Timer(delay, new WeakTimerListener(this));
      timer.setRepeats(false);
    }

    void setDelay(int delay) {
      timer.setInitialDelay(delay);
    }

    public void stateChanged(ChangeEvent evt) {
      timer.restart();
    }

    public void actionPerformed(ActionEvent evt) {
      Caret c = caret;
      JTextComponent component = editorUI.getComponent();

      if (component != null) {
        if (c != null) {
          BaseDocument doc = Utilities.getDocument(editorUI.getComponent());
          if (doc != null) {
            int pos = c.getDot();
            String s = Utilities.debugPosition(doc, pos);
            setText(CELL_POSITION, s);
          }
        }

        Boolean b = (Boolean)editorUI.getProperty(EditorUI.OVERWRITE_MODE_PROPERTY);
        boolean om = (b != null && b.booleanValue());
        if (om != overwriteModeDisplayed) {
          overwriteModeDisplayed = om;
          setText(CELL_TYPING_MODE, overwriteModeDisplayed ? OVERWRITE : INSERT);
        }
      }
    }
  }

  static class Cell extends JLabel {
    private static final long serialVersionUID = -2554600362177165648L;

    private Dimension maxDimension;
    private String[] widestStrings;

    Cell(String name, String[] widestStrings) {
      setName(name);
      setBorder(new SoftBevelBorder(SoftBevelBorder.LOWERED));
      setOpaque(true);
      this.widestStrings = widestStrings;
    }

    void updateSize() {
      Font f = getFont();
      if (maxDimension == null) {
        maxDimension = new Dimension();
      }
      if (f != null) {
        Border b = getBorder();
        Insets ins = (b != null) ? b.getBorderInsets(this) : NULL_INSETS;
        FontMetrics fm = getFontMetrics(f);
        String text = this.getText();

        if (text == null) {
          text = "";
        }

        int mw = fm.stringWidth(text);

        maxDimension.height = fm.getHeight() + ins.top + ins.bottom;
        if (widestStrings != null) {
          for (int i = 0; i < widestStrings.length; i++) {
            text = widestStrings[i];
            if (text == null) {
              text = "";
            }

            mw = Math.max(mw, fm.stringWidth(text));
          }
        }
        maxDimension.width = mw + ins.left + ins.right;
      }
    }

    public Dimension getPreferredSize() {
      if (maxDimension == null) {
        maxDimension = new Dimension();
      }
      return new Dimension(maxDimension);
    }

    public Dimension getMinimumSize() {
      if (maxDimension == null) {
        maxDimension = new Dimension();
      }
      return new Dimension(maxDimension);
    }

    public void setFont(Font f) {
      super.setFont(f);
      updateSize();
    }
  }
}
