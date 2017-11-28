package com.sp.web.repository.user;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;
import com.sp.web.Constants;
import com.sp.web.account.ExpiryAccountHelper;
import com.sp.web.dto.UserDTO;
import com.sp.web.exception.InvalidRequestException;
import com.sp.web.form.AddressForm;
import com.sp.web.form.UserProfileForm;
import com.sp.web.model.DeletedUser;
import com.sp.web.model.ExternalUser;
import com.sp.web.model.FeatureType;
import com.sp.web.model.FeedbackUser;
import com.sp.web.model.FeedbackUserArchive;
import com.sp.web.model.GroupAssociation;
import com.sp.web.model.HiringUser;
import com.sp.web.model.MasterPassword;
import com.sp.web.model.RoleType;
import com.sp.web.model.SPFeature;
import com.sp.web.model.SequenceId;
import com.sp.web.model.User;
import com.sp.web.model.UserGroup;
import com.sp.web.model.UserProfileSettings;
import com.sp.web.model.UserStatus;
import com.sp.web.model.account.SPPlanType;
import com.sp.web.model.hiring.user.HiringUserArchive;
import com.sp.web.product.AccountFactory;
import com.sp.web.repository.team.GroupRepository;
import com.sp.web.service.hiring.user.HiringUserFactory;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;

/**
 * @author pradeep
 * 
 *         The mongo repository for the user entity processing.
 */
@Repository
public class MongoUserRepository implements UserRepository {
  
  private static final String GROUP_LIST = "groupAssociationList";
  private static final String EMAIL = "email";
  
  /**
   * Intializing the logger private static final Logger LOG = Logger
   * .getLogger(MongoAccountRepository.class);
   */
  
  /** Mongo Template for accessing data from mongo. */
  private final MongoTemplate mongoTemplate;
  
  @Autowired
  GroupRepository groupRepoistory;
  
  @Autowired
  private AccountFactory accountFactory;
  
  @Autowired
  private ExpiryAccountHelper expiryAccountHelper;
  
  @Autowired
  private HiringUserFactory hiringUserFactory;
  
  @Inject
  public MongoUserRepository(MongoTemplate mongoTemplate) {
    this.mongoTemplate = mongoTemplate;
  }
  
  /**
   * <code>findByEmail</code> method will find the users with email present.
   * 
   * @see com.sp.web.assessment.account.AccountRepository#findByEmail(java.lang .String)
   */
  @Override
  public User findByEmail(String email) {
    return mongoTemplate.findOne(query(where(EMAIL).is(email)), User.class);
  }
  
  @Override
  public User findByEmail(String email, String companyId) {
    Assert.hasText(email, "Email is required.");
    Assert.hasText(companyId, "Company id is required.");
    return mongoTemplate
        .findOne(
            query(where(EMAIL).is(email).andOperator(
                where(Constants.ENTITY_COMPANY_ID).is(companyId))), User.class);
  }
  
  @Override
  public List<User> findByEmail(List<String> userList) {
    
    if (userList == null) {
      return new ArrayList<User>();
    }
    
    return mongoTemplate.find(query(where(EMAIL).in(userList)), User.class);
  }
  
  @Override
  public User findByEmailValidated(String userEmail) {
    return getValidatedUser(userEmail);
  }
  
  @Override
  public User findByEmailValidated(String memberEmail, String companyId) {
    return Optional.ofNullable(findByEmail(memberEmail, companyId)).orElseThrow(
        () -> new InvalidRequestException("Member :" + memberEmail + ": not found in company."));
  }
  
  @Override
  public User findByEmailAndRole(String email, RoleType role) {
    Assert.hasText(email, "Email is required.");
    Assert.notNull(role, "Role is required.");
    return mongoTemplate.findOne(
        query(where(EMAIL).is(email).andOperator(where(Constants.ENTITY_USER_ROLES).in(role))),
        User.class);
  }
  
  @Override
  public User createUser(User user) {
    mongoTemplate.save(user);
    return user;
  }
  
  /**
   * @see AccountRepository#updatePassword(String, String).
   */
  @Override
  public boolean updatePassword(String email, String password) {
    Update objToUpdate = new Update();
    objToUpdate.set("password", password);
    mongoTemplate.updateFirst(query(where("email").is(email)), objToUpdate, User.class);
    return Boolean.TRUE;
  }
  
  @Override
  public void deleteUser(User userToDelete) {
    
    // deleting the user group associations
    if (userToDelete.getGroupAssociationList() != null) {
      userToDelete.getGroupAssociationList().forEach(
          ga -> deleteFromGroupAssociation(userToDelete, ga));
    }
    
    if (userToDelete.getAnalysis() != null) {
      DeletedUser deletedUser = new DeletedUser(userToDelete);
      mongoTemplate.save(deletedUser);
      expiryAccountHelper.expireUser(userToDelete);
    } else {
      // As per comment during manage account, users who have taken prism lens, that member
      // subscription will not be given to them in case user is deleted. */
      accountFactory.releaseMemberSubscription(userToDelete.getCompanyId(), SPPlanType.Primary);
    }
    
    // In case the user is an people analytics admin, cannot deelte the suer from the system, all
    // the roles will be remove for the user. */
    // deleting the user
    if (userToDelete.hasRole(RoleType.Hiring)) {
      for (SPFeature spFeature : SPPlanType.Primary.getFeatures()) {
        List<RoleType> rolesToBeRemoved = Arrays.asList(spFeature.getRoles());
        userToDelete.getRoles().removeAll(rolesToBeRemoved);
        userToDelete.removeRole(RoleType.User);
        mongoTemplate.save(userToDelete);
        
        /* get the hiring user and udate the in erti flag */
        HiringUser hiringUser = hiringUserFactory.getUserByEmail(userToDelete.getEmail(),
            userToDelete.getCompanyId());
        if (hiringUser != null) {
          hiringUser.setInErti(false);
          hiringUserFactory.updateUser(hiringUser);
        }
        
        /* update the archive, incase employee is archived. */
        HiringUserArchive hiringUserArchive = hiringUserFactory.getArchivedUserByEmail(
            userToDelete.getEmail(), userToDelete.getCompanyId());
        if (hiringUserArchive != null) {
          hiringUserArchive.setInErti(false);
          hiringUserFactory.updateUser(hiringUserArchive);
        }
        
      }
    } else {
      mongoTemplate.remove(userToDelete);
    }
    
  }
  
  @Override
  public User updateUserAnalysis(User user) {
    Update objToUpdate = new Update();
    objToUpdate.set("analysis", user.getAnalysis());
    objToUpdate.set("userStatus", user.getUserStatus());
    mongoTemplate.upsert(query(where("id").is(user.getId())), objToUpdate, User.class);
    return user;
  }
  
  /**
   * Removes the user from the user group.
   * 
   * @param user
   *          - user to remove
   * @param ga
   *          - group association
   * @return true if group updated
   */
  private boolean deleteFromGroupAssociation(User user, GroupAssociation ga) {
    UserGroup group = groupRepoistory.findByName(user.getCompanyId(), ga.getName());
    boolean toUpdate = false;
    
    if (group != null) {
      if (ga.isGroupLead() && user.getEmail().equalsIgnoreCase(group.getGroupLead())) {
        group.setGroupLead(null);
        toUpdate = true;
      }
      
      if (group.getMemberList() != null && group.getMemberList().remove(user.getEmail())) {
        toUpdate = true;
      }
      
      if (toUpdate) {
        groupRepoistory.save(group);
      }
    }
    return toUpdate;
  }
  
  @Override
  public UserDTO getUserSummary(String userEmail) {
    return getUserSummary(getValidatedUser(userEmail));
  }
  
  @Override
  public List<UserDTO> getUserSummary(List<String> userEmailList) {
    // get all the users with the given user email id's
    List<User> userList = findByEmail(userEmailList);
    
    // create user summary list to send
    List<UserDTO> userSummaryList = new ArrayList<UserDTO>(userList.size());
    
    for (User u : userList) {
      // add the summary to the summary
      userSummaryList.add(getUserSummary(u));
    }
    
    return userSummaryList;
  }
  
  /**
   * Gets the user summary for the given user.
   * 
   * @param user
   *          - user
   * @return the user summary
   */
  private UserDTO getUserSummary(User user) {
    return new UserDTO(user);
  }
  
  /**
   * Gets a user from the given user email if not found throws an InvalidRequestException.
   * 
   * @param userEmail
   *          - user email
   * @return the user
   */
  private User getValidatedUser(String userEmail) {
    User user = findByEmail(userEmail);
    if (user == null) {
      throw new InvalidRequestException("User :" + userEmail + ": not found !!!");
    }
    return user;
  }
  
  @Override
  public User updateUserProfile(UserProfileForm userProfile, AddressForm address) {
    
    // get the user object to update
    User userToUpdate = getValidatedUser(userProfile.getEmail());
    
    // update the user information
    userProfile.update(userToUpdate);
    // update the address information
    address.update(userToUpdate);
    
    if (userToUpdate.getUserStatus() == UserStatus.PROFILE_INCOMPLETE) {
      if (userToUpdate.getAnalysis() == null) {
        // updating the profile to assessment pending
        userToUpdate.setUserStatus(UserStatus.ASSESSMENT_PENDING);
      } else {
        userToUpdate.setUserStatus(UserStatus.VALID);
      }
    }
    // save the object to the database
    mongoTemplate.save(userToUpdate);
    
    return userToUpdate;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see com.sp.web.account.AccountRepository#addRole(com.sp.web.model.User,
   * com.sp.web.model.RoleType)
   */
  @Override
  public User addRole(User userToUpdate, RoleType roleToAdd) {
    if (userToUpdate.getRoles().contains(roleToAdd)) {
      throw new InvalidRequestException("User " + userToUpdate.getEmail()
          + " already has the role " + roleToAdd + " !!!");
    }
    Update updateObj = new Update();
    Set<RoleType> userRoles = userToUpdate.getRoles();
    userRoles.add(roleToAdd);
    updateObj.set("roles", userRoles);
    mongoTemplate.updateFirst(query(where("id").is(userToUpdate.getId())), updateObj, User.class);
    
    return userToUpdate;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see com.sp.web.account.AccountRepository#removeRole(com.sp.web.model.User,
   * com.sp.web.model.RoleType)
   */
  @Override
  public User removeRole(User userToUpdate, RoleType roleToRemove) {
    if (!userToUpdate.getRoles().remove(roleToRemove)) {
      throw new InvalidRequestException("User " + userToUpdate.getEmail()
          + " does not have the role " + roleToRemove + " !!!");
    }
    Update updateObj = new Update();
    updateObj.set("roles", userToUpdate.getRoles());
    mongoTemplate.updateFirst(query(where("id").is(userToUpdate.getId())), updateObj, User.class);
    
    return userToUpdate;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see com.sp.web.account.AccountRepository#addTag(com.sp.web.model.User, java.lang.String)
   */
  @Override
  public User addTag(User userToModify, String tagName) {
    
    // check if the tag list and tag already exists for the user
    List<String> tagList = userToModify.getTagList();
    if (tagList == null) {
      tagList = new ArrayList<String>();
      userToModify.setTagList(tagList);
    } else if (userToModify.getTagList().contains(tagName)) {
      throw new InvalidRequestException("User " + userToModify.getEmail() + " already has the tag "
          + tagName + " !!!");
    }
    
    // adding the tag and persisting to DB
    tagList.add(tagName);
    
    Update updateObj = new Update();
    updateObj.set("tagList", tagList);
    mongoTemplate.updateFirst(query(where("id").is(userToModify.getId())), updateObj, User.class);
    
    return userToModify;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see com.sp.web.account.AccountRepository#removeTag(com.sp.web.model.User, java.lang.String)
   */
  @Override
  public User removeTag(User userToModify, String tagName) {
    List<String> tagList = userToModify.getTagList();
    
    if (tagList == null || !tagList.remove(tagName)) {
      throw new InvalidRequestException("User " + userToModify.getEmail()
          + " does not have the tag " + tagName + " !!!");
    }
    
    Update updateObj = new Update();
    updateObj.set("tagList", tagList);
    mongoTemplate.updateFirst(query(where("id").is(userToModify.getId())), updateObj, User.class);
    
    return userToModify;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see com.sp.web.account.AccountRepository#addToGroup(com.sp.web.model.User, java.lang.String,
   * boolean)
   */
  @Override
  public User addGroupAssociation(User userToModify, UserGroup group, boolean isGroupLead) {
    
    // add the group association for the user
    // NOTE: the add group association also checks if there are any
    // existing group associations for the particular group before adding
    userToModify.addGroupAssociation(new GroupAssociation(group, isGroupLead));
    updateUserAfterGroupAssociationChange(userToModify);
    
    return userToModify;
  }
  
  /**
   * Helper method to update the user after the group associations have been changed.
   * 
   * @param userToModify
   *          - user to update
   */
  private void updateUserAfterGroupAssociationChange(User userToModify) {
    Update updateObj = new Update();
    updateObj.set(GROUP_LIST, userToModify.getGroupAssociationList());
    updateObj.set(Constants.ENTITY_USER_ROLES, userToModify.getRoles());
    mongoTemplate.updateFirst(query(where("id").is(userToModify.getId())), updateObj, User.class);
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see com.sp.web.account.AccountRepository#removeGroupAssociation(com.sp.web .model.User,
   * java.lang.String)
   */
  @Override
  public User removeGroupAssociation(User userToModify, String groupName) {
    if (userToModify.removeGroupAssociation(new GroupAssociation(groupName))) {
      
      updateUserAfterGroupAssociationChange(userToModify);
    }
    
    return userToModify;
  }
  
  @Override
  public boolean removeUser(User userToRemove) {
    WriteResult result = mongoTemplate.remove(userToRemove);
    return (result.getN() > 1);
  }
  
  /**
   * <code>updateProfileImage</code> will update the user profile image.
   * 
   * @param imageToUpdate
   *          for the user
   */
  @Override
  public User updateProfileImage(User imageToUpdate) {
    Update objToUpdate = new Update();
    objToUpdate.set("profileImage", imageToUpdate.getProfileImage());
    mongoTemplate.updateFirst(query(where("email").is(imageToUpdate.getEmail())), objToUpdate,
        User.class);
    return imageToUpdate;
  }
  
  /**
   * 
   * @see com.sp.web.repository.user.UserRepository#updateUserStatus(com.sp.web.model.User)
   */
  @Override
  public User updateUserStatus(User user) {
    Update objToUpdate = new Update();
    objToUpdate.set("userStatus", user.getUserStatus());
    mongoTemplate.updateFirst((query(where("email").is(user.getEmail()))), objToUpdate, User.class);
    return user;
  }
  
  @Override
  public long getNumberOfMembers(String companyId, SPPlanType planType) {
    return getNumberOfActiveMembers(companyId, planType)
        + getNumberOfDeletedMembers(companyId, planType);
  }
  
  @Override
  public int getNumberOfActiveMembers(String companyId, SPPlanType planType) {
    RoleType planRole = planType == SPPlanType.IntelligentHiring ? RoleType.Hiring : RoleType.User;
    return (int) mongoTemplate.count(
        Query.query(Criteria.where("companyId").is(companyId).and(Constants.ENTITY_USER_ROLES)
            .in(planRole)), User.class);
  }
  
  @Override
  public int getNumberOfDeletedMembers(String companyId, SPPlanType planType) {
    RoleType planRole = planType == SPPlanType.IntelligentHiring ? RoleType.Hiring : RoleType.User;
    return (int) mongoTemplate.count(
        Query.query(Criteria.where("companyId").is(companyId).and(Constants.ENTITY_USER_ROLES)
            .in(planRole)), DeletedUser.class);
  }
  
  @Override
  public void updateUser(User user) {
    mongoTemplate.save(user);
  }
  
  @Override
  public User updateRoles(User user) {
    Update updateObj = new Update();
    Set<RoleType> userRoles = user.getRoles();
    updateObj.set("roles", userRoles);
    mongoTemplate.updateFirst(query(where("id").is(user.getId())), updateObj, User.class);
    return user;
  }
  
  @Override
  public User findByAccountId(String accountId) {
    return mongoTemplate.findOne(query(where("accountId").is(accountId)), User.class);
  }
  
  @Override
  public List<User> findByAccountId(List<String> accountIdList) {
    return mongoTemplate.find(query(where("accountId").in(accountIdList)), User.class);
  }
  
  /**
   * @see com.sp.web.repository.user.UserRepository#findUsers(java.lang.String, java.lang.String)
   */
  @Override
  public List<User> findUsers(String propertyName, String propertyValue) {
    return mongoTemplate.find(Query.query(Criteria.where(propertyName).is(propertyValue)),
        User.class);
  }
  
  /**
   *
   * 
   * @see com.sp.web.repository.user.UserRepository#findExternalUserByEmail(java.lang.String)
   */
  @Override
  public ExternalUser findExternalUserByEmail(String email) {
    return mongoTemplate
        .findOne(Query.query(Criteria.where("email").is(email)), ExternalUser.class);
  }
  
  /**
   * @see com.sp.web.repository.user.UserRepository#createExternalUser(com.sp.web.model.ExternalUser)
   */
  @Override
  public void createExternalUser(ExternalUser externalUser) {
    mongoTemplate.save(externalUser);
  }
  
  @Override
  public void removeAllDeletedUsers(String companyId) {
    mongoTemplate.remove(query(where("companyId").is(companyId)), DeletedUser.class);
  }
  
  @Override
  public List<FeedbackUser> findFeedbackUsers(String email, FeatureType featureType) {
    Query query2 = query(where("feedbackFor").is(email).andOperator(
        where("featureType").is(featureType)));
    return mongoTemplate.find(query2, FeedbackUser.class);
  }
  
  @Override
  public List<FeedbackUser> findFeedbackUsers(String email) {
    return mongoTemplate.find(query(where("feedbackFor").is(email)), FeedbackUser.class);
  }
  
  @Override
  public FeedbackUser findFeedbackUser(String feedbackUserId) {
    return mongoTemplate.findById(feedbackUserId, FeedbackUser.class);
  }
  
  @Override
  public FeedbackUser findFeedbackUser(String email, FeatureType featureType, String companyId) {
    return mongoTemplate.findOne(
        query(where(Constants.ENTITY_EMAIL).is(email).andOperator(
            where(Constants.ENTITY_FEATURE_TYPE).is(featureType),
            where(Constants.ENTITY_COMPANY_ID).is(companyId))), FeedbackUser.class);
  }
  
  @Override
  public List<FeedbackUserArchive> findFeedbackUsersArchive(String email) {
    return mongoTemplate.find(Query.query(Criteria.where("feedbackFor").is(email)),
        FeedbackUserArchive.class);
  }
  
  @Override
  public void updateGenericUser(Object user) {
    mongoTemplate.save(user);
  }
  
  /**
   * 
   * @see com.sp.web.repository.user.UserRepository#findUserById(java.lang.String)
   */
  @Override
  public User findUserById(String id) {
    return mongoTemplate.findById(id, User.class);
  }
  
  /**
   * @see com.sp.web.repository.user.UserRepository#findArchivedFeedbackUser(java.lang.String)
   */
  @Override
  public FeedbackUserArchive findArchivedFeedbackUser(String feedbackUserId) {
    return mongoTemplate.findById(feedbackUserId, FeedbackUserArchive.class);
  }
  
  @Override
  public List<User> findMemberByStatus(String companyId, UserStatus userStatus) {
    Assert.hasText(companyId, "Company Id is requrired !!!");
    Assert.notNull(userStatus, "User Status is requrired !!!");
    return mongoTemplate.find(
        query(where(Constants.ENTITY_COMPANY_ID).is(companyId).andOperator(
            where(Constants.ENTITY_USER_STATUS).is(userStatus))), User.class);
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public Set<String> getAllTags(String companyId) {
    HashSet<String> distinctTags = new HashSet<String>();
    
    // get distinct roles for candidates
    DBCollection collection = mongoTemplate.getCollection(mongoTemplate
        .getCollectionName(User.class));
    List<String> distinct = collection.distinct(Constants.ENTITY_TAGS,
        query(where(Constants.ENTITY_COMPANY_ID).is(companyId)).getQueryObject());
    distinctTags.addAll(distinct);
    
    return distinctTags;
  }
  
  @Override
  public List<User> findAllMembersByEmail(String email) {
    Assert.hasText(email, "Email is required.");
    return mongoTemplate.find(query(where(Constants.ENTITY_EMAIL).is(email)), User.class);
  }
  
  @Override
  public User findByToken(String tokenId) {
    Assert.hasText(tokenId, "Token id is required.");
    return mongoTemplate.findOne(query(where(Constants.ENTITY_PROFILE_TOKEN).is(tokenId)),
        User.class);
  }
  
  @Override
  public void updateUserCertificate(User user) {
    Assert.notNull(user, "User must not be null.");
    user.setCertificateNumber("" + getNextCertificateSequence());
    mongoTemplate.save(user);
  }
  
  private long getNextCertificateSequence() {
    String key = "orderNo";
    
    // get sequence id
    Query query = new Query(Criteria.where("_id").is(key));
    
    // increase sequence id by 1
    Update update = new Update();
    update.inc("certSeq", 1);
    
    // return new increased id
    FindAndModifyOptions options = new FindAndModifyOptions();
    options.returnNew(true);
    
    // this is the magic happened.
    SequenceId seqId = mongoTemplate.findAndModify(query, update, options, SequenceId.class);
    
    // if no id, throws SequenceException
    // optional, just a way to tell user when the sequence id is failed to generate.
    if (seqId == null) {
      throw new InvalidRequestException("Unable to get sequence id for key : " + key);
    }
    
    return seqId.getCertSeq();
  }
  
  @Override
  public User findByCertificate(String certificateNumber) {
    Assert.hasText(certificateNumber, "The certificate number required.");
    return mongoTemplate.findOne(
        query(where(Constants.ENTITY_CERTIFICATE_NUMBER).is(certificateNumber)), User.class);
  }
  
  @Override
  public List<User> findAllMembers(boolean withBlocked) {
    if (withBlocked) {
      return mongoTemplate.findAll(User.class);
    } else {
      return mongoTemplate.find(query(where("deactivated").is(false)), User.class);
    }
    
  }
  
  /**
   * @see com.sp.web.repository.user.UserRepository#updateAutoLearning(com.sp.web.model.User,
   *      java.lang.Boolean)
   */
  @Override
  public void updateAutoLearning(User user, Boolean updateAutoFlag) {
    UserProfileSettings profileSettings = user.getProfileSettings();
    profileSettings.setAutoUpdateLearning(updateAutoFlag);
    updateUser(user);
  }
  
  /**
   * @see com.sp.web.repository.user.UserRepository#findUserByGoalId(java.lang.String)
   */
  @Override
  public User findUserByGoalId(String id) {
    return mongoTemplate.findOne(Query.query(Criteria.where("userGoalId").is(id)), User.class);
  }
  
  /**
   * @see com.sp.web.repository.user.UserRepository#findValidUsersByTitle(java.lang.String,
   *      java.lang.String)
   */
  @Override
  public List<User> findValidUsersByTitle(String companyId, String title) {
    return mongoTemplate.find(
        Query.query(Criteria.where("companyId").is(companyId).and("title").is(title)
            .and("userStatus").is(UserStatus.VALID)), User.class);
  }
  
  @Override
  public List<FeedbackUser> findAllFeedbackUsersByFeedbackForAndFeature(String feedbackFor,
      FeatureType featureType) {
    return mongoTemplate.find(query(where(Constants.ENTITY_FEEDBACK_FOR).is(feedbackFor)
        .andOperator(where(Constants.ENTITY_FEATURE_TYPE).is(featureType))), FeedbackUser.class);
  }
  
  /**
   * @see com.sp.web.repository.user.UserRepository#findByCompanyAndRole(java.lang.String,
   *      com.sp.web.model.RoleType)
   */
  @Override
  public List<User> findByCompanyAndRole(String companyId, RoleType roleType) {
    return mongoTemplate.find(
        Query.query(Criteria.where("companyId").is(companyId).and(Constants.ENTITY_USER_ROLES)
            .in(roleType)), User.class);
  }
  
  @Override
  public void deleteFeedbackUser(FeedbackUser user) {
    mongoTemplate.remove(user);
  }
  
  /**
   * @see com.sp.web.repository.user.UserRepository#findGenericUserById(java.lang.Object)
   */
  @SuppressWarnings("unchecked")
  @Override
  public <T> T findGenericUserById(String id, T user) {
    return (T) mongoTemplate.findById(id, user.getClass());
  }
  
  @Override
  public List<User> findAllMembersByCompetencyProfileId(String competencyProfileId) {
    return mongoTemplate.find(
        query(where(Constants.ENTITY_COMPETENCY_PROFILE_ID).is(competencyProfileId)), User.class);
  }
  
  @Override
  public void removeAllFeedbackUsers(FeatureType featureType, String companyId) {
    mongoTemplate.remove(
        query(where(Constants.ENTITY_FEATURE_TYPE).is(featureType).andOperator(
            where(Constants.ENTITY_COMPANY_ID).is(companyId))), FeedbackUser.class);
  }
  
  @Override
  public List<FeedbackUser> findAllFeedbackUsersByCompany(String companyId) {
    return mongoTemplate.find(query(where(Constants.ENTITY_COMPANY_ID).is(companyId)),
        FeedbackUser.class);
  }
  
  /**
   * @see com.sp.web.repository.user.UserRepository#findSysPermissionUsers()
   */
  @Override
  public List<User> findSysPermissionUsers() {
    return mongoTemplate.find(
        Query.query(Criteria.where(Constants.ENTITY_USER_ROLES).in(RoleType.SysAdminMemberRole)),
        User.class);
  }
  
  /**
   * @see com.sp.web.repository.user.UserRepository#getMasterPassword()
   */
  @Override
  public MasterPassword getMasterPassword() {
    List<MasterPassword> allMaster = mongoTemplate.findAll(MasterPassword.class);
    if (CollectionUtils.isEmpty(allMaster)) {
      return null;
    } else {
      return allMaster.get(0);
    }
  }
  
  /**
   * (non-Javadoc)
   * 
   * @see com.sp.web.repository.user.UserRepository#updateMasterPassword(com.sp.web.model.MasterPassword)
   */
  @Override
  public void updateMasterPassword(MasterPassword masterPassword) {
    mongoTemplate.save(masterPassword);
  }
  
  @Override
  public List<FeedbackUser> findAllFeedbackUsers(FeatureType type, String companyId) {
    return mongoTemplate.find(
        query(where(Constants.ENTITY_FEATURE_TYPE).is(type).andOperator(
            where(Constants.ENTITY_COMPANY_ID).is(companyId))), FeedbackUser.class);
  }
  
  @Override
  public List<FeedbackUser> findAllFeedbackUsers(FeatureType type, String companyId, String memberId) {
    return mongoTemplate.find(
        query(where(Constants.ENTITY_FEATURE_TYPE).is(type).and(Constants.ENTITY_COMPANY_ID)
            .is(companyId).and(Constants.ENTITY_FEEDBACK_FOR).is(memberId)), FeedbackUser.class);
  }
  
  @Override
  public List<User> findAllUsers(List<String> userIds) {
    return mongoTemplate.find(query(where(Constants.ENTITY_ID).in(userIds)), User.class, "user");
  }
  
  @Override
  public List<User> findUserByName(String value, String companyId) {
    String[] split = value.split(" ");
    String firstName = org.springframework.util.StringUtils.trimAllWhitespace(split[0]);
    StringBuilder lastNameBuilder = new StringBuilder();
    int index = 0;
    for (String lastName : split) {
      if (index != 0) {
        lastNameBuilder.append(lastName);
      }
      index++;
    }
    List<User> users = mongoTemplate.find(
        Query.query(Criteria.where("firstName").is(firstName).and("companyId").is(companyId)),
        User.class);
    
    if (CollectionUtils.isEmpty(users)) {
      users = mongoTemplate.find(
          Query.query(Criteria.where("firstName").is(StringUtils.capitalize(firstName))
              .and("companyId").is(companyId)), User.class);
    }
    
    if (CollectionUtils.isEmpty(users)) {
      users = mongoTemplate.find(
          Query.query(Criteria.where("firstName").is(firstName).and("lastName")
              .is(lastNameBuilder.toString()).and("companyId").is(companyId)), User.class);
    }
    return users;
  }
  
  @Override
  public long findCompetencyUserCount(String companyId) {
    DBCollection collection = mongoTemplate.getCollection(mongoTemplate
        .getCollectionName(User.class));
    
    return collection
        .find(
            query(
                where("companyId").is(companyId).andOperator(
                    where("competencyProfileId").exists(true))).getQueryObject()).count();
  }
  
  @Override
  public List<User> findAllUsersWithCompetencyProfile(String companyId) {
    return mongoTemplate
        .find(
            query(where("companyId").is(companyId).andOperator(
                where("competencyProfileId").exists(true), where("deactivated").is(false))),
            User.class);
  }
  
  @Override
  public List<User> findValidUsers(String companyId) {
    return mongoTemplate.find(
        query(where(Constants.ENTITY_COMPANY_ID).is(companyId)
            .andOperator(where(Constants.ENTITY_USER_STATUS).is(UserStatus.VALID))
            .and("deactivated").is(false)), User.class);
  }
    
  @Override
  public List<User> getAllBasicUserInfo(String companyId) {
    final MongoConverter converter = mongoTemplate.getConverter();
    
    DBCollection collection = mongoTemplate.getCollection(mongoTemplate
        .getCollectionName(User.class));
    
    final Query query = query(where(Constants.ENTITY_COMPANY_ID).is(companyId)
        .and(Constants.ENTITY_USER_STATUS).is(UserStatus.VALID + ""));
    
    DBObject projections = new BasicDBObject("firstName", "1");
    projections.put("lastName", "1");
    projections.put("title", "1");
    projections.put("email", "1");
    projections.put("profileImage", "1");
    
    List<User> users = new ArrayList<User>();
    
    Iterator<DBObject> iterator = collection.find(query.getQueryObject(), projections).iterator();
    while (iterator.hasNext()) {
      DBObject next = iterator.next();
      final User user = converter.read(User.class, next);
      user.setUserStatus(UserStatus.VALID);
      users.add(user);
    }
    return users;
  }
}
