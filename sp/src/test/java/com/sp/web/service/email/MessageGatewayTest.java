package com.sp.web.service.email;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import com.icegreen.greenmail.util.GreenMailUtil;
import com.sp.web.mvc.test.setup.SPTestBase;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.mail.Message;
import javax.mail.MessagingException;

public class MessageGatewayTest extends SPTestBase {

  /** Countdown latch. */
  private CountDownLatch lock = new CountDownLatch(1);

  private static final Logger LOG = Logger.getLogger(MessageGatewayTest.class);

  @Before
  public void setUp() throws Exception {
    testSmtp.start();
  }

  @After
  public void stop() {
    testSmtp.stop();
  }

  /**
   * Test method for
   * {@link com.sp.web.service.email.transform.EmailTransformer#transformMessage(com.sp.web.service.email.MessageParams)}
   * .
   */
  @Autowired
  private CommunicationGateway gateway;

  @Test
  public void testTransformMessage() throws MessagingException {

    EmailParams emailParams = new EmailParams();
    //emailParams.setFrom("pradeepruhil85@gmail.com");
    emailParams.setSubject("Welcome Test");
    emailParams.setTemplateName("welcomeEmail.stg");
    emailParams.getTos().add("dax@surepeople.com");
    emailParams.addParam("firstName", "Pradeep");
    gateway.sendMessage(emailParams);
    LOG.info("Message sennt");

    try {
      lock.await(5000, TimeUnit.MILLISECONDS);
    } catch (InterruptedException e) { // TODO Auto-generated catch block
      e.printStackTrace();
    }
    testMessageBody(1);

    /* Test template cache */
    gateway.sendMessage(emailParams);
    LOG.info("Message sennt");

    try {
      lock.await(5000, TimeUnit.MILLISECONDS);
    } catch (InterruptedException e) { // TODO Auto-generated catch block
      e.printStackTrace();
    }
    testMessageBody(2);

    /* Template not missing test */
    emailParams.setTemplateName("templateMissing.stg");
    gateway.sendMessage(emailParams);

  }

  private void testMessageBody(int message) throws MessagingException {
    Message[] messages = testSmtp.getReceivedMessages();
    assertEquals(message, messages.length);
    assertEquals("Welcome Test", messages[0].getSubject());
    String body = GreenMailUtil.getBody(messages[0]).replaceAll("\r\n\r\n", "");
    System.out.println(body);
    assertThat(body, containsString("Welcome to the SurePeople, Pradeep!View your member profile at:"));
  }

  @After
  public void cleanup() {
    testSmtp.stop();
  }
}
