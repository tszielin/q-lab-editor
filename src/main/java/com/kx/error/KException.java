package com.kx.error;


import tszielin.qlab.error.StudioException;

public class KException extends StudioException {
  private static final long serialVersionUID = 6343898126544336636L;

  public KException(String message) {
    super(message);
  }

  public KException(Throwable cause) {
    super(cause);
  }

  public KException(String message, Throwable cause) {
    super(message, cause);
  }
}
