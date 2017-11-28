/**
 * 
 */
package com.sp.web.mvc.test.setup;

import static org.junit.Assert.assertTrue;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetupTest;
import com.sp.web.test.setup.DBSetup;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.UUID;

/**
 * @author daxabraham
 * 
 *         This is the base setup class that can be extended by all the other test classes
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextHierarchy({ @ContextConfiguration(locations ="classpath:applicationContext.xml"),
    @ContextConfiguration(locations ="classpath:com/sp/web/config/security.xml"),
    @ContextConfiguration(locations ="classpath:com/sp/web/config/integration-email-test.xml"),
    @ContextConfiguration(locations ="classpath:com/sp/web/config/integration-publisher-test.xml"),
    @ContextConfiguration(locations ="classpath:com/sp/web/config/integration-subscriber-test.xml")
})
@ActiveProfiles(profiles = "Test")
public class SPTestBase {

  @Autowired
  protected WebApplicationContext wac;

  @Autowired
  private FilterChainProxy springSecurityFilterChain;

  @Autowired
  MongoTemplate mongoTemplate;

  protected Logger log;

  protected MockMvc mockMvc;
  protected MockHttpSession session;
  protected MockHttpSession session2;
  protected DBSetup dbSetup;

  protected GreenMail testSmtp;
  
  protected GreenMail testImap;
  
  static{
    System.setProperty("appPropsFile", "./properties/test/");
  }
  
  @Before
  public void setUpBefore() throws Exception {
    
    log = Logger.getLogger(this.getClass());
    this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac)
        .addFilter(springSecurityFilterChain, "/*").build();
    session = new MockHttpSession(wac.getServletContext(), UUID.randomUUID().toString());
    session2 = new MockHttpSession(wac.getServletContext(), UUID.randomUUID().toString());
    dbSetup = new DBSetup(mongoTemplate);
    testSmtp = new GreenMail(ServerSetupTest.SMTP);
    testImap = new GreenMail(ServerSetupTest.IMAP);
    // testSmtp.start();
  }

  @Test
  public void testDoNothing() {
    assertTrue(true);
  }
  
  @Bean(name = "jmsTemplatePub")
  @Profile("Test")
  public JmsTemplate publisher() {
    return Mockito.mock(JmsTemplate.class);
  }
  
  @Bean(name = "jmsTemplateSub")
  public JmsTemplate jmsTemplateSub() {
    return Mockito.mock(JmsTemplate.class);
  }
}
