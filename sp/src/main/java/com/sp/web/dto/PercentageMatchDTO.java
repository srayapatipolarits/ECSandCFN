package com.sp.web.dto;

import java.math.BigDecimal;

/**
 * @author Dax Abraham
 *
 *         The DTO class to store the percentage match.
 */
public class PercentageMatchDTO {
  
  BigDecimal primaryPersonality;
  BigDecimal underPressurePersonality;
  BigDecimal processingBluePrint;
  BigDecimal conflictManagement;
  BigDecimal fundamentalNeeds;
  BigDecimal leanringStyle;
  BigDecimal decisionMaking;
  BigDecimal motivation;
  
  public BigDecimal getPrimaryPersonality() {
    return primaryPersonality;
  }
  
  public void setPrimaryPersonality(BigDecimal primaryPersonality) {
    this.primaryPersonality = primaryPersonality;
  }
  
  public BigDecimal getUnderPressurePersonality() {
    return underPressurePersonality;
  }
  
  public void setUnderPressurePersonality(BigDecimal underPressurePersonality) {
    this.underPressurePersonality = underPressurePersonality;
  }
  
  public BigDecimal getProcessingBluePrint() {
    return processingBluePrint;
  }
  
  public void setProcessingBluePrint(BigDecimal processingBluePrint) {
    this.processingBluePrint = processingBluePrint;
  }
  
  public BigDecimal getConflictManagement() {
    return conflictManagement;
  }
  
  public void setConflictManagement(BigDecimal conflictManagement) {
    this.conflictManagement = conflictManagement;
  }

  public BigDecimal getFundamentalNeeds() {
    return fundamentalNeeds;
  }

  public void setFundamentalNeeds(BigDecimal fundamentalNeeds) {
    this.fundamentalNeeds = fundamentalNeeds;
  }

  public BigDecimal getLeanringStyle() {
    return leanringStyle;
  }

  public void setLeanringStyle(BigDecimal leanringStyle) {
    this.leanringStyle = leanringStyle;
  }

  public BigDecimal getDecisionMaking() {
    return decisionMaking;
  }

  public void setDecisionMaking(BigDecimal decisionMaking) {
    this.decisionMaking = decisionMaking;
  }

  public BigDecimal getMotivation() {
    return motivation;
  }

  public void setMotivation(BigDecimal motivation) {
    this.motivation = motivation;
  }
}
