package com.sp.web.controller.profile;

import com.sp.web.Constants;
import com.sp.web.account.AccountRepository;
import com.sp.web.assessment.personality.PersonalityBeanResponse;
import com.sp.web.assessment.personality.PersonalityType;
import com.sp.web.assessment.personality.RangeType;
import com.sp.web.assessment.processing.AnalysisBean;
import com.sp.web.assessment.processing.ConflictManagementComparator;
import com.sp.web.assessment.questions.CategoryType;
import com.sp.web.assessment.questions.TraitType;
import com.sp.web.controller.i18n.MessagesFactory;
import com.sp.web.dao.CompanyDao;
import com.sp.web.dto.BaseGoalDto;
import com.sp.web.dto.BaseUserDTO;
import com.sp.web.dto.CertificateUserDTO;
import com.sp.web.dto.CompanyLogoDTO;
import com.sp.web.dto.UserBadgeDTO;
import com.sp.web.exception.InvalidRequestException;
import com.sp.web.model.Gender;
import com.sp.web.model.HiringUser;
import com.sp.web.model.User;
import com.sp.web.model.UserProfileSettings;
import com.sp.web.model.goal.GoalCategory;
import com.sp.web.model.goal.GoalStatus;
import com.sp.web.model.goal.PersonalityPracticeArea;
import com.sp.web.mvc.SPResponse;
import com.sp.web.product.CompanyFactory;
import com.sp.web.repository.hiring.HiringRepository;
import com.sp.web.repository.user.UserRepository;
import com.sp.web.service.badge.BadgeFactory;
import com.sp.web.service.goals.SPGoalFactory;
import com.sp.web.utils.LocaleHelper;
import com.sp.web.utils.MessagesHelper;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.TimeZone;
import java.util.stream.Collectors;

/**
 * @author pradeep
 *
 *         The profile controller helper.
 */
@Component
public class ProfileControllerHelper {
  
  private static final ConflictManagementComparator comparator = new ConflictManagementComparator();
  
  @Autowired
  private UserRepository userRepository;
  
  @Autowired
  private AccountRepository accountRepository;
  
  @Autowired
  private HiringRepository hiringRepository;
  
  @Autowired
  private MessagesFactory messagesFactory;
  
  @Autowired
  private CompanyFactory companyFactory;
  
  @Autowired
  private SPGoalFactory goalsFactory;
  
  @Autowired
  @Lazy
  private BadgeFactory badgeFactory;
  
  private static final Logger LOG = Logger.getLogger(ProfileControllerHelper.class);
  
  private static final String PERSONLITY_KEYS_PREFIX = "profile.personality.%s.";
  private static final String[] PERSONALITY_KEYS = { "WorkEnvironmentContribution",
      "EmotionalPosture", "DrivingIdeal", "AssessesOthersBy", "MotivationalStyle",
      "MostFavorableWorkEnvironment", "RelationalTaskOrientation", "MobilityPreference",
      "EnvironmentalStressors", "Over-uses", "WhenPressured", "UneasyWhen", "Leadership",
      "SummaryOfStrengths", "JobMatch", "CommunicatingWith", "CommunicatingWith.heading",
      "DisagreeingWith", "DisagreeingWith.heading", "NegativePerceptionsMayHaveOfOthers",
      "NegativePerceptionsOthersMayHaveOf.heading", "NegativePerceptionsMayHaveOfOthers.heading",
      "NegativePerceptionsOthersMayHaveOf", "NotableCharacteristics", "FundamentalDesiresDrive",
      "RecommendedImprovements", "HowToSuperviseAndEncourage", "summary1", "summary2", "type",
      "summary3", "summary4", "summary5" };
  
  private static final String[][] MOTIVATION_KEYS = {
      { "profile.motivation.power.greater.text", "profile.motivation.compliance.greater.text",
          "profile.motivation.power.compliance.equal" },
      { "profile.motivation.attainment.greater.text",
          "profile.motivation.recongnitioneffort.greater.text",
          "profile.motivation.recongnitioneffort.attainment.equal" },
      { "profile.motivation.affliation.greater.text", "profile.motivation.activity.greater.text",
          "profile.motivation.affliation.activity.equal" },
      { "profile.motivation.affirmedbyothers.greater.text",
          "profile.motivation.selfaffirmed.greater.text",
          "profile.motivation.self.other.affired.equal" },
      { "profile.motivation.exchangeofideas.greater.text",
          "profile.motivation.recievedirection.greater.text",
          "profile.motivation.recievedirection.exchange.equal" },
      { "profile.motivation.freedom.greater.text", "profile.motivation.consistency.greater.text",
          "profile.motivation.freedom.consistency.equal" },
      { "profile.motivation.prefersprocess.greater.text",
          "profile.motivation.taskcompletion.greater.text",
          "profile.motivation.tastcompletion.process.equal" },
      { "profile.motivation.accomplishment.greater.text",
          "profile.motivation.hygiene.greater.text",
          "profile.motivation.hygiene.accomplishment.equal" } };
  
  private static final String[] CONFLICT_MANAGEMENT_KEYS = { "profile.cm.{1}.headline",
      "profile.cm.{0}.{1}.subheading", "profile.cm.{1}.posture", "profile.cm.{1}.posture.quote",
      "profile.cm.{1}.attitude", "profile.cm.{1}.attitude.quote", "profile.cm.{1}.strategy",
      "profile.cm.{1}.strategy.text", "profile.cm.{1}.interaction",
      "profile.cm.{1}.interaction.text", "profile.cm.{1}.goalRelation",
      "profile.cm.{1}.goalRelation.quote", "profile.cm.{1}.problemAreas",
      "profile.cm.{1}.problemAreas.text" };
  
  private static final String[] CONFLICT_MANAGEMENT_RESP_KEYS = { "profile.cm.%s.headline",
      "profile.cm.%s.subheading", "profile.cm.%s.posture", "profile.cm.%s.posture.quote",
      "profile.cm.%s.attitude", "profile.cm.%s.attitude.quote", "profile.cm.%s.strategy",
      "profile.cm.%s.strategy.text", "profile.cm.%s.interaction", "profile.cm.%s.interaction.text",
      "profile.cm.%s.goalRelation", "profile.cm.%s.goalRelation.quote",
      "profile.cm.%s.problemAreas", "profile.cm.%s.problemAreas.text" };
  
  private static final String[] DECISION_MAKING_KEYS = { "profile.dm.%s.p1", "profile.dm.%s.p2",
      "profile.dm.%s.p1" };
  
  private static final String[] LEARNING_STYLE_KEYS = { "profile.learning.%s.frustration",
      "profile.learning.%s.frustration.p1", "profile.learning.%s.p1" };
  
  private static final Map<String, Map<String, String>> personalityMessagesMap = new HashMap<String, Map<String, String>>();
  private static final Map<String, Map<String, String>> profileStaticMessages = new HashMap<String, Map<String, String>>();
  
  private static final String DECISION_MSG_FORMATTER = "profile.dm.%s.percentage";
  private static final String LEARNING_MSG_FORMATTER = "profile.learning.%s.characterstics.percentage";
  
  /**
   * The helper method to get the logged in user profile.
   * 
   * @param user
   *          - logged in user
   * @return the response to the logged in user profile
   */
  public SPResponse getProfile(User user) {
    // add the user profile to the response
    SPResponse response = new SPResponse();
    UserBadgeDTO userDto = new UserBadgeDTO(user);
    userDto.updateBadge(user, badgeFactory);
    
    if (StringUtils.isBlank(user.getCompanyId())) {
      userDto.setCompanyId("N/A");
    } else {
      // adding company details
      CompanyDao company = companyFactory.getCompany(user.getCompanyId());
      response.add(Constants.PARAM_COMPANY, new CompanyLogoDTO(company));
    }
    response.add(Constants.PARAM_MEMBER, userDto);
    response.add(Constants.PARAM_TIME_ZONE, TimeZone.getDefault());
    response.add(Constants.PARAM_LOCALE, user.getProfileSettings().getLocale().toString());
    response.add(Constants.PARAM_PA_VISITED, user.getProfileSettings().isPeopleAnalyticsVisited());
    return response;
  }
  
  /**
   * The helper method to get the logged in user analysis.
   * 
   * @param user
   *          - logged in user
   * @return the response to the logged in user analysis
   */
  public SPResponse getAnalysis(User user) {
    // add the user profile to send back
    return new SPResponse().add(Constants.PARAM_ANALYSIS, Optional.ofNullable(user.getAnalysis())
        .orElseThrow(() -> new InvalidRequestException("User analysis not completed yet !!!")));
  }
  
  /**
   * The helper method to get the logged in user analysis.
   * 
   * @param user
   *          - logged in user
   * @return the response to the logged in user analysis
   */
  public SPResponse getFullAnalysis(User user, Object[] param) {
    // add the user profile to send back
    SPResponse fullAnalysisWithContent = new SPResponse();
    User otherUser = user;
    String email = (param != null && param.length > 0) ? (String) param[0] : null;
    
    boolean selfPortrait = true;
    /* if email is present, then give the full analysis of requested user */
    if (StringUtils.isNotBlank(email) && !email.equalsIgnoreCase(user.getEmail())) {
      otherUser = userRepository.findByEmailValidated(email);
      selfPortrait = false;
    }
    return getFullAnalysis(otherUser, fullAnalysisWithContent, selfPortrait, user.getLocale());
  }
  
  /**
   * Adds the users full profile details to the response.
   * 
   * @param user
   *          - user
   * @param resp
   *          - response object
   * @param selfPortrait
   *          - flag for self portrait
   * @param locale
   *          - locale
   * @return the response object
   */
  public SPResponse getFullAnalysis(User user, SPResponse resp, boolean selfPortrait, Locale locale) {
    AnalysisBean analysis = user.getAnalysis();
    resp.add(
        Constants.PARAM_ANALYSIS,
        Optional.ofNullable(analysis).orElseThrow(
            () -> new InvalidRequestException("User analysis not completed yet !!!")));
    
    String[] genderTextArray = MessagesHelper.getGenderTextArray(locale, user.getGender());
    final String firstName = user.getFirstName();
    
    for (CategoryType categoryType : analysis.getCompletedCategories()) {
      getCateogorySpecifcAnalysis(user, analysis, firstName, genderTextArray, categoryType, locale,
          resp);
    }
    
    resp.add(messagesFactory.getTraitsMessages(locale.toString()));
    resp.getSuccess().remove(null);
    
    if (selfPortrait) {
      final HashMap<RangeType, PersonalityBeanResponse> personalityMap = analysis.getPersonality();
      PersonalityType primaryPersonalityType = personalityMap.get(RangeType.Primary)
          .getPersonalityType();
      List<BaseGoalDto> primaryGoals = getPersonalityGoals(primaryPersonalityType,
          user.getUserLocale());
      resp.add(Constants.PARAM_PRIMARY_PRACTICE_AREAS, primaryGoals);
      PersonalityType underPressurePersonalityType = personalityMap.get(RangeType.UnderPressure)
          .getPersonalityType();
      if (primaryPersonalityType == underPressurePersonalityType) {
        resp.add(Constants.PARAM_UNDER_PRESSURE_PRACTICE_AREAS, primaryGoals);
      } else {
        resp.add(Constants.PARAM_UNDER_PRESSURE_PRACTICE_AREAS,
            getPersonalityGoals(underPressurePersonalityType, user.getUserLocale()));
      }
    }
    return resp;
  }
  
  private List<BaseGoalDto> getPersonalityGoals(PersonalityType primaryPersonalityType,
      String locale) {
    PersonalityPracticeArea personalityPracticeArea = goalsFactory
        .getPersonalityPracticeArea(primaryPersonalityType);
    return personalityPracticeArea
        .getGoalIds()
        .stream()
        .map(gid -> goalsFactory.getGoal(gid, locale))
        .filter(
            spg -> spg.getCategory() == GoalCategory.GrowthAreas
                && spg.getStatus() == GoalStatus.ACTIVE).map(BaseGoalDto::new)
        .collect(Collectors.toList());
  }
  
  private void addMappedPersonality(SPResponse fullAnalysisWithContent, RangeType type,
      HashMap<RangeType, PersonalityBeanResponse> personalityMap) {
    fullAnalysisWithContent.add("profile.analysis.mapped." + type.toString() + ".personalityType",
        MessagesHelper.getMessage(personalityMap.get(type).getPersonalityType().toString()));
  }
  
  /**
   * getPersonalityAnalysis will return the persoanlity analysis data for the user profile.
   * 
   * @param user
   *          logged in user
   * @return the SPResponse.
   */
  public SPResponse getPersonalityAnalysis(User user, Object[] param) {
    
    User otherUser = user;
    String email = (param != null && param.length > 0) ? (String) param[0] : null;
    
    /* if email is present, then give the full analysis of requested user */
    if (StringUtils.isNotBlank(email)) {
      otherUser = userRepository.findByEmailValidated(email);
    }
    
    SPResponse response = new SPResponse();
    addPersonalityAnalysis(otherUser, response);
    return response;
    
  }
  
  /**
   * Add the personality analysis for the user to the response.
   * 
   * @param user
   *          - user
   * @param response
   *          - response to update
   */
  public void addPersonalityAnalysis(User user, SPResponse response) {
    AnalysisBean analysis = user.getAnalysis();
    response.add(
        Constants.PARAM_ANALYSIS,
        Optional.ofNullable(analysis).orElseThrow(
            () -> new InvalidRequestException("User analysis not completed yet !!!")));
    
    /** Add mappedn primary perosnality types. */
    final HashMap<RangeType, PersonalityBeanResponse> personalityMap = analysis.getPersonality();
    addMappedPersonality(response, RangeType.Primary, personalityMap);
    addMappedPersonality(response, RangeType.UnderPressure, personalityMap);
    
    PersonalityBeanResponse personalityBeanResponse = personalityMap.get(RangeType.Primary);
    String primaryPersonalityType = personalityBeanResponse.getPersonalityType().toString();
    
    PersonalityBeanResponse underPressureResponse = personalityMap.get(RangeType.UnderPressure);
    String underPressurePersonalityType = underPressureResponse.getPersonalityType().toString();
    
    String[] keys = { "profile.personality.%s.type", "profile.spectrum.%s.description",
        "profile.video.%s" };
    
    /** Add primary data. */
    for (String key : keys) {
      String formattedKey = String.format(key, primaryPersonalityType);
      String priamarykey = String.format(key, "primary");
      try {
        response.add(priamarykey, MessagesHelper.genderNormalizeFromKey(formattedKey, user));
      } catch (NoSuchMessageException messageException) {
        if (LOG.isDebugEnabled()) {
          LOG.debug(messageException);
        }
      }
      
    }
    
    /** Add secondary data. */
    for (String key : keys) {
      String formattedKey = String.format(key, underPressurePersonalityType);
      String underPressure = String.format(key, "underPressure");
      try {
        response.add(underPressure, MessagesHelper.genderNormalizeFromKey(formattedKey, user));
      } catch (NoSuchMessageException messageException) {
        if (LOG.isDebugEnabled()) {
          LOG.debug(messageException);
        }
        
      }
      
    }
    response.add("profile.personality.primary.heading",
        MessagesHelper.getMessage("profile.personality.primary.heading", user.getLocale()));
    response.add("profile.personality.underPressure.heading",
        MessagesHelper.getMessage("profile.personality.underPressure.heading", user.getLocale()));
    response.add("profile.personality.dimension.text",
        MessagesHelper.getMessage("profile.personality.dimension.text", user.getLocale()));
    response.add("profile.user.personality.subheading",
        MessagesHelper.genderNormalizeFromKey("profile.user.personality.subheading", user));
  }
  
  /**
   * Get the full analysis for the hiring candidate.
   * 
   * @param user
   *          - user
   * @param params
   *          - params
   * @return the response to the hiring candidate analysis
   */
  public SPResponse getHiringUserFullAnalysis(User user, Object[] params) {
    SPResponse fullAnalysisWithContent = new SPResponse();
    
    User otherUser = user;
    String id = (params != null && params.length > 0) ? (String) params[0] : null;
    /* if email is present, then give the full analysis of requested user */
    if (StringUtils.isNotBlank(id)) {
      otherUser = hiringRepository.findById(id);
    }
    SPResponse fullAnalysis = getFullAnalysis(otherUser, fullAnalysisWithContent, false,
        user.getLocale());
    fullAnalysis.add("user", new BaseUserDTO(otherUser));
    return fullAnalysis;
  }
  
  /**
   * Get the full analysis for the user token.
   * 
   * @param params
   *          - the params
   * @return the response to the get profile request
   */
  public SPResponse getAnalysisFullPublic(Object[] params) {
    SPResponse fullAnalysisWithContent = new SPResponse();
    
    String tokenId = (String) params[0];
    
    // get the user for the given token
    User user = Optional.ofNullable(hiringRepository.findByToken(tokenId)).orElseThrow(
        () -> new InvalidRequestException("Invalid url."));
    
    // adding the user profile to the response
    fullAnalysisWithContent.add(Constants.PARAM_USER, new CertificateUserDTO(user));
    
    return getFullAnalysis(user, fullAnalysisWithContent, false, user.getLocale());
  }
  
  /**
   * Helper method to get the full analysis for the user from the certificate token.
   * 
   * @param params
   *          - params
   * @return the response to the the certificate request
   */
  public SPResponse getAnalysisFullCertificate(Object[] params) {
    SPResponse fullAnalysisWithContent = new SPResponse();
    
    String certificateNumber = (String) params[0];
    
    // get the user for the given token
    User user = Optional.ofNullable(userRepository.findByCertificate(certificateNumber))
        .orElseThrow(() -> new InvalidRequestException("Invalid url."));
    
    // adding the user profile to the response
    final CertificateUserDTO certificateUser = new CertificateUserDTO(user);
    // adding the expiry date of the certificate
    // Account userAccount = accountRepository.findValidatedAccountByAccountId(user.getAccountId());
    // certificateUser.setExpiresOn(MessagesHelper.formatDate(userAccount.getExpiresTime()));
    fullAnalysisWithContent.add(Constants.PARAM_USER, certificateUser);
    
    final UserProfileSettings profileSettings = user.getProfileSettings();
    if (profileSettings != null && profileSettings.isCertificateProfilePublicView()) {
      getFullAnalysis(user, fullAnalysisWithContent, false, user.getLocale());
    }
    
    return fullAnalysisWithContent;
  }
  
  /**
   * <code>getCateogoyrSpecificAnaylysis</code> method return the analysis content for the category
   * specific.
   * 
   * @param user
   *          current logged in user
   * @param analysis
   *          - user analysis
   * @param firstName
   *          - user first name
   * @param genderText
   *          - gender text replacement
   * @param type
   *          category type
   * @param locale
   *          - user locale
   * @param response
   *          - response to update
   * @return the updated response object
   */
  public SPResponse getCateogorySpecifcAnalysis(User user, AnalysisBean analysis, String firstName,
      String[] genderText, CategoryType type, Locale locale, SPResponse response) {
    
    /* Get the analysis for the user trait */
    switch (type) {
    case Processing:
      processProcessingMessages(response, locale, analysis, firstName, genderText, user.getGender());
      break;
    case MotivationHow:
      processMotivationHow(response, analysis, locale, firstName, genderText);
      break;
    case MotivationWhy:
      processMotivationWhy(response, analysis, locale, firstName, genderText);
      break;
    case ConflictManagement:
      processConflictManagment(response, analysis, locale, firstName, genderText);
      break;
    case DecisionMaking:
      processDescionMaking(response, analysis, locale, firstName, genderText);
      break;
    case FundamentalNeeds:
      processFundamentalNeeds(response, analysis, locale);
      break;
    case LearningStyle:
      processLearningStyle(response, analysis, locale);
      break;
    case Personality:
      processPersonalityProfile(response, locale, analysis, firstName, genderText);
      break;
    default:
      break;
    }
    
    /* Add the personality keys to the response */
    response.add(messagesFactory.getMessagesWith("profileKeys", LocaleHelper.locale()));
    
    return response;
  }
  
  /**
   * <code>processPersonaliytPRofile</code> method will process the perosnality profile iwth the
   * content.
   * 
   * @param response
   *          with he content.
   * @param user
   *          logged in user.
   * @param locale
   *          user locale
   */
  private void processPersonalityProfile(SPResponse response, Locale locale, AnalysisBean analysis,
      String firstName, String[] genderTextArray) {
    
    addPersonalityMessages(response, firstName, analysis.getPersonality(RangeType.Primary)
        .toString(), "primary", genderTextArray, locale);
    addPersonalityMessages(response, firstName, analysis.getPersonality(RangeType.UnderPressure)
        .toString(), "underPressure", genderTextArray, locale);
    
    response.add(getAllStaticMessages(CategoryType.Personality, locale));
  }
  
  /**
   * Adds the personality messages, for the given personality type. The type defines if this is
   * added as primary or under pressure text.
   * 
   * @param response
   *          - response to update
   * @param userFirstName
   *          - users first name
   * @param personalityType
   *          - personality type
   * @param type
   *          - primary or under pressure
   * @param genderTextArray
   *          - gender text array
   * @param locale
   *          - locale
   */
  private void addPersonalityMessages(SPResponse response, String userFirstName,
      String personalityType, String type, String[] genderTextArray, Locale locale) {
    
    String respKeyPrefix = String.format(PERSONLITY_KEYS_PREFIX, type);
    Map<String, String> personalityMessages = getPersonalityMessages(personalityType, locale);
    
    for (Entry<String, String> entry : personalityMessages.entrySet()) {
      try {
        response.add(respKeyPrefix + entry.getKey(),
            MessagesHelper.genderNormalize(entry.getValue(), userFirstName, genderTextArray));
      } catch (NoSuchMessageException messageException) {
        if (LOG.isDebugEnabled()) {
          LOG.debug(messageException);
        }
      }
    }
  }
  
  /**
   * Gets the map of messages to add for the given personality type.
   * 
   * @param personalityType
   *          - personality type
   * @param locale
   *          - locale
   * @return the messages for the given personality type in the locale
   */
  private Map<String, String> getPersonalityMessages(String personalityType, Locale locale) {
    Map<String, String> messagesMap = personalityMessagesMap.get(personalityType
        + locale.toString());
    if (messagesMap == null) {
      String messageKeyPrefix = String.format(PERSONLITY_KEYS_PREFIX, personalityType);
      messagesMap = new HashMap<String, String>();
      for (String key : PERSONALITY_KEYS) {
        try {
          messagesMap.put(key, MessagesHelper.getMessage(messageKeyPrefix + key, locale));
        } catch (NoSuchMessageException messageException) {
          if (LOG.isDebugEnabled()) {
            LOG.debug(messageException);
          }
        }
      }
      personalityMessagesMap.put(personalityType + locale.toString(), messagesMap);
    }
    return messagesMap;
  }
  
  /**
   * <code>processLearningStyle</code> methdo will give the cotent for the learnig style.
   * 
   * @param response
   *          for the user
   * @param user
   *          logged in user
   * @param locale
   *          user locale
   */
  private void processLearningStyle(SPResponse response, AnalysisBean analysis, Locale locale) {
    
    Map<TraitType, BigDecimal> learningStyle = analysis.getLearningStyle();
    BigDecimal globalBd = learningStyle.get(TraitType.Global);
    BigDecimal analyticalBd = learningStyle.get(TraitType.Analytical);
    
    switch (globalBd.compareTo(analyticalBd)) {
    case 1:
      response.add("profile.learning.summary",
          MessagesHelper.getMessage("profile.learning.global.greater", locale));
      response.add("profile.learning.sub.heading",
          MessagesHelper.getMessage("profile.learning.global.heading", locale));
      break;
    case -1:
    case 0:
    default:
      response.add("profile.learning.summary",
          MessagesHelper.getMessage("profile.learning.analytical.greater", locale));
      response.add("profile.learning.sub.heading",
          MessagesHelper.getMessage("profile.learning.analytical.heading", locale));
      break;
    }
    
    for (Entry<TraitType, BigDecimal> entry : learningStyle.entrySet()) {
      String formattedKey = String.format(LEARNING_MSG_FORMATTER, entry.getKey().toString()
          .toLowerCase());
      response.add(formattedKey, MessagesHelper.getMessage(formattedKey, locale, entry.getValue()));
    }
    
    Map<String, String> messages = getStaticMessages(CategoryType.LearningStyle, locale);
    if (messages == null) {
      messages = getAllStaticMessages(CategoryType.LearningStyle, locale, learningStyle.keySet());
    }
    response.add(messages);
  }
  
  /**
   * <code>processFundamentalNeeds</code> method will process the fundamental needs.
   * 
   * @param response
   *          - the response
   * @param analysis
   *          user analysis
   * @param locale
   *          user locale
   */
  private void processFundamentalNeeds(SPResponse response, AnalysisBean analysis, Locale locale) {
    
    // DAX : Removed as it seems never used on site.
    // List<TraitType> traitPriorities = analysis.traitPriorities(CategoryType.FundamentalNeeds);
    // if (traitPriorities == null) {
    // traitPriorities = fundamentalNeeds.entrySet().stream().sorted(comparator.reversed()).limit(2)
    // .map(Entry::getKey).collect(Collectors.toList());
    // }
    //
    // final TraitType trait1 = traitPriorities.get(0);
    // final TraitType trait2 = traitPriorities.get(1);
    // BigDecimal value1 = fundamentalNeeds.get(trait1);
    // BigDecimal value2 = fundamentalNeeds.get(trait2);
    //
    // response.add("profile.fm.primary.need", MessagesHelper.getMessage("profile.fm.primary.need",
    // locale, firstName, trait1.toString(), value1));
    // response.add(
    // "profile.fm.secondary.need",
    // MessagesHelper.getMessage("profile.fm.secondary.need", locale, genderText[6],
    // trait2.toString(), value2));
    
    Map<String, String> messages = getStaticMessages(CategoryType.FundamentalNeeds, locale);
    if (messages == null) {
      messages = getAllStaticMessages(CategoryType.FundamentalNeeds, locale, analysis
          .getFundamentalNeeds().keySet());
    }
    
    response.add(messages);
  }
  
  /**
   * <code>procssDeciionMaking</code> method will return the decion making content.
   * 
   * @param response
   *          Sp response to be fetched
   * @param analysis
   *          user analysis
   * @param locale
   *          user locale
   * @param firstName
   *          user first name
   * @param genderText
   *          gender text replacement
   */
  private void processDescionMaking(SPResponse response, AnalysisBean analysis, Locale locale,
      String firstName, String[] genderText) {
    
    /* get the decision making data */
    Map<TraitType, BigDecimal> decisionMaking = analysis.getDecisionMaking();
    BigDecimal carefulBd = decisionMaking.get(TraitType.Careful);
    BigDecimal rapdiBd = decisionMaking.get(TraitType.Rapid);
    BigDecimal inwardBd = decisionMaking.get(TraitType.Inward);
    BigDecimal outwardBd = decisionMaking.get(TraitType.Outward);
    
    /* check if inward and outward are equal () */
    String summary = null;
    
    final int compareTo1 = outwardBd.compareTo(inwardBd);
    final int compareTo2 = rapdiBd.compareTo(carefulBd);
    
    if (compareTo1 != 0 && compareTo2 != 0) {
      summary = MessagesHelper.getMessage(
          "profile.dm.inward.careful.unequal",
          locale,
          firstName,
          (compareTo1 == 1 ? MessagesHelper.getMessage("Outward", locale) : MessagesHelper
              .getMessage("Inward", locale)),
          (compareTo2 == 1 ? MessagesHelper.getMessage("Rapid", locale) : MessagesHelper
              .getMessage("Careful", locale)));
    } else {
      String summary1 = null;
      switch (compareTo1) {
      case 0:
        summary1 = MessagesHelper.getMessage("profile.dm.inward.outward.equal", locale, firstName);
        break;
      case 1:
        summary1 = MessagesHelper.getMessage("profile.dm.inward.greater.outward", locale,
            firstName, MessagesHelper.getMessage("Outward", locale));
        break;
      case -1:
        summary1 = MessagesHelper.getMessage("profile.dm.inward.greater.outward", locale,
            firstName, MessagesHelper.getMessage("Inward", locale));
        break;
      default:
        break;
      }
      
      String summary2 = null;
      switch (compareTo2) {
      case 0:
        summary2 = MessagesHelper.getMessage("profile.dm.careful.rapid.equal", locale,
            genderText[1]);
        break;
      case 1:
        summary2 = MessagesHelper.getMessage("profile.dm.careful.greater.rapid", genderText[1],
            MessagesHelper.getMessage("Rapid", locale));
        break;
      case -1:
        summary2 = MessagesHelper.getMessage("profile.dm.careful.greater.rapid", genderText[1],
            MessagesHelper.getMessage("Careful", locale));
        break;
      default:
        break;
      }
      summary = summary1 + " " + summary2;
    }
    
    response.add("profile.dm.summary2", summary);
    
    for (Entry<TraitType, BigDecimal> entry : decisionMaking.entrySet()) {
      String key = String.format(DECISION_MSG_FORMATTER, entry.getKey().toString().toLowerCase());
      response.add(key, MessagesHelper.getMessage(key, locale, entry.getValue()));
    }
    
    response
        .add(getAllStaticMessages(CategoryType.DecisionMaking, locale, decisionMaking.keySet()));
  }
  
  /**
   * <code>procesConflictManagment</code> method will return the data for the conflictManagent.
   * 
   * @param response
   *          sp respons.
   * @param user
   *          logged in.
   * @param locale
   *          locale
   */
  private void processConflictManagment(SPResponse response, AnalysisBean analysis, Locale locale,
      String firstName, String[] genderText) {
    
    Map<TraitType, BigDecimal> conflictManagement = analysis.getConflictManagement();
    List<TraitType> traitPriorities = analysis.traitPriorities(CategoryType.ConflictManagement);
    
    if (traitPriorities == null) {
      LOG.warn("Trait priorities not set for user :" + firstName);
      traitPriorities = conflictManagement.entrySet().stream().sorted(comparator.reversed())
          .limit(2).map(Entry::getKey).collect(Collectors.toList());
    }
    
    TraitType trait1 = traitPriorities.get(0);
    TraitType trait2 = traitPriorities.get(1);
    
    processConflictManagement(0, trait1, conflictManagement.get(trait1), firstName, genderText,
        response, locale, "Primary");
    processConflictManagement(0, trait2, conflictManagement.get(trait2), firstName, genderText,
        response, locale, "Secondary");
    
    String trait1Str = MessagesHelper.getMessage(trait1.toString(), locale).toLowerCase();
    String trait2Str = MessagesHelper.getMessage(trait2.toString(), locale).toLowerCase();
    response.add("profile.cm.heading.summary2", MessagesHelper.getMessage(
        "profile.cm.heading.summary2", locale, firstName, trait1Str, trait2Str));
    
    response.add(getAllStaticMessages(CategoryType.ConflictManagement, locale));
  }
  
  private void processConflictManagement(int index, TraitType traitType, BigDecimal value,
      String firstName, String[] genderText, SPResponse response, Locale locale, String type) {
    
    String traitLC = traitType.toString().toLowerCase();
    String lowerType = type.toLowerCase();
    for (int i = 0; i < CONFLICT_MANAGEMENT_KEYS.length; i++) {
      String traitkey = MessagesHelper.format(CONFLICT_MANAGEMENT_KEYS[i], lowerType, traitLC);
      String formatKey = String.format(CONFLICT_MANAGEMENT_RESP_KEYS[i], type);
      response.add(formatKey, MessagesHelper.getMessage(traitkey, locale, value, firstName));
    }
  }
  
  /**
   * <code>processMotivation</code> method will process the content for the motivation and send the
   * data in the json format.
   * 
   * @param response
   *          PS Response
   * @param analysis
   *          analysis
   * @param locale
   *          user locale
   * @param firstName
   *          first name
   * @param genderText
   *          gender text
   */
  private void processMotivationHow(SPResponse response, AnalysisBean analysis, Locale locale,
      String firstName, String[] genderText) {
    
    final Map<TraitType, BigDecimal> motivationHow = analysis.getMotivationHow();
    
    response.add(
        "profile.motivation.why.how.message",
        MessagesHelper.genderNormalize(
            MessagesHelper.getMessage("profile.motivation.why.how.message", locale), firstName,
            genderText).toUpperCase());
    
    addMotivationMessages(firstName, genderText, locale, TraitType.AffirmedByOthers,
        TraitType.SelfAffirmed, 3, motivationHow, response);
    
    addMotivationMessages(firstName, genderText, locale, TraitType.ExchangeOfIdeas,
        TraitType.ReceiveDirection, 4, motivationHow, response);
    
    addMotivationMessages(firstName, genderText, locale, TraitType.Freedom, TraitType.Consistency,
        5, motivationHow, response);
    
    addMotivationMessages(firstName, genderText, locale, TraitType.PrefersProcess,
        TraitType.TaskCompletion, 6, motivationHow, response);
    
    response.add(getAllStaticMessages(CategoryType.Motivation, locale));
  }
  
  /**
   * <code>processMotivation</code> method will process the content for the motivation and send the
   * data in the json format.
   * 
   * @param response
   *          PS Response
   * @param analysis
   *          analysis
   * @param locale
   *          user locale
   * @param firstName
   *          first name
   * @param genderText
   *          gender text
   */
  private void processMotivationWhy(SPResponse response, AnalysisBean analysis, Locale locale,
      String firstName, String[] genderText) {
    
    final Map<TraitType, BigDecimal> motivationWhy = analysis.getMotivationWhy();
    
    addMotivationMessages(firstName, genderText, locale, TraitType.Power, TraitType.Compliance, 0,
        motivationWhy, response);
    
    addMotivationMessages(firstName, genderText, locale, TraitType.AttainmentOfGoals,
        TraitType.RecognitionForEffort, 1, motivationWhy, response);
    
    addMotivationMessages(firstName, genderText, locale, TraitType.Affiliation, TraitType.Activity,
        2, motivationWhy, response);
  }
  
  /**
   * Add the motivation messages.
   * 
   * @param firstName
   *          - first name
   * @param genderText
   *          - gender text
   * @param locale
   *          - locale
   * @param trait1
   *          - trait 1
   * @param trait2
   *          - trait 2
   * @param keyIndex
   *          - index for the keys
   * @param valuesMap
   *          - values map
   * @param response
   *          - response to update
   */
  private void addMotivationMessages(String firstName, String[] genderText, Locale locale,
      TraitType trait1, TraitType trait2, int keyIndex, Map<TraitType, BigDecimal> valuesMap,
      SPResponse response) {
    
    String[] keys = MOTIVATION_KEYS[keyIndex];
    String firstKey = keys[0];
    String secondKey = keys[1];
    String thirdKey = keys[2];
    
    String trait1Str = trait1.toString();
    String trait2Str = trait2.toString();
    
    BigDecimal trait1Value = valuesMap.get(trait1);
    BigDecimal trait2Value = valuesMap.get(trait2);
    
    switch (trait1Value.compareTo(trait2Value)) {
    case 1:
      response.add("profile.motivation." + trait1Str + ".headline", MessagesHelper.getMessage(
          "profile.motivation.headline", locale, MessagesHelper.getMessage(trait1Str, locale)
              .toUpperCase(), trait1Value, MessagesHelper.getMessage(trait2Str, locale)
              .toUpperCase(), trait2Value));
      response.add("profile.motivation." + trait1Str + ".text", MessagesHelper.genderNormalize(
          MessagesHelper.getMessage(firstKey, locale), firstName, genderText));
      break;
    case -1:
      response.add("profile.motivation." + trait1Str + ".headline", MessagesHelper.getMessage(
          "profile.motivation.headline", locale, MessagesHelper.getMessage(trait2Str, locale)
              .toUpperCase(), trait2Value, MessagesHelper.getMessage(trait1Str, locale)
              .toUpperCase(), trait1Value));
      response.add("profile.motivation." + trait1Str + ".text", MessagesHelper.genderNormalize(
          MessagesHelper.getMessage(secondKey, locale), firstName, genderText));
      break;
    case 0:
      response.add("profile.motivation." + trait1Str + ".headline", MessagesHelper.getMessage(
          "profile.motivation.headline", locale, MessagesHelper.getMessage(trait1Str, locale)
              .toUpperCase(), trait1Value, MessagesHelper.getMessage(trait2Str, locale)
              .toUpperCase(), trait2Value));
      response.add("profile.motivation." + trait1Str + ".text", MessagesHelper.genderNormalize(
          MessagesHelper.getMessage(thirdKey, locale), firstName, genderText));
      break;
    default:
      break;
    }
  }
  
  /**
   * <code>processProcessingBluePrint</code> method will populate the content for the blue print
   * 
   * @param response
   *          in which content needs to be sent.
   * @param user
   *          logged in user
   * @param locale
   *          user locale
   * @param genderText
   *          - gender text
   * @param firstName
   *          - first name
   */
  private void processProcessingMessages(SPResponse response, Locale locale, AnalysisBean analysis,
      String firstName, String[] genderText, Gender gender) {
    
    Map<TraitType, BigDecimal> map = analysis.getProcessing();
    
    BigDecimal externalBd = map.get(TraitType.External);
    BigDecimal intennalBd = map.get(TraitType.Internal);
    BigDecimal concreteBd = map.get(TraitType.Concrete);
    BigDecimal intutiveBd = map.get(TraitType.Intuitive);
    BigDecimal congnitiveBd = map.get(TraitType.Cognitive);
    BigDecimal affectiveBd = map.get(TraitType.Affective);
    BigDecimal orderlyBd = map.get(TraitType.Orderly);
    BigDecimal spontaneousBd = map.get(TraitType.Spontaneous);
    
    String externalInternal = addProcessingMessages(locale, externalBd, intennalBd,
        TraitType.External, TraitType.Internal, "processmessage.external.equal.internal");
    
    String concreteIntitute = addProcessingMessages(locale, concreteBd, intutiveBd,
        TraitType.Concrete, TraitType.Intuitive, "processmessage.concrete.equal.intutive");
    
    String congnitiveAffective = null;
    switch (congnitiveBd.compareTo(affectiveBd)) {
    case 0:
      congnitiveAffective = (gender == Gender.M) ? MessagesHelper.getMessage(
          "processmessage.congnitiveAffective.equal.male", locale)
          : ((gender == Gender.F) ? MessagesHelper.getMessage(
              "processmessage.congnitiveAffective.equal.female", locale) : MessagesHelper
              .getMessage("processmessage.congnitiveAffective.equal.neutral", locale));
      break;
    
    case 1:
      congnitiveAffective = (gender == Gender.M) ? MessagesHelper.getMessage(
          "processMessage.congnitive.male", locale) : ((gender == Gender.F) ? MessagesHelper
          .getMessage("processMessage.congnitive.female", locale) : MessagesHelper.getMessage(
          "processMessage.congnitive.neutral", locale));
      break;
    
    case -1:
      congnitiveAffective = (gender == Gender.M) ? MessagesHelper.getMessage(
          "processMessage.affective.male", locale) : ((gender == Gender.F) ? MessagesHelper
          .getMessage("processMessage.affective.female", locale) : MessagesHelper.getMessage(
          "processMessage.affective.neutral", locale));
      break;
    
    default:
      break;
    }
    
    String orderlyProcessing = addProcessingMessages(locale, orderlyBd, spontaneousBd,
        TraitType.Orderly, TraitType.Spontaneous, "processmessage.order.equal.processing");
    
    response.add("profile.processing.summary", MessagesHelper.getMessage(
        "profile.processing.summary", locale, firstName, externalInternal, concreteIntitute,
        congnitiveAffective, orderlyProcessing, genderText[0], genderText[1]));
    
    response.add("profile.processing.external.heading",
        MessagesHelper.getMessage("profile.processing.external.heading", locale, externalBd));
    response.add("profile.processing.internal.heading",
        MessagesHelper.getMessage("profile.processing.internal.heading", locale, intennalBd));
    response.add("profile.processing.concrete.heading",
        MessagesHelper.getMessage("profile.processing.concrete.heading", locale, concreteBd));
    response.add("profile.processing.intutive.heading",
        MessagesHelper.getMessage("profile.processing.intutive.heading", locale, intutiveBd));
    response.add("profile.processing.cognitive.heading",
        MessagesHelper.getMessage("profile.processing.cognitive.heading", locale, congnitiveBd));
    response.add("profile.processing.affective.heading",
        MessagesHelper.getMessage("profile.processing.affective.heading", locale, affectiveBd));
    response.add("profile.processing.orderly.heading",
        MessagesHelper.getMessage("profile.processing.orderly.heading", locale, orderlyBd));
    response.add("profile.processing.spontaneous.heading",
        MessagesHelper.getMessage("profile.processing.spontaneous.heading", locale, spontaneousBd));
    
    response.add(getAllStaticMessages(CategoryType.Processing, locale));
  }
  
  /**
   * Adding the processing messages.
   * 
   * @param locale
   *          - locale
   * @param trait1Value
   *          - trait 1 value
   * @param trait2Value
   *          - trait 2 value
   * @param trait1
   *          - trait1
   * @param trait2
   *          - trait2
   * @param equalMessageKey
   *          - message key to use if the values are equal
   * @return the message to display
   */
  private String addProcessingMessages(Locale locale, BigDecimal trait1Value,
      BigDecimal trait2Value, TraitType trait1, TraitType trait2, String equalMessageKey) {
    
    String message = null;
    switch (trait1Value.compareTo(trait2Value)) {
    case 0:
      message = MessagesHelper.getMessage("processmessage.external.equal.internal", locale);
      break;
    case 1:
      message = "<strong>" + MessagesHelper.getMessage(trait1.toString(), locale).toLowerCase()
          + "</strong>";
      break;
    case -1:
      message = "<strong>" + MessagesHelper.getMessage(trait2.toString(), locale).toLowerCase()
          + "</strong>";
      break;
    default:
      break;
    }
    return message;
  }
  
  /**
   * Get all the static messages for the given category type.
   * 
   * @param type
   *          - category type
   * @param locale
   *          - locale
   * @return the messages
   */
  private Map<String, String> getAllStaticMessages(CategoryType type, Locale locale) {
    return getAllStaticMessages(type, locale, null);
  }
  
  /**
   * Get all the static messages for the given category type.
   * 
   * @param type
   *          - category type
   * @param locale
   *          - locale
   * @param keySet
   *          - keys
   * @return the messages
   */
  private Map<String, String> getAllStaticMessages(CategoryType type, Locale locale,
      Set<TraitType> keySet) {
    Map<String, String> messages = profileStaticMessages.get(type + locale.toString());
    if (messages == null) {
      messages = new HashMap<String, String>();
      switch (type) {
      case Personality:
        messages.put("profile.personality.primary.heading",
            MessagesHelper.getMessage("profile.personality.primary.heading", locale));
        messages.put("profile.personality.primary.summary",
            MessagesHelper.getMessage("profile.personality.primary.summary", locale));
        messages.put("profile.personality.underPressure.heading",
            MessagesHelper.getMessage("profile.personality.underPressure.heading", locale));
        messages.put("profile.personality.underPressure.summary",
            MessagesHelper.getMessage("profile.personality.underPressure.summary", locale));
        messages.put("profile.personality.WorkEnvironmentContribution",
            MessagesHelper.getMessage("profile.personality.WorkEnvironmentContribution", locale));
        messages.put("profile.personality.EmotionalPosture",
            MessagesHelper.getMessage("profile.personality.EmotionalPosture", locale));
        messages.put("profile.personality.DrivingIdeal",
            MessagesHelper.getMessage("profile.personality.DrivingIdeal", locale));
        messages.put("profile.personality.AssessesOthersBy",
            MessagesHelper.getMessage("profile.personality.AssessesOthersBy", locale));
        messages.put("profile.personality.MotivationalStyle",
            MessagesHelper.getMessage("profile.personality.MotivationalStyle", locale));
        messages.put("profile.personality.MostFavorableWorkEnvironment",
            MessagesHelper.getMessage("profile.personality.MostFavorableWorkEnvironment", locale));
        messages.put("profile.personality.RelationalTaskOrientation",
            MessagesHelper.getMessage("profile.personality.RelationalTaskOrientation", locale));
        messages.put("profile.personality.MobilityPreference",
            MessagesHelper.getMessage("profile.personality.MobilityPreference", locale));
        messages.put("profile.personality.EnvironmentalStressors",
            MessagesHelper.getMessage("profile.personality.EnvironmentalStressors", locale));
        messages.put("profile.personality.OverUses",
            MessagesHelper.getMessage("profile.personality.OverUses", locale));
        messages.put("profile.personality.WhenPressured",
            MessagesHelper.getMessage("profile.personality.WhenPressured", locale));
        messages.put("profile.personality.UneasyWhen",
            MessagesHelper.getMessage("profile.personality.UneasyWhen", locale));
        messages.put("profile.personality.Leadership",
            MessagesHelper.getMessage("profile.personality.Leadership", locale));
        messages.put("profile.personality.SummaryOfStrengths",
            MessagesHelper.getMessage("profile.personality.SummaryOfStrengths", locale));
        messages.put("profile.personality.JobMatch",
            MessagesHelper.getMessage("profile.personality.JobMatch", locale));
        messages.put("profile.personality.ToCommunicateWithAsLeader",
            MessagesHelper.getMessage("profile.personality.ToCommunicateWithAsLeader", locale));
        messages.put("profile.personality.ToDisagreeWithAsLeader",
            MessagesHelper.getMessage("profile.personality.ToDisagreeWithAsLeader", locale));
        messages.put("profile.personality.PossibleNegativePerceptionsOfOthers", MessagesHelper
            .getMessage("profile.personality.PossibleNegativePerceptionsOfOthers", locale));
        messages.put("profile.personality.NegativePerceptionsOthersMayHave", MessagesHelper
            .getMessage("profile.personality.NegativePerceptionsOthersMayHave", locale));
        messages.put("profile.personality.NotableCharacteristics",
            MessagesHelper.getMessage("profile.personality.NotableCharacteristics", locale));
        messages.put("profile.personality.FundamentalDesiresDrive",
            MessagesHelper.getMessage("profile.personality.FundamentalDesiresDrive", locale));
        messages.put("profile.personality.HowToSuperviseAndEncourage",
            MessagesHelper.getMessage("profile.personality.HowToSuperviseAndEncourage", locale));
        messages.put("profile.personality.RecommendedImprovements",
            MessagesHelper.getMessage("profile.personality.RecommendedImprovements", locale));
        messages.put("profile.personality.dimension.text",
            MessagesHelper.getMessage("profile.personality.dimension.text", locale));
        break;
      case Processing:
        messages.put("profile.processing.spontaneous.text",
            MessagesHelper.getMessage("profile.processing.spontaneous.text", locale));
        messages.put("profile.processing.cognitive.affective.summary",
            MessagesHelper.getMessage("profile.processing.cognitive.affective.summary", locale));
        messages.put("profile.processing.concrete.intutive.summary",
            MessagesHelper.getMessage("profile.processing.concrete.intutive.summary", locale));
        messages.put("profile.processing.external.intenral.summary",
            MessagesHelper.getMessage("profile.processing.external.intenral.summary", locale));
        messages.put("profile.processing.orderly.spontaneous.summary",
            MessagesHelper.getMessage("profile.processing.orderly.spontaneous.summary", locale));
        messages.put("profile.processing.trait.message",
            MessagesHelper.getMessage("profile.processing.trait.message", locale));
        messages.put("profile.processing.heading",
            MessagesHelper.getMessage("profile.processing.heading", locale));
        String internalTrait = MessagesHelper.getMessage(TraitType.Internal.toString(), locale);
        String externalTrait = MessagesHelper.getMessage(TraitType.External.toString(), locale);
        messages.put("profile.processing.internal.external.heading", MessagesHelper.getMessage(
            "profile.processing.vs", locale, externalTrait.toUpperCase(),
            internalTrait.toUpperCase()));
        
        String cognitiveTrait = MessagesHelper.getMessage(TraitType.Cognitive.toString(), locale);
        String affectiveTrait = MessagesHelper.getMessage(TraitType.Affective.toString(), locale);
        messages.put("profile.processing.cognitive.affective.heading", MessagesHelper.getMessage(
            "profile.processing.vs", locale, cognitiveTrait.toUpperCase(),
            affectiveTrait.toUpperCase()));
        
        String spontaneousTrait = MessagesHelper.getMessage(TraitType.Spontaneous.toString());
        String orderlyTrait = MessagesHelper.getMessage(TraitType.Orderly.toString());
        messages.put("profile.processing.orderly.spontaneous.heading", MessagesHelper.getMessage(
            "profile.processing.vs", locale, orderlyTrait.toUpperCase(),
            spontaneousTrait.toUpperCase()));
        
        String concreteTrait = MessagesHelper.getMessage(TraitType.Concrete.toString(), locale);
        String intutiveTrait = MessagesHelper.getMessage(TraitType.Intuitive.toString(), locale);
        messages.put("profile.processing.concrete.intutive.heading", MessagesHelper.getMessage(
            "profile.processing.vs", locale, concreteTrait.toUpperCase(),
            intutiveTrait.toUpperCase()));
        
        messages.put("profile.processing.orderly.text",
            MessagesHelper.getMessage("profile.processing.orderly.text", locale));
        messages.put("profile.processing.affective.text",
            MessagesHelper.getMessage("profile.processing.affective.text", locale));
        messages.put("profile.processing.cognitive.text",
            MessagesHelper.getMessage("profile.processing.cognitive.text", locale));
        messages.put("profile.processing.concrete.text",
            MessagesHelper.getMessage("profile.processing.concrete.text", locale));
        messages.put("profile.processing.intutive.text",
            MessagesHelper.getMessage("profile.processing.intutive.text", locale));
        messages.put("profile.processing.external.text",
            MessagesHelper.getMessage("profile.processing.external.text", locale));
        messages.put("profile.processing.internal.text",
            MessagesHelper.getMessage("profile.processing.internal.text", locale));
        break;
      case Motivation:
        messages.put("profile.motivation.heading",
            MessagesHelper.getMessage("profile.motivation.heading", locale));
        messages.put("profile.motivation.paragraph",
            MessagesHelper.getMessage("profile.motivation.paragraph", locale));
        messages.put("profile.motivation.why",
            MessagesHelper.getMessage("profile.motivation.why", locale));
        messages.put("profile.motivation.how",
            MessagesHelper.getMessage("profile.motivation.how", locale));
        break;
      case ConflictManagement:
        messages.put("profile.cm.heading", MessagesHelper.getMessage("profile.cm.heading", locale));
        messages.put("profile.cm.heading.summary1",
            MessagesHelper.getMessage("profile.cm.heading.summary1", locale));
        messages.put("profile.cm.learnmore",
            MessagesHelper.getMessage("profile.cm.learnmore", locale));
        break;
      case DecisionMaking:
        for (TraitType traitType : keySet) {
          final String traitLC = traitType.toString().toLowerCase();
          for (String key : DECISION_MAKING_KEYS) {
            String format = String.format(key, traitLC);
            messages.put(format, MessagesHelper.getMessage(format, locale));
          }
        }
        messages.put("profile.dm.heading", MessagesHelper.getMessage("profile.dm.heading", locale));
        messages.put("profile.dm.summary", MessagesHelper.getMessage("profile.dm.summary", locale));
        messages.put("profile.dm.invaward.vs.outward",
            MessagesHelper.getMessage("profile.dm.invaward.vs.outward", locale));
        messages.put("profile.dm.inward.vs.outward.text",
            MessagesHelper.getMessage("profile.dm.inward.vs.outward.text", locale));
        messages.put("profile.dm.learnmore",
            MessagesHelper.getMessage("profile.dm.learnmore", locale));
        messages.put("profile.dm.note.text",
            MessagesHelper.getMessage("profile.dm.note.text", locale));
        messages.put("profile.dm.careful.vs.rapid",
            MessagesHelper.getMessage("profile.dm.careful.vs.rapid", locale));
        messages.put("profile.dm.careful.vs.rapid.text",
            MessagesHelper.getMessage("profile.dm.careful.vs.rapid.text", locale));
        break;
      case FundamentalNeeds:
        messages.put("profile.fm.heading", MessagesHelper.getMessage("profile.fm.heading", locale));
        messages.put("profile.fm.summary", MessagesHelper.getMessage("profile.fm.summary", locale));
        
        String[] keys = { "profile.fm.%s.heading", "profile.fm.%s.p1" };
        for (TraitType traitType : keySet) {
          String traitStrLC = traitType.toString().toLowerCase();
          for (String key : keys) {
            String format = String.format(key, traitStrLC);
            messages.put(format, MessagesHelper.getMessage(format, locale));
          }
        }
        messages.put("profile.fm.learnmore",
            MessagesHelper.getMessage("profile.fm.learnmore", locale));
        messages.put("profile.fm.learnmore",
            MessagesHelper.getMessage("profile.fm.learnmore", locale));
        messages.put("profile.fm.principles.heading",
            MessagesHelper.getMessage("profile.fm.principles.heading", locale));
        messages.put("profile.fm.principles.p1",
            MessagesHelper.getMessage("profile.fm.principles.p1", locale));
        messages.put("profile.fm.principles.p2",
            MessagesHelper.getMessage("profile.fm.principles.p2", locale));
        break;
      case LearningStyle:
        messages.put("profile.learning.heading",
            MessagesHelper.getMessage("profile.learning.heading", locale));
        for (TraitType trait : keySet) {
          String traitStrLC = trait.toString().toLowerCase();
          for (String key : LEARNING_STYLE_KEYS) {
            String formattedKey = String.format(key, traitStrLC);
            messages.put(formattedKey, MessagesHelper.getMessage(formattedKey, locale));
          }
        }
        messages.put("profile.learning.global.vs.analytical",
            MessagesHelper.getMessage("profile.learning.global.vs.analytical", locale));
        messages.put("profile.learning.global.vs.analytical.p1",
            MessagesHelper.getMessage("profile.learning.global.vs.analytical.p1", locale));
        messages.put("profile.learning.global.vs.analytical.p2",
            MessagesHelper.getMessage("profile.learning.global.vs.analytical.p2", locale));
        break;
      default:
        break;
      }
      if (!messages.isEmpty()) {
        profileStaticMessages.put(type + locale.toString(), messages);
      }
    }
    return messages;
  }
  
  /**
   * This method only gets the values but does not create the messages.
   * 
   * @param type
   *          - category type
   * @param locale
   *          - locale
   * @return the messages from the static messages map
   */
  private Map<String, String> getStaticMessages(CategoryType type, Locale locale) {
    return profileStaticMessages.get(type + locale.toString());
  }
  
  /**
   * Reset the cache for static messages.
   */
  public static void resetCache() {
    profileStaticMessages.clear();
  }
  
}
