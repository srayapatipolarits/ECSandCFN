package com.sp.web.audit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sp.web.model.User;
import com.sp.web.model.audit.ServiceType;
import com.sp.web.model.log.LogActionType;
import com.sp.web.mvc.SPResponse;
import com.sp.web.service.tracking.ActivityTrackingHelper;
import com.sp.web.utils.ApplicationContextUtils;
import com.sp.web.utils.GenericUtils;

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Dax Abraham
 *
 *         This is the audit logging aspect class.
 */
@Aspect
@Component
@Lazy
public class AuditAdvice {
  
  private static final Logger log = Logger.getLogger(AuditAdvice.class);
  private static final User anonymousUser = new User("anoynomousUser@anonymous.com", null);
  private LogAuditService auditService;
  private ActivityTrackingHelper activityTrackingHelper;
  
  /**
   * Constructor.
   */
  public AuditAdvice() {
    try {
      auditService = ApplicationContextUtils.getBean(LogAuditService.class);
    } catch (Exception e) {
      log.warn("Unable to create audit advice.", e);
    }
    
    try {
      activityTrackingHelper = ApplicationContextUtils.getBean(ActivityTrackingHelper.class);
    } catch (Exception e) {
      log.warn("Unable to get tracking service.", e);
    }
    
  }
  
  /**
   * AuditControllers method will audit all the controller request having @REsponsebody annotationon
   * them.
   * 
   * @param point
   *          Point cut for the methods
   * @param responseBodyAnnoation
   *          Response annotation
   * @return the object.
   * @throws Throwable
   *           in case of exception thrown.
   */
  @Around("@annotation(responseBodyAnnoation) && annotatedClass(auditAnnotation) && notAnnotated()")
  public Object auditControllers(ProceedingJoinPoint point, ResponseBody responseBodyAnnoation,
      Audit auditAnnotation) throws Throwable {
    if (auditAnnotation.skip()) {
      return point.proceed();
    }
    return process(point, auditAnnotation);
  }
  
  /**
   * Process the audit log and activity tracking for all request.
   * 
   * @param point
   *          - adivce pjp
   * @param auditAnnotation
   *          - audit annotation
   * @return the response.
   * @throws Throwable
   *           in case any exception occurred.
   */
  public Object process(ProceedingJoinPoint point, Audit auditAnnotation) throws Throwable {
    User user = processAuditLog(point.getSignature(), point.getArgs(), auditAnnotation.type(),
        auditAnnotation.skip());
    return processActvityTracking(user, point, auditAnnotation, point.proceed());
  }
  
  /**
   * Audit Advice will audit all the activity shave @Audit and @ResponseBody annotation.
   * 
   * @param point
   *          pjp
   * @param responseBodyAnnoation
   *          response body annotation
   * @param auditAnnotation
   *          audit annotation
   * @return the object.
   * @throws Throwable
   *           exception.
   */
  @Around("@annotation(responseBodyAnnoation) && @annotation(auditAnnotation)")
  public Object auditControllersMethod(ProceedingJoinPoint point,
      ResponseBody responseBodyAnnoation, Audit auditAnnotation) throws Throwable {
    if (auditAnnotation.skip()) {
      return point.proceed();
    }
    return process(point, auditAnnotation);
  }
  
  /**
   * AuditControllers method will audit all the controller request having @REsponsebody annotationon
   * them.
   * 
   * @param point
   *          Point cut for the methods
   * @param responseBodyAnnoation
   *          Response annotation
   * @return the object.
   * @throws Throwable
   *           in case of exception thrown.
   */
  @Around("@annotation(responseBodyAnnoation) &&  notAnnotated() && notAnnotatedClass()")
  public Object auditControllersDefault(ProceedingJoinPoint point,
      ResponseBody responseBodyAnnoation) throws Throwable {
    
    processAuditLog(point.getSignature(), point.getArgs(), ServiceType.All, false);
    return point.proceed();
  }
  
  /**
   * Process audit log and tracking annotation.
   * 
   * @param point
   *          - point cut
   * @param auditAnnotation
   *          - audit annotation
   * @param response
   *          - response returned.
   * @return the resposne
   */
  private Object processActvityTracking(User user, ProceedingJoinPoint point,
      Audit auditAnnotation, Object response) {
    
    if (auditAnnotation.skip()) {
      return response;
    }
    if (response instanceof SPResponse) {
      SPResponse spResponse = (SPResponse) response;
      /* check if any error is present, if present return */
      if (spResponse.hasErrors()) {
        return response;
      }
    }
    
    final LogActionType actionType = auditAnnotation.actionType();
    if (actionType == LogActionType.Default) {
      return response;
    }
    
    if (activityTrackingHelper == null) {
      try {
        activityTrackingHelper = ApplicationContextUtils.getBean(ActivityTrackingHelper.class);
      } catch (Exception e) {
        log.warn("Unable to get tracking service.", e);
      }
    }
    
    if (activityTrackingHelper != null) {
      try {
        activityTrackingHelper.trackActivity(user, actionType, point.getArgs());
      } catch (Exception ex) {
        log.warn("Error occurred during tracking activity for user " + user + " for response "
            + response, ex);
      }
    }
    return response;
  }
  
  /**
   * Process audit log.
   * 
   * @param point
   *          - point cut
   * @param type
   *          - service type
   * @param skip
   *          - flag for skip
   * @return the user for the request
   */
  private User processAuditLog(Signature signature, Object[] args, ServiceType type, boolean skip) {
    
    if (skip) {
      return null;
    }
    StringBuffer sb = new StringBuffer();
    User user = generateMessage(sb, signature, args);
    
    if (auditService == null) {
      try {
        auditService = ApplicationContextUtils.getBean(LogAuditService.class);
      } catch (Exception e) {
        log.warn("Unable to get audit service.", e);
      }
    }
    if (auditService != null) {
      auditService.audit(user, sb.toString(), type, skip);
    }
    return user;
  }
  
  /**
   * Generate the string message to log.
   * 
   * @param sb
   *          - string buffer
   * @param signature
   *          - method signature
   * @param args
   *          - arguments
   * @return the user for the request
   */
  private User generateMessage(StringBuffer sb, Signature signature, Object[] args) {
    sb.append(signature.getName());
    User user = anonymousUser;
    if (args != null) {
      sb.append(": with args : ");
      for (Object arg : args) {
        if (arg instanceof Authentication) {
          user = GenericUtils.getUserFromAuthentication((Authentication) arg);
        } else {
          String message = null;
          try {
            ObjectMapper mapper = new ObjectMapper();
            message = mapper.writeValueAsString(arg);
          } catch (Exception ex) {
            message = arg.toString();
          }
          
          sb.append("[").append((arg == null) ? arg : message).append("] ");
        }
      }
    }
    return user;
  }
  
  /**
   * @return - get the user.
   */
  // private User getUser() {
  // User user = null;
  // Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
  // if (authentication.getPrincipal() instanceof User
  // || authentication.getPrincipal() instanceof SPUserDetail) {
  // user = GenericUtils.getUserFromAuthentication(authentication);
  // }
  // return user;
  // }
  
  @Pointcut("@within(classAnnotation)")
  protected void annotatedClass(Audit classAnnotation) {
  }
  
  @Pointcut("!@within(com.sp.web.audit.Audit)")
  protected void notAnnotatedClass() {
  }
  
  @Pointcut("!@annotation(com.sp.web.audit.Audit)")
  protected void notAnnotated() {
  }
}