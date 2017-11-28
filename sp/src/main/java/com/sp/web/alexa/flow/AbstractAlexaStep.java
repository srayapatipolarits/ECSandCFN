package com.sp.web.alexa.flow;

import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.amazon.speech.ui.SimpleCard;
import com.sp.web.utils.MessagesHelper;

import java.util.Locale;

public abstract class AbstractAlexaStep implements Step {
  
  /**
   * create SpeechletAskResponse for the pased parameters
   * 
   * @param cardTitle
   * @param key
   * @param locale
   * @param objects
   * @return
   */
  public static SpeechletResponse createSpeechletAskResponse(String cardTitle, String key, Locale locale,
      Object... objects) {
    
    String speechText = MessagesHelper.getMessage(key, locale, objects);
    // Create the Simple card content.
    SimpleCard card = new SimpleCard();
    card.setTitle(cardTitle);
    card.setContent(speechText);
    
    // Create the plain text output.
    PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
    speech.setText(speechText);
    
    // Create reprompt
    Reprompt reprompt = new Reprompt();
    reprompt.setOutputSpeech(speech);
    
    return SpeechletResponse.newAskResponse(speech, reprompt, card);
  }
  
  /**
   * create SpeechletAskResponse for the pased parameters
   * 
   * @param cardTitle
   * @param key
   * @param locale
   * @param objects
   * @return
   */
  public SpeechletResponse createSpeechletTellResponse(String cardTitle, String key, Locale locale,
      Object... objects) {
    
    String speechText = MessagesHelper.getMessage(key, locale, objects);
    // Create the Simple card content.
    SimpleCard card = new SimpleCard();
    card.setTitle(cardTitle);
    card.setContent(speechText);
    
    // Create the plain text output.
    PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
    speech.setText(speechText);
    
    return SpeechletResponse.newTellResponse(speech, card);
  }
}
