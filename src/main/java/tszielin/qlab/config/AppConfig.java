package tszielin.qlab.config;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.LookAndFeel;
import javax.swing.UIManager;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.io.FilenameUtils;

import studio.kdb.Config;
import studio.kdb.Server;
import tszielin.qlab.config.data.ConnectionStatus;
import tszielin.qlab.config.data.DataType;
import tszielin.qlab.config.data.EditorFile;
import tszielin.qlab.config.data.KdbService;
import tszielin.qlab.config.data.KdbServiceComparator;
import tszielin.qlab.config.data.Sort;
import tszielin.qlab.error.ArgumentException;
import tszielin.qlab.kdb.AuthenticationType;
import tszielin.qlab.util.config.XMLConfig;
import tszielin.qlab.util.error.ConfigException;

public class AppConfig extends XMLConfig {
  private static AppConfig instance;

  private final static String FILENAME = "q-lab.xml";

  protected AppConfig() throws ConfigException, ArgumentException {
    super(System.getProperty("user.home") + "/.q-lab", FILENAME, false, true, 60000);
    if (configuration.isEmpty() &&
        (new File(System.getProperty("user.home") + "/.studioforkdb/studio.properties")).exists()) {
      convert();
    }
    if (configuration.isEmpty()) {
      init();
    }
  }

  public static AppConfig getConfig() throws ConfigException, ArgumentException {
    if (instance == null) {
      instance = new AppConfig();
    }
    return instance;
  }

  public boolean canReload() {
    return true;
  }

  public boolean isManual() {
    return false;
  }

  @Override
  protected String getFileName() {
    return FILENAME;
  }

  private String getFontName() {
    return configuration.getString("editor.font[@name]", "Monospaced");
  }

  private void setFontName(String name) throws ConfigurationException {
    if (name != null && name.trim().length() > 0) {
      configuration.setProperty("editor.font[@name]", name);
    }
    else {
      configuration.clearProperty("editor.font[@name]");
    }
    configuration.save();
  }

  private int getFontSize() {
    return configuration.getInt("editor.font[@size]", 14);
  }

  private void setFontSize(int size) throws ConfigurationException {
    if (size > 0 && size != 14) {
      configuration.setProperty("editor.font[@size]", Integer.valueOf(size));
    }
    else {
      configuration.clearProperty("editor.font[@size]");
    }
    configuration.save();
  }

  public Font getFont() {
    return new Font(getFontName(), Font.PLAIN, getFontSize());
  }

  public void setFont(Font font) throws ConfigException {
    if (font != null) {
      try {
        setFontName(font.getFamily());
        setFontSize(font.getSize());
      }
      catch (ConfigurationException ex) {
        throw new ConfigException(ex);
      }
    }
  }

  public int getTabSize() {
    return configuration.getInt("editor.tab[@size]", 2);
  }

  public void setTabSize(int size) throws ConfigException {
    if (size > 0 && size != 2) {
      configuration.setProperty("editor.tab[@size]", Integer.valueOf(size));
    }
    else {
      configuration.clearProperty("editor.tab[@size]");
    }
    try {
      configuration.save();
    }
    catch (ConfigurationException ex) {
      throw new ConfigException(ex);
    }
  }

  public boolean isExpandTabs() {
    return configuration.getBoolean("editor.tab[@expand]", false);
  }

  public void setExpandTabs(boolean expand) throws ConfigException {
    if (expand) {
      configuration.setProperty("editor.tab[@expand]", Boolean.valueOf(expand));
    }
    else {
      configuration.clearProperty("editor.tab[@expand]");
    }
    try {
      configuration.save();
    }
    catch (ConfigurationException ex) {
      throw new ConfigException(ex);
    }
  }

  public boolean isMatchingBrackets() {
    return configuration.getBoolean("editor.brackets[@match]", false);
  }

  public void setMatchingBrackets(boolean match) throws ConfigException {
    if (match) {
      configuration.setProperty("editor.brackets[@match]", Boolean.valueOf(match));
    }
    else {
      configuration.clearProperty("editor.brackets[@match]");
    }
    try {
      configuration.save();
    }
    catch (ConfigurationException ex) {
      throw new ConfigException(ex);
    }
  }

  public boolean isUnixEOL() {
    return configuration.getBoolean("editor.unix[@eol]", false);
  }

  public void setUnixEOL(boolean unix) throws ConfigException {
    if (unix) {
      configuration.setProperty("editor.unix[@eol]", Boolean.valueOf(unix));
    }
    else {
      configuration.clearProperty("editor.unix[@eol]");
    }
    try {
      configuration.save();
    }
    catch (ConfigurationException ex) {
      throw new ConfigException(ex);
    }
  }

  public boolean isMargin() {
    return configuration.getBoolean("editor.margin[@show]", true);
  }

  public void setMargin(boolean show) throws ConfigException {
    if (!show) {
      configuration.setProperty("editor.margin[@show]", Boolean.FALSE);
    }
    else {
      configuration.clearProperty("editor.margin[@show]");
    }
    try {
      configuration.save();
    }
    catch (ConfigurationException ex) {
      throw new ConfigException(ex);
    }
  }

  public Integer getMargin() {
    return configuration.getInteger("editor.margin[@pos]", Integer.valueOf(80));
  }

  public void setMargin(int margin) throws ConfigException {
    if (margin >= 40 && margin <= 160) {
      configuration.setProperty("editor.margin[@pos]", Integer.valueOf(margin));
    }
    try {
      configuration.save();
    }
    catch (ConfigurationException ex) {
      throw new ConfigException(ex);
    }
  }

  public String getLookAndFeel() {
    return configuration.getString("global.lookAndFeel[@name]", UIManager.getSystemLookAndFeelClassName());
  }

  public void setLookAndFeel(String lookAndFeel) throws ConfigException {
    if (lookAndFeel != null && !"javax.swing.plaf.metal.MetalLookAndFeel".equals(lookAndFeel)) {
      configuration.setProperty("global.lookAndFeel[@name]", lookAndFeel);
    }
    else {
      configuration.clearProperty("global.lookAndFeel[@name]");
    }
    try {
      configuration.save();
    }
    catch (ConfigurationException ex) {
      throw new ConfigException(ex);
    }
  }

  public boolean isMaximized() {
    return configuration.getBoolean("global.window[@maximized]", false);
  }

  public void setMaximized(boolean maximized) throws ConfigException {
    if (maximized) {
      configuration.setProperty("global.window[@maximized]", Boolean.valueOf(maximized));
    }
    else {
      configuration.clearProperty("global.window[@maximized]");
    }
    try {
      configuration.save();
    }
    catch (ConfigurationException ex) {
      throw new ConfigException(ex);
    }
  }

  public void setLookAndFeel(Class<LookAndFeel> lookAndFeel) throws ConfigException {
    setLookAndFeel(lookAndFeel != null ? lookAndFeel.getCanonicalName() : null);
  }

  // @SuppressWarnings("unchecked")
  // public List<String> getKeywords() {
  // return configuration.getList("q.keyword");
  // }
  //
  // public void setKeywords(String[] keywords) throws ConfigException {
  // setKeywords(keywords != null ? Arrays.asList(keywords) : null);
  // }
  //
  // public void setKeywords(List<String> keywords) throws ConfigException {
  // if (keywords != null && !keywords.isEmpty()) {
  // Collections.sort(keywords);
  // configuration.setProperty("q.keyword", keywords);
  // }
  // else {
  // configuration.clearProperty("q.keyword");
  // }
  // try {
  // configuration.save();
  // }
  // catch (ConfigurationException ex) {
  // throw new ConfigException(ex);
  // }
  // }

  @SuppressWarnings("unchecked")
  public List<String> getTokens() {
    List<String> tokens = null;
    List<HierarchicalConfiguration> fields = configuration.configurationsAt("tokens.token");
    if (fields != null) {
      for (HierarchicalConfiguration field : fields) {
        if (field.getString("[@name]") != null) {
          if (tokens == null) {
            tokens = new ArrayList<String>();
          }
          tokens.add(field.getString("[@name]"));
        }
      }
    }
    if (tokens != null && !tokens.isEmpty()) {
      Collections.sort(tokens);
    }
    return tokens;
  }

  @SuppressWarnings("unchecked")
  private HierarchicalConfiguration getTokenItem(String name) {
    if (name == null || name.trim().length() == 0) {
      return null;
    }
    List<HierarchicalConfiguration> fields = configuration.configurationsAt("tokens.token");
    if (fields != null) {
      for (HierarchicalConfiguration field : fields) {
        if (name.equalsIgnoreCase(field.getString("[@name]"))) {
          return field;
        }
      }
    }
    return null;
  }

  @SuppressWarnings("unchecked")
  public Color getTokenColor(int type) {
    List<HierarchicalConfiguration> fields = configuration.configurationsAt("tokens.token");
    if (fields != null) {
      for (HierarchicalConfiguration field : fields) {
        if (type == field.getInt("[@type]", 0)) {
          return new Color(field.getInt("[@value]", UIManager.getColor("Table.foreground").getRGB()));
        }
      }
    }
    return UIManager.getColor("Table.foreground");
  }

  public Color getTokenColor(String token) {
    HierarchicalConfiguration field = getTokenItem(token);
    return field != null ? new Color(field.getInt("[@value]", Color.black.getRGB())) : Color.black;
  }

  public Color getTokenColor(String token, Color defaultColor) {
    HierarchicalConfiguration field = getTokenItem(token);
    return field != null ? new Color(field.getInt("[@value]", defaultColor.getRGB())) : defaultColor;
  }

  private void setTokenColor(String token, int value) throws ConfigException {
    if (token != null && !token.trim().isEmpty()) {
      HierarchicalConfiguration field = getTokenItem(token);
      if (field != null) {
        field.setProperty("[@value]", value);
      }
      else {
        configuration.setProperty("tokens.token(-1)[@name]", token.toUpperCase());
        configuration.setProperty("tokens.token[@value](-1)", value);
        if ("boolean".equalsIgnoreCase(token)) {
          configuration.setProperty("tokens.token[@type](-1)", -1);
        }
        else {
          if ("byte".equalsIgnoreCase(token)) {
            configuration.setProperty("tokens.token[@type](-1)", -4);
          }
          else {
            if ("short".equalsIgnoreCase(token)) {
              configuration.setProperty("tokens.token[@type](-1)", -5);
            }
            else {
              if ("integer".equalsIgnoreCase(token)) {
                configuration.setProperty("tokens.token[@type](-1)", -6);
              }
              else {
                if ("long".equalsIgnoreCase(token)) {
                  configuration.setProperty("tokens.token[@type](-1)", -7);
                }
                else {
                  if ("real".equalsIgnoreCase(token)) {
                    configuration.setProperty("tokens.token[@type](-1)", -8);
                  }
                  else {
                    if ("float".equalsIgnoreCase(token)) {
                      configuration.setProperty("tokens.token[@type](-1)", -9);
                    }
                    else {
                      if ("charvector".equalsIgnoreCase(token)) {
                        configuration.setProperty("tokens.token[@type](-1)", 10);
                      }
                      else {
                        if ("symbol".equalsIgnoreCase(token)) {
                          configuration.setProperty("tokens.token[@type](-1)", -11);
                        }
                        else {
                          if ("month".equalsIgnoreCase(token)) {
                            configuration.setProperty("tokens.token[@type](-1)", -13);
                          }
                          else {
                            if ("date".equalsIgnoreCase(token)) {
                              configuration.setProperty("tokens.token[@type](-1)", -14);
                            }
                            else {
                              if ("datetime".equalsIgnoreCase(token)) {
                                configuration.setProperty("tokens.token[@type](-1)", -15);
                              }
                              else {
                                if ("minute".equalsIgnoreCase(token)) {
                                  configuration.setProperty("tokens.token[@type](-1)", -17);
                                }
                                else {
                                  if ("second".equalsIgnoreCase(token)) {
                                    configuration.setProperty("tokens.token[@type](-1)", -18);
                                  }
                                  else {
                                    if ("time".equalsIgnoreCase(token)) {
                                      configuration.setProperty("tokens.token[@type](-1)", -19);
                                    }
                                    else {
                                      if ("timestamp".equalsIgnoreCase(token)) {
                                        configuration.setProperty("tokens.token[@type](-1)", -12);
                                      }
                                      else {
                                        if ("timespan".equalsIgnoreCase(token)) {
                                          configuration.setProperty("tokens.token[@type](-1)", -16);
                                        }
                                      }
                                    }
                                  }
                                }
                              }
                            }
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
    try {
      configuration.save();
    }
    catch (ConfigurationException ex) {
      throw new ConfigException(ex);
    }
  }

  public void setTokenColor(String token, Color color) throws ConfigException {
    setTokenColor(token, color.getRGB());
  }

  @SuppressWarnings("unchecked")
  private HierarchicalConfiguration getService(String host, int port, String username) {
    if (host == null || host.trim().length() == 0) {
      return null;
    }
    if (port < 1 || port > 65535) {
      return null;
    }
    username = username == null || username.trim().isEmpty() ? System.getProperty("user.name") : username;

    List<HierarchicalConfiguration> fields = configuration.configurationsAt("connections.connection");
    if (fields != null) {
      for (HierarchicalConfiguration field : fields) {
        if (host.equals(field.getString("[@host]")) && port == field.getInt("[@port]", 0) &&
            username.equals(field.getString("[@username]"))) {
          return field;
        }
      }
    }
    return null;
  }

  public KdbService getKdbService(String host, int port, String username) throws ArgumentException {
    HierarchicalConfiguration hc = getService(host, port, username);
    return hc != null ? new KdbService(hc) : null;
  }

  public void removeKdbService(KdbService connection) throws ConfigException {
    if (connection != null) {
      HierarchicalConfiguration field = getService(connection.getHost(), connection.getPort(),
          connection.getUsername());
      if (field != null) {
        field.clear();
        try {
          configuration.save();
        }
        catch (ConfigurationException ex) {
          throw new ConfigException(ex);
        }
      }
    }
  }

  public void setKdbService(KdbService connection) throws ConfigException {
    if (connection != null) {
      HierarchicalConfiguration field = getService(connection.getHost(), connection.getPort(),
          connection.getUsername());
      if (field != null) {
        field.setProperty("[@host]", connection.getHost());
        field.setProperty("[@port]", connection.getPort());
        if (connection.getName() != null &&
            !(connection.getName().equals(connection.getHost()) || connection.getName().equals(
                connection.getHost() + ":" + String.valueOf(connection.getPort())))) {
          field.setProperty("[@name]", connection.getName());
        }
        else {
          field.clearProperty("[@name]");
        }
        field.setProperty("[@username]", connection.getUsername());
        if (connection.getPassword() != null) {
          field.setProperty("[@password]", connection.getPassword());
        }
        else {
          field.clearProperty("[@password]");
        }
        if (connection.getType() != AuthenticationType.USERNAME_AND_PASSWORD) {
          field.setProperty("[@auth]", connection.getType().name());
        }
        else {
          field.clearProperty("[@auth]");
        }
        if (connection.getParams() != null && connection.getParams().length() > 0) {
          field.setProperty("[@params]", connection.getParams());
        }
        else {
          field.clearProperty("[@params]");
        }
        if (connection.getTitleColor() != null && connection.getTitleColor().getRGB() != Color.BLACK.getRGB()) {
          field.setProperty("[@title]", connection.getTitleColor().getRGB());
        }
        else {
          field.clearProperty("[@title]");
        }
        if (connection.getParams() != null && connection.getParams().trim().length() > 0) {
          field.setProperty("[@params]", connection.getParams());
        }
        else {
          field.clearProperty("[@title]");
        }
      }
      else {
        configuration.setProperty("connections.connection(-1)[@host]", connection.getHost());
        configuration.setProperty("connections.connection[@port](-1)", connection.getPort());
        if (connection.getName() != null &&
            !(connection.getName().equals(connection.getHost()) || connection.getName().equals(
                connection.getHost() + ":" + String.valueOf(connection.getPort())))) {
          configuration.setProperty("connections.connection[@name](-1)", connection.getName());
        }
        configuration.setProperty("connections.connection[@username](-1)", connection.getUsername());
        if (connection.getPassword() != null) {
          configuration.setProperty("connections.connection[@password](-1)", connection.getPassword());
        }
        if (connection.getType() != AuthenticationType.USERNAME_AND_PASSWORD) {
          configuration.setProperty("connections.connection[@auth](-1)", connection.getType().name());
        }
        if (connection.getTitleColor() != null && connection.getTitleColor().getRGB() != Color.BLACK.getRGB()) {
          configuration.setProperty("connections.connection[@title](-1)", connection.getTitleColor().getRGB());
        }
        if (connection.getParams() != null && connection.getParams().length() > 0) {
          configuration.setProperty("connections.connection[@params](-1)", connection.getParams());
        }
      }
      try {
        configuration.save();
      }
      catch (ConfigurationException ex) {
        throw new ConfigException(ex);
      }
    }
  }

  public void setKdbService(String name, String host, int port, String username, String password,
      AuthenticationType type, int titleColor) throws ConfigException, ArgumentException {
    setKdbService(new KdbService(name, host, port, username, password, type, titleColor));
  }

  public void setKdbService(String name, String host, int port, String username, String password,
      AuthenticationType type, Color titleColor) throws ConfigException, ArgumentException {
    setKdbService(new KdbService(name, host, port, username, password, type, titleColor));
  }

  public void setKdbService(String name, String host, int port, String username, String password,
      AuthenticationType type) throws ConfigException, ArgumentException {
    setKdbService(new KdbService(name, host, port, username, password, type));
  }

  public void setKdbService(String name, String host, int port, String username, String password)
      throws ConfigException, ArgumentException {
    setKdbService(new KdbService(name, host, port, username, password));
  }

  public void setKdbService(String name, String host, int port, String username) throws ConfigException,
      ArgumentException {
    setKdbService(new KdbService(name, host, port, username));
  }

  public void setKdbService(String name, String host, int port) throws ConfigException, ArgumentException {
    setKdbService(new KdbService(name, host, port));
  }

  public void setKdbService(String host, int port) throws ConfigException, ArgumentException {
    setKdbService(new KdbService(host, port));
  }

  @SuppressWarnings("unchecked")
  public Collection<String> getHosts() {
    Object property = configuration.getProperty("connections.connection[@host]");
    List<String> list = null;
    if (property instanceof String) {
      list = new ArrayList<String>();
      list.add((String)property);
    }
    else {
      if (property instanceof Collection) {
        List<String> hosts = new Vector<String>((Collection<String>)property);
        if (hosts != null && !hosts.isEmpty()) {
          Collections.sort(hosts);
          list = new ArrayList<String>();
          for (String host : hosts) {
            if (!list.contains(host)) {
              list.add(host);
            }
          }
        }
      }
    }
    return list;
  }

  @SuppressWarnings("unchecked")
  public Set<KdbService> getConnections(String host) {
    Set<KdbService> set = new TreeSet<KdbService>(new KdbServiceComparator());
    if (host == null || host.trim().length() == 0) {
      return set;
    }
    List<HierarchicalConfiguration> fields = configuration.configurationsAt("connections.connection");
    if (fields != null) {
      for (HierarchicalConfiguration field : fields) {
        if (host.equals(field.getString("[@host]"))) {
          try {
            set.add(new KdbService(field));
          }
          catch (ArgumentException ignored) {
          }
        }
      }
    }
    return set;
  }

  @SuppressWarnings("unchecked")
  public Set<KdbService> getConnections() {
    Set<KdbService> set = new TreeSet<KdbService>(new KdbServiceComparator());
    List<HierarchicalConfiguration> fields = configuration.configurationsAt("connections.connection");
    if (fields != null) {
      for (HierarchicalConfiguration field : fields) {
        try {
          set.add(new KdbService(field));
        }
        catch (ArgumentException ignored) {
        }
      }
    }
    return set;
  }

  @SuppressWarnings("unchecked")
  public List<EditorFile> getOpenedFiles() {
    List<EditorFile> list = new ArrayList<EditorFile>();
    List<HierarchicalConfiguration> fields = configuration.configurationsAt("files.opened");
    if (fields != null) {
      for (HierarchicalConfiguration field : fields) {
        String name = field.getString("[@file]");
        if (name != null && name.trim().length() > 0) {
          KdbService connection = null;
          try {
            connection = getKdbService(field.getString("[@host]"), field.getInt("[@port]", -1),
                field.getString("[@username]"));
            ConnectionStatus status = null;
            try {
              status = ConnectionStatus.valueOf(field.getString("[@status]", "NOT_CONNECTED"));
            }
            catch (Exception ex) {
              status = ConnectionStatus.NOT_CONNECTED;
            }
            if (connection != null) {
              connection.setStatus(status);
            }
          }
          catch (ArgumentException ignored) {
          }
          list.add(new EditorFile(new File(name), connection));
        }
      }
    }
    return list;
  }

  @SuppressWarnings("unchecked")
  private HierarchicalConfiguration getOpened(String filename) {
    if (filename == null || filename.trim().length() == 0) {
      return null;
    }
    List<HierarchicalConfiguration> fields = configuration.configurationsAt("files.opened");
    if (fields != null) {
      for (HierarchicalConfiguration field : fields) {
        if (filename.equals(field.getString("[@file]"))) {
          return field;
        }
      }
    }
    return null;
  }

  public EditorFile getOpenedFile(String filename) {
    HierarchicalConfiguration field = getOpened(filename);
    if (field != null) {
      String name = field.getString("[@file]");
      if (name != null && name.trim().length() > 0) {
        KdbService connection = null;
        try {
          connection = getKdbService(field.getString("[@host]"), field.getInt("[@port]", -1),
              field.getString("[@username]"));
          ConnectionStatus status = null;
          try {
            status = ConnectionStatus.valueOf(field.getString("[@status]", "NOT_CONNECTED"));
          }
          catch (Exception ex) {
            status = ConnectionStatus.NOT_CONNECTED;
          }
          if (connection != null) {
            connection.setStatus(status);
          }
        }
        catch (ArgumentException ignored) {
        }
        return new EditorFile(new File(name), connection);
      }
    }
    return null;
  }

  @SuppressWarnings("unchecked")
  public void setOpenedFiles(List<EditorFile> files) throws ConfigException {
    if (files == null) {
      return;
    }
    List<HierarchicalConfiguration> fields = configuration.configurationsAt("files.opened");
    if (fields != null) {
      for (HierarchicalConfiguration field : fields) {
        field.clear();
      }
    }
    for (EditorFile file : files) {
      if (file != null && file.getFile().exists()) {
        HierarchicalConfiguration field = getOpened(file.getPath());
        if (field != null) {
          field.setProperty("[@file]", file.getPath());
          if (file.getConnection() == null) {
            field.clearProperty("[@host]");
            field.clearProperty("[@port]");
            field.clearProperty("[@username]");
            field.clearProperty("[@status]");
          }
          else {
            if (file.getConnection().getHost() == null || file.getConnection().getHost().trim().length() == 0) {
              field.clearProperty("[@host]");
              field.clearProperty("[@port]");
              field.clearProperty("[@username]");
              field.clearProperty("[@status]");
            }
            else {
              field.setProperty("[@host]", file.getConnection().getHost());
              field.setProperty("[@port]", file.getConnection().getPort());
              field.setProperty("[@username]", file.getConnection().getUsername());
              field.setProperty("[@status]", file.getConnection().getUsername());
            }
          }
        }
        else {
          configuration.setProperty("files.opened(-1)[@file]", file.getPath());
          if (file.getConnection() != null) {
            configuration.setProperty("files.opened[@host](-1)", file.getConnection().getHost());
            configuration.setProperty("files.opened[@port](-1)", file.getConnection().getPort());
            configuration.setProperty("files.opened[@username](-1)", file.getConnection().getUsername());
            configuration.setProperty("files.opened[@status](-1)", file.getConnection().getUsername());
          }
        }
        try {
          configuration.save();
        }
        catch (ConfigurationException ex) {
          throw new ConfigException(ex);
        }
      }
    }
  }

  public void addOpenedFile(EditorFile file) throws ConfigException {
    if (file == null || file.getFile() == null || !file.getFile().exists()) {
      return;
    }
    List<EditorFile> list = getOpenedFiles();
    if (list.contains(file)) {
      list.remove(file);
    }
    while (list.size() > 10) {
      list.remove(list.size() - 1);
    }
    list.add(0, file);
    setOpenedFiles(list);
  }

  @SuppressWarnings("unchecked")
  public List<EditorFile> getLastFiles() {
    List<EditorFile> list = new ArrayList<EditorFile>();
    List<HierarchicalConfiguration> fields = configuration.configurationsAt("files.last");
    if (fields != null) {
      for (HierarchicalConfiguration field : fields) {
        String name = field.getString("[@file]");
        if (name != null && name.trim().length() > 0) {
          KdbService connection = null;
          try {
            connection = getKdbService(field.getString("[@host]"), field.getInt("[@port]", -1),
                field.getString("[@username]"));
            ConnectionStatus status = null;
            try {
              status = ConnectionStatus.valueOf(field.getString("[@status]", "NOT_CONNECTED"));
            }
            catch (Exception ex) {
              status = ConnectionStatus.NOT_CONNECTED;
            }
            if (connection != null) {
              connection.setStatus(status);
            }
          }
          catch (ArgumentException ignored) {
          }
          list.add(new EditorFile(new File(name), connection, field.getBoolean("[@active]", false)));
        }
      }
    }
    return list;
  }

  @SuppressWarnings("unchecked")
  private HierarchicalConfiguration getLast(String filename) {
    if (filename == null || filename.trim().length() == 0) {
      return null;
    }
    List<HierarchicalConfiguration> fields = configuration.configurationsAt("files.last");
    if (fields != null) {
      for (HierarchicalConfiguration field : fields) {
        if (filename.equals(field.getString("[@file]"))) {
          return field;
        }
      }
    }
    return null;
  }

  @SuppressWarnings("unchecked")
  public void setLastFiles(List<EditorFile> files) throws ConfigException {
    List<HierarchicalConfiguration> fields = configuration.configurationsAt("files.last");
    if (fields != null) {
      for (HierarchicalConfiguration field : fields) {
        field.clear();
      }
    }
    if (files == null) {
      return;
    }
    for (EditorFile file : files) {
      if (file != null && file.getFile().exists()) {
        HierarchicalConfiguration field = getLast(file.getPath());
        if (field != null) {
          field.setProperty("[@file]", file.getPath());
          if (file.isActive()) {
            configuration.setProperty("files.last[@active](-1)", Boolean.TRUE);
          }
          if (file.getConnection() == null) {
            field.clearProperty("[@host]");
            field.clearProperty("[@port]");
            field.clearProperty("[@username]");
            field.clearProperty("[@status]");
          }
          else {
            if (file.getConnection().getHost() == null || file.getConnection().getHost().trim().isEmpty()) {
              field.clearProperty("[@host]");
              field.clearProperty("[@port]");
              field.clearProperty("[@username]");
              field.clearProperty("[@status]");
            }
            else {
              field.setProperty("[@host]", file.getConnection().getHost());
              field.setProperty("[@port]", file.getConnection().getPort());
              field.setProperty("[@username]", file.getConnection().getUsername());
              field.setProperty("[@status]", file.getConnection().getStatus());
            }
          }
        }
        else {
          configuration.setProperty("files.last(-1)[@file]", file.getPath());
          if (file.isActive()) {
            configuration.setProperty("files.last[@active](-1)", Boolean.TRUE);
          }
          if (file.getConnection() != null) {
            configuration.setProperty("files.last[@host](-1)", file.getConnection().getHost());
            configuration.setProperty("files.last[@port](-1)", file.getConnection().getPort());
            configuration.setProperty("files.last[@username](-1)", file.getConnection().getUsername());
            configuration.setProperty("files.last[@status](-1)", file.getConnection().getStatus());
          }
        }
      }
    }
    try {
      configuration.save();
    }
    catch (ConfigurationException ex) {
      throw new ConfigException(ex);
    }
  }

  public void addLastFile(EditorFile file) throws ConfigException {
    if (file == null || file.getFile() == null || !file.getFile().exists()) {
      return;
    }
    List<EditorFile> list = getLastFiles();
    if (list.contains(file)) {
      list.remove(file);
    }
    while (list.size() > 10) {
      list.remove(list.size() - 1);
    }
    list.add(0, file);
    setLastFiles(list);
  }

  public String getCurrentPath() {
    return configuration.getString("files.path");
  }

  public void setCurrentPath(String path) throws ConfigException {
    configuration.setProperty("files.path", path);
    try {
      configuration.save();
    }
    catch (ConfigurationException ex) {
      throw new ConfigException(ex);
    }
  }

  public void setAutoSavePath(String path) throws ConfigException {
    if (path == null || path.trim().length() == 0 ||
        FilenameUtils.separatorsToUnix(System.getProperty("user.home") + "/.q-lab/.autosave").equals(path)) {
      configuration.clearProperty("files.autoSave[@path]");
    }
    else {
      configuration.setProperty("files.autoSave[@path]", path);
    }
    try {
      configuration.save();
    }
    catch (ConfigurationException ex) {
      throw new ConfigException(ex);
    }
  }

  public String getAutoSavePath() {
    return FilenameUtils.separatorsToUnix(configuration.getString("files.autoSave[@path]",
        System.getProperty("user.home") + "/.studioforkdb/.autosave"));
  }

  public Boolean isAutoSave() {
    return configuration.getBoolean("files.autoSave[@save]", Boolean.TRUE);
  }

  public void setAutoSave(boolean show) throws ConfigException {
    if (!show) {
      configuration.setProperty("files.autoSave[@save]", Boolean.FALSE);
    }
    else {
      configuration.clearProperty("files.autoSave[@save]");
    }
    try {
      configuration.save();
    }
    catch (ConfigurationException ex) {
      throw new ConfigException(ex);
    }
  }

  public void setAutoSaveTime(int time) throws ConfigException {
    if (time == 2 || time < 0 || time > 60) {
      configuration.clearProperty("files.autoSave[@time]");
    }
    else {
      configuration.setProperty("files.autoSave[@time]", time * 60000);
    }
    try {
      configuration.save();
    }
    catch (ConfigurationException ex) {
      throw new ConfigException(ex);
    }
  }

  public long getAutoSaveTime() {
    return configuration.getLong("files.autoSave[@time]", 120000);
  }

  @SuppressWarnings("unchecked")
  private HierarchicalConfiguration getFormatField(DataType type) {
    if (type == null) {
      return null;
    }
    List<HierarchicalConfiguration> fields = configuration.configurationsAt("grid.format");
    if (fields != null) {
      for (HierarchicalConfiguration field : fields) {
        DataType dataType = null;
        try {
          dataType = DataType.valueOf(field.getString("[@type]"));
        }
        catch (Exception ignored) {
        }
        if (type == dataType) {
          return field;
        }
      }
    }
    return null;
  }

  public String getFormat(DataType type) {
    HierarchicalConfiguration field = getFormatField(type);
    return field == null ? null : field.getBoolean("[@format]", false) ? field.getString("[@pattern]") : null;
  }

  public boolean isFormatted(DataType type) {
    HierarchicalConfiguration field = getFormatField(type);
    return field == null ? false : field.getBoolean("[@format]", false);
  }

  public void setFormat(DataType type, boolean formatted, String pattern) throws ConfigException {
    HierarchicalConfiguration field = getFormatField(type);
    if (field != null) {
      field.setProperty("[@format]", Boolean.valueOf(formatted));
      if (formatted) {
        if (pattern == null || pattern.trim().length() == 0) {
          field.clearProperty("[@pattern]");
        }
        else {
          field.setProperty("[@pattern]", pattern);
        }
      }
    }
    else {
      configuration.setProperty("grid.format(-1)[@type]", type.name());
      configuration.setProperty("grid.format[@format](-1)", Boolean.valueOf(formatted));
      if (pattern != null) {
        configuration.setProperty("grid.format[@pattern](-1)", pattern);
      }
    }
    try {
      configuration.save();
    }
    catch (ConfigurationException ex) {
      throw new ConfigException(ex);
    }
  }

  public boolean hasTokenColors() {
    return configuration.getBoolean("grid.color", false);
  }

  public void setTokenColors(boolean colors) throws ConfigException {
    if (colors) {
      configuration.setProperty("grid.color", Boolean.TRUE);
    }
    else {
      configuration.clearProperty("grid.color");
    }
    try {
      configuration.save();
    }
    catch (ConfigurationException ex) {
      throw new ConfigException(ex);
    }
  }

  public boolean hasMultiConsoles() {
    return configuration.getBoolean("grid.multi", true);
  }

  public void setMultiConsoles(boolean multi) throws ConfigException {
    if (!multi) {
      configuration.setProperty("grid.multi", Boolean.FALSE);
    }
    else {
      configuration.clearProperty("grid.multi");
    }
    try {
      configuration.save();
    }
    catch (ConfigurationException ex) {
      throw new ConfigException(ex);
    }
  }

  public int getFunctionLength() {
    return configuration.getInt("grid.function", 15);
  }

  public void setFunctionLength(int length) throws ConfigException {
    if (length != 15) {
      configuration.setProperty("grid.function", length);
    }
    else {
      configuration.clearProperty("grid.function");
    }
    try {
      configuration.save();
    }
    catch (ConfigurationException ex) {
      throw new ConfigException(ex);
    }
  }

  public boolean isZeroNumeration() {
    return configuration.getBoolean("grid.zero", Boolean.FALSE).booleanValue();
  }

  public void setZeroNumeration(boolean zero) throws ConfigException {
    if (zero) {
      configuration.setProperty("grid.zero", Boolean.valueOf(zero));
    }
    else {
      configuration.clearProperty("grid.zero");
    }
    try {
      configuration.save();
    }
    catch (ConfigurationException ex) {
      throw new ConfigException(ex);
    }
  }

  public Sort getSortType() {
    String sortType = configuration.getString("connections.view[@sort]", "PORT");
    try {
      return Sort.valueOf(sortType.toUpperCase());
    }
    catch (Exception ex) {
      return Sort.PORT;
    }
  }

  public void setSortType(Sort sort) throws ConfigException {
    if (sort != Sort.PORT) {
      configuration.setProperty("connections.view[@sort]", sort.name());
    }
    else {
      configuration.clearProperty("connections.view[@sort]");
    }
    try {
      configuration.save();
    }
    catch (ConfigurationException ex) {
      throw new ConfigException(ex);
    }
  }

  public boolean isLostConnectionMessage() {
    return configuration.getBoolean("global.connection[@lost]", true);
  }

  public void setLostConnectionMessage(boolean show) throws ConfigException {
    if (!show) {
      configuration.setProperty("global.connection[@lost]", Boolean.FALSE);
    }
    else {
      configuration.clearProperty("global.connection[@lost]");
    }
    try {
      configuration.save();
    }
    catch (ConfigurationException ex) {
      throw new ConfigException(ex);
    }
  }

  public boolean isChangeConnectionNofication() {
    return configuration.getBoolean("global.connection[@change]", true);
  }

  public void setChangeConnectionNofication(boolean show) throws ConfigException {
    if (!show) {
      configuration.setProperty("global.connection[@change]", Boolean.FALSE);
    }
    else {
      configuration.clearProperty("global.connection[@change]");
    }
    try {
      configuration.save();
    }
    catch (ConfigurationException ex) {
      throw new ConfigException(ex);
    }
  }

  public String getExportPath() {
    return configuration.getString("files.export[@path]");
  }

  public void setExportPath(String path) throws ConfigException {
    configuration.setProperty("files.export[@path]", path);
    try {
      configuration.save();
    }
    catch (ConfigurationException ex) {
      throw new ConfigException(ex);
    }
  }

  public String getFileFilter() {
    return configuration.getString("files.export[@filter]");
  }

  public void setFileFilter(String description) throws ConfigException {
    configuration.setProperty("files.export[@filter]", description);
    try {
      configuration.save();
    }
    catch (ConfigurationException ex) {
      throw new ConfigException(ex);
    }
  }

  public String getQApp() {
    if (configuration.getString("global.q") != null) {
      try {
        setQApp(configuration.getString("global.q"));
        configuration.clearProperty("global.q");
      }
      catch (ConfigException ignored) {
      }
    }
    return configuration.getString("global.q.app");
  }

  public void setQApp(String qapp) throws ConfigException {
    if (qapp == null || qapp.trim().length() == 0) {
      return;
    }
    configuration.setProperty("global.q.app", qapp);
    try {
      configuration.save();
    }
    catch (ConfigurationException ex) {
      throw new ConfigException(ex);
    }
  }

  public String getQHome() {
    return configuration.getString("global.q.home");
  }

  public void setQHome(String qapp) throws ConfigException {
    if (qapp == null || qapp.trim().length() == 0) {
      return;
    }
    configuration.setProperty("global.q.home", qapp);
    try {
      configuration.save();
    }
    catch (ConfigurationException ex) {
      throw new ConfigException(ex);
    }
  }
  
  public String getQStartup() {
    return configuration.getString("global.q.startup");
  }

  public void setQStartup(String qapp) throws ConfigException {
    if (qapp == null || qapp.trim().length() == 0) {
      return;
    }
    configuration.setProperty("global.q.startup", qapp);
    try {
      configuration.save();
    }
    catch (ConfigurationException ex) {
      throw new ConfigException(ex);
    }
  }


  public URL getUpdateURL() throws MalformedURLException {
    String update = configuration.getString("global.url", "https://sites.google.com/site/qlabeditor/update.xml");
    try {
      configuration.save();
    }
    catch (ConfigurationException ignored) {
    }
    return new URL(update);
  }

  private void init() throws NumberFormatException, ConfigException {
    setTokenColor("DATE", Integer.valueOf("b88a00", 16).intValue());
    setTokenColor("SYMBOL", Integer.valueOf("b30086", 16).intValue());
    setTokenColor("WHITESPACE", 0);
    setTokenColor("LONG", Integer.valueOf("3368ff", 16).intValue());
    setTokenColor("IDENTIFIER", Integer.valueOf("b4a000", 16).intValue());
    setTokenColor("KEYWORD", Integer.valueOf("b4a000", 16).intValue());
    setTokenColor("REAL", Integer.valueOf("3368ff", 16).intValue());
    setTokenColor("OPERATOR", Integer.valueOf("0", 16).intValue());
    setTokenColor("DEFAULT", Integer.valueOf("0", 16).intValue());
    setTokenColor("INTEGER", Integer.valueOf("3368ff", 16).intValue());
    setTokenColor("MINUTE", Integer.valueOf("b88a00", 16).intValue());
    setTokenColor("TIMESPAN", Integer.valueOf("b88a00", 16).intValue());
    setTokenColor("BACKGROUND", Integer.valueOf("ffffff", 16).intValue());
    setTokenColor("MONTH", Integer.valueOf("b88a00", 16).intValue());
    setTokenColor("SYSTEM", Integer.valueOf("f0b400", 16).intValue());
    setTokenColor("COMMAND", Integer.valueOf("f0b400", 16).intValue());
    setTokenColor("EOLCOMMENT", Integer.valueOf("808080", 16).intValue());
    setTokenColor("USERCOMMENT", Color.red.getRGB());
    setTokenColor("TIME", Integer.valueOf("b88a00", 16).intValue());
    setTokenColor("CHARVECTOR", Integer.valueOf("00c814", 16).intValue());
    setTokenColor("BOOLEAN", Integer.valueOf("33ccff", 16).intValue());
    setTokenColor("BYTE", Integer.valueOf("3368ff", 16).intValue());
    setTokenColor("DATETIME", Integer.valueOf("b88a00", 16).intValue());
    setTokenColor("SECOND", Integer.valueOf("b88a00", 16).intValue());
    setTokenColor("SHORT", Integer.valueOf("3368ff", 16).intValue());
    setTokenColor("TIMESTAMP", Integer.valueOf("b88a00", 16).intValue());
    setTokenColor("FLOAT", Integer.valueOf("3368ff", 16).intValue());
    if (System.getenv().containsKey("QHOME")) {
      
    }
  }

  private void convert() throws ConfigException, ArgumentException {
    Config c = Config.getInstance();
    // setKeywords(c.getQKeywords());
    setFont(c.getFont());
    Properties p = c.getProperties();
    for (Object key : p.keySet()) {
      if (key.toString().startsWith("token.")) {
        String token = key.toString().substring("token.".length());
        Object value = p.getProperty("token." + token);
        if (value instanceof String) {
          setTokenColor(token, Integer.parseInt((String)value, 16));
        }
      }
    }
    Server[] ss = c.getServers();
    for (Server s : ss) {
      try {
        setKdbService(s.getName(), s.getHost(), s.getPort(), s.getUsername(), s.getPassword());
      }
      catch (Exception ignored) {
        System.out.println(s);
      }
    }
    String[] ff = c.getMRUFiles();
    for (String f : ff) {
      addOpenedFile(new EditorFile(new File(f), null));
    }
  }
}