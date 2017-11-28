package com.sp.web.repository.resume;

import com.sp.web.model.resume.SPResume;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <code>MongoSPResumeRepository</code> class will date for the sp resume.
 * 
 * @author pradeepruhil
 *
 */
@Repository
public class MongoSPResumeRepository implements SPResumeRepository {
  
  @Autowired
  private MongoTemplate mongoTemplate;
  
  /**
   * @see com.sp.web.repository.resume.SPResumeRepository#getAllResume(java.lang.String)
   */
  @Override
  public List<SPResume> getAllResume(String userId) {
    return mongoTemplate
        .find(Query.query(Criteria.where("resumeForId").is(userId)), SPResume.class);
  }
  
  /**
   * @see com.sp.web.repository.resume.SPResumeRepository#createResume(com.sp.web.model.resume.SPResume)
   */
  @Override
  public void createResume(SPResume resume) {
    mongoTemplate.save(resume);
  }
  
  /**
   * @see com.sp.web.repository.resume.SPResumeRepository#getPdfDocument(java.lang.String)
   */
  @Override
  public SPResume getPdfDocument(String resumeId) {
    return mongoTemplate.findById(resumeId, SPResume.class);
  }
  
  /**
   * @see com.sp.web.repository.resume.SPResumeRepository#deleteResume(java.lang.String)
   */
  @Override
  public void deleteResume(String resumeId) {
    mongoTemplate.remove(Query.query(Criteria.where("_id").is(resumeId)), SPResume.class);
  }
  
}
