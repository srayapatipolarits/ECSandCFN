/**
 * 
 */
package com.sp.web.config;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import com.sp.web.mvc.test.setup.SPTestBase;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author daxabraham
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class DataConfigTest extends SPTestBase {

  @Autowired
  private DataConfig dataConfig;

  @Autowired
  private MongoDbFactory mongoDbFactory;

  @Autowired
  private MongoTemplate mongoTemplate;

  private static final Logger LOG = Logger.getLogger(DataConfig.class);

  /**
   * Test method for {@link com.sp.web.config.DataConfig#mongoDbFactory()}.
   * 
   * @throws Exception
   */
  @Test
  public void testMongoDbFactory() {
    try {
      assertNotNull("Mongo Factory is null !!!", dataConfig.mongoDbDevFactory());
      assertNotNull("Mongo Template :" + mongoDbFactory);
      assertThat("The mogo db name !!!", "sp", is(mongoDbFactory.getDb().getName()));
    } catch (Exception e) {
      LOG.fatal("Could not get mongo db factory !!!", e);
      fail(e.getMessage());
    }
  }

  /**
   * Test method for {@link com.sp.web.config.DataConfig#mongoTemplate()}.
   */
  @Test
  public void testMongoTemplate() {
    try {
      assertNotNull("Mongo Template :" + dataConfig.mongoDevTemplate());
      assertNotNull("Mongo Template :" + mongoTemplate);
    } catch (Exception e) {
      LOG.fatal("Could not get mongo db template !!!", e);
      fail(e.getMessage());
    }
  }

}
