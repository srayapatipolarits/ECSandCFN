package com.sp.web.assessment.questions;

import com.sp.web.Constants;
import com.sp.web.exception.SPException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
//import com.sp.web.xml.questions.QuestionsDocument.Questions;
//import com.sp.web.xml.questions.RatingDocument.Rating;
//import com.sp.web.xml.questions.RatingResponseDocument.RatingResponse;
//import com.sp.web.xml.questions.TraitTransformDocument.TraitTransform;
//import com.sp.web.xml.questions.VariantDocument.Variant;
//import com.sp.web.xml.questions.CategoryDocument.Category;
//import com.sp.web.xml.questions.MultipleChoiceResponseDocument.MultipleChoiceResponse;
//import com.sp.web.xml.questions.QuestionDocument.Question;

/**
 * <p>
 * This class is the factory class for the Questions to be used by all the classes that require the
 * access to the questions.
 * 
 * This class would be responsible to keep the questions up to date and adapt to any changes to the
 * questions in the runtime.
 * </p>
 * 
 * @author daxabraham
 * 
 */
public class QuestionsFactory {
  
  private static final String QUESTIONS_XML = "questions";
  
  /*
   * The reference to the logger object
   */
  private static final Logger log = Logger.getLogger(QuestionsFactory.class);
  
  /*
   * The reference to the initial assessment
   */
  private CategoriesBean categoriesSummary;
  private Map<CategoryType, List<QuestionsBean>> categoriesMap;
  private Map<Integer, TraitsBean> traitsMap;
  
  /**
   * This would initialize the factory and load the Questions from the file.
   */
  public QuestionsFactory(String locale) {
    load(getQuestionsFile(locale));
  }
  
  /**
   * This method processes the questions and creates the assessment initial summary to start the.
   * assessment exercise
   * @param nodeList 
   *          - category nodes
   */
  private void processCategoriesSummary(NodeList nodeList) {
    
    // Create the Sections Bean
    CategoriesBean categoriesBean = new CategoriesBean();

    // set the total number sections
    categoriesBean.setTotalCategories(nodeList.getLength());
    
    for (int i = 0; i < nodeList.getLength(); i++) {
      Element categoryNode = (Element) nodeList.item(i);
      // set the different information for the Section Info bean from the category
      CategoryBean sectionInfo = new CategoryBean();
      NamedNodeMap attributes = categoryNode.getAttributes();
      
      sectionInfo.setNumber(Integer.valueOf(attributes.getNamedItem("Number").getNodeValue()));
      sectionInfo.setInstruction(attributes.getNamedItem("Directions").getNodeValue());
      sectionInfo.setTotalQuestions(categoryNode.getElementsByTagName("Question").getLength());
      sectionInfo.setType(attributes.getNamedItem("Type").getNodeValue());
      
      CategoryType categoryType = CategoryType.valueOf(attributes.getNamedItem("Name").getNodeValue());
      // add the section info to the section
      categoriesBean.add(categoryType, sectionInfo);
      
    }
    this.categoriesSummary = categoriesBean;
  }
  
  protected String getQuestionsFile(String locale) {
    if (locale.equalsIgnoreCase(Constants.DEFAULT_LOCALE)) {
      return QUESTIONS_XML.concat(".xml");
    } else {
      return QUESTIONS_XML.concat("_" + locale + ".xml");
    }
    
  }
  
  /**
   * This method loads the questions.
   */
  protected void load(String questionsXMLFile) {
    try {
      DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder docBuilder = dbFactory.newDocumentBuilder();
      Document xmlDoc = docBuilder.parse(this.getClass()
          .getClassLoader().getResourceAsStream(questionsXMLFile));
      
      Element rootElement = xmlDoc.getDocumentElement();
      
      // check if questions returned is not null
      if (rootElement != null) {
        // create the summary for the question categories
        final NodeList categoryElements = rootElement.getElementsByTagName("Category");
        processCategoriesSummary(categoryElements);
        
        // create the list of questions optimized for JSON response
        createQuestionsAndTraitsArrays(categoryElements);
      }
    } catch (IOException e) {
      log.fatal("Error processing the XML :" + QUESTIONS_XML, e);
      throw new SPException("Error processing the XML :" + QUESTIONS_XML, e);
    } catch (Exception e) {
      log.fatal("Error processing the XML :" + QUESTIONS_XML, e);
      throw new SPException("Error processing the XML :" + QUESTIONS_XML, e);
    }
  }
  
  /**
   * Method to create the optimized questions array.
   * @param categoryElements
   *            - category elements 
   */
  private void createQuestionsAndTraitsArrays(NodeList categoryElements) {
    // get the maximum number of questions
    HashMap<CategoryType, List<QuestionsBean>> tempCategoriesMap = new HashMap<CategoryType, List<QuestionsBean>>();
    HashMap<Integer, TraitsBean> tempTraitsMap = new HashMap<Integer, TraitsBean>();
    
    // loop over all the questions and create the questions and traits arrays
    for (int i = 0; i < categoryElements.getLength(); i++) {
      Element category = (Element) categoryElements.item(i);
      CategoryType categoryType = CategoryType.valueOf(category.getAttribute("Name"));
      NodeList questionsNodeList = category.getElementsByTagName("Question");
      List<QuestionsBean> questionsList = new ArrayList<QuestionsBean>(questionsNodeList.getLength());
      tempCategoriesMap.put(categoryType, questionsList);
      final int number = Integer.parseInt(category.getAttribute("Number"));
      
      // iterate over all the questions and add them to the array
      for (int j = 0; j < questionsNodeList.getLength(); j++) {
        Element questionNode = (Element) questionsNodeList.item(j);
        
        // create the questions bean
        QuestionsBean questionsBean = new QuestionsBean();
        questionsBean.setCategoryNumber(number);
        final int questionNumber = Integer.parseInt(questionNode.getAttribute("Number"));
        questionsBean.setNumber(questionNumber);
        TraitsBean traitsBean = new TraitsBean();
        createQuestionAndTrait(questionNode, questionsBean, traitsBean);
        questionsList.add(questionsBean);
        tempTraitsMap.put(questionNumber, traitsBean);
      }      
    }
    this.setCategoriesMap(tempCategoriesMap);
    this.setTraitsMap(tempTraitsMap);
  }
  
  /**
   * This method creates the question and trait beans.
   * 
   * @param question
   *          - the question to create the beans from
   * @param questionsBean
   *          - the question bean to update
   * @param traitsBean
   *          - the traits bean to update
   */
  private void createQuestionAndTrait(Element questionNode, QuestionsBean questionsBean,
      TraitsBean traitsBean) {
    Element variantNode = (Element) questionNode.getElementsByTagName("Variant").item(0);
    if (variantNode == null) {
      log.fatal("Variant not found in question :" + questionNode);
      throw new SPException("Variant not found in question :" + questionNode);
    }
    final String variantType = variantNode.getAttribute("Type");
    try {
      questionsBean.setType(QuestionType.valueOf(variantType));
      if (questionNode.getAttribute("Category").equalsIgnoreCase("Personality")) {
        // Special handling for personality type
        traitsBean.setType(QuestionType.PersonalityRating);
        questionsBean.setType(QuestionType.PersonalityRating);
      } else {
        // set the regular question type
        traitsBean.setType(questionsBean.getType());
      }
    } catch (Exception e) {
      log.fatal("Could not convert the question type string :" + variantType, e);
      throw new SPException("Could not convert the question type string :" + variantType, e);
    }
    questionsBean.setDescription(getDescription(variantNode));
    // create a list for the list of questions
    List<OptionsBean> tempOptionsList = new ArrayList<OptionsBean>();
    // create a temporary list for the traits transform
    Map<String, List<TraitsTransform>> tempTraitsTransformMap = new HashMap<String, List<TraitsTransform>>();
    switch (questionsBean.getType()) {
    case MultipleChoice: // Add the options and the traits for the multiple choice type of questions
      NodeList mcrNodes = ((Element) variantNode.getElementsByTagName("MultipleChoiceResponses")
          .item(0)).getElementsByTagName("MultipleChoiceResponse");
      for (int i = 0; i < mcrNodes.getLength(); i++) {
        Element mcrNode = (Element) mcrNodes.item(i);
        final OptionsBean questionOption = new OptionsBean(mcrNode);
        tempOptionsList.add(questionOption);
        List<TraitsTransform> tt = new ArrayList<TraitsTransform>();
        NodeList traitTransformNodes = ((Element) mcrNode.getElementsByTagName("TraitTransforms")
            .item(0)).getElementsByTagName("TraitTransform");
        for (int j = 0; j < traitTransformNodes.getLength(); j++) {
          Element traitTransformNode = (Element) traitTransformNodes.item(j);
          TraitsTransform tempTransform = new TraitsTransform(traitTransformNode);
          tt.add(tempTransform);
        }
        tempTraitsTransformMap.put(questionOption.getId(), tt);
      }
      questionsBean.setOptionsList(tempOptionsList);
      traitsBean.setTraitsTransformMap(tempTraitsTransformMap);
      break;
    case Rating: // Add the options and the traits for the Ratings type of questions
    case PersonalityRating:
    case VariableRating:
      NodeList ratingResponseNodes = ((Element) variantNode.getElementsByTagName("RatingResponses")
          .item(0)).getElementsByTagName("RatingResponse");
      
      for (int i = 0; i < ratingResponseNodes.getLength(); i++) {
        Element ratingResponseNode = (Element) ratingResponseNodes.item(i);
        final OptionsBean questionOption = new OptionsBean(ratingResponseNode);
        tempOptionsList.add(questionOption);
        List<TraitsTransform> tt = new ArrayList<TraitsTransform>();
        NodeList traitTransformNodes = ((Element) ratingResponseNode.getElementsByTagName("TraitTransforms")
            .item(0)).getElementsByTagName("TraitTransform");
        for (int j = 0; j < traitTransformNodes.getLength(); j++) {
          Element traitTransformNode = (Element) traitTransformNodes.item(j);
          float amount = Float.valueOf(traitTransformNode.getAttribute("Amount"));
          if (amount != 0) {
            TraitsTransform tempTransform = new TraitsTransform(traitTransformNode);
            tt.add(tempTransform);
          }
          tempTraitsTransformMap.put(questionOption.getId(), tt);
        }
      }
      // get the rating factor
      NodeList ratingNodes = ((Element) variantNode.getElementsByTagName("Ratings").item(0))
          .getElementsByTagName("Rating");
      RatingBean[] factor = new RatingBean[ratingNodes.getLength()];
      for (int k = 0; k < ratingNodes.getLength(); k++) {
        Element ratingNode = (Element) ratingNodes.item(k);
        RatingBean rb = new RatingBean(ratingNode);
        factor[k] = rb;
      }
      questionsBean.setOptionsList(tempOptionsList);
      traitsBean.setTraitsTransformMap(tempTraitsTransformMap);
      traitsBean.setFactor(factor);
      break;
    default:
      log.fatal("Don't know how to process question type :" + questionsBean.getType());
      throw new SPException("Don't know how to process question type :" + questionsBean.getType());
    }
    
  }
  
  private String getDescription(Element variantNode) {
    String description = null;
    Node firstChild = variantNode.getFirstChild().getNextSibling();
    if (firstChild.getNodeName().equals("Text")) {
      description = firstChild.getTextContent();
    }
    return description;
  }

  /**
   * The categories summary.
   * 
   * @return the categoriesSummary
   */
  public CategoriesBean getCategoriesSummary() {
    return categoriesSummary;
  }
  
  public Map<CategoryType, List<QuestionsBean>> getCategoriesMap() {
    return categoriesMap;
  }
  
  public void setCategoriesMap(Map<CategoryType, List<QuestionsBean>> categoriesMap) {
    this.categoriesMap = categoriesMap;
  }
  
  public Map<Integer, TraitsBean> getTraitsMap() {
    return traitsMap;
  }
  
  public void setTraitsMap(Map<Integer, TraitsBean> traitsMap) {
    this.traitsMap = traitsMap;
  }
  
  public List<CategoryBean> getCategoryInfo(List<CategoryType> categories) {
    return categoriesSummary.getCategoryInfo(categories);
  }
  
  public List<QuestionsBean> getQuestions(CategoryType categoryType) {
    return categoriesMap.get(categoryType);
  }
  
  public TraitsBean getTrait(int questionNumber) {
    return traitsMap.get(questionNumber);
  }
}
