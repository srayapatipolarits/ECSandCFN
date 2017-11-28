package com.sp.web.mvc.assessment;

import static com.sp.web.Constants.ACCURACY_RANGE_HIGH;
import static com.sp.web.Constants.ACCURACY_RANGE_LOW;
import static com.sp.web.Constants.PRECISION;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sp.web.Constants;
import com.sp.web.assessment.personality.PersonalityBean;
import com.sp.web.assessment.personality.PersonalityBeanResponse;
import com.sp.web.assessment.personality.PersonalityFactory;
import com.sp.web.assessment.personality.RangeType;
import com.sp.web.assessment.processing.AnalysisBean;
import com.sp.web.assessment.processing.AnalysisBeanDTO;
import com.sp.web.assessment.questions.AssessmentQuestionFactory;
import com.sp.web.assessment.questions.CategoryType;
import com.sp.web.assessment.questions.OptionsBean;
import com.sp.web.assessment.questions.QuestionFactoryType;
import com.sp.web.assessment.questions.QuestionsBean;
import com.sp.web.assessment.questions.QuestionsFactory;
import com.sp.web.assessment.questions.TraitType;
import com.sp.web.assessment.questions.TraitsBean;
import com.sp.web.exception.PersonalityNotFoundException;
import com.sp.web.model.User;
import com.sp.web.mvc.test.setup.SPTestBase;
import com.sp.web.test.setup.AuthenticationHelper;
import com.sp.web.xml.questions.CategoryDocument.Category;
import com.sp.web.xml.questions.MultipleChoiceResponseDocument.MultipleChoiceResponse;
import com.sp.web.xml.questions.QuestionDocument.Question;
import com.sp.web.xml.questions.QuestionsDocument.Questions;
import com.sp.web.xml.questions.RatingResponseDocument.RatingResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MvcResult;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author daxabraham
 * 
 *         This test class is used to convert the test responses from the existing assessments to
 *         the new format for validation.
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class TestAssessmentValidatorFromExistingTests extends SPTestBase {
  
  private static final String CSV_DELIMITTER = ",";
  private static final String TSV_DELIMITTER = "\t";
  
  /*
   * The reference to the questions factory
   */
  @Autowired
  AssessmentQuestionFactory abstractQuestionsFactory;
  
  @Autowired
  PersonalityFactory personalityFactory;
  
  Map<String, QuestionsBean> questionMap = new HashMap<String, QuestionsBean>();
  Map<String, List<String>> responseMap = new ConcurrentHashMap<String, List<String>>();
  Map<String, AnalysisBean> analysisResultMap = new HashMap<String, AnalysisBean>();
  
  @Value("classpath:test-assessments.csv")
  private Resource responseCSV;
  
  @Value("classpath:workstyle-analysis.csv")
  private Resource workstyleAnalysisCSV;
  
  private ObjectMapper om;
  
  @Autowired
  AuthenticationHelper authenticationHelper;
  
  boolean isAuthenticationDone = false;
  
  /**
   * Setup method.
   * 
   * @throws Exception
   *           - error setting up
   */
  @Before
  public void setup() throws Exception {
    loadQuestions();
    loadResponses();
    loadAnalysisResults();
    om = new ObjectMapper();
    
    // login the user
    if (!isAuthenticationDone) {
      authenticationHelper.authenticateUser(session);
      isAuthenticationDone = true;
    }
    
    testSmtp.start();
  }
  
  @After
  public void teardown() {
    testSmtp.stop();
  }
  
  /**
   * Loads the analysis results.
   * 
   * @throws IOException
   *           - error processing
   * @throws PersonalityNotFoundException
   *           - error in personality processing
   */
  private void loadAnalysisResults() throws IOException, PersonalityNotFoundException {
    List<String> responseList = IOUtils.readLines(workstyleAnalysisCSV.getInputStream());
    responseList.remove(0); // removing the header row
    for (String responseStr : responseList) {
      String[] responseArray = responseStr.split(CSV_DELIMITTER);
      AnalysisBean analysisBean = new AnalysisBean();
      try {
        analysisBean.setAccuracy(new BigDecimal(responseArray[3]).setScale(PRECISION,
            RoundingMode.HALF_UP));
      } catch (NumberFormatException e) {
        log.debug("Could not convert to big decimal :" + responseArray[3], e);
        throw e;
      }
      
      // setting processing type
      HashMap<TraitType, BigDecimal> processingMap = new HashMap<TraitType, BigDecimal>();
      processingMap.put(TraitType.External,
          new BigDecimal(responseArray[4]).setScale(PRECISION, RoundingMode.HALF_UP));
      processingMap.put(TraitType.Internal,
          new BigDecimal(responseArray[5]).setScale(PRECISION, RoundingMode.HALF_UP));
      processingMap.put(TraitType.Concrete,
          new BigDecimal(responseArray[6]).setScale(PRECISION, RoundingMode.HALF_UP));
      processingMap.put(TraitType.Intuitive,
          new BigDecimal(responseArray[7]).setScale(PRECISION, RoundingMode.HALF_UP));
      processingMap.put(TraitType.Affective,
          new BigDecimal(responseArray[8]).setScale(PRECISION, RoundingMode.HALF_UP));
      processingMap.put(TraitType.Cognitive,
          new BigDecimal(responseArray[9]).setScale(PRECISION, RoundingMode.HALF_UP));
      processingMap.put(TraitType.Orderly,
          new BigDecimal(responseArray[10]).setScale(PRECISION, RoundingMode.HALF_UP));
      processingMap.put(TraitType.Spontaneous,
          new BigDecimal(responseArray[11]).setScale(PRECISION, RoundingMode.HALF_UP));
      analysisBean.setProcessing(processingMap);
      
      // setting conflict management
      Map<TraitType, BigDecimal> conflictManagementMap = new HashMap<TraitType, BigDecimal>();
      conflictManagementMap.put(TraitType.Compete,
          new BigDecimal(responseArray[12]).setScale(PRECISION, RoundingMode.HALF_UP));
      conflictManagementMap.put(TraitType.Collaborate,
          new BigDecimal(responseArray[13]).setScale(PRECISION, RoundingMode.HALF_UP));
      conflictManagementMap.put(TraitType.Compromise,
          new BigDecimal(responseArray[14]).setScale(PRECISION, RoundingMode.HALF_UP));
      conflictManagementMap.put(TraitType.Avoid,
          new BigDecimal(responseArray[15]).setScale(PRECISION, RoundingMode.HALF_UP));
      conflictManagementMap.put(TraitType.Accommodate,
          new BigDecimal(responseArray[16]).setScale(PRECISION, RoundingMode.HALF_UP));
      analysisBean.setConflictManagement(conflictManagementMap);
      
      // setting Learning style
      Map<TraitType, BigDecimal> learningStyleMap = new HashMap<TraitType, BigDecimal>();
      learningStyleMap.put(TraitType.Analytical,
          new BigDecimal(responseArray[17]).setScale(PRECISION, RoundingMode.HALF_UP));
      learningStyleMap.put(TraitType.Global,
          new BigDecimal(responseArray[18]).setScale(PRECISION, RoundingMode.HALF_UP));
      analysisBean.setLearningStyle(learningStyleMap);
      
      // setting the motivation why style
      Map<TraitType, BigDecimal> motivationWhyMap = new HashMap<TraitType, BigDecimal>();
      motivationWhyMap.put(TraitType.AttainmentOfGoals,
          new BigDecimal(responseArray[19]).setScale(PRECISION, RoundingMode.HALF_UP));
      motivationWhyMap.put(TraitType.RecognitionForEffort,
          new BigDecimal(responseArray[20]).setScale(PRECISION, RoundingMode.HALF_UP));
      motivationWhyMap.put(TraitType.Power,
          new BigDecimal(responseArray[21]).setScale(PRECISION, RoundingMode.HALF_UP));
      motivationWhyMap.put(TraitType.Compliance,
          new BigDecimal(responseArray[22]).setScale(PRECISION, RoundingMode.HALF_UP));
      motivationWhyMap.put(TraitType.Affiliation,
          new BigDecimal(responseArray[23]).setScale(PRECISION, RoundingMode.HALF_UP));
      motivationWhyMap.put(TraitType.Activity,
          new BigDecimal(responseArray[24]).setScale(PRECISION, RoundingMode.HALF_UP));
      analysisBean.setMotivationWhy(motivationWhyMap);
      
      // setting the motivation why style
      Map<TraitType, BigDecimal> motivationHowMap = new HashMap<TraitType, BigDecimal>();
      motivationHowMap.put(TraitType.AffirmedByOthers,
          new BigDecimal(responseArray[25]).setScale(PRECISION, RoundingMode.HALF_UP));
      motivationHowMap.put(TraitType.ExchangeOfIdeas,
          new BigDecimal(responseArray[26]).setScale(PRECISION, RoundingMode.HALF_UP));
      motivationHowMap.put(TraitType.Consistency,
          new BigDecimal(responseArray[27]).setScale(PRECISION, RoundingMode.HALF_UP));
      motivationHowMap.put(TraitType.TaskCompletion,
          new BigDecimal(responseArray[28]).setScale(PRECISION, RoundingMode.HALF_UP));
      motivationHowMap.put(TraitType.SelfAffirmed,
          new BigDecimal(responseArray[29]).setScale(PRECISION, RoundingMode.HALF_UP));
      motivationHowMap.put(TraitType.ReceiveDirection,
          new BigDecimal(responseArray[30]).setScale(PRECISION, RoundingMode.HALF_UP));
      motivationHowMap.put(TraitType.Freedom,
          new BigDecimal(responseArray[31]).setScale(PRECISION, RoundingMode.HALF_UP));
      motivationHowMap.put(TraitType.PrefersProcess,
          new BigDecimal(responseArray[32]).setScale(PRECISION, RoundingMode.HALF_UP));
      analysisBean.setMotivationHow(motivationHowMap);
      
      // setting the motivation why style
      Map<TraitType, BigDecimal> motivationWhatMap = new HashMap<TraitType, BigDecimal>();
      motivationWhatMap.put(TraitType.Hygiene,
          new BigDecimal(responseArray[33]).setScale(PRECISION, RoundingMode.HALF_UP));
      motivationWhatMap.put(TraitType.Accomplishment,
          new BigDecimal(responseArray[34]).setScale(PRECISION, RoundingMode.HALF_UP));
      analysisBean.setMotivationWhat(motivationWhatMap);
      
      // setting the personality style
      HashMap<RangeType, PersonalityBeanResponse> personalityType = new HashMap<RangeType, PersonalityBeanResponse>();
      PersonalityBeanResponse personalityBean = personalityFactory
          .getPersonalityFromSegmentScore(responseArray[35]);
      personalityType.put(RangeType.Primary, personalityBean);
      personalityBean = personalityFactory.getPersonalityFromSegmentScore(responseArray[36]);
      personalityType.put(RangeType.UnderPressure, personalityBean);
      personalityBean = personalityFactory.getPersonalityFromSegmentScore(responseArray[37]);
      personalityType.put(RangeType.PerceivedByOthers, personalityBean);
      analysisBean.setPersonality(personalityType);
      
      // setting fundamental needs style
      Map<TraitType, BigDecimal> fundamentalNeedsMap = new HashMap<TraitType, BigDecimal>();
      fundamentalNeedsMap.put(TraitType.Security,
          new BigDecimal(responseArray[38]).setScale(PRECISION, RoundingMode.HALF_UP));
      fundamentalNeedsMap.put(TraitType.Significance,
          new BigDecimal(responseArray[39]).setScale(PRECISION, RoundingMode.HALF_UP));
      fundamentalNeedsMap.put(TraitType.Control,
          new BigDecimal(responseArray[40]).setScale(PRECISION, RoundingMode.HALF_UP));
      analysisBean.setFundamentalNeeds(fundamentalNeedsMap);
      
      // setting decision making
      Map<TraitType, BigDecimal> decisionMakingMap = new HashMap<TraitType, BigDecimal>();
      decisionMakingMap.put(TraitType.Careful,
          new BigDecimal(responseArray[41]).setScale(PRECISION, RoundingMode.HALF_UP));
      decisionMakingMap.put(TraitType.Rapid,
          new BigDecimal(responseArray[42]).setScale(PRECISION, RoundingMode.HALF_UP));
      decisionMakingMap.put(TraitType.Inward,
          new BigDecimal(responseArray[43]).setScale(PRECISION, RoundingMode.HALF_UP));
      decisionMakingMap.put(TraitType.Outward,
          new BigDecimal(responseArray[44]).setScale(PRECISION, RoundingMode.HALF_UP));
      analysisBean.setDecisionMaking(decisionMakingMap);
      
      analysisResultMap.put(responseArray[2], analysisBean);
    }
  }
  
  /**
   * This method loads all the responses.
   * 
   * @throws IOException
   *           - error loading the responses
   */
  private void loadResponses() throws IOException {
    List<String> responseList = IOUtils.readLines(responseCSV.getInputStream());
    responseList.remove(0); // removing the header line
    for (String responseLine : responseList) {
      String[] columnCSVArray = StringUtils.split(responseLine, ',');
      String testId = columnCSVArray[1];
      List<String> userResponseList = responseMap.get(testId);
      if (userResponseList == null) {
        userResponseList = new ArrayList<String>();
        responseMap.put(testId, userResponseList);
      }
      StringBuffer responseStr = new StringBuffer(columnCSVArray[4]);
      
      if (columnCSVArray.length > 7) {
        responseStr.deleteCharAt(0); // removing "
        for (int i = 5; i < columnCSVArray.length; i++) {
          responseStr.append(CSV_DELIMITTER).append(columnCSVArray[i]);
          if (columnCSVArray[i].contains("\"")) {
            responseStr.deleteCharAt(responseStr.length() - 1);
            break;
          }
        }
      }
      userResponseList.add(columnCSVArray[3] + ":" + responseStr.toString());
    }
    // weed out the less than 200 responses
    for (String key : responseMap.keySet()) {
      List<String> userResponseList = responseMap.get(key);
      if (userResponseList.size() != 200) {
        log.debug("Weeding out assessment :" + key + ":" + userResponseList.size());
        responseMap.remove(key);
      }
    }
    log.debug("The response map :" + responseMap);
  }
  
  /**
   * Load the questions.
   */
  private void loadQuestions() {
//    // creating the question map to generate the selections and the results in the required formats
//    QuestionsFactory questionsFactory = abstractQuestionsFactory.getQuestionFactory("en_US",
//        QuestionFactoryType.ThirdPartyQuestion);
//    Questions question = questionsFactory.getQuestions();
//    
//    int catNum = 0;
//    int questionNum;
//    List<OptionsBean> responseVariants;
//    for (Category category : question.getCategoryList()) {
//      questionNum = 0;
//      for (Question q : category.getQuestionList()) {
//        QuestionsBean questionBean = new QuestionsBean();
//        questionBean.setCategoryNumber(catNum);
//        questionBean.setNumber(questionNum++);
//        responseVariants = new ArrayList<OptionsBean>();
//        if (q.getVariant().isSetMultipleChoiceResponses()) {
//          for (MultipleChoiceResponse mcr : q.getVariant().getMultipleChoiceResponses()
//              .getMultipleChoiceResponseList()) {
//            responseVariants.add(new OptionsBean(mcr));
//          }
//        } else {
//          for (RatingResponse ratingResponse : q.getVariant().getRatingResponses()
//              .getRatingResponseList()) {
//            responseVariants.add(new OptionsBean(ratingResponse));
//          }
//        }
//        questionBean.setOptionsList(responseVariants);
//        questionMap.put(q.getVariant().getId(), questionBean);
//      }
//      catNum++;
//    }
  }
  
//  @Test
//  public void testSingleTest() throws Exception {
//    String testId = "6099";
//    List<String> userResponseList = responseMap.get(testId);
//    MvcResult result = null;
//    int index = 0;
//    
//    assertNotNull("The user response map for " + testId, userResponseList);
//    
//    for (String userResponse : userResponseList) {
//      String[] userResponseArray = userResponse.split(":");
//      String[] selectionArray = userResponseArray[1].split(CSV_DELIMITTER);
//      QuestionsBean questionBean = questionMap.get(userResponseArray[0]);
//      assertNotNull("User bean found null for variant id :" + userResponseArray[0], questionBean);
//      StringBuffer answer = new StringBuffer();
//      index = 0;
//      for (String selectionStr : selectionArray) {
//        int idx = questionBean.getOptionsList().indexOf(selectionStr);
//        if (idx == -1) {
//          log.debug("Could not find selection :" + selectionStr + ": in trait id :"
//              + userResponseArray[0] + ":List:" + questionBean.getOptionsList());
//          throw new RuntimeException();
//        }
//        answer.append(questionBean.getOptionsList().indexOf(selectionStr));
//        if (index < selectionArray.length - 1) {
//          answer.append(CSV_DELIMITTER);
//        }
//        index++;
//      }
//      
//      result = processQuestion(questionBean.getCategoryNumber(), questionBean.getNumber(),
//          answer.toString());
//    }
//    log.debug("Received response :" + result.getResponse().getContentAsString());
//    JSONObject jsonObject = new JSONObject(result.getResponse().getContentAsString())
//        .getJSONObject("success");
//    assertThat(jsonObject.get("Success").toString(), is("true"));
//    User user = dbSetup.getUser("admin@admin.com");
//    AnalysisBean analysis = user.getAnalysis();
//    log.debug("Analysis object created :" + analysis);
//    assertNotNull("The final analysis object was null !!!", analysis);
//    
//    AnalysisBean refAnalysis = analysisResultMap.get(testId);
//    assertNotNull("The reference analysis was not found for TestID :" + testId, refAnalysis);
//    
//    assertTrue(
//        "The accuracy type score :" + convert(analysis.getAccuracy()) + ":"
//            + refAnalysis.getAccuracy(),
//        getAccuracyIndex(convert(analysis.getAccuracy())) == getAccuracyIndex(refAnalysis
//            .getAccuracy()));
//    compareTraits(CategoryType.Processing, analysis.getProcessing(), refAnalysis.getProcessing());
//    compareTraits(CategoryType.ConflictManagement, analysis.getConflictManagement(),
//        refAnalysis.getConflictManagement());
//    compareTraits(CategoryType.LearningStyle, analysis.getLearningStyle(),
//        refAnalysis.getLearningStyle());
//    compareTraits(CategoryType.Motivation, analysis.getMotivationWhy(),
//        refAnalysis.getMotivationWhy());
//    compareTraits(CategoryType.Motivation, analysis.getMotivationHow(),
//        refAnalysis.getMotivationHow());
//    compareTraits(CategoryType.Motivation, analysis.getMotivationWhat(),
//        refAnalysis.getMotivationWhat());
//    // validate personality types
//    Map<RangeType, PersonalityBeanResponse> personalityMap = analysis.getPersonality();
//    Map<RangeType, PersonalityBeanResponse> refPersonalityMap = refAnalysis.getPersonality();
//    for (RangeType r : personalityMap.keySet()) {
//      PersonalityBean pb = personalityMap.get(r);
//      PersonalityBean refPB = refPersonalityMap.get(r);
//      assertTrue(
//          "The personality type mismatch for range type :" + r + ": " + pb.getPersonalityType()
//              + ":" + refPB.getPersonalityType(),
//          pb.getPersonalityType() == refPB.getPersonalityType());
//    }
//    
//    compareTraits(CategoryType.FundamentalNeeds, analysis.getFundamentalNeeds(),
//        refAnalysis.getFundamentalNeeds());
//    compareTraits(CategoryType.DecisionMaking, analysis.getDecisionMaking(),
//        refAnalysis.getDecisionMaking());
//    
//    assertTrue(true);
//  }
  
  private BigDecimal convert(BigDecimal value) {
    return value.movePointLeft(Constants.POINT_MOVEMENT);
  }
  
  /**
   * Get the accuracy validation.
   * 
   * @param accuracy
   *          - the accuracy data get index for
   * @return - the index from Accuracy range high and low
   */
  private int getAccuracyIndex(BigDecimal accuracy) {
    double accuracyDbl = accuracy.doubleValue();
    int index1;
    for (index1 = 0; index1 < ACCURACY_RANGE_HIGH.length; index1++) {
      if (ACCURACY_RANGE_HIGH[index1] > accuracyDbl) {
        break;
      }
    }
    
    if (index1 == ACCURACY_RANGE_HIGH.length) {
      throw new RuntimeException("The accuracy value :" + accuracyDbl + ": was not found in "
          + ACCURACY_RANGE_HIGH.length);
    }
    
    int index2;
    for (index2 = 0; index2 < ACCURACY_RANGE_LOW.length; index2++) {
      if (ACCURACY_RANGE_LOW[index2] > accuracyDbl) {
        break;
      }
    }
    
    if (index1 != index2 - 1) {
      throw new RuntimeException(
          "The accuracy index mismatched between High Range and Low Range !!!");
    }
    
    return index1;
  }
  
  /**
   * Generate question for data.
   */
  //@Test
  public void testGenerateQuestionsForPrecisionConsultingCompany() {
//    QuestionsFactory questionsFactory = abstractQuestionsFactory.getQuestionFactory("en_US",
//        QuestionFactoryType.SPAssessmentQuestion);
//    Questions question = questionsFactory.getQuestions();
//    
//    StringBuffer sb = new StringBuffer();
//    StringBuffer optionsBuffer = new StringBuffer();
//    
//    String categoryDirectionsString = null;
//    String questionTextString = null;
//    
//    for (Category category : question.getCategoryList()) {
//      categoryDirectionsString = category.getDirections();
//      for (Question q : category.getQuestionList()) {
//        questionTextString = q.getVariant().getText();
//        if (questionTextString == null || questionTextString.trim().length() == 0) {
//          sb.append("\"").append(categoryDirectionsString).append("\"").append(TSV_DELIMITTER);
//        } else {
//          sb.append("\"").append(questionTextString).append("\"").append(TSV_DELIMITTER);
//        }
//        optionsBuffer.append("\"");
//        if (q.getVariant().isSetMultipleChoiceResponses()) {
//          for (MultipleChoiceResponse mcr : q.getVariant().getMultipleChoiceResponses()
//              .getMultipleChoiceResponseList()) {
//            optionsBuffer.append(mcr.getText()).append(TSV_DELIMITTER).append("\n");
//          }
//        } else {
//          for (RatingResponse ratingResponse : q.getVariant().getRatingResponses()
//              .getRatingResponseList()) {
//            optionsBuffer.append(ratingResponse.getText()).append(TSV_DELIMITTER).append("\n");
//          }
//        }
//        optionsBuffer.deleteCharAt(optionsBuffer.length() - 1);
//        optionsBuffer.deleteCharAt(optionsBuffer.length() - 1);
//        optionsBuffer.append("\"").append(TSV_DELIMITTER);
//      }
//    }
//    File fileToWrite = new File("b.csv");
//    PrintWriter pw = null;
//    try {
//      pw = new PrintWriter(fileToWrite, "UTF-8");
//      pw.write(sb.toString());
//      pw.write("\n");
//      pw.write(optionsBuffer.toString());
//    } catch (FileNotFoundException e) {
//      e.printStackTrace();
//      fail(e.getMessage());
//    } catch (IOException e) {
//      e.printStackTrace();
//      fail(e.getMessage());
//    } finally {
//      if (pw != null) {
//        pw.flush();
//        pw.close();
//      }
//    }
//    log.debug("Questions :\n" + sb);
//    log.debug("Options: \n" + optionsBuffer);
  }
  
  /**
   * Get the list of numbers to skip from the questions set.
   */
  //@Test
  public void testGenerateQuestionsNumbersToSkip() {
//    QuestionsFactory questionsFactory = abstractQuestionsFactory.getQuestionFactory("en_US",
//        QuestionFactoryType.SPAssessmentQuestion);
//    Questions question = questionsFactory.getQuestions();
//    
//    StringBuffer sb = new StringBuffer();
//    
//    short index = 1;
//    for (Category category : question.getCategoryList()) {
//      for (Question q : category.getQuestionList()) {
//        short questionNumber = q.getNumber();
//        if (index < questionNumber) {
//          while (index < questionNumber) {
//            sb.append(index).append(",");
//            index++;
//          }
//        }
//        index++;
//      }
//    }
//    System.out.println(sb.toString());
  }  
  
//  /**
//   * Generate the data.
//   */
//  // @Test
//  public void testGenerateDataForPrecisionConsultingCompany() {
//    
//    String[] testIdArray = { "44", "6076", "45", "6075", "46", "6070", "6072", "6071", "6073",
//        "12", "14", "6088", "15", "16", "17", "18", "3057", "3056", "5", "8", "22", "6098", "23",
//        "6097", "25", "6099", "26", "6058", "7103", "27", "28", "29", "6059", "7104", "6090",
//        "6092", "6091", "6094", "6093", "6096", "6095", "30", "32", "6065", "34", "6064", "35",
//        "6067", "6100", "36", "5055", "6066", "37", "5056", "6069", "6102", "38", "5057", "6068",
//        "39", "5058", "1055", "1053", "1052", "1051", "6061", "6060", "6063", "6062", "1049",
//        "1047", "40", "41", "42", "43" };
//    StringBuffer sb = new StringBuffer();
//    
//    // System.out.println(responseMap.keySet().stream().map(k -> "\"" + k + "\"")
//    // .collect(Collectors.joining(",")));
//    
//    int index;
//    for (String testId : testIdArray) {
//      List<String> userResponseList = responseMap.get(testId);
//      if (userResponseList == null) {
//        throw new RuntimeException("Invalid test id :" + testId);
//      }
//      sb.append(testId).append(CSV_DELIMITTER);
//      for (String userResponse : userResponseList) {
//        String[] userResponseArray = userResponse.split(":");
//        String[] selectionArray = userResponseArray[1].split(CSV_DELIMITTER);
//        QuestionsBean questionsBeanArray = questionMap.get(userResponseArray[0]);
//        StringBuffer answer = new StringBuffer();
//        index = 0;
//        for (String selectionStr : selectionArray) {
//          int idx = questionsBeanArray.getOptionsList().indexOf(selectionStr);
//          if (idx == -1) {
//            log.debug("Could not find selection :" + selectionStr + ": in trait id :"
//                + userResponseArray[0] + ":List:" + questionsBeanArray.getOptionsList());
//            throw new RuntimeException();
//          }
//          answer.append(questionsBeanArray.getOptionsList().indexOf(selectionStr));
//          if (index < selectionArray.length - 1) {
//            answer.append(CSV_DELIMITTER);
//          }
//          index++;
//        }
//        if (answer.toString().contains(CSV_DELIMITTER)) {
//          sb.append("\"").append(answer).append("\"");
//        } else {
//          sb.append(answer);
//        }
//        sb.append(CSV_DELIMITTER);
//      }
//      sb.deleteCharAt(sb.length() - 1);
//      sb.append("\n");
//    }
//    log.debug("Answer :\n" + sb);
//  }
  
  /**
   * Helper method to Compare the traits.
   * 
   * @param traitsMap
   *          - the processing map to compare
   * @param refTraitsMap
   *          - the reference processing map
   */
  private void compareTraits(CategoryType category, Map<TraitType, BigDecimal> traitsMap,
      Map<TraitType, BigDecimal> refTraitsMap) {
    for (TraitType trait : traitsMap.keySet()) {
      try {
        BigDecimal value1 = convert(traitsMap.get(trait));
        BigDecimal value2 = refTraitsMap.get(trait).setScale(2, Constants.ROUNDING_MODE);
        assertTrue("The " + category + ":" + trait + " score :" + value1 + ":" + value2,
            value1.compareTo(value2) == 0);
      } catch (Exception e) {
        log.debug("Could not process Category: " + category + " trait :" + trait, e);
        throw e;
      }
    }
  }
  
  /**
   * Process request.
   * 
   * @param categoryNumber
   *          - cat
   * @param questionNumber
   *          - question
   * @param answer
   *          - answer
   * @return - response
   * @throws Exception
   *           - error processing request
   */
  private MvcResult processQuestion(int categoryNumber, int questionNumber, String answer)
      throws Exception {
    MvcResult result;
    result = this.mockMvc
        .perform(
            post("/assessment/process").contentType(MediaType.TEXT_PLAIN).session(session)
                .param("c", categoryNumber + "").param("q", questionNumber + "").param("a", answer))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        
        .andReturn();
    return result;
  }
  
  @Test
  public void testLoadPreviousAnalysis() {
    final String userEmail = "dax@surepeople.com";
    String oldUserId = "7166";
    
    dbSetup.removeAllUsers();
    try {
      dbSetup.createUsers();
    } catch (IOException e) {
      e.printStackTrace();
      fail();
    }
    
    AnalysisBean analysis = analysisResultMap.get(oldUserId);
    assertNotNull("Old user Analysis not found !!!", analysis);
    
    User user = dbSetup.getUser(userEmail);
    assertNotNull("User :" + userEmail, user);
    
    // setting the analysis
    user.setAnalysis(new AnalysisBeanDTO(analysis));
    dbSetup.addUpdate(user);
    
  }
  
}
