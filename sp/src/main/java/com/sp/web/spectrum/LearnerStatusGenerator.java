package com.sp.web.spectrum;

import com.sp.web.account.AccountRepository;
import com.sp.web.model.BookMarkTracking;
import com.sp.web.model.Company;
import com.sp.web.model.RequestStatus;
import com.sp.web.model.SPFeature;
import com.sp.web.model.TrackingType;
import com.sp.web.model.User;
import com.sp.web.model.UserStatus;
import com.sp.web.model.goal.PracticeFeedback;
import com.sp.web.model.goal.SPNote;
import com.sp.web.model.spectrum.LearnerStatus;
import com.sp.web.model.spectrum.LearnerStausTracker;
import com.sp.web.model.spectrum.PracticeNoteFeedbackTracker;
import com.sp.web.model.spectrum.SP360LearnerStatusTracker;
import com.sp.web.model.spectrum.TimeFilter;
import com.sp.web.model.tracking.ArticlesCompletedArticleTracking;
import com.sp.web.model.tracking.RecommendedArticleTracking;
import com.sp.web.model.tracking.SP360RequestTracking;
import com.sp.web.model.tracking.TrainingLibraryVisitTracking;
import com.sp.web.product.CompanyFactory;
import com.sp.web.repository.FeedbackRepository;
import com.sp.web.repository.goal.SPNoteFeedbackRepository;
import com.sp.web.repository.growth.GrowthRepository;
import com.sp.web.repository.library.TrackingRepository;
import com.sp.web.repository.spectrum.SpectrumRepository;
import com.sp.web.repository.user.UserRepository;
import com.sp.web.utils.DateTimeUtil;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * <code>LearnerStatusGenerator</code> class holds the learning statics of a company.
 * 
 * @author pradeepruhil
 *
 */
@Component
public class LearnerStatusGenerator {
  
  private static final Logger LOG = Logger.getLogger(LearnerStatusGenerator.class);
  
  @Autowired
  private TrackingRepository trackingRepository;
  
  @Autowired
  private UserRepository userRepository;
  
  @Autowired
  private SpectrumRepository spectrumRepository;
  
  @Autowired
  private AccountRepository accountRepository;
  
  @Autowired
  private GrowthRepository growthRepository;
  
  @Autowired
  private FeedbackRepository feedbackRepository;
  
  @Autowired
  private SPNoteFeedbackRepository spNoteFeedbackRepository;
  
  @Autowired
  private CompanyFactory companyFactory;
  
//  private boolean isGrowthRequestWithCompanyDone;
  
  @Autowired
  private Environment environment;
  
  /**
   * <code>getLearnerStatus</code> method will generate the learner status for the user passed in
   * 
   * @param users
   *          for which learner status is to be retreived.
   * @return the Learner Status.
   */
  public LearnerStatus generateLearnerStatus(List<User> users, String companyId) {
    
    /* filter the users who have completed there assessment */
    
    Map<TimeFilter, LearnerStausTracker> learnerStatusTrackerMap = new LinkedHashMap<TimeFilter, LearnerStausTracker>();
    populateLearnerStatusTracker(learnerStatusTrackerMap);
    if (!CollectionUtils.isEmpty(users)) {
      List<String> usersIds = users.stream().map(usr -> usr.getId()).collect(Collectors.toList());
//      List<String> usersEmails = users.stream().map(usr -> usr.getEmail())
//          .collect(Collectors.toList());
      
      /* fetch articles completed count */
      List<ArticlesCompletedArticleTracking> allArticlesCompletedCount = trackingRepository
          .getAllArticlesCompleted(usersIds, companyId);
      
      
      
      List<BookMarkTracking> articlesBookmarkedCount = trackingRepository
          .getAllBookMarKTrackingBean(usersIds, companyId);
      
      // int pendingGrowthRequest = growthRepository.getPendingGrowthRequests(usersEmails);
      // List<? extends GrowthRequest> growthRequestTrackings = growthRepository
      // .getAllGrowthRequestsByCompany(companyId);
      //
      List<SPNote> practiceFeedbackTrackings = spNoteFeedbackRepository
          .findAllPracticeFeedbackNoteByCompany(usersIds, companyId);
      
      populateArticlesCompletedData(allArticlesCompletedCount, learnerStatusTrackerMap);
      populateBookMarkTrackingData(articlesBookmarkedCount, learnerStatusTrackerMap);
      // populateGrowthFeedbackData(growthRequestTrackings, learnerStatusTrackerMap,
      // pendingGrowthRequest);
      populatePracticeFeedbackData(practiceFeedbackTrackings, learnerStatusTrackerMap);
      
      List<RecommendedArticleTracking> articlesRecommenedCount = trackingRepository
          .findRecommendedTrackingBean(companyId, usersIds);
      populateRecommendedArticleData(articlesRecommenedCount, learnerStatusTrackerMap);
      
      Company company = companyFactory.getCompany(companyId);
      
      if (company.getFeatureList().contains(SPFeature.PrismLens)) {
        /* pending growth request */
        int pendingFeedbackRequests = feedbackRepository.getPendingFeedbackRequests(usersIds);

        List<SP360RequestTracking> sp360RequestTrackings = trackingRepository
            .findAllSp360RequestTracking(companyId, usersIds);
        populateSP360FeedbackData(sp360RequestTrackings, learnerStatusTrackerMap,
            pendingFeedbackRequests);
      }
            
      /* fetch the training library vist for the users passed */
      List<TrainingLibraryVisitTracking> trainingLibraryVistCount = trackingRepository
          .getTrainingLibraryVist(companyId, usersIds);
      populateTrainingLibraryVisitData(trainingLibraryVistCount, learnerStatusTrackerMap);
    }
    
    LOG.debug("Date populated for spectrum " + learnerStatusTrackerMap);
    
    LearnerStatus learnerStatus = new LearnerStatus();
    learnerStatus.setCompanyId(companyId);
    learnerStatus.setLearnerStatusTrackerMap(learnerStatusTrackerMap);
    return learnerStatus;
    
  }
  
  /**
   * populatePracticeFeedbackData method will populate the practice feedback data.
   * 
   * @param practiceFeedbackTrackings
   *          list of practice feedback data.
   * @param learnerStatusTrackerMap
   *          learner status map.
   */
  private void populatePracticeFeedbackData(List<SPNote> practiceFeedbackTrackings,
      Map<TimeFilter, LearnerStausTracker> learnerStatusTrackerMap) {
    if (!CollectionUtils.isEmpty(practiceFeedbackTrackings)) {
      practiceFeedbackTrackings
          .stream()
          .forEach(
              prFeedback -> {
              TimeFilter timeFilter = DateTimeUtil.getTimeFilterDate(DateTimeUtil
                  .getLocalDateFromMongoId(prFeedback.getId()));
              List<LearnerStausTracker> learnerStatusTrackers = getLearnetStatusTrackersToUpdate(
                  learnerStatusTrackerMap, timeFilter);
              learnerStatusTrackers.forEach((learnetStatussTrack) -> {
                  PracticeNoteFeedbackTracker practiceFeedbackTracker = learnetStatussTrack
                      .getPrNoteFeedbackTracker();
                  if (prFeedback instanceof PracticeFeedback) {
                    practiceFeedbackTracker.setRequestSent(practiceFeedbackTracker.getRequestSent() + 1);
                    if (((PracticeFeedback) prFeedback).getFeedbackStatus() == RequestStatus.COMPLETED) {
                      practiceFeedbackTracker.setCompleted(practiceFeedbackTracker.getCompleted() + 1);
                    } else if (((PracticeFeedback) prFeedback).getFeedbackStatus() == RequestStatus.DECLINED) {
                      practiceFeedbackTracker.setDeclined(practiceFeedbackTracker.getDeclined() + 1);
                    } else {
                      practiceFeedbackTracker.setPending(practiceFeedbackTracker.getPending() + 1);
                    }
                  } else {
                    practiceFeedbackTracker.setTotalNotes(practiceFeedbackTracker.getTotalNotes() + 1);
                  }
                  
                });
            });
      
    }
    
  }
  
  /**
   * <code>populateArticlesCompletedData</code> method will populate the date for the completd
   * articles.
   * 
   * @param articlesCompleted
   *          list tracking.
   * @param learnerStatusTrackerMap
   *          map holdign the data.
   */
  private void populateArticlesCompletedData(
      List<ArticlesCompletedArticleTracking> articlesCompleted,
      Map<TimeFilter, LearnerStausTracker> learnerStatusTrackerMap) {
    if (!CollectionUtils.isEmpty(articlesCompleted)) {
      articlesCompleted
          .stream()
          .forEach(
              arComp -> {
              TimeFilter timeFilter = DateTimeUtil.getTimeFilterDate(arComp.getAccessTime());
              List<LearnerStausTracker> learnetStatusTrackersToUpdate = getLearnetStatusTrackersToUpdate(
                  learnerStatusTrackerMap, timeFilter);
              learnetStatusTrackersToUpdate.forEach((learnetStatussTrack) -> {
                  learnetStatussTrack.setArticlesCompleted(learnetStatussTrack
                      .getArticlesCompleted() + 1);
                  populateArticlesCompletedGraph(learnetStatussTrack, arComp.getAccessTime());
                });
              
            });
    }
  }
  
  private void populateArticlesCompletedGraph(LearnerStausTracker learnerStausTracker,
      LocalDateTime localDateTime) {
    TimeFilter timeFilter = learnerStausTracker.getFilter();
    SortedMap<String, Integer> graphs = learnerStausTracker.getGraphs();
    if (graphs == null) {
      graphs = new TreeMap<String, Integer>();
      if (timeFilter == TimeFilter.DAY) {
        for (int i = 0; i < 24; i++) {
          graphs.put(String.valueOf(i), 0);
        }
      }
      learnerStausTracker.setGraphs(graphs);
    }
    
    LocalDateTime todayDate = LocalDateTime.now();
    switch (timeFilter) {
    case DAY:
      int count = graphs.get(String.valueOf(localDateTime.getHour()));
      count = count + 1;
      graphs.put(String.valueOf(localDateTime.getHour()), count);
      break;
    
    case WEEK:
      
      for (int i = 0; i < 7; i++) {
        LocalDateTime dateTime = todayDate.minusDays(i);
        String graphDate = dateTime.toLocalDate().toString();
        Integer weekCount = graphs.get(graphDate);
        if (weekCount == null) {
          weekCount = 0;
        }
        if (dateTime.getDayOfWeek() == localDateTime.getDayOfWeek()) {
          weekCount = weekCount.intValue() + 1;
        }
        graphs.put(graphDate, weekCount);
      }
      break;
    
    case MONTH:
      for (int i = 1; i <= 4; i++) {
        LocalDateTime week = todayDate.plusWeeks(-i);
        LocalDateTime endWeekDate = week.plusWeeks(1);
        String endWeekDateStr = endWeekDate.toLocalDate().toString();
        Integer countMonth = graphs.get(endWeekDateStr);
        if (countMonth == null) {
          countMonth = 0;
        }
        if (localDateTime.isAfter(week) && localDateTime.isBefore(endWeekDate)) {
          countMonth = countMonth.intValue() + 1;
        }
        graphs.put(endWeekDateStr, countMonth);
      }
      break;
    
    case YEAR:
      for (int i = 1; i <= 12; i++) {
        LocalDateTime startOfMonth = todayDate.plusMonths(-i);
        LocalDateTime endOfMonth = startOfMonth.plusMonths(1);
        String endOfMonthStr = endOfMonth.toLocalDate().toString();
        Integer countYear = graphs.get(endOfMonthStr);
        if (countYear == null) {
          countYear = 0;
        }
        if (localDateTime.isAfter(startOfMonth) && localDateTime.isBefore(endOfMonth)) {
          countYear = countYear.intValue() + 1;
        }
        graphs.put(endOfMonthStr, countYear);
      }
      break;
    default:
      // Commenting the previous year data as of now. In case reqired for future, just uncomment.
      // LocalDateTime startOfYear = todayDate.plusYears(-1);
      // LocalDateTime previousYear = startOfYear.minusDays(1);
      // if (localDateTime.isBefore(startOfYear)) {
      // Integer countDefault = graphs.get(previousYear.toString());
      // if (countDefault == null) {
      // countDefault = 1;
      // } else {
      // countDefault = countDefault.intValue() + 1;
      // }
      // graphs.put(previousYear.toString(), countDefault);
      // }
    }
    learnerStausTracker.setGraphs(graphs);
  }
  
  private void populateTrainingLibraryVisitData(
      List<TrainingLibraryVisitTracking> trainingLibraryVisitCount,
      Map<TimeFilter, LearnerStausTracker> learnerStatusTrackerMap) {
    
    if (!CollectionUtils.isEmpty(trainingLibraryVisitCount)) {
      
      trainingLibraryVisitCount.stream().forEach(
          arComp -> {
          TimeFilter timeFilter = DateTimeUtil.getTimeFilterDate(arComp.getAccessTime());
          List<LearnerStausTracker> learnetStatusTrackers = getLearnetStatusTrackersToUpdate(
              learnerStatusTrackerMap, timeFilter);
          learnetStatusTrackers.forEach((learnetStatussTrack) -> {
              learnetStatussTrack.setTrainingLibraryVisits(learnetStatussTrack
                  .getTrainingLibraryVisits() + 1);
            });
          
        });
    }
  }
  
  private void populateBookMarkTrackingData(List<BookMarkTracking> bookMarksCount,
      Map<TimeFilter, LearnerStausTracker> learnerStatusTrackerMap) {
    
    if (!CollectionUtils.isEmpty(bookMarksCount)) {
      
      bookMarksCount.stream()
          .forEach(
              arComp -> {
              TimeFilter timeFilter = DateTimeUtil.getTimeFilterDate(arComp.getAccessTime());
              List<LearnerStausTracker> learnerStatusTracker = getLearnetStatusTrackersToUpdate(
                  learnerStatusTrackerMap, timeFilter);
              learnerStatusTracker.forEach((learnetStatussTrack) -> {
                  learnetStatussTrack.setArtclesBookmarked(learnetStatussTrack
                      .getArtclesBookmarked() + 1);
                });
              
            });
    }
  }
  
  private void populateRecommendedArticleData(List<RecommendedArticleTracking> recommendedArticles,
      Map<TimeFilter, LearnerStausTracker> learnerStatusTrackerMap) {
    
    if (!CollectionUtils.isEmpty(recommendedArticles)) {
      
      recommendedArticles.stream()
          .forEach(
              arComp -> {
              TimeFilter timeFilter = DateTimeUtil.getTimeFilterDate(arComp.getAccessTime());
              List<LearnerStausTracker> learnerStatusTrackers = getLearnetStatusTrackersToUpdate(
                  learnerStatusTrackerMap, timeFilter);
              learnerStatusTrackers.forEach((learnetStatussTrack) -> {
                  learnetStatussTrack.setArticlesRecommened(learnetStatussTrack
                      .getArticlesRecommened() + 1);
                });
              
            });
    }
  }
  
  private void populateSP360FeedbackData(List<SP360RequestTracking> sp360Articles,
      Map<TimeFilter, LearnerStausTracker> learnerStatusTrackerMap, int pendingFeedbackRequests) {
    
    if (!CollectionUtils.isEmpty(sp360Articles)) {
      
      sp360Articles.stream().forEach(
          arComp -> {
          TimeFilter timeFilter = DateTimeUtil.getTimeFilterDate(arComp.getAccessTime());
          List<LearnerStausTracker> learnerStatusTracker = getLearnetStatusTrackersToUpdate(
              learnerStatusTrackerMap, timeFilter);
          learnerStatusTracker.forEach((learnetStatussTrack) -> {
              SP360LearnerStatusTracker sp360LearnerTracker = learnetStatussTrack
                  .getSp360LearnerTracker();
              
              if (arComp.getTrackingType() == TrackingType.REQUESTS) {
                sp360LearnerTracker.setRequestSent(sp360LearnerTracker.getRequestSent() + 1);
              }
              
              if (arComp.getTrackingType() == TrackingType.COMPLETED) {
                sp360LearnerTracker.setAssessmentCompleted(sp360LearnerTracker
                    .getAssessmentCompleted() + 1);
              } else if (arComp.getTrackingType() == TrackingType.DEACTIVATED) {
                sp360LearnerTracker.setAssessmentDeactivated(sp360LearnerTracker
                    .getAssessmentDeactivated() + 1);
              }
              
              sp360LearnerTracker.setAssessmentPending(pendingFeedbackRequests);
            });
        });
    }
  }
  
  /**
   * <code>populateGrowthFeedbackData</code> will fetch the growth feebdack for the list of users
   * passed in.
   * 
   * @param growthRequests
   *          artices to be shown
   * @param pendingGrowthRequest
   
  private void populateGrowthFeedbackData(List<? extends GrowthRequest> growthRequests,
      Map<TimeFilter, LearnerStausTracker> learnerStatusTrackerMap, int pendingGrowthRequest) {
    
    if (!CollectionUtils.isEmpty(growthRequests)) {
      
      growthRequests.stream().forEach(
          gr -> {
            TimeFilter timeFilter = DateTimeUtil.getTimeFilterDate(DateTimeUtil
                .getLocalDateFromMongoId(gr.getId()));
            List<LearnerStausTracker> learnerStatusTrackers = getLearnetStatusTrackersToUpdate(
                learnerStatusTrackerMap, timeFilter);
            learnerStatusTrackers.forEach((learnetStatussTrack) -> {
              GrowthLearnerTracker growthLearnerTracker = learnetStatussTrack
                  .getGrowthLearnerTracker();
              growthLearnerTracker.setRequestSent(growthLearnerTracker.getRequestSent() + 1);
              if (gr.getRequestStatus() == RequestStatus.COMPLETED) {
                growthLearnerTracker.setCompleted(growthLearnerTracker.getCompleted() + 1);
              }
              // /else if (arComp.getTrackingType() == TrackingType.DEACTIVATED) {
              // growthLearnerTracker.setDeactivated((growthLearnerTracker.getDeactivated() + 1));
              // }
                else if (gr.getRequestStatus() == RequestStatus.DECLINED) {
                  growthLearnerTracker.setDeclined(growthLearnerTracker.getDeclined() + 1);
                }
                growthLearnerTracker.setPending(pendingGrowthRequest);
                
              });
          });
      
    }
  }
  */
  private List<LearnerStausTracker> getLearnetStatusTrackersToUpdate(
      Map<TimeFilter, LearnerStausTracker> learnerStatusTrackerMap, TimeFilter timeFilter) {
    List<LearnerStausTracker> learnerStausTrackers = new ArrayList<LearnerStausTracker>();
    switch (timeFilter) {
    case DAY:
      learnerStausTrackers.add(learnerStatusTrackerMap.get(TimeFilter.DAY));
    case WEEK:
      learnerStausTrackers.add(learnerStatusTrackerMap.get(TimeFilter.WEEK));
    case MONTH:
      learnerStausTrackers.add(learnerStatusTrackerMap.get(TimeFilter.MONTH));
    case YEAR:
      learnerStausTrackers.add(learnerStatusTrackerMap.get(TimeFilter.YEAR));
    default:
      break;
    }
    return learnerStausTrackers;
    
  }
  
  private void populateLearnerStatusTracker(
      Map<TimeFilter, LearnerStausTracker> learnerStatusTrackerMap) {
    createLearnerStatusTracker(learnerStatusTrackerMap, TimeFilter.DAY);
    createLearnerStatusTracker(learnerStatusTrackerMap, TimeFilter.WEEK);
    createLearnerStatusTracker(learnerStatusTrackerMap, TimeFilter.MONTH);
    createLearnerStatusTracker(learnerStatusTrackerMap, TimeFilter.YEAR);
  }
  
  private void createLearnerStatusTracker(Map<TimeFilter, LearnerStausTracker> learnerStatusMap,
      TimeFilter timeFilter) {
    LearnerStausTracker learnerStausTracer = learnerStatusMap.get(timeFilter);
    if (learnerStausTracer == null) {
      learnerStausTracer = new LearnerStausTracker();
      learnerStausTracer.setFilter(timeFilter);
      learnerStatusMap.put(timeFilter, learnerStausTracer);
    }
  }
  
  /**
   * <code>getLearnerStatus</code> method will return the learner status of the user for the
   * compamny. passed.
   * 
   * @param companyId
   *          of the user.
   * @return the learner status.
   */
  public LearnerStatus populateLearnerStatus(String companyId) {
    /* fetch the list fo users belong to this company */
    List<User> validUsers = userRepository.findMemberByStatus(companyId, UserStatus.VALID);
    
    LearnerStatus learnerStatus = spectrumRepository.getLearnerStatusBalance(companyId);
    LearnerStatus generateLearnerStatusBalances = generateLearnerStatus(validUsers, companyId);
    if (learnerStatus == null) {
      generateLearnerStatusBalances.setCompanyId(companyId);
      learnerStatus = generateLearnerStatusBalances;
    } else {
      learnerStatus.setCompanyId(companyId);
      learnerStatus.setLearnerStatusTrackerMap(generateLearnerStatusBalances
          .getLearnerStatusTrackerMap());
    }
    spectrumRepository.saveLeanerStatusBalance(learnerStatus);
    return learnerStatus;
    
  }
  
  /**
   * LoadBalancs method will load the profile balances.
   */
  public void loadLearnerStatus() {
    
    /* find all the company present in the SUrepeople plateform. */
    List<Company> findAllCompanies = accountRepository.findAllCompanies();
//    populateGrowthRequestWithCompany();
    findAllCompanies.stream().forEach(comp -> {
        try {
          populateLearnerStatus(comp.getId());
        } catch (Exception ex) {
          LOG.info("No users present for the company, Skiiping it " + comp.getName());
        }
      });
  }
  
  /**
   * @param companyId
   
  private void populateGrowthRequestWithCompany() {
    
    List<GrowthRequest> allRequests = growthRepository.getAllRequests();
    if (!isGrowthRequestWithCompanyDone) {
      
      List<GrowthRequest> validRequests = allRequests.stream()
          .filter(gr -> gr.getCompanyId() == null).collect(Collectors.toList());
      
      validRequests.stream().forEach(gr -> {
        String requestedByEmail = gr.getRequestedByEmail();
        
        User user = userRepository.findByEmail(requestedByEmail);
        if (user != null) {
          String companyId2 = user.getCompanyId();
          if (companyId2 != null) {
            gr.setCompanyId(companyId2);
            growthRepository.updateGrowthRequest(gr);
          }
          
        }
        
      });
      isGrowthRequestWithCompanyDone = true;
      
    }
    
  }*/
}
