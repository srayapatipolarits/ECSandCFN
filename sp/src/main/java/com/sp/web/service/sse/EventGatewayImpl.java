package com.sp.web.service.sse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

/**
 * Event Gateway IMpl for the sendig mesage to the JMS.
 * 
 * @author Dax Abraham
 * 
 */
@Component("eventGateway")
@Profile("PROD")
public class EventGatewayImpl implements EventGateway {

  private static final Logger log = Logger.getLogger(EventGatewayImpl.class);

  @Autowired
  private JmsTemplate jmsTemplate;

  @Override
  public void sendEvent(MessageEventRequest messageEventRequest) {

    /* check of mandatory field */
    if (StringUtils.isBlank(messageEventRequest.getCompanyId())) {
      log.error("Company Id is not present, Cannot send event request "
          + messageEventRequest.getEventActionType());
      return;
    }
    log.debug("Received request :" + messageEventRequest.getEventActionType()
        + ":" + messageEventRequest.getUserIds() + ":"
        + messageEventRequest.getMessagePayLoad());
    jmsTemplate.convertAndSend(messageEventRequest);
  }

}
