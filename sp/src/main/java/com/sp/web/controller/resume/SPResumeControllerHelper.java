package com.sp.web.controller.resume;

import com.sp.web.Constants;
import com.sp.web.controller.notifications.NotificationType;
import com.sp.web.controller.social.LinkedinControllerHelper;
import com.sp.web.dto.resume.SPResumeDTO;
import com.sp.web.exception.InvalidRequestException;
import com.sp.web.exception.SPException;
import com.sp.web.form.resume.SPResumeForm;
import com.sp.web.model.User;
import com.sp.web.model.resume.SPResume;
import com.sp.web.mvc.SPResponse;
import com.sp.web.repository.resume.SPResumeRepository;
import com.sp.web.service.email.CommunicationGateway;
import com.sp.web.service.email.DataSourceAttahcment;
import com.sp.web.service.email.EmailParams;
import com.sp.web.service.pdf.PDFCreatorService;
import com.sp.web.service.phantom.PhantomService;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * <code>SpResumeControllerHelper</code> class is a helper class for sp resume.
 * 
 * @author pradeepruhil
 */
@Component
public class SPResumeControllerHelper {

  /** Initializng the logger. */
  private static final Logger LOG = Logger.getLogger(SPResumeControllerHelper.class);

  @Autowired
  private SPResumeRepository resumeRepository;

  @Autowired
  @Qualifier("itextPdfService")
  private PDFCreatorService pdfCreatorService;

  @Autowired
  private LinkedinControllerHelper linkedInHelper;

  @Autowired
  private CommunicationGateway communicationGateway;

  @Autowired
  private PhantomService phantomService;

  private static final String PDF = ".pdf";

  private static final String HTTP_LOCALHOST_8080_SP = "http://localhost:8080/sp";

  private static final String RESUME_HTML = "resume.html";

  private static final String RESOURCES = "/resources/";

  private static final String RESOURCES_JS_SP_PHANTOM_PDF_JS = "/resources/js/spPhantomPdf.js";
  
  /**
   * <code>getUserResume</code> will return all the user resume which he has
   * created.
   * 
   * @param user
   *          logged individual user.
   * @return the sp response
   */
  public SPResponse getUserResume(User user) {
    SPResponse response = new SPResponse();
    List<SPResume> allResume = resumeRepository.getAllResume(user.getId());

    LOG.debug("Returning the resume for the user" + user.getEmail() + ", Resume " + allResume);

    response.add("resume", allResume.stream().map(SPResumeDTO::new).collect(Collectors.toList()));
    return response;
  }

  /**
   * <code>createResume</code> will create the user resume
   * 
   * @param user
   *          logged in user.
   * @param param
   *          contains the information for the user.
   * @return the message for resume creation.
   */
  public SPResponse createResume(User user, Object[] param) {

    SPResumeForm resumeForm = (SPResumeForm) param[0];
    if (StringUtils.isBlank(resumeForm.getContent())) {
      throw new InvalidRequestException("Invalid request parameters");
    }
    String context = (String) param[1];
    byte[] byteArray = generatePDFByte(resumeForm, user, context);
    SPResume resume = new SPResume();
    resume.setDateCreated(LocalDate.now());
    resume.setBytes(byteArray);
    resume.setFileName(user.getFirstName() + user.getLastName() + "_" + resumeForm.getRole());
    resume.setUserRole(resumeForm.getRole());
    resume.setResumeForId(user.getId());
    resume.setProviderName(resumeForm.getProviderName());
    resumeRepository.createResume(resume);

    SPResponse response = new SPResponse();
    response.isSuccess();
    return response;
  }

  /**
   * previewResume method will preview resume.
   * 
   * @param user
   *          logged in user.
   * @param param
   *          contain the parameter.
   * @return the pdf.
   */
  public SPResponse previewResume(User user, Object[] param) {
    SPResumeForm resumeForm = (SPResumeForm) param[0];
    String context = (String) param[1];
    HttpSession httpSession = (HttpSession) param[2];
    byte[] byteArray = generatePDFByte(resumeForm, user, context);

    /*
     * Save the pdf in sesssion so that it will be used to get the pdf in new
     * tab
     */
    httpSession.setAttribute("previewPdf", byteArray);
    SPResponse spResponse = new SPResponse();
    spResponse.isSuccess();
    spResponse.add("url", "/sp/resume/getPreviewPdf");
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
  public SPResponse getPreviewPdf(User user, Object[] param) {
    HttpServletResponse httpServletResponse = (HttpServletResponse) param[1];
    HttpSession httpSession = (HttpSession) param[0];
    SPResponse response = new SPResponse();
    /* get the pdf */
    byte[] byteArray = (byte[]) httpSession.getAttribute("previewPdf");
    if (byteArray != null) {
      generatePDF(httpServletResponse, byteArray);
    } else {
      response.addError("message", "Please recreate preview for the resume");
    }
    return response;
  }

  /**
   * getREsumePdf will return the pdf to the use.
   * 
   * @param user
   *          logged in user.
   * @param param
   *          array.
   * @return the resume pdf.
   */
  public SPResponse getResumePdf(User user, Object[] param) {

    String resumeId = (String) param[0];
    HttpServletResponse response = (HttpServletResponse) param[1];

    SPResume resume = resumeRepository.getPdfDocument(resumeId);
    generatePDF(response, resume.getBytes());
    return null;
  }

  /**
   * getREsumePdf will return the pdf to the use.
   * 
   * @param user
   *          logged in user.
   * @param param
   *          array.
   * @return the resume pdf.
   */
  public SPResponse deleteResume(User user, Object[] param) {

    String resumeId = (String) param[0];
    SPResponse response = new SPResponse();
    resumeRepository.deleteResume(resumeId);
    response.isSuccess();
    return response;
  }

  /**
   * <code>shareResume</code> will share the resume to the user.
   * 
   * @param user
   *          who wants to share the resume
   * @param param
   *          contains the parameters.
   * @return the share resume functionaltiy.
   */
  public SPResponse shareResume(User user, Object[] param) {
    String resumeId = (String) param[0];
    String to = (String) param[1];
    String subject = (String) param[2];

    SPResume spresume = resumeRepository.getPdfDocument(resumeId);
    byte[] bytes = spresume.getBytes();
    ByteArrayOutputStream baos = new ByteArrayOutputStream(bytes.length);
    baos.write(bytes, 0, bytes.length);
    String body = (String) param[3];
    EmailParams emailParams = new EmailParams();
    emailParams.setTos(to);
    emailParams.setSubject(subject);
    emailParams.setViaFrom(true);
    emailParams.addParam(Constants.PARAM_NAME, user.getFirstName() + " " + user.getLastName().charAt(0));
    emailParams.addParam(Constants.PARAM_MESSAGE, body);
    emailParams.setTemplateName(NotificationType.ShareResume.getTemplateName());
    emailParams
        .addDataSourceAttachment(new DataSourceAttahcment(spresume.getFileName() + PDF, baos, "application/pdf"));
    communicationGateway.sendMessage(emailParams);
    SPResponse response = new SPResponse();
    response.isSuccess();
    return response;
  }

  /**
   * generatePDFByte method will generate the PDf and create the byte array of
   * it.
   * 
   * @param resumeForm
   *          contains the html from which pdf will be generated and converted
   *          into byte arrray.
   * @param user
   *          logged in user
   * @param context
   *          real path of the application.
   * @return the byte array.
   */
  private byte[] generatePDFByte(SPResumeForm resumeForm, User user, String context) {
    String fileNamePath = RESOURCES + user.getId() + RESUME_HTML;
    File htmlFile = new File(context + fileNamePath);

    try {
      if (!htmlFile.exists()) {
        htmlFile.createNewFile();
      }
      String resumeContent  = new String(resumeForm.getContent().getBytes(),"UTF-8");
      resumeContent = URLDecoder.decode(resumeContent, "UTF-8");
      resumeContent = resumeContent.replaceAll("“", "&ldquo;").replaceAll("”", "&rdquo;");
      resumeContent = resumeContent.replaceAll("‘", "&rsquo;").replaceAll("’", "&rsquo;");
      resumeContent = resumeContent.replaceAll("•", "&bull;");
      
      resumeContent = resumeContent.replaceAll("&amp;", "&");
      FileOutputStream fileOutputStream = new FileOutputStream(htmlFile);
      IOUtils.write(resumeContent, fileOutputStream,"UTF-8");
    } catch (Exception e) {
      LOG.error("File Not found",e);
      throw new SPException("Exception occurred while creating resume, Please try after some time");
    }

    String scripthPath = context + RESOURCES_JS_SP_PHANTOM_PDF_JS;
    
    phantomService.creatPdf(HTTP_LOCALHOST_8080_SP + fileNamePath,
        user.getId() + PDF, scripthPath);
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

  /**
   * generatePDF will generate the PDF from the byte date.
   * 
   * @param response
   *          httpservlet response
   * @param bytes
   *          byte array
   */
  private void generatePDF(HttpServletResponse response, byte[] bytes) {
    ByteArrayOutputStream baos = new ByteArrayOutputStream(bytes.length);
    baos.write(bytes, 0, bytes.length);
    response.setContentLength(baos.size());
    response.setContentType("application/pdf");
    response.setHeader("Expires", "0");
    response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
    response.setHeader("Pragma", "public");

    try {
      OutputStream os = response.getOutputStream();
      baos.writeTo(os);
      os.flush();
      os.close();

    } catch (IOException e) {
      e.printStackTrace();
    }

  }
}
