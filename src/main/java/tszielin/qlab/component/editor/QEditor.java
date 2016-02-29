package tszielin.qlab.component.editor;

import java.awt.Color;
import java.io.File;
import java.util.TimerTask;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import org.netbeans.editor.*;
import org.netbeans.editor.example.QKit;
import org.netbeans.editor.ext.q.QFormatter;

import com.kx.KdbConnection;
import com.kx.kdb.K;

import tszielin.qlab.component.pane.ConsolesTabbedPane;
import tszielin.qlab.component.pane.EditorsTabbedPane;
import tszielin.qlab.component.popup.EditorPopup;
import tszielin.qlab.config.data.*;
import tszielin.qlab.error.FileException;
import tszielin.qlab.event.*;
import tszielin.qlab.util.event.DataEvent;
import tszielin.qlab.util.image.IconsItem;
import tszielin.qlab.util.listener.DataListener;
import tszielin.qlab.util.listener.PopupListener;

public class QEditor extends Editor {
  private static final long serialVersionUID = 8484442847920710705L;
  
  private KdbConnection kdbServer;
  private boolean debuger;

  class KDBConnect extends TimerTask {
    private QEditor editor;
    private KdbConnection kdbServer;
    
    public KDBConnect(QEditor editor, KdbConnection kdbServer) {
      this.editor = editor;
      this.kdbServer = kdbServer;
    }

    public void run() {
      if (editor != null && editor.getConnection() != null) {
        if (kdbServer == null) {
          kdbServer = new KdbConnection(editor.getConnection().getHost(), editor.getConnection().getPort(),
              editor.getConnection().getCredentials());
          kdbServer.addDataListener((DataListener)editor);
        }
        try {
          kdbServer.reconnect(true);
          editor.getConnection().setStatus(ConnectionStatus.CONNECTED);
          kdbServer.write(new K.KCharacterArray("`dbg in key `"));
          try {
            K.KType<?> result = kdbServer.getResponse();
            if (result instanceof K.KBoolean) {
              debuger = ((K.KBoolean)result).getValue();
              if (Utilities.getEditorUI(editor).getGlyphGutter() != null) {
                Utilities.getEditorUI(editor).getGlyphGutter().setDebuger(debuger);
              }
            }            
          }
          catch (Throwable ignored) {
          }          
        }
        catch (Exception ex) {
          if (editor.getConnection().getStatus() == ConnectionStatus.CONNECTED) {
            editor.getConnection().setStatus(ConnectionStatus.LOST_CONNECTION);
          }
          if (editor.getConnection() == null || editor.getConnection().getStatus() == ConnectionStatus.NOT_CONNECTED) {
            JOptionPane.showMessageDialog(getEditors(), "Cannot connect to " +
              editor.getConnection().getServiceInfo(), "kdb+ Server", 
              JOptionPane.ERROR_MESSAGE);
            kdbServer = null;
          }
        }
        
        editor.setConnectionStatus();
      }
    }
  }
  
  public QEditor(EditorsTabbedPane tabEditors, ConsolesTabbedPane tabConsoles) throws FileException {
    this((String)null, tabEditors, tabConsoles);
  }

  public QEditor(EditorFile file, EditorsTabbedPane tabEditors, ConsolesTabbedPane tabConsoles) throws FileException {
    super(file, tabEditors, tabConsoles);
    setContentType("text/q");
    Formatter.setFormatter(QKit.class, new QFormatter(config));        
    Utilities.getEditorUI(this).getStatusBar();
    
    loadFile();
    
    undoManager = new EditorUndoManager(this);
    getDocument().addUndoableEditListener(undoManager);
    getDocument().putProperty(BaseDocument.UNDO_MANAGER_PROP, undoManager);
    undoManager.discardAllEdits();

    getDocument().addDocumentListener(this);
    
    if (file.getConnection() != null) {
      setConnection(file.getConnection());
    }
    else {
      Utilities.setStatusBarText(this, StatusBar.CELL_SERVER, "<not connected>", true, Color.RED);
    }
    
    JPopupMenu popup = new EditorPopup(this, this.tabEditors, tabConsoles);
    addMouseListener(new PopupListener(popup));
    setComponentPopupMenu(popup);
            
    setActions(); 
  }
  
  public QEditor(File file, EditorsTabbedPane tabEditors, ConsolesTabbedPane tabConsoles) throws FileException {
    this(new EditorFile(file), tabEditors, tabConsoles);
  }
  
  public QEditor(String filename, EditorsTabbedPane tabEditors, ConsolesTabbedPane tabConsoles) throws FileException {
    this(filename != null ? new File(filename) : null, tabEditors, tabConsoles);
  }
  
  public void dispose() {
    super.dispose();
    if (kdbServer != null) {
      kdbServer.close();
      kdbServer = null;
    }
  }
  public KdbConnection getKDBServer() {
    if (kdbServer != null) {
      return kdbServer;
    }
    else {
      if (getConnection() != null && getConnectionStatus() == ConnectionStatus.LOST_CONNECTION) {
        kdbServer = new KdbConnection(getConnection().getHost(), getConnection().getPort(), getConnection().getCredentials());
        kdbServer.addDataListener(this);        
        setConnectionStatus();        
        return kdbServer;        
      }
    }    
    setConnectionStatus();
    return null;
  }
  
  @Override
  public KdbService getConnection() {
    return editorFile != null ? editorFile.getConnection() : null;
  }
  
  public ConnectionStatus getConnectionStatus() {
    return editorFile == null || editorFile.getConnection() == null ? ConnectionStatus.NOT_CONNECTED : editorFile.getConnection().getStatus();
  }
  
  public synchronized void setConnection(KdbService connection) {
    if (editorFile == null) {
      return;
    }
    editorFile.setConnection(connection);
    if (this.kdbServer != null) {
      this.kdbServer.close();
      if (connection == null) {
        this.kdbServer.removeDataListener((DataListener)tabEditors);
        this.kdbServer = null;
      }
    }
    if (editorFile.getConnection() != null) {
      this.kdbServer = new KdbConnection(editorFile.getConnection().getHost(), editorFile.getConnection().getPort(), 
          editorFile.getConnection().getCredentials());
      this.kdbServer.addDataListener((DataListener)this);
      timer.schedule(new KDBConnect(this, this.kdbServer), 250);
      Utilities.setStatusBarText(this, StatusBar.CELL_SERVER, "Checking connection...", true, Color.MAGENTA);
    }
    setConnectionStatus();
  }
  
  public boolean isDebuger() {
    return debuger;
  }

  @Override
  public String getTitle() {
    return " - " + getPath(true) + 
      (editorFile.getConnection() == null || 
          editorFile.getConnection().getStatus() == ConnectionStatus.NOT_CONNECTED ? " (not connected)" :
        " (" + editorFile.getConnection().getInfo(true, false) +
            (editorFile.getConnection().getStatus() == ConnectionStatus.LOST_CONNECTION ? " - connection lost" : "") + ")");            
  }
  
  @Override
  public String getTooltip() {
    return  editorFile != null ? 
        "<html><table cellpadding=\"0\" cellspacing=\"0\" border=\"0\"><tr><td align=\"right\">File:</td><td>" + editorFile.getPath() + "</td></tr>" + 
        (editorFile.getConnection() != null && (editorFile.getConnection().getStatus() == ConnectionStatus.CONNECTED || 
            editorFile.getConnection().getStatus() == ConnectionStatus.LOST_CONNECTION) ? 
            "<tr><td align=\"right\">kdb Server:</td><td>" +
              editorFile.getConnection().getInfo(true, false) +
                (editorFile.getConnection().getStatus() == ConnectionStatus.LOST_CONNECTION ? "<br>(connection lost)" : "") +
              "</td></tr>" : "") +
        (isModified() ? "<tr><td align=\"right\">Status:</td><td>Modified</td></tr>" : "") +
            "</table></html>" : null;
  }

  @Override
  public void onData(DataEvent<?> event) {
    if (event != null) {
      super.onData(event);
      if (event.getSource() instanceof JPanel) {
        if (event instanceof TabIndexChanged && ((TabIndexChanged)event).getData() != -1) {
          if (tabEditors.getEditor(((TabIndexChanged)event).getData()) == this) {
            setActions();
          }
          if (editorFile != null) {
            editorFile.setActive(true);
          }
        }
      }
      else {
        if (event instanceof ConnectionClosed && event.getSource() instanceof Runnable && 
            ((ConnectionClosed)event).getData() != null && editorFile != null && 
            editorFile.getConnection() != null) {
          if (config.isLostConnectionMessage()) {
            fireData.onData(new ConnectionClosed(this, ((ConnectionClosed)event).getData()));
          }
          this.editorFile.getConnection().setStatus(ConnectionStatus.LOST_CONNECTION);
          if (kdbServer != null) {
            kdbServer.close();  
            if (kdbServer != null) {
              kdbServer.removeDataListener(this);
            }
          }
          kdbServer = null;

          setConnectionStatus();
        }
      }
    }
  }
  
  public void setConnectionStatus() {
    Utilities.setStatusBarText(this, StatusBar.CELL_SERVER,
        getConnection() == null ? "<not connected>" :
          getConnection().getStatus() == ConnectionStatus.CONNECTED ? 
              getConnection().getServiceInfo() :
                getConnection().getStatus() == ConnectionStatus.NOT_CONNECTED ? 
                    "<not connected>" : "(connection lost)", true,
          getConnection() == null ? Color.red :
            getConnection().getStatus() == ConnectionStatus.CONNECTED ? 
                getConnection().getTitleColor() : 
                  getConnection().getStatus() == ConnectionStatus.NOT_CONNECTED ? 
                      Color.red : Color.darkGray);
    GlyphGutter glyphGutter = Utilities.getEditorUI(this).getGlyphGutter();
    if (glyphGutter != null) {
      glyphGutter.setConnected(
        getConnection() != null ? getConnection().getStatus() : null);
    }
    fireData.onData(new KdbServiceChanged(this, getConnection()));
    setActions();
  }

  @Override
  public void insertUpdate(DocumentEvent event) {
    super.insertUpdate(event);
    if (tabEditors.insertMatchingBrackets()) {
      Document doc = event.getDocument();
      int offset = event.getOffset();
      int length = event.getLength();
      if (length == 1) {
        String inserted = "";
        try {
          inserted = doc.getText(offset, length);
          char ch = inserted.charAt(0);
          if ((ch == '(' || ch == '{' || ch == '[' || ch == '"') &&
              doc.getText(event.getOffset() - 1, length).charAt(0) != '"') {
            doc.insertString(offset + 1,
                ch == '(' ? ")" : ch == '{' ? "}" : ch == '[' ? "]" : "\"", null);
            setCaretPosition(getCaretPosition() - 1);
          }
        }
        catch (BadLocationException ignored) {
        }
      }
    }
  }
  
  @Override
  public Icon getIcon() {
    return new ImageIcon(IconsItem.IMAGE_Q_FILE);
  }
}
