package com.sp.web.service.sse;

import com.sp.web.Constants;
import com.sp.web.controller.systemadmin.SessionFactory;
import com.sp.web.exception.SPException;
import com.sp.web.model.SessionUpdateActionRequest;
import com.sp.web.utils.ApplicationContextUtils;

import org.apache.log4j.Logger;

import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

/**
 * JMSSubscriber is the JMS subscriber for the Surepeople. {
 * 
 * @author pradeepruhil
 *
 */
public class JMSSubscriber implements MessageListener {
  
  /** Initializing the logger. */
  private static final Logger LOG = Logger.getLogger(JMSSubscriber.class);
  
  /**
   * Method will read object message and invoke the corresponding event subscriber for the user.
   * 
   * @param Message
   *          message
   *
   */
  @Override
  public void onMessage(Message message) {
    
    if (LOG.isDebugEnabled()) {
      LOG.debug("Entering Subscriber onMessage.");
    }
    
    String correlationId = null;
    String messageId = null;
    
    if (message instanceof ObjectMessage) {
      
      try {
        
        correlationId = message.getJMSCorrelationID();
        messageId = message.getJMSCorrelationID();
        
        Object obj = ((ObjectMessage) message).getObject();
        if ((obj instanceof SessionUpdateActionRequest)) {
          SessionUpdateActionRequest request = (SessionUpdateActionRequest) obj;
          Map<String, Object> actionParams = request.getParams();
          SessionFactory sessionFactory = ApplicationContextUtils.getBean(SessionFactory.class);
          if (actionParams.containsKey(Constants.PARAM_COMPANY)) {
            sessionFactory.doCompanyUpdate(request);
          } else {
            sessionFactory.doUpdate(request);
          }
        } else if (obj instanceof MessageEventRequest) {
          EventSubscriber eventSubscriber = ApplicationContextUtils.getBean(EventSubscriber.class);
          MessageEventRequest request = (MessageEventRequest) obj;
          eventSubscriber.processEventRequest(request);
        }
        if (LOG.isDebugEnabled()) {
          LOG.debug("Message successfully processed" + " Correlation ID: " + correlationId
              + " Message ID:" + messageId);
        }
      } catch (JMSException ex) {
        LOG.error("Unable to read message from the Topic for User: " + ex);
        throw new SPException(ex);
      }
    } else {
      LOG.error("Message must be of type ObjectMessage.");
      throw new IllegalArgumentException("Message must be of type ObjectMessage");
    }
  }
  
}
