package com.sp.web.controller.spectrum;

import com.sp.web.controller.ControllerHelper;
import com.sp.web.mvc.SPResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * SpectrumController will provide the request interface for the spectrum tool.
 * 
 * @author pradeepruhil
 *
 */
@Controller
public class SpectrumController {
  
  @Autowired
  private SpectrumControllerHelper spectrumControllerHelper;
  
  /**
   * getProfileBalance method will return the profile balance json for the spectrum.
   * 
   * @param groupName
   *          groupName for which profile balance is to be retreived.
   * @param token
   *          of the user.
   * @return the profilebalance.
   */
  @RequestMapping(value = "/spectrum/getProfileBalance", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getProfileBalance(@RequestParam(required = false) String groupName,
      Authentication token) {
    return ControllerHelper.process(spectrumControllerHelper::getProfileBalance, token, groupName);
  }
  
  /**
   * <code>getLearnerStatus</code> method will return the learnr statistcs of the users.
   * 
   * @param groupName
   *          for which learner stats are to be returned.
   * @param authenticationToken
   *          user logged in
   * @return the SP Response.
   */
  @RequestMapping(value = "/spectrum/getLearnerStatus", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getLearnerStatus(@RequestParam(required = false) String groupName,
      Authentication authenticationToken) {
    return ControllerHelper.process(spectrumControllerHelper::getLearnerStatus,
        authenticationToken, groupName);
  }
  
  /**
   * <code>getHiringInsights</code> method will return the hiring insights of the user.
   * 
   * @param role
   *          of the hiringuser.
   * @param authenticationToken
   *          user logged in
   * @return the SP Response.
   */
  @RequestMapping(value = "/spectrum/getHiringInsights", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getHiringInsights(@RequestParam(required = false) String role,
      Authentication authenticationToken) {
    return ControllerHelper.process(spectrumControllerHelper::getHirngFilterInsights,
        authenticationToken, role);
  }
  
  /**
   * <code>getLearnerStatus</code> method will return the hiring candidate profile balance.
   * 
   * @param role
   *          of the hiring user.
   * @param authenticationToken
   *          user logged in
   * @return the SP Response.
   */
  @RequestMapping(value = "/spectrum/getHiringProfileBalance", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getHiringCandidateProfileBalance(@RequestParam(required = false) String role,
      Authentication authenticationToken) {
    return ControllerHelper.process(spectrumControllerHelper::getHirngFilterProfileBalance,
        authenticationToken, role);
  }
  
  /**
   * <code>getLearnerStatus</code> method will return the hiring candidate profile balance.
   * 
   * @param role
   *          of the hiring user.
   * @param authenticationToken
   *          user logged in
   * @return the SP Response.
   */
  @RequestMapping(value = "/spectrum/getHiringRoles", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getHiringRoles(Authentication authenticationToken) {
    return ControllerHelper.process(spectrumControllerHelper::getHiringRoles, authenticationToken);
  }
  
  /**
   * <code>getBluePrintAnalytics</code> method will return the blueprint analytics for a company.
   * 
   * @param authenticationToken
   *          user logged in
   * @return the SP Response.
   */
  @RequestMapping(value = "/spectrum/getBlueprintAnalytics", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getBlueprintAnalytics(Authentication authenticationToken) {
    return ControllerHelper.process(spectrumControllerHelper::getBlueprintAnalytics,
        authenticationToken);
  }
  
  /**
   * <code>getErtiInsights</code> method will return the erit insights for the all user.
   * 
   * @param authenticationToken
   *          user logged in
   * @return the SP Response.
   */
  @RequestMapping(value = "/spectrum/getErtiInsights", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getErtiInsights(Authentication authenticationToken) {
    return ControllerHelper.process(spectrumControllerHelper::getErtiInsights, authenticationToken);
  }
  
  /**
   * <code>getOrganizationPlanActivities</code> method will the organization plan activity for the
   * company.
   * 
   * @param authenticationToken
   *          user logged in
   * @return the SP Response.
   */
  @RequestMapping(value = "/spectrum/getOrganizationPlanActivities", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getOrganizationPlanActivities(Authentication authenticationToken) {
    return ControllerHelper.process(spectrumControllerHelper::getOrganizationPlanActivities,
        authenticationToken);
  }
  
  /**
   * <code>getErtiAnalytics</code> method will the return the erti analytics for the company.
   * 
   * @param authenticationToken
   *          user logged in
   * @return the SP Response.
   */
  @RequestMapping(value = "/spectrum/getErtiAnalytics", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getErtiAnalytics(Authentication authenticationToken) {
    return ControllerHelper
        .process(spectrumControllerHelper::getErtiAnalytics, authenticationToken);
  }
  
  /**
   * <code>getCompetencyInsights</code> method will the return the competency insights for the
   * company.
   * 
   * @param authenticationToken
   *          user logged in
   * @return the SP Response.
   */
  @RequestMapping(value = "/spectrum/getCompetencyInsights", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getCompetencyAnalytics(
      @RequestParam(required = false) String competencyProfileId, Authentication authenticationToken) {
    return ControllerHelper.process(spectrumControllerHelper::getCompetencyInsights,
        authenticationToken, competencyProfileId);
  }
  
  /**
   * <code>getEngagementMatrix</code> method will the return the engagement matrix of the company.
   * company.
   * 
   * @param authenticationToken
   *          user logged in
   * @return the SP Response.
   */
  @RequestMapping(value = "/spectrum/getEngagementMatrix", method = RequestMethod.POST)
  @ResponseBody
  public SPResponse getEngagementMatrix(@RequestParam(required = false) String fromDate,
      @RequestParam(required = false) String endDate, Authentication authenticationToken) {
    return ControllerHelper.process(spectrumControllerHelper::getEngagementMatrix,
        authenticationToken, fromDate, endDate);
  }
  
  /**
   * View For Spectrum Home Page.
   * 
   */
  @RequestMapping(value = "/spectrum", method = RequestMethod.GET)
  public String validateSpectrum(Authentication token) {
    return "printCharts";
  }
  
}
