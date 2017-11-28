package com.sp.web.audit;

import com.sp.web.model.User;
import com.sp.web.model.audit.ServiceType;

/**
 * @author Dax Abraham
 *
 *         This is the service class to log the auditing.
 */
public interface AuditService {

  void audit(User user, String message,ServiceType serviceType, boolean skip);
  
  void archiveAuditLogs();
}
