package com.sp.web.mvc.test.setup;

import static org.junit.Assert.assertTrue;

import com.sp.web.assessment.personality.PersonalityBeanResponse;
import com.sp.web.assessment.personality.PersonalityType;
import com.sp.web.assessment.personality.RangeType;
import com.sp.web.dao.goal.UserGoalDao;
import com.sp.web.dto.PaymentInstrumentDTO;
import com.sp.web.model.Account;
import com.sp.web.model.AccountType;
import com.sp.web.model.Address;
import com.sp.web.model.FeedbackUser;
import com.sp.web.model.Gender;
import com.sp.web.model.HiringUser;
import com.sp.web.model.PaymentInstrument;
import com.sp.web.model.PaymentRecord;
import com.sp.web.model.PaymentType;
import com.sp.web.model.Promotion;
import com.sp.web.model.PromotionType;
import com.sp.web.model.RoleType;
import com.sp.web.model.SPMedia;
import com.sp.web.model.User;
import com.sp.web.model.UserStatus;
import com.sp.web.model.UserType;
import com.sp.web.model.account.SPPlan;
import com.sp.web.model.account.SPPlanType;
import com.sp.web.model.hiring.user.HiringUserArchive;
import com.sp.web.promotions.PromotionStatus;
import com.sp.web.service.goals.SPGoalFactoryHelper;
import com.sp.web.test.setup.AuthenticationHelper;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

/**
 * @author Dax Abraham The logged in version of the SPTestBase.
 */
public class SPTestLoggedInBase extends SPTestBase {
  
  @Autowired
  protected AuthenticationHelper authenticationHelper;
  
  @Autowired
  protected SPGoalFactoryHelper goalsFactory;
  
  boolean isAuthenticationDone = false;
  
  static {
    System.setProperty("appPropsFile", "./properties/test/");
  }
  
  @Before
  public void setUp() throws Exception {
    
    try {
      // login the user
      if (!isAuthenticationDone) {
        authenticationHelper.authenticateUser(session);
        isAuthenticationDone = true;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  @Test
  public void testDoNothing() {
    assertTrue(true);
  }
  
  protected void updateProductsAndPaymentInstrument(String accountId, String[] prodArray) {
    Account account = dbSetup.getAccount(accountId);
    account.setProducts(Arrays.asList(prodArray));
    updatePI(account);
    dbSetup.addUpdate(account);
  }
  
  protected void updatePI(Account account) {
    PaymentInstrument pi = new PaymentInstrument();
    pi.setCardNumber("12345 12345 12344 1234");
    pi.setCardType("MC");
    dbSetup.addUpdate(pi);
    account.setPaymentInstrumentId(pi.getId());
  }
  
  protected Promotion updatePromo(String accountId, String productId, double unitPrice) {
    return updatePromo(accountId, productId, unitPrice, PromotionStatus.Active, "1000");
  }
  
  protected Promotion updatePromo(String accountId, String productId, double unitPrice,
      PromotionStatus promoStatus, String promoId) {
    Promotion promo = new Promotion();
    promo.setId(promoId);
    promo.setCode(promoId);
    LocalDate dateTime = LocalDate.now();
    promo.setStartDate(dateTime.minusDays(10));
    promo.setEndDate(dateTime.plusDays(10));
    promo.setPromotionType(PromotionType.TIME_BASED);
    promo.setProductIdList(Arrays.asList(productId));
    promo.setName("Product Promo" + promoId);
    promo.setUnitPrice(unitPrice);
    promo.setStatus(PromotionStatus.Active);
    dbSetup.addUpdate(promo);
    Account account = dbSetup.getAccount(accountId);
    account.addPromotion(promo);
    dbSetup.addUpdate(account);
    return promo;
  }
  
  protected PaymentRecord updatePaymentRecord(String accountId, SPPlanType spPlanType) {
    PaymentRecord paymentRecord = new PaymentRecord();
    paymentRecord.setAmount(500.0);
    paymentRecord.setId("1");
    paymentRecord.setCreatedOn(new Date());
    paymentRecord.setReason("Money for nothing !!!");
    Account account = dbSetup.getAccount(accountId);
    
    SPPlan spPlan = account.getSpPlanMap().get(spPlanType);
    String paymentInstrumentId = spPlan.getPaymentInstrumentId();
    PaymentInstrument paymentInstrument = dbSetup.getPaymentInstrument(paymentInstrumentId);
    PaymentInstrumentDTO instrumentDTO = new PaymentInstrumentDTO(paymentInstrument);
    instrumentDTO.setPaymentType(PaymentType.CREDIT_CARD);
    paymentRecord.setPaymentInstrument(instrumentDTO);
    dbSetup.addUpdate(paymentRecord);
    
    account.setLastPaymentId(paymentRecord.getId());
    dbSetup.addUpdate(account);
    return paymentRecord;
  }
  
  protected Account updateIndividualUser(String productId, String accountId) {
    updateProductsAndPaymentInstrument(accountId, new String[] { productId });
    Account account = dbSetup.getAccount(accountId);
    
    account.setType(AccountType.Individual);
    dbSetup.addUpdate(account);
    
    User user = dbSetup.getUser("dax@surepeople.com");
    user.setCompanyId(null);
    user.setAccountId(accountId);
    user.addRole(RoleType.IndividualAccountAdministrator);
    user.setUserStatus(UserStatus.VALID);
    dbSetup.addUpdate(user);
    
    User userForLogin = new User();
    userForLogin.setEmail("dax@surepeople.com");
    userForLogin.setPassword("pwd123");
    
    authenticationHelper.doAuthenticate(session2, userForLogin);
    return account;
  }
  
  protected HiringUser addHiringCandidate() {
    return addHiringCandidate("dax@einstix.com");
  }
  
  protected HiringUser addHiringCandidate(String email) {
    // adding a hiring candidate
    HiringUser user = new HiringUser();
    user.setFirstName("Aisha");
    user.setLastName("Abraham");
    user.setCompanyId("1");
    user.setEmail(email);
    user.setPhoneNumber("+919818399147");
    user.setUserStatus(UserStatus.INVITATION_SENT);
    // user.addRole(RoleType.HiringCandidate);
    user.setTagList(Arrays.asList(new String[] { "Tag1", "Tag2" }));
    Address address = new Address();
    address.setAddressLine1("Address Line 1");
    address.setAddressLine2("Address Line 2");
    address.setCity("City");
    address.setState("State");
    address.setCountry("Country");
    address.setZipCode("Zip");
    user.setAddress(address);
    user.setAssessmentDueDate(LocalDate.now().plusMonths(1));
    HashSet<String> hiringRoles = new HashSet<String>();
    hiringRoles.add("CEO");
    hiringRoles.add("Board");
    user.setHiringRoleIds(hiringRoles);
    user.setLinkedInUrl("http://linkedin.com/aishaAbraham");
    SPMedia media = new SPMedia();
    media.setUrl("http://linkedin.com/aishaAbraham");
    user.addProfileUrl(media);
    user.setTokenUrl("http://uat.surepeople.com/token/someToken");
    user.setCreatedOn(LocalDate.now());
    user.setType(UserType.Member);
    dbSetup.addUpdate(user);
    return user;
  }
  
  protected HiringUserArchive addHiringCandidateArchive() {
    // adding a hiring candidate
    HiringUserArchive user = new HiringUserArchive();
    user.setFirstName("Aisha");
    user.setLastName("Abraham");
    user.setCompanyId("1");
    user.setEmail("aisha-archive@surepeople.com");
    user.setUserStatus(UserStatus.INVITATION_SENT);
    user.addRole(RoleType.HiringCandidate);
    user.setTagList(Arrays.asList(new String[] { "Tag1", "Tag2" }));
    Address address = new Address();
    address.setAddressLine1("Address Line 1");
    address.setAddressLine2("Address Line 2");
    address.setCity("City");
    address.setState("State");
    address.setCountry("Country");
    address.setZipCode("Zip");
    user.setAddress(address);
    user.setAssessmentDueDate(LocalDate.now().plusMonths(1));
    HashSet<String> hiringRoles = new HashSet<String>();
    hiringRoles.add("CEO");
    hiringRoles.add("HR");
    user.setHiringRoleIds(hiringRoles);
    user.setLinkedInUrl("http://linkedin.com/aishaAbraham");
    user.setGender(Gender.F);
    user.setDob(LocalDate.of(2007, 3, 23));
    user.setType(UserType.Member);
    dbSetup.addUpdate(user);
    return user;
  }
  
  /**
   * Add the user goals for the user.
   * 
   * @param authUser
   *          - auth user
   * @return the user goals dao
   */
  public UserGoalDao addUserGoals(User authUser) {
    goalsFactory.addGoalsForUser(authUser);
    UserGoalDao userGoal = goalsFactory.getUserGoal(authUser);
    userGoal.getGoalsProgressMap().values().forEach(gp -> {
      goalsFactory.selectGoalForUser(authUser, gp.getGoal().getId());
    });
    userGoal = goalsFactory.getUserGoal(authUser);
    return userGoal;
  }
  
  /**
   * Method to add the feedback goals for the user.
   * 
   * @param fbUserId
   *          - fb user id
   * @param user
   *          - user
   */
  public void addFeedbackGoalsToUser(String fbUserId, User user) {
    FeedbackUser fbUser = dbSetup.getFeedbackUserById(fbUserId);
    HashMap<RangeType, PersonalityBeanResponse> personality = fbUser.getAnalysis().getPersonality();
    PersonalityBeanResponse personalityBeanResponse = personality.get(RangeType.Primary);
    personalityBeanResponse.setPersonalityType(PersonalityType.Refiner);
    personalityBeanResponse = personality.get(RangeType.UnderPressure);
    personalityBeanResponse.setPersonalityType(PersonalityType.Motivator);
    goalsFactory.addGoalsForUser(fbUser);
    goalsFactory.addFeedbackUserGoals((FeedbackUser) fbUser, user);
  }
  
  protected void startMail() {
    testSmtp.start();
  }
  
  protected void stopMail() {
    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    testSmtp.stop();
  }
  
}
