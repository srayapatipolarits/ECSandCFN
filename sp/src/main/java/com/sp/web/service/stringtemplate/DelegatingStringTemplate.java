package com.sp.web.service.stringtemplate;

import org.apache.log4j.Logger;
import org.stringtemplate.v4.DateRenderer;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.StringRenderer;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;

/**
 * Package-private helper that delegates to org.antlr.stringtemplate as the actual StringTemplate
 * implementation. Encapsulates the dependency on Terrence Parr's StringTemplate implementation.
 * 
 * @author pruhil
 */
class DelegatingStringTemplate implements StringTemplate {
  
  private static final Logger LOG = Logger.getLogger(DelegatingStringTemplate.class);
  
  /**
   * Delagating instance of String Template
   */
  private ST delegate;
  
  /**
   * Constructor intializing the string template bean
   * 
   * @param delegate
   *          {@link ST} instance
   */
  public DelegatingStringTemplate(ST delegate) {
    this.delegate = delegate;
  }
  
  /**
   * <code>put</code> method will remove the value first assoicaited with template and then add the
   * value synchronously.
   * 
   * @param name
   *          of the model to be replaced in the template
   * @param value
   *          to be replaced with
   */
  public void put(String name, Object value) {
    delegate.add(name, value);
  }
  
  public String render() {
    return delegate.render();
  }

  /* (non-Javadoc)
   * @see com.sp.web.service.stringtemplate.StringTemplate#render(java.lang.String)
   */
  @Override
  public String render(String templateString) {
    ST template = new ST(templateString, '$', '$');
    template.groupThatCreatedThisInstance.registerRenderer(Date.class, new DateRenderer());
    template.groupThatCreatedThisInstance.registerRenderer(String.class, new StringRenderer());
    template.groupThatCreatedThisInstance.registerRenderer(LocalDateTime.class, new LocalDateTimeRenderer());
    Map<String, Object> attributes = delegate.getAttributes();
    attributes.forEach((key,value) -> {template.add(key, value);
    LOG.info("key : " + key + ", value : " + value);
    });
    return template.render();
  }
  
}