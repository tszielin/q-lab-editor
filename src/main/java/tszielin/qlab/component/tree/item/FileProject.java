package tszielin.qlab.component.tree.item;

import java.io.File;

import javax.swing.Icon;

import tszielin.qlab.component.Iconable;
import tszielin.qlab.component.Tooltipable;
import tszielin.qlab.config.data.Project;

public class FileProject extends File implements Tooltipable, Iconable {
  private static final long serialVersionUID = 982141883318299486L;
  private Project project;
  
  public FileProject(Project project) {
    super(project.getPath().getPath());
    this.project = project;
  }

  public String getTooltip() {
    return project != null ? project.getTooltip() : null;
  }

  public Icon getIcon() {
    return project != null ? project.getIcon() : null;
  }

  public boolean isClosed() {
    return project == null ? true : project.isClosed();
  }

  public void setClosed(boolean closed) {
    if (project != null) {
      this.project.setClosed(closed);
    }
  }
  
  public Project getProject() {
    return project;
  }
}
