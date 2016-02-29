/*
 * Studio for kdb+ by Charles Skelton is licensed under a Creative Commons
 * Attribution-Noncommercial-Share Alike 3.0 Germany License
 * http://creativecommons.org/licenses/by-nc-sa/3.0 except for the netbeans components which retain
 * their original copyright notice
 */

package studio.ui;

import java.awt.Frame;
import java.io.CharArrayWriter;
import java.io.PrintWriter;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class ExceptionGroup extends ThreadGroup {
  public ExceptionGroup() {
    super("ExceptionGroup");
  }

  public void uncaughtException(Thread thread, Throwable cause) {
    CharArrayWriter writer = new CharArrayWriter();
    cause.printStackTrace(new PrintWriter(writer));
    JOptionPane.showMessageDialog(findActiveFrame(),
        "An uncaught exception occurred\nDetails:\n" + writer.toString(), "Exception", JOptionPane.ERROR_MESSAGE);
  }

  private Frame findActiveFrame() {
    Frame[] frames = JFrame.getFrames();
    for (Frame frame : frames) {
      if (frame.isVisible())
        return frame;
    }
    return null;
  }
}
