package com.sp.web.validation;

import com.sp.web.utils.MessagesHelper;

import org.passay.DigitCharacterRule;
import org.passay.LengthRule;
import org.passay.PasswordData;
import org.passay.PasswordValidator;
import org.passay.RuleResult;
import org.passay.RuleResultDetail;
import org.passay.SpecialCharacterRule;
import org.passay.UppercaseCharacterRule;
import org.passay.WhitespaceRule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * PasswordConstraintValidator will validate the password using the java 303 validation. It will be
 * called whenever we have {@link ValidPassword} annotation applied to a class variable.
 * 
 * @author pradeepruhil
 *
 */
public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword, String> {
  
  /**
   * Method to initialize from ConstraintValidator.
   */
  @Override
  public void initialize(final ValidPassword validPassword) {
    
  }
  
  /**
   * @see ConstraintValidator#isValid(Object, ConstraintValidatorContext).
   */
  @Override
  public boolean isValid(final String password, final ConstraintValidatorContext context) {
    final PasswordValidator validator = new PasswordValidator(Arrays.asList(new LengthRule(8, 14),
        new UppercaseCharacterRule(1), new DigitCharacterRule(1), new SpecialCharacterRule(1),
        new WhitespaceRule()));
    final RuleResult result = validator.validate(new PasswordData(password));
    if (result.isValid()) {
      return true;
    }
    context.disableDefaultConstraintViolation();
    context.buildConstraintViolationWithTemplate(
        new StringBuffer("\n").append(getMessage(result)).toString()).addConstraintViolation();
    return false;
  }
  
  /**
   * getMessage method will return the messages to the front end to be shown to the user.
   * 
   * @param result
   *          contains the validation result.
   * @return the error message in case of validation fails.
   */
  public List<String> getMessage(RuleResult result) {
    final List<String> messages = new ArrayList<>();
    for (RuleResultDetail detail : result.getDetails()) {
      messages.add(resolve(detail));
    }
    return messages;
  }
  
  /**
   * <code>resolve</code> will fetch the message for the error key and add the dynamic values to the
   * error message.
   * 
   * @param detail
   *          is the rule result details.
   * @return the message.
   */
  public String resolve(final RuleResultDetail detail) {
    final String key = detail.getErrorCode();
    final String message = MessagesHelper.getMessage(key);
    String format;
    if (message != null) {
      format = String.format(message, detail.getValues());
    } else {
      if (!detail.getParameters().isEmpty()) {
        format = String.format("%s:%s", key, detail.getParameters());
      } else {
        format = String.format("%s", key);
      }
    }
    return format;
  }
}