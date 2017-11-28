package com.sp.web.validation;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * ValidPassword annotation validate the password string entered by the user.
 * 
 * @author pradeepruhil
 *
 */
@Documented
@Constraint(validatedBy = PasswordConstraintValidator.class)
@Target({ TYPE, FIELD, ANNOTATION_TYPE, ElementType.PARAMETER })
@Retention(RUNTIME)
public @interface ValidPassword {
  
  /**
   * the default message to be be used in case the password is invalid.
   * 
   * @return
   */
  String message() default "Invalid Password";
  
  /**
   * groups is the group of classes annotation will be applied to.
   * 
   * @return
   */
  Class<?>[] groups() default {};
  
  /**
   * payload contains the initial data.
   * 
   * @return
   */
  Class<? extends Payload>[] payload() default {};
  
}