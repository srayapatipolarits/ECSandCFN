package com.sp.web.repository.hiring;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.mongodb.DBCollection;
import com.sp.web.Constants;
import com.sp.web.exception.InvalidRequestException;
import com.sp.web.model.HiringUser;
import com.sp.web.model.UserStatus;
import com.sp.web.model.UserType;
import com.sp.web.model.hiring.user.HiringUserArchive;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * @author Dax Abraham
 *
 *         The mongo implementation for the hiring repository.
 */
@Repository
public class MongoHiringRepository implements HiringRepository {
  
  @Autowired
  MongoTemplate mongoTemplate;
  
  @Override
  public List<HiringUser> getAllUsers(String companyId) {
    Assert.hasText(companyId, "Company id required !!!");
    return mongoTemplate.find(query(where(Constants.ENTITY_COMPANY_ID).is(companyId)),
        HiringUser.class);
  }
  
  @Override
  public List<HiringUser> getAllUsers(String companyId, List<String> candidateEmails) {
    return mongoTemplate.find(
        query(where(Constants.ENTITY_COMPANY_ID).is(companyId).andOperator(
            where(Constants.ENTITY_EMAIL).in(candidateEmails))), HiringUser.class);
  }
  
  @Override
  public List<HiringUser> getAllUsers(String companyId, UserType type) {
    return mongoTemplate.find(
        query(where(Constants.ENTITY_COMPANY_ID).is(companyId).andOperator(
            where(Constants.ENTITY_TYPE).is(type))), HiringUser.class);
  }
  
  @Override
  public List<HiringUser> getAllValidUsers(String companyId) {
    Assert.hasText(companyId, "Company id required !!!");
    return mongoTemplate.find(
        query(where(Constants.ENTITY_COMPANY_ID).is(companyId).andOperator(
            where(Constants.ENTITY_USER_STATUS).is(UserStatus.VALID))), HiringUser.class);
  }
  
  @Override
  public Collection<? extends HiringUser> getAllArchivedUsers(String companyId) {
    Assert.hasText(companyId, "Company id required !!!");
    return mongoTemplate.find(query(where(Constants.ENTITY_COMPANY_ID).is(companyId)),
        HiringUserArchive.class);
  }
  
  @Override
  public HiringUser findByEmail(String companyId, String email) {
    Assert.hasLength(companyId, "Company id required !!!");
    Assert.hasText(email, "Email is required !!!");
    return mongoTemplate.findOne(query(where(Constants.ENTITY_COMPANY_ID).is(companyId)
        .andOperator(where(Constants.ENTITY_EMAIL).is(email))), HiringUser.class);
  }
  
  @Override
  public List<HiringUser> findByEmail(String email) {
    Assert.hasText(email, "Email is required !!!");
    return mongoTemplate.find(query(where(Constants.ENTITY_EMAIL).is(email)), HiringUser.class);
  }
  
  @Override
  public HiringUser findByEmailValidated(String companyId, String candidateEmail) {
    return Optional.ofNullable(findByEmail(companyId, candidateEmail)).orElseThrow(
        () -> new InvalidRequestException("Candidate has been archived or deleted."));
  }
  
  @Override
  public HiringUser addHiringUser(HiringUser hiringUser) {
    mongoTemplate.save(hiringUser);
    return hiringUser;
  }
  
  @Override
  public HiringUser updateHiringUser(HiringUser hiringUser) {
    mongoTemplate.save(hiringUser);
    return hiringUser;
  }
  
  @Override
  public HiringUser findById(String hiringUserId) {
    Assert.hasText(hiringUserId, "User id cannot be null or empty !!!");
    return mongoTemplate.findById(hiringUserId, HiringUser.class);
  }
  
  @Override
  public HiringUser findByIdValidated(String hiringUserId) {
    return Optional.ofNullable(findById(hiringUserId)).orElseThrow(
        () -> new InvalidRequestException("Hiring user not found for id:" + hiringUserId));
  }
  
  @Override
  public HiringUserArchive findArchivedUserById(String candidateId) {
    Assert.hasText(candidateId, "Candidate id cannot be null or empty !!!");
    return mongoTemplate.findById(candidateId, HiringUserArchive.class);
  }
  
  @Override
  public HiringUserArchive findArchivedUserByIdValidated(String candidateId) {
    return Optional.ofNullable(findArchivedUserById(candidateId)).orElseThrow(
        () -> new InvalidRequestException("Hiring user not found for id:" + candidateId));
  }
  
  @Override
  public HiringUserArchive findArchivedCandidateByEmail(String companyId, String email) {
    Assert.hasLength(companyId, "Company id required !!!");
    Assert.hasText(email, "Email is required !!!");
    return mongoTemplate.findOne(query(where(Constants.ENTITY_COMPANY_ID).is(companyId)
        .andOperator(where(Constants.ENTITY_EMAIL).is(email))), HiringUserArchive.class);
  }
  
  @Override
  public Collection<? extends HiringUserArchive> getAllArchiveUsers(String companyId,
      List<String> candidateEmails) {
    return mongoTemplate.find(
        query(where(Constants.ENTITY_COMPANY_ID).is(companyId).andOperator(
            where(Constants.ENTITY_EMAIL).in(candidateEmails))), HiringUserArchive.class);
  }
  
  @Override
  public List<HiringUserArchive> findByEmailHiringArchive(String email) {
    Assert.hasText(email, "Email is required !!!");
    return mongoTemplate.find(query(where(Constants.ENTITY_EMAIL).is(email)),
        HiringUserArchive.class);
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public Set<String> getAllHiringRolesArchived(String companyId) {
    HashSet<String> distinctRoles = new HashSet<String>();
    
    // get distinct roles for candidates
    DBCollection collection = mongoTemplate.getCollection(mongoTemplate
        .getCollectionName(HiringUserArchive.class));
    
    List<String> distinct = collection.distinct(Constants.ENTITY_HIRING_ROLES,
        query(where(Constants.ENTITY_COMPANY_ID).is(companyId)).getQueryObject());
    distinctRoles.addAll(distinct);
    
    return distinctRoles;
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public Set<String> getAllHiringRolesCandidates(String companyId) {
    HashSet<String> distinctRoles = new HashSet<String>();
    
    // get distinct roles for candidates
    DBCollection collection = mongoTemplate.getCollection(mongoTemplate
        .getCollectionName(HiringUser.class));
    
    List<String> distinct = collection.distinct(Constants.ENTITY_HIRING_ROLES,
        query(where(Constants.ENTITY_COMPANY_ID).is(companyId)).getQueryObject());
    distinctRoles.addAll(distinct);
    
    return distinctRoles;
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public Set<String> getAllTags(String companyId) {
    HashSet<String> distinctTags = new HashSet<String>();
    
    // get distinct roles for candidates
    DBCollection collection = mongoTemplate.getCollection(mongoTemplate
        .getCollectionName(HiringUser.class));
    List<String> distinct = collection.distinct(Constants.ENTITY_TAGS,
        query(where(Constants.ENTITY_COMPANY_ID).is(companyId)).getQueryObject());
    distinctTags.addAll(distinct);
    
    // get distinct roles for archive candidates
    collection = mongoTemplate.getCollection(mongoTemplate
        .getCollectionName(HiringUserArchive.class));
    distinct = collection.distinct(Constants.ENTITY_TAGS,
        query(where(Constants.ENTITY_COMPANY_ID).is(companyId)).getQueryObject());
    distinctTags.addAll(distinct);
    
    return distinctTags;
  }
  
  @Override
  public List<HiringUserArchive> findAllHiredCandidates(String companyId) {
    Assert.hasText(companyId, "Company id is requrired !!!");
    return mongoTemplate.find(
        query(where(Constants.ENTITY_COMPANY_ID).is(companyId).andOperator(
            where(Constants.ENTITY_USER_STATUS).is(UserStatus.HIRED))), HiringUserArchive.class);
  }
  
  @Override
  public Collection<? extends HiringUser> getAllArchivedUsersForRole(String companyId, String role) {
    return mongoTemplate.find(
        Query.query(Criteria.where(Constants.ENTITY_COMPANY_ID).is(companyId)
            .andOperator(Criteria.where(Constants.ENTITY_HIRING_ROLES).in(role))),
        HiringUserArchive.class);
  }
  
  @Override
  public List<HiringUser> findByRole(String companyId, String role) {
    return mongoTemplate
        .find(
            Query.query(Criteria.where(Constants.ENTITY_COMPANY_ID).is(companyId)
                .andOperator(Criteria.where(Constants.ENTITY_HIRING_ROLES).in(role))),
            HiringUser.class);
  }
  
  @Override
  public void delete(HiringUser hiringUser) {
    mongoTemplate.remove(hiringUser);
  }
  
  @Override
  public void delete(HiringUserArchive hiringUser) {
    mongoTemplate.remove(hiringUser);
  }
  
  @Override
  public void save(HiringUserArchive user) {
    mongoTemplate.save(user);
  }
  
  @Override
  public HiringUser findByToken(String tokenId) {
    Assert.hasText(tokenId, "Token id is required.");
    return mongoTemplate.findOne(query(where(Constants.ENTITY_PROFILE_TOKEN).is(tokenId)),
        HiringUser.class);
  }
  
  @Override
  public List<HiringUser> findAllUsers(List<String> userIds) {
    return mongoTemplate.find(query(where(Constants.ENTITY_ID).in(userIds)), HiringUser.class,
        "hiringUser");
  }
  
}
