package com.sp.web.repository.generic;

import java.util.List;

/**
 * @author Dax Abraham
 * 
 *         This is the interface class for a generic mongo repository for general functions that we
 *         usually do.
 */
public interface GenericMongoRepository<T> {

  /**
   * Find all the instances.
   * 
   * @return
   *      all the instances
   */
  List<T> findAll();
  
  /**
   * Find by id method.
   * 
   * @param id
   *          - id
   * @return
   *    the instance found else null
   */
  T findById(String id);
  
  /**
   * Find the entity by for the given company id.
   * 
   * @param companyId
   *          - company id
   * @return
   *    the entity by the company id
   */
  T findByCompanyId(String companyId);
  
  /**
   * Find the entity by for the given user id.
   * 
   * @param userId
   *          - user id
   * @return
   *    the entity by the user id
   */
  T findByUser(String userId);
  
  /**
   * Find all the entities with the given company id.
   * 
   * @param companyId
   *            - company id
   * @return
   *    the list of entities for the given company id.
   */
  List<T> findAllByCompanyId(String companyId);
  
  /**
   * Save the given instance.
   * 
   * @param objToSave
   *        - instance to save
   */
  void save(T objToSave);
  
  /**
   * Instance to delete.
   * 
   * @param objToDelete
   *          - instance to delete
   */
  void delete(T objToDelete);
}
