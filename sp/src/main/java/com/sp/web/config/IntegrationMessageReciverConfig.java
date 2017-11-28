package com.sp.web.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * load integration jms xml
 * 
 */
@Configuration
@ImportResource("classpath:com/sp/web/config/integration-subscriber.xml")
@Component
@Profile("PROD-Subscriber")
public class IntegrationMessageReciverConfig {
}