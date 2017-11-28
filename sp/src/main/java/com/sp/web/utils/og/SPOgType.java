package com.sp.web.utils.og;

import com.sp.web.model.SPFeature;

import org.apache.commons.lang3.ArrayUtils;

/**
 * SPOgType defines the different OG pages where OG is to defined for pages.
 * 
 * @author pradeepruhil
 *
 */
public enum SPOgType {
  
  Erti(SPFeature.Erti, "/sp/goals#/practice"), Competency(SPFeature.Competency,
      "/sp/competency/home#/profiles"), OrganizationPlan(SPFeature.OrganizationPlan,
      "/sp/goalsOrg#/organization"), KnowledgeCenter(SPFeature.Prism, "/sp/trainingLibrary"), Blueprint(
      SPFeature.Blueprint, "/sp/blueprint#/publish"), Spectrum(SPFeature.Spectrum, "/sp/spectrum"), CompetencyManager(
      SPFeature.Competency, "/sp/admin/competency/manager"), Hiring(SPFeature.Prism,
      "/sp/hiring"), Pulse(SPFeature.Pulse, "/sp/pulse"), Prism(SPFeature.Prism,
      "/sp/profile"), Personality(SPFeature.Prism, "/sp/profile#/personality"), ProcessingBlueprint(
      SPFeature.Prism, "/sp/profile#/processingBlueprint"), Motivation(SPFeature.Prism,
      "/sp/profile#/motivation"), ConflictManagement(SPFeature.Prism,
      "/sp/profile#/conflictManagement"), FundamentalNeeds(SPFeature.Prism,
      "/sp/profile#/fundamentalNeeds"), DecisionMaking(SPFeature.Prism,
      "/sp/profile#/decisionMaking"), LearningStyle(SPFeature.Prism, "/sp/profile#/learningStyle"), PrismLens(
      SPFeature.PrismLens, "/sp/feedback"), RelationShipAdvisor(SPFeature.RelationShipAdvisor,
      "/sp/prism/relationship-advisor"), Article(SPFeature.Prism, "/sp/trainingLibrary/article"), Default(
      SPFeature.Prism, "https://www.surepeople.com");
  
  private String[] urlMapping;
  
  private SPFeature spFeature;
  
  private SPOgType(SPFeature spFeature, String... urlMapping) {
    this.urlMapping = urlMapping;
    this.spFeature = spFeature;
  }
  
  public String[] getUrlMapping() {
    return urlMapping;
  }
  
  /**
   * getOgType will return the og type for the URL.
   * 
   * @param url
   *          for which OG type is to be found.
   * @return the OGTYpe.
   */
  public static SPOgType getOgType(String url) {
    
    for (SPOgType ogType : SPOgType.values()) {
      
      String[] urlMapping = ogType.getUrlMapping();
      boolean isOgType = ArrayUtils.contains(urlMapping, url);
      /* special check for article URL, as article URL is dynamic */
      if (url.contains("sp/trainingLibrary/article")) {
        isOgType = true;
        ogType = SPOgType.Article;
      }
      if (isOgType) {
        return ogType;
      }
    }
    return Default;
  }
  
  public SPFeature getSpFeature() {
    return spFeature;
  }
}
