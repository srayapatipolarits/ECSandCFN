package com.sp.web.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * load integration jms xml
 * 
 * @author vikram
 */
@Configuration
@ImportResource(value = { "classpath:com/sp/web/config/integration-jms-test.xml",
    "classpath:com/sp/web/config/integration-publisher-test.xml",
    "classpath:com/sp/web/config/integration-subscriber-test.xml" })
@Component
@Profile("Test")
public class IntegrationJMSConfigTest {
}