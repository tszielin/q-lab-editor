package tszielin.qlab.util.event;

public class DataEvent<T> extends java.util.EventObject {
  private static final long serialVersionUID = -3719306486107594627L;
  protected transient T data;

  public DataEvent(Object source, T data) {
    super(source);
    this.data = data;
  }

  public T getData() {
    return this.data;
  }
}
