package com.sp.web.service.hiring.match.processor;

import com.sp.web.assessment.processing.AnalysisBean;
import com.sp.web.assessment.questions.CategoryType;
import com.sp.web.dao.hiring.match.PortraitMatcherData;
import com.sp.web.dto.hiring.user.MatchDetailsDTO;
import com.sp.web.model.hiring.match.MatchCriteria;
import com.sp.web.model.hiring.match.PortraitDataMatch;

import java.util.List;
import java.util.Map;

/**
 * 
 * @author Dax Abraham
 *
 *         The portrait match processor interface to load as well as process the match.
 */
public interface PortraitMatchProcessor {
  
  /**
   * This method will be called for the processor to set the category type.
   * 
   * @param categoryType
   *            - category type
   */
  void setCategoryType(CategoryType categoryType);
  
  /**
   * Load the match data into the report and matcher maps.
   * 
   * @param categoryType
   *          - category type
   * @param matchData
   *          - match data
   * @param portraitMatchReportData
   *          - the report data to load
   * @param portraitMacher
   *          - the matcher data
   */
  void loadData(CategoryType categoryType, Map<String, PortraitDataMatch> matchData,
      Map<String, List<MatchCriteria>> reportData, Map<String, PortraitMatcherData> matcherData);
  
  /**
   * The method to process the match.
   * 
   * @param analysis
   *          - user analysis
   * @param portraitMatchData
   *          - portrait match data
   * @param matchResult
   *          - the object to update the match results into
   */
  void process(AnalysisBean analysis, Map<String, PortraitMatcherData> portraitMatchData,
      Map<String, MatchDetailsDTO> matchResult);
}
