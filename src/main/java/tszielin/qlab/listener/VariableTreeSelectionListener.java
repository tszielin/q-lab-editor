package tszielin.qlab.listener;

import javax.swing.JEditorPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

import com.kx.KdbConnection;
import com.kx.kdb.*;
import com.kx.kdb.K.KList;
import com.kx.kdb.K.KType;

public class VariableTreeSelectionListener implements TreeSelectionListener {
  private final JTree tree;
  private final KdbConnection server;
  private final JEditorPane text;
  
  public VariableTreeSelectionListener(JTree tree, KdbConnection server, JEditorPane text) {
    this.tree = tree;
    this.server = server;
    this.text = text;  
  }

  @Override
  public void valueChanged(TreeSelectionEvent  event) {
    DefaultMutableTreeNode node = tree.getLastSelectedPathComponent() instanceof DefaultMutableTreeNode ?
        (DefaultMutableTreeNode)tree.getLastSelectedPathComponent() : null;
    if (node != null && node.isLeaf() && node.getUserObject() instanceof String) {            
      String command = null;
      if (!(node.getParent().isLeaf())) {
        command = (String)((DefaultMutableTreeNode)node.getParent()).getUserObject();
        command = command.equals("<default>") ? "" : command + ".";
      }
      command += (String)node.getUserObject();
      try {
        server.write(new K.KCharacterArray("type " + command));
        Object result = server.getResponse();
        short type = (short)0;
        if (result instanceof K.KShort) {
          type = ((K.KShort)result).getValue();
        }
        server.write(new K.KCharacterArray(command));
        result = server.getResponse();
        if (result instanceof KType<?>) {
          StringBuilder sb = new StringBuilder("<html><body><b><font color=\"blue\">");
          sb.append(command).append("</b></font><br><small>Type: ").append(type).append("h</small><p>");
          String value = ((KType<?>)result).toString(false);
          if (result instanceof KList) {
            sb.append(value.substring(0, 1));
            sb.append(value.substring(value.indexOf(";") + 1));
          }
          else {
            sb.append(value);
          }
          sb.append("</p></html></body>");
          text.setText(sb.toString());
          text.setCaretPosition(0);
        }
      }
      catch (Throwable ex) {
        text.setText(
            "<html><body><b><font color=\"red\">Error:</font></b><p>The server sent the response: " + 
            ex.getMessage() + "</p></html></body>");
      }
    }
    else {
      text.setText(null);
    }
  }  
}
