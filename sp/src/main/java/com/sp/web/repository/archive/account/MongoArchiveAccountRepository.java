package com.sp.web.repository.archive.account;

import com.sp.web.model.Company;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

/**
 * @author Dax Abraham
 *
 *         The mongo implementation of the archive repository.
 */
@Repository
public class MongoArchiveAccountRepository implements ArchiveAccountRepository {
  
  @Autowired
  @Qualifier("archiveTemplate")
  private MongoTemplate archiveTemplate;
  
  /**
   * @see com.sp.web.repository.archive.ArchiveAccountRepository#findArchiveCompanyById(java.lang.String)
   */
  @Override
  public Company findArchiveCompanyById(String companyId) {
    Assert.hasText(companyId, "Company Id is blank or null");
    return archiveTemplate.findById(companyId, Company.class);
  }

  /**
   * @see com.sp.web.repository.archive.account.ArchiveAccountRepository#getCompanyForAccount(java.lang.String)
   */
  @Override
  public Company getCompanyForAccount(String accountId) {
    Assert.hasText(accountId, "accountId Id is blank or null");
    return archiveTemplate.findOne(Query.query(Criteria.where("accountId").is(accountId)), Company.class);
  }
}