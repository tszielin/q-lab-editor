/*
 * Studio for kdb+ by Charles Skelton is licensed under a Creative Commons
 * Attribution-Noncommercial-Share Alike 3.0 Germany License
 * http://creativecommons.org/licenses/by-nc-sa/3.0 except for the netbeans components which retain
 * their original copyright notice
 */

package studio.ui;

public class QuitHandler {
  private Studio s;

  public QuitHandler(Studio s) {
    this.s = s;
  }

  public boolean quit() {
    return s.quit();
  }
}
