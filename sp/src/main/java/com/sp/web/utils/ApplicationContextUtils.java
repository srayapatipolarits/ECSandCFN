package com.sp.web.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author Dax Abraham
 * 
 *         The utility class to get the application context.
 */
@Component
public class ApplicationContextUtils implements ApplicationContextAware {

  private static ApplicationContext ctx;

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework
   * .context.ApplicationContext)
   */
  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    setAppContext(applicationContext);
  }

  private static void setAppContext(ApplicationContext applicationContext) {
    ApplicationContextUtils.ctx = applicationContext;
  }

  public static ApplicationContext getApplicationContext() {
    return ctx;
  }

  /**
   * Get the bean.
   * 
   * @param className
   *          - class name
   * @param args
   *          - constructor arguments
   * @return - the bean instance
   */
  public static <T> T getBean(Class<T> className, Object... args) {
    return (args == null || args.length == 0) ? getApplicationContext().getBean(className)
        : getApplicationContext().getBean(className, args);
  }

  /**
   * Helper method to the bean from application context with the given bean name.
   * 
   * @param beanName
   *          - name of the bean to get
   * @param args
   *          - optional arguments for the bean
   * @return the bean for the given bean name
   */
  public static Object getBean(String beanName, Object... args) {
    return (args == null || args.length == 0) ? getApplicationContext().getBean(beanName)
        : getApplicationContext().getBean(beanName, args);
  }
}
