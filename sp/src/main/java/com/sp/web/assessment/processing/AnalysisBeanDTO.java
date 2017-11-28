package com.sp.web.assessment.processing;

import com.sp.web.Constants;
import com.sp.web.assessment.questions.TraitType;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * @author Dax Abraham
 * 
 *         The DTO class to store the analysis in the format that is required by the front end.
 */
public class AnalysisBeanDTO extends AnalysisBean {
  
  /**
   * The serial version uid.
   */
  private static final long serialVersionUID = -7847855219085496402L;
  private static final BigDecimal ONE = new BigDecimal(1);
  private static final ConflictManagementComparator comparator = new ConflictManagementComparator();
  
  /** Default Constructor. */
  public AnalysisBeanDTO() {
    
  }
  
  /**
   * Constructor.
   * 
   * @param analysis
   *          - analysis for the DTO
   */
  public AnalysisBeanDTO(AnalysisBean analysis) {
    // convert and copy the data
    setConflictManagement(convertTo100(analysis.getConflictManagement()));
    setDecisionMaking(convert(analysis.getDecisionMaking()));
    setFundamentalNeeds(convertTo100(analysis.getFundamentalNeeds()));
    setLearningStyle(convert(analysis.getLearningStyle()));
    setMotivationHow(convert(analysis.getMotivationHow()));
    setMotivationWhat(convert(analysis.getMotivationWhat()));
    setMotivationWhy(convert(analysis.getMotivationWhy()));
    setPersonality(analysis.getPersonality());
    setProcessing(convert(analysis.getProcessing()));
    setCreatedOn(analysis.getCreatedOn());
    updatePriorities();
    setCompletedCategories(analysis.getCompletedCategories());
  }
  
  /**
   * This method converts the values to a percentage format for sending it to the front end.
   * 
   * @param valueMapToConvert
   *          - value map to convert
   * @return the updated map with the percent values
   */
  private Map<TraitType, BigDecimal> convert(Map<TraitType, BigDecimal> valueMapToConvert) {
    HashMap<TraitType, BigDecimal> tempMap = new HashMap<TraitType, BigDecimal>();
    if (valueMapToConvert != null) {
      valueMapToConvert.forEach((key, value) -> tempMap.put(key, convert(value)));
    }
    return tempMap;
  }
  
  /**
   * Converts the passed value to a percentage.
   * 
   * @param valueToConvert
   *          - value
   * @return converted value
   */
  private BigDecimal convert(BigDecimal valueToConvert) {
    BigDecimal updatedValue = null;
    if (valueToConvert != null) {
      if (valueToConvert.compareTo(ONE) != 1) {
        updatedValue = valueToConvert
            .setScale(Constants.PERCENT_PRECISION, Constants.ROUNDING_MODE).movePointRight(
                Constants.POINT_MOVEMENT);
      } else {
        updatedValue = valueToConvert;
      }
    } else {
      updatedValue = Constants.ZERO_VALUE;
    }
    return updatedValue;
  }
  
  private Map<TraitType, BigDecimal> convertTo100(Map<TraitType, BigDecimal> valueMapToConvert) {
    HashMap<TraitType, BigDecimal> tempMap = new HashMap<TraitType, BigDecimal>();
    if (valueMapToConvert != null) {
      double total = 0d;
      int size = valueMapToConvert.size();
      int counter = 1;
      List<Entry<TraitType, BigDecimal>> collect = valueMapToConvert.entrySet().stream()
          .sorted(comparator.reversed()).collect(Collectors.toList());
      for (Entry<TraitType, BigDecimal> entry : collect) {
        BigDecimal value = entry.getValue();
        BigDecimal convert = convert(value);
        total += convert.doubleValue();
        if (total > 100.0) {
          convert = convert.subtract(ONE);
        } else if (total == 99 && counter == size) {
          convert = convert.add(ONE);
        }
        tempMap.put(entry.getKey(), convert);
        counter++;
      }
    }
    return tempMap;
  }
}
