package com.sp.web.service.hiring.match.processor;

import com.sp.web.assessment.processing.AnalysisBean;
import com.sp.web.assessment.questions.CategoryType;
import com.sp.web.dao.hiring.match.PortraitMatcherData;
import com.sp.web.dto.hiring.user.MatchDetailsDTO;
import com.sp.web.model.hiring.match.MatchCriteria;
import com.sp.web.model.hiring.match.PortraitDataMatch;

import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public abstract class AbstractPortraitMatchProcessor implements PortraitMatchProcessor {
  
  protected static final Logger log = Logger.getLogger(AbstractPortraitMatchProcessor.class);
  protected CategoryType categoryType;
  
  /**
   * Load the report data.
   * 
   * @param entry
   *          - the data to load
   * @param reportData
   *          - report data map
   * @return
   *    the entry
   */
  protected Entry<String, PortraitDataMatch> loadReport(Entry<String, PortraitDataMatch> entry,
      Map<String, List<MatchCriteria>> reportData) {
    reportData.put(entry.getKey(), entry.getValue().getMatchCriterias());
    return entry;
  }
  
  @Override
  public void setCategoryType(CategoryType categoryType) {
    this.categoryType = categoryType;
  }
  
  @Override
  public void loadData(CategoryType categoryType, Map<String, PortraitDataMatch> matchData,
      Map<String, List<MatchCriteria>> reportData, Map<String, PortraitMatcherData> matcherData) {
    matchData.entrySet()
             .stream()
             .filter(map -> map.getValue().isEnabled())
             .map(map -> loadReport(map, reportData))
             .forEach(map -> matcherData.put(map.getKey(), loadData(map.getValue())));
  }

  /**
   * Load the report data and the match data.
   * 
   * @param type
   *          - key type
   * @param matchData
   *          - match data
   * @param matcherData
   *          - matcher data
   */
  protected abstract PortraitMatcherData loadData(PortraitDataMatch value);
  
  @Override
  public abstract void process(AnalysisBean analysis,
      Map<String, PortraitMatcherData> portraitMatchData, Map<String, MatchDetailsDTO> matchResult);
  
}
