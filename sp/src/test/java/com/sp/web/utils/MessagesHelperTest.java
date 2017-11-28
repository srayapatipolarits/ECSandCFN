package com.sp.web.utils;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import com.sp.web.model.Gender;
import com.sp.web.model.User;
import com.sp.web.mvc.test.setup.SPTestBase;

import org.junit.Test;
import org.springframework.context.NoSuchMessageException;

public class MessagesHelperTest extends SPTestBase {

  @Test
  public void test() {
    User user = new User();
    try {
      MessagesHelper.genderNormalizeFromKey("abcd", user);
      fail("Not supposed to work !!!");
    } catch (NoSuchMessageException e) {
      log.debug("Incorrect key", e);
    }
    
    String genderNormalizeFromKey = MessagesHelper.genderNormalizeFromKey(
        "compare.Promoter.Motivator.secondary.avoid", user);
    log.debug(genderNormalizeFromKey);

    user.setFirstName("Dax");
    user.setGender(Gender.M);

    assertThat(genderNormalizeFromKey, containsString("her"));

    genderNormalizeFromKey = MessagesHelper.genderNormalizeFromKey(
        "compare.Promoter.Motivator.secondary.avoid", user);
    log.debug(genderNormalizeFromKey);

    assertThat(genderNormalizeFromKey, containsString("him"));
    assertThat(genderNormalizeFromKey, containsString("Dax"));
      
  }

}
