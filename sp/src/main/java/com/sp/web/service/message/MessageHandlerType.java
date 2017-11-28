package com.sp.web.service.message;

/**
 * MessageHandler type is the handler type of the message.
 * 
 * @author pradeepruhil
 *
 */
public enum MessageHandlerType {
  
  EngagementMatrix("engagementMatrixHandler");
  
  private String type;
  
  /**
   * Constructor intializing the message handler.
   * 
   * @param type
   *          is th spring bean name to be called by the messaging processor.
   */
  private MessageHandlerType(String type) {
    this.type = type;
  }
  
  /**
   * getType method return the message handler type;
   * 
   * @return
   */
  public String getType() {
    return type;
  }
  
}
