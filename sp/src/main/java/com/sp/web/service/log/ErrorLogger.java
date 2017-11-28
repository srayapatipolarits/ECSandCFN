package com.sp.web.service.log;

import org.apache.log4j.Logger;
import org.springframework.messaging.support.ErrorMessage;

/**
 * @author Dax Abraham
 * 
 *         The error logger for the service.
 */
public class ErrorLogger {

  private static final Logger log = Logger.getLogger(ErrorLogger.class);
  
  public void log(ErrorMessage errorMessage) {
    log.debug("Could not process message !!!", errorMessage.getPayload());
  }
}
