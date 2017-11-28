package com.sp.web.controller.spectrum;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.sp.web.account.AccountRepository;
import com.sp.web.controller.competency.CompetencyFactory;
import com.sp.web.dao.CompanyDao;
import com.sp.web.model.FeedbackUser;
import com.sp.web.model.SPRating;
import com.sp.web.model.SPRatingScore;
import com.sp.web.model.User;
import com.sp.web.model.competency.BaseCompetencyEvaluationScore;
import com.sp.web.model.competency.CompetencyEvaluation;
import com.sp.web.model.competency.CompetencyProfile;
import com.sp.web.model.competency.EvaluationType;
import com.sp.web.model.competency.RatingConfiguration;
import com.sp.web.model.competency.RatingConfigurationType;
import com.sp.web.model.competency.UserCompetency;
import com.sp.web.model.competency.UserCompetencyEvaluation;
import com.sp.web.model.competency.UserCompetencyEvaluationDetails;
import com.sp.web.model.competency.UserCompetencyEvaluationScore;
import com.sp.web.model.goal.DSAction;
import com.sp.web.model.goal.DSActionCategory;
import com.sp.web.model.goal.GoalStatus;
import com.sp.web.model.goal.SPGoal;
import com.sp.web.mvc.test.setup.SPTestLoggedInBase;
import com.sp.web.product.CompanyFactory;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author pradeepruhil
 *
 *         The test cases for spectrum controller.
 */
public class SpectrumControllerTest extends SPTestLoggedInBase {
  
  private static final Random rand = new Random();

  @Autowired
  CompanyFactory companyFactory;
  
  @Autowired
  CompetencyFactory competencyFactory;
  
  @Autowired
  AccountRepository accountRepository;
  
  /**
   * Setup.
   */
  @Before
  public void setUp() throws Exception {
    dbSetup.removeAllUsers();
    dbSetup.removeAllCompanies();
    dbSetup.removeAllGroups();
    dbSetup.removeAll("profileBalance");
    
    dbSetup.createUsers();
    dbSetup.createCompanies();
    dbSetup.addGroups();
    User user = dbSetup.getUser("pradeep1@surepeople.com");
    authenticationHelper.doAuthenticateWithoutPassword(session2, user);
  }
  
  @Test
  public void getProfileBalanceTest() throws Exception {
    
    MvcResult result = mockMvc
        .perform(
            MockMvcRequestBuilders.post("/spectrum/getProfileBalance")
                .contentType(MediaType.TEXT_PLAIN).session(session2))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
        .andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true"))
        .andExpect(jsonPath("$.success.profileBalance.companyId").value("1")).andReturn();
    log.debug("The MVC Response : " + result.getResponse().getContentAsString());  
  }
  
  @Test
  public void getBlueprintAnalytics() throws Exception {
    dbSetup.removeSpGoals();
    dbSetup.createBluePrint();
    MvcResult result = mockMvc
        .perform(
            MockMvcRequestBuilders.post("/spectrum/getBlueprintAnalytics")
                .contentType(MediaType.TEXT_PLAIN).session(session2))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
        .andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true"))
        .andExpect(jsonPath("$.success.bluePrintAnalytics.companyId").value("1")).andReturn();
    log.debug("The MVC Response : " + result.getResponse().getContentAsString());
  }
  
  @Test
  public void getHiringCandidateProfileBalanceTest() throws Exception {
    
    MvcResult result = mockMvc
        .perform(
            MockMvcRequestBuilders.post("/spectrum/getHiringCandidateProfileBalance")
                .contentType(MediaType.TEXT_PLAIN).session(session2))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
        .andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true"))
        .andExpect(jsonPath("$.success.profileBalance.companyId").value("1")).andReturn();
    log.debug("The MVC Response : " + result.getResponse().getContentAsString());  
  }

  @Test
  public void getHiringInsights() throws Exception {
    dbSetup.removeAll("hiringFilterInsights");
    dbSetup.removeAllHiringUsers();
    dbSetup.removeAllHiringArchiveUsers();
    dbSetup.createHiringUsers();
    dbSetup.createHiringArchiveUsers();
    
    MvcResult hiringResults = mockMvc
        .perform(
            MockMvcRequestBuilders.post("/spectrum/getHiringInsights")
                .contentType(MediaType.TEXT_PLAIN).session(session2))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
        .andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true"))
        .andReturn();
    log.debug("The MVC Response : " + hiringResults.getResponse().getContentAsString()); 
    
    MvcResult hiringResultsRole = mockMvc
        .perform(
            MockMvcRequestBuilders.post("/spectrum/getHiringInsights")
                .contentType(MediaType.TEXT_PLAIN).session(session2).param("role", "Actor"))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
        .andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true"))
        .andReturn();
    log.debug("The MVC Response : " + hiringResultsRole.getResponse().getContentAsString());    
  }
  
  @Test
  public void getErtiInsights() throws Exception {
    MvcResult result = mockMvc
        .perform(
            MockMvcRequestBuilders.post("/spectrum/getErtiInsights")
                .contentType(MediaType.TEXT_PLAIN).session(session2))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
        .andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true"))
        .andExpect(jsonPath("$.success.ertiInsights.companyId").value("1")).andReturn();
    log.debug("The MVC Response : " + result.getResponse().getContentAsString());   
  }
  
  @Test
  public void testLearnerStatus() throws Exception {
    MvcResult learnerStatus = mockMvc
        .perform(
            MockMvcRequestBuilders.post("/spectrum/getLearnerStatus")
                .contentType(MediaType.TEXT_PLAIN).session(session2))
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
        .andExpect(jsonPath("$.success").exists())
        .andExpect(jsonPath("$.success.Success").value("true"))
        .andReturn();
    log.debug("The MVC Response : " + learnerStatus.getResponse().getContentAsString());     
  }

  @Test
  public void testGetCompetencyInsights() throws Exception {
    try {
     
      dbSetup.removeSpGoals();
      dbSetup.removeAll("competencyProfile");
      dbSetup.removeAll("competencyEvaluation");
      dbSetup.removeAll("competencyEvaluationRequest");
      dbSetup.removeAllFeedbackUsers();
      dbSetup.removeAll("userTodoRequests");
      dbSetup.removeAll("spectrumCompetencyProfileEvaluationResults");
      dbSetup.removeAll("userCompetencyEvaluationDetails");
      
      MvcResult results = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/spectrum/getCompetencyInsights")
                  .contentType(MediaType.TEXT_PLAIN).session(session2))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.competencyProfileListing", hasSize(0)))
          .andExpect(jsonPath("$.success.competencyProfileDetails").isEmpty())
          .andReturn();
      log.debug("The MVC Response : " + results.getResponse().getContentAsString());
      
      User user = dbSetup.getUser();
      String companyId = user.getCompanyId();
      CompetencyProfile competencyProfile = createCompetencyProfile(companyId);
      final String competencyProfileId = competencyProfile.getId();
      user.setCompetencyProfileId(competencyProfileId);
      dbSetup.addUpdate(user);
      CompetencyEvaluation competencyEvaluation = createCompetencyEvaluation(companyId);
      
      results = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/spectrum/getCompetencyInsights")
                  .contentType(MediaType.TEXT_PLAIN).session(session2))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.competencyProfileListing", hasSize(0)))
          .andExpect(jsonPath("$.success.competencyProfileDetails").isEmpty())
          .andReturn();
      log.debug("The MVC Response : " + results.getResponse().getContentAsString());
      
      competencyFactory.endEvaluation(competencyEvaluation);
      
      results = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/spectrum/getCompetencyInsights")
                  .contentType(MediaType.TEXT_PLAIN).session(session2))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.competencyProfileListing", hasSize(1)))
          .andExpect(jsonPath("$.success.competencyProfileDetails").exists())
          .andReturn();
      log.debug("The MVC Response : " + results.getResponse().getContentAsString());
      
      User user2 = dbSetup.getUser("dax@surepeople.com");
      user2.setCompetencyProfileId(competencyProfileId);
      dbSetup.addUpdate(user2);
      competencyEvaluation = createCompetencyEvaluation(companyId);
      List<EvaluationType> requiredEvaluationList = competencyEvaluation.getRequiredEvaluationList();
      requiredEvaluationList.add(EvaluationType.Self);
      requiredEvaluationList.add(EvaluationType.Peer);
      competencyFactory.update(competencyEvaluation);
      addScores(user, competencyProfile, competencyEvaluation);
      addScores(user2, competencyProfile, competencyEvaluation);
      competencyFactory.endEvaluation(competencyEvaluation);

      results = mockMvc
          .perform(
              MockMvcRequestBuilders.post("/spectrum/getCompetencyInsights")
                  .contentType(MediaType.TEXT_PLAIN).session(session2))
          .andExpect(content().contentType("application/json;charset=UTF-8"))
          .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
          .andExpect(jsonPath("$.success").exists())
          .andExpect(jsonPath("$.success.Success").value("true"))
          .andExpect(jsonPath("$.success.competencyProfileListing", hasSize(1)))
          .andExpect(jsonPath("$.success.competencyProfileDetails").exists())
          .andReturn();
      log.debug("The MVC Response : " + results.getResponse().getContentAsString());
      
    } catch (Exception e) {
      e.printStackTrace();
      fail();
    }
  }

  private void addScores(User user, CompetencyProfile competencyProfile,
      CompetencyEvaluation competencyEvaluation) {
    UserCompetency userCompetencyEvaluation = competencyFactory.getUserCompetency(user.getId());
    UserCompetencyEvaluation evaluation = userCompetencyEvaluation.getEvaluation(competencyEvaluation.getId());
    evaluation.setManager((UserCompetencyEvaluationScore) addRandomEvaluation(
        new UserCompetencyEvaluationScore(getRandomFeedbackUser()), competencyProfile));
    evaluation
        .setSelf(addRandomEvaluation(new BaseCompetencyEvaluationScore(), competencyProfile));
    evaluation.addPeer((UserCompetencyEvaluationScore) addRandomEvaluation(
        new UserCompetencyEvaluationScore(getRandomFeedbackUser()), competencyProfile));
    evaluation.addPeer((UserCompetencyEvaluationScore) addRandomEvaluation(
        new UserCompetencyEvaluationScore(getRandomFeedbackUser()), competencyProfile));
    competencyFactory.update(userCompetencyEvaluation);
  }

  private BaseCompetencyEvaluationScore addRandomEvaluation(
      BaseCompetencyEvaluationScore score,
      CompetencyProfile competencyProfile) {
    List<String> competencyIdList = competencyProfile.getCompetencyIdList();
    double[] scoreArray = new double[competencyIdList.size()];
    double totalScore = 0d;
    final int competencySize = scoreArray.length;
    for (int i = 0; i < competencySize; i++) {
      scoreArray[i] = rand.nextInt(5);
      totalScore += scoreArray[i];
    }
    
    UserCompetencyEvaluationDetails evaluationDetails = new UserCompetencyEvaluationDetails();
    evaluationDetails.setScoreArray(scoreArray);
    evaluationDetails.setTotalScore(totalScore / competencySize);
    competencyFactory.update(evaluationDetails);
    score.setCompetencyEvaluationScoreDetailsId(evaluationDetails.getId());
    score.setScore(evaluationDetails.getTotalScore());
    return score;
  }

  private FeedbackUser getRandomFeedbackUser() {
    FeedbackUser fbUser = new FeedbackUser();
    int nextInt = rand.nextInt();
    fbUser.setFirstName("First" + nextInt);
    fbUser.setLastName("Last" + nextInt);
    fbUser.setEmail("test" + nextInt + "@yopmail.com");
    dbSetup.addUpdate(fbUser);
    return fbUser;
  }

  private CompetencyEvaluation createCompetencyEvaluation(final String companyId) {
    List<EvaluationType> requiredEvaluationList = new ArrayList<EvaluationType>();
    requiredEvaluationList.add(EvaluationType.Manager);
    CompetencyEvaluation competencyEvaluation = CompetencyEvaluation.newInstance(companyId,
        requiredEvaluationList);
    competencyEvaluation.setEndDate(LocalDateTime.now().plusDays(2));
    dbSetup.addUpdate(competencyEvaluation);
    // adding the competency profiles
    competencyFactory.getCompanyCompetencyProfiles(companyId).stream()
        .map(c -> competencyFactory.getCompetencyProfile(c.getId()))
        .forEach(competencyEvaluation::addCompetencyProfile);

    // iterate over all the users and add them to the competency profiles user map
    List<User> memberList = accountRepository.getAllMembersForCompany(companyId);
    memberList.stream().filter(m -> m.getCompetencyProfileId() != null)
        .forEach(m -> competencyFactory.addUserToCompetencyEvaluation(m, competencyEvaluation));
    
    competencyFactory.update(competencyEvaluation);
    
    CompanyDao company = companyFactory.getCompany(companyId);
    company.setCompetencyEvaluationId(competencyEvaluation.getId());
    company.setEvaluationInProgress(true);
    companyFactory.updateCompanyDao(company);
    return competencyEvaluation;
  }    
  
  private CompetencyProfile createCompetencyProfile(String companyId) {
    CompetencyProfile competencyProfile = new CompetencyProfile();
    competencyProfile.setActive(true);
    competencyProfile.setName("Some competency name");
    competencyProfile.setDescription("Some competency description.");
    competencyProfile.setCompanyId(companyId);
    RatingConfiguration ratingConfiguration = new RatingConfiguration(
        RatingConfigurationType.Numeric, 5);
    competencyProfile.setRatingConfiguration(ratingConfiguration);
    SPGoal competency = new SPGoal();
    competency.setName("Some competency name.");
    competency.setStatus(GoalStatus.ACTIVE);
    SPRating rating = new SPRating();
    competency.setRating(rating);
    final List<SPRatingScore> ratingList = new ArrayList<SPRatingScore>();
    SPRatingScore ratingScore = new SPRatingScore();
    ratingScore.setTitle("Terrible");
    ratingScore.setScore(1);
    ratingScore.setDescription("How terrible.");
    ratingList.add(ratingScore);
    SPRatingScore ratingScore2 = new SPRatingScore();
    ratingScore2.setTitle("Intermediate Terrible");
    ratingScore2.setScore(3);
    ratingScore2.setDescription("How intermediate terrible.");
    ratingList.add(ratingScore2);
    SPRatingScore ratingScore3 = new SPRatingScore();
    ratingScore3.setTitle("Super Terrible");
    ratingScore3.setScore(10);
    ratingScore3.setDescription("How super terrible.");
    ratingList.add(ratingScore3);
    rating.setRatingList(ratingList);
    List<DSActionCategory> devStrategyActionCategoryList = new ArrayList<DSActionCategory>();
    DSActionCategory actionCategory = new DSActionCategory();
    devStrategyActionCategoryList.add(actionCategory);
    competency.setDevStrategyActionCategoryList(devStrategyActionCategoryList);
    actionCategory.setTitle("action category name");
    List<DSAction> actionList = new ArrayList<DSAction>();
    DSAction action =  new DSAction();
    actionList.add(action);
    actionCategory.setActionList(actionList);
    action.setTitle("Strategy name");
    List<String> mandatoryArticles = new ArrayList<String>();
    mandatoryArticles.add("1");
    competency.setMandatoryArticles(mandatoryArticles);
    dbSetup.addUpdate(competency);
    List<String> competencyIdList = new ArrayList<String>();
    competencyIdList.add(competency.getId());
    competencyProfile.setCompetencyIdList(competencyIdList);
    dbSetup.addUpdate(competencyProfile);
    return competencyProfile;
  }  
}


