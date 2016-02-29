/*
 * Studio for kdb+ by Charles Skelton is licensed under a Creative Commons
 * Attribution-Noncommercial-Share Alike 3.0 Germany License
 * http://creativecommons.org/licenses/by-nc-sa/3.0 except for the netbeans components which retain
 * their original copyright notice
 */
package org.netbeans.editor.ext.q;

import java.awt.Color;
import java.util.*;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.text.*;

import org.netbeans.editor.*;
import org.netbeans.editor.Utilities;
import org.netbeans.editor.ext.CompletionQuery;

import com.kx.KdbConnection;
import com.kx.kdb.K;

import tszielin.qlab.component.editor.QEditor;
import tszielin.qlab.config.data.ConnectionStatus;
import tszielin.qlab.config.data.KdbService;
import tszielin.qlab.util.image.IconsItem;

public class QCompletionQuery implements CompletionQuery {
  private enum CompletionType {
    KEYWORD, NAMESPACE, DEFINED, TABLE, FUNCTION, COLUMN
  }

  public CompletionQuery.Result query(JTextComponent component, int offset, SyntaxSupport support) {
    CompletionQuery.Result cqResult = null;

    try {
      if (component instanceof QEditor) {
        KdbService connection = ((QEditor)component).getConnection();

        if (connection != null && connection.getStatus() == ConnectionStatus.CONNECTED) {                  
          Caret caret = ((QEditor)component).getCaret();
          
          int rowStart = Utilities.getRowStart(component, caret.getDot());
          int rowEnd = Utilities.getRowEnd(component, caret.getDot());
          int wordStart = Utilities.getWordStart(component, caret.getDot());
          int wordEnd = Utilities.getWordEnd(component, caret.getDot());
          if (wordEnd == -1) {
            wordEnd = caret.getDot();
          }
          if (rowEnd == -1) {
            rowEnd = caret.getDot();
          }
          if (wordEnd >= 0 && wordStart == -1) {
            wordStart = 0;
          }
          String text = ((QEditor)component).getDocument().getText(wordStart, (wordEnd-wordStart));
          int pos = wordEnd;
          while (pos < rowEnd) {
            String str = ((QEditor)component).getDocument().getText(pos, 1);
            if (!(" ".endsWith(str) || "\t".equals(str) || "\n".equals(str) || "\r".equals(str))) {
              text = text + str;
              pos++;
              continue;
            }
            break;
          }
          caret.setDot(pos);
          
          pos = wordStart - 1;
          while (pos >= rowStart) {
            String str = ((QEditor)component).getDocument().getText(pos, 1);
            if (!(" ".endsWith(str) || "\t".equals(str) || "\n".equals(str) || "\r".equals(str))) {
              text = str + text;
              pos--;
              continue;
            }
            break;
          }          

          Map<String, List<CompletionType>> result = null;
          KdbConnection kdbServer = null;
          Object obj = null;
          String items[] = null;
          
          try {
            kdbServer = new KdbConnection(connection.getHost(), connection.getPort(), connection.getCredentials());
            kdbServer.reconnect(true);
            if (text.length() > 1 && text.endsWith(".") && !text.startsWith(".")) {
              kdbServer.write(new K.KCharacterArray("cols " + text.substring(0, text.indexOf("."))));
              obj = kdbServer.getResponse();
              if (obj instanceof K.KSymbolArray) {
                items = (String[])((K.KSymbolArray)obj).getArray();
                List<CompletionQuery.ResultItem> resultItem = new ArrayList<CompletionQuery.ResultItem>();
                for (String item : items) {                  
                  resultItem.add(new BooleanAttribItem(item, CompletionType.COLUMN, offset, 0));
                }
                cqResult = new CompletionQuery.DefaultResult(component, "Columns", resultItem, offset, 0);
              }
            }
            else {
              kdbServer.write(new K.KCharacterArray("\\a"));
              obj = kdbServer.getResponse();
              if (obj instanceof K.KSymbolArray) {
                items = (String[])((K.KSymbolArray)obj).getArray();
                for (String item : items) {
                  String str = text.trim().length() == 0 ? item : 
                      item.startsWith(text) ? item : null;
                  if (str != null) {
                    if (result == null) {
                      result = new TreeMap<String, List<CompletionType>>(String.CASE_INSENSITIVE_ORDER);
                    }
                    if (!result.containsKey(str)) {
                      result.put(str, new ArrayList<CompletionType>(Arrays.asList(new CompletionType[] {CompletionType.TABLE})));
                    }
                    else {
                      if (!result.get(str).contains(CompletionType.TABLE)) {
                        result.get(str).add(CompletionType.TABLE);
                      }
                    }
                  }
                }
              }
              
              kdbServer.write(new K.KCharacterArray("\\f"));
              obj = kdbServer.getResponse();
              if (obj instanceof K.KSymbolArray) {
                items = (String[])((K.KSymbolArray)obj).getArray();
                for (String item : items) {
                  String str = text.trim().length() == 0 ? item : 
                      item.startsWith(text) ? item : null;
                  if (str != null) {
                    if (result == null) {
                      result = new TreeMap<String, List<CompletionType>>(String.CASE_INSENSITIVE_ORDER);
                    }
                    if (!result.containsKey(str)) {
                      result.put(str, new ArrayList<CompletionType>(Arrays.asList(new CompletionType[] {CompletionType.FUNCTION})));
                    }
                    else {
                      if (!result.get(str).contains(CompletionType.FUNCTION)) {
                        result.get(str).add(CompletionType.FUNCTION);
                      }
                    }
                  }
                }
              }

              QSyntax syntax = new QSyntax();
              Set<String> keys = syntax.getKeywords();
              for(String key : keys) {
                String str = key.trim().length() == 0 ? null :
                  text.trim().length() == 0 ? key : 
                    key.startsWith(text) ? key : null;
                if (str != null) {
                  if (result == null) {
                    result = new TreeMap<String, List<CompletionType>>(String.CASE_INSENSITIVE_ORDER);
                  }
                  if (!result.containsKey(str)) {
                    result.put(str, new ArrayList<CompletionType>(Arrays.asList(new CompletionType[] {CompletionType.KEYWORD})));
                  }
                  else {
                    if (!result.get(str).contains(CompletionType.KEYWORD)) {
                      result.get(str).add(CompletionType.KEYWORD);
                    }
                  }
                }
              }
              keys = syntax.getKeys();
              for(String key : keys) {
                String str = key.trim().length() == 0 ? null :
                  text.trim().length() == 0 ? key : 
                    key.startsWith(text) ? key : null;
                if (str != null) {
                  if (result == null) {
                    result = new TreeMap<String, List<CompletionType>>(String.CASE_INSENSITIVE_ORDER);
                  }
                  if (!result.containsKey(str)) {
                    result.put(str, new ArrayList<CompletionType>(Arrays.asList(new CompletionType[] {CompletionType.NAMESPACE})));
                  }
                  else {
                    if (!result.get(str).contains(CompletionType.NAMESPACE)) {
                      result.get(str).add(CompletionType.NAMESPACE);
                    }
                  }
                }
              }
              
              kdbServer.write(new K.KCharacterArray("key `"));
              obj = kdbServer.getResponse();
              if (obj instanceof K.KSymbolArray) {
                List<String> namespaces = Arrays.asList((String[])((K.KSymbolArray)obj).getArray());
                for (String namespace : namespaces) {
                  String add = namespace;
                  String test = text.indexOf(".") != text.lastIndexOf(".") ? text.substring(0, text.lastIndexOf(".")) : text;
                  if ("q".equalsIgnoreCase(namespace) || "o".equals(namespace) || "h".equals(namespace)) {
                    continue;
                  }
                  add = text.trim().length() > 0 ? ("." + namespace).startsWith(test) ? "." + namespace : null : "." + namespace;                  
                  if (add == null) {
                    continue;
                  }
                  kdbServer.write(new K.KCharacterArray("key `" + add));
                  obj = kdbServer.getResponse();
                  if (obj instanceof K.KSymbolArray) {
                    items = (String[])((K.KSymbolArray)obj).getArray();
                    for (String item : items) {
                      String str = item.trim().length() == 0 ? null : text.trim().length() > 0
                          ? ("." + namespace + "." + item).startsWith(text) ? "." + namespace +
                              "." + item : null : "." + namespace + "." + item;
                      if (str != null) {
                        if (result == null) {
                          result = new TreeMap<String, List<CompletionType>>(
                              String.CASE_INSENSITIVE_ORDER);
                        }
                        if (!result.containsKey(str)) {
                          result.put(str, new ArrayList<CompletionType>(
                              Arrays.asList(new CompletionType[]{CompletionType.DEFINED})));
                        }
                        else {
                          if (!result.get(str).contains(CompletionType.DEFINED)) {
                            result.get(str).add(CompletionType.DEFINED);
                          }
                        }
                      }
                    }
                  }
                }
                if (result != null) {
                  List<CompletionQuery.ResultItem> resultItem = new ArrayList<CompletionQuery.ResultItem>();
                  for (String key : result.keySet()) {
                    for (CompletionType ct : result.get(key)) {
                      resultItem.add(new BooleanAttribItem(key, ct, caret.getDot()-text.length(), text.length()));
                    }
                  }
                  cqResult = new CompletionQuery.DefaultResult(component, 
                      (text.trim().length() > 0 ? text : "All" ) + "...", 
                      resultItem, offset, 0);
                }
              }
            }
          }
          catch (Throwable th) {
          }
          finally {
            if (kdbServer != null) {
              kdbServer.close();
            }
          }
        }
      }
    }
    catch (Throwable th) {
    }
    return cqResult;
  }

  private static abstract class QResultItem implements CompletionQuery.ResultItem {
    String baseText;
    int offset;
    int length;

    public QResultItem(String baseText, int offset, int length) {
      this.baseText = baseText;
      this.offset = offset;
      this.length = length;
    }

    boolean replaceText(JTextComponent component, String text) {
      BaseDocument doc = (BaseDocument)component.getDocument();
      doc.atomicLock();
      try {
        doc.remove(offset, length);
        doc.insertString(offset, text, null);
      }
      catch (BadLocationException exc) {
        return false; // not sucessfull
      }
      finally {
        doc.atomicUnlock();
      }
      return true;
    }

    public boolean substituteCommonText(JTextComponent c, int a, int b, int subLen) {
      return replaceText(c, getItemText().substring(0, subLen));
    }

    public boolean substituteText(JTextComponent c, int a, int b, boolean shift) {
      return replaceText(c, getItemText());
    }

    /** @return Properly colored JLabel with text gotten from <CODE>getPaintText()</CODE>. */
    public java.awt.Component getPaintComponent(javax.swing.JList<?> list, boolean isSelected,
        boolean cellHasFocus) {
      
      javax.swing.JLabel rubberStamp = new javax.swing.JLabel(" " + getPaintText(), getIcon(), JLabel.LEFT);
      rubberStamp.setOpaque(true);

      if (isSelected) {
        rubberStamp.setBackground(list.getSelectionBackground());
        rubberStamp.setForeground(list.getSelectionForeground());
      }
      else {
        rubberStamp.setBackground(list.getBackground());
        rubberStamp.setForeground(getPaintColor());
      }
      return rubberStamp;
    }

    String getPaintText() {
      return getItemText();
    }

    abstract Color getPaintColor();

    public String getItemText() {
      return baseText;
    }
    
    abstract Icon getIcon();
  }

  private static class BooleanAttribItem extends QResultItem {
    CompletionType type;

    public BooleanAttribItem(String name, CompletionType type, int offset, int length) {
      super(name, offset, length);
      this.type = type;
    }

    Color getPaintColor() {
      switch(type) {
        case TABLE:
          return Color.gray.darker();
        case FUNCTION:
          return Color.green.darker();
        case DEFINED:
          return Color.gray;
        case KEYWORD:
          return Color.blue.darker();
        case NAMESPACE:
          return Color.green.darker();
        default:
          return Color.black;          
      }
    }

    public boolean substituteText(JTextComponent c, int a, int b, boolean shift) {
      replaceText(c, shift ? baseText + " " : baseText);
      return false; // always refresh
    }

    Icon getIcon() {
      switch(type) {
        case NAMESPACE:
          return IconsItem.ICON_NAMESPACE;
        case TABLE:
          return IconsItem.ICON_TABLE;
        case FUNCTION:
          return IconsItem.ICON_FUNCTION;
        case DEFINED:
          return IconsItem.ICON_BRACKETS;
        default:
          return IconsItem.ICON_BLANK;          
      }
    }
  }
}
