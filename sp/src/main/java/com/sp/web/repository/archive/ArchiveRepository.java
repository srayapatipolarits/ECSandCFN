package com.sp.web.repository.archive;

import java.util.List;

/**
 * @author Dax Abraham
 *
 *         The repository interface for archiving data.
 */
public interface ArchiveRepository {
  
  /**
   * Archive the given object.
   * 
   * @param objectToArchive
   *          - object to archive
   */
  void archive(Object objectToArchive);
  
  /**
   * Returns all the rows in the collections from the archive repository.
   * 
   * @param <T>
   *          type paramtere for the class.
   * @param collectionName
   *          name of the collection.
   * @param collectionClass
   *          class of the collections
   * @return the archived objects.
   */
  <T> List<T> getAllArchived(String collectionName, Class<T> collectionClass);
}
