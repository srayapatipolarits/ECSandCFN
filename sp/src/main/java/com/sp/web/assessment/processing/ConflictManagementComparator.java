package com.sp.web.assessment.processing;

import com.sp.web.assessment.questions.TraitType;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Map.Entry;

public class ConflictManagementComparator implements Comparator<Entry<TraitType, BigDecimal>> {

  @Override
  public int compare(Entry<TraitType, BigDecimal> o1, Entry<TraitType, BigDecimal> o2) {
    final int scoreCompare = o1.getValue().compareTo(o2.getValue());
    if (scoreCompare == 0) {
      return (o1.getKey().getPriority()).compareTo(o2.getKey().getPriority());
    }
    return scoreCompare;
  }
  
}
