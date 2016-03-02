package tszielin.qlab.util.image;

import java.awt.*;
import java.awt.image.ImageProducer;
import java.io.*;
import java.net.URL;
import java.util.*;

public class ImageLoader {
  static ImageCache urlImageCache = new ImageCache(32);

  /**
   * Load image from resource
   * @param name resource name (file name, URL)
   * @param component reference to component
   * @return Image loaded image
   * @throws IOException 
   */
  public static Image loadFromResource(String name, Component component) throws IOException {
    return loadFromResource(name, component, component.getClass());
  }

  /**
   * Load image from resource
   * @param name resource name (file, URL)
   * @param cls class name
   * @return Image loaded image
   */
  public static Image loadFromResource(String name, Class<?> cls)  {
    try {
      return loadFromResource(name, null, cls);
    }
    catch(IOException ignored) {
      return null;
    }
  }

  /**
   * Load image from resource
   * @param name image name
   * @param component reference to component
   * @param cls Class class name
   * @return Image
   */
  public static Image loadFromResource(String name, Component component, Class<?> cls)
      throws IOException {
    URL url = cls.getResource(name);
    if (url == null) {
      return null;
    }
    Image image = urlImageCache.get(url.toString());
    if (image != null) {
      return image;
    }
    Object content = url.getContent();
    if (content instanceof Image) {
      image = (Image)content;
    }
    else {
      if (content instanceof ImageProducer) {
        if (component != null) {
          image = component.createImage((ImageProducer)content);
        }
        else {
          image = Toolkit.getDefaultToolkit().createImage((ImageProducer)content);
        }
      }
      else {
        return null;
      }
    }
    if (component != null) {
      component.prepareImage(image, component);
    }
    else {
      Toolkit.getDefaultToolkit().prepareImage(image, -1, -1, component);
    }
    urlImageCache.put(url.toString(), image, component);
    return image;
  }

  /**
   * Wait for image while image is loaded
   * 
   * @param component
   *          reference to component
   * @param image
   *          reference to image
   * @return boolean loaded correctly
   * @throws InterruptedException 
   */
  public static boolean waitForImage(Component component, Image image) throws InterruptedException {
    if (image == null) {
      return false;
    }
    if (image.getWidth(null) > 0) {
      return true;
    }

    MediaTracker tracker = new MediaTracker(component);
    tracker.addImage(image, 1);
    tracker.waitForID(1);
    return !tracker.isErrorID(1);
  }
}

class ImageCache implements Serializable {
  private static final long serialVersionUID = 2314216982965306778L;
  private transient Map<Object,Image> map = new Hashtable<Object,Image>(); // do not Serialize
  private transient Vector<Object> list = new Vector<Object>(); // do not Serialize
  private int limit;

  /**
   * Constrcor with default cache limit
   */
  ImageCache() {
    this(10);
  }

  /**
   * Constructor
   * @param limit int cache limit
   */
  ImageCache(int limit) {
    if (limit <= 0) {
      throw new IllegalArgumentException();
    }
    this.limit = limit;
  }

  /**
   * Set new limit
   * @param newLimit int limit value
   */
  void setLimit(int newLimit) {
    if (newLimit <= 0) {
      throw new IllegalArgumentException();
    }
    int size;
    while ((size = list.size()) > newLimit) {
      Object lastKey = list.elementAt(size - 1);
      list.removeElementAt(size - 1);
      map.remove(lastKey);
    }
    this.limit = newLimit;
  }

  /**
   * Get limit
   * @return int limit value
   */
  int getLimit() {
    return limit;
  }

  /**
   * Put in collection image
   * @param key Object image identifier
   * @param image Image image
   * @param component Component assigned component
   */
  void put(Object key, Image image, Component component) {
    // throw away tail key if cache is too full
    if (limit > 0 && list.size() >= limit) {
      Object lastKey = list.elementAt(list.size() - 1);
      list.removeElementAt(list.size() - 1);
      if (lastKey != null) {
        Image last = map.get(lastKey);
        try {
          ImageLoader.waitForImage(component, last);
        }
        catch(InterruptedException ignored) {          
        }
      }
      map.remove(lastKey);
    }
    map.put(key, image);
    list.insertElementAt(key, 0);
  }

  /**
   * Get image from collection
   * @param key Object image identifier
   * @return Image image
   */
  Image get(Object key) {
    Image image = map.get(key);
    if (image != null) {
      // every access moves key to head to maintain MRU
      list.removeElement(key);
      list.insertElementAt(key, 0);
    }
    return image;
  }
}
