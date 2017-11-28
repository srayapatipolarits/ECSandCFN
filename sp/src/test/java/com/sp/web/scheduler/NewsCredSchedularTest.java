package com.sp.web.scheduler;

import com.sp.web.mvc.test.setup.SPTestBase;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author pradeep
 *
 */
public class NewsCredSchedularTest extends SPTestBase {

  @Autowired
  private NewsCredSchedular newsCredSchedular;

  @Test
  public void processNewsCredArticles() {
    dbSetup.removeArticles();
    newsCredSchedular.processNewsCredArticles();
  }

}
