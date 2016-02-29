package tszielin.qlab.event;

import tszielin.qlab.util.event.DataEvent;

public class TabIndexChanged extends DataEvent<Integer> {
  private static final long serialVersionUID = 3570378659622195248L;

  public TabIndexChanged(Object source, int tabIndex) {
    super(source, tabIndex);
  }
}
