package com.sp.web.readability;


/**
 * Implements various readability indexes
 * http://code.google.com/p/panos-ipeirotis/source/browse/trunk/src/com/ipeirotis/readability/?r=2
 * @author Panos Ipeirotis
 *
 */

/** 
 * Made few modifications to this code to fix few issues and cater to SP needs
 * 
 * Prasanna Venkatesh
 */
public class Readability {
    
    Integer sentences;
    Integer complex;
    Integer words;
    Integer syllables;
    Integer characters;
    
    public Integer getCharacters() {
        return characters;
    }

    public Integer getComplex() {
        return complex;
    }

    public Integer getSentences() {
        return sentences;
    }

    public Integer getSyllables() {
        return syllables;
    }

    public Integer getWords() {
        return words;
    }

    public Readability(String text) {
      
        text = text.replaceAll("\\<.*?\\>", "");
        this.sentences = getNumberOfSentences(text);
        this.complex = getNumberOfComplexWords(text);
        this.words = getNumberOfWords(text);
        this.syllables = getNumberOfSyllables(text);
        this.characters = getNumberOfCharacters(text);
        
      
    }
    
    /**
     * 
     */
    public BagOfReadabilityObjects getMetrics() {
    	BagOfReadabilityObjects bo = new BagOfReadabilityObjects();
    	
    	bo.setARI(this.getARI());
    	bo.setCharacters(this.getCharacters());
    	bo.setColemanLiau(this.getColemanLiau());
    	bo.setComplexwords(this.getComplex());
    	bo.setFleschKincaid(this.getFleschKincaidGradeLevel());
    	bo.setFleschReading(this.getFleschReadingEase());
    	bo.setGunningFog(this.getGunningFog());
    	bo.setSentences(this.getSentences());
    	bo.setSMOG(this.getSMOG());
    	bo.setSMOGIndex(this.getSMOGIndex());
    	bo.setSyllables(this.getSyllables());
    	bo.setWords(this.getWords());
    	
    	return bo;
    }
    /** 
     * Returns true is the word contains 3 or more syllables
     * 
     * @param w
     * @return
     */
    private static boolean isComplex(String w) {
        int syllables = Syllabify.syllable(w);
        return (syllables > 2);
    }
    
    /**
     * Returns the number of letter characters in the text
     * 
     * @return
     */
    private static Integer getNumberOfCharacters(String text) {
        String cleanText = Utilities.cleanLine(text);
        String[] word  = cleanText.split(" ");
        
        Integer characters = 0;
        for (String w : word) {
            characters += w.length();
        }
        return characters;
    }
   
    /**
     * Returns the number of words with 3 or more syllables
     * 
     * @param text
     * @return the number of words in the text with 3 or more syllables
     */
    private static Integer getNumberOfComplexWords(String text) {
        String cleanText = Utilities.cleanLine(text);
        String[] words  = cleanText.split(" ");
        int complex = 0;
        for (String w : words) {
            if (isComplex(w)) complex++;
        }
        return complex;
    }
    
    private static Integer getNumberOfWords(String text) {
        String cleanText = Utilities.cleanLine(text);
        String[] word  = cleanText.split(" ");
        int words = 0;
        for (String w : word) {
           if (w.length()>0) words ++;
        }
        return words;
    }

    /**
     * Returns the total number of syllables in the words of the text
     * @param text
     * @return the total number of syllables in the words of the text
     */
    private static Integer getNumberOfSyllables(String text) {
        String cleanText = Utilities.cleanLine(text);
        String[] word  = cleanText.split(" ");
        int syllables = 0;
        for (String w : word) {
            if (w.length()>0) {
                syllables += Syllabify.syllable(w);
            }
        }
        return syllables;
    }
    
  private static Integer getNumberOfSentences(String text) {
    int sentenceCount = 0;
    String SENTENCE_DELIMETERS = ".!?";
    for (int i = 0; i < text.length() - 1; i++) {
      for (int j = 0; j < SENTENCE_DELIMETERS.length(); j++) {
        if (text.charAt(i) == SENTENCE_DELIMETERS.charAt(j)) {
          if (text.charAt(i + 1) == SENTENCE_DELIMETERS.charAt(j)) {
            sentenceCount--;
          }
          sentenceCount++;
        }
      }
    }
    return sentenceCount;
    }
    
    /**
     * 
     * http://en.wikipedia.org/wiki/SMOG_Index
     * 
     * @param text
     * @return The SMOG index of the text
     */
    public Double getSMOGIndex() {
        double score = Math.sqrt( complex * (30.0 / sentences) ) +3;      
        return Utilities.round(score, 3);
    }
    
    /** 
     * 
     * http://en.wikipedia.org/wiki/SMOG
     * 
     * @param text
     * @return Retugns the SMOG value for the text
     */
    public Double getSMOG() {
        double score = 1.043 * Math.sqrt( complex * (30.0 / sentences) ) +3.1291;  
        return Utilities.round(score, 3);
    }
    
    /**
     * 
     * http://en.wikipedia.org/wiki/Flesch-Kincaid_Readability_Test
     * 
     * @param text
     * @return Returns the Flesch_Reading Ease value for the text
     */
    public Double getFleschReadingEase() {
    	
        double score = 206.835 - 1.015*words/sentences - 84.6*syllables/words;
        
        return Utilities.round(score, 3);
    }
    
    /**
     * 
     * http://en.wikipedia.org/wiki/Flesch-Kincaid_Readability_Test
     * 
     * @param text
     * @return Returns the Flesch-Kincaid_Readability_Test value for the text
     */
    public Double getFleschKincaidGradeLevel() {
        double score =  0.39 * words/sentences + 11.8 * syllables/words - 15.59;
        return Utilities.round(score, 3);
    }

    /**
     * 
     * http://en.wikipedia.org/wiki/Automated_Readability_Index
     * 
     * @param text
     * @return the Automated Readability Index for text
     */
    public Double getARI() {
    	double score = 4.71 * characters/words + 0.5 * words/sentences - 21.43;
        return Utilities.round(score, 3);
    }

    /**
     * 
     * http://en.wikipedia.org/wiki/Gunning-Fog_Index
     * 
     * @param text
     * @return the Gunning-Fog Index for text
     */
    public double getGunningFog() {
        double score = 0.4 * (words/sentences + 100 * complex/words);
        return Utilities.round(score, 3);
    }

    /**
     * 
     * http://en.wikipedia.org/wiki/Coleman-Liau_Index
     * 
     * @return The Coleman-Liau_Index value for the text
     */
    public Double getColemanLiau() {
        double score = (5.89 * characters/words) - (30 * sentences/words) - 15.8;
        return Utilities.round(score, 3);
    }
    
    public Double getAverageScore() {
      double score = (getARI() + getGunningFog() + getColemanLiau() + getFleschKincaidGradeLevel() + getSMOGIndex())/5 ;
      return Utilities.round(score, 3);
  }

}