package com.sp.web.repository.readability;

import com.sp.web.model.readability.ReadabilityScore;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;


/**
 * @author Prasanna Venkatesh
 * 
 *         
 */
@Repository
public class MongoReadabilityRepository implements ReadabilityRepository {
  
  @Autowired
  MongoTemplate mongoTemplate;

  
  public ReadabilityScore getScoreById(String id) {
    return mongoTemplate.findById(id, ReadabilityScore.class);
  }

  public void saveReadabilityScore(ReadabilityScore score) {
    mongoTemplate.save(score);
    
  }

 
}
