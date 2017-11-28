package com.sp.web.controller.sse;

import com.sp.web.model.User;
import com.sp.web.service.sse.SseEventFactory;
import com.sp.web.utils.GenericUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

import javax.servlet.http.HttpSession;

/**
 * <code>SseController</code> class will send the server side events to the
 * client.
 * 
 * @author pradeepruhil
 *
 */
@Controller
public class SseController {

  @Autowired
  private SseEventFactory eventFactory;
  
  /**
   * getMessage method is the message event from the server which is to be sent
   * to client.
   * 
   * @param authentication
   *          Logged in User
   * @param httpSession
   *          http session of the user.
   * @return sse emmitter
   * @throws IOException
   *           in case exceptio occurred.
   */
  @RequestMapping("/sse/push")
  public SseEmitter getMessage(Authentication authentication,
      HttpSession httpSession) throws IOException {
    User user = GenericUtils.getUserFromAuthentication(authentication);
    SseEmitter sseEmitter = eventFactory.createSseEmitter(user.getId(),
        user.getCompanyId(), httpSession.getId());
    return sseEmitter;
  }
}
