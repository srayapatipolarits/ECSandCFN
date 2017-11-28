package com.sp.web.scheduler.competency;

import com.sp.web.controller.admin.competency.AdminCompetencyControllerHelper;
import com.sp.web.controller.competency.CompetencyFactory;
import com.sp.web.model.User;
import com.sp.web.model.competency.CompetencyEvaluation;
import com.sp.web.model.competency.EvaluationType;
import com.sp.web.model.competency.UserCompetency;
import com.sp.web.model.competency.UserCompetencyEvaluation;
import com.sp.web.model.competency.UserCompetencyEvaluationScore;
import com.sp.web.model.todo.CompetencyTodoRequest;
import com.sp.web.model.todo.TodoRequest;
import com.sp.web.model.todo.UserTodoRequests;
import com.sp.web.service.todo.TodoFactory;
import com.sp.web.user.UserFactory;
import com.sp.web.utils.GenericUtils;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Dax Abraham
 * 
 *         The scheduler class that runs every day to send out the notifications etc.
 */
@Component
public class CompetencyScheduler {
  
  private static final Logger log = Logger.getLogger(CompetencyScheduler.class);
  
  /*
   * The number of days after which notifications will be sent daily.
   */
  private static final int DAILY_NOTIFICATION_DAYS_LEFT = 5;
  
  private static final int SELF_NOTIFICATION_DAYS_MOD = 3;
  
  private static final int MANAGER_NOTIFICATION_DAYS_MOD = 5;
  
  @Autowired
  private Environment enviornment;
  
  @Autowired
  private CompetencyFactory competencyFactory;
  
  @Autowired
  private UserFactory userFactory;
  
  @Autowired
  private AdminCompetencyControllerHelper helper;
  
  @Autowired
  private TodoFactory todoFactory;
  
  /**
   * This method runs every day and checks the competency evaluations and sends out reminders as
   * required.
   */
  @Scheduled(cron = "${competency.scheduler}")
  public void processCompetnecyEvaluations() {
    
    // run only on job server
    if (!GenericUtils.isJobServerNode(enviornment)) {
      return;
    }
    
    // get all the competency evaluations
    List<CompetencyEvaluation> currentCompetencyEvaluations = competencyFactory
        .getCurrentCompetencyEvaluations();
    
    // iterate over all and then process the notifications
    currentCompetencyEvaluations.forEach(this::process);
  }
  
  /**
   * Process the competency evaluation.
   * 
   * @param competencyEvaluation
   *          - competency evaluation
   */
  private void process(CompetencyEvaluation competencyEvaluation) {
    try {
      // return if the date is past
      if (LocalDate.now().isAfter(competencyEvaluation.getEndDate().toLocalDate())) {
        log.warn("Competency evaluation end date in the past. "
            + competencyEvaluation.getCompanyId());
        return;
      }
      
      // get the period to calculate the notification flags
      long dailyPeriod = ChronoUnit.DAYS.between(LocalDate.now(), competencyEvaluation
          .getEndDate().toLocalDate());
      long period = ChronoUnit.DAYS.between(competencyEvaluation.getStartDate().toLocalDate(),
          LocalDate.now());
      
      boolean sendDaily = (dailyPeriod <= DAILY_NOTIFICATION_DAYS_LEFT);
      boolean sendSelf = (period % SELF_NOTIFICATION_DAYS_MOD == 0);
      boolean sendManager = (period % MANAGER_NOTIFICATION_DAYS_MOD == 0);
      
      // get the evaluation types
      final List<EvaluationType> requiredEvaluationList = competencyEvaluation
          .getRequiredEvaluationList();
      final Set<String> managersReminderList = new HashSet<>();
      
      final String competencyEvaluationId = competencyEvaluation.getId();
      competencyEvaluation
          .getUserIds()
          .stream()
          .forEach(
              userId -> sendNotification(userId, requiredEvaluationList, sendDaily, sendManager,
                  sendSelf, managersReminderList, competencyEvaluationId));
      
      // send a consolidated reminder to all the managers
      final String companyId = competencyEvaluation.getCompanyId();
      managersReminderList
          .forEach(m -> helper.sendReminder(null, false, false, m, true, companyId));
    } catch (Exception ex) {
      log.error("Reminders emails are not sent for comptency " + competencyEvaluation, ex);
    }
  }
  
  /**
   * Send notification for the given evaluation result.
   * 
   * @param userId2
   *          - evaluation result
   * @param requiredEvaluationList
   *          - required evaluation list
   * @param sendDaily
   *          - flag for send daily
   * @param sendEvaluationReminder
   *          - flag for send evaluation reminder
   * @param sendInitiateReminder
   *          - flag to send initiation reminder
   * @param competencyEvaluationId
   *          - competency evaluation id
   */
  private void sendNotification(String userId,
      List<EvaluationType> requiredEvaluationList, boolean sendDaily,
      boolean sendEvaluationReminder, boolean sendInitiateReminder,
      Set<String> managerReminderList, String competencyEvaluationId) {
    
    User user = userFactory.getUser(userId);
    if (user != null) {
      UserTodoRequests userTodoRequests = todoFactory.getUserTodoRequests(user);
      if (userTodoRequests != null) {
        TodoRequest todoRequest = userTodoRequests.findRequest(competencyEvaluationId,
            competencyEvaluationId);
        if (todoRequest != null && todoRequest instanceof CompetencyTodoRequest) {
          switch (((CompetencyTodoRequest) todoRequest).getTaskType()) {
          case CompetencyEvaluationInitiatePeer:
          case CompetencyEvaluationInitiateManagerPeer:
          case CompetencyEvaluationInitiateManagerSelf:
          case CompetencyEvaluationInitiateManager:
          case CompetencyEvaluationInitiate:
            if (sendDaily || sendInitiateReminder) {
              helper.sendReminder(user, true, false);
            }
            break;
          
          case CompetencyEvaluationInitiateSelfPeer:
          case CompetencyEvaluationSelf:
            if (sendDaily || sendEvaluationReminder) {
              helper.sendReminder(user, false, true);
            }
            break;
          
          default:
            break;
          }
        }
        
        // check for manager evaluation
        if (requiredEvaluationList.contains(EvaluationType.Manager)) {
          UserCompetency userCompetency = competencyFactory.getUserCompetency(userId);
          if (userCompetency != null) {
            UserCompetencyEvaluation evaluation = userCompetency.getEvaluation(competencyEvaluationId);
            if (evaluation != null) {
              UserCompetencyEvaluationScore managerEvaluation = evaluation.getManager();
              if (managerEvaluation != null
                  && managerEvaluation.getCompetencyEvaluationScoreDetailsId() == null
                  && (sendEvaluationReminder || sendDaily)) {
                managerReminderList.add(managerEvaluation.getReviewer().getId());
              }
            }
          }
        }
      }
    }
  }
  
}
