package com.sp.web.service.pdf;

import com.sp.web.model.Token;
import com.sp.web.service.token.TokenProcessor;

import org.springframework.stereotype.Component;

/**
 * Docraptor token process will handle the token process URL.
 * 
 * @author pradeepruhil
 *
 */
@Component("docraptorPdfTokenProcessor")
public class DocraptorTokenProcessor implements TokenProcessor {
  
  @Override
  public void processToken(Token token) {
    
    PdfSourceType type = PdfSourceType.valueOf((String) token.getParam("type"));
    switch (type) {
    case MedixTalent:
      token.setRedirectToView("medixPrismCandidate");
      break;
    case MedixRecruiter:
      token.setRedirectToView("medixPrismRecuiter");
      break;
    
    case SurePeoplePrism:
      token.setRedirectToView("surepeoplePrism");
    default:
      break;
    }
  }
  
}
