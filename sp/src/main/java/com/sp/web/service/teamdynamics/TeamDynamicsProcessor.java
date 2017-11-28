package com.sp.web.service.teamdynamics;

import com.sp.web.assessment.questions.CategoryType;
import com.sp.web.dto.teamdynamics.TeamDynamicsResultDTO;
import com.sp.web.model.User;
import com.sp.web.model.teamdynamics.TeamDynamicsLoadData;

/**
 * TeamDynamicsProcessor interface provides the services to load and process the team dynamcis.
 * 
 * @author pradeepruhil
 *
 */
public interface TeamDynamicsProcessor {
  
  /**
   * loadData service will load the data for the user.
   * 
   * @param user
   *          for which data is to be loaded into the team dynamics.
   * @param categoryType
   *          category type
   * 
   * @param dynamicsLoadData
   *          is the dynamics data.
   */
  void loadData(User user, TeamDynamicsLoadData dynamicsLoadData, CategoryType categoryType);
  
  /**
   * processResult will load the team dynamics result.
   * 
   * @param dynamicsLoadData
   *          load data for the team dynamics.
   * @param categoryType
   *          category type.
   * @param dynamicsResultDTO
   *          is the result dto.
   */
  void processResult(TeamDynamicsLoadData dynamicsLoadData,
      TeamDynamicsResultDTO dynamicsResultDTO, CategoryType categoryType);
  
}
