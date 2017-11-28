package com.sp.web.dao.hiring.match;

import com.sp.web.assessment.questions.CategoryType;
import com.sp.web.model.hiring.match.HiringPortrait;
import com.sp.web.model.hiring.match.MatchCriteria;
import com.sp.web.service.hiring.match.processor.HiringPortraitMatchProcessorFactory;

import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 
 * @author Dax Abraham
 *
 *         The DAO class for the hiring portrait instance.
 */
@Document(collection = "hiringPortrait")
public class HiringPortraitDao extends HiringPortrait {
  
  @Transient
  private int traitsCount;
  @Transient
  private Map<CategoryType, Map<String, List<MatchCriteria>>> portraitMatchReportData;
  @Transient
  private Map<CategoryType, Map<String, PortraitMatcherData>> portraitMacher;
  
  /**
   * Default constructor.
   */
  public HiringPortraitDao() {
  }
  
  /**
   * Constructor.
   * 
   * @param portrait
   *          - the portrait
   */
  public HiringPortraitDao(HiringPortrait portrait, HiringPortraitMatchProcessorFactory factory) {
    BeanUtils.copyProperties(portrait, this);
    load(factory);
  }
  
  /**
   * Load the portrait match report and matcher data.
   * 
   * @param factory
   *          - factory
   */
  private void load(HiringPortraitMatchProcessorFactory factory) {
    portraitMatchReportData = new HashMap<CategoryType, Map<String, List<MatchCriteria>>>();
    portraitMacher = new HashMap<CategoryType, Map<String, PortraitMatcherData>>();
    getCategoryDataMap().forEach(
        (categoryType, matchData) -> factory.loadPortraitData(categoryType, matchData,
            portraitMatchReportData, portraitMacher));
    traitsCount = portraitMatchReportData.values().stream().mapToInt(Map::size).sum();
  }
  
  public Map<CategoryType, Map<String, List<MatchCriteria>>> getPortraitMatchReportData() {
    return portraitMatchReportData;
  }
  
  public void setPortraitMatchReportData(
      Map<CategoryType, Map<String, List<MatchCriteria>>> portraitMatchReportData) {
    this.portraitMatchReportData = portraitMatchReportData;
  }
  
  public int getTraitsCount() {
    return traitsCount;
  }
  
  public void setTraitsCount(int traitsCount) {
    this.traitsCount = traitsCount;
  }
  
  public Map<CategoryType, Map<String, PortraitMatcherData>> getPortraitMacher() {
    return portraitMacher;
  }
  
  public void setPortraitMacher(Map<CategoryType, Map<String, PortraitMatcherData>> portraitMacher) {
    this.portraitMacher = portraitMacher;
  }

  public boolean isAuthorized(String companyId) {
    return Optional.ofNullable(getCompanyIds()).map(companyIds -> companyIds.contains(companyId))
        .orElse(false);
  }
}
