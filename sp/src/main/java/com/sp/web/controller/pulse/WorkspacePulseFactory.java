package com.sp.web.controller.pulse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sp.web.Constants;
import com.sp.web.controller.notifications.NotificationType;
import com.sp.web.controller.notifications.NotificationsProcessor;
import com.sp.web.dto.pulse.PulseQuestionSetListDTO;
import com.sp.web.exception.InvalidRequestException;
import com.sp.web.exception.SPException;
import com.sp.web.form.PulseAssessmentForm;
import com.sp.web.model.Company;
import com.sp.web.model.User;
import com.sp.web.model.account.SPPlanType;
import com.sp.web.model.log.LogActionType;
import com.sp.web.model.pulse.PulseAssessment;
import com.sp.web.model.pulse.PulseQuestionBean;
import com.sp.web.model.pulse.PulseQuestionSet;
import com.sp.web.model.pulse.PulseQuestionSetStatus;
import com.sp.web.model.pulse.PulseRequest;
import com.sp.web.model.pulse.PulseResults;
import com.sp.web.model.pulse.PulseScore;
import com.sp.web.model.pulse.PulseScoreBean;
import com.sp.web.model.pulse.PulseSelection;
import com.sp.web.model.pulse.QuestionOptions;
import com.sp.web.model.pulse.QuestionSetType;
import com.sp.web.model.todo.TodoRequest;
import com.sp.web.product.CompanyFactory;
import com.sp.web.repository.pulse.WorkspacePulseRepository;
import com.sp.web.repository.user.UserRepository;
import com.sp.web.service.log.LogGateway;
import com.sp.web.service.todo.TodoFactory;
import com.sp.web.user.UserFactory;
import com.sp.web.utils.MessagesHelper;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

/**
 * @author Dax Abraham
 *
 *         The factory class for the workspace pulse.
 */
@Component
public class WorkspacePulseFactory {
  
  private static final Logger log = Logger.getLogger(WorkspacePulseFactory.class);
  
  @Autowired
  private UserRepository userRepository;
  
  private WorkspacePulseRepository pulseRepository;
  
  @Autowired
  private TodoFactory todoFactory;
  
  /**
   * Default pulse question set.
   */
  private PulseQuestionSet defaultPulseQuetionSet;
  
  private String defaultPulseQuestionSetId;
  
  private Environment environment;
  
  @Autowired
  @Qualifier("defaultNotificationProcessor")
  private NotificationsProcessor notificationProcessor;
  
  @Autowired
  private CompanyFactory companyFactory;
  
  @Autowired
  private UserFactory userFactory;
  
  @Autowired
  @Qualifier("notificationLog")
  LogGateway logGateway;
  
  /**
   * Constructor to initialize the default question set.
   */
  @Inject
  public WorkspacePulseFactory(Environment environment, WorkspacePulseRepository pulseRepository) {
    this.pulseRepository = pulseRepository;
    this.environment = environment;
    
    // load the default question set
    load();
  }
  
  /**
   * Load the environment properties.
   */
  private void load() {
    String defaultPulseQuestionSetName = environment
        .getProperty("workspace.pulse.default.questionSet");
    defaultPulseQuetionSet = pulseRepository
        .findPulseQuestionSetByName(defaultPulseQuestionSetName);
    if (log.isDebugEnabled()) {
      log.debug("Setting default pulseQuestion set :" + defaultPulseQuetionSet);
    }
    if (defaultPulseQuetionSet == null) {
      ObjectMapper om = new ObjectMapper();
      try {
        defaultPulseQuetionSet = om.readValue(
            this.getClass().getClassLoader().getResourceAsStream("defaultPulseQuestions.json"),
            PulseQuestionSet.class);
        log.debug("Saved the pulse question set !!!");
        LocalDate now = LocalDate.now();
        defaultPulseQuetionSet.setCreatedOn(now);
        defaultPulseQuetionSet.setUpdatedOn(now);
        // add the default question set to the database
        pulseRepository.savePulseQuestionSet(defaultPulseQuetionSet);
      } catch (IOException e) {
        log.fatal("Could not load the default pulse question set from file !!!", e);
        throw new SPException("Could not load default pulse question set from file !!!");
      }
    }
    this.defaultPulseQuestionSetId = defaultPulseQuetionSet.getId();
  }
  
  /**
   * Gets the default pulse question set.
   * 
   * @return the default pulse question set
   */
  public PulseQuestionSet getDefaultPulseQuestionSet() {
    return defaultPulseQuetionSet;
  }
  
  /**
   * Start the pulse for the given pulse start request.
   * 
   * @param startDate
   *          - start date
   * @param endDate
   *          - end date
   * @param companyId
   *          - company
   * @param pulseQuestionSetId
   *          - pulse question set
   * @return the newly created pulse request
   */
  public PulseRequest startPulse(LocalDate startDate, LocalDate endDate, String companyId,
      String pulseQuestionSetId) {
    
    // validate start date and end date
    if (startDate.isBefore(LocalDate.now())) {
      throw new InvalidRequestException(
          "Cannot start pulse in the past, invalid pulse start date !!!");
    }
    
    Period period = Period.between(startDate, endDate);
    if (period.getDays() < 1) {
      throw new InvalidRequestException(
          "Invalid start date and end date must be at least one day apart !!!");
    }
    
    PulseQuestionSet pulseQuestionSet = pulseRepository
        .findPulseQuestionSetById(pulseQuestionSetId);
    
    if (pulseQuestionSet == null) {
      throw new InvalidRequestException("Pulse question set not found.");
    }
    
    if (pulseQuestionSet.getStatus() != PulseQuestionSetStatus.Active) {
      throw new InvalidRequestException("Pulse not active.");
    }
    
    if (!pulseQuestionSet.isForAll()) {
      if (!pulseQuestionSet.getCompanyId().contains(companyId)) {
        throw new InvalidRequestException("Pulse not avaialble for company.");
      }
    }
    
    PulseRequest pulseRequest = new PulseRequest();
    final Company company = companyFactory.getCompany(companyId);
    
    // creating the pulse request
    pulseRequest.setCompanyId(companyId);
    pulseRequest.setStartDate(startDate);
    pulseRequest.setEndDate(endDate);
    pulseRequest.setPulseQuestionSetId(pulseQuestionSetId);
    
    // creating the request in the database
    pulseRepository.createPulseRequest(pulseRequest);
    
    // add the pulse task for the company
    addPulseTask(company, pulseRequest, pulseQuestionSet);
    return pulseRequest;
  }
  
  /**
   * Add a pulse task for the company.
   * 
   * @param company
   *          - company to update
   * @param pulseRequest
   *          - the pulse request
   */
  private void addPulseTask(Company company, PulseRequest pulseRequest,
      PulseQuestionSet pulseQuestionSet) {
    // add the pulse request to the users of the company
    final List<User> allMembersForCompany = userFactory.getAllMembersForCompany(company.getId());
    final HashMap<String, Object> params = new HashMap<String, Object>();
    params.put(Constants.PARAM_END_DATE, MessagesHelper.formatDate(pulseRequest.getEndDate()));
    final String pulseQuestionSetName = pulseQuestionSet.getName();
    params.put(Constants.PARAM_PULSE_NAME, pulseQuestionSetName);
    final String pulseRequestId = pulseRequest.getId();
    params.put(Constants.PARAM_PULSE_REQUEST_ID, pulseRequestId);
    params.put(Constants.PARAM_DAY,
        pulseRequest.getEndDate().getDayOfWeek()
            .getDisplayName(TextStyle.FULL, Locale.getDefault()));
    params.put(Constants.PARAM_NOTIFICATION_URL_PARAM, pulseRequestId);
    final TodoRequest todoRequest = TodoRequest.newPulseRequest(pulseQuestionSetName,
        pulseRequestId, pulseRequest.getEndDate());
    allMembersForCompany.stream().filter(u -> !u.isDeactivated())
        .forEach(u -> addTodoAndSendNotification(params, pulseQuestionSetName, todoRequest, u));
  }

  private void addTodoAndSendNotification(final HashMap<String, Object> params,
      final String pulseQuestionSetName, final TodoRequest todoRequest, User user) {
    try {
      todoFactory.addTodo(user, todoRequest);
      params.put(Constants.PARAM_NOTIFICATION_MESSAGE, MessagesHelper.getMessage(
          LogActionType.PulseAssessmentStart.getMessageKey(),user.getLocale(), pulseQuestionSetName));
      notificationProcessor.process(NotificationType.PulseStart, user, user, params, false);
    } catch (Exception e) {
      log.warn("Error adding user to the pulse. " + user.getEmail(), e);
    }
  }
  
  /**
   * Process the pulse results for the given set of members.
   * 
   * @param pulseRequestId
   *          - the pulse request
   * @param memberList
   *          - the members
   * @return the pulse result
   */
  public PulseResults getPulseResult(String pulseRequestId, String pulseQuestionSetId,
      List<User> memberList) {
    
    List<PulseAssessment> pulseAssessments = getPulseResults(pulseRequestId, memberList);
    
    // get the pulse results
    PulseResults pulseResult = processPulseResult(pulseQuestionSetId, pulseAssessments);
    
    // get total number of members expected to take the pulse
    pulseResult.setNumberOfMembers(memberList.size());
    
    return pulseResult;
  }

  /**
   * Get all the pulse results for the given pulse request.
   * 
   * @param pulseRequest
   *          - the pulse request
   * @return the pulse result
   */
  public PulseResults getPulseResult(PulseRequest pulseRequest) {
    // get the list of assessments for the company
    List<PulseAssessment> pulseAssessments = pulseRepository.getAllPulseAssessments(pulseRequest
        .getId());
    
    // get the pulse results
    PulseResults pulseResult = processPulseResult(pulseRequest.getPulseQuestionSetId(),
        pulseAssessments);
    
    // get the number of members of the company
    pulseResult.setNumberOfMembers(userRepository.getNumberOfActiveMembers(pulseRequest
        .getCompanyId(),SPPlanType.Primary));
    
    // copy the rest of the information like start date, end date etc.
    BeanUtils.copyProperties(pulseRequest, pulseResult);
    
    // storing the pulse request
    pulseResult.setPulseRequestId(pulseRequest.getId());
    
    return pulseResult;
  }
  
  /**
   * Processes the pulse assessments and creates the results.
   * 
   * @param pulseQuestionSetId
   *          - pulse question set
   * @param pulseAssessments
   *          - pulse assessment
   * @return the pulse results
   */
  private PulseResults processPulseResult(String pulseQuestionSetId,
      List<PulseAssessment> pulseAssessments) {
    
    PulseQuestionSet questionSet = defaultPulseQuetionSet;
    
    // get the question set
    // check if the question set is same as the default question set
    if (!defaultPulseQuestionSetId.equals(pulseQuestionSetId)) {
      questionSet = pulseRepository.findPulseQuestionSetById(pulseQuestionSetId);
      if (questionSet == null) {
        throw new InvalidRequestException("Question set not found for question set id :"
            + pulseQuestionSetId);
      }
    }
    
    // process each of the assessments
    final Map<String, PulseScore> pulseScore = new HashMap<String, PulseScore>();
    
    // fill the pulse score with the zero values
    final Map<String, List<PulseQuestionBean>> questions = questionSet.getQuestions();
    for (String key : questions.keySet()) {
      List<PulseQuestionBean> questionList = questions.get(key);
      PulseScore tempPulseScore = new PulseScore();
      pulseScore.put(key, tempPulseScore);
      List<List<PulseScoreBean>> summationList = new ArrayList<List<PulseScoreBean>>();
      tempPulseScore.setSummationList(summationList);
      for (PulseQuestionBean questionBean : questionList) {
        List<PulseScoreBean> questionSummation = new ArrayList<PulseScoreBean>();
        questionBean.getOptionsList().forEach(q -> questionSummation.add(new PulseScoreBean()));
        summationList.add(questionSummation);
      }
    }
    
    for (PulseAssessment assessment : pulseAssessments) {
      processAssessment(pulseScore, questions, assessment);
    }
    
    // create the pulse results
    PulseResults pulseResults = new PulseResults();
    final int numberOfRespondents = pulseAssessments.size();
    
    if (numberOfRespondents > 0) {
      // average out the score
      pulseScore.values().forEach(pScore -> processScoreAverage(pScore, numberOfRespondents));
    }
    // set the pulse score
    pulseResults.setPulseScore(pulseScore);
    // set the number of respondents
    pulseResults.setNumberOfRespondents(numberOfRespondents);
    return pulseResults;
  }
  
  private void processScoreAverage(PulseScore pulseScore, double numberOfRespondents) {
    final double score = (pulseScore.getTotalScore().getScore() / pulseScore.getSummationList()
        .size()) / numberOfRespondents;
    pulseScore.setScore(BigDecimal.valueOf(score)
        .setScale(Constants.PULSE_PRECISION, Constants.ROUNDING_MODE).doubleValue());
    // also average out the pulse score
    pulseScore.getSummationList().forEach(
        questionSummationList -> {
          questionSummationList.forEach(psb -> {
            BigDecimal percentRespondents = new BigDecimal(
                ((psb.getScore() * 100) / numberOfRespondents)).setScale(Constants.PULSE_PRECISION,
                Constants.ROUNDING_MODE);
            psb.setPercentResponded(percentRespondents.doubleValue());
          });
        });
  }
  
  /**
   * Process the assessment for the user.
   * 
   * @param pulseScore
   *          - the pulse score
   * @param questions
   *          - questions
   * @param assessment
   *          - assessment
   */
  private void processAssessment(Map<String, PulseScore> pulseScore,
      Map<String, List<PulseQuestionBean>> questions, PulseAssessment assessment) {
    
    // get the assessment response
    Map<String, List<PulseSelection>> assessmentResponse = assessment.getAssessment();
    
    // iterate over all the questions
    for (String key : questions.keySet()) {
      // get the questions list to iterate
      List<PulseQuestionBean> questionList = questions.get(key);
      // get the response list
      List<PulseSelection> responseList = assessmentResponse.get(key);
      
      // get the category score to update
      final PulseScore categoryPulseScore = pulseScore.get(key);
      // get the summation list
      List<List<PulseScoreBean>> summationList = categoryPulseScore.getSummationList();
      // iterate over the questions
      for (int i = 0; i < questionList.size(); i++) {
        // mark the question selection
        final PulseQuestionBean questionBean = questionList.get(i);
        PulseSelection pulseSelection = responseList.get(i);
        List<PulseScoreBean> questionSelectionMarking = summationList.get(i);
        final int selectionIndex = pulseSelection.getSelectionIndex();
        PulseScoreBean scoreBean = questionSelectionMarking.get(selectionIndex);
        // update the score
        scoreBean.increment(1);
        // increment the total score for the category
        QuestionOptions selectedQuestionOption = questionBean.getOptionsList().get(selectionIndex);
        categoryPulseScore.getTotalScore().increment(selectedQuestionOption.getFactor());
      }
    }
  }
  
  /**
   * Process the pulse assessment.
   * 
   * @param pulseRequestId
   *          - pulse request
   * @param pulseAssessment
   *          - pulse assessment
   * @param user
   *          - user
   */
  public void processAssessment(String pulseRequestId, List<PulseAssessmentForm> pulseAssessment,
      User user) {
    
    // validate the input data
    Assert.hasText(pulseRequestId, MessagesHelper.getMessage("pulse.error.message.generic"));
    Assert.notNull(user, MessagesHelper.getMessage("pulse.error.message.generic"));
    Assert.notNull(pulseAssessment, MessagesHelper.getMessage("pulse.error.message.generic"));
    
    // validate the request is still present
    PulseRequest pulseRequest = Optional.ofNullable(
        pulseRepository.findPulseRequestById(pulseRequestId)).orElseThrow(
        () -> new InvalidRequestException(MessagesHelper
            .getMessage("pulse.error.message.not.available")));
    
    if (pulseRequest.getEndDate().isBefore(LocalDate.now())) {
      throw new InvalidRequestException(
          MessagesHelper.getMessage("pulse.error.message.not.available"));
    }
    
    // get the pulse question set from the request
    PulseQuestionSet pulseQuestionSet = Optional.ofNullable(
        pulseRepository.findPulseQuestionSetById(pulseRequest.getPulseQuestionSetId()))
        .orElseThrow(
            () -> new InvalidRequestException(MessagesHelper
                .getMessage("pulse.error.message.generic")));
    
    processAssessmentAsyn(pulseRequestId, pulseAssessment, user, pulseRequest, pulseQuestionSet);
  }
  
  /**
   * Processing the assessment results asynchronously.
   * 
   * @param pulseRequestId
   *          - pulse request id
   * @param pulseAssessment
   *          - pulse assessment
   * @param user
   *          - user
   * @param pulseRequest
   *          - pulse request
   * @param pulseQuestionSet
   *          - pulse question set
   */
  @Async
  private void processAssessmentAsyn(String pulseRequestId,
      List<PulseAssessmentForm> pulseAssessment, User user, PulseRequest pulseRequest,
      PulseQuestionSet pulseQuestionSet) {
    // iterate over all the questions and then create a new pulse assessment
    Map<String, List<PulseQuestionBean>> questions = pulseQuestionSet.getQuestions();
    PulseAssessment assessmentBean = new PulseAssessment(pulseRequestId, user.getId());
    Map<String, List<PulseSelection>> assessmentMap = new HashMap<String, List<PulseSelection>>(3);
    for (PulseAssessmentForm assessmentForm : pulseAssessment) {
      
      // get the question bean
      List<PulseQuestionBean> questionBeanList = Optional.ofNullable(
          questions.get(assessmentForm.getCategoryName())).orElseThrow(
          () -> new InvalidRequestException("Category :" + assessmentForm.getCategoryName()
              + ": not found in question set :" + pulseRequest.getPulseQuestionSetId()));
      
      // get all the selections from the member
      final int[] questionSelectionIndex = assessmentForm.getQuestionSelectionIndex();
      
      // create the pulse selection to store all the selections by the user
      List<PulseSelection> pulseSelectionList = new ArrayList<PulseSelection>();
      for (PulseQuestionBean questionBean : questionBeanList) {
        try {
          int selectionIndex = questionSelectionIndex[questionBean.getNumber()];
          pulseSelectionList.add(new PulseSelection(questionBean.getNumber(), selectionIndex));
        } catch (IndexOutOfBoundsException e) {
          log.warn("Processing the save request !!!", e);
          throw new InvalidRequestException("Question :" + questionBean.getNumber()
              + " not answered for the category :" + assessmentForm.getCategoryName());
        }
      }
      assessmentMap.put(assessmentForm.getCategoryName(), pulseSelectionList);
    }
    assessmentBean.setAssessment(assessmentMap);
    
    // assert if all the categories have been answered
    if (assessmentMap.size() != questions.size()) {
      throw new InvalidRequestException("Following categories have not been answered :"
          + questions.keySet().stream().filter(k -> !assessmentMap.containsKey(k))
              .collect(Collectors.joining(" ,")));
    }
    
    // store the assessment into the db
    try {
      pulseRepository.savePulseAssessment(assessmentBean);
    } catch (DuplicateKeyException e) {
      log.warn("Could not store the assessment for the member !!!", e);
      throw new InvalidRequestException("Unable to store the assessment for the member !!!", e);
    }
    
    // removing the pulse request
    todoFactory.remove(user, pulseRequestId);

    /*
     * Remving the activity creation when user completes pulse.
     * https://surepeople.mydonedone.com/issuetracker/projects/46273/issues/49
     */
    // adding the activity
//    LogRequest logRequest = new LogRequest(LogActionType.PulseAssessmentComplete, user);
//    logRequest.addActivityMessage(MessagesHelper.getMessage(
//        LogActionType.PulseAssessmentComplete.getActivityKey(), pulseQuestionSet.getName()));
//    logGateway.logActivity(logRequest);
  }
  
  /**
   * This method processes the pulse end request.
   * 
   * @param pulseRequest
   *          - pulse request
   * @param saveResults
   *          - flag to indicate if results should be saved
   * @return the pulse result
   */
  public PulseResults finishPulse(PulseRequest pulseRequest, boolean saveResults) {
    
    // get the pulse result
    PulseResults pulseResult = getPulseResult(pulseRequest);
    return finishPulse(pulseRequest, saveResults, pulseResult);
  }
  
  /**
   * This method processes the pulse end request.
   * 
   * @param pulseRequest
   *          pulse request
   * @param saveResults
   *          save results flag
   * @param pulseResult
   *          pulse result.
   * @return the pulse result.
   */
  public PulseResults finishPulse(PulseRequest pulseRequest, boolean saveResults,
      PulseResults pulseResult) {
    if (saveResults) {
      // save the pulse result
      pulseRepository.savePulseResult(pulseResult);
    }
    // remove the pulse request
    delete(pulseRequest);
    
    final String pulseRequestId = pulseRequest.getId();
    
    List<User> allMembersForCompany = userFactory.getAllMembersForCompany(pulseRequest
        .getCompanyId());
    allMembersForCompany.forEach(u -> todoFactory.remove(u, pulseRequestId));
    
    return pulseResult;
  }
  
  /**
   * Get the pulse question set.
   * 
   * @param pulseQuestionSetId
   *          - pulse question set
   * @return - the pulse question set
   */
  public PulseQuestionSet getPulseQuestionSet(String pulseQuestionSetId) {
    
    PulseQuestionSet pulseQuestionSet;
    if (StringUtils.isNotBlank(pulseQuestionSetId)) {
      // get the pulse question set for the given question set id
      pulseQuestionSet = pulseRepository.findPulseQuestionSetById(pulseQuestionSetId);
      
      if (pulseQuestionSet == null) {
        if (defaultPulseQuestionSetId.equals(pulseQuestionSetId)) {
          load();
          pulseQuestionSet = defaultPulseQuetionSet;
        }
      }
      
      // check if the pulse question set is null
      if (pulseQuestionSet == null) {
        throw new InvalidRequestException("Question set not found for :" + pulseQuestionSetId);
      }
    } else {
      pulseQuestionSet = defaultPulseQuetionSet;
    }
    return pulseQuestionSet;
  }
  
  /**
   * Get the list of pulse questions applicable for the given company.
   * 
   * @param companyId
   *          - company id
   * @return the list of pulse questions
   */
  public List<PulseQuestionSetListDTO> getPulseQuestionSets(String companyId) {
    List<PulseQuestionSet> findPulseQuestionSetsFor = pulseRepository
        .findPulseQuestionSetsFor(companyId);
    return findPulseQuestionSetsFor
        .stream()
        .filter(
            pqs -> ((pqs.getStatus() == PulseQuestionSetStatus.Active) 
                && (pqs.getQuestionSetType() == QuestionSetType.Company)))
        .map(PulseQuestionSetListDTO::new).collect(Collectors.toList());
  }
  
  /**
   * Send the reminder to the people of the company to finish the pulse.
   * 
   * @param pulseRequestId
   *          - pulse request id
   */
  public void sendReminder(String pulseRequestId) {
    PulseRequest pulseRequest = pulseRepository.findPulseRequestById(pulseRequestId);
    Assert.notNull(pulseRequest, "Pulse request not found.");
    PulseQuestionSet pulseQuestionSetId = pulseRepository.findPulseQuestionSetById(pulseRequest
        .getPulseQuestionSetId());
    
    final HashMap<String, Object> params = new HashMap<String, Object>();
    params.put(Constants.PARAM_END_DATE, MessagesHelper.formatDate(pulseRequest.getEndDate()));
    params.put(Constants.PARAM_PULSE_NAME, pulseQuestionSetId.getName());
    params.put(Constants.PARAM_DAY,
        pulseRequest.getEndDate().getDayOfWeek()
            .getDisplayName(TextStyle.FULL, Locale.getDefault()));
    params.put(Constants.PARAM_PULSE_REQUEST_ID, pulseRequestId);
    List<User> allMembersForCompany = userFactory.getAllMembersForCompany(pulseRequest
        .getCompanyId());
    allMembersForCompany
        .stream()
        .filter(u -> todoFactory.hasTask(u, pulseRequestId))
        .forEach(
            u -> notificationProcessor.process(NotificationType.PulseReminder, u, u, params, false));
  }
  
  /**
   * Factory method to cancel a currently executing pulse.
   * 
   * @param pulseRequestId
   *          - pulse request id
   * @param saveResults
   *          - flag to indicate if the results need to be saved
   */
  public void cancelPulse(String pulseRequestId, boolean saveResults) {
    // get and validate the pulse request
    PulseRequest pulseRequest = pulseRepository.findPulseRequestById(pulseRequestId);
    Assert.notNull(pulseRequest, "Pulse request not found.");
    
    // finish the pulse
    PulseResults pulseResult = finishPulse(pulseRequest, saveResults);
    // update the end date for the pulse
    pulseResult.setEndDate(LocalDate.now());
    if (saveResults) {
      pulseRepository.savePulseResult(pulseResult);
    }
  }
  
  /**
   * Get the pulse request for the company.
   * 
   * @param companyId
   *          - company id
   * @return the pulse request
   */
  public PulseRequest getPulseRequest(String companyId) {
    return pulseRepository.findPulseRequest(companyId);
  }
  
  /**
   * Delete the pulse request.
   * 
   * @param pulseRequest
   *          - pulse request
   */
  public void delete(PulseRequest pulseRequest) {
    pulseRepository.removePulseRequest(pulseRequest);
  }
  
  /**
   * Delete the pulse result.
   * 
   * @param pulseResult
   *          - pulse result to delete
   */
  public void delete(PulseResults pulseResult) {
    pulseRepository.delete(pulseResult);
  }

  /**
   * Delete the pulse assessment.
   * 
   * @param pulseResult
   *          - pulse result to delete
   */
  public void delete(PulseAssessment pulseAssessment) {
    pulseRepository.delete(pulseAssessment);
  }
  
  /**
   * Get all the pulse results for the company.
   * 
   * @param companyId
   *          - company id
   * @return the pulse results
   */
  public List<PulseResults> getAllPulseResults(String companyId) {
    return pulseRepository.getAllPulseResults(companyId);
  }
  
  /**
   * Get the pulse results for the given members.
   * 
   * @param pulseRequestId
   *            - pulse request
   * @param memberList
   *            - member list
   * @return
   *    the list of pulse results found
   */
  public List<PulseAssessment> getPulseResults(String pulseRequestId, List<User> memberList) {
    // get the assessments for the question and the list of members
    List<PulseAssessment> pulseAssessments = pulseRepository.getAllPulseAssessments(pulseRequestId,
        memberList.stream().map(User::getId).collect(Collectors.toList()));
    return pulseAssessments;
  }
  
}