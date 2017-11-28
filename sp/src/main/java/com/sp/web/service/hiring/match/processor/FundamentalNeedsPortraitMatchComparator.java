package com.sp.web.service.hiring.match.processor;

import com.sp.web.assessment.questions.TraitType;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 
 * @author Dax Abraham
 *
 *         The comparator for the traits and score comparison.
 */
public class FundamentalNeedsPortraitMatchComparator implements Comparator<Entry<TraitType, BigDecimal>> {

  private Map<TraitType, Integer> traitsPriority;

  public FundamentalNeedsPortraitMatchComparator(Map<TraitType, Integer> traitsPriority) {
    this.traitsPriority = traitsPriority;
  }
  
  @Override
  public int compare(Entry<TraitType, BigDecimal> o1, Entry<TraitType, BigDecimal> o2) {
    
    final int scoreCompare = o1.getValue().compareTo(o2.getValue());
    if (scoreCompare == 0) {
      return getTraitPriority(o1.getKey()).compareTo(getTraitPriority(o2.getKey()));
    }
    return scoreCompare;
  }

  private Integer getTraitPriority(TraitType key) {
    return traitsPriority.getOrDefault(key, Integer.MIN_VALUE);
  }
  
}
