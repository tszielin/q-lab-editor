package tszielin.qlab.config.data;

import java.util.Comparator;

import tszielin.qlab.config.AppConfig;

public class KdbServiceComparator implements Comparator<KdbService> {

  public KdbServiceComparator() {
  }

  public int compare(KdbService c1, KdbService c2) {
    if (c1 == null && c2 == null) {
      return 0;
    }
    if (c1 == null && c2 != null) {
      return 1;
    }
    if (c1 != null && c2 == null) {
      return -1;
    }
    int port = c1.getPort() - c2.getPort();
    int name = c1.getName() == null ? "".compareTo(c2.getName() == null ? "" : c2.getName())
        : c1.getName().compareTo(c2.getName() == null ? "" : c2.getName());
    int host = c1.getHost().compareTo(c2.getHost());
    int user = c1.getUsername().compareTo(c2.getUsername());
    Sort sort = null;
    try {
      sort = AppConfig.getConfig().getSortType();
    }
    catch (Exception ex) {
      sort = Sort.PORT;
    }
    switch (sort) {      
      case NAME:
        return host == 0 ? 
          (name == 0 ? (port == 0 ? user : port) : name) : host;
      case USER:
        return host == 0 ? 
          (user == 0 ? (port == 0 ? name : port) : user) : host;
      default:
        return host == 0 ?
          (port == 0 ? (name == 0 ? user : name) : port) : host;
    }
  }
}
