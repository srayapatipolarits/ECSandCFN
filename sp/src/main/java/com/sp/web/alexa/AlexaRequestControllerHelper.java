
package com.sp.web.alexa;

import com.amazon.speech.speechlet.Speechlet;
import com.amazon.speech.speechlet.SpeechletRequestHandlerException;
import com.amazon.speech.speechlet.SpeechletToSpeechletV2Adapter;
import com.amazon.speech.speechlet.SpeechletV2;
import com.amazon.speech.speechlet.servlet.ServletSpeechletRequestHandler;
import com.sp.web.model.User;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * AlexaRequestControllerHelper method is request handler for the alexa request.
 * 
 * @author pradeepruhil
 *
 */
@Component
public class AlexaRequestControllerHelper extends ServletSpeechletRequestHandler {
  
  /** Initializing the logger. */
  private static final Logger log = Logger.getLogger(AlexaRequestControllerHelper.class);
  
  @Autowired
  @Qualifier("alexaServiceHandler")
  private Speechlet speechlet;
  
  private SpeechletV2 speechletV2;
  
  /**
   * getAlexaResponse method will return the alexa response for the user.
   * 
   * @param user
   *          who has logged in.
   * @param request
   *          http servlet request.
   * @throws IOException
   *           exception.
   */
  public void getAlexaResponse(User user, HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    byte[] outputBytes = null;
    /** initlaize the speechv2 */
    if (speechletV2 == null) {
      speechletV2 = new SpeechletToSpeechletV2Adapter(speechlet);
    }
    /* get the request body */
    byte[] bytes = (byte[]) request.getAttribute("SPEECH_REQUEST");
    
    if (log.isDebugEnabled()) {
      log.debug("Specch request returned is " + bytes);
    }
    try {
      outputBytes = handleSpeechletCall(speechletV2, bytes);
    } catch (SpeechletRequestHandlerException | SecurityException ex) {
      int statusCode = HttpServletResponse.SC_BAD_REQUEST;
      log.error("Exception occurred in doPost, returning status code {}", statusCode, ex);
      response.sendError(statusCode, ex.getMessage());
    } catch (Exception ex) {
      int statusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
      log.error("Exception occurred in doPost, returning status code {}", statusCode, ex);
      response.sendError(statusCode, ex.getMessage());
    }
    
    response.setContentType("application/json");
    response.setStatus(HttpServletResponse.SC_OK);
    try (final OutputStream out = response.getOutputStream()) {
      response.setContentLength(outputBytes.length);
      out.write(outputBytes);
    }
  }
}
