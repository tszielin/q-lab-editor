/*
 * Studio for kdb+ by Charles Skelton is licensed under a Creative Commons
 * Attribution-Noncommercial-Share Alike 3.0 Germany License
 * http://creativecommons.org/licenses/by-nc-sa/3.0 except for the netbeans components which retain
 * their original copyright notice
 */
package studio.utils;

import java.awt.*;
import javax.swing.*;
import java.awt.print.*;

public class PrintUtilities implements Printable {
  private Component componentToBePrinted;

  public static void printComponent(Component component) {
    new PrintUtilities(component).print();
  }

  public PrintUtilities(Component componentToBePrinted) {
    this.componentToBePrinted = componentToBePrinted;
  }

  public void print() {
    PrinterJob printJob = PrinterJob.getPrinterJob();
    printJob.setPrintable(this);
    if (printJob.printDialog())
      try {
        printJob.print();
      }
      catch (PrinterException pe) {
        System.err.println("Printing error: " + pe);
      }
  }

  public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) {
    int response = NO_SUCH_PAGE;

    Graphics2D graphics2D = (Graphics2D)graphics;
    disableDoubleBuffering(componentToBePrinted);
    Dimension dimension = componentToBePrinted.getSize();
    double panelWidth = dimension.width;
    double panelHeight = dimension.height;
    double pageHeight = pageFormat.getImageableHeight();
    double pageWidth = pageFormat.getImageableWidth();
    double scale = pageWidth / panelWidth;
    int totalNumPages = (int)Math.ceil(scale * panelHeight / pageHeight);
    if (pageIndex >= totalNumPages) {
      response = NO_SUCH_PAGE;
    }
    else {
      graphics2D.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
      graphics2D.translate(0f, -pageIndex * pageHeight);
      graphics2D.scale(scale, scale);
      componentToBePrinted.paint(graphics2D);
      enableDoubleBuffering(componentToBePrinted);
      response = Printable.PAGE_EXISTS;
    }
    return response;
  }

  public void disableDoubleBuffering(Component component) {
    RepaintManager.currentManager(component).setDoubleBufferingEnabled(false);
  }

  public void enableDoubleBuffering(Component component) {
    RepaintManager.currentManager(component).setDoubleBufferingEnabled(true);
  }
}