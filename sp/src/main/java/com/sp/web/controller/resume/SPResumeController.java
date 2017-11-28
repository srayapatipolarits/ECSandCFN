package com.sp.web.controller.resume;

import com.sp.web.controller.ControllerHelper;
import com.sp.web.form.resume.SPResumeForm;
import com.sp.web.mvc.SPResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * SPResumeController contains all the request for the user resume.
 * 
 * @author pradeepruhil
 */
@Controller
public class SPResumeController {

  @Autowired
  private SPResumeControllerHelper resumeControllerHelper;

  @Autowired
  ServletContext context;

  /**
   * <code>getResumeList</code> method will return all the lsit of reusmes.
   * 
   * @param token
   *          loggeed in user
   * @return the sp resposne.
   */
  @RequestMapping(value = "/resume/getAllResumes", method = RequestMethod.GET)
  @ResponseBody
  public SPResponse getResumeList(Authentication token) {
    return ControllerHelper.process(resumeControllerHelper::getUserResume, token);
  }

  /**
   * <code>createResume</code> method will return all the lsit of reusmes.
   * 
   * @param token
   *          loggeed in user
   * @return the sp resposne.
   */
  @RequestMapping(value = "/resume/createResume", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse createResume(SPResumeForm resumeForm, Authentication token) {
    String realPath = context.getRealPath("");
    return ControllerHelper.process(resumeControllerHelper::createResume, token, resumeForm, realPath);
  }

  /**
   * <code>getResumeList</code> method will return all the lsit of reusmes.
   * 
   * @param token
   *          loggeed in user
   * @return the sp resposne.
   */
  @RequestMapping(value = "/resume/previewResume", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse previewResume(SPResumeForm resumeForm, Authentication token,
      HttpSession session) {
    String realPath = context.getRealPath("");
    return ControllerHelper.process(resumeControllerHelper::previewResume, token, resumeForm, realPath, session);
  }

  @RequestMapping(value = "/resume/getPreviewPdf", method = RequestMethod.GET)
  @ResponseBody
  public SPResponse getPreviewPdf(Authentication token, HttpSession session,
      HttpServletResponse response) {
    return ControllerHelper.process(resumeControllerHelper::getPreviewPdf, token, session,response);
  }

  /**
   * <code>getResumePdf</code> method will return the resume in pdf format.
   * 
   * @param token
   *          loggeed in user
   * @return the sp resposne.
   */
  @RequestMapping(value = "/resume/getResume/{resumeId}", method = RequestMethod.GET)
  @ResponseBody
  public SPResponse getResumePdf(@PathVariable String resumeId, Authentication token,
      HttpServletResponse response) {
    return ControllerHelper.process(resumeControllerHelper::getResumePdf, token, resumeId, response);
  }

  /**
   * View For Resume Home Page.
   */
  @RequestMapping(value = "/spResume", method = RequestMethod.GET)
  public String getResumeStart(Authentication token) {
    return "resumeStart";
  }

  /**
   * View For Resume Home Page.
   */
  @RequestMapping(value = "/createResume", method = RequestMethod.GET)
  public String createResumeView(Authentication token) {
    return "createResume";
  }

  /**
   * View For Share.
   */
  @RequestMapping(value = "/messageAll", method = RequestMethod.GET)
  public String messageAll(Authentication token) {
    return "messagePop";
  }

  /**
   * deleteConfirmation.
   */
  @RequestMapping(value = "/resume/deleteConfirmation", method = RequestMethod.GET)
  public String deleteConfirmation(Authentication token) {
    return "deleteConfirmationResume";
  }

  /**
   * <code>deleteResume</code> method will delete the resume
   * 
   * @param token
   *          loggeed in user
   * @return the sp resposne.
   */
  @RequestMapping(value = "/resume/delete/{resumeId}", method = RequestMethod.GET)
  @ResponseBody
  public SPResponse deleteResume(@PathVariable String resumeId, Authentication token,
      HttpServletResponse response) {
    return ControllerHelper.process(resumeControllerHelper::deleteResume, token, resumeId, response);
  }

  /**
   * <code>getResumeList</code> method will return all the lsit of reusmes.
   * 
   * @param token
   *          loggeed in user
   * @return the sp resposne.
   */
  @RequestMapping(value = "/resume/share", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse shareResume(@RequestParam String resumeId, @RequestParam String to, @RequestParam String subject,
      @RequestParam String body, Authentication token) {
    return ControllerHelper.process(resumeControllerHelper::shareResume, token, resumeId, to, subject, body);
  }

}
