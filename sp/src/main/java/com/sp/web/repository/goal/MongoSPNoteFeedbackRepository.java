package com.sp.web.repository.goal;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.sp.web.Constants;
import com.sp.web.exception.DashboardRedirectException;
import com.sp.web.exception.InvalidRequestException;
import com.sp.web.model.FeedbackUser;
import com.sp.web.model.RequestStatus;
import com.sp.web.model.goal.PracticeFeedback;
import com.sp.web.model.goal.SPNote;
import com.sp.web.model.goal.SPNoteFeedbackType;
import com.sp.web.utils.MessagesHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * <code>MongoSPNoteFeedbackRepository</code> class will load the note feedback from mongo.
 * 
 * @author vikram
 *
 */
@Repository
public class MongoSPNoteFeedbackRepository implements SPNoteFeedbackRepository {
  
  /** Mongo Template to perform operation on mongo. */
  @Autowired
  @Qualifier("mongoTemplate")
  private MongoTemplate mongoTemplate;
  
  @Autowired
  @Qualifier("deletedTemplate")
  private MongoTemplate deletedTemplate;
  
  /**
   * <code>addNote</code> method will add the note in the repository.
   * 
   * @param goal
   *          to be updated/created
   * @return the goal
   */
  @Override
  public SPNote addNote(SPNote spnote) {
    mongoTemplate.insert(spnote);
    return spnote;
  }
  
  @Override
  public SPNote updateNote(String noteId, String desc) {
    
    SPNote spnote = mongoTemplate.findOne(Query.query(Criteria.where("_id").is(noteId)),
        SPNote.class);
    spnote.setContent(desc);
    mongoTemplate.save(spnote);
    
    return spnote;
  }
  
  @Override
  public void deleteNote(String noteId) {
    SPNote spNote = findNoteById(noteId);
    deletedTemplate.insert(spNote);     
    mongoTemplate.remove(query(where(Constants.ENTITY_ID).is(noteId)), SPNote.class);
  }

  @Override
  public int deleteAllNote(String goalId) {
    return deleteAll(goalId, SPNoteFeedbackType.NOTE);
  }

  /**
   * Delete all the data for the type and the given goal id.
   * 
   * @param goalId
   *          - practice area id
   * @param type 
   *          - type 
   * @return 
   *    the number of records affected
   */
  private int deleteAll(String goalId, SPNoteFeedbackType type) {
    List<SPNote> allNotes = mongoTemplate.find(
        query(where(Constants.ENTITY_GOAL_ID).is(goalId).andOperator(
            where(Constants.ENTITY_TYPE).is(type))), SPNote.class);
    deletedTemplate.insertAll(allNotes);
    return mongoTemplate.remove(
        query(where(Constants.ENTITY_GOAL_ID).is(goalId).andOperator(
            where(Constants.ENTITY_TYPE).is(type))), SPNote.class).getN();
  }

  @Override
  public PracticeFeedback createRequestFeedback(PracticeFeedback feedback) {
    
    mongoTemplate.insert(feedback);
    return feedback;
    
  }
  
  @Override
  public PracticeFeedback updateRequestFeedback(PracticeFeedback feedback) {
    
    mongoTemplate.save(feedback);
    return feedback;
  }
  
  @Override
  public void deleteRequestFeedback(String feedbackId) {
    PracticeFeedback findById = mongoTemplate.findById(feedbackId, PracticeFeedback.class);
    removeFeedback(findById);
  }

  /**
   * Remove the given practice feedback.
   * 
   * @param practiceFeedback
   *          - feedback to remove
   */
  private void removeFeedback(PracticeFeedback practiceFeedback) {
    if (practiceFeedback != null) {
      deletedTemplate.save(practiceFeedback);
      mongoTemplate.remove(practiceFeedback);
      
      List<FeedbackUser> feedbackUser = mongoTemplate.find(query(where(Constants.ENTITY_ID)
          .is(practiceFeedback.getFeedbackUserId())), FeedbackUser.class);
      deletedTemplate.insertAll(feedbackUser);
      
      mongoTemplate.remove(query(where(Constants.ENTITY_ID)
          .is(practiceFeedback.getFeedbackUserId())), FeedbackUser.class);
      
    }
  }

  @Override
  public int deleteAllFeedback(String goalId) {
    List<PracticeFeedback> find = mongoTemplate.find(query(where(Constants.ENTITY_GOAL_ID).is(goalId).andOperator(
            where(Constants.ENTITY_TYPE).is(SPNoteFeedbackType.FEEDBACK))), PracticeFeedback.class);
    
    find.forEach(this::removeFeedback);
    return find.size();
  }
  
  @Override
  public PracticeFeedback giveRequestFeedback(String feedbackId, String feedbackResponse) {
    
    PracticeFeedback feedback = mongoTemplate.findOne(
        Query.query(Criteria.where("_id").is(feedbackId)), PracticeFeedback.class);
    /* check if feedback is given */
    if (feedback == null) {
      throw new DashboardRedirectException(MessagesHelper.getMessage("service.growl.message5"));
    }
    if (feedback.getFeedbackStatus() != RequestStatus.NOT_INITIATED) {
      throw new InvalidRequestException("Feedback is already provided ");
    }
    feedback.setFeedbackResponse(feedbackResponse);
    feedback.setFeedbackStatus(RequestStatus.COMPLETED);
    mongoTemplate.save(feedback);
    return feedback;
  }
  
  @Override
  public List<PracticeFeedback> findRequestFeedbackReceived(String email) {
    final Query query = query(where("feedbackUserEmail").is(email));
    query.addCriteria(where("type").is(SPNoteFeedbackType.FEEDBACK));
    query.addCriteria(where("feedbackStatus").is(RequestStatus.NOT_INITIATED));
    query.with(new Sort(Direction.DESC, "createdOn"));
    return mongoTemplate.find(query, PracticeFeedback.class);
  }
  
  @Override
  public List<SPNote> findAllNotes(String userId) {
    
    final Query query = query(where("userId").is(userId));
    query.with(new Sort(Direction.DESC, "createdOn"));
    return mongoTemplate.find(query, SPNote.class);
    
  }
  
  @Override
  public List<SPNote> findNotesForGoal(String userId, String goalId) {
    
    final Query query = query(where("userId").is(userId));
    query.addCriteria(where("goalId").is(goalId));
    query.with(new Sort(Direction.DESC, "createdOn"));
    return mongoTemplate.find(query, SPNote.class);
    
  }
  
  @Override
  public List<PracticeFeedback> findAllFeedback(String userId) {
    
    final Query query = query(where("userId").is(userId));
    query.with(new Sort(Direction.DESC, "createdOn"));
    return mongoTemplate.find(query, PracticeFeedback.class);
    
  }
  
  @Override
  public List<PracticeFeedback> findFeedbackForGoal(String userId, String goalId) {
    
    final Query query = query(where("userId").is(userId));
    query.addCriteria(where("goalId").is(goalId));
    query.with(new Sort(Direction.DESC, "createdOn"));
    return mongoTemplate.find(query, PracticeFeedback.class);
    
  }
  
  @Override
  public List<SPNote> findAllNotesFeedback(String userId) {
    
    final Query query = query(where("userId").is(userId));
    List<SPNote> notesFeedbackList = getNotesFeedbackList(query);
    return notesFeedbackList;
  }
  
  @Override
  public List<SPNote> findAllNotesFeedbackForGoal(String userId, String goalId) {
    
    final Query query = query(where("userId").is(userId));
    query.addCriteria(where("goalId").is(goalId));
    
    return getNotesFeedbackList(query);
  }
  
  /**
   * Method to get the list of notes and feedback for the given query.
   * 
   * @param query
   *          - query
   * @return the list of notes and feedback objects
   */
  private List<SPNote> getNotesFeedbackList(final Query query) {
    DBCollection collection = mongoTemplate.getCollection(mongoTemplate
        .getCollectionName(SPNote.class));
    MongoConverter converter = mongoTemplate.getConverter();
    
    DBObject keys = new BasicDBObject();
    DBObject sortBy = new BasicDBObject("createdOn",-1);
    
    
    int limit = query.getLimit();
    List<DBObject> dbObjectList = (limit == 0) ? collection.find(query.getQueryObject(), keys).sort(sortBy)
        .toArray() : collection.find(query.getQueryObject(), keys).sort(sortBy).limit(limit).toArray();
    List<SPNote> notesFeedbackList = new ArrayList<SPNote>();
    
    for (DBObject dbObject : dbObjectList) {
      
      String className = (String) dbObject.get("_class");
      switch (className) {
      case "com.sp.web.model.goal.PracticeFeedback":
        notesFeedbackList.add(converter.read(PracticeFeedback.class, dbObject));
        break;
      default:
        notesFeedbackList.add(converter.read(SPNote.class, dbObject));
      }
    }
    return notesFeedbackList;
  }
  
  @Override
  public SPNote findNoteById(String noteId) {
    return mongoTemplate.findOne(Query.query(Criteria.where("_id").is(noteId)), SPNote.class);
  }
  
  @Override
  public SPNote findNoteFeedbackById(String notefeedbackId) {
    
    DBCollection collection = mongoTemplate.getCollection(mongoTemplate
        .getCollectionName(SPNote.class));
    
    DBObject dbObject = collection.findOne(query(where("_id").is(notefeedbackId)).getQueryObject());
    MongoConverter converter = mongoTemplate.getConverter();
    if (dbObject != null) {
      String className = (String) dbObject.get("_class");
      switch (className) {
      case "com.sp.web.model.goal.SPNote":
        return converter.read(SPNote.class, dbObject);
      case "com.sp.web.model.goal.PracticeFeedback":
        return converter.read(PracticeFeedback.class, dbObject);
      default:
        return converter.read(SPNote.class, dbObject);
      }
    }
    throw new InvalidRequestException("Article :" + notefeedbackId + " not found.");
    
  }
  
  @Override
  public PracticeFeedback findFeedbackById(String feedbackId) {
    
    return mongoTemplate.findOne(Query.query(Criteria.where("_id").is(feedbackId)),
        PracticeFeedback.class);
    
    /*
     * DBCollection collection = mongoTemplate.getCollection(mongoTemplate
     * .getCollectionName(PracticeFeedback.class)); DBObject dbObject =
     * collection.findOne(query(where("_id").is(feedbackId)).getQueryObject()); MongoConverter
     * converter = mongoTemplate.getConverter(); if (dbObject != null) { String className = (String)
     * dbObject.get("_class"); switch (className) { case "com.sp.web.model.goal.SPNote": return
     * converter.read(SPNote.class, dbObject); case "com.sp.web.model.goal.PracticeFeedback": return
     * converter.read(PracticeFeedback.class, dbObject); default: return
     * converter.read(SPNote.class, dbObject); } } throw new InvalidRequestException("Article :" +
     * feedbackId + " not found.");
     */
  }
  
  public int getAllFeedbackCount(String userId) {
    return (int) mongoTemplate.count(Query.query(Criteria.where("_id").is(userId)),
        PracticeFeedback.class);
  }
  
  public int getAllNotesCount(String userId) {
    return (int) mongoTemplate.count(Query.query(Criteria.where("_id").is(userId)), SPNote.class);
  }
  
  @Override
  public List<SPNote> findTopNotesFeedback(String userId) {
    final Query query = query(where("userId").is(userId));
    query.limit(Constants.DASHBOARD_NOTES_FEEDBACK_LIMIT);
    return getNotesFeedbackList(query);
  }

  @Override
  public PracticeFeedback findFeedbackbyTokenId(String tokenId) {
    
    final Query query = query(where("tokenId").is(tokenId));
    return mongoTemplate.findOne(query, PracticeFeedback.class);
  }
  
  /**
   * @see com.sp.web.repository.goal.SPNoteFeedbackRepository#findAllPracticeFeedbacknNoteByCompany(java.util.List,
   *      java.lang.String)
   */
  @Override
  public List<SPNote> findAllPracticeFeedbackNoteByCompany(List<String> usersIds, String companyId) {
    final Query query = query(where("companyId").is(companyId).and("userId").in(usersIds));
    return getNotesFeedbackList(query);
  }

  @Override
  public void deleteRequest(SPNote spNoteFeedback) {
    mongoTemplate.remove(spNoteFeedback);
  }

  /**
   * @see com.sp.web.repository.goal.SPNoteFeedbackRepository#removeAllNotesAndFeeback(java.lang.String)
   */
  @Override
  public void removeAllNotesAndFeeback(String forUserId) {
    /**
     * Adding notes to deleted repository, as it might be asked when a demo account is moved to a
     * new account, user can ask for his previous notes to retreive.
     */
    List<SPNote> allNotes = mongoTemplate.find(Query.query(Criteria.where("userId").is(forUserId)), SPNote.class);
    deletedTemplate.insert(allNotes, SPNote.class);
    List<PracticeFeedback> feedback = mongoTemplate.find(
        Query.query(Criteria.where("userId").is(forUserId)), PracticeFeedback.class);
    deletedTemplate.insert(feedback, PracticeFeedback.class);
    mongoTemplate.remove(Query.query(Criteria.where("userId").is(forUserId)), SPNote.class);
    mongoTemplate.remove(Query.query(Criteria.where("userId").is(forUserId)), PracticeFeedback.class);
  }
}
