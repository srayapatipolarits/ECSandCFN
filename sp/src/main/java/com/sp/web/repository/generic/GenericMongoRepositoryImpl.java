package com.sp.web.repository.generic;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.sp.web.Constants;
import com.sp.web.repository.archive.ArchiveRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.GenericTypeResolver;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Dax Abraham
 * 
 *         The implementation class for the generic Mongo interface.
 */
@Repository
public class GenericMongoRepositoryImpl<T> implements GenericMongoRepository<T> {
  
  @Autowired
  protected MongoTemplate mongoTemplate;
  
  @Autowired
  protected ArchiveRepository archiveRepository;
  
  private Class<T> type;
  
  @SuppressWarnings("unchecked")
  public GenericMongoRepositoryImpl() {
    this.type = (Class<T>) GenericTypeResolver.resolveTypeArgument(getClass(),
        GenericMongoRepositoryImpl.class);
  }
  
  @Override
  public T findById(String id) {
    return mongoTemplate.findById(id, type);
  }
  
  @Override
  public void save(T objToSave) {
    mongoTemplate.save(objToSave);
  }
  
  @Override
  public void delete(T objToDelete) {
    mongoTemplate.remove(objToDelete);
  }
  
  @Override
  public List<T> findAll() {
    return mongoTemplate.findAll(type);
  }
  
  @Override
  public T findByCompanyId(String companyId) {
    return mongoTemplate.findOne(query(where(Constants.ENTITY_COMPANY_ID).is(companyId)), type);
  }
  
  @Override
  public T findByUser(String userId) {
    return mongoTemplate.findOne(query(where(Constants.ENTITY_USER_ID).is(userId)), type);
  }
  
  @Override
  public List<T> findAllByCompanyId(String companyId) {
    return mongoTemplate.find(query(where(Constants.ENTITY_COMPANY_ID).is(companyId)), type);
  }
}
