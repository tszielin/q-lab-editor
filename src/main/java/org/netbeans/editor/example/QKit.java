package org.netbeans.editor.example;

import javax.swing.text.Document;

import org.netbeans.editor.*;
import org.netbeans.editor.ext.*;
import org.netbeans.editor.ext.q.*;

public class QKit extends ExtKit {
  private static final long serialVersionUID = 2918923094667471438L;

  public QKit() {
    super();
  }

  public String getContentType() {
    return "text/q"; // NOI18N
  }

  public Syntax createSyntax(Document document) {
    return new QSyntax();
  }

  public SyntaxSupport createSyntaxSupport(BaseDocument doc) {
    return new QSyntaxSupport(doc);
  }

  public Completion createCompletion(ExtEditorUI extEditorUI) {
    return new QCompletion(extEditorUI);
  }
}
