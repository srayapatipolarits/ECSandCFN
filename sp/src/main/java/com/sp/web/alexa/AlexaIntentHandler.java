package com.sp.web.alexa;

import com.amazon.speech.speechlet.SpeechletResponse;

/**
 * AlexaIntentHandler will handle all the intent of the alexa and give the response back to the
 * user.
 * 
 * @author pradeepruhil
 *
 */
public interface AlexaIntentHandler {
  
  /**
   * Execute method will execute the intent handler will return back the result.
   * 
   * @param intentRequest
   *          is the intent request.
   * @return the SpeechletResponse.
   */
  SpeechletResponse execute(SPIntentRequest intentRequest);
}
