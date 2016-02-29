package tszielin.qlab.component.editor;

import java.awt.Dimension;
import java.io.File;

import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import org.netbeans.editor.BaseDocument;

import tszielin.qlab.component.pane.ConsolesTabbedPane;
import tszielin.qlab.component.pane.EditorsTabbedPane;
import tszielin.qlab.component.popup.EditorPopup;
import tszielin.qlab.config.data.EditorFile;
import tszielin.qlab.error.FileException;
import tszielin.qlab.util.listener.PopupListener;

public class PlainEditor extends Editor {
  private static final long serialVersionUID = -9139878259922723065L;

  public PlainEditor(EditorsTabbedPane tabEditors, ConsolesTabbedPane tabConsoles) throws FileException {
    this((String)null, tabEditors, tabConsoles);
  }

  public PlainEditor(EditorFile file, EditorsTabbedPane tabEditors, ConsolesTabbedPane tabConsoles) throws FileException {
    super(file, tabEditors, tabConsoles);
    
    if (file != null && file.getFile() != null) {
      loadFile();
    }
    
    getDocument().putProperty(PlainDocument.tabSizeAttribute, Integer.valueOf(2));
    undoManager = new EditorUndoManager(this);
    getDocument().addUndoableEditListener(undoManager);
    getDocument().putProperty(BaseDocument.UNDO_MANAGER_PROP, undoManager);
    undoManager.discardAllEdits();
    getDocument().addDocumentListener(this);
    
    JPopupMenu popup = new EditorPopup(this, this.tabEditors, tabConsoles);
    addMouseListener(new PopupListener(popup));
    setComponentPopupMenu(popup);
            
    setActions(); 
    
    try {
      String text = this.getDocument().getText(0, this.getDocument().getLength());
      if (text.indexOf("\n") == -1) {
        lineWidth = SwingUtilities.computeStringWidth(this.getFontMetrics(this.getFont()), text);
      }
      else {
        while (text.indexOf("\n") > -1) {
          String line = text.substring(0, text.indexOf("\n"));
          text = text.substring(line.length() + 1);
          int width = SwingUtilities.computeStringWidth(this.getFontMetrics(this.getFont()), line);
          if (width > lineWidth) {
            lineWidth = width;
          }
        }
      }
    }
    catch (BadLocationException ignored) {
    }
  }
  
  public PlainEditor(File file, EditorsTabbedPane tabEditors, ConsolesTabbedPane tabConsoles) throws FileException {
    this(new EditorFile(file), tabEditors, tabConsoles);
  }
  
  public PlainEditor(String filename, EditorsTabbedPane tabEditors, ConsolesTabbedPane tabConsoles) throws FileException {
    this(filename != null ? new File(filename) : null, tabEditors, tabConsoles);
  }  
  
  @Override
  public void setSize(Dimension dimension) {
    dimension.width = lineWidth < getParent().getSize().width ? getParent().getSize().width : 
      dimension.width < getParent().getSize().width - 15 ? getParent().getSize().width - 15 : lineWidth + 10;
    super.setSize(dimension);
  }
}
