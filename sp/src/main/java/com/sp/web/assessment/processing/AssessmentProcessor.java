package com.sp.web.assessment.processing;

import static com.sp.web.Constants.PERSONALITY_LEAST;
import static com.sp.web.Constants.PERSONALITY_MOST;
import static com.sp.web.Constants.PRECISION;
import static com.sp.web.Constants.ROUNDING_MODE;

import com.sp.web.assessment.personality.PersonalityBeanResponse;
import com.sp.web.assessment.personality.PersonalityFactory;
import com.sp.web.assessment.personality.PersonalityMatcher;
import com.sp.web.assessment.personality.RangeType;
import com.sp.web.assessment.questions.CategoryType;
import com.sp.web.assessment.questions.OptionsBean;
import com.sp.web.assessment.questions.QuestionsBean;
import com.sp.web.assessment.questions.QuestionsFactory;
import com.sp.web.assessment.questions.RatingBean;
import com.sp.web.assessment.questions.TraitType;
import com.sp.web.assessment.questions.TraitsBean;
import com.sp.web.assessment.questions.TraitsTransform;
import com.sp.web.exception.InvalidParameterException;
import com.sp.web.exception.InvalidRequestException;
import com.sp.web.exception.SPException;
import com.sp.web.model.User;
import com.sp.web.model.assessment.AssessmentProgressTracker;
import com.sp.web.model.assessment.PrismAssessment;
import com.sp.web.mvc.signin.LoginHelper;
import com.sp.web.user.UserFactory;
import com.sp.web.utils.ApplicationContextUtils;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import javax.inject.Inject;

/**
 * @author Dax Abraham
 * 
 *         The processor class for all the assessments.
 */
@Component
@Scope("prototype")
public class AssessmentProcessor {
  
  private static final BigDecimal ZERO_VALUE = BigDecimal.valueOf(0).setScale(PRECISION,
      ROUNDING_MODE);

  /**
   * Reference to the logger.
   */
  private static final Logger LOG = Logger.getLogger(AssessmentProcessor.class);
  
  private static final BigDecimal ONE_PERCENT = BigDecimal.valueOf(0.01f);
  
  /**
   * The reference to the questions factory.
   */
  private QuestionsFactory questionsFactory;
  
  /**
   * The reference to the personality factory to process the personality type.
   */
  @Autowired
  PersonalityFactory personalityFactory;
  
  /**
   * Reference to the repository to store and retrieve the assessment progress.
   */
  AssessmentProgressStoreRepoistory repository;
  
  /* LoginHelper to update the user */
  @Autowired
  private LoginHelper loginHelper;
  
  /**
   * The user for the assessment.
   */
  private User user;
  
  @Autowired
  private UserFactory userFactory;
  
  private PrismAssessment assessment;
  
  /**
   * Constructor.
   * 
   * @param user
   *          - the user taking the assessment
   */
  @Inject
  public AssessmentProcessor(User user, QuestionsFactory questionsFactory) {
    
    this.repository = ApplicationContextUtils.getApplicationContext().getBean(
        AssessmentProgressStoreRepoistory.class);
    
    AssessmentProgressTracker assessmentTracker = repository.getAssessmentTracker(user.getId());
    if (assessmentTracker == null) {
      assessmentTracker = new AssessmentProgressTracker();
      assessmentTracker.setUserId(user.getId());
      repository.add(assessmentTracker);
    }
    
    String currentAssessment = assessmentTracker.getCurrentAssessment();
    if (currentAssessment != null && assessmentTracker.isAssessmentInProgress()) {
      assessment = repository.getPrismAssessment(currentAssessment);
    } else {
      assessment = new PrismAssessment(user);
      assessment.setUserId(user.getId());
      assessment.setCreatedOn(LocalDateTime.now());
      repository.update(assessment);
      assessmentTracker.setCurrentAssessment(assessment.getId());
      assessmentTracker.setAssessmentInProgress(true);
      repository.update(assessmentTracker);
    }
    
    this.user = user;
    this.questionsFactory = questionsFactory;
  }
  
  /**
   * This method resets the current question by one.
   */
  public void processPrevious() {
    assessment.processPrevious(questionsFactory);
    repository.update(assessment);
  }
  
  /**
   * The method to process the assessment response from the user.
   * 
   * @param questionNumber
   *          - question number
   * @param answer
   *          - selected answer
   */
  public void process(int questionNumber, List<String> answer) {
    validate(questionNumber, answer);
    assessment.process(questionNumber, answer);
    repository.update(assessment);
  }
  
  private void validate(int questionNumber, List<String> answer) {
    QuestionsBean lastQuestion = getQuestion().get();
    Assert.isTrue(lastQuestion.getNumber() == questionNumber, "Question mismatch.");
    Optional<OptionsBean> findAnswer;
    switch (lastQuestion.getType()) {
    case MultipleChoice:
      Assert.isTrue(answer.size() == 1, "Answer size mismatch.");
      findAnswer = lastQuestion.findAnswer(answer.get(0));
      Assert.isTrue(findAnswer.isPresent(), "Answer not found.");
      break;
    
    case PersonalityRating:
    case Rating:
    case VariableRating:
      TraitsBean trait = questionsFactory.getTrait(questionNumber);
      RatingBean[] factor = trait.getFactor();
      Assert.isTrue(answer.size() == factor.length, "Answer size mismatch.");
      for (int i = 0; i < answer.size(); i++) {
        findAnswer = lastQuestion.findAnswer(answer.get(0));
        Assert.isTrue(findAnswer.isPresent(), "Answer not found.");
      }
      break;
    default:
      break;
    }
  }

  /**
   * Calculates the score for the given transform.
   * 
   * @param traitsTransform
   *          - the traits to update
   * @param factor
   *          - the factor to apply
   * @param doIncrement
   *          - if true will increment else decrement
   */
  private void calculateScore(Map<CategoryType, Map<TraitType, ScoreBean>> score,
      List<TraitsTransform> traitsTransform, double factor) {
    for (TraitsTransform transform : traitsTransform) {
      Map<TraitType, ScoreBean> traitsMap = score.get(transform.getCategory());
      if (traitsMap == null) {
        traitsMap = new HashMap<TraitType, ScoreBean>();
        score.put(transform.getCategory(), traitsMap);
      }
      ScoreBean currentScore = traitsMap.get(transform.getTrait());
      if (currentScore == null) {
        currentScore = new ScoreBean();
        traitsMap.put(transform.getTrait(), currentScore);
      }
      currentScore.increment(transform.getIncrementAmount() * factor);
    }
  }
  
  /**
   * Increment the variable rating scores.
   * 
   * @param score
   *          - score
   * @param traitsTransformList
   *          - traits transform list
   * @param factor
   *          - factor
   * @param index
   *          - index
   */
  private void calculateScoreVariableRating(Map<CategoryType, Map<TraitType, ScoreBean>> score,
      List<TraitsTransform> traitsTransformList, double factor, int index) {
    for (TraitsTransform transform : traitsTransformList) {
      Map<TraitType, ScoreBean> traitsMap = score.computeIfAbsent(transform.getCategory(),
          v -> new HashMap<TraitType, ScoreBean>());
      VariableRatingScoreBean currentScore = (VariableRatingScoreBean) traitsMap.computeIfAbsent(
          transform.getTrait(), v -> new VariableRatingScoreBean());
      currentScore.increment(index, transform.getIncrementAmount() * factor);
    }
  }
  
  private Map<CategoryType, Map<TraitType, ScoreBean>> processScore() {
    final Map<CategoryType, Map<TraitType, ScoreBean>> score = new HashMap<CategoryType, Map<TraitType, ScoreBean>>();
    // get the users assessment responses
    Map<Integer, List<String>> assessmentDataMap = assessment.getAssessmentDataMap();
    // get the traits map
    Map<Integer, TraitsBean> traitsMap = questionsFactory.getTraitsMap();
    String ans = null;
    RatingBean[] ratingFactorArray;
    RatingBean ratingBean;
    ArrayList<TraitsTransform> tempTransformList = new ArrayList<TraitsTransform>();
    String requiredRating;
    for (Entry<Integer, List<String>> entry : assessmentDataMap.entrySet()) {
      TraitsBean traitsBean = traitsMap.get(entry.getKey());
      List<String> userResponse = entry.getValue();
      if (traitsBean != null) {
        final int responseSize = userResponse.size();
        switch (traitsBean.getType()) {
        case MultipleChoice:
          // get the traits transform corresponding to the selected
          // response
          if (responseSize > 1) {
            throw new InvalidParameterException(
                "Invalid answer multiple choice question type can have only one selection in the request.");
          }
          ans = userResponse.get(0);
          List<TraitsTransform> traitsTransformList = traitsBean.getTraitsTransform(ans);
          if (traitsTransformList == null || traitsTransformList.isEmpty()) {
            continue;
          }
          
          calculateScore(score, traitsTransformList, 1);
          break;
        case Rating:
          ratingFactorArray = traitsBean.getFactor();
          if (responseSize != ratingFactorArray.length) {
            LOG.fatal("Could not process question:" + entry.getKey());
            throw new InvalidParameterException(
                "Invalid answer ratings question type must have selections "
                    + "equal to number of options in the request.");
          }
          
          for (int i = 0; i < responseSize; i++) {
            ans = userResponse.get(i);
            traitsTransformList = traitsBean.getTraitsTransform(ans);
            if (traitsTransformList == null || traitsTransformList.isEmpty()) {
              continue;
            }
            
            ratingBean = ratingFactorArray[i];
            if (ratingBean.getFactor() == 0) {
              continue;
            }
            // get all the traits for the given answer
            calculateScore(score, traitsTransformList, ratingBean.getFactor());
          }
          break;
        case PersonalityRating:
          ratingFactorArray = traitsBean.getFactor();
          if (responseSize != 2) {
            LOG.fatal("Could not process question:" + entry.getKey());
            LOG.fatal("Answer received :" + userResponse);
            throw new InvalidParameterException(
                "Invalid answer ratings question type must have selections equal to "
                    + "number of options in the request.");
          }
          for (int i = 0; i < responseSize; i++) {
            ans = userResponse.get(i);
            // get all the traits for the given answer
            traitsTransformList = traitsBean.getTraitsTransform(ans);
            if (traitsTransformList == null || traitsTransformList.isEmpty()) {
              continue;
            }
            
            ratingBean = ratingFactorArray[i];
            if (ratingBean.getFactor() == 0) {
              continue;
            }
            // for personality type only choose the one that is
            // applicable
            // i.e. 0 for most and 1 for least
            for (TraitsTransform t : traitsTransformList) {
              requiredRating = t.getRequiredRating();
              // check for most before adding
              if (requiredRating == null) {
                throw new SPException("Required rating not found for Traits Transform :"
                    + t.getCategory() + ":" + t.getTrait());
              }
              if (i == 0) { // personality most
                if (requiredRating.equalsIgnoreCase(PERSONALITY_MOST)) {
                  tempTransformList.add(t);
                }
              } else if (i == 1) { // personality least
                // check for least before adding
                if (requiredRating.equalsIgnoreCase(PERSONALITY_LEAST)) {
                  tempTransformList.add(t);
                }
              }
            }
            if (tempTransformList.size() > 0) {
              calculateScore(score, tempTransformList, ratingBean.getFactor());
            }
            tempTransformList.clear();
          }
          break;
        case VariableRating:
          ratingFactorArray = traitsBean.getFactor();
          if (responseSize != ratingFactorArray.length) {
            LOG.fatal("Could not process question:" + entry.getKey());
            throw new InvalidParameterException(
                "Invalid answer ratings question type must have selections "
                    + "equal to number of options in the request.");
          }
          
          for (int i = 0; i < responseSize; i++) {
            ans = userResponse.get(i);
            traitsTransformList = traitsBean.getTraitsTransform(ans);
            if (traitsTransformList == null || traitsTransformList.isEmpty()) {
              continue;
            }
            
            ratingBean = ratingFactorArray[i];
            if (ratingBean.getFactor() == 0) {
              continue;
            }
            // get all the traits for the given answer
            calculateScoreVariableRating(score, traitsTransformList, ratingBean.getFactor(), i);
          }
          break;
        default:
          LOG.fatal("Don't know how to process the question type :" + traitsBean.getType());
          throw new SPException("Don't know how to process the question type :"
              + traitsBean.getType());
        }
      } else {
        LOG.warn("Trait not found for question number : " + entry.getKey());
      }
    }
    
    return score;
  }

  /**
   * Processes the final result.
   * 
   * @return the final analysis of the assessment
   */
  public AnalysisBean processFinalResult() {
    
    // perform the complete calculations for all the scores
    // resetting any previously calculated scores
    final Map<CategoryType, Map<TraitType, ScoreBean>> score = processScore();
    
    // re getting from db
    user = user.getUpdatedUser(userFactory);
    
    if (user == null) {
      throw new InvalidRequestException("User not found :" + assessment.getUserId());
    }

    final String userId = user.getId();
    
    AnalysisBean analysis = user.getAnalysis();
    
    if (analysis == null) {
      // creating a new analysis bean to store the assessment
      // also setting the created on for the analysis
      analysis = new AnalysisBean();
      analysis.setCreatedOn(LocalDateTime.now());
      analysis.setCompletedCategories(new HashSet<CategoryType>());
      user.setAnalysis(analysis);
      userFactory.updateUser(user);
    }
    
    final List<CategoryType> categories = assessment.getCategories();
    for (CategoryType category : categories) {
      
      switch (category) {
      case Processing:
        // calculating processing
        analyzeProcessing(score, analysis);
        break;
      case ConflictManagement:
        // calculating conflict management
        analyzeConflictManagement(score, analysis);
        break;
      case LearningStyle:
        // calculating Learning style
        analyzeLearningStyle(score, analysis);
        break;
      case MotivationWhy:
        // calculating motivation why style
        analyzeMotivationWhy(score, analysis);
        break;
      case MotivationHow:
        // calculating motivation how style
        analyzeMotivationHow(score, analysis);
        break;
      case MotivationWhat:
        // calculating motivation what style
        analyzeMotivationWhat(score, analysis);
        break;
      case Personality:
        // calculating the personality style
        analyzePersonality(score, analysis);
        break;
      case FundamentalNeeds:
        // Calculate fundamental needs
        analyzeFundamentalNeeds(score, analysis);
        break;
      case DecisionMaking:
        // Calculate decision making
        analyzeDecisionMaking(score, analysis);
        break;
      default:
        LOG.warn("Cannot process category :" + category);
        break;
      }
    }
    
    // update the list of categories
    analysis.updateCompletedCategories(categories);
    
    // create the DTO and replace the current analysis object
    AnalysisBeanDTO analysisDTO = new AnalysisBeanDTO(analysis);
    
    // store the analysis to the database
    // Update the user status to assessment complete */
    if (user != null) {
      assessment.setCompletedOn(LocalDateTime.now());
      assessment.setCompleted(true);
      repository.update(assessment);
      AssessmentProgressTracker assessmentTracker = repository.getAssessmentTracker(userId);
      repository.update(assessmentTracker.assessmentCompleted());
      userFactory.updateAnalysisAndInitializeUser(user, analysisDTO);
    } else {
      LOG.fatal("Unable to store the user analysis for user :" + userId);
      throw new SPException("Unable to store the user analysis for user :" + userId);
    }
    return analysis;
  }
  
  /**
   * Analyze the decision making style category.
   * 
   * @param score
   *          - the score map
   * @param analysis
   *          - user analysis
   */
  private void analyzeDecisionMaking(Map<CategoryType, Map<TraitType, ScoreBean>> score,
      AnalysisBean analysis) {
    Map<TraitType, BigDecimal> tempMap = new HashMap<TraitType, BigDecimal>();
    Map<TraitType, ScoreBean> scoreMap = score.get(CategoryType.DecisionMaking);
    
    if (scoreMap == null) {
      scoreMap = new HashMap<TraitType, ScoreBean>();
    }
    
    analyzeProcessingHelper(tempMap, scoreMap, TraitType.Careful, TraitType.Rapid);
    analyzeProcessingHelper(tempMap, scoreMap, TraitType.Inward, TraitType.Outward);
    
    analysis.setDecisionMaking(tempMap);
  }
  
  /**
   * Analyze the fundamental needs style category.
   * 
   * @param score
   *          - the score map
   * @param analysis
   *          - user analysis
   */
  private void analyzeFundamentalNeeds(Map<CategoryType, Map<TraitType, ScoreBean>> score,
      AnalysisBean analysis) {
    Map<TraitType, BigDecimal> tempMap = new HashMap<TraitType, BigDecimal>();
    Map<TraitType, ScoreBean> scoreMap = score.get(CategoryType.FundamentalNeeds);
    
    if (scoreMap == null) {
      scoreMap = new HashMap<TraitType, ScoreBean>();
    }
    
    ScoreBean securityScore = (ScoreBean) scoreMap.computeIfAbsent(TraitType.Security,
        v -> new ScoreBean());
    ScoreBean significanceScore = (ScoreBean) scoreMap.computeIfAbsent(TraitType.Significance,
        v -> new ScoreBean());
    ScoreBean controlScore = (ScoreBean) scoreMap.computeIfAbsent(TraitType.Control,
        v -> new ScoreBean());
    
    tempMap.put(TraitType.Security,
        BigDecimal.valueOf(securityScore.getScore()).setScale(PRECISION, ROUNDING_MODE));
    tempMap.put(TraitType.Significance,
        BigDecimal.valueOf(significanceScore.getScore()).setScale(PRECISION, ROUNDING_MODE));
    tempMap.put(TraitType.Control,
        BigDecimal.valueOf(controlScore.getScore()).setScale(PRECISION, ROUNDING_MODE));

    analysis.setFundamentalNeeds(tempMap);
  }
  
  /**
   * Analyze the personality style category.
   * 
   * @param score
   *          - the score map
   * @param analysis
   *          - user analysis
   */
  private void analyzePersonality(Map<CategoryType, Map<TraitType, ScoreBean>> score,
      AnalysisBean analysis) {
    HashMap<RangeType, PersonalityBeanResponse> tempMap = new HashMap<RangeType, PersonalityBeanResponse>();
    Map<TraitType, ScoreBean> scoreMap = score.get(CategoryType.Personality);
    PersonalityMatcher personalityMatcher = personalityFactory.getPersonalityMatcher();
    
    if (scoreMap == null) {
      scoreMap = new HashMap<TraitType, ScoreBean>();
    }
    
    // setting the primary
    int dominanceMost = (scoreMap.get(TraitType.Dominance_Most) != null) ? scoreMap.get(
        TraitType.Dominance_Most).getIntScore() : 0;
    int inducmentMost = (scoreMap.get(TraitType.Inducement_Most) != null) ? scoreMap.get(
        TraitType.Inducement_Most).getIntScore() : 0;
    int submissionMost = (scoreMap.get(TraitType.Submission_Most) != null) ? scoreMap.get(
        TraitType.Submission_Most).getIntScore() : 0;
    int complianceMost = (scoreMap.get(TraitType.Compliance_Most) != null) ? scoreMap.get(
        TraitType.Compliance_Most).getIntScore() : 0;
    
    tempMap.put(RangeType.Primary, personalityMatcher.getPrimaryPersonalityScore(dominanceMost,
        inducmentMost, submissionMost, complianceMost));
    
    // setting the under pressure
    int dominanceLeast = (scoreMap.get(TraitType.Dominance_Least) != null) ? scoreMap.get(
        TraitType.Dominance_Least).getIntScore() : 0;
    int inducmentLeast = (scoreMap.get(TraitType.Inducement_Least) != null) ? scoreMap.get(
        TraitType.Inducement_Least).getIntScore() : 0;
    int submissionLeast = (scoreMap.get(TraitType.Submission_Least) != null) ? scoreMap.get(
        TraitType.Submission_Least).getIntScore() : 0;
    int complianceLeast = (scoreMap.get(TraitType.Compliance_Least) != null) ? scoreMap.get(
        TraitType.Compliance_Least).getIntScore() : 0;
    
    tempMap.put(RangeType.UnderPressure, personalityMatcher.getUnderPressurePersonalityScore(
        dominanceLeast, inducmentLeast, submissionLeast, complianceLeast));
    
    // setting the perceived by others
    tempMap.put(RangeType.PerceivedByOthers, personalityMatcher
        .getPerceievedByOthersPersonalityScore(dominanceMost - dominanceLeast, inducmentMost
            - inducmentLeast, submissionMost - submissionLeast, complianceMost - complianceLeast));
    
    // fix the personality if there is a tight or under or over shift
    personalityFactory.fixPerosnality(tempMap);
    
    // setting the analysis
    analysis.setPersonality(tempMap);
    
  }
  
  /**
   * Analyze the motivation how style category.
   * 
   * @param score
   *          - the score map
   * @param analysis
   *          - user analysis
   */
  private void analyzeMotivationWhat(Map<CategoryType, Map<TraitType, ScoreBean>> score,
      AnalysisBean analysis) {
    Map<TraitType, BigDecimal> tempMap = new HashMap<TraitType, BigDecimal>();
    Map<TraitType, ScoreBean> scoreMap = score.get(CategoryType.Motivation);
    double motivationWhatMaxValue = 6;
    
    if (scoreMap == null) {
      if (LOG.isDebugEnabled()) {
        LOG.debug("Ignoring motivation what as no score set.");
      }
      return;
    }
    
    ScoreBean scoreBean = scoreMap.get(TraitType.Hygiene);
    
    if (scoreBean != null) {
      tempMap.put(
          TraitType.Hygiene,
          BigDecimal.valueOf(scoreBean.getScore() / motivationWhatMaxValue).setScale(PRECISION,
              ROUNDING_MODE));
    } else {
      tempMap.put(TraitType.Hygiene, new BigDecimal(0).setScale(PRECISION, ROUNDING_MODE));
      
    }
    
    scoreBean = scoreMap.get(TraitType.Accomplishment);
    if (scoreBean != null) {
      tempMap.put(
          TraitType.Accomplishment,
          BigDecimal.valueOf(scoreBean.getScore() / motivationWhatMaxValue).setScale(PRECISION,
              ROUNDING_MODE));
    } else {
      tempMap.put(TraitType.Accomplishment, new BigDecimal(0).setScale(PRECISION, ROUNDING_MODE));
      
    }
    
    analysis.setMotivationWhat(tempMap);
  }
  
  /**
   * Analyze the motivation how style category.
   * 
   * @param score
   *          - the score map
   * @param analysis
   *          - user analysis
   */
  private void analyzeMotivationHow(Map<CategoryType, Map<TraitType, ScoreBean>> score,
      AnalysisBean analysis) {
    Map<TraitType, BigDecimal> tempMap = new HashMap<TraitType, BigDecimal>();
    Map<TraitType, ScoreBean> scoreMap = score.get(CategoryType.Motivation);
    double motivationHowMaxValue = 8d;
    
    if (scoreMap == null) {
      if (LOG.isDebugEnabled()) {
        LOG.debug("Ignoring motivation how as no score set.");
      }
      return;
    }
    
    ScoreBean scoreBean = scoreMap.get(TraitType.AffirmedByOthers);
    BigDecimal calculatedScore;
    if (scoreBean != null) {
      calculatedScore = BigDecimal.valueOf(scoreBean.getScore() / motivationHowMaxValue).setScale(
          PRECISION, ROUNDING_MODE);
    } else {
      calculatedScore = ZERO_VALUE;
    }
    tempMap.put(TraitType.AffirmedByOthers, calculatedScore);
    tempMap.put(TraitType.SelfAffirmed, BigDecimal.ONE.subtract(calculatedScore));
    
    scoreBean = scoreMap.get(TraitType.ExchangeOfIdeas);
    if (scoreBean != null) {
      calculatedScore = BigDecimal.valueOf(scoreBean.getScore() / motivationHowMaxValue).setScale(PRECISION,
          ROUNDING_MODE);
    } else {
      calculatedScore = ZERO_VALUE;
    }
    tempMap.put(TraitType.ExchangeOfIdeas, calculatedScore);
    tempMap.put(TraitType.ReceiveDirection, BigDecimal.ONE.subtract(calculatedScore));

    scoreBean = scoreMap.get(TraitType.Consistency);
    if (scoreBean != null) {
      calculatedScore = BigDecimal.valueOf(scoreBean.getScore() / motivationHowMaxValue).setScale(
          PRECISION, ROUNDING_MODE);
    } else {
      calculatedScore = ZERO_VALUE;
    }
    tempMap.put(TraitType.Consistency, calculatedScore);
    tempMap.put(TraitType.Freedom, BigDecimal.ONE.subtract(calculatedScore));
    
    scoreBean = scoreMap.get(TraitType.TaskCompletion);
    if (scoreBean != null) {
      calculatedScore = BigDecimal.valueOf(scoreBean.getScore() / motivationHowMaxValue).setScale(PRECISION,
          ROUNDING_MODE);
    } else {
      calculatedScore = ZERO_VALUE;
    }
    tempMap.put(TraitType.TaskCompletion, calculatedScore);
    tempMap.put(TraitType.PrefersProcess, BigDecimal.ONE.subtract(calculatedScore));
    
    analysis.setMotivationHow(tempMap);
    
  }
  
  /**
   * Analyze the motivation why style category.
   * 
   * @param score
   *          - the score map
   * @param analysis
   *          - user analysis
   */
  private void analyzeMotivationWhy(Map<CategoryType, Map<TraitType, ScoreBean>> score,
      AnalysisBean analysis) {
    Map<TraitType, BigDecimal> tempMap = new HashMap<TraitType, BigDecimal>();
    Map<TraitType, ScoreBean> scoreMap = score.get(CategoryType.Motivation);
    double motivationWhyValue = .1d;
    
    if (scoreMap == null) {
      if (LOG.isDebugEnabled()) {
        LOG.debug("Ignoring motivation why as no score set.");
      }
      return;
    }
    
    ScoreBean scoreBean = scoreMap.get(TraitType.AttainmentOfGoals);
    if (scoreBean != null) {
      tempMap.put(
          TraitType.AttainmentOfGoals,
          BigDecimal.valueOf(scoreBean.getScore() * motivationWhyValue).setScale(PRECISION,
              ROUNDING_MODE));
    } else {
      tempMap.put(TraitType.AttainmentOfGoals, ZERO_VALUE);
    }
    
    scoreBean = scoreMap.get(TraitType.RecognitionForEffort);
    if (scoreBean != null) {
      tempMap.put(
          TraitType.RecognitionForEffort,
          BigDecimal.valueOf(scoreBean.getScore() * motivationWhyValue).setScale(PRECISION,
              ROUNDING_MODE));
    } else {
      tempMap.put(TraitType.RecognitionForEffort, ZERO_VALUE);
    }
    
    scoreBean = scoreMap.get(TraitType.Power);
    if (scoreBean != null) {
      tempMap.put(TraitType.Power, BigDecimal.valueOf(scoreBean.getScore() * motivationWhyValue)
          .setScale(PRECISION, ROUNDING_MODE));
    } else {
      tempMap.put(TraitType.Power, ZERO_VALUE);
    }
    
    scoreBean = scoreMap.get(TraitType.Compliance);
    if (scoreBean != null) {
      tempMap.put(
          TraitType.Compliance,
          BigDecimal.valueOf(scoreBean.getScore() * motivationWhyValue).setScale(PRECISION,
              ROUNDING_MODE));
    } else {
      tempMap.put(TraitType.Compliance, ZERO_VALUE);
    }
    
    scoreBean = scoreMap.get(TraitType.Affiliation);
    if (scoreBean != null) {
      tempMap.put(
          TraitType.Affiliation,
          BigDecimal.valueOf(scoreBean.getScore() * motivationWhyValue).setScale(PRECISION,
              ROUNDING_MODE));
    } else {
      tempMap.put(TraitType.Affiliation, ZERO_VALUE);
    }
    
    scoreBean = scoreMap.get(TraitType.Activity);
    if (scoreBean != null) {
      tempMap.put(TraitType.Activity, BigDecimal.valueOf(scoreBean.getScore() * motivationWhyValue)
          .setScale(PRECISION, ROUNDING_MODE));
    } else {
      tempMap.put(TraitType.Activity, ZERO_VALUE);
    }
    
    analysis.setMotivationWhy(tempMap);
  }
  
  /**
   * Analyze the learning style category.
   * 
   * @param score
   *          - the score map
   * @param analysis
   *          - user analysis
   */
  private void analyzeLearningStyle(Map<CategoryType, Map<TraitType, ScoreBean>> score,
      AnalysisBean analysis) {
    Map<TraitType, BigDecimal> tempMap = new HashMap<TraitType, BigDecimal>();
    Map<TraitType, ScoreBean> scoreMap = score.get(CategoryType.LearningStyle);
    
    analyzeProcessingHelper(tempMap, scoreMap, TraitType.Analytical, TraitType.Global);
    analysis.setLearningStyle(tempMap);
  }
  
  /**
   * Analyze the conflict management category.
   * 
   * @param score
   *          - the score map
   * @param analysis
   *          - user analysis
   */
  private void analyzeConflictManagement(Map<CategoryType, Map<TraitType, ScoreBean>> score,
      AnalysisBean analysis) {
    
    Map<TraitType, BigDecimal> tempMap = new HashMap<TraitType, BigDecimal>();
    Map<TraitType, ScoreBean> scoreMap = score.get(CategoryType.ConflictManagement);


    double compete = scoreMap.computeIfAbsent(TraitType.Compete, v -> new ScoreBean()).getScore();
    double compromise = scoreMap.computeIfAbsent(TraitType.Compromise, v -> new ScoreBean()).getScore();
    double collaborate = scoreMap.computeIfAbsent(TraitType.Collaborate, v -> new ScoreBean()).getScore();
    double avoid = scoreMap.computeIfAbsent(TraitType.Avoid, v -> new ScoreBean()).getScore();
    double accomodate = scoreMap.computeIfAbsent(TraitType.Accommodate, v -> new ScoreBean()).getScore();
    
    double total = compete + compromise + collaborate + avoid + accomodate;
    
    tempMap.put(TraitType.Compete,
        BigDecimal.valueOf(compete / total).setScale(PRECISION, ROUNDING_MODE));
    
    tempMap.put(TraitType.Compromise,
        BigDecimal.valueOf(compromise / total).setScale(PRECISION, ROUNDING_MODE));

    tempMap.put(TraitType.Collaborate,
        BigDecimal.valueOf(collaborate / total).setScale(PRECISION, ROUNDING_MODE));

    tempMap.put(TraitType.Avoid,
        BigDecimal.valueOf(avoid / total).setScale(PRECISION, ROUNDING_MODE));
    
    tempMap.put(TraitType.Accommodate,
        BigDecimal.valueOf(accomodate / total).setScale(PRECISION, ROUNDING_MODE));

    analysis.setConflictManagement(tempMap);
  }
  
  // /**
  // * Analyze the accuracy category.
  // */
  // private void analyzeAccuracy() {
  // Map<TraitType, ScoreBean> categoryMap = score.get(CategoryType.Accuracy);
  // ScoreBean accuracyScoreBean = null;
  // if (categoryMap != null) {
  // accuracyScoreBean = score.get(CategoryType.Accuracy).get(TraitType.Accurate);
  // }
  //
  // if (accuracyScoreBean != null) {
  // int accuracyIdx = Double.valueOf(accuracyScoreBean.getScore()).intValue();
  // Random rnd = new Random();
  // if (accuracyIdx >= ACCURACY_RANGE_HIGH.length) {
  // accuracyIdx = ACCURACY_RANGE_HIGH.length - 1;
  // }
  // analysis
  // .setAccuracy(BigDecimal
  // .valueOf(
  // ACCURACY_RANGE_LOW[accuracyIdx]
  // + (rnd.nextDouble() * (ACCURACY_RANGE_HIGH[accuracyIdx] - ACCURACY_RANGE_LOW[accuracyIdx])))
  // .setScale(PRECISION, ROUNDING_MODE));
  // } else {
  // analysis.setAccuracy(BigDecimal.valueOf(0));
  // }
  // }
  
  /**
   * Analyze the processing category.
   * 
   * @param score
   *          - the score map
   * @param analysis
   *          - user analysis
   */
  private void analyzeProcessing(Map<CategoryType, Map<TraitType, ScoreBean>> score,
      AnalysisBean analysis) {
    Map<TraitType, BigDecimal> tempMap = new HashMap<TraitType, BigDecimal>();
    Map<TraitType, ScoreBean> scoreMap = score.get(CategoryType.Processing);
    
    analyzeProcessingHelper(tempMap, scoreMap, TraitType.External, TraitType.Internal);
    analyzeProcessingHelper(tempMap, scoreMap, TraitType.Concrete, TraitType.Intuitive);
    analyzeProcessingHelper(tempMap, scoreMap, TraitType.Affective, TraitType.Cognitive);
    analyzeProcessingHelper(tempMap, scoreMap, TraitType.Orderly, TraitType.Spontaneous);
    
    analysis.setProcessing(tempMap);
  }
  
  /**
   * Processing helper to calculate the scores for multiple traits.
   * 
   * @param tempMap
   *          - temporary result map
   * @param scoreMap
   *          - score map
   * @param trait1
   *          - first trait to process
   * @param trait2
   *          - second trait to process
   */
  private void analyzeProcessingHelper(Map<TraitType, BigDecimal> tempMap,
      Map<TraitType, ScoreBean> scoreMap, TraitType trait1, TraitType trait2) {
    
    if (scoreMap == null) {
      scoreMap = new HashMap<TraitType, ScoreBean>();
    }
    double dblScore1 = (scoreMap.get(trait1) != null) ? scoreMap.get(trait1).getScore() : 0;
    double dblScore2 = (scoreMap.get(trait2) != null) ? scoreMap.get(trait2).getScore() : 0;
    
    double aggregateScore = (dblScore1 + dblScore2);
    
    if (aggregateScore == 0) {
      aggregateScore = 1d;
    }
    
    BigDecimal score1 = BigDecimal.valueOf(dblScore1 / aggregateScore).setScale(PRECISION,
        ROUNDING_MODE);
    BigDecimal score2 = BigDecimal.valueOf(dblScore2 / aggregateScore).setScale(PRECISION,
        ROUNDING_MODE);
    
    final BigDecimal totalScore = score1.add(score2);
    switch (totalScore.compareTo(BigDecimal.ONE)) {
    case -1:
      if (score1.compareTo(score2) == -1) {
        score2 = score2.add(ONE_PERCENT);
      } else {
        score1 = score1.add(ONE_PERCENT);
      }
      break;
    case 1:
      if (score1.compareTo(score2) == -1) {
        score1 = score1.subtract(ONE_PERCENT);
      } else {
        score2 = score2.subtract(ONE_PERCENT);
      }
      break;
    default:
      // do nothing
    }
    tempMap.put(trait1, score1);
    tempMap.put(trait2, score2);
  }
  
  /**
   * Gets the next question.
   * 
   * @return the next question
   */
  public Optional<QuestionsBean> getQuestion() {
    
    List<QuestionsBean> questions = questionsFactory.getQuestions(assessment.getCategory());
    
    boolean isAssessmentComplete = false;
    if (questions == null) {
      if (assessment.allCategoriesAnswered()) {
        processFinalResult();
        isAssessmentComplete = true;
      } else {
        throw new IllegalArgumentException("Something went wrong please contact the administrator.");
      }
    } else {
      int questionIndex = assessment.getQuestionIndex();
      
      // check if the questions in the particular category are over
      if (questionIndex >= questions.size()) {
        
        CategoryType nextCategory = assessment.getNextCategory(); // incrementing the category
        
        if (nextCategory != null) {
          if (LOG.isDebugEnabled()) {
            LOG.debug("Changing the category to :" + nextCategory);
          }
          questions = questionsFactory.getQuestions(nextCategory);
          questionIndex = assessment.getQuestionIndex();
          repository.update(assessment);
        } else {
          if (LOG.isDebugEnabled()) {
            LOG.debug("All the questions have been answered, going to process response.");
          }
          processFinalResult();
          isAssessmentComplete = true;
        }
      }
      if (!isAssessmentComplete) {
        return Optional.ofNullable(questions.get(questionIndex));
      }
    }
    
    // no more questions to send back
    return Optional.empty();
  }
  
  /**
   * Gets the last answer.
   * 
   * @return the last answer
   */
  public List<String> getLastAnswer() {
    return assessment.getLastAnswer();
  }
  
  public void categoriesToProcess(List<CategoryType> categories) {
    assessment.initializeCategories(categories);
    repository.update(assessment);
  }

  public int getTotalQuestionsAnswered() {
    return assessment.getQuestionsAnsweredCount();
  }

  public User getUser() {
    return user;
  }
}
