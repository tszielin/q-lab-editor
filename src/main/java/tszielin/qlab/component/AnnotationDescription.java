package tszielin.qlab.component;

import java.awt.Image;
import java.awt.Toolkit;

import org.netbeans.editor.AnnotationDesc;
import org.netbeans.editor.AnnotationTypes;

public class AnnotationDescription extends AnnotationDesc {
  private int line;
  
  public AnnotationDescription(int line) {
    super(0, 0);
    this.line = line;
  }

  @Override
  public String getAnnotationType() {
    return AnnotationTypes.PROP_COMBINE_GLYPHS;
  }

  @Override
  public int getLine() {
    return line;
  }

  @Override
  public int getOffset() {
    return 0;
  }

  @Override
  public String getShortDescription() {
    return "Breakpoint";
  }
  
  @Override
  public boolean isVisible() {
    return true;
  }
 
  @Override
  public Image getGlyph() {
    if (getClass().getResource("/org/netbeans/editor/resources/defaultglyph.gif") != null) {
      return Toolkit.getDefaultToolkit().getImage(
          getClass().getResource("/org/netbeans/editor/resources/defaultglyph.gif"));
    }
    return null;
}
}
