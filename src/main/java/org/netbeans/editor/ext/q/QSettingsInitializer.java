/*
 * Studio for kdb+ by Charles Skelton is licensed under a Creative Commons
 * Attribution-Noncommercial-Share Alike 3.0 Germany License
 * http://creativecommons.org/licenses/by-nc-sa/3.0 except for the netbeans components which retain
 * their original copyright notice
 */
package org.netbeans.editor.ext.q;

import java.awt.RenderingHints;
import java.util.HashMap;
import java.util.Map;

import org.netbeans.editor.*;
import org.netbeans.editor.example.QKit;

public class QSettingsInitializer extends Settings.AbstractInitializer {
  private static String Q_PREFIX = "q-settings-initializer"; // NOI18N

  public QSettingsInitializer() {
    super(Q_PREFIX);
  }

  public void updateSettingsMap(Class kitClass, Map settingsMap) {
    if (kitClass == BaseKit.class) {
      QTokenColoringInitializer colorInitializer = new QTokenColoringInitializer();
      colorInitializer.updateSettingsMap(kitClass, settingsMap);
    }

    if (kitClass == QKit.class) {
      settingsMap.put(SettingsNames.LINE_NUMBER_VISIBLE, Boolean.TRUE);

      HashMap hints = new HashMap();
      // TODO: Detect if JDK6.0 and, if so, update VALUE_TEXT_ANTIALIAS_ON with other settings
      hints.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
      settingsMap.put(SettingsNames.RENDERING_HINTS, hints);
      SettingsUtil.updateListSetting(settingsMap, SettingsNames.TOKEN_CONTEXT_LIST,
          new TokenContext[]{QTokenContext.context});
    }
  }
}
