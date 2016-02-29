package net.mariottini.layout;

import java.awt.*;

/**
 * A layout manager that piles components either vertically or horizontally. Components are layed
 * out with their preferred with and height. This is a semplified version of {@link PileLayout}. See
 * the document "<a href="doc-files/PileLayoutHowTo.html">How to use PileLayout</a>" for more
 * information.
 * <P>
 * <DL>
 * <DT><B>License:</B></DT>
 * <DD>
 * 
 * <pre>
 *  Copyright � 2006, 2007 Roberto Mariottini. All rights reserved.
 * 
 *  Permission is granted to anyone to use this software in source and binary forms
 *  for any purpose, with or without modification, including commercial applications,
 *  and to alter it and redistribute it freely, provided that the following conditions
 *  are met:
 * 
 *  o  Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer.
 *  o  The origin of this software must not be misrepresented; you must not
 *     claim that you wrote the original software. If you use this software
 *     in a product, an acknowledgment in the product documentation would be
 *     appreciated but is not required.
 *  o  Altered source versions must be plainly marked as such, and must not
 *     be misrepresented as being the original software.
 * 
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 *  IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 *  ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 *  FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 *  DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 *  OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 *  HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 *  LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 *  OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * 
 * <pre></DD>
 * </DL>
 * 
 * @author Roberto Mariottini
 */
public final class SimplePileLayout implements LayoutManager {
  private static final boolean DEBUG = false;

  /** the placement direction is horizontal */
  public static final int DIRECTION_HORIZONTAL = 1;
  /** the placement direction is vertical */
  public static final int DIRECTION_VERTICAL = 2;

  private static final int DEFAULT_GAP = 4;

  private final int direction;
  private final int gap;
  private final Insets insets;

  /**
   * Builds a SimplePileLayout with the specified placement direction, with the default gap and with
   * the default margins.
   * 
   * @param direction
   *          one of {@link #DIRECTION_HORIZONTAL} or {@link #DIRECTION_VERTICAL}
   */
  public SimplePileLayout(int direction) {
    this(direction, DEFAULT_GAP, null);
  }

  /**
   * Builds a SimplePileLayout with the specified placement direction and with the specified gap,
   * used also for the margins.
   * 
   * @param direction
   *          one of {@link #DIRECTION_HORIZONTAL} or {@link #DIRECTION_VERTICAL}
   * @param gap
   *          the gap used for component spacing and also for margins
   */
  public SimplePileLayout(int direction, int gap) {
    this(direction, gap, null);
  }

  /**
   * Builds a SimplePileLayout with the specified placement direction, with the default gap and with
   * the specified margins.
   * 
   * @param direction
   *          one of {@link #DIRECTION_HORIZONTAL} or {@link #DIRECTION_VERTICAL}
   * @param margins
   *          the margins to use
   */
  public SimplePileLayout(int direction, Insets margins) {
    this(direction, DEFAULT_GAP, margins);
  }

  /**
   * Builds a SimplePileLayout with the specified placement direction, with the specified gap and
   * with the specified margins.
   * 
   * @param direction
   *          one of {@link #DIRECTION_HORIZONTAL} or {@link #DIRECTION_VERTICAL}
   * @param gap
   *          the gap used for component spacing
   * @param margins
   *          the margins to use
   */
  public SimplePileLayout(int direction, int gap, Insets margins) {
    this.direction = direction;
    this.gap = gap;
    this.insets = margins;
  }

  // Called by the Container add methods. Layout managers that don't associate strings with their
  // components
  // generally do nothing in this method.
  public void addLayoutComponent(String x, Component comp) {
  }

  // Called by the Container remove and removeAll methods. Many layout managers do nothing in this
  // method,
  // relying instead on querying the container for its components, using the Container method
  // getComponents
  // (in the API reference documentation).
  public void removeLayoutComponent(Component comp) {
  }

  // Called by the Container getPreferredSize method, which is itself called under a variety of
  // circumstances.
  // This method should calculate and return the ideal size of the container, assuming that the
  // components it
  // contains will be at or above their preferred sizes. This method must take into account the
  // container's
  // internal borders, which are returned by the getInsets (in the API reference documentation)
  // method.
  public Dimension preferredLayoutSize(Container cont) {
    return doWork(cont, false);
  }

  // Called by the Container getMinimumSize method, which is itself called under a variety of
  // circumstances.
  // This method should calculate and return the minimum size of the container, assuming that the
  // components
  // it contains will be at or above their minimum sizes. This method must take into account the
  // container's
  // internal borders, which are returned by the getInsets method.
  public Dimension minimumLayoutSize(Container cont) {
    return doWork(cont, false);
  }

  // Called when the container is first displayed, and each time its size changes. A layout
  // manager's
  // layoutContainer method doesn't actually draw components. It simply invokes each component's
  // setSize,
  // setLocation, and setBounds methods to set the component's size and position.
  // This method must take into account the container's internal borders, which are returned by the
  // getInsets
  // method. If appropriate, it should also take the container's orientation (returned by the
  // getComponentOrientation (in the API reference documentation) method) into account. You can't
  // assume that
  // the preferredLayoutSize or minimumLayoutSize method will be called before layoutContainer is
  // called.
  public void layoutContainer(Container cont) {
    if (DEBUG)
      System.out.println("Minimum dimensions: " + doWork(cont, true));
    else
      doWork(cont, true);
  }

  private final Dimension doWork(Container cont, boolean setBounds) {
    if (DEBUG)
      System.out.println("===============================");
    if (DEBUG)
      System.out.println("insets: " + insets + " gap: " + gap);
    if (DEBUG)
      System.out.println("cont.getInsets(): " + cont.getInsets());

    int pos = 0;
    int alt = 0;
    int rem = 0;
    int altrem = 0;

    // calculate insets
    if (insets != null) {
      if (direction == DIRECTION_HORIZONTAL) {
        pos += insets.left;
        alt += insets.top;
        rem += insets.right;
        altrem += insets.bottom;
      }
      else {
        pos += insets.top;
        alt += insets.left;
        rem += insets.bottom;
        altrem += insets.right;
      }
    }
    else {
      pos += gap;
      alt += gap;
      rem += gap;
      altrem += gap;
    }

    // calculate container insets (border)
    if (direction == DIRECTION_HORIZONTAL) {
      pos += cont.getInsets().left;
      alt += cont.getInsets().top;
      rem += cont.getInsets().right;
      altrem += cont.getInsets().bottom;
    }
    else {
      pos += cont.getInsets().top;
      alt += cont.getInsets().left;
      rem += cont.getInsets().bottom;
      altrem += cont.getInsets().right;
    }

    int fitSize = 0;
    if (setBounds) {
      Dimension size = cont.getSize();
      if (direction == DIRECTION_HORIZONTAL) {
        fitSize = size.height;
      }
      else {
        fitSize = size.width;
      }
      fitSize -= alt + altrem;
    }

    if (DEBUG)
      System.out.println("pos: " + pos + " alt: " + alt + " rem: " + rem + " altrem: " + altrem +
          " fitSize: " + fitSize);

    int altmax = 0;
    Component[] comps = cont.getComponents();
    for (int i = 0; i < comps.length; ++i) {
      Component comp = comps[i];
      Dimension d = comp.getPreferredSize();
      if (DEBUG)
        System.out.println("comp.getPreferredSize(): " + d);
      if (direction == DIRECTION_HORIZONTAL) {
        if (d.height > altmax) {
          altmax = d.height;
        }
        if (setBounds) {
          Dimension max = comp.getMaximumSize();
          if (DEBUG)
            System.out.println("comp.getMaximumSize(): " + max);
          if (max.height >= fitSize) {
            d.height = Math.max(d.height, fitSize);
          }
          comp.setBounds(pos, alt, d.width, d.height);
          if (DEBUG)
            System.out.println("comp.setBounds(): " + comp.getBounds());
        }
        pos += d.width;
      }
      else {
        if (d.width > altmax) {
          altmax = d.width;
        }
        if (setBounds) {
          Dimension max = comp.getMaximumSize();
          if (DEBUG)
            System.out.println("comp.getMaximumSize(): " + max);
          if (max.width >= fitSize) {
            d.width = Math.max(d.width, fitSize);
          }
          comp.setBounds(alt, pos, d.width, d.height);
          if (DEBUG)
            System.out.println("comp.getBounds(): " + comp.getBounds());
        }
        pos += d.height;
      }
      pos += gap;
    }
    pos -= gap;

    if (DEBUG)
      System.out.println("pos: " + pos + " altmax: " + altmax);
    if (direction == DIRECTION_HORIZONTAL) {
      return new Dimension(pos + rem, alt + altmax + altrem);
    }
    else {
      return new Dimension(alt + altmax + altrem, pos + rem);
    }
  }
}
