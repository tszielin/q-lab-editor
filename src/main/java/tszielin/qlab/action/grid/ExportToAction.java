package tszielin.qlab.action.grid;

import java.awt.event.ActionEvent;
import java.io.*;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.TableModel;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

import com.kx.kdb.Dateable;
import com.kx.kdb.K;

import tszielin.qlab.config.AppConfig;
import tszielin.qlab.error.StudioException;
import tszielin.qlab.util.error.ConfigException;
import tszielin.qlab.util.image.IconsItem;

public class ExportToAction extends GridAction {
  private static final long serialVersionUID = 3160272082953782991L;
  private AppConfig config;
  
  private final int ROWS_PER_SHEET = (int)(Math.pow(2, 16));

  class XMLFileFilter extends FileFilter {
    @Override
    public boolean accept(File file) {
      return file.isDirectory() || file.getName().toLowerCase().endsWith(".xml");
    }

    @Override
    public String getDescription() {
      return "*.xml (XML file)";
    }
  }

  class CSVFileFilter extends FileFilter {
    @Override
    public boolean accept(File file) {
      return file.isDirectory() || file.getName().toLowerCase().endsWith(".csv");
    }

    @Override
    public String getDescription() {
      return "*.csv (CSV file (comma delimited))";
    }
  }

  class TxtFileFilter extends FileFilter {
    @Override
    public boolean accept(File file) {
      return file.isDirectory() || file.getName().toLowerCase().endsWith(".txt");
    }

    @Override
    public String getDescription() {
      return "*.txt (Text file (tab delimited))";
    }
  }

  class XlsFileFilter extends FileFilter {
    @Override
    public boolean accept(File file) {
      return file.isDirectory() || file.getName().toLowerCase().endsWith(".xls");
    }

    @Override
    public String getDescription() {
      return "*.xls (Microsoft Excel sheet)";
    }
  }

  public ExportToAction(JTable table) {
    super(table, "Export to ...", 'E', IconsItem.ICON_BLANK, KeyStroke.getKeyStroke("control E"),
        "Export to ... (XML/Excel/CSV/Text", "Export result to XML/Excel/CSV/Text file");
    try {
      config = AppConfig.getConfig();
    }
    catch (StudioException ignored) {
    }
  }

  @Override
  public void actionPerformed(ActionEvent event) {
    if (getTable() == null || getTable().getRowCount() == 0) {
      return;
    }

    JFileChooser chooser = new JFileChooser();
    chooser.setDialogTitle("Export result set as...");
    chooser.setFileHidingEnabled(true);

    chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

    
    String description = null;
    if (config != null) {
      description = config.getFileFilter();
    }
    FileFilter filter = new XMLFileFilter();
    FileFilter last = null;
    if (filter.getDescription().equals(description)) {
      last = filter;
    }
    else {
      chooser.addChoosableFileFilter(filter);
    }
    filter = new XlsFileFilter();
    if (filter.getDescription().equals(description)) {
      last = filter;
    }
    else {      
      chooser.addChoosableFileFilter(filter);
    }
    filter = new TxtFileFilter();
    if (filter.getDescription().equals(description)) {
      last = filter;
    }
    else {      
      chooser.addChoosableFileFilter(filter);
    }
    filter = new CSVFileFilter();
    if (filter.getDescription().equals(description)) {
      last = filter;
    }
    else {      
      chooser.addChoosableFileFilter(filter);
    }    

    if (config != null && config.getExportPath() != null) {
      chooser.setCurrentDirectory(new File(config.getExportPath()));
    }
    chooser.setAcceptAllFileFilterUsed(false);
    chooser.setFileFilter(last);

    if (chooser.showSaveDialog(SwingUtilities.windowForComponent(getTable())) == JFileChooser.APPROVE_OPTION) {
      File file = chooser.getSelectedFile();
      if (config != null) {
        try {
          config.setExportPath(file.getParent());
          config.setFileFilter(chooser.getFileFilter().getDescription());
        }
        catch (ConfigException ignored) {
        }
        if (chooser.getFileFilter() instanceof XMLFileFilter) {
          if (file.getName().lastIndexOf(".") == -1) {
            file = new File(file.getParent(), file.getName() + ".xml");
          }
          if (checkFile(file)) {
            toXML(file, getTable().getModel());
          }
        }
        else {
          if (chooser.getFileFilter() instanceof XlsFileFilter) {
            if (file.getName().lastIndexOf(".") == -1) {
              file = new File(file.getParent(), file.getName() + ".xls");
            }
            if (checkFile(file)) {
              toXLS(file, getTable().getModel());
            }
          }
          else {
            if (chooser.getFileFilter() instanceof CSVFileFilter) {
              if (file.getName().lastIndexOf(".") == -1) {
                file = new File(file.getParent(), file.getName() + ".csv");
              }
              if (checkFile(file)) {
                toDelimited(file, getTable().getModel(), ",");
              }
            }
            else {
              if (chooser.getFileFilter() instanceof TxtFileFilter) {
                if (file.getName().lastIndexOf(".") == -1) {
                  file = new File(file.getParent(), file.getName() + ".txt");
                }
                if (checkFile(file)) {
                  toDelimited(file, getTable().getModel(), "\t");
                }
              }
              else {
                JOptionPane.showMessageDialog(SwingUtilities.windowForComponent(getTable()),
                    "You didn't specify what format to export the file as.\nExport cancelled.",
                    "Export", JOptionPane.WARNING_MESSAGE);
              }
            }
          }
        }
      }
    }
  }

  private void toDelimited(final File file, final TableModel model, final String delimiter) {
    final String message = "Exporting data to " + file.getPath();
    final String note = "0% complete";
    UIManager.put("ProgressMonitor.progressText", "Progress");

    final ProgressMonitor monitor = new ProgressMonitor(SwingUtilities.windowForComponent(getTable()), message, note, 0, 100);
    monitor.setMillisToDecideToPopup(100);
    monitor.setMillisToPopup(100);
    monitor.setProgress(0);

    Runnable runner = new Runnable() {
      public void run() {
        if (file != null) {
          BufferedWriter writer = null;
          try {
            writer = new BufferedWriter(new FileWriter(file));
            for (int col = 0; col < model.getColumnCount(); col++) {
              if (col > 0) {
                writer.write(delimiter);
              }
              writer.write(model.getColumnName(col));
            }
            writer.newLine();

            int lastProgress = 0;

            for (int row = 1; row <= model.getRowCount(); row++) {
              for (int col = 0; col < model.getColumnCount(); col++) {
                if (col > 0) {
                  writer.write(delimiter);
                }
                Object value = model.getValueAt(row - 1, col);
                if (value instanceof K.KType<?>) {
                  if (!((K.KType<?>)value).isNull()) {
                    writer.write(value.toString());
                  }
                }
              }
              writer.newLine();

              if (monitor.isCanceled()) {
                if (writer != null) {
                  try {
                    writer.flush();
                    writer.close();
                  }
                  catch (IOException ignored) {
                  }
                }
                file.delete();
                monitor.close();
              }
              else {
                final int progress = (100 * row) / model.getRowCount();
                if (progress > lastProgress) {
                  final String note = String.valueOf(progress) + "% complete";
                  SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                      monitor.setProgress(progress);
                      monitor.setNote(note);
                    }
                  });

                  Thread.yield();
                }
              }
            }
          }
          catch (Exception ex) {
            JOptionPane.showMessageDialog(SwingUtilities.windowForComponent(getTable()), ex.getMessage(), "Export",
                JOptionPane.ERROR_MESSAGE);
          }
          finally {
            if (writer != null) {
              try {
                writer.flush();
                writer.close();
              }
              catch (IOException ignored) {
              }
            }
            monitor.close();
          }
        }
      }
    };

    Thread thread = new Thread(runner);
    thread.setName("export");
    thread.setPriority(Thread.MIN_PRIORITY);
    thread.start();
  }

  private void toXML(final File file, final TableModel model) {
    final String message = "Exporting data to " + file.getPath();
    final String note = "0% complete";
    UIManager.put("ProgressMonitor.progressText", "Progress");

    final ProgressMonitor monitor = new ProgressMonitor(SwingUtilities.windowForComponent(getTable()), message, note, 0, 100);
    monitor.setMillisToDecideToPopup(100);
    monitor.setMillisToPopup(100);
    monitor.setProgress(0);

    Runnable runner = new Runnable() {
      public void run() {
        if (file != null) {
          BufferedWriter writer = null;
          try {
            writer = new BufferedWriter(new FileWriter(file));
            writer.write("<rows>");
            writer.newLine();
            for (int row = 1; row <= model.getRowCount(); row++) {
              writer.write("  <row>");
              writer.newLine();

              int lastProgress = 0;
              for (int col = 0; col < model.getColumnCount(); col++) {
                Object value = model.getValueAt(row - 1, col);
                if (value instanceof K.KType<?>) {
                  if (((K.KType<?>)value).isNull()) {
                    continue;
                  }
                  writer.write("    <" + model.getColumnName(col) + ">");
                  writer.write(((K.KType<?>)value).toString(false));
                  writer.write("</" + model.getColumnName(col) + ">");
                  writer.newLine();
                }
              }
              writer.write("  </row>");
              writer.newLine();

              if (monitor.isCanceled()) {
                if (writer != null) {
                  try {
                    writer.flush();
                    writer.close();
                  }
                  catch (IOException ignored) {
                  }
                }
                file.delete();
                monitor.close();
                
              }
              else {
                final int progress = (100 * row) / model.getRowCount();
                if (progress > lastProgress) {
                  final String note = String.valueOf(progress) + "% complete";
                  SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                      monitor.setProgress(progress);
                      monitor.setNote(note);
                    }
                  });

                  Thread.yield();
                }
              }
            }
            writer.write("</rows>");
          }
          catch (Exception ex) {
            JOptionPane.showMessageDialog(SwingUtilities.windowForComponent(getTable()), ex.getMessage(), "Export",
                JOptionPane.ERROR_MESSAGE);
          }
          finally {
            if (writer != null) {
              try {
                writer.flush();
                writer.close();
              }
              catch (IOException ignored) {
              }
            }
            monitor.close();
          }
        }
      }
    };

    Thread thread = new Thread(runner);
    thread.setName("export");
    thread.setPriority(Thread.MIN_PRIORITY);
    thread.start();
  }

  private void toXLS(final File file, final TableModel model) {
    final String message = "Exporting data to " + file.getPath();
    final String note = "0% complete";
    UIManager.put("ProgressMonitor.progressText", "Progress");

    final ProgressMonitor monitor = new ProgressMonitor(SwingUtilities.windowForComponent(getTable()), message, note, 0, 100);
    monitor.setMillisToDecideToPopup(100);
    monitor.setMillisToPopup(100);
    monitor.setProgress(0);

    Runnable runner = new Runnable() {
      public void run() {                
        if (file != null) {
          FileOutputStream stream = null;

          HSSFWorkbook workbook = new HSSFWorkbook();
          CreationHelper helper = workbook.getCreationHelper();
          
          CellStyle dateCellStyle = workbook.createCellStyle();
          dateCellStyle.setDataFormat((short)0xe);
          CellStyle timeCellStyle = workbook.createCellStyle();
          timeCellStyle.setDataFormat((short)0x15);
          CellStyle datetimeCellStyle = workbook.createCellStyle();
          datetimeCellStyle.setDataFormat((short)0x16);
          CellStyle monthCellStyle = workbook.createCellStyle();
          monthCellStyle.setDataFormat((short)0x11);
          CellStyle minuteCellStyle = workbook.createCellStyle();
          minuteCellStyle.setDataFormat((short)0x14);
          
          CellStyle headStyle = workbook.createCellStyle();
          headStyle.setFillForegroundColor(IndexedColors.BLUE.getIndex());
          
          HSSFSheet sheet = null;
          try {
            Row sheetRow = null;
            int lastProgress = 0;
            Cell cell = null;
            int row = 0;
            int sheetId = -1;
            int sheetCol = 0;
            while (row < model.getRowCount()) {
              if (row % (ROWS_PER_SHEET - 1) == 0) {                
                sheet = workbook.createSheet("Sheet" + ++sheetId);
                sheetRow = sheet.createRow(0);
                for (int col = 0; col < model.getColumnCount(); col++) {
                  cell = sheetRow.createCell(col);
                  cell.setCellValue(helper.createRichTextString(model.getColumnName(col)));
                  cell.setCellStyle(headStyle);
                }
                sheetCol = 0;
              }
              sheetRow = sheet.createRow(++sheetCol % ROWS_PER_SHEET);
              for (int col = 0; col < model.getColumnCount(); col++) {
                Object value = model.getValueAt(row, col);
                if (value instanceof K.KType<?> && !((K.KType<?>)value).isNull()) {
                  if (model.getColumnClass(col) == K.KSymbolArray.class) {
                    sheetRow.createCell(col).setCellValue(
                        helper.createRichTextString(escape(((K.KSymbol)value).toString(false))));
                  }
                  else {
                    if (value instanceof Dateable) {
                      cell = sheetRow.createCell(col);
                      cell.setCellValue(((Dateable)value).toDate());
                      cell.setCellStyle(dateCellStyle);
                    }
                    else {                      
                      if (model.getColumnClass(col) == K.KBooleanArray.class) {
                        sheetRow.createCell(col).setCellValue(((K.KBoolean)value).getValue());
                      }
                      else {
                        if (model.getColumnClass(col) == K.KFloatArray.class) {
                          sheetRow.createCell(col).setCellValue(((K.KFloat)value).getValue());
                        }
                        else {
                          if (model.getColumnClass(col) == K.KRealArray.class) {
                            sheetRow.createCell(col).setCellValue(((K.KReal)value).getValue());
                          }
                          else {
                            if (model.getColumnClass(col) == K.KLongArray.class) {
                              sheetRow.createCell(col).setCellValue(((K.KLong)value).getValue());
                            }
                            else {
                              if (model.getColumnClass(col) == K.KIntegerArray.class) {
                                sheetRow.createCell(col).setCellValue(((K.KInteger)value).getValue());
                              }
                              else {
                                if (model.getColumnClass(col) == K.KShortArray.class) {
                                  sheetRow.createCell(col).setCellValue(((K.KShort)value).getValue());
                                }
                                else if (model.getColumnClass(col) == K.KCharacterArray.class) {
                                  sheetRow.createCell(col).setCellValue(
                                      helper.createRichTextString(escape(((K.KCharacter)value).toString(false))));
                                }
                                else {
                                  if (value instanceof K.KType<?>) {
                                  sheetRow.createCell(col).setCellValue(
                                      helper.createRichTextString(escape(((K.KType<?>)value).toString(false))));
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
                else {
                  sheetRow.createCell(col).setCellValue(helper.createRichTextString(""));
                }
              }

              if (monitor.isCanceled()) {
                monitor.close();
                return;
              }
              else {
                final int progress = (100 * row) / model.getRowCount();
                if (progress > lastProgress) {
                  final String note = String.valueOf(progress) + "% complete";
                  SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                      monitor.setProgress(progress);
                      monitor.setNote(note);
                    }
                  });

                  Thread.yield();
                }
              }
              row++;
            }
            
            stream = new FileOutputStream(file);
            workbook.write(stream);
          }
          catch (Exception ex) {
            JOptionPane.showMessageDialog(SwingUtilities.windowForComponent(getTable()), ex.getMessage(), "Export",
                JOptionPane.ERROR_MESSAGE);
          }
          finally {
            if (stream != null) {
              try {
                stream.flush();
                stream.close();
              }
              catch (IOException ignored) {
              }
            }
            monitor.close();
          }
        }
      }
    };

    Thread thread = new Thread(runner);
    thread.setName("export");
    thread.setPriority(Thread.MIN_PRIORITY);
    thread.start();
  }

  private boolean checkFile(File file) {
    if (file.exists()) {
      if (JOptionPane.showConfirmDialog(SwingUtilities.windowForComponent(getTable()), "File " + file.getPath() +
          " exists.\nDo you want to overwrite?", "Export to...",
          JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
        return file.canWrite();
      }
      return false;
    }
    return true;
  }

  private String escape(String text) {
    StringBuilder result = new StringBuilder();
    StringCharacterIterator iterator = new StringCharacterIterator(text);
    char character = iterator.current();
    while (character != CharacterIterator.DONE) {
      switch (character) {
        case '<':
          result.append("&lt;");
          break;
        case '>':
          result.append("&gt;");
          break;
        case '\"':
          result.append("&quot;");
          break;
        case '\'':
          result.append("&apos;");
          break;
        case '&':
          result.append("&amp;");
          break;
        default:
          result.append(character);
          break;
      }
      character = iterator.next();
    }
    return result.toString();
  }
}
