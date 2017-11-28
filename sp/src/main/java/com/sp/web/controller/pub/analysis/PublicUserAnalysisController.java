package com.sp.web.controller.pub.analysis;

import com.sp.web.Constants;
import com.sp.web.controller.ControllerHelper;
import com.sp.web.controller.profile.ProfileControllerHelper;
import com.sp.web.dto.BaseUserDTO;
import com.sp.web.model.HiringUser;
import com.sp.web.model.Token;
import com.sp.web.mvc.SPResponse;
import com.sp.web.service.hiring.user.HiringUserFactory;
import com.sp.web.service.pdf.PdfSourceType;
import com.sp.web.service.token.SPTokenFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * PublicUserAnalysisController provides the user analysis to the user.
 * 
 * @author pradeepruhil
 *
 */
@Controller
public class PublicUserAnalysisController {
  
  @Autowired
  private SPTokenFactory spTokenFactory;
  
  @Autowired
  private ProfileControllerHelper helper;
  
  @Autowired
  private HiringUserFactory hiringUserFactory;
  
  /**
   * getTokenResponse for public analysis method will return the analysis for the user.
   * 
   * @param tokenId
   *          is the token id.
   * @return the public user analysis controller.
   */
  @RequestMapping(value = "/profilePublic/analysis/{tokenId}", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getTokenResponse(@PathVariable String tokenId) {
    
    Token token = spTokenFactory.findTokenById(tokenId);
    HiringUser user = hiringUserFactory.getUser(token.getParamAsString("hiringUserId"));
    PdfSourceType pdfSource = PdfSourceType.valueOf((String) token.getParam("type"));
    SPResponse response = null;
    switch (pdfSource) {
    case MedixRecruiter:
    case MedixTalent:
      response = helper.getFullAnalysis(user, null);
      break;
    default:
      break;
    }
    if (response == null) {
      response = new SPResponse();
    }
    response.add(Constants.PARAM_USER, new BaseUserDTO(user));
    return response;
  }
}
