package com.sp.web.validation;

import com.sp.web.exception.SPException;
import com.sp.web.mvc.SPResponse;
import com.sp.web.utils.LocaleHelper;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

/**
 * RestErrorHandler advice is triggerd whenever there is invalid parameter request is sent from the
 * client. It captures the error and send the error response to the user.
 * 
 * @author pradeepruhil
 *
 */
@ControllerAdvice
public class RestErrorHandler {
  
  /** Message source to fetch the message. */
  private MessageSource messageSource;
  
  /**
   * Initialize the error handler.
   * 
   * @param messageSource
   *          is the mssage source.
   */
  @Autowired
  public RestErrorHandler(MessageSource messageSource) {
    this.messageSource = messageSource;
  }
  
  /**
   * Advice to be called when the {@link MethodArgumentNotValidException} is thrown.
   * 
   * @param ex
   *          is the exception.
   * @return the SPResponse.
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public SPResponse processValidationError(MethodArgumentNotValidException ex) {
    BindingResult result = ex.getBindingResult();
    List<FieldError> fieldErrors = result.getFieldErrors();
    
    return processFieldErrors(fieldErrors);
  }
  
  /**
   * Advice to be called when the {@link BindException} is called due to validation fails for the
   * parameter.
   * 
   * @param ex
   *          BindException.
   * @return the SPResponse.
   */
  @ExceptionHandler(BindException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public SPResponse processValidationBindError(BindException ex) {
    BindingResult result = ex.getBindingResult();
    List<FieldError> fieldErrors = result.getFieldErrors();
    return processFieldErrors(fieldErrors);
  }
  
  /**
   * processFieldErrors methdo will process the field errors and add the error in the response.
   * 
   * @param fieldErrors
   *          is the fields which have error.
   * @return the response.
   */
  private SPResponse processFieldErrors(List<FieldError> fieldErrors) {
    SPResponse response = new SPResponse();
    if (CollectionUtils.isNotEmpty(fieldErrors)) {
      fieldErrors.stream().forEach(fe -> response.addError(fe));
    } else {
      response.addError(new SPException(messageSource.getMessage("pulse.error.message.generic",
          null, LocaleHelper.locale())));
    }
    return response;
  }
}
