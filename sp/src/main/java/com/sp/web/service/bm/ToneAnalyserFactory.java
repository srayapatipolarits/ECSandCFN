package com.sp.web.service.bm;

import com.ibm.watson.developer_cloud.tone_analyzer.v3.ToneAnalyzer;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.ToneAnalysis;
import com.sp.web.dto.bm.ta.ToneAnalysisRecordDTO;
import com.sp.web.dto.bm.ta.UserToneAnalysisDTO;
import com.sp.web.model.User;
import com.sp.web.model.bm.ta.ToneAnalysisRecord;
import com.sp.web.model.bm.ta.ToneRequestType;
import com.sp.web.model.bm.ta.UserToneAnalysis;
import com.sp.web.product.CompanyFactory;
import com.sp.web.repository.bm.ta.ToneAnalysisRepository;
import com.sp.web.repository.bm.ta.UserToneAnalysisRepository;
import com.sp.web.user.UserFactory;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

/**
 * @author Dax Abraham
 *
 *         The factory class for all the tone analysis functionality.
 */
@Component
public class ToneAnalyserFactory {
  
  private static final Logger log = Logger.getLogger(ToneAnalyserFactory.class);
  private int count;
  private static final int MAX_LIMIT = 500;
  private boolean limitReached = false;
  private LocalDate limitReachedDate = null;
  private ToneAnalyzer service;
  
  @Autowired
  ToneAnalysisRepository toneAnalysisRepository;

  @Autowired
  UserToneAnalysisRepository userToneAnalysisRepository;
  
  @Autowired
  UserFactory userFactory;
  
  @Autowired
  CompanyFactory companyFactory;
  
  /**
   * Constructor to create the tone analyzer service.
   * 
   * @param env
   *        - environment for user name and password
   */
  @Inject
  public ToneAnalyserFactory(Environment env) {
    service = new ToneAnalyzer(ToneAnalyzer.VERSION_DATE_2016_05_19);
    String username = env.getProperty("ta.username");
    service.setUsernameAndPassword(username, env.getProperty("ta.password"));
    limitReached = false;
    limitReachedDate = LocalDate.now();
    count = 0;
  }
  
  /**
   * Analyze the given text for the given user.
   * 
   * @param text
   *          - text
   * @param user
   *          - user
   */
  @Async
  public void process(ToneRequestType type, String text, User user) {
    try {
      if (filterText(text)) {
        return;
      }
      
      if (!limitReached) {
        doAnalysis(type, text, user);
        count++;
        if (count == MAX_LIMIT) {
          limitReached = true;
        }
      } else {
        LocalDate now = LocalDate.now();
        if (!now.equals(limitReachedDate)) {
          limitReached = false;
          limitReachedDate = now;
          count = 0;
          doAnalysis(type, text, user);
        }
      }
    } catch (Exception e) {
      log.warn("Unable to analyze the message.", e);
    }
  }

  /**
   * Filter the given word based on the different criterion.
   * 
   * @param text
   *          - text
   * @return
   *    true if filter else false
   */
  private boolean filterText(String text) {
    if (text == null) {
      return true;
    }
    
    String[] split = text.trim().split(" ");
    if (split.length <= 1) {
      return true;
    }
    
    return false;
  }

  /**
   * Performing the analysis on the provided text.
   * @param type 
   *          - source of request
   * @param text
   *          - text
   * @param user
   *          - user
   */
  private void doAnalysis(ToneRequestType type, String text, User user) {
    // Analyzing the tone for the text
    ToneAnalysis analysis = service.getTone(text, null).execute();
    // creating and saving the tone analysis record
    ToneAnalysisRecord toneAnalysisRecord = new ToneAnalysisRecord(user, analysis, type, text);
    toneAnalysisRepository.save(toneAnalysisRecord);
    // updating the user aggregate
    UserToneAnalysis userToneAnalysis = getUserToneAnalysis(user);
    userToneAnalysis.add(toneAnalysisRecord);
    updateUserToneAnalysis(userToneAnalysis);
    
    if (log.isDebugEnabled()) {
      log.debug(analysis);
    }
  }

  /**
   * Update the user tone analysis.
   * 
   * @param userToneAnalysis
   *            - user tone analysis
   */
  public void updateUserToneAnalysis(UserToneAnalysis userToneAnalysis) {
    userToneAnalysisRepository.save(userToneAnalysis);
  }

  /**
   * Get the user tone analysis for the given user.
   * 
   * @param user
   *          - user
   * @return
   *    the user tone analysis
   */
  public synchronized UserToneAnalysis getUserToneAnalysis(User user) {
    UserToneAnalysis userToneAnalysis;
    final String userToneAnalysisId = user.getUserToneAnalysisId();
    if (userToneAnalysisId == null) {
      userToneAnalysis = new UserToneAnalysis(user);
      userToneAnalysisRepository.save(userToneAnalysis);
      user.setUserToneAnalysisId(userToneAnalysis.getId());
      userFactory.updateUserAndSession(user);
    } else {
      userToneAnalysis = userToneAnalysisRepository.findById(userToneAnalysisId);
    }
    return userToneAnalysis;
  }

  /**
   * Get all the aggregate tone analysis for the given company.
   * 
   * @param companyId
   *          - company id
   * @return
   *     the list of aggregate tone analysis for the given company
   */
  public List<UserToneAnalysisDTO> getAll(String companyId) {
    companyFactory.getCompany(companyId);
    List<UserToneAnalysisDTO> dtoList = new ArrayList<UserToneAnalysisDTO>();
    for (UserToneAnalysis userToneAnalysis : userToneAnalysisRepository.findAllByCompanyId(companyId)) {
      User user = userFactory.getUser(userToneAnalysis.getUserId());
      if (user != null) {
        dtoList.add(new UserToneAnalysisDTO(user, userToneAnalysis));
      }
    }
    return dtoList;
  }

  /**
   * Get all the user tone analysis records.
   * 
   * @param userId
   *          - user id
   * @return
   *    the list of user tone analysis records
   */
  public List<ToneAnalysisRecordDTO> getAllUserToneAnalysis(String userId) {
    List<ToneAnalysisRecord> toneAnalysisList = toneAnalysisRepository.findAllByUserId(userId);
    return toneAnalysisList.stream().map(ToneAnalysisRecordDTO::new).collect(Collectors.toList());
  }
  
}
