package tszielin.qlab.component.editor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import org.bounce.text.xml.XMLEditorKit;
import org.bounce.text.xml.XMLStyleConstants;
import org.netbeans.editor.BaseDocument;

import tszielin.qlab.component.pane.ConsolesTabbedPane;
import tszielin.qlab.component.pane.EditorsTabbedPane;
import tszielin.qlab.component.popup.EditorPopup;
import tszielin.qlab.config.data.EditorFile;
import tszielin.qlab.error.FileException;
import tszielin.qlab.util.image.IconsItem;
import tszielin.qlab.util.listener.PopupListener;

public class XMLEditor extends Editor {
  private static final long serialVersionUID = 7554654484804572243L;

  public XMLEditor(EditorsTabbedPane tabEditors, ConsolesTabbedPane tabConsoles) throws FileException {
    this((String)null, tabEditors, tabConsoles);
  }

  public XMLEditor(EditorFile file, EditorsTabbedPane tabEditors, ConsolesTabbedPane tabConsoles) throws FileException {
    super(file, tabEditors, tabConsoles);
    XMLEditorKit kit = new XMLEditorKit();
    kit.install(this);    
    setEditorKit(kit);
    if (file != null && file.getFile() != null) {
      try {
        read(new FileReader(file.getFile()), file.getFile());
      }
      catch (IOException ex) {
        throw new FileException(ex);
      }
    }
    getDocument().putProperty(PlainDocument.tabSizeAttribute, Integer.valueOf(2));
    getDocument().putProperty(XMLEditorKit.ERROR_HIGHLIGHTING_ATTRIBUTE, Boolean.TRUE);
    kit.setAutoIndentation(true);
    kit.setTagCompletion(true);
    
    kit.setStyle(XMLStyleConstants.COMMENT, Color.LIGHT_GRAY, Font.PLAIN);
    kit.setStyle(XMLStyleConstants.ELEMENT_NAME, Color.BLUE, Font.BOLD);
    kit.setStyle(XMLStyleConstants.ENTITY, Color.BLACK, Font.PLAIN);
    kit.setStyle(XMLStyleConstants.STRING, Color.BLACK, Font.ITALIC);
    
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
  
  public XMLEditor(File file, EditorsTabbedPane tabEditors, ConsolesTabbedPane tabConsoles) throws FileException {
    this(new EditorFile(file), tabEditors, tabConsoles);
  }
  
  public XMLEditor(String filename, EditorsTabbedPane tabEditors, ConsolesTabbedPane tabConsoles) throws FileException {
    this(filename != null ? new File(filename) : null, tabEditors, tabConsoles);
  }

  @Override
  public Icon getIcon() {
    return new ImageIcon(IconsItem.IMAGE_XML_FILE);
  }
  
  @Override
  protected void loadFile() throws FileException {
    if (editorFile != null && editorFile.getFile() != null && 
        editorFile.getFile().getPath().endsWith(".xml")) {
      super.loadFile();
    }
  }
  
  @Override
  public void setSize(Dimension dimension) {
    dimension.width = lineWidth < getParent().getSize().width ? getParent().getSize().width : 
      dimension.width < getParent().getSize().width - 15 ? getParent().getSize().width - 15 : lineWidth + 200;
    super.setSize(dimension);
  }
}
