package com.sp.web.service.pc;

import com.sp.web.model.pubchannel.PublicChannel;
import com.sp.web.repository.pc.PublicChannelRepository;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * PublicChannleFactory is the factory clss for the public channel.
 * 
 * @author pradeepruhil
 *
 */
@Component
public class PublicChannelFactory {
  
  /** Initializing the logger. */
  private static final Logger LOG = Logger.getLogger(PublicChannelFactory.class);
  
  @Autowired
  private PublicChannelRepository publicChannelRepositoy;
  
  /**
   * getPublicChannel will return the public channle for the passed pcRefId and companyId.
   * 
   * @param pcRefId
   *          pcRefId for fetching the public channel.
   * @param companyId
   *          of the company.
   * @return the get public channel.
   */
  @Cacheable(value = "publicChannel", key = "#pcRefId + #companyId", unless = "#result == null")
  public PublicChannel getPublicChannel(String pcRefId, String companyId) {
    if (LOG.isDebugEnabled()) {
      LOG.debug("getPublicChannel, pcRefId " + pcRefId + ", companyId " + companyId);
    }
    return publicChannelRepositoy.findByPcRefId(pcRefId, companyId);
  }
  
  /**
   * updatePublicChannel will save the public channel in mongodb.
   * 
   * @param publicChannel
   *          to be updated.
   */
  @CacheEvict(value = "publicChannel", key = "#publicChannel.pcRefId + #publicChannel.companyId")
  public void updatePublicChannel(PublicChannel publicChannel) {
    publicChannelRepositoy.save(publicChannel);
  }
  
  /**
   * deletePublicChannel will delete the public channel for the user.
   * 
   * @param publicChannel
   *          to be deleted.
   */
  @CacheEvict(value = "publicChannel", key = "#publicChannel.pcRefId + #publicChannel.companyId")
  public void deletePublicChannel(PublicChannel publicChannel) {
    publicChannelRepositoy.delete(publicChannel);
  }
  
  /**
   * getPublicChannels will retrieve the public channel from database.
   * 
   * @param pcRefId
   *          pcRefId.
   * @param parentRefId
   *          parent RefId.
   * @param companyId
   *          of the publicChannels.
   * @return the list of public channels.
   */
  public PublicChannel getPubliChannelsNoCache(String pcRefId, String companyId) {
    return publicChannelRepositoy.findByPcRefId(pcRefId, companyId);
  }
  
  /**
   * Get all the public channels for the given pc reference id.
   * 
   * @param pcRefId
   *          - public channel reference id
   * @return the list of public channels
   */
  public List<PublicChannel> getPubliChannelsNoCache(String pcRefId) {
    return publicChannelRepositoy.findByPcRefId(pcRefId);
  }
  
  /**
   * getPublicChannels will retrieve the public channel from database.
   * 
   * @param parentRefId
   *          parent RefId.
   * @param companyId
   *          of the publicChannels.
   * @return the list of public channels.
   */
  public List<PublicChannel> getPubliChannelsByParentNoCache(String parentRefId, String companyId) {
    return publicChannelRepositoy.findByParentRefId(parentRefId, companyId);
  }
  
  /**
   * Get the public channels with the given parent reference id.
   * 
   * @param parentRefId
   *          - parent reference id
   * @return the list of public channels
   */
  public List<PublicChannel> getPubliChannelsByParentNoCache(String parentRefId) {
    return publicChannelRepositoy.findByParentRefId(parentRefId);
  }
  
  /**
   * findAllPubliChannel method will find all the public channel associated with the company.
   * 
   * @param companyId
   *          id of the company.
   */
  public List<PublicChannel> findAllPubliChannelByCompanyId(String companyId) {
    return publicChannelRepositoy.findAllByCompanyId(companyId);
  }
  
  /**
   * findAllPubliChannel method will find all the public channel associated with the company.
   * 
   */
  public List<PublicChannel> findAllPubliChannel() {
    return publicChannelRepositoy.findAll();
  }
}
