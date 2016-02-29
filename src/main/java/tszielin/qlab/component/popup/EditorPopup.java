package tszielin.qlab.component.popup;

import java.util.Hashtable;
import java.util.Map;

import javax.swing.Action;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;

import org.netbeans.editor.ActionFactory;
import org.netbeans.editor.BaseKit;
import org.netbeans.editor.ext.ExtKit;

import tszielin.qlab.action.editor.CloseAction;
import tszielin.qlab.action.editor.CloseAllAction;
import tszielin.qlab.action.editor.CoUncommentAction;
import tszielin.qlab.action.editor.ReleaseConnectionAction;
import tszielin.qlab.action.editor.SaveAction;
import tszielin.qlab.action.editor.SaveAllAction;
import tszielin.qlab.action.editor.SaveAsAction;
import tszielin.qlab.action.kdb.CancelRunAction;
import tszielin.qlab.action.kdb.RunAction;
import tszielin.qlab.action.kdb.RunLineAction;
import tszielin.qlab.component.editor.Editor;
import tszielin.qlab.component.editor.QEditor;
import tszielin.qlab.component.pane.ConsolesTabbedPane;
import tszielin.qlab.component.pane.EditorsTabbedPane;
import tszielin.qlab.listener.ActionHintsListener;
import tszielin.qlab.listener.EditorPopupListener;
import tszielin.qlab.util.component.menu.ActionMenuItem;
import tszielin.qlab.util.image.IconsItem;

public class EditorPopup extends JPopupMenu {
  private static final long serialVersionUID = 3862094614365315388L;
  
  public EditorPopup(Editor editor, EditorsTabbedPane tabEditors, ConsolesTabbedPane tabConsoles) {
    super("Editor");
    
    addPopupMenuListener(new EditorPopupListener());

    Action cancelAction = new CancelRunAction(tabEditors);
    Action action = new RunAction(tabEditors, tabConsoles, cancelAction);
    add(new ActionMenuItem(action, new ActionHintsListener(tabEditors, action)));
    action = new RunLineAction(tabEditors, tabConsoles, cancelAction);
    add(new ActionMenuItem(action, new ActionHintsListener(tabEditors, action)));
    add(new ActionMenuItem(cancelAction, new ActionHintsListener(tabEditors, cancelAction)));
    addSeparator();
    action = new ReleaseConnectionAction(tabEditors);
    add(new ActionMenuItem(action, new ActionHintsListener(tabEditors, action)));
    
    Map<Object, ActionMenuItem> map = new Hashtable<Object, ActionMenuItem>();
    Action[] actions = editor.getActions();
    ActionMenuItem item = null;
    if (actions != null && actions.length > 0) {
      for (int count = 0; count < actions.length; count++) {
        item = new ActionMenuItem(actions[count], new ActionHintsListener(tabEditors, actions[count]));
        if (actions[count] instanceof BaseKit.CopyAction) {
          item.getAction().putValue(Action.NAME, "Copy");
          item.getAction().putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control C"));
          item.getAction().putValue(Action.SHORT_DESCRIPTION, "Copy text");
          item.getAction().putValue(Action.LONG_DESCRIPTION, "Copy selected text to clipboard");
          item.getAction().putValue(Action.SMALL_ICON, IconsItem.ICON_COPY);
          map.put(item.getAction().getValue(Action.NAME), item);
        }
        else {
          if (actions[count] instanceof BaseKit.CutAction) {
            item.getAction().putValue(Action.NAME, "Cut");
            item.getAction().putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control X"));
            item.getAction().putValue(Action.SHORT_DESCRIPTION, "Cut text");
            item.getAction().putValue(Action.LONG_DESCRIPTION, "Cut selected text and copy it to clipboard");
            item.getAction().putValue(Action.SMALL_ICON, IconsItem.ICON_CUT);
            map.put(item.getAction().getValue(Action.NAME), item);
          }
          else {
            if (actions[count] instanceof BaseKit.PasteAction) {
              item.getAction().putValue(Action.NAME, "Paste");
              item.getAction().putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control V"));
              item.getAction().putValue(Action.SHORT_DESCRIPTION, "Paste text");
              item.getAction().putValue(Action.LONG_DESCRIPTION, "Paste text from clipboard");
              item.getAction().putValue(Action.SMALL_ICON, IconsItem.ICON_PASTE);
              map.put(item.getAction().getValue(Action.NAME), item);              
            }
            else {
              if (actions[count] instanceof BaseKit.SelectAllAction) {
                item.getAction().putValue(Action.NAME, "Select All");
                item.getAction().putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control A"));
                item.getAction().putValue(Action.SHORT_DESCRIPTION, "Select all lines");
                item.getAction().putValue(Action.LONG_DESCRIPTION, "Select all lines in current editor");
                item.getAction().putValue(Action.SMALL_ICON, IconsItem.ICON_SELECT_LINES);
                map.put(item.getAction().getValue(Action.NAME), item);
              }
              else {
                if (actions[count] instanceof BaseKit.SelectLineAction) {
                  item.getAction().putValue(Action.NAME, "Select line");
                  item.getAction().putValue(Action.SHORT_DESCRIPTION, "Select current line");
                  item.getAction().putValue(Action.LONG_DESCRIPTION, "Select current line");
                  item.getAction().putValue(Action.SMALL_ICON, IconsItem.ICON_SELECT_LINE);
                  map.put(item.getAction().getValue(Action.NAME), item);
                }
                else {
                  if (actions[count] instanceof ExtKit.GotoAction) {
                    item.getAction().putValue(Action.NAME, "Go to line...");
                    item.getAction().putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control L"));
                    item.getAction().putValue(Action.SHORT_DESCRIPTION, "Go to line..");
                    item.getAction().putValue(Action.LONG_DESCRIPTION, "Go to line..");
                    item.getAction().putValue(Action.SMALL_ICON, IconsItem.ICON_GOTO_LINE);
                    map.put(item.getAction().getValue(Action.NAME), item);
                    
                    if (item.getAction().getValue(Action.ACCELERATOR_KEY) instanceof KeyStroke) {
                      editor.getInputMap().put((KeyStroke)item.getAction().getValue(Action.ACCELERATOR_KEY), 
                          item.getAction());
                    }
                  }
                  else {
                    if (actions[count] instanceof ExtKit.ReplaceAction) {
                      item.getAction().putValue(Action.NAME, "Find/Replace");
                      item.getAction().putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control F"));
                      item.getAction().putValue(Action.SHORT_DESCRIPTION, "Find or replace..");
                      item.getAction().putValue(Action.LONG_DESCRIPTION, "Find or replace text in current editor.");
                      item.getAction().putValue(Action.SMALL_ICON, IconsItem.ICON_FIND_REPLACE);
                      map.put(item.getAction().getValue(Action.NAME), item);

                      if (item.getAction().getValue(Action.ACCELERATOR_KEY) instanceof KeyStroke) {
                        editor.getInputMap().put(
                            (KeyStroke)item.getAction().getValue(Action.ACCELERATOR_KEY),
                            item.getAction());
                      }
                    }
                    else {
                      if (actions[count] instanceof ActionFactory.RedoAction) {
                        item.getAction().putValue(Action.NAME, "Redo");
                        item.getAction().putValue(Action.SMALL_ICON, IconsItem.ICON_REDO);
                        item.getAction().putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control Y"));
                        item.getAction().putValue(Action.SHORT_DESCRIPTION, "Redo typing");
                        item.getAction().putValue(Action.LONG_DESCRIPTION, "Redo typing in current editor.");
                        item.getAction().setEnabled(false);
                        map.put(item.getAction().getValue(Action.NAME), item);
                      }
                      else {
                        if (actions[count] instanceof ActionFactory.UndoAction) {
                          item.getAction().putValue(Action.NAME, "Undo Typing");
                          item.getAction().putValue(Action.SMALL_ICON, IconsItem.ICON_UNDO);
                          item.getAction().putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control Z"));
                          item.getAction().putValue(Action.SHORT_DESCRIPTION, "Undo typing");
                          item.getAction().putValue(Action.LONG_DESCRIPTION, "Undo typing in current editor.");
                          item.getAction().setEnabled(false);
                          map.put(item.getAction().getValue(Action.NAME), item);                          
                        }
                        else {
                          if (actions[count] instanceof ExtKit.CompletionShowAction) {
                            editor.getInputMap().put(KeyStroke.getKeyStroke("control SPACE"),
                                actions[count]);
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
        
    if (map.get("Undo Typing") != null || map.get("Redo")!= null) {
      addSeparator();
      if (map.get("Undo Typing") != null) {
        add(map.get("Undo Typing"));
      }
      if (map.get("Redo") != null) {
        add(map.get("Redo"));
      }
    }
    if (map.get("Copy") != null || map.get("Cut") != null || map.get("Paste") != null) {
      addSeparator();
      if (map.get("Copy") != null) {
        add(map.get("Copy"));
      }
      if (map.get("Cut") != null) {
        add(map.get("Cut"));
      }
      if (map.get("Paste") != null) {
        add(map.get("Paste"));
      }
    }
    if (map.get("Select All") != null || map.get("Select line") != null) {
      addSeparator();
      if (map.get("Select All") != null) {
        add(map.get("Select All"));
      }
      if (map.get("Select line") != null) {
        add(map.get("Select line"));
      }
    }
    if (map.get("Go to line...") != null || map.get("Find/Replace") != null) {
      addSeparator();
      if (map.get("Go to line...") != null) {
        add(map.get("Go to line..."));
      }
      if (map.get("Find/Replace") != null) {
        add(map.get("Find/Replace"));
      }      
    }

    if (editor instanceof QEditor) {
      addSeparator();
      action = new CoUncommentAction(tabEditors);
      add(new ActionMenuItem(action, new ActionHintsListener(tabEditors, action)));
      editor.getInputMap().put(KeyStroke.getKeyStroke("control SLASH"), action);
    }

    addSeparator();    
    action = new SaveAction(tabEditors);
    add(new ActionMenuItem(action, new ActionHintsListener(tabEditors, action)));

    action = new SaveAsAction(tabEditors);
    add(new ActionMenuItem(action, new ActionHintsListener(tabEditors, action)));
    
    action = new SaveAllAction(tabEditors);
    add(new ActionMenuItem(action, new ActionHintsListener(tabEditors, action)));
    if (action.getValue(Action.ACCELERATOR_KEY) instanceof KeyStroke) {
      editor.getInputMap().put((KeyStroke)action.getValue(Action.ACCELERATOR_KEY), action);
    }

    addSeparator();
    action = new CloseAction(tabEditors);
    add(new ActionMenuItem(action, new ActionHintsListener(tabEditors, action)));
    if (action.getValue(Action.ACCELERATOR_KEY) instanceof KeyStroke) {
      editor.getInputMap().put((KeyStroke)action.getValue(Action.ACCELERATOR_KEY), action); 
    }

    action = new CloseAllAction(tabEditors);
    add(new ActionMenuItem(action, new ActionHintsListener(tabEditors, action)));
    if (action.getValue(Action.ACCELERATOR_KEY) instanceof KeyStroke) {
      editor.getInputMap().put((KeyStroke)action.getValue(Action.ACCELERATOR_KEY), action); 
    }
    
//    editor.getInputMap().put(KeyStroke.getKeyStroke("control SPACE"), new KeywordEditorAction(editor));    
//    editor.getInputMap().put(KeyStroke.getKeyStroke("control L"), gotoAction);
  }
}
