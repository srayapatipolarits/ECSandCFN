package com.sp.web.service.message;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class SPMessageEnvelop implements Serializable {
  
  private static final long serialVersionUID = 340099228062567996L;
  
  private Map<String, Object> payLoadData;
  
  private MessageHandlerType messageHandler;
  
  /**
   * getPayLoadData method will get the payload data.
   * 
   * @return the payload data.
   */
  public Map<String, Object> getPayLoadData() {
    if (payLoadData == null) {
      payLoadData = new HashMap<String, Object>();
    }
    return payLoadData;
  }
  
  public void setPayLoadData(Map<String, Object> payLoadData) {
    this.payLoadData = payLoadData;
  }
  
  public void setMessageHandler(MessageHandlerType messageHandler) {
    this.messageHandler = messageHandler;
  }
  
  public MessageHandlerType getMessageHandler() {
    return messageHandler;
  }
  
  /**
   * addData method will add the data to enveope
   * 
   * @param key
   *          key for the envelop data.
   * @param value
   *          of the data.
   */
  public void addData(String key, Object value) {
    getPayLoadData().put(key, value);
  }
  
  @Override
  public String toString() {
    return "SPMessageEnvelop [payLoadData=" + payLoadData + ", messageHandler=" + messageHandler
        + "]";
  }
  
}
