package com.sp.web.service.fallback;

import com.sp.web.exception.FallbackFailException;
import com.sp.web.model.fallback.FallbackBean;
import com.sp.web.repository.archive.ArchiveRepository;
import com.sp.web.repository.fallback.FallbackRepository;
import com.sp.web.utils.ApplicationContextUtils;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * FallbackFactory is the factory which process the fallback which are present in the system.
 * 
 * @author pradeepruhil
 *
 */
@Service
public class FallbackFactory {
  
  /** intiailizing the logger. */
  private static final Logger log = Logger.getLogger(FallbackFactory.class);
  @Autowired
  private FallbackRepository fallbackRepository;
  
  @Autowired
  private ArchiveRepository archiveRepository;
  
  /**
   * createFallback method wil create the fallback to be processed.
   * 
   * @param fallback
   *          is the fallback bean.
   */
  public void createFallback(FallbackBean fallback) {
    fallbackRepository.save(fallback);
  }
  
  /**
   * createFallback method wil create the fallback to be processed.
   * 
   * @param fallbackData
   *          is the fallback data
   * @param fallbackProcessor
   *          fallback processor.
   * @param companyId
   *          is the company id.
   */
  public void createFallback(Object fallbackData, String fallbackProcessor, String companyId) {
    FallbackBean fallbackBean = new FallbackBean();
    fallbackBean.setCompanyId(companyId);
    fallbackBean.setFallbackData(fallbackData);
    fallbackBean.setRetryCount(3);
    fallbackBean.setFallbackProcessor(fallbackProcessor);
    fallbackRepository.save(fallbackBean);
  }
  
  /**
   * processFallbackByCompany will process the fallback for the passed company.
   * 
   * @param companyId
   *          is the company id passed.
   */
  public void processFallbacksByCompany(String companyId) {
    if (StringUtils.isNotBlank(companyId)) {
      List<FallbackBean> fallbacks = fallbackRepository.findAllByCompanyId(companyId);
      processFallbacks(fallbacks);
    } else {
      processFallbacks();
    }
    
  }
  
  /**
   * processFallback will process all the fallbacks.
   */
  public void processFallbacks() {
    
    List<FallbackBean> fallbacks = fallbackRepository.findAll();
    processFallbacks(fallbacks);
  }
  
  /**
   * processFallback method will process the fallbacks.
   * 
   * @param fallbacks
   *          contains the lsit of fallbacks.
   */
  private void processFallbacks(List<FallbackBean> fallbacks) {
    
    fallbacks.stream().forEach(fb -> {
      processFallback(fb);
    });
  }
  
  /**
   * Process the fallback will process the fallback.
   * 
   * @param fallbackBean
   *          is the fallback method.
   */
  private void processFallback(FallbackBean fallbackBean) {
    
    if (fallbackBean != null) {
      
      String fallbackProcessor = fallbackBean.getFallbackProcessor();
      if (StringUtils.isNotBlank(fallbackProcessor)) {
        
        FallbackProcessor processor = (FallbackProcessor) ApplicationContextUtils
            .getBean(fallbackProcessor);
        
        if (processor != null) {
          boolean fallbackProcessed = false;
          for (int i = 0; i < fallbackBean.getRetryCount(); i++) {
            try {
              if (!fallbackProcessed) {
                processor.processFallback(fallbackBean);
                fallbackRepository.delete(fallbackBean);
                fallbackProcessed = true;
                break;
              }
              
            } catch (FallbackFailException ex) {
              log.error("Error occurred while processing the fallback, retry :" + i, ex);
            }
          }
          
          if (!fallbackProcessed) {
            if (log.isDebugEnabled()) {
              log.debug("Fall back is not processed, going to archive");
              archiveRepository.archive(fallbackBean);
              fallbackRepository.delete(fallbackBean);
            }
          }
        }
      }
      
    }
  }
}
