/*
 * Studio for kdb+ by Charles Skelton is licensed under a Creative Commons
 * Attribution-Noncommercial-Share Alike 3.0 Germany License
 * http://creativecommons.org/licenses/by-nc-sa/3.0 except for the netbeans components which retain
 * their original copyright notice
 */

package studio.ui;

import java.awt.Cursor;
import java.awt.Window;
import java.awt.Dialog.ModalityType;
import java.util.Locale;
import java.util.TimeZone;

import javax.swing.*;

import org.jfree.chart.*;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.general.SeriesException;
import org.jfree.data.time.*;
import org.jfree.data.xy.*;

import com.kx.kdb.K;

import studio.kdb.KTableModel;
import studio.kdb.ToDouble;
import tszielin.qlab.util.image.IconsItem;

public class LineChart {
  private ChartPanel chartPanel;
  private final static TimeZone TIME_ZONE = TimeZone.getTimeZone("GMT");
  private JDialog dialog;

  public LineChart(JTable table) {
    if (table.getModel() instanceof KTableModel) {
      Window window = SwingUtilities.windowForComponent(table);
      JFreeChart chart = createDataset(window, (KTableModel)table.getModel());
      if (chart != null) {        
        if (window != null) {
          window.setCursor(Cursor.getDefaultCursor());
        }
        dialog = new EscapeDialog(window, "Chart", ModalityType.APPLICATION_MODAL);
        chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        chartPanel.setMouseZoomable(true, false);
        dialog.setContentPane(chartPanel);
        dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        dialog.setIconImage(IconsItem.IMAGE_APP);
        dialog.pack();
        dialog.requestFocus();
        dialog.toFront();
        dialog.setVisible(true);
      }
    }
  }

  private JFreeChart createDataset(Window window, KTableModel table) {
    XYDataset ds = null;

    if (table.getColumnCount() > 0) {
      Class<?> cls = table.getColumnClass(0);

      if ((cls == K.KDateArray.class) || (cls == K.KTimeArray.class) ||
          (cls == K.KMonthArray.class) || (cls == K.KMinuteArray.class) ||
          (cls == K.KSecondArray.class) || (cls == K.KDatetimeArray.class)) {
        TimeSeriesCollection tsc = new TimeSeriesCollection(TIME_ZONE);

        for (int col = 1; col < table.getColumnCount(); col++) {
          TimeSeries series = null;
          Object obj = null;
          try {
            if (cls == K.KDateArray.class) {
              series = new TimeSeries(table.getColumnName(col), "Time", "Value");
              K.KDateArray dates = (K.KDateArray)table.getColumn(0);

              for (int row = 0; row < dates.getLength(); row++) {
                K.KDate date = dates.get(row);
                Day day = new Day(date.toDate(), TIME_ZONE, Locale.getDefault());

                obj = table.getValueAt(row, col);
                if (obj instanceof K.KBase<?>) {
                  if (!((K.KBase<?>)obj).isNull()) {
                    if (obj instanceof ToDouble) {
                      series.addOrUpdate(day, ((ToDouble)obj).toDouble());
                    }
                  }
                }
              }
            }
            else {
              if (cls == K.KTimeArray.class) {
                series = new TimeSeries(table.getColumnName(col), "Time", "Value");

                K.KTimeArray times = (K.KTimeArray)table.getColumn(0);
                for (int row = 0; row < table.getRowCount(); row++) {
                  K.KTime time = times.get(row);
                  Millisecond ms = new Millisecond(time.toDate(), TIME_ZONE, Locale.getDefault());

                  obj = table.getValueAt(row, col);
                  if (obj instanceof K.KBase<?>) {
                    if (!((K.KBase<?>)obj).isNull()) {
                      if (obj instanceof ToDouble) {
                        series.addOrUpdate(ms, ((ToDouble)obj).toDouble());
                      }
                    }
                  }
                }
              }
              else {
                if (cls == K.KDatetimeArray.class) {
                  series = new TimeSeries(table.getColumnName(col), "Time", "Value");
                  K.KDatetimeArray times = (K.KDatetimeArray)table.getColumn(0);

                  for (int row = 0; row < table.getRowCount(); row++) {
                    K.KDatetime time = times.get(row);
                    Millisecond ms = new Millisecond(time.toDate(), TIME_ZONE, Locale.getDefault());

                    obj = table.getValueAt(row, col);
                    if (obj instanceof K.KBase<?>) {
                      if (!((K.KBase<?>)obj).isNull()) {
                        if (obj instanceof ToDouble) {
                          series.addOrUpdate(ms, ((ToDouble)obj).toDouble());
                        }
                      }
                    }
                  }
                }
                else {
                  if (cls == K.KMonthArray.class) {
                    series = new TimeSeries(table.getColumnName(col), "Time", "Value");
                    K.KMonthArray times = (K.KMonthArray)table.getColumn(0);
                    for (int row = 0; row < table.getRowCount(); row++) {
                      K.KMonth time = times.get(row);
                      int m = time.getValue() + 24000;
                      int y = m / 12;
                      m = 1 + m % 12;

                      Month month = new Month(m, y);

                      obj = table.getValueAt(row, col);
                      if (obj instanceof K.KBase) {
                        if (!((K.KBase<?>)obj).isNull()) {
                          if (obj instanceof ToDouble) {
                            series.addOrUpdate(month, ((ToDouble)obj).toDouble());
                          }
                        }
                      }
                    }
                  }
                  else {
                    if (cls == K.KSecondArray.class) {
                      series = new TimeSeries(table.getColumnName(col), "Time", "Value");
                      K.KSecondArray times = (K.KSecondArray)table.getColumn(0);
                      for (int row = 0; row < table.getRowCount(); row++) {
                        K.KSecond time = times.get(row);
                        Second second = new Second(time.getValue() % 60, time.getValue() / 60, 0, 1, 1, 2001);

                        obj = table.getValueAt(row, col);
                        if (obj instanceof K.KBase) {
                          if (!((K.KBase<?>)obj).isNull()) { 
                            if (obj instanceof ToDouble) {
                              series.addOrUpdate(second, ((ToDouble)obj).toDouble());
                            }
                          }
                        }
                      }
                    }
                    else {
                      if (cls == K.KMinuteArray.class) {
                        series = new TimeSeries(table.getColumnName(col), "Time", "Value");
                        K.KMinuteArray times = (K.KMinuteArray)table.getColumn(0);
                        for (int row = 0; row < table.getRowCount(); row++) {
                          K.KMinute time = times.get(row);
                          Minute minute = new Minute(time.getValue() % 60, time.getValue() / 60, 1, 1, 2001);
                          obj = table.getValueAt(row, col);
                          if (obj instanceof K.KBase) {
                            if (!((K.KBase<?>)obj).isNull()) {
                              if (obj instanceof ToDouble) {
                                series.addOrUpdate(minute, ((ToDouble)obj).toDouble());
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
          catch (SeriesException ex) {
            JOptionPane.showMessageDialog(window, ex.getMessage(), "Chart", JOptionPane.ERROR_MESSAGE);
          }
          if (series.getItemCount() > 0) {
            tsc.addSeries(series);
          }
        }
        ds = tsc;
      }
      else { 
        if ((cls == K.KFloatArray.class) || (cls == K.KRealArray.class) ||
            (cls == K.KShortArray.class) || (cls == K.KIntegerArray.class) ||
            (cls == K.KLongArray.class)) {
          XYSeriesCollection xysc = new XYSeriesCollection();

          for (int col = 1; col < table.getColumnCount(); col++) {
            XYSeries series = null;

            try {
              series = new XYSeries(table.getColumnName(col));

              for (int row = 0; row < table.getRowCount(); row++) {
                if (table.getValueAt(row, 0) instanceof ToDouble &&
                    table.getValueAt(row, col) instanceof ToDouble) {
                  double x = ((ToDouble)table.getValueAt(row, 0)).toDouble();
                  double y = ((ToDouble)table.getValueAt(row, col)).toDouble();
                  series.add(x, y);
                }
              }
            }
            catch (SeriesException ex) {
              JOptionPane.showMessageDialog(window, ex.getMessage(), "Chart", JOptionPane.ERROR_MESSAGE);
            }

            if (series.getItemCount() > 0) {
              xysc.addSeries(series);
            }            
          }

          ds = xysc;
        }
      }
    }

    if (ds != null) {
      boolean legend = false;

      if (ds.getSeriesCount() > 1) {
        legend = true;
      }
      if (ds instanceof XYSeriesCollection) {
        return ChartFactory.createXYLineChart("", "", "", ds, PlotOrientation.VERTICAL, legend,
            true, true);
      }
      else {
        if (ds instanceof TimeSeriesCollection) {
          return ChartFactory.createTimeSeriesChart("", "", "", ds, legend, true, true);
        }
      }
    }
    return null;
  }
}
