package com.sp.web.model.log;

/**
 * @author Dax Abraham
 *
 *         The activity log message.
 */
public class ActivityLogMessage extends LogMessage {

  private static final long serialVersionUID = -5592704600041311262L;

  /**
   * Default constructor.
   */
  public ActivityLogMessage() {
    super();
  }

  public ActivityLogMessage(LogActionType logType, String memberId) {
    super(logType, memberId);
  }
}
