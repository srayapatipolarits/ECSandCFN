package com.sp.web.mvc.assessment;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sp.web.Constants;
import com.sp.web.assessment.processing.AnalysisBean;
import com.sp.web.assessment.processing.ScoreBean;
import com.sp.web.assessment.questions.AssessmentQuestionFactory;
import com.sp.web.assessment.questions.AssessmentType;
import com.sp.web.assessment.questions.CategoryType;
import com.sp.web.assessment.questions.OptionsBean;
import com.sp.web.assessment.questions.QuestionFactoryType;
import com.sp.web.assessment.questions.QuestionsBean;
import com.sp.web.assessment.questions.QuestionsFactory;
import com.sp.web.assessment.questions.TraitType;
import com.sp.web.assessment.questions.TraitsBean;
import com.sp.web.assessment.questions.TraitsTransform;
import com.sp.web.dao.CompanyDao;
import com.sp.web.model.Gender;
import com.sp.web.model.HiringUser;
import com.sp.web.model.RoleType;
import com.sp.web.model.User;
import com.sp.web.model.UserStatus;
import com.sp.web.model.UserType;
import com.sp.web.model.assessment.PrismAssessment;
import com.sp.web.mvc.test.setup.SPTestLoggedInBase;
import com.sp.web.product.CompanyFactory;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

public class AssessmentControllerTests extends SPTestLoggedInBase {

  @Value("classpath:assessment-initial-sections.json")
  private Resource sampleHtml;
  private String initialJson;
  private ObjectMapper om;
  
  @Autowired
  AssessmentQuestionFactory assessmentquestionsFactory;

  @Autowired
  CompanyFactory companyFactory;

  /*
   * The reference to the questions factory
   */
  @Autowired
  AssessmentQuestionFactory abstractQuestionsFactory;
  
  private QuestionsFactory questionsFactory;
  
  /**
   * Setup.
   * 
   * @throws Exception
   *          - throw exception
   */
  @Before
  public void setup1() throws Exception {
    try {
      this.initialJson = IOUtils.toString(sampleHtml.getInputStream());
      om = new ObjectMapper();
      // login the user
      log.debug("Session :" + session.getId());
      if (questionsFactory == null) {
        questionsFactory = abstractQuestionsFactory.getQuestionFactory(Constants.DEFAULT_LOCALE,
            QuestionFactoryType.SPAssessmentQuestion);
      }
      
      testSmtp.start();
    } catch (Error e) {
      e.printStackTrace();
      fail();
    }
  }

  @After
  public void after() {
    testSmtp.stop();
  }

  @Test
  public void testInitialAssessment() throws Exception {

    dbSetup.removeAllAssessmentProgressStore();

    MvcResult result = initialAssessment();
    log.debug("The MVC Response : " + result.getResponse().getContentAsString());
  }

  private MvcResult initialAssessment() throws Exception {
    MvcResult result = this.mockMvc
        .perform(post("/assessment/start").contentType(MediaType.TEXT_PLAIN).session(session))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(content().json(initialJson)).andReturn();
    return result;
  }

  @Test
  public void getAssessmentForm() throws Exception {
    this.mockMvc.perform(get("/assessment").contentType(MediaType.TEXT_PLAIN).session(session))
        .andExpect(MockMvcResultMatchers.view().name("assessment")).andReturn();
  }

  @Test
  public void testProcessNextAssessment() throws Exception {
    // clean up before test
    dbSetup.removeAllAssessmentProgressStore();
    int questionNumber = 1;

    QuestionsBean questionBean = processAndGetQuestionBean(questionNumber);

    questionNumber++;
    assertTrue("Validate question number incremented !!!", questionBean.getNumber() == questionNumber);
    log.debug("The MVC Response : " + questionBean);

    questionBean = processAndGetQuestionBean(questionNumber);

    questionNumber++;
    assertTrue("Validate question number incremented !!!", questionBean.getNumber() == questionNumber);
    log.debug("The MVC Response : " + questionBean);

    questionNumber = questionsFactory.getCategoriesSummary().getCategoryMap()
        .get(CategoryType.Personality).getTotalQuestions() - 1;
    questionBean = processAndGetQuestionBean(questionNumber);

    assertTrue("Validate question number incremented !!!", questionBean.getNumber() == 0);
    log.debug("The MVC Response : " + questionBean.getNumber());

    // clean up after test
    dbSetup.removeAllAssessmentProgressStore();
  }

  @Test
  public void testInvalidCategory() throws Exception {
    int questionNumber = 1;

    MvcResult result = processQuestion(questionNumber, "0");
    assertNotNull(result);
    assertTrue(
        "Category Error Message !!!" + result.getResponse().getContentAsString(),
        result.getResponse().getContentAsString()
            .contains("Invalid category number :-1: sent in the request."));

    result = processQuestion(questionNumber, "0");
    assertNotNull(result);
    assertTrue(
        "Category Error Message !!!" + result.getResponse().getContentAsString(),
        result.getResponse().getContentAsString()
            .contains("Invalid category number :9: sent in the request."));

  }

  @Test
  public void testInvalidQuestionNumber() throws Exception {
    int questionNumber = -1;

    MvcResult result = processQuestion(questionNumber, "0");
    assertNotNull(result);
    assertTrue("Invalid question number error message !!!"
        + result.getResponse().getContentAsString(), result.getResponse().getContentAsString()
        .contains("Invalid question number :-1: sent in the request"));

    questionNumber = 40;
    result = processQuestion(questionNumber, "0");
    assertNotNull(result);
    assertTrue(
        "Category Error Message !!!" + result.getResponse().getContentAsString(),
        result.getResponse().getContentAsString()
            .contains("Invalid question number :40: sent in the request."));

  }

  @Test
  public void testInvalidAnswer() throws Exception {

    // multiple choice type of question
    int questionNumber = 0;

    MvcResult result = processQuestion(questionNumber, "");
    assertNotNull(result);
    assertTrue("Invalid question number error message !!!"
        + result.getResponse().getContentAsString(), result.getResponse().getContentAsString()
        .contains("Invalid answer cannot be null or not present in the request"));

    result = processQuestion(questionNumber, "-1");
    assertNotNull(result);
    assertTrue(
        "Invalid question number error message !!!" + result.getResponse().getContentAsString(),
        result
            .getResponse()
            .getContentAsString()
            .contains(
                "Invalid answer multiple choice question type invalid selection number in the request"));

    result = processQuestion(questionNumber, "100");
    assertNotNull(result);
    assertTrue(
        "Invalid question number error message !!!" + result.getResponse().getContentAsString(),
        result
            .getResponse()
            .getContentAsString()
            .contains(
                "Invalid answer multiple choice question type invalid selection number in the request"));

    result = processQuestion(questionNumber, "0,1");
    assertNotNull(result);
    assertTrue(
        "Invalid question number error message !!!" + result.getResponse().getContentAsString(),
        result
            .getResponse()
            .getContentAsString()
            .contains(
                "Invalid answer multiple choice question type can have only one selection in the request"));

    // rating type of question
    questionNumber = 0;

    result = processQuestion(questionNumber, "0");
    assertNotNull(result);
    assertTrue(
        "Invalid question number error message !!!" + result.getResponse().getContentAsString(),
        result
            .getResponse()
            .getContentAsString()
            .contains(
                "Invalid answer ratings question type must have selections equal to number of options in the request"));

    result = processQuestion(questionNumber, "-1,1,2");
    assertNotNull(result);
    assertTrue(
        "Invalid question number error message !!!" + result.getResponse().getContentAsString(),
        result
            .getResponse()
            .getContentAsString()
            .contains(
                "Invalid answer multiple choice question type invalid selection number in the request"));

    result = processQuestion(questionNumber, "10,1,2");
    assertNotNull(result);
    assertTrue(
        "Invalid question number error message !!!" + result.getResponse().getContentAsString(),
        result
            .getResponse()
            .getContentAsString()
            .contains(
                "Invalid answer multiple choice question type invalid selection number in the request"));

  }

  @Test
  public void testProcessRandomCompleteAssessment() {

    try {
      // clean up before test
      dbSetup.removeAllAssessmentProgressStore();
      dbSetup.removeAllLogs();
      dbSetup.removeSpGoals();
      dbSetup.createGoals();

      processRandomAssessment();
      
      User user = dbSetup.getUser("admin@admin.com");
      AnalysisBean analysis = user.getAnalysis();
      Map<TraitType, BigDecimal> fundamentalNeeds = analysis.getFundamentalNeeds();
      double sum = fundamentalNeeds.values().stream().mapToDouble(v -> v.doubleValue()).sum();
      
      assertThat(sum, not(greaterThan(100.0)));

      Thread.sleep(5000);
      
      // clean up after test
      dbSetup.removeAllAssessmentProgressStore();
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }

  }

  @Test
  public void testProcessCompleteAssessmentForHiringEmployee() throws Exception {

    try {
      // clean up before test
      dbSetup.removeAllAssessmentProgressStore();
      dbSetup.removeAllLogs();
      dbSetup.removeAllHiringUsers();
      dbSetup.removeAllHiringUsersArchive();
      dbSetup.removeSpGoals();
      dbSetup.createGoals();
      dbSetup.removeAll("personalityPracticeArea");
      dbSetup.createPersonalityPracticeAreas();
      dbSetup.removeArticles();
      dbSetup.createArticles();

      final String email = "dax@surepeople.com";
      String companyId = "1";

      // adding a hiring candidate 
      // and test to see if the assessment is copied over
      // for the hiring candidate
      HiringUser hiringUser = new HiringUser();
      hiringUser.setEmail(email);
      hiringUser.setCompanyId(companyId);
      hiringUser.setType(UserType.Member);
      hiringUser.addRole(RoleType.HiringEmployee);
      dbSetup.addUpdate(hiringUser);
      
      authenticationHelper.doAuthenticateWithoutPassword(session, hiringUser);

      assertThat(hiringUser.getAnalysis(), is(nullValue()));
      
      processRandomAssessment();
      
      Thread.sleep(3000);
      
      hiringUser = dbSetup.getHiringCandidate(email, companyId);
      assertThat(hiringUser.getAnalysis(), is(not(nullValue())));
      assertThat(hiringUser.getUserStatus(), is(UserStatus.VALID));
      
      Thread.sleep(5000);
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }

  @Test
  public void testProcessCompleteAssessmentForHiringCandidate() throws Exception {

    try {
      // clean up before test
      dbSetup.removeAllAssessmentProgressStore();
      dbSetup.removeAllLogs();
      dbSetup.removeAllHiringUsers();
      dbSetup.removeAllHiringUsersArchive();
      dbSetup.removeSpGoals();
      dbSetup.createGoals();
      dbSetup.removeAll("personalityPracticeArea");
      dbSetup.createPersonalityPracticeAreas();
      dbSetup.removeArticles();
      dbSetup.createArticles();

      final String email = "dax@surepeople.com";
      String companyId = "1";

      final User user = dbSetup.getUser();
      // adding a hiring candidate 
      // and test to see if the assessment is copied over
      // for the hiring candidate
      HiringUser hiringUser = new HiringUser();
      hiringUser.setEmail(email);
      hiringUser.setCompanyId(companyId);
      hiringUser.setType(UserType.HiringCandidate);
      hiringUser.addRole(RoleType.HiringCandidate);
      Set<String> hiringCoordinatorIds = new HashSet<String>();
      hiringCoordinatorIds.add(user.getId());
      hiringUser.setHiringCoordinatorIds(hiringCoordinatorIds);
      dbSetup.addUpdate(hiringUser);
      
      authenticationHelper.doAuthenticateWithoutPassword(session, hiringUser);

      assertThat(hiringUser.getAnalysis(), is(nullValue()));
      
      processRandomAssessment();
      
      Thread.sleep(3000);
      
      hiringUser = dbSetup.getHiringCandidate(email, companyId);
      assertThat(hiringUser.getAnalysis(), is(not(nullValue())));
      assertThat(hiringUser.getUserStatus(), is(UserStatus.VALID));
      
      Thread.sleep(5000);
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }  
  
//  @Test
//  public void testAssessmentCalculations() throws Exception {
//    
//    try {
//      User user = dbSetup.getUser("admin@admin.com");
//      AssessmentProgressTracker assessmentProgressStore = new AssessmentProgressTracker();
//      assessmentProgressStore.setUserId(user.getId());
//      
//      final HashMap<CategoryType, HashMap<TraitType, ScoreBean>> score = 
//                      new HashMap<CategoryType, HashMap<TraitType, ScoreBean>>();
//      HashMap<TraitType, ScoreBean> motivationScores = new HashMap<TraitType, ScoreBean>();
//      ScoreBean consistencyScore = new ScoreBean();
//      consistencyScore.setScore(5d);
//      motivationScores.put(TraitType.Consistency, consistencyScore);
//      score.put(CategoryType.Motivation, motivationScores);
//      assessmentProgressStore.setScore(score);
//      dbSetup.addUpdate(assessmentProgressStore);
//      
//      AssessmentProcessor processor = new AssessmentProcessor(user, questionsFactory);
//      final AnalysisBean analysis = new AnalysisBean();
//      processor.setAnalysis(analysis);
//      processor.analyzeMotivationHow();
//      
//      Map<TraitType, BigDecimal> motivationHow = analysis.getMotivationHow();
//      System.out.println(motivationHow.get(TraitType.Consistency).doubleValue() + ":"
//          + motivationHow.get(TraitType.Freedom).doubleValue());
//      
//    } catch (Exception e) {
//      e.printStackTrace();
//      fail();
//    }
//  }
  

  @Test
  public void testGetQuestionStats() throws Exception {
    HashMap<TraitType, ScoreBean> scoreMap = new HashMap<TraitType, ScoreBean>();
    Map<Integer, TraitsBean> traitsMap = questionsFactory.getTraitsMap();
    for (Entry<Integer, TraitsBean> trait : traitsMap.entrySet()) {
      TraitsBean traitsBean = trait.getValue();
      Collection<List<TraitsTransform>> traitsTransforms = traitsBean.getTraitsTransformMap().values();
      for (List<TraitsTransform> optionTraits : traitsTransforms) {
        for (TraitsTransform traitsTransform : optionTraits) {
          TraitType tempTrait = traitsTransform.getTrait();
          ScoreBean scoreBean = scoreMap.get(tempTrait);
          if (scoreBean == null) {
            scoreBean = new ScoreBean();
            scoreMap.put(tempTrait, scoreBean);
          }
          scoreBean.increment(traitsTransform.getIncrementAmount());
        }
      }
    }
    
    for (TraitType traitType : scoreMap.keySet()) {
      log.debug(traitType + ":" + scoreMap.get(traitType).getScore());
    }
  }

  @Test
  public void testDefaultAssessment() throws Exception {
    
    dbSetup.removeAll("assessmentProgressTracker");
    dbSetup.removeAll("prismAssessment");
    
    MvcResult result = null;
    try {
      User user = dbSetup.getUser();
      user.setAnalysis(null);
      user.setUserStatus(UserStatus.ASSESSMENT_PENDING);
      dbSetup.addUpdate(user);
      
      authenticationHelper.doAuthenticateWithoutPassword(session, user);
      
      Map<Integer, TraitsBean> traitsMap = questionsFactory.getTraitsMap();
      result = this.mockMvc
          .perform(
              post("/assessment/start").contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())        
          .andExpect(content().contentType("application/json;charset=UTF-8")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      QuestionsBean questionsBean = getQuestionsBean(result);
      
      while (questionsBean != null) {
        log.debug("Processing Question Number :" + questionsBean.getNumber());
        questionsBean = processRandomAssessment(questionsBean, traitsMap);
      }
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }
  
  @Test
  public void testStartAndStopAssessment() throws Exception {
    
    dbSetup.removeAll("assessmentProgressTracker");
    dbSetup.removeAll("prismAssessment");
    
    MvcResult result = null;
    try {
      User user = dbSetup.getUser();
      user.setAnalysis(null);
      user.setUserStatus(UserStatus.ASSESSMENT_PENDING);
      dbSetup.addUpdate(user);
      
      authenticationHelper.doAuthenticateWithoutPassword(session, user);
      
      Map<Integer, TraitsBean> traitsMap = questionsFactory.getTraitsMap();
      result = this.mockMvc
          .perform(post("/assessment/start").contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
          .andExpect(content().contentType("application/json;charset=UTF-8")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      QuestionsBean questionsBean = getQuestionsBean(result);
      
      for (int i = 0; i < 3; i++) {
        log.debug("Processing Question Number :" + questionsBean.getNumber());
        questionsBean = processRandomAssessment(questionsBean, traitsMap);
      }
      
      result = this.mockMvc
          .perform(post("/assessment/start").contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success.answeredCount").value(3))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }

  @Test
  public void testPreviousAssessment() throws Exception {
    
    dbSetup.removeAll("assessmentProgressTracker");
    dbSetup.removeAll("prismAssessment");
    
    MvcResult result = null;
    try {
      User user = dbSetup.getUser();
      user.setAnalysis(null);
      user.setUserStatus(UserStatus.ASSESSMENT_PENDING);
      dbSetup.addUpdate(user);
      
      authenticationHelper.doAuthenticateWithoutPassword(session, user);
      
      Map<Integer, TraitsBean> traitsMap = questionsFactory.getTraitsMap();
      result = this.mockMvc
          .perform(post("/assessment/start").contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
          .andExpect(content().contentType("application/json;charset=UTF-8")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      QuestionsBean questionsBean = getQuestionsBean(result);

      result = this.mockMvc
          .perform(post("/assessment/previous").contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.error").exists())
          .andExpect(jsonPath("$.error.IllegalArgumentException").value("No questions answered yet."))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      for (int i = 0; i < 3; i++) {
        log.debug("Processing Question Number :" + questionsBean.getNumber());
        questionsBean = processRandomAssessment(questionsBean, traitsMap);
      }
      
      result = this.mockMvc
          .perform(post("/assessment/previous").contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
      List<PrismAssessment> all = dbSetup.getAll(PrismAssessment.class);
      PrismAssessment prismAssessment = all.get(0);
      assertThat(prismAssessment.getQuestionsAnsweredCount(), equalTo(2));
      assertThat(prismAssessment.getQuestionIndex(), equalTo(2));
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }

  @Test
  public void testAssessmentSessionTimeout() throws Exception {
    
    dbSetup.removeAll("assessmentProgressTracker");
    dbSetup.removeAll("prismAssessment");
    
    MvcResult result = null;
    try {
      User user = dbSetup.getUser();
      user.setAnalysis(null);
      user.setUserStatus(UserStatus.ASSESSMENT_PENDING);
      dbSetup.addUpdate(user);
      
      authenticationHelper.doAuthenticateWithoutPassword(session, user);
      
      //Map<Integer, TraitsBean> traitsMap = questionsFactory.getTraitsMap();
      result = this.mockMvc
          .perform(post("/assessment/start").contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
          .andExpect(content().contentType("application/json;charset=UTF-8")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      //QuestionsBean questionsBean = getQuestionsBean(result);
      

      result = this.mockMvc
          .perform(post("/assessment/process")
              .param("questionNum", "161")
              .param("ans", "1, 2")
              .contentType(MediaType.TEXT_PLAIN).session(session2))
          .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
          .andExpect(content().contentType("application/json;charset=UTF-8")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }
  
  
  @Test
  public void testMotivationAssessment() throws Exception {
    
    dbSetup.removeAll("assessmentProgressTracker");
    dbSetup.removeAll("prismAssessment");
    
    final String[] motivationHow = { "58ky734l,tldj04zk,z5xb7180,0p2407w3",
        "rkls7dt7,y53lg93q,r2fzg664,kgx579xp", "p1m7pg1v,jg3fgkrc,hyympc49,bcftggwt",
        "7vg8wj3f,27ygnmty,8bm2nqqh,fy4wwmzz", "5tnj3sy3,t4g3wss2,zqzx3p1k,074rwwnm",
        "qmjkbv0p,r3nd31mr,k00s3yq6,xq55byw7", "plpvj3vc,hh16k0yt,bxjf94nb,j05196kw",
        "8wqpjch0,2s12j9mg,7dkwr5wy,fh7hr8sh" };
    
    MvcResult result = null;
    try {
      User user = dbSetup.getUser();
      user.setAnalysis(null);
      user.setUserStatus(UserStatus.ASSESSMENT_PENDING);
      dbSetup.addUpdate(user);
      
      authenticationHelper.doAuthenticateWithoutPassword(session, user);
      
      Map<Integer, TraitsBean> traitsMap = questionsFactory.getTraitsMap();
      result = this.mockMvc
          .perform(
              post("/assessment/start")
              .param("type", AssessmentType.Motivation + "")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())        
          .andExpect(content().contentType("application/json;charset=UTF-8")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      QuestionsBean questionsBean = getQuestionsBean(result);
      
      int index = 0;
      while (questionsBean != null) {
        log.debug("Processing Question Number :" + questionsBean.getNumber());
        questionsBean = processAssessment(questionsBean, traitsMap);
//        if (questionsBean.getCategoryNumber() == 5) {
//          questionsBean = processAssessment(questionsBean, motivationHow[index++]);
//        } else {
//          questionsBean = processAssessment(questionsBean, traitsMap);
//        }
      }
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }  

  @Test
  public void testFundamentalNeedsAssessment() throws Exception {
    
    dbSetup.removeAll("assessmentProgressTracker");
    dbSetup.removeAll("prismAssessment");
    
    final String[] ans = { "9t8r21t2,njg590z3,gfsk9x3j", "vm4s93wn,1qsf96r6,6996j21p",
        "lhngj8ts,7tg198nr,y6tvr7zt", "zpzpjglw,49hhrbwc,bd53rfrx", "qlhbqmk0,w504yhth,jhvqrjng",
        "9cccypll,gzw55lw2,n2ks5nsm", "18w05vkq,v56d5rn5,6vdtdrv6", "l1q1dxm9,yrxgmxsb,7ckm5yf8",
        "4vl2m0nw,by8pl2kf,z728c3dd", "j1x9l5gz,wq3qt5m0,q3lxl8bj" };
    
    MvcResult result = null;
    try {
      User user = dbSetup.getUser();
      user.setAnalysis(null);
      user.setUserStatus(UserStatus.ASSESSMENT_PENDING);
      user.getProfileSettings().setLocale(new Locale("es", "LA"));
      dbSetup.addUpdate(user);
      
      authenticationHelper.doAuthenticateWithoutPassword(session, user);
      
      //Map<Integer, TraitsBean> traitsMap = questionsFactory.getTraitsMap();
      result = this.mockMvc
          .perform(
              post("/assessment/start")
              .param("type", AssessmentType.FundamentalNeeds + "")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())        
          .andExpect(content().contentType("application/json;charset=UTF-8")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      QuestionsBean questionsBean = getQuestionsBean(result);
      
      int index = 0;
      while (questionsBean != null) {
        log.debug("Processing Question Number :" + questionsBean.getNumber());
//        questionsBean = processRandomAssessment(questionsBean, traitsMap);
//        questionsBean = processAssessment(questionsBean, traitsMap);
        questionsBean = processAssessment(questionsBean, ans[index++]);
      }
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }
  
  @Test
  public void testProcessingDecisionMakingAssessment() throws Exception {
    
    dbSetup.removeAll("assessmentProgressTracker");
    dbSetup.removeAll("prismAssessment");
    
    MvcResult result = null;
    try {
      User user = dbSetup.getUser();
      user.setAnalysis(null);
      user.setUserStatus(UserStatus.ASSESSMENT_PENDING);
      dbSetup.addUpdate(user);
      
      authenticationHelper.doAuthenticateWithoutPassword(session, user);
      
      Map<Integer, TraitsBean> traitsMap = questionsFactory.getTraitsMap();
      result = this.mockMvc
          .perform(
              post("/assessment/start")
              .param("type", AssessmentType.PDM + "")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())        
          .andExpect(content().contentType("application/json;charset=UTF-8")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      QuestionsBean questionsBean = getQuestionsBean(result);
      
      while (questionsBean != null) {
        log.debug("Processing Question Number :" + questionsBean.getNumber());
        questionsBean = processRandomAssessment(questionsBean, traitsMap);
      }
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }
  
  @Test
  public void testLearningStyleAssessment() throws Exception {
    
    dbSetup.removeAll("assessmentProgressTracker");
    dbSetup.removeAll("prismAssessment");
    
    MvcResult result = null;
    try {
      User user = dbSetup.getUser();
      user.setAnalysis(null);
      user.setUserStatus(UserStatus.ASSESSMENT_PENDING);
      dbSetup.addUpdate(user);
      
      authenticationHelper.doAuthenticateWithoutPassword(session, user);
      
      Map<Integer, TraitsBean> traitsMap = questionsFactory.getTraitsMap();
      result = this.mockMvc
          .perform(
              post("/assessment/start")
              .param("type", AssessmentType.LearningStyle + "")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())        
          .andExpect(content().contentType("application/json;charset=UTF-8")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      QuestionsBean questionsBean = getQuestionsBean(result);
      
      while (questionsBean != null) {
        log.debug("Processing Question Number :" + questionsBean.getNumber());
        questionsBean = processRandomAssessment(questionsBean, traitsMap);
      }
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }
  
  @Test
  public void testConflictManagementAssessment() throws Exception {
    
    dbSetup.removeAll("assessmentProgressTracker");
    dbSetup.removeAll("prismAssessment");
    
    MvcResult result = null;
    try {
      User user = dbSetup.getUser();
      user.setAnalysis(null);
      user.setUserStatus(UserStatus.ASSESSMENT_PENDING);
      dbSetup.addUpdate(user);
      
      authenticationHelper.doAuthenticateWithoutPassword(session, user);
      
      Map<Integer, TraitsBean> traitsMap = questionsFactory.getTraitsMap();
      result = this.mockMvc
          .perform(
              post("/assessment/start")
              .param("type", AssessmentType.ConflictManagement + "")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())        
          .andExpect(content().contentType("application/json;charset=UTF-8")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      QuestionsBean questionsBean = getQuestionsBean(result);
      
      while (questionsBean != null) {
        log.debug("Processing Question Number :" + questionsBean.getNumber());
        questionsBean = processRandomAssessment(questionsBean, traitsMap);
      }
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }  

  @Test
  public void testAllAssessment() throws Exception {
    
    dbSetup.removeAll("assessmentProgressTracker");
    dbSetup.removeAll("prismAssessment");
    
    MvcResult result = null;
    try {
      User user = dbSetup.getUser();
      user.setAnalysis(null);
      user.setUserStatus(UserStatus.ASSESSMENT_PENDING);
      user.getProfileSettings().setLocale(Locale.US);
      user.setGender(Gender.M);
      dbSetup.addUpdate(user);
      
      authenticationHelper.doAuthenticateWithoutPassword(session, user);
      
      Map<Integer, TraitsBean> traitsMap = questionsFactory.getTraitsMap();
      result = this.mockMvc
          .perform(
              post("/assessment/start")
              .param("type", AssessmentType.All + "")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())        
          .andExpect(content().contentType("application/json;charset=UTF-8")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      QuestionsBean questionsBean = getQuestionsBean(result);
      
      while (questionsBean != null) {
        log.debug("Processing Question Number :" + questionsBean.getNumber());
        questionsBean = processRandomAssessment(questionsBean, traitsMap);
      }
      
      result = this.mockMvc
          .perform(
              post("/signedIn/getAnalysisFull").contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(jsonPath("$.success.Success").value("true")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  } 
  
  @Test
  public void testAllAssessmentHiringCandidate() throws Exception {
    
    dbSetup.removeAll("assessmentProgressTracker");
    dbSetup.removeAll("prismAssessment");
    dbSetup.removeAll("hiringUser");
    
    MvcResult result = null;
    try {
      
      final String email = "dax@surepeople.com";
      String companyId = "1";

      CompanyDao company = companyFactory.getCompany(companyId);
      company.setSharePortrait(true);
      companyFactory.updateCompany(company);
      
      final User user = dbSetup.getUser();
      // adding a hiring candidate 
      // and test to see if the assessment is copied over
      // for the hiring candidate
      HiringUser hiringUser = new HiringUser();
      hiringUser.setEmail(email);
      hiringUser.setCompanyId(companyId);
      hiringUser.setType(UserType.HiringCandidate);
      hiringUser.addRole(RoleType.HiringCandidate);
      Set<String> hiringCoordinatorIds = new HashSet<String>();
      hiringCoordinatorIds.add(user.getId());
      hiringUser.setHiringCoordinatorIds(hiringCoordinatorIds);
      dbSetup.addUpdate(hiringUser);
      
      authenticationHelper.doAuthenticateWithoutPassword(session, hiringUser);
      assertThat(hiringUser.getAnalysis(), is(nullValue()));
      
      Map<Integer, TraitsBean> traitsMap = questionsFactory.getTraitsMap();
      result = this.mockMvc
          .perform(
              post("/assessment/start")
              .param("type", AssessmentType.All + "")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())        
          .andExpect(content().contentType("application/json;charset=UTF-8")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      QuestionsBean questionsBean = getQuestionsBean(result);
      
      while (questionsBean != null) {
        log.debug("Processing Question Number :" + questionsBean.getNumber());
        questionsBean = processRandomAssessment(questionsBean, traitsMap);
      }
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }
  
  @Test
  public void testAllAssessmentHiringEmployee() throws Exception {
    
    dbSetup.removeAll("assessmentProgressTracker");
    dbSetup.removeAll("prismAssessment");
    dbSetup.removeAll("hiringUser");
    
    MvcResult result = null;
    try {
      
      final String email = "dax@surepeople.com";
      String companyId = "1";

      CompanyDao company = companyFactory.getCompany(companyId);
      company.setSharePortrait(true);
      companyFactory.updateCompany(company);
      
      final User user = dbSetup.getUser();
      // adding a hiring candidate 
      // and test to see if the assessment is copied over
      // for the hiring candidate
      HiringUser hiringUser = new HiringUser();
      hiringUser.setEmail(email);
      hiringUser.setCompanyId(companyId);
      hiringUser.setType(UserType.Member);
      hiringUser.addRole(RoleType.HiringEmployee);
      Set<String> hiringCoordinatorIds = new HashSet<String>();
      hiringCoordinatorIds.add(user.getId());
      hiringUser.setHiringCoordinatorIds(hiringCoordinatorIds);
      dbSetup.addUpdate(hiringUser);
      
      authenticationHelper.doAuthenticateWithoutPassword(session, hiringUser);
      assertThat(hiringUser.getAnalysis(), is(nullValue()));
      
      Map<Integer, TraitsBean> traitsMap = questionsFactory.getTraitsMap();
      result = this.mockMvc
          .perform(
              post("/assessment/start")
              .param("type", AssessmentType.All + "")
              .contentType(MediaType.TEXT_PLAIN).session(session))
          .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())        
          .andExpect(content().contentType("application/json;charset=UTF-8")).andReturn();
      log.debug("The MVC Response : " + result.getResponse().getContentAsString());
      QuestionsBean questionsBean = getQuestionsBean(result);
      
      while (questionsBean != null) {
        log.debug("Processing Question Number :" + questionsBean.getNumber());
        questionsBean = processRandomAssessment(questionsBean, traitsMap);
      }
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }  
  
  private QuestionsBean getQuestionsBean(MvcResult result) throws JSONException,
      UnsupportedEncodingException, IOException, JsonParseException, JsonMappingException {
    JSONObject jsonObject = new JSONObject(result.getResponse().getContentAsString());
    jsonObject = jsonObject.getJSONObject("success");
    return om.readValue(jsonObject.get("question").toString(), QuestionsBean.class);
  }

  private QuestionsBean processAssessment(QuestionsBean questionsBean, String ans) throws Exception {
    final int qNo = questionsBean.getNumber();
    log.debug("QNo:"
        + qNo
        + ":{"
        + StringUtils.join(
            questionsBean.getOptionsList().stream().map(Object::toString)
                .collect(Collectors.toList()), ',') + "}, Selection :[" + ans + "]");
    
    MvcResult result = this.mockMvc
        .perform(
            post("/assessment/process")
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

  private QuestionsBean processAssessment(QuestionsBean questionsBean,
      Map<Integer, TraitsBean> traitsMap) throws Exception {
    final int qNo = questionsBean.getNumber();
    String ans = null;
    switch (questionsBean.getType()) {
    case MultipleChoice:
      ans = questionsBean.getOptionsList().get(0).getId();
      break;
    case Rating:
      ans = StringUtils.join(getAnswer(questionsBean.getOptionsList(), 4), ',');
      break;
    case PersonalityRating:
      ans = StringUtils.join(getAnswer(questionsBean.getOptionsList(), 2), ',');
      break;
    case VariableRating:
      ans = StringUtils.join(getAnswer(questionsBean.getOptionsList(), 3), ',');
      break;
    default:
      throw new RuntimeException("Do not know how to process :" + questionsBean.getType());
    }
    
    log.debug("QNo:"
        + qNo
        + ":{"
        + StringUtils.join(
            questionsBean.getOptionsList().stream().map(Object::toString)
                .collect(Collectors.toList()), ',') + "}, Selection :[" + ans + "]");
    
    MvcResult result = this.mockMvc
        .perform(
            post("/assessment/process")
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

  private List<String> getAnswer(List<OptionsBean> optionsList, int size) {
    return optionsList.stream().limit(size).map(OptionsBean::getId).collect(Collectors.toList());
  }

  private QuestionsBean processRandomAssessment(QuestionsBean questionsBean,
      Map<Integer, TraitsBean> traitsMap) throws Exception {
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
            post("/assessment/process")
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

//  @Test
//  public void testCompleteAssessment() throws Exception {
//    try {
//      int[][][] assessment = {
//          { { 0 }, { 0 }, { 0 }, { 0 }, { 0 }, { 0 }, { 0 }, { 1 }, { 0 }, { 0 }, { 0 }, { 0 },
//              { 0 }, { 0 }, { 0 }, { 1 }, { 0 }, { 0 }, { 0 }, { 0 }, { 0 }, { 1 }, { 0 }, { 1 },
//              { 0 }, { 0 }, { 0 }, { 0 }, { 0 }, { 0 }, { 0 }, { 0 }, { 1 }, { 0 }, { 0 }, { 0 },
//              { 0 }, { 0 }, { 0 }, { 0 } },
//          { { 1 }, { 1 }, { 1 }, { 0 }, { 1 }, { 0 }, { 0 }, { 1 }, { 1 }, { 1 }, { 1 }, { 0 },
//              { 1 }, { 0 }, { 1 }, { 0 }, { 1 }, { 1 }, { 1 }, { 0 }, { 1 }, { 0 }, { 0 }, { 1 },
//              { 1 }, { 0 }, { 0 }, { 1 }, { 0 }, { 1 }, { 1 }, { 1 }, { 1 }, { 1 }, { 1 }, { 1 },
//              { 0 } },
//          { { 1 }, { 0 }, { 0 }, { 0 }, { 0 }, { 0 }, { 0 }, { 0 }, { 1 }, { 0 }, { 0 }, { 1 },
//              { 0 }, { 1 }, { 0 } },
//          { { 1 }, { 0 }, { 2 }, { 0 }, { 0 }, { 3 }, { 3 }, { 0 }, { 2 }, { 0 }, { 0 }, { 0 },
//              { 0 }, { 0 }, { 0 } },
//          { { 3, 1, 2, 0 }, { 2, 1, 3, 0 }, { 3, 0, 1, 2 }, { 1, 3, 2, 0 }, { 3, 1, 2, 0 },
//              { 0, 1, 2, 3 }, { 3, 0, 2, 1 }, { 3, 2, 1, 0 } },
//          { { 3, 0, 2, 1 }, { 1, 3, 2, 0 }, { 0, 3, 1, 2 }, { 2, 3, 0, 1 }, { 1, 0, 3, 2 },
//              { 3, 0, 2, 1 } },
//          { { 1, 2 }, { 2, 1 }, { 2, 3 }, { 1, 3 }, { 3, 2 }, { 3, 2 }, { 1, 2 }, { 0, 1 }, { 2, 1 },
//              { 2, 0 }, { 0, 3 }, { 3, 0 }, { 0, 1 }, { 2, 0 }, { 0, 3 }, { 3, 2 }, { 0, 3 },
//              { 2, 1 }, { 2, 0 }, { 1, 2 }, { 2, 3 }, { 0, 2 }, { 2, 0 }, { 1, 3 }, { 1, 0 },
//              { 0, 3 }, { 1, 2 }, { 3, 2 } },
//          { { 2, 0, 1 }, { 1, 2, 0 }, { 0, 1, 2 }, { 2, 0, 1 }, { 1, 2, 0 }, { 0, 1, 2 },
//              { 2, 0, 1 }, { 1, 2, 0 }, { 0, 2, 1 }, { 2, 0, 1 } },
//          { { 1 }, { 0 }, { 1 }, { 0 }, { 0 }, { 0 }, { 0 }, { 0 }, { 1 }, { 0 }, { 0 } } };
//      User user = dbSetup.getUser();
//      
//      AssessmentProcessor processor = ApplicationContextUtils.getBean(AssessmentProcessor.class,
//          user, assessmentquestionsFactory.getQuestionFactory("en_US",
//              QuestionFactoryType.SPAssessmentQuestion));
//      AssessmentProgressStore store = processor.getStore();
//      store.setAssessment(assessment);
//      AnalysisBean processFinalResult = processor.processFinalResult();
//      log.debug(processFinalResult);
//    } catch (Exception e) {
//      e.printStackTrace();
//      fail();
//    }
//  }
  
  
  /**
   * Process.
   * 
   * @throws Exception
   *           - exception
   */
  public void processRandomAssessment() throws Exception {
    MvcResult result = null;
    JSONObject jsonObject = null;
    QuestionsBean response = null;
    String respString = null;

    Map<Integer, TraitsBean> traitsArray = questionsFactory.getTraitsMap();
    // going to complete the whole assessment
    for (Entry<Integer, TraitsBean> entry : traitsArray.entrySet()) {
      TraitsBean trait = entry.getValue();
      StringBuffer answerBuffer = new StringBuffer();
      List<String> answerRandomizer = null;
      final Set<String> traitKeys = trait.getTraitsTransformMap().keySet();
      switch (trait.getType()) {
      case MultipleChoice:
        answerRandomizer = randomizeAnswer(traitKeys);
        answerBuffer.append(answerRandomizer.get(0));
        break;
      case Rating:
        answerBuffer.append(StringUtils.join(randomizeAnswer(traitKeys, trait.getFactor().length), ','));
        break;
      case PersonalityRating:
        answerBuffer.append(StringUtils.join(randomizeAnswer(traitKeys, trait.getFactor().length), ','));
        break;
      default:
        throw new RuntimeException("Do not know how to process :" + trait.getType());
      }
      result = processQuestion(entry.getKey(), answerBuffer.toString());
      respString = result.getResponse().getContentAsString();
      if (respString.contains("question")) {
        jsonObject = new JSONObject(respString);
        jsonObject = jsonObject.getJSONObject("success");
        response = om.readValue(jsonObject.get("question").toString(), QuestionsBean.class);
        assertNotNull("The response bean is not null !!", response);
      } else {
        log.debug("Processing done !!!");
      }
    }
  }

  private QuestionsBean processAndGetQuestionBean(int questionNumber)
      throws Exception {
    return processAndGetQuestionBean(questionNumber, "0");
  }

  private QuestionsBean processAndGetQuestionBean(int questionNumber,
      String answer) throws Exception {
    MvcResult result = processQuestion(questionNumber, answer);
    return getQuestionsBean(result);
  }

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
  

  private MvcResult processQuestion(int questionNumber, String answer) {
    MvcResult result = null;
    try {
      result = this.mockMvc
          .perform(
              post("/assessment/process").contentType(MediaType.TEXT_PLAIN).session(session)
                  .param("q", questionNumber + "").param("a", answer))
          .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())        
          .andExpect(content().contentType("application/json;charset=UTF-8")).andReturn();
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
    return result;
  }

}
