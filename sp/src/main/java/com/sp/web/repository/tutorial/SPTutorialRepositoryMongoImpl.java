package com.sp.web.repository.tutorial;

import com.sp.web.exception.InvalidRequestException;
import com.sp.web.model.tutorial.SPTutorial;
import com.sp.web.repository.generic.GenericMongoRepositoryImpl;

import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 
 * @author Dax Abraham
 *
 *         The mongo implementation for the SP Tutorial repository interface.
 */
@Repository
public class SPTutorialRepositoryMongoImpl extends GenericMongoRepositoryImpl<SPTutorial> implements
    SPTutorialRepository {

  @Override
  public SPTutorial findByCompanyId(String companyId) {
    throw new InvalidRequestException("Not supported.");
  }

  @Override
  public List<SPTutorial> findAllByCompanyId(String companyId) {
    throw new InvalidRequestException("Not supported.");
  }
  
  
}
