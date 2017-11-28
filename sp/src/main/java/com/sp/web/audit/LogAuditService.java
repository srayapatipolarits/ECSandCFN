package com.sp.web.audit;

import com.sp.web.model.User;
import com.sp.web.model.audit.AuditLogBean;
import com.sp.web.model.audit.ServiceType;
import com.sp.web.model.usertracking.UserTrackingType;
import com.sp.web.repository.archive.ArchiveRepository;
import com.sp.web.respository.systemadmin.SystemAdminRepository;
import com.sp.web.service.message.MessageHandlerType;
import com.sp.web.service.message.SPMessageEnvelop;
import com.sp.web.service.message.SPMessageGateway;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Dax Abraham
 *
 *         The service class implementation to write to log file.
 */
@Component
public class LogAuditService implements AuditService {
  
  private static final Logger log = Logger.getLogger(LogAuditService.class);
  
  @Autowired
  private SystemAdminRepository systemAdminRepository;
  
  @Autowired
  private Environment enviornment;
  
  @Autowired
  private ArchiveRepository archiveRepository;
  
  
  @Autowired
  private SPMessageGateway messageGateway;
  
  @Override
  public void audit(User user, String message, ServiceType serviceType, boolean skip) {
    process(user, message, serviceType, skip);
  }

  @Async
  private void process(User user, String message, ServiceType serviceType, boolean skip) {
    log.info("Audit:" + user.getEmail() + " - " + message + ", Service Type " + serviceType + ", skip : "
        + skip);
    if (skip) {
      return;
    }
    AuditLogBean auditLogBean = new AuditLogBean();
    auditLogBean.setCreatedOn(LocalDateTime.now());
    auditLogBean.setServiceType(serviceType);
    auditLogBean.setLogMessage(message);
    auditLogBean.setEmail(user.getEmail());
    auditLogBean.setCompanyId(user.getCompanyId());
    systemAdminRepository.addAuditLogs(auditLogBean);
    
    SPMessageEnvelop messageEnvelop = new SPMessageEnvelop();
    messageEnvelop.setMessageHandler(MessageHandlerType.EngagementMatrix);
    messageEnvelop.addData("userId", user.getId());
    messageEnvelop.addData("activityType", UserTrackingType.Session);
    messageGateway.sendMessage(messageEnvelop);
  }
  
  /**
   * Cron job to auto archive the logs.
   */
  @Scheduled(cron = "${audit.archiveLogs.schedule}")
  public void archiveAuditLogs() {
    
    int daysForArchiveLogs = Integer.valueOf(enviornment
        .getProperty("audit.archiveLogs.days.frequency"));
    
    LocalDateTime localDate = LocalDateTime.now();
    LocalDateTime minusDays = localDate.minusDays(daysForArchiveLogs);
    
    List<AuditLogBean> findAuditLogs = systemAdminRepository.findAuditLogs();
    if (!CollectionUtils.isEmpty(findAuditLogs)) {
      List<AuditLogBean> auditLogsToBeArchived = findAuditLogs.stream()
          .filter(aul -> aul.getCreatedOn().isBefore(minusDays)).collect(Collectors.toList());
      if (!CollectionUtils.isEmpty(auditLogsToBeArchived)) {
        archiveRepository.archive(auditLogsToBeArchived);
        /* Remove the audit logs */
        systemAdminRepository.removeAuditLogs(auditLogsToBeArchived);
      }
    }
  }
}