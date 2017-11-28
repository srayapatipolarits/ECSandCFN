package com.sp.web.mvc.assessment;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sp.web.Constants;
import com.sp.web.assessment.questions.AssessmentQuestionFactory;
import com.sp.web.assessment.questions.QuestionFactoryType;
import com.sp.web.assessment.questions.QuestionsBean;
import com.sp.web.assessment.questions.QuestionsFactory;
import com.sp.web.assessment.questions.ThirdPartyQuestionsFactory;
import com.sp.web.assessment.questions.TraitsBean;
import com.sp.web.model.FeedbackUser;
import com.sp.web.model.User;
import com.sp.web.model.UserType;
import com.sp.web.mvc.test.setup.SPTestFeedbackUserLoggedInBaseTest;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.Set;

/**
 * @author Dax Abraham
 *
 *         The assessment test class for third party assessment controller.
 */
public class ThirdPartyAssessmentControllerTest extends SPTestFeedbackUserLoggedInBaseTest {

  private ObjectMapper om = new ObjectMapper();

  @Autowired
  AssessmentQuestionFactory abstractQuestionsFactory;
  
  ThirdPartyQuestionsFactory questionsFactory;

  private FeedbackUser fbUser;

  /**
   * Doing setup.
   */
  @Before
  public void doSetup() throws Exception {
    if (fbUser == null) {
      dbSetup.removeAll("feedbackUser");
      User fbUserSrc = dbSetup.getUser("admin@admin.com");
      fbUser = new FeedbackUser();
      fbUser.setEmail(fbUserSrc.getEmail());
      fbUser.setCompanyId(fbUserSrc.getCompanyId());
      fbUser.setFirstName(fbUserSrc.getFirstName());
      fbUser.setLastName(fbUserSrc.getLastName());
      User user = dbSetup.getUser("dax@surepeople.com");
      dbSetup.addUpdate(user);
      fbUser.setFeedbackFor(user.getId());
      fbUser.setType(UserType.Member);
      dbSetup.addUpdate(fbUser);
    }
    
    questionsFactory = (ThirdPartyQuestionsFactory) abstractQuestionsFactory.getQuestionFactory(
        Constants.DEFAULT_LOCALE, QuestionFactoryType.ThirdPartyQuestion);
  }
  
//  @Test
//  public void testInitialAssessment() {
//
//    dbSetup.removeAllAssessmentProgressStore();
//
//    try {
//      MvcResult result = initialAssessment();
//      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
//    } catch (Exception e) {
//      e.printStackTrace();
//    }
//  }
//  
//  private MvcResult initialAssessment() throws Exception {
//    MvcResult result = this.mockMvc
//        .perform(post("/assessment360/start/" + fbUser.getId())
//        .contentType(MediaType.TEXT_PLAIN).session(session))
//        .andExpect(content().contentType("application/json;charset=UTF-8"))
//        .andExpect(jsonPath("$.success.question.description").doesNotExist())
//        .andExpect(jsonPath("$.success.question.type").value("PersonalityRating"))
//        .andReturn();
//    log.debug("Result is :" + result.getResponse().getContentAsString());
//    return result;
//  }
//  
//  @Test
//  public void testStartAndStopAssessment() throws Exception {
//    // clean up before test
//    dbSetup.removeAllAssessmentProgressStore();
//    int questionNumber = 0;
//
//    // do the initial assessment
//    initialAssessment();
//
//    // answer the first question
//    QuestionsBean questionsBean = processAndGetQuestionBean(questionNumber);
//    assertNotNull("The next question !!!", questionsBean);
//
//    // authenticating second user
////    MvcResult result = this.mockMvc
////        .perform(post("/assessment360/start").contentType(MediaType.TEXT_PLAIN).session(session))
////        .andExpect(content().contentType("application/json;charset=UTF-8"))
////        .andExpect(jsonPath("$.success.question.number").value(1))
////        .andExpect(jsonPath("$.success.question.categoryNumber").value(0)).andReturn();
////
////    log.debug("The response :" + result.getResponse().getContentAsString());
//
//    questionNumber = 9;
//    MvcResult result = processQuestion(questionNumber, "0");
//
//    // answer the last question and see if analysis is present
//    questionNumber = 20;
//
//    result = processQuestion(questionNumber, "0,1");
//    JSONObject jsonObject = new JSONObject(result.getResponse().getContentAsString())
//        .getJSONObject("error");
//    assertThat("Result is failed as not all questions have been answered !!!",
//        jsonObject.getString("InvalidParameterException"),
//        is("Invalid answer cannot be null or not present in the request."));
//    log.debug("The response :" + result.getResponse().getContentAsString());
//
//  }
//  
//  @Test
//  public void testProcessNextAssessment() throws Exception {
//    // clean up before test
//    dbSetup.removeAllAssessmentProgressStore();
//    int questionNumber = 1;
//
//    QuestionsBean questionsBean = processAndGetQuestionBean(questionNumber);
//
//    questionNumber++;
//    assertThat("Validate question number incremented !!!", questionsBean.getNumber(),
//        is(questionNumber));
//    log.debug("The MVC Response : " + questionsBean);
//
//    questionsBean = processAndGetQuestionBean(questionNumber);
//
//    questionNumber++;
//    assertThat("Validate question number incremented !!!", questionsBean.getNumber(),
//        is(questionNumber));
//    log.debug("The MVC Response : " + questionsBean);
//
//    // commented as there is only one section in lens now
////    questionNumber = questionsFactory.getCategoriesSummary().getCategoriesList()
////        .get(categoryNumber).getTotalQuestions() - 1;
////    questionsBean = processAndGetQuestionBean(categoryNumber, questionNumber);
////
////    assertThat("Validate question number incremented !!!", questionsBean.getNumber(),
////        is(0));
////    assertThat("Validate category number incremented !!!", questionsBean.getCategoryNumber(),
////        is(++categoryNumber));
////    log.debug("The MVC Response : " + questionsBean.asJSONString());
//  }
//  
//  @Test
//  public void testProcessPreviousAssessment() throws Exception {
//    // clean up before test
//    dbSetup.removeAllAssessmentProgressStore();
//
//    int questionNumber = 1;
//
//    QuestionsBean questionsBean = processAndGetQuestionBean(questionNumber);
//
//    questionNumber++;
//    assertTrue("Validate question number incremented !!!", questionsBean.getNumber() == questionNumber);
//    log.debug("The MVC Response : " + questionsBean);
//
//    questionsBean = processAndGetQuestionBean(questionNumber);
//
//    questionNumber++;
//    assertTrue("Validate question number incremented !!!", questionsBean.getNumber() == questionNumber);
//    log.debug("The MVC Response : " + questionsBean);
//
//    MvcResult result;
//    result = this.mockMvc
//        .perform(post("/assessment360/previous/" + fbUser.getId())
//        .contentType(MediaType.TEXT_PLAIN).session(session))
//        .andExpect(content().contentType("application/json;charset=UTF-8"))
//        .andExpect(jsonPath("$.success").exists())
//        .andExpect(jsonPath("$.success.Success").value("true"))
//        .andExpect(jsonPath("$.success.question.number").value(questionNumber - 1))
//        .andReturn();
//
//    log.debug("The MVC result is:" + result.getResponse().getContentAsString());
//  }
//  
//  @Test
//  public void testInvalidCategory() throws Exception {
//    int questionNumber = 1;
//
//    MvcResult result = processQuestion(questionNumber, "0");
//    assertNotNull(result);
//    assertTrue(
//        "Category Error Message !!!" + result.getResponse().getContentAsString(),
//        result.getResponse().getContentAsString()
//            .contains("Invalid category number :-1: sent in the request."));
//
//    result = processQuestion(questionNumber, "0");
//    assertNotNull(result);
//    assertTrue(
//        "Category Error Message !!!" + result.getResponse().getContentAsString(),
//        result.getResponse().getContentAsString()
//            .contains("Invalid category number :1: sent in the request."));
//  }
//  
//  @Test
//  public void testInvalidQuestionNumber() throws Exception {
//    int questionNumber = -1;
//
//    MvcResult result = processQuestion(questionNumber, "0");
//    assertNotNull(result);
//    assertTrue("Invalid question number error message !!!"
//        + result.getResponse().getContentAsString(), result.getResponse().getContentAsString()
//        .contains("Invalid question number :-1: sent in the request"));
//
//    result = processQuestion(questionNumber, "0");
//    assertNotNull(result);
//    assertTrue(
//        "Category Error Message !!!" + result.getResponse().getContentAsString(),
//        result.getResponse().getContentAsString()
//            .contains("Invalid question number :28: sent in the request."));
//  }
//
//  @Test
//  public void testInvalidAnswer() throws Exception {
//
//    dbSetup.removeAll("assessmentProgressStore");
//    
//    // multiple choice type of question
//    int questionNumber = 0;
//
//    MvcResult result = processQuestion(questionNumber, "");
//    assertNotNull(result);
//    assertTrue("Invalid question number error message !!!"
//        + result.getResponse().getContentAsString(), result.getResponse().getContentAsString()
//        .contains("Invalid answer cannot be null or not present in the request"));
//
//    result = processQuestion(questionNumber, "-1, 100");
//    assertNotNull(result);
//    assertTrue(
//        "Invalid question number error message !!!" + result.getResponse().getContentAsString(),
//        result
//            .getResponse()
//            .getContentAsString()
//            .contains(
//                "Invalid answer multiple choice question type invalid selection number in the request"));
//
//    result = processQuestion(questionNumber, "100, 100");
//    assertNotNull(result);
//    assertTrue(
//        "Invalid question number error message !!!" + result.getResponse().getContentAsString(),
//        result
//            .getResponse()
//            .getContentAsString()
//            .contains(
//                "Invalid answer multiple choice question type invalid selection number in the request"));
//
////    result = processQuestion(categoryNumber, questionNumber, "0,1");
////    assertNotNull(result);
////    assertTrue(
////        "Invalid question number error message !!!" + result.getResponse().getContentAsString(),
////        result
////            .getResponse()
////            .getContentAsString()
////            .contains(
////                "Invalid answer multiple choice question type can have only one selection in the request"));
//
//    // rating type of question
//    questionNumber = 0;
//
//    result = processQuestion(questionNumber, "0");
//    assertNotNull(result);
//    assertTrue(
//        "Invalid question number error message !!!" + result.getResponse().getContentAsString(),
//        result
//            .getResponse()
//            .getContentAsString()
//            .contains(
//                "Invalid category number :3: sent in the request."));
//
//    result = processQuestion(questionNumber, "-1,1,2");
//    assertNotNull(result);
//    assertTrue(
//        "Invalid question number error message !!!" + result.getResponse().getContentAsString(),
//        result
//            .getResponse()
//            .getContentAsString()
//            .contains(
//           "Invalid answer ratings question type must have selections equal to number of options in the request."));
//
//    result = processQuestion(questionNumber, "10,1,2");
//    assertNotNull(result);
//    assertTrue(
//        "Invalid question number error message !!!" + result.getResponse().getContentAsString(),
//        result
//            .getResponse()
//            .getContentAsString()
//            .contains(
//                "Invalid answer ratings question type must have selections "
//                + "equal to number of options in the request."));
//  }
  
  @Test
  public void testProcessRandomCompleteAssessment() throws Exception {

    // clean up before test
    dbSetup.removeAll("activityTracking");
    dbSetup.removeAll("assessmentProgressTracker");
    dbSetup.removeAll("prismAssessment");
    

    MvcResult result = null;

    try {
      Map<Integer, TraitsBean> traitsMap = questionsFactory.getTraitsMap();
      final String userId = fbUser.getId();
      
      result = this.mockMvc
          .perform(
              post("/assessment360/start/" + userId)
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())        
          .andExpect(content().contentType("application/json;charset=UTF-8")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      QuestionsBean questionsBean = getQuestionsBean(result);
      
      while (questionsBean != null) {
        log.debug("Processing Question Number :" + questionsBean.getNumber());
        questionsBean = processRandomAssessment(questionsBean, traitsMap, userId);
      }
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
    
    Thread.sleep(2000);
  }  
  
  private QuestionsBean getQuestionsBean(MvcResult result) throws JSONException,
      UnsupportedEncodingException, IOException, JsonParseException, JsonMappingException {
    JSONObject jsonObject = new JSONObject(result.getResponse().getContentAsString());
    jsonObject = jsonObject.getJSONObject("success");
    return om.readValue(jsonObject.get("question").toString(), QuestionsBean.class);
  }
  
  private QuestionsBean processRandomAssessment(QuestionsBean questionsBean,
      Map<Integer, TraitsBean> traitsMap, String userId) throws Exception {
    final int qNo = questionsBean.getNumber();
    TraitsBean trait = traitsMap.get(qNo);
    List<String> answerRandomizer = null;
    final Set<String> traitKeys = trait.getTraitsTransformMap().keySet();
    String ans = null;
    switch (trait.getType()) {
    case MultipleChoice:
      answerRandomizer = randomizeAnswer(traitKeys);
      ans = answerRandomizer.get(0);
      break;
    case Rating:
      ans = StringUtils.join(randomizeAnswer(traitKeys, trait.getFactor().length), ',');
      break;
    case PersonalityRating:
      ans = StringUtils.join(randomizeAnswer(traitKeys, trait.getFactor().length), ',');
      break;
    case VariableRating:
      ans = StringUtils.join(randomizeAnswer(traitKeys, trait.getFactor().length), ',');
      break;
    default:
      throw new RuntimeException("Do not know how to process :" + trait.getType());
    }
    
    log.debug("QNo:"
        + qNo
        + ":{"
        + StringUtils.join(
            questionsBean.getOptionsList().stream().map(Object::toString)
                .collect(Collectors.toList()), ',') + "}, Selection :[" + ans + "]");
    
    MvcResult result = this.mockMvc
        .perform(
            post("/assessment360/process/" + userId)
            .param("questionNum", qNo + "")
            .param("ans", ans)
            .contentType(MediaType.TEXT_PLAIN).session(session))
        .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())        
        .andExpect(content().contentType("application/json;charset=UTF-8")).andReturn();
    
    String respString = result.getResponse().getContentAsString();
    if (respString.contains("question")) {
      return getQuestionsBean(result);
    } else {
      log.debug("Processing done !!!");
    }
    return null;
  }
  
//  private QuestionsBean processAndGetQuestionBean(int questionNumber)
//      throws Exception {
//    return processAndGetQuestionBean(questionNumber, "0, 2");
//  }
//
//  private QuestionsBean processAndGetQuestionBean(int questionNumber,
//      String answer) throws Exception {
//    MvcResult result = processQuestion(questionNumber, answer);
//    JSONObject jsonObject = new JSONObject(result.getResponse().getContentAsString())
//        .getJSONObject("success");
//    QuestionsBean questionsBean = om.readValue(jsonObject.get("question").toString(), QuestionsBean.class);
//    return questionsBean;
//  }

  /**
   * Method to randomize the answers ordering.
   * 
   * @param traitKeys
   *          - the size of array to return
   * @return - the random order for the answers
   */
  private List<String> randomizeAnswer(Set<String> traitKeys) {
    List<String> ansOrder = new ArrayList<String>(traitKeys);
    Collections.shuffle(ansOrder);
    return ansOrder;
  }
  
  private List<String> randomizeAnswer(Set<String> traitKeys, int length) {
    List<String> randomizeAnswer = randomizeAnswer(traitKeys);
    return randomizeAnswer.subList(0, length);
  }

//  private MvcResult processQuestion(int questionNumber, String answer)
//      throws Exception {
//    MvcResult result;
//    result = this.mockMvc
//        .perform(
//            post("/assessment360/process/" + fbUser.getId())
//            .contentType(MediaType.TEXT_PLAIN).session(session)
//                .param("q", questionNumber + "").param("a", answer))
//        .andExpect(content().contentType("application/json;charset=UTF-8")).andReturn();
//    return result;
//  }
}
