package com.sp.web.repository.archive;

import com.sp.web.exception.SPException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Dax Abraham
 *
 *         The mongo implementation of the archive repository.
 */
@Repository
public class MongoArchiveRepository implements ArchiveRepository {
  
  private static final Logger log = Logger.getLogger(MongoArchiveRepository.class);
  
  @Autowired
  @Qualifier("archiveTemplate")
  private MongoTemplate archiveTemplate;
  
  @Override
  public void archive(Object objectToArchive) {
    try {
      if (objectToArchive != null) {
        try {
          if (objectToArchive.getClass().isAssignableFrom(ArrayList.class)) {
            insertAll((List<Object>) objectToArchive);
          } else {
            archiveTemplate.save(objectToArchive);
          }
          log.info("Object archived");
          
        } catch (Exception e) {
          log.fatal("Could not archive object :" + objectToArchive, e);
        }
      }
      ;
    } catch (Exception e) {
      log.debug("Could not archive :" + objectToArchive);
      throw new SPException(e);
    }
  }
  
  private void insertAll(List<Object> object) {
    archiveTemplate.insertAll(object);
  }
  
  /**
   * @see com.sp.web.repository.archive.ArchiveRepository#getAllArchived(java.lang.String,
   *      java.lang.Class)
   */
  @Override
  public <T> List<T> getAllArchived(String collectionName, Class<T> collectionClass) {
    return archiveTemplate.findAll(collectionClass, collectionName);
  }
}