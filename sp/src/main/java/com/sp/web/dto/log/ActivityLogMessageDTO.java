package com.sp.web.dto.log;

import com.sp.web.model.log.ActivityLogMessage;

/**
 * @author Dax Abraham
 * 
 *         The activity log message DTO.
 */
public class ActivityLogMessageDTO extends BaseLogMessageDTO {

  private static final long serialVersionUID = 5236840675352004188L;

  /**
   * Constructor.
   * 
   * @param logMessage
   *          - log message
   */
  public ActivityLogMessageDTO(ActivityLogMessage logMessage) {
    super(logMessage);
  }

}
