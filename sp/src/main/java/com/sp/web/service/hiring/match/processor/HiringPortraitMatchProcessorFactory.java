package com.sp.web.service.hiring.match.processor;

import com.sp.web.Constants;
import com.sp.web.assessment.processing.AnalysisBean;
import com.sp.web.assessment.questions.CategoryPriority;
import com.sp.web.assessment.questions.CategoryType;
import com.sp.web.dao.hiring.match.HiringPortraitDao;
import com.sp.web.dao.hiring.match.PortraitMatcherData;
import com.sp.web.dto.hiring.match.HiringPortraitMatchResultDTO;
import com.sp.web.dto.hiring.user.MatchDetailsDTO;
import com.sp.web.model.hiring.match.MatchCriteria;
import com.sp.web.model.hiring.match.PortraitDataMatch;
import com.sp.web.utils.ApplicationContextUtils;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.DoubleAccumulator;

/**
 * 
 * @author Dax Abraham
 *
 *         The Hiring portrait match processor factory.
 */
@Component
public class HiringPortraitMatchProcessorFactory {
  
  private static final Logger log = Logger.getLogger(HiringPortraitMatchProcessorFactory.class);
  
  private Map<CategoryType, PortraitMatchProcessor> processorMap = new HashMap<CategoryType, PortraitMatchProcessor>();
  
  /**
   * Method to load the portrait data to the report and matcher maps.
   * 
   * @param categoryType
   *          - category type
   * @param matchData
   *          - match data
   * @param portraitMatchReportData
   *          - match report map
   * @param portraitMacher
   *          - matcher map
   */
  public void loadPortraitData(CategoryType categoryType, Map<String, PortraitDataMatch> matchData,
      Map<CategoryType, Map<String, List<MatchCriteria>>> portraitMatchReportData,
      Map<CategoryType, Map<String, PortraitMatcherData>> portraitMacher) {
    
    final Map<String, List<MatchCriteria>> reportData = new HashMap<String, List<MatchCriteria>>();
    final Map<String, PortraitMatcherData> matcherData = new HashMap<String, PortraitMatcherData>();
    getProcessor(categoryType).loadData(categoryType, matchData, reportData, matcherData);
    if (!reportData.isEmpty()) {
      portraitMatchReportData.put(categoryType, reportData);
      portraitMacher.put(categoryType, matcherData);
    }
  }
  
  /**
   * Method to process the portrait match.
   * 
   * @param portrait
   *          - portrait
   * @param analysis
   *          - user analysis
   * @return the match result
   */
  public HiringPortraitMatchResultDTO process(HiringPortraitDao portrait, AnalysisBean analysis) {
    
    // the match result to store the data in
    HiringPortraitMatchResultDTO matchResult = new HiringPortraitMatchResultDTO();
    
    // get the matcher
    Map<CategoryType, Map<String, PortraitMatcherData>> portraitMacher = portrait
        .getPortraitMacher();
    
    // map to store the match scores for each category priority
    Map<CategoryPriority, List<Double>> categoryPriorityScore = new HashMap<CategoryPriority, List<Double>>();
    
    // process the portrait matcher
    portraitMacher.forEach((key, value) -> process(key, value, analysis, matchResult,
        categoryPriorityScore));
    
    // calculate all the scores
    DoubleAccumulator matchScore = new DoubleAccumulator((val1, val2) -> val1 + val2,
        Double.MIN_VALUE);
    categoryPriorityScore.forEach((key, value) -> matchScore.accumulate(computeScore(value,
        portrait.getCategoryPriorityWeight(key))));
    
    // setting the match percentage
    matchResult.setMatchPercent(BigDecimal.valueOf(matchScore.doubleValue())
        .setScale(Constants.PRICE_ROUNDING_PRECISION, Constants.ROUNDING_MODE).intValue());
    return matchResult;
  }
  
  /**
   * Process the match.
   * 
   * @param categoryType
   *          - category type
   * @param portraitMatcherData
   *          - matcher data
   * @param analysis
   *          - user analysis
   * @param matchResult
   *          - match result
   * @param categoryPriorityScore
   *          - category price score to update
   */
  private void process(CategoryType categoryType,
      Map<String, PortraitMatcherData> portraitMatcherData, AnalysisBean analysis,
      HiringPortraitMatchResultDTO matchResult,
      Map<CategoryPriority, List<Double>> categoryPriorityScore) {
    
    // get the processor
    PortraitMatchProcessor processor = getProcessor(categoryType);
    
    // the category match result details
    Map<String, MatchDetailsDTO> categoryMatchResult = new HashMap<String, MatchDetailsDTO>();
    matchResult.add(categoryType, categoryMatchResult);
    processor.process(analysis, portraitMatcherData, categoryMatchResult);
    
    // get the score list for the priority of the category and add the individual scale match scores
    List<Double> scoreList = categoryPriorityScore.computeIfAbsent(categoryType.getPriority(),
        k -> new ArrayList<Double>());
    scoreList.add(categoryMatchResult.values().stream().mapToInt(MatchDetailsDTO::getMatchPercent)
        .average().orElse(0d));
  }
  
  /**
   * Compute the category priority score and also apply the weight.
   * 
   * @param scores
   *          - scores
   * @param categoryPriorityWeight
   *          - weight to apply
   * @return the score
   */
  private double computeScore(List<Double> scores, BigDecimal categoryPriorityWeight) {
    double average = scores.stream().mapToDouble(Double::doubleValue).average().orElse(0d);
    return categoryPriorityWeight.multiply(BigDecimal.valueOf(average)).doubleValue();
  }
  
  /**
   * Get the processor for the given category type.
   * 
   * @param categoryType
   *          - category type
   * @return the match processor
   */
  private PortraitMatchProcessor getProcessor(CategoryType categoryType) {
    PortraitMatchProcessor processor = processorMap.get(categoryType);
    if (processor == null) {
      try {
        processor = (PortraitMatchProcessor) ApplicationContextUtils.getBean(categoryType
            .getPortraitMatchProcessor());
        processor.setCategoryType(categoryType);
        processorMap.put(categoryType, processor);
      } catch (Exception e) {
        log.warn("Processor not found for " + categoryType, e);
        throw new IllegalArgumentException("Processor not found for " + categoryType, e);
      }
    }
    return processor;
  }
  
}
