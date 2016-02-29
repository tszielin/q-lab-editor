package tszielin.qlab.action.kdb;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;

import org.netbeans.editor.BaseDocument;
import org.netbeans.editor.Utilities;

import studio.kdb.*;
import studio.ui.QGrid;
import tszielin.qlab.action.editor.EditorAction;
import tszielin.qlab.component.console.*;
import tszielin.qlab.component.editor.QEditor;
import tszielin.qlab.component.pane.ConsolesTabbedPane;
import tszielin.qlab.component.pane.EditorsTabbedPane;
import tszielin.qlab.config.AppConfig;
import tszielin.qlab.config.AppInformation;
import tszielin.qlab.config.data.ConnectionStatus;
import tszielin.qlab.error.StudioException;
import tszielin.qlab.event.WorkerChanged;
import tszielin.qlab.util.event.FireData;
import tszielin.qlab.util.image.IconsItem;
import tszielin.qlab.util.listener.DataListener;

import com.kx.KdbConnection;
import com.kx.error.KException;
import com.kx.kdb.K;

public class RunLineAction extends EditorAction {  
  private static final long serialVersionUID = 1798197243616133203L;
  public static String LAST_QUERY;
  private SwingWorker<Object, Object> worker;
  
  private ConsolesTabbedPane tabConsoles;
  private FireData fireData;
  private boolean multiConsoles;
  
  protected RunLineAction(EditorsTabbedPane tabEditors,
      ConsolesTabbedPane tabConsoles, Action action, String caption, char mnemonic, 
      Icon icon, KeyStroke key, String toolTip, String hint) {
    super(tabEditors, caption, mnemonic, icon, key, toolTip, hint);
    this.tabConsoles = tabConsoles;
    if (action instanceof DataListener) {
      fireData = new FireData();
      fireData.addDataListener((DataListener)action);
    }    
  }

  public RunLineAction(EditorsTabbedPane tabEditors, ConsolesTabbedPane tabConsoles, Action action) {
    this(tabEditors, tabConsoles, action, "Execute line...", (char)0, IconsItem.ICON_RUN_TO, KeyStroke.getKeyStroke("control ENTER"), "Execute line..", "Execute the current line as a query.");
  }

  public void actionPerformed(ActionEvent event) {
    String query = getQuery();
    if (query != null && !query.trim().isEmpty() && getEditor() instanceof QEditor) {      
      executeK4Query(((QEditor)getEditor()).getKDBServer(), query);        
    }
  }
  
  protected String getQuery() {
    if (getEditor() != null) {          
      Caret caret = getEditor().getCaret();
      BaseDocument doc = (BaseDocument)getEditor().getDocument();
      try {
        int startPos = Utilities.getRowStart(doc, caret.getDot());
        int endPos = Utilities.getRowEnd(doc, caret.getDot());
        String text = getEditor().getText(startPos, endPos - startPos);
        return text != null && !text.trim().isEmpty() && !text.startsWith("/") ? 
            text.trim() : "";
      }
      catch (BadLocationException e) {
        getEditor().getToolkit().beep();
      }
    }
    return null;
  }
  
  private void executeK4Query(final KdbConnection server, final String query) {   
    if (query == null || query.trim().length() == 0 || server == null) {
      return;
    }
    
    try {
      multiConsoles = AppConfig.getConfig().hasMultiConsoles();
    }
    catch(StudioException ignored) {
      multiConsoles = true;
    }
    
    worker = new SwingWorker<Object, Object>() {
      private K.KType<?> result = null;
      private Throwable exception;
      private long execTime = 0;

      @Override
      public Object doInBackground() {
        if (!(getEditor() instanceof QEditor)) {
          return null;
        }
        Cursor cursor = getEditor().getCursor();
        Color editorBackground = getEditor().getBackground();
        int consoleId = tabConsoles.getConsoleID(getEditor());
        try {
          getEditor().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
          getEditors().setBackgroundAt(getEditors().getSelectedIndex(), Color.darkGray);
          if (consoleId > -1 && consoleId < tabConsoles.getTabCount()) {
            tabConsoles.setBackgroundAt(consoleId, Color.darkGray);
          }
          getEditor().setBackground(new Color(245, 245, 245));
          if (server.isClosed()) {
            server.reconnect(true);
            if (((QEditor)getEditor()).getConnectionStatus() == ConnectionStatus.LOST_CONNECTION) {
              ((QEditor)getEditor()).getConnection().setStatus(ConnectionStatus.CONNECTED);
              ((QEditor)getEditor()).setConnectionStatus();
            }
          }
          if (query != null && !query.trim().isEmpty()) {
            long startTime = System.currentTimeMillis();
            server.write(new K.KCharacterArray(query));
            result = server.getResponse();
            execTime = System.currentTimeMillis() - startTime;
            LAST_QUERY = query;
          }
        }
        catch (Throwable cause) {
          result = null;
          System.gc();
          exception = cause;
        }
        finally {
          getEditor().setBackground(editorBackground);
          getEditor().setCursor(cursor);
          getEditors().setBackgroundAt(getEditors().getSelectedIndex(), UIManager.getColor("TabbedPane.highlight"));
          if (consoleId > -1 && consoleId < tabConsoles.getTabCount()) {
            tabConsoles.setBackgroundAt(consoleId, UIManager.getColor("TabbedPane.highlight"));
          }
        }
        return null;
      }

      @Override
      public void done() {
        if (!isCancelled() && getEditor() instanceof QEditor) {
          if (exception != null) {
            try {
              throw exception;
            }
            catch (IOException ex) {
              JOptionPane.showMessageDialog(
                  SwingUtilities.windowForComponent(getEditors()),
                  "A communications error occurred whilst sending the query.\n" +
                  "Please check that the server is running on " +
                      server.getInfo() + "\nError detail is:\n\t" +
                      ex.getMessage(), "Communcation error", JOptionPane.ERROR_MESSAGE);
            }
            catch (KException ex) { 
              Console console = null;
              if (tabConsoles.getTabCount() > 0) {
                console = tabConsoles.getConsole((QEditor)getEditor());
                if (console instanceof ErrorConsole) {
                  console.setComponent(null);
                }
                else {
                  int index = tabConsoles.getConsoleID(console);
                  if (index > -1) {
                    console = new ErrorConsole((QEditor)getEditor());
                    tabConsoles.setComponentAt(index, console);
                    tabConsoles.setIconAt(index, IconsItem.ICON_EXLAM);
                    tabConsoles.setTitleAt(index, "Error details <" + getEditors().getEditorTitle() + ">");
                  }
                }
              }
              if (console == null) {
                console = new ErrorConsole((QEditor)getEditor());
                tabConsoles.addTab("Error details <" + getEditors().getEditorTitle() + ">", IconsItem.ICON_EXLAM, console); 
              }
              console.setStatus(DataType.Error.name());
              tabConsoles.setSelectedComponent(console);
              tabConsoles.setIconAt(tabConsoles.getSelectedIndex(), IconsItem.ICON_EXLAM);
    
              String hint = QErrors.lookup(ex.getMessage());
              hint = hint != null ? "Studio Hint: Possibly this error refers to <i>" + hint +
                  "</i>" : "";

              ((ErrorConsole)console).setError("<font color=red size=3 face=\"Dialog\">An error occurred during execution of the query.<br>" +
                  "The server sent the response: <b>" +
                  ex.getMessage() +
                  "</b><br>" +
                  hint +
                  "</font>", query);
            }
            catch (java.lang.OutOfMemoryError ex) {
              JOptionPane.showMessageDialog(
                  SwingUtilities.windowForComponent(getEditors()),
                  "Out of memory whilst communicating with " + server.getInfo() + 
                  "\nThe result set is probably too large.\n" + 
                  "Try increasing the memory available to " + 
                  AppInformation.getInformation().getTitle() + "\nthrough the command line option -J -Xmx1024m",
                  "Memory error", JOptionPane.ERROR_MESSAGE);
            }
            catch (Throwable ex) {
              String message = ex.getMessage();

              if ((message == null) || (message.length() == 0))
                message = "No message with exception. Exception is " + ex.toString();

              JOptionPane.showMessageDialog(SwingUtilities.windowForComponent(getEditors()),
                  "An unexpected error occurred whilst communicating with " + server.getInfo() + 
                  "\nError detail is:\n\t" + message, "Communication error", JOptionPane.ERROR_MESSAGE);
            }            
          }
          try {
            processK4Results(query, result, execTime);
          }
          catch (Exception e) {
            JOptionPane.showMessageDialog(SwingUtilities.windowForComponent(getEditors()),
                "An unexpected error occurred while communicating with " + server.getInfo() + 
                "\nError detail is:\n\t" + e.getMessage(), "Communication error", JOptionPane.ERROR_MESSAGE);
          }
          finally {
            if (fireData != null) {
              fireData.onData(new WorkerChanged(this));
            }
            cleanup();
          }
        }
      }
      
      private void cleanup() {
        System.gc();
        worker = null;
      }
    };
    if (fireData != null) {
      fireData.onData(new WorkerChanged(this, worker, server));
    }
    worker.execute();
  }
  
  private void processK4Results(String query, K.KType<?> result, long execTime) throws KException {
    if (result != null && getEditor() instanceof QEditor) {
      Console console = null;
      if (!multiConsoles && tabConsoles.getTabCount() > 1) {
        while(tabConsoles.getTabCount() > 1) {
          tabConsoles.remove(tabConsoles.getTabCount() - 1);
        }
      }
      if (result.getType() != 10 && (KTableTableModel.isTable(result) || isDictionary(result) || isList(result))) {
        if (tabConsoles.getTabCount() > 0) {
          console = multiConsoles ? tabConsoles.getConsole((QEditor)getEditor()) :
            tabConsoles.getComponentAt(0) instanceof Console ? (Console)tabConsoles.getComponentAt(0) : null;
          if (console instanceof GridConsole) {
            console.setComponent(new QGrid(result), query, result.getType(), execTime);
          }
          else {
            int index = tabConsoles.getConsoleID(console);
            if (index > -1) {
              console = new GridConsole(new QGrid(result), (QEditor)getEditor(), query, 
                  result.getType(), execTime);
              tabConsoles.setComponentAt(index, console);
              tabConsoles.setIconAt(index, IconsItem.ICON_CONSOLE);
              tabConsoles.setTitleAt(index, getEditors().getEditorTitle());
            }
          }
        }
        if (console == null) {
          console = new GridConsole(new QGrid(result), (QEditor)getEditor(), query, 
              result.getType(), execTime);
          tabConsoles.addTab(getEditors().getEditorTitle(), IconsItem.ICON_TABLE, console);
        }
        ((GridConsole)console).setStatus(isDictionary(result) ? DataType.Dictionary
            : isList(result) ? DataType.List : DataType.Table);
        tabConsoles.setSelectedComponent(console);
        tabConsoles.setIconAt(tabConsoles.getSelectedIndex(), IconsItem.ICON_TABLE);
        if (!multiConsoles) {
          tabConsoles.setTitleAt(0, getEditors().getEditorTitle());
        }
      }
      else {
                JEditorPane editorPane = new JEditorPane("text/plain", result.toString(true));
        if (tabConsoles.getTabCount() > 0) {
          console = multiConsoles ? tabConsoles.getConsole((QEditor)getEditor()) : 
            tabConsoles.getComponentAt(0) instanceof Console ? (Console)tabConsoles.getComponentAt(0) : null;
          if (console instanceof ResultConsole) {
            ((ResultConsole)console).setComponent(editorPane, query, result.getType(), execTime);
          }
          else {
            int index = tabConsoles.getConsoleID(console);
            if (index > -1) {
              console = new ResultConsole(editorPane, (QEditor)getEditor(), query, result.getType(), execTime);
              tabConsoles.setComponentAt(index, console);
              tabConsoles.setIconAt(index, IconsItem.ICON_CONSOLE);
            }
          }
        }
        if (console == null) {
          console = new ResultConsole(editorPane, (QEditor)getEditor(), query, result.getType(), execTime);
          tabConsoles.addTab(getEditors().getEditorTitle(), IconsItem.ICON_CONSOLE, console);
        }
        ((ResultConsole)console).setStatus();
        if (!multiConsoles) {
          tabConsoles.setTitleAt(0, getEditors().getEditorTitle());
        }
        tabConsoles.setSelectedComponent(console);
      }
    }
  }

  private boolean isDictionary(Object obj) {
    return obj instanceof K.KDictionary && 
      ((K.KDictionary)obj).getKeys() instanceof K.KBaseArray && 
      ((K.KDictionary)obj).getValues() instanceof K.KBaseArray;
  }
  
  private boolean isList(Object obj) {
    return obj instanceof K.KList && 
      ((K.KList)obj).getArray() != null || obj instanceof K.KBaseArray;
  }
}