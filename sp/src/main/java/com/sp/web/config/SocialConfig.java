package com.sp.web.config;

import com.sp.web.model.User;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.social.UserIdSource;
import org.springframework.social.config.annotation.ConnectionFactoryConfigurer;
import org.springframework.social.config.annotation.EnableSocial;
import org.springframework.social.config.annotation.SocialConfigurer;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.mem.InMemoryUsersConnectionRepository;
import org.springframework.social.connect.web.ConnectController;
import org.springframework.social.connect.web.ReconnectFilter;
import org.springframework.social.linkedin.api.LinkedIn;
import org.springframework.social.linkedin.connect.LinkedInConnectionFactory;

/**
 * Social Config provides integration with linkedin.
 */
@Configuration
@EnableSocial
public class SocialConfig implements SocialConfigurer {

  //
  // SocialConfigurer implementation methods
  //

  /**
   * Add connection factory for linkedin.
   */
  @Override
  public void addConnectionFactories(ConnectionFactoryConfigurer cfConfig, Environment env) {

    cfConfig.addConnectionFactory(new LinkedInConnectionFactory(env.getProperty("linkedin.appKey"), env
        .getProperty("linkedin.appSecret")));
  }

  /**
   * <code>getUserIdSource</code> method returns the current logged in userid.
   */
  @Override
  public UserIdSource getUserIdSource() {
    return new UserIdSource() {
      @Override
      public String getUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
          throw new IllegalStateException("Unable to get a ConnectionRepository: no user signed in");
        }
        User user = (User) authentication.getPrincipal();

        return user.getEmail();
      }
    };
  }

  /**
   * <code>LinkedConnectionRepository</code> managing the linkedin connection in memory.
   */
  @Override
  public UsersConnectionRepository getUsersConnectionRepository(ConnectionFactoryLocator connectionFactoryLocator) {
    return new InMemoryUsersConnectionRepository(connectionFactoryLocator);
  }

  /**
   * <code>linkedin</code> proivdes the method to integrate with the linkedin api
   * 
   * @param repository
   *          used for managing the connections.
   * @return the logged in user
   */
  @Bean
  @Scope(value = "request", proxyMode = ScopedProxyMode.INTERFACES)
  public LinkedIn linkedin(ConnectionRepository repository) {
    Connection<LinkedIn> connection = repository.findPrimaryConnection(LinkedIn.class);
    return connection != null ? connection.getApi() : null;
  }

  /**
   * <code>connectCOntrooler</code> is the contoller for connecting to linkedin. Reqeuest URL to connect the linkedin is
   * = http://<host>/<app_name>/linkedin.
   * 
   * @param connectionFactoryLocator
   * @param connectionRepository
   * @return
   */
  @Bean
  public ConnectController connectController(ConnectionFactoryLocator connectionFactoryLocator,
      ConnectionRepository connectionRepository) {
    ConnectController connectController = new ConnectController(connectionFactoryLocator, connectionRepository);
    return connectController;
  }

  @Bean
  public ReconnectFilter apiExceptionHandler(UsersConnectionRepository usersConnectionRepository,
      UserIdSource userIdSource) {
    return new ReconnectFilter(usersConnectionRepository, userIdSource);
  }

}
