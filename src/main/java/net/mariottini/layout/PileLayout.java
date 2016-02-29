package net.mariottini.layout;

import java.awt.*;
import java.util.Hashtable;

/**
 * A layout manager that piles components either vertically or horizontally. Components are layed
 * out with their preferred with and height, aligned or resized according to the constraints used.
 * See the document "<a href="doc-files/PileLayoutHowTo.html">How to use PileLayout</a>" for more
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
public final class PileLayout implements LayoutManager2 {
  private static final boolean DEBUG = false;

  private static final int LEFT = 0;
  private static final int CENTER = 1;
  private static final int RIGHT = 2;
  private static final int TOP = LEFT;
  private static final int MIDDLE = CENTER;
  private static final int BOTTOM = RIGHT;
  private static final int FIT = 3;
  private static final int EXPAND = 4;

  /** the component is horizontally aligned to the left (valid ony for vertical placement) */
  public static final Integer ALIGN_LEFT = new Integer(LEFT);
  /** the component is horizontally center aligned (valid ony for vertical placement) */
  public static final Integer ALIGN_CENTER = new Integer(CENTER);
  /** the component is horizontally aligned to the right (valid ony for vertical placement) */
  public static final Integer ALIGN_RIGHT = new Integer(RIGHT);
  /** the component is vertically aligned to the top (valid ony for horizontal placement) */
  public static final Integer ALIGN_TOP = ALIGN_LEFT;
  /** the component is vertically aligned to the middle (valid ony for horizontal placement) */
  public static final Integer ALIGN_MIDDLE = ALIGN_CENTER;
  /** the component is vertically aligned to the bottom (valid ony for horizontal placement) */
  public static final Integer ALIGN_BOTTOM = ALIGN_RIGHT;
  /**
   * the component is not aligned, but fitted in size (adjusting its height for horizontal placement
   * or its width for vertical placement)
   */
  public static final Integer ALIGN_FIT = new Integer(FIT);
  /** like {@link #ALIGN_FIT} but expands in both dimensions */
  public static final Integer ALIGN_EXPAND = new Integer(EXPAND);

  /** the placement direction is horizontal */
  public static final int DIRECTION_HORIZONTAL = 1;
  /** the placement direction is vertical */
  public static final int DIRECTION_VERTICAL = 2;

  private static final int DEFAULT_GAP = 4;

  private final int direction;
  private final int gap;
  private final Insets insets;

  private Hashtable<Component, Object> constraints;

  /**
   * Builds a PileLayout with the specified placement direction, with the default gap and with the
   * default margins.
   * 
   * @param direction
   *          one of {@link #DIRECTION_HORIZONTAL} or {@link #DIRECTION_VERTICAL}
   */
  public PileLayout(int direction) {
    this(direction, DEFAULT_GAP, null);
  }

  /**
   * Builds a PileLayout with the specified placement direction and with the specified gap, used
   * also for the margins.
   * 
   * @param direction
   *          one of {@link #DIRECTION_HORIZONTAL} or {@link #DIRECTION_VERTICAL}
   * @param gap
   *          the gap used for component spacing and also for margins
   */
  public PileLayout(int direction, int gap) {
    this(direction, gap, null);
  }

  /**
   * Builds a PileLayout with the specified placement direction, with the default gap and with the
   * specified margins.
   * 
   * @param direction
   *          one of {@link #DIRECTION_HORIZONTAL} or {@link #DIRECTION_VERTICAL}
   * @param margins
   *          the margins to use
   */
  public PileLayout(int direction, Insets margins) {
    this(direction, DEFAULT_GAP, margins);
  }

  /**
   * Builds a PileLayout with the specified placement direction, with the specified gap and with the
   * specified margins.
   * 
   * @param direction
   *          one of {@link #DIRECTION_HORIZONTAL} or {@link #DIRECTION_VERTICAL}
   * @param gap
   *          the gap used for component spacing
   * @param margins
   *          the margins to use
   */
  public PileLayout(int direction, int gap, Insets margins) {
    this.direction = direction;
    this.gap = gap;
    this.insets = margins;
  }

  // Called by the Container add methods. Layout managers that don't associate strings with their
  // components
  // generally do nothing in this method.
  public void addLayoutComponent(String x, Component comp) {
    if (DEBUG)
      System.out.println("addLayoutComponent() x: " + x + " comp: " + comp.getClass());
  }

  // Called by the Container remove and removeAll methods. Many layout managers do nothing in this
  // method,
  // relying instead on querying the container for its components, using the Container method
  // getComponents
  // (in the API reference documentation).
  public void removeLayoutComponent(Component comp) {
    if (DEBUG)
      System.out.println("removeLayoutComponent() comp: " + comp.getClass());
    if (constraints != null) {
      constraints.remove(comp);
    }
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
    if (DEBUG) {
      System.out.println("Minimum dimensions: " + doWork(cont, true));
    }
    else {
      doWork(cont, true);
    }
  }

  // Adds the specified component to the layout, using the specified constraint object.
  public void addLayoutComponent(Component comp, Object constr) {
    if (DEBUG)
      System.out.println("addLayoutComponent() constr: " + constr + " comp: " + comp.getClass());

    if (constr != null && constr != ALIGN_LEFT && constr != ALIGN_CENTER && constr != ALIGN_RIGHT &&
        constr != ALIGN_FIT && constr != ALIGN_EXPAND) {
      throw new IllegalArgumentException("cannot add to layout: constraint must be one of: "
          + "ALIGN_LEFT, ALIGN_CENTER, ALIGN_RIGHT, " + "ALIGN_TOP, ALIGN_MIDDLE, ALIGN_BOTTOM, "
          + "ALIGN_FIT, ALIGN_EXPAND");
    }

    if (constraints == null) {
      constraints = new Hashtable<Component, Object>();
    }

    if (constr != null) {
      constraints.put(comp, constr);
    }
  }

  // Returns the alignment along the x axis.
  public float getLayoutAlignmentX(Container target) {
    return 0.5f;
  }

  // Returns the alignment along the y axis.
  public float getLayoutAlignmentY(Container target) {
    return 0.5f;
  }

  // Invalidates the layout, indicating that if the layout manager has cached information it should
  // be discarded.
  public void invalidateLayout(Container target) {
    if (DEBUG)
      System.out.println("invalidateLayout() target: " + target);
  }

  // Calculates the maximum size dimensions for the specified container, given the components it
  // contains.
  public Dimension maximumLayoutSize(Container target) {
    return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
  }

  // do the work (generic)
  private final Dimension doWork(Container cont, boolean setBounds) {
    if (DEBUG)
      System.out.println("===============================");
    if (DEBUG)
      System.out.println("insets: " + insets + " gap: " + gap);

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
    Insets contInsets = cont.getInsets();
    if (DEBUG)
      System.out.println("cont.getInsets(): " + contInsets);
    if (direction == DIRECTION_HORIZONTAL) {
      pos += contInsets.left;
      alt += contInsets.top;
      rem += contInsets.right;
      altrem += contInsets.bottom;
    }
    else {
      pos += contInsets.top;
      alt += contInsets.left;
      rem += contInsets.bottom;
      altrem += contInsets.right;
    }

    int fitSize = 0;
    Dimension contSize = null;
    if (setBounds) {
      contSize = cont.getSize();
      if (DEBUG)
        System.out.println("cont.getSize(): " + contSize);
      if (direction == DIRECTION_HORIZONTAL) {
        fitSize = contSize.height;
      }
      else {
        fitSize = contSize.width;
      }
      fitSize -= alt + altrem;
    }

    if (DEBUG)
      System.out.println("pos: " + pos + " alt: " + alt + " rem: " + rem + " altrem: " + altrem +
          " fitSize: " + fitSize);

    Component expandComp = null;
    int expandCompIndex = -1;
    int altmax = 0;
    Component[] comps = cont.getComponents();
    for (int i = 0; i < comps.length; ++i) {
      Component comp = comps[i];
      int constr = LEFT;
      if (setBounds && constraints != null) {
        Integer c = (Integer)constraints.get(comp);
        if (c != null) {
          constr = c.intValue();
        }
      }
      Dimension d = comp.getPreferredSize();
      if (DEBUG)
        System.out.println("comp.getPreferredSize(): " + d);
      if (direction == DIRECTION_HORIZONTAL) {
        if (d.height > altmax) {
          altmax = d.height;
        }
        if (setBounds) {
          switch (constr) {
            case TOP:
              comp.setBounds(pos, alt, d.width, d.height);
              break;

            case MIDDLE:
              comp.setBounds(pos, alt + (fitSize - d.height) / 2, d.width, d.height);
              break;

            case BOTTOM:
              comp.setBounds(pos, alt + (fitSize - d.height), d.width, d.height);
              break;

            case EXPAND:
              expandComp = comp;
              expandCompIndex = i;
              // no break!!
            case FIT:
              comp.setBounds(pos, alt, d.width, Math.max(d.height, fitSize));
              break;
          }
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
          switch (constr) {
            case LEFT:
              comp.setBounds(alt, pos, d.width, d.height);
              break;

            case CENTER:
              comp.setBounds(alt + (fitSize - d.width) / 2, pos, d.width, d.height);
              break;

            case RIGHT:
              comp.setBounds(alt + (fitSize - d.width), pos, d.width, d.height);
              break;

            case EXPAND:
              expandComp = comp;
              expandCompIndex = i;
              // no break!!
            case FIT:
              comp.setBounds(alt, pos, Math.max(d.width, fitSize), d.height);
              break;
          }
          if (DEBUG)
            System.out.println("comp.getBounds(): " + comp.getBounds());
        }
        pos += d.height;
      }
      pos += gap;
    }
    pos -= gap;

    if (expandComp != null) {
      Rectangle b = expandComp.getBounds();
      int delta = 0;
      if (direction == DIRECTION_HORIZONTAL) {
        delta = contSize.width - pos - rem;
        b.width += delta;
      }
      else {
        delta = contSize.height - pos - rem;
        b.height += delta;
      }
      if (delta > 0) {
        expandComp.setBounds(b);
        if (DEBUG)
          System.out.println("expandComp.getBounds(): " + expandComp.getBounds());
        Point l = new Point();
        for (int i = expandCompIndex + 1; i < comps.length; ++i) {
          if (direction == DIRECTION_HORIZONTAL) {
            comps[i].getLocation(l);
            l.x += delta;
            comps[i].setLocation(l);
          }
          else {
            comps[i].getLocation(l);
            l.y += delta;
            comps[i].setLocation(l);
          }
        }
      }
    }

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
