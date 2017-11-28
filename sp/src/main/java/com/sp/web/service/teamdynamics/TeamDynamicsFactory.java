package com.sp.web.service.teamdynamics;

import com.sp.web.assessment.questions.CategoryType;
import com.sp.web.dto.teamdynamics.TeamDynamicsResultDTO;
import com.sp.web.form.teamdynamics.TeamDynamicForm;
import com.sp.web.model.User;
import com.sp.web.model.teamdynamics.TeamDynamicsLoadData;
import com.sp.web.service.hiring.user.HiringUserFactory;
import com.sp.web.user.UserFactory;
import com.sp.web.utils.ApplicationContextUtils;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * TeamDynamicsFactory will peform the team dynamics and will give the result back.
 * 
 * @author pradeepruhil
 *
 */
@Component
public class TeamDynamicsFactory {
  
  /** Initialzing the logger. */
  private Logger log = Logger.getLogger(TeamDynamicsFactory.class);
  @Autowired
  private UserFactory userFactory;
  
  @Autowired
  private HiringUserFactory hiringUserFactory;
  
  /**
   * getTeamDynamcis will get the team dynamics for the users.
   * 
   * @param teamDynamicForm
   *          teamDynamcisForm.
   * @return the TeamDynamcisResultDTO .
   */
  public TeamDynamicsResultDTO getTeamDynamics(TeamDynamicForm teamDynamicForm) {
    
    /* get users for which team dynamics is to be performed. */
    List<User> users = getUsers(teamDynamicForm);
    
    TeamDynamicsLoadData loadData = new TeamDynamicsLoadData();
    loadData.setUsers(users);
    CategoryType[] values = CategoryType.values();
    for (User user : users) {
      for (CategoryType cat : values) {
        /* skipping acurarcy and learning style as they are not required as of now */
        if (cat == CategoryType.Accuracy || cat == CategoryType.LearningStyle) {
          continue;
        }
        TeamDynamicsProcessor processor = getProcessor(cat);
        processor.loadData(user, loadData, cat);
      }
    }
    
    /* Populate the result for the loaded data */
    
    TeamDynamicsResultDTO teamDynamicsResultDTO = new TeamDynamicsResultDTO(users);
    
    Set<CategoryType> keySet = loadData.getCategoryPairData().keySet();
    
    for (CategoryType categ : keySet) {
      TeamDynamicsProcessor processor = getProcessor(categ);
      processor.processResult(loadData, teamDynamicsResultDTO, categ);
    }
    return teamDynamicsResultDTO;
    
  }
  
  /**
   * getUsersMethod will return the user either from PA or from ERT-i.
   * 
   * @param teamDynamicForm
   *          team Dyamics form.
   * @return the list of users.
   */
  private List<User> getUsers(TeamDynamicForm teamDynamicForm) {
    List<String> userIds = teamDynamicForm.getUserIds();
    return teamDynamicForm.isPa() ? castList(hiringUserFactory.getUsers(userIds)) : userFactory
        .getUsers(userIds);
    
  }
  
  /**
   * Get the processor for the given category type.
   * 
   * @param categoryType
   *          - category type
   * @return the match processor
   */
  private TeamDynamicsProcessor getProcessor(CategoryType categoryType) {
    TeamDynamicsProcessor processor = null;
    try {
      processor = (TeamDynamicsProcessor) ApplicationContextUtils.getBean(categoryType
          .getTeamDynamicsProcessor());
    } catch (Exception e) {
      log.warn("Processor not found for " + categoryType, e);
      throw new IllegalArgumentException("Processor not found for " + categoryType, e);
    }
    return processor;
  }
  
  private <T, E> List<T> castList(List<E> list) {
    @SuppressWarnings("unchecked")
    List<T> result = (List<T>) list;
    return result;
  }
}
