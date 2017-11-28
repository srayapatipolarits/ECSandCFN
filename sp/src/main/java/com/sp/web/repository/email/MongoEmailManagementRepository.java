package com.sp.web.repository.email;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.sp.web.Constants;
import com.sp.web.model.email.EmailManagement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Dax Abraham
 * 
 *         The mongo implementation of email management repository interface.
 */
@Repository
public class MongoEmailManagementRepository implements EmailManagementRepository {

  /* Mongo Template to perform operation on mongo. */
  @Autowired
  private MongoTemplate mongoTemplate;

  @Override
  public List<EmailManagement> getAll() {
    return mongoTemplate.findAll(EmailManagement.class);
  }

  @Override
  public EmailManagement getForCompanyId(String companyId) {
    return mongoTemplate.findOne(query(where(Constants.ENTITY_COMPANY_ID).is(companyId)),
        EmailManagement.class);
  }

  @Override
  public void save(EmailManagement emailManagement) {
    mongoTemplate.save(emailManagement);
  }

  @Override
  public void delete(EmailManagement emailManagement) {
    mongoTemplate.remove(emailManagement);
  }
  
}
