package com.sp.web.model.log;

/**
 * @author Dax Abraham
 *
 *         The notification log message.
 */
public class NotificationLogMessage extends LogMessage {

  private static final long serialVersionUID = 1712827384272507712L;
  private String urlParam;
  private boolean read;

  /**
   * Default Constructor.
   */
  public NotificationLogMessage() {
    super();
  }

  /**
   * Constructor.
   * 
   * @param logActionType
   *            - log action type
   * @param memberId
   *            - member id
   */
  public NotificationLogMessage(LogActionType logActionType, String memberId) {
    super(logActionType, memberId);
  }

  public String getUrlParam() {
    return urlParam;
  }

  public void setUrlParam(String urlParam) {
    this.urlParam = urlParam;
  }

  public boolean isRead() {
    return read;
  }

  public void setRead(boolean read) {
    this.read = read;
  }
}
