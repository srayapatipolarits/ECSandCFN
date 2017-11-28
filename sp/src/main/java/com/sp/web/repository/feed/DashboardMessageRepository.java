package com.sp.web.repository.feed;

import com.sp.web.model.feed.DashboardMessage;
import com.sp.web.repository.generic.GenericMongoRepository;

import java.util.List;

/**
 * @author Dax Abraham
 * 
 *         The repository interface for dashboard messages.
 */
public interface DashboardMessageRepository extends GenericMongoRepository<DashboardMessage> {

  /**
   * Get the dashboard message from the given source id.
   * 
   * @param srcId
   *          - source id
   * @param companyId 
   *          - company id
   * @return
   *    the dashboard message
   */
  DashboardMessage findBySrcId(String srcId, String companyId);

  /**
   * Get all dashboard message for the given ownerId.
   * 
   * @param ownerId
   *          - owner id
   * @return
   *    the list of dashboard messages by the given owner
   */
  List<DashboardMessage> findByOwnerId(String ownerId);
  
}
