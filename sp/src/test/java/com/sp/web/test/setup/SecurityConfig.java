package com.sp.web.test.setup;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Spring Security Configuration. Applies the policies that secure the Surepeople web application.
 * 
 * <p>
 * In standard mode, we apply standard password encoding (SHA-256 1024 iteration hashing + random
 * salting) and encryption (Password-based 256-Bit AES + site-global salt + secure random 16-byte iV
 * handling).
 * </p>
 * <p>
 * Spring Security is currently best configured using its XML namespace, so this class imports a XML
 * file containing most of the configuration information. The PasswordEncoder, TextEncryptor are
 * configured in Java.
 * </p>
 * 
 * @author pradeep
 */
@Configuration
@ImportResource(value = { "classpath:com/sp/web/config/spring-oauth2-test.xml",
    "classpath:com/sp/web/config/security.xml" })
@Component
@Profile("Test")
public class SecurityConfig {
  
  /**
   * <code>passwordEncoder</code> method will return BCryptPasswordEncoder encoder which is more
   * secured password encoding. Default log parameter is 10
   * 
   * @return the BCryptPasswordEncoder
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}