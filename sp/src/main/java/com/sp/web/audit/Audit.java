package com.sp.web.audit;

import com.sp.web.model.audit.ServiceType;
import com.sp.web.model.log.LogActionType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Dax Abraham
 * 
 *         The audit aspect interface.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
public @interface Audit {
  
  /**
   * Get the value of the aspect.
   */
  String value() default "";

  /**
   * Get the service type.
   */
  ServiceType type() default ServiceType.All;
  
  /**
   * Default log action Type.
   */
  LogActionType actionType() default LogActionType.Default;
  
  /**
   * Annotation to skip for auditing.
   */
  boolean skip() default false;
}
