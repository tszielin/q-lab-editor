package tszielin.qlab.event;

import tszielin.qlab.util.event.DataEvent;

public class TabCountChanged extends DataEvent<Integer> {
  private static final long serialVersionUID = -2503768997924740001L;

  public TabCountChanged(Object source, int tabCount) {
    super(source, tabCount);
  }
}
