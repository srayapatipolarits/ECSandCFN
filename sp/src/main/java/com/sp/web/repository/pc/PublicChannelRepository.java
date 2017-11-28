package com.sp.web.repository.pc;

import com.sp.web.model.pubchannel.PublicChannel;
import com.sp.web.repository.generic.GenericMongoRepository;

import java.util.List;

/**
 * <code>PubliChannelRepository</code> is the repositry class for public channels.
 * 
 * @author pradeepruhil
 *
 */
public interface PublicChannelRepository extends GenericMongoRepository<PublicChannel> {
  
  /**
   * findByPcRefId method will retreive the public channel on the basis of pcRefId and companyId.
   * 
   * @param pcfRefId
   *          is the pcfRefId for the public channel.
   * @param companyId
   *          is the companyId for the public channel.
   * @return the public channel.
   */
  PublicChannel findByPcRefId(String pcfRefId, String companyId);

  /**
   * Get all the public channels for the given public channel reference id.
   * 
   * @param pcRefId
   *          - public channel reference id
   * @return
   *    list of public channels
   */
  List<PublicChannel> findByPcRefId(String pcRefId);
  
  /**
   * findByParentRefId method will retreive the public channel on the basis of parent ref id.
   * 
   * @param parentRefId
   *          for the public channel.
   * @param companyId
   *          of the public channel.
   * @return the PubliChannel.
   */
  List<PublicChannel> findByParentRefId(String parentRefId, String companyId);

  /**
   * Get the public channels with the given parent reference id.
   * 
   * @param parentRefId
   *          - parent reference id
   * @return
   *    the list of public channels
   */
  List<PublicChannel> findByParentRefId(String parentRefId);

  /**
   * findAllByCompanyId method will retreive the public channel on the basis of companyId.
   * 
   * @param companyId
   *          of the public channel.
   * @return the PubliChannel.
   */
  List<PublicChannel> findAllByCompanyId(String companyId);

}
