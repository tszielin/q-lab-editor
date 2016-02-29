package org.netbeans.editor.ext.q;

import org.netbeans.editor.Formatter;
import org.netbeans.editor.example.QKit;

import tszielin.qlab.config.AppConfig;

public class QFormatter extends Formatter {

  public QFormatter(AppConfig config) {
    super(QKit.class);
    setExpandTabs(config.isExpandTabs());
    if (!config.isExpandTabs()) {
      setTabSize(config.getTabSize());
    }
    else {
      setSpacesPerTab(config.getTabSize() + 1);
    }
  }
}
