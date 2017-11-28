package com.sp.web.controller.social;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

/**
 * LinkedInResponseFilter class will replace the new line carriage return and
 * new line response filter.
 * 
 * @author pradeepruhil
 */
public final class LinkedInResponseFilter implements Filter {

  /** Initializing the logger. */
  private static final Logger LOG = Logger.getLogger(LinkedInResponseFilter.class);

  @Override
  public void init(FilterConfig filterConfig) {
  }

  /**
   * do filter to precess the response
   */
  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
      ServletException {

    ResponseWrapper responseWrapper = new ResponseWrapper((HttpServletResponse) response);

    chain.doFilter(request, responseWrapper);

    String responseContent = new String(responseWrapper.getDataStream());
    responseContent = StringUtils.replacePattern(responseContent, "\\\\r\\\\n", "<br/>");
    responseContent = StringUtils.replacePattern(responseContent, "\\\\n", "<br/>");

    LOG.debug("Replaced content is " + responseContent);
    byte[] responseToSend = responseContent.getBytes();

    response.getOutputStream().write(responseToSend);

  }

  @Override
  public void destroy() {
  }

}