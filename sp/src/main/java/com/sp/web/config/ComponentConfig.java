package com.sp.web.config;

import com.sp.web.audit.LogAuditService;

import net.authorize.util.StringUtils;
import net.sf.ehcache.management.ManagementService;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jmx.support.MBeanServerFactoryBean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.management.MBeanServer;

/**
 * Configuration for SP application @Components such as @Services,
 * 
 * @Repositories, and @Controllers. Loads externalized property values required to configure the
 *                various application properties. Not much else here, as we rely on @Component
 *                scanning in conjunction with @Inject by-type autowiring.
 * @author Dax Abraham
 */
@Configuration
@ComponentScan(basePackages = "com.sp.web")
@EnableCaching
@EnableAsync
@EnableScheduling
@EnableAspectJAutoProxy
public class ComponentConfig implements SchedulingConfigurer {
  
  @Override
  public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
    taskRegistrar.setScheduler(taskSchedulerExecutor());
  }
  
  @Bean(destroyMethod = "shutdown")
  public Executor taskSchedulerExecutor() {
    return Executors.newScheduledThreadPool(100);
  }
  
  @Bean
  public CacheManager cacheManager() {
    return new EhCacheCacheManager(ehCacheCacheManager().getObject());
  }
  
  /**
   * <code>ehCacheManager</code>
   * 
   * @return
   */
  @Bean
  public EhCacheManagerFactoryBean ehCacheCacheManager() {
    EhCacheManagerFactoryBean cmfb = new EhCacheManagerFactoryBean();
    String envProfile = System.getProperty("ehcache");
    if (StringUtils.isEmpty(envProfile)) {
      envProfile = "DEV";
    }
    cmfb.setConfigLocation(new ClassPathResource("ehcache-" + envProfile + ".xml"));
    cmfb.setShared(true);
    return cmfb;
  }
  
  @Bean
  public MBeanServerFactoryBean mbeanServer() {
    MBeanServerFactoryBean beanServerFactoryBean = new MBeanServerFactoryBean();
    beanServerFactoryBean.setLocateExistingServerIfPossible(true);
    return beanServerFactoryBean;
  }
  
  @Bean
  public ManagementService managementService() {
    net.sf.ehcache.CacheManager cacheManager = ehCacheCacheManager().getObject();
    MBeanServer beanServer = mbeanServer().getObject();
    ManagementService managementService = new ManagementService(cacheManager, beanServer, true,
        true, true, true);
    managementService.init();
    return managementService;
  }
  
  @Bean(name = "logAuditService")
  public LogAuditService logAuditService() {
    return new LogAuditService();
  }
}