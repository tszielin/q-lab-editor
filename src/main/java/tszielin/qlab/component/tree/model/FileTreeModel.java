package tszielin.qlab.component.tree.model;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.swing.event.TreeModelEvent;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.apache.commons.io.FilenameUtils;

import tszielin.qlab.component.tree.comparator.FileComparator;
import tszielin.qlab.component.tree.comparator.ProjectComparator;
import tszielin.qlab.component.tree.item.FileItem;
import tszielin.qlab.component.tree.item.FileProject;
import tszielin.qlab.config.ProjectConfig;
import tszielin.qlab.config.data.KdbService;
import tszielin.qlab.config.data.Project;

public class FileTreeModel extends TreeModelSupport implements TreeModel, Serializable {
  private static final long serialVersionUID = 2248040977465203611L;
  private List<FileProject> root;
  private ProjectConfig config;
  private static final FileComparator FILE_COMPARATOR = new FileComparator();
  private static final ProjectComparator PROJECT_COMPARATOR = new ProjectComparator();

  public FileTreeModel(ProjectConfig config) {
    this.config = config;
    if (this.config != null) {
      List<Project> list = this.config.getProjects();
      if (list != null) {
        for (Project item : list) {
          if (item != null && item.getPath() != null && item.getPath().exists() &&
              item.getPath().isDirectory()) {
            addRoot(item);
          }
        }
      }
      else {
        this.root = new ArrayList<FileProject>();
      }
    }
  }
  
  public Object getRoot() {   
    return root;
  }
  
  public void addRoot(Project project) {
    if (this.root == null) {
      this.root = new ArrayList<FileProject>();
    }
    this.root.add(new FileProject(project));
    fireTreeStructureChanged(new TreeModelEvent(this, new TreePath(getRoot())));
  }
  
  public void removeRoot(Project project) {
    if (this.root == null || project == null) {
      return;
    }
    FileProject remove = null;
    for (FileProject fp : this.root) {
      if (project.equals(fp.getProject())) {
        remove = fp;
        break;
      }
    }
    if (remove != null) {
      this.root.remove(remove);
      fireTreeStructureChanged(new TreeModelEvent(this, new TreePath(getRoot())));
    }
  }

  public Object getChild(Object parent, int index) {
    Object[] array = getChildren(parent);
    if (array == null) {
      return new Object();
    }
    index = index >= array.length ? array.length - 1 : index;
    return array[index];
  }
  
  @SuppressWarnings("unchecked")
  protected Object[] getChildren(Object parent) {
    if (parent instanceof List<?>) {
      Collections.sort((List<FileProject>)parent, PROJECT_COMPARATOR);
      return ((List<?>)parent).toArray();
    }
    File file = null;
    KdbService connection = null;
    if (parent instanceof FileProject) {
      file = (FileProject)parent;
      if (((FileProject)parent).getProject() != null &&
          ((FileProject)parent).getProject().getConnection() != null) {
        connection = ((FileProject)parent).getProject().getConnection();
      }
    }
    if (parent instanceof FileItem) {
      file = (File)parent;
    }
    if (file != null) {
      if (file.isDirectory()) {        
        File[] files = file.listFiles(config.getFilters());
        List<FileItem> list = new ArrayList<FileItem>();
        for (File f : files) {
          if (canShow(f)) {
            list.add(new FileItem(FilenameUtils.separatorsToUnix(f.getPath()), connection));
          }
        }
//        for (int pos = 0; pos < files.length; pos++) {
//          System.out.println(files[pos]);
//          if ((files[pos].isFile() && !files[pos].getName().startsWith(".cvs")) || 
//              (files[pos].isDirectory() && 
//                  (files[pos].listFiles(config.getFilters()).length != 0 && 
//                      !(files[pos].getName().equalsIgnoreCase(".svn") || 
//                          files[pos].getName().equals("CVS"))))) {
//            list.add(new FileItem(files[pos].getPath(), connection));
//          }
//        }
        files = list.toArray(new File[0]);
        Arrays.sort(files, FILE_COMPARATOR);
        return files;
      }
    }
    return new File[0];
  }

  public int getChildCount(Object parent) {
    File file = null;
    if (parent instanceof File) {
      file = ((File)parent);
    }
    else {
        if (parent instanceof List<?>) {
          return ((List<?>)parent).size();
        }
    }
    if (file != null) {
      if (file.isDirectory()) {
        List<File> list = new ArrayList<File>(Arrays.asList(file.listFiles(config.getFilters())));
        Iterator<File> iter = list.iterator();
        while (iter.hasNext()) {
          if (!canShow(iter.next())) {
            iter.remove();
          }
        }
        return list.size();
      }
    }
    return 0;
  }

  public boolean isLeaf(Object node) {
    return node instanceof List<?> ? false : 
      node instanceof File ? ((File)node).isFile() : false;
  }

  public void valueForPathChanged(TreePath path, Object newValue) {
  }

  public int getIndexOfChild(Object parent, Object child) {
    return 0;
  }
  
  public boolean canShow(File file) {
    if (file.isFile()) {
      return !file.getName().startsWith(".cvs") ;
    }
    else {
      if (file.isDirectory()) {
        if (file.getName().equalsIgnoreCase(".svn") || file.getName().equals("CVS")) {
          return false;
        }
        else {
          File[] files = file.listFiles(config.getFilters());
          if (files.length == 0) {
            return false;
          }
          else {
            boolean show = false;
            for (File f : files) {
              show |= canShow(f);
            }
            return show;
          }
        }
      }
    }
    return false;
  }
}