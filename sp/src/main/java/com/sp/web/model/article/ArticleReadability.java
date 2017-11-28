package com.sp.web.model.article;

import java.io.Serializable;

/**
 * Contains all the Readability related information related to an article.
 * 
 * @author Prasanna Venkatesh
 *
 */
public class ArticleReadability implements Serializable{

 
  private static final long serialVersionUID = 1L;
  
  private int characters;
  
  private int words;
  
  private int sentences;
  
  private int syllables;
  
  private int complexWords;
  
  private double automatedReadabilityIndex;
  
  private double colemanLiauIndex;
  
  private double fleschKincaidGradeLevel;
  
  private double fleschKincaidReadingEase;
  
  private double gunningFogScore;
  
  private double smogIndex;
  
  private double average;

  public int getCharacters() {
    return characters;
  }

  public void setCharacters(int characters) {
    this.characters = characters;
  }

  public int getWords() {
    return words;
  }

  public void setWords(int words) {
    this.words = words;
  }

  public int getSentences() {
    return sentences;
  }

  public void setSentences(int sentences) {
    this.sentences = sentences;
  }

  public int getSyllables() {
    return syllables;
  }

  public void setSyllables(int syllables) {
    this.syllables = syllables;
  }

  public int getComplexWords() {
    return complexWords;
  }

  public void setComplexWords(int complexWords) {
    this.complexWords = complexWords;
  }

  public double getAutomatedReadabilityIndex() {
    return automatedReadabilityIndex;
  }

  public void setAutomatedReadabilityIndex(double automatedReadabilityIndex) {
    this.automatedReadabilityIndex = automatedReadabilityIndex;
  }

  public double getColemanLiauIndex() {
    return colemanLiauIndex;
  }

  public void setColemanLiauIndex(double colemanLiauIndex) {
    this.colemanLiauIndex = colemanLiauIndex;
  }

  public double getFleschKincaidGradeLevel() {
    return fleschKincaidGradeLevel;
  }

  public void setFleschKincaidGradeLevel(double fleschKincaidGradeLevel) {
    this.fleschKincaidGradeLevel = fleschKincaidGradeLevel;
  }

  public double getFleschKincaidReadingEase() {
    return fleschKincaidReadingEase;
  }

  public void setFleschKincaidReadingEase(double fleschKincaidReadingEase) {
    this.fleschKincaidReadingEase = fleschKincaidReadingEase;
  }

  public double getGunningFogScore() {
    return gunningFogScore;
  }

  public void setGunningFogScore(double gunningFogScore) {
    this.gunningFogScore = gunningFogScore;
  }

  public double getSmogIndex() {
    return smogIndex;
  }

  public void setSmogIndex(double smogIndex) {
    this.smogIndex = smogIndex;
  }

  public double getAverage() {
    return average;
  }

  public void setAverage(double average) {
    this.average = average;
  }

  
  
}
