package com.sp.web.service.translation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Translable annotation on a field will be used as a tralable value in a class.
 * 
 * @author pradeepruhil
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.FIELD, ElementType.LOCAL_VARIABLE })
public @interface Translable {
  /**
   * Annotation to skip for auditing.
   */
  boolean skipTranslate() default false;
}
