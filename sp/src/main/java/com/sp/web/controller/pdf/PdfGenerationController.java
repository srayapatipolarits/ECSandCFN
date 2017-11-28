package com.sp.web.controller.pdf;

import com.sp.web.controller.ControllerHelper;
import com.sp.web.mvc.SPResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * PdfGenerationContoller will contain the pdf html and will generate the pdf.
 * @author pradeepruhil
 *
 */
@Controller
public class PdfGenerationController {
  
  @Autowired
  private PdfGenerationControllerHelper pdfGenerationControllerHelper;
  
  @Autowired
  ServletContext context;
  
  /**
   * <code>generatePdf</code> method will return all the lsit of reusmes.
   * 
   * @param token
   *          loggeed in user
   * @return the sp resposne.
   */
  @RequestMapping(value = "/pdf/generatePdf", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse generatePdf(@RequestParam String content,@RequestParam(required=false) String pdfName, Authentication token,
      HttpSession session) {
    String realPath = context.getRealPath("");
    return ControllerHelper.process(pdfGenerationControllerHelper::generatePdf, token, content, realPath, session, pdfName);
  }
  
  @RequestMapping(value = "/pdf/getPdf/*", method = RequestMethod.GET)
  @ResponseBody
  public SPResponse getPreviewPdf(Authentication token, HttpSession session,
      HttpServletResponse response) {
    return ControllerHelper.process(pdfGenerationControllerHelper::getGeneratedPdf, token, session,response);
  }

  
}
