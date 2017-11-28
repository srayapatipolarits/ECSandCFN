package com.sp.web.controller.pdf;

import com.sp.web.exception.InvalidRequestException;
import com.sp.web.exception.SPException;
import com.sp.web.model.User;
import com.sp.web.mvc.SPResponse;
import com.sp.web.service.phantom.PhantomService;
import com.sp.web.utils.GenericUtils;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * <code>PdfGenerationControllerHelper</code> class is the helper class for generating pdf.
 * 
 * @author pradeepruhil
 *
 */
@Component
public class PdfGenerationControllerHelper {
  
  private static final Logger LOG = Logger.getLogger(PdfGenerationControllerHelper.class);
  @Autowired
  private PhantomService phantomService;
  
  @Autowired
  private Environment environment;
  
  private static final String PDF = ".pdf";
  
  private static final String HTTP_LOCALHOST_8080_SP = "phatomjs.server.hostname";
  
  private static final String PDF_HTML = "pdf.html";
  
  private static final String RESOURCES = "/resources/";
  
  private static final String RESOURCES_JS_SP_PHANTOM_PDF_JS = "/resources/pdf/spPhantomPdf.js";
  
  /**
   * <code>generatePdf</code> will generate the pdf.
   * 
   * @param user
   *          current logged in user.
   * @param param
   *          contains the paramter
   * @return the bytecode for the response.
   */
  public SPResponse generatePdf(User user, Object[] param) {
    String html = (String) param[0];
    if (StringUtils.isEmpty(html)) {
      throw new InvalidRequestException("Input html cannot be null or empty");
    }
    String context = (String) param[1];
    HttpSession session = (HttpSession) param[2];
    String pdfName = (String) param[3];
    
    LOG.debug("Html for which pdf is to be gerneated " + html);
    byte[] byteArray = generatePDFByte(html, user, context);
    
    /*
     * Save the pdf in sesssion so that it will be used to get the pdf in new tab
     */
    session.setAttribute("previewPdf", byteArray);
    SPResponse spResponse = new SPResponse();
    spResponse.isSuccess();
    if (StringUtils.isNotEmpty(pdfName)) {
      spResponse.add("url", "/sp/pdf/getPdf/" + pdfName);
    } else {
      spResponse.add("url", "/sp/pdf/getPdf/");
    }
    
    return spResponse;
    
  }
  
  /**
   * <code>getPreviewPdf</code> methdo will return the preview pdf
   * 
   * @param user
   *          logged in user
   * @param param
   *          contains the param
   * @return null as we have to show the pdf.
   */
  public SPResponse getGeneratedPdf(User user, Object[] param) {
    HttpServletResponse httpServletResponse = (HttpServletResponse) param[1];
    HttpSession httpSession = (HttpSession) param[0];
    SPResponse response = new SPResponse();
    /* get the pdf */
    byte[] byteArray = (byte[]) httpSession.getAttribute("previewPdf");
    if (byteArray != null) {
      GenericUtils.generatePDF(httpServletResponse, byteArray);
    } else {
      response.addError("message", "Please recreate preview for the resume");
    }
    return response;
  }
  
  /**
   * generatePDFByte will return the bytes of the pdf generated.
   * 
   * @param html
   *          for which pdf is to be generated.
   * @param user
   *          logged in user
   * @param context
   *          applicationc ontext path.
   * @return the pdf.
   */
  private byte[] generatePDFByte(String html, User user, String context) {
    String fileNamePath = RESOURCES + user.getId() + PDF_HTML;
    File htmlFile = new File(context + fileNamePath);
    
    try {
      if (!htmlFile.exists()) {
        htmlFile.createNewFile();
      }
      String resumeContent = new String(html.getBytes(), "UTF-8");
      resumeContent = URLDecoder.decode(resumeContent, "UTF-8");
      resumeContent = resumeContent.replaceAll("“", "&ldquo;").replaceAll("”", "&rdquo;");
      resumeContent = resumeContent.replaceAll("‘", "&rsquo;").replaceAll("’", "&rsquo;");
      resumeContent = resumeContent.replaceAll("•", "&bull;");
      resumeContent = resumeContent.replaceAll("–", "&ndash;");
      
      resumeContent = resumeContent.replaceAll("&amp;", "&");
      FileOutputStream fileOutputStream = new FileOutputStream(htmlFile);
      IOUtils.write(resumeContent, fileOutputStream, "UTF-8");
    } catch (Exception e) {
      LOG.error("File Not found", e);
      throw new SPException("Exception occurred while creating resume, Please try after some time");
    }
    String scripthPath = context + RESOURCES_JS_SP_PHANTOM_PDF_JS;
    String phantomHostName = environment.getProperty(HTTP_LOCALHOST_8080_SP);
    phantomService.creatPdf(phantomHostName + fileNamePath, user.getId() + PDF, scripthPath);
    LOG.debug("Pdf Generated succesfully");
    byte[] byteArray = null;
    try {
      /* Read the pdf file */
      File pdfFile = new File(user.getId() + PDF);
      InputStream input = new FileInputStream(pdfFile);
      byteArray = IOUtils.toByteArray(input);
      
      pdfFile.delete();
      htmlFile.delete();
      
    } catch (IOException e) {
      LOG.error("Not able to find the pdf.", e);
    }
    return byteArray;
  }
}
