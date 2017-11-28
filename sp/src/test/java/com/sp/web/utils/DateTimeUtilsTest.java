/**
 * 
 */
package com.sp.web.utils;

import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;

/**
 * @author pradeepruhil
 *
 */
public class DateTimeUtilsTest {
  
  /** 
   * Test method for {@link com.sp.web.utils.DateTimeUtil#getLocalDateFromMongoId(java.lang.String)}.
   */
  @Test
  public void testGetLocalDateFromMongoId() {
    Assert.assertEquals(LocalDate.of(2015, 2, 24), DateTimeUtil.getLocalDateFromMongoId("54ebb51ad4c64ea866292d93"));
  }
  
}
