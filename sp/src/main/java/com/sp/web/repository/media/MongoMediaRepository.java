package com.sp.web.repository.media;

import com.sp.web.model.SPMedia;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Prasanna Venkatesh
 * 
 *         The mongo implementation of the Image repository.
 */
@Repository
public class MongoMediaRepository implements MediaRepository {
  
  @Autowired
  MongoTemplate mongoTemplate;
  
  @Override
  public SPMedia getMedia(String id) {
    return mongoTemplate.findById(id, SPMedia.class);
  }
  
  @Override
  public void saveMedia(SPMedia image) {
    mongoTemplate.save(image);
    
  }
  
  @Override
  public List<SPMedia> getAllMedia() {
    Query query = new Query();
    query.with(new Sort(Direction.DESC, "createdOn"));
    return mongoTemplate.find(query,SPMedia.class);
  }
  
  @Override
  public void deleteMedia(String id) {
    mongoTemplate.remove(Query.query(Criteria.where("_id").is(id)), SPMedia.class); 
  }
  
  @Override
  public void updateMedia(String url, String id) {
    SPMedia spImage = mongoTemplate.findById(id, SPMedia.class);
    spImage.setUrl(url);
    mongoTemplate.save(spImage);
  }
  
  @Override
  public void updateMedia(SPMedia image) {
    SPMedia spImage = mongoTemplate.findById(image.getId(), SPMedia.class);
    
    spImage.setName(image.getName());
    spImage.setAltText(image.getAltText());
    spImage.setTags(image.getTags());
    spImage.setStatus(image.getStatus());
    
    mongoTemplate.save(spImage);
  }
  
}
