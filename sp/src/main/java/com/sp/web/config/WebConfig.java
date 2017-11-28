/*
 * Copyright 2010-2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sp.web.config;

import com.sp.web.Constants;
import com.sp.web.controller.i18n.I18nMessagesHelper;
import com.sp.web.controller.i18n.SPResourceBundleMessageSource;
import com.sp.web.theme.SurePeopleMongoThemeSource;
import com.sp.web.utils.DateTimeZoneHandlerInterceptor;
import com.sp.web.utils.Location;
import com.sp.web.utils.UserLocationHandlerInterceptor;

import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.MethodParameter;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.format.datetime.joda.JodaTimeContextHolder;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mobile.device.DeviceHandlerMethodArgumentResolver;
import org.springframework.mobile.device.DeviceResolverHandlerInterceptor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.theme.CookieThemeResolver;
import org.springframework.web.servlet.view.UrlBasedViewResolver;
import org.springframework.web.servlet.view.tiles3.TilesConfigurer;
import org.springframework.web.servlet.view.tiles3.TilesView;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

/**
 * Spring MVC Configuration. Implements {@link WebMvcConfigurer}, which provides convenient
 * callbacks that allow us to customize aspects of the Spring Web MVC framework. These callbacks
 * allow us to register custom interceptors, message converters, argument resovlers, a validator,
 * resource handling, and other things.
 * 
 * @author Dax Abraham
 * @see WebMvcConfigurer
 */
@Configuration
@EnableWebMvc
@EnableCaching
@EnableAsync
@PropertySource("file:${appPropsFile}/application.properties")
public class WebConfig extends WebMvcConfigurerAdapter {
  
  @Inject
  private Environment environment;
  
  @Autowired
  private I18nMessagesHelper helper;
  
  // implementing WebMvcConfigurer
  
  /**
   * Interceptors.
   */
  public void addInterceptors(InterceptorRegistry registry) {
    // registry.addInterceptor(new
    // AccountExposingHandlerInterceptor()).addPathPatterns("/");
    registry.addInterceptor(new DateTimeZoneHandlerInterceptor());
    registry.addInterceptor(new UserLocationHandlerInterceptor());
    registry.addInterceptor(new DeviceResolverHandlerInterceptor());
    LocaleChangeInterceptor changeInterceptor = new SPLocaleChangeInterceptor();
    changeInterceptor.setParamName("lang");
    registry.addInterceptor(changeInterceptor);
  }
  
  /**
   * Argument resolvers.
   */
  public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
    // argumentResolvers.add(new AccountHandlerMethodArgumentResolver());
    argumentResolvers.add(new DateTimeZoneHandlerMethodArgumentResolver());
    argumentResolvers.add(new LocationHandlerMethodArgumentResolver());
    argumentResolvers.add(new DeviceHandlerMethodArgumentResolver());
  }
  
  /**
   * The configurer to resolve all the properties.
   * 
   * @return the property source configurer
   */
  @Bean
  public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
    PropertySourcesPlaceholderConfigurer properties = new PropertySourcesPlaceholderConfigurer();
    
    String filePath = System.getProperty("appPropsFile");
    properties.setLocation(new FileSystemResource(filePath + "/application.properties"));
    properties.setIgnoreResourceNotFound(false);
    
    return properties;
  }
  
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/resources/**").addResourceLocations("/resources/");
  }
  
  public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
    converters.add(new MappingJackson2HttpMessageConverter());
    StringHttpMessageConverter stringConverter = new StringHttpMessageConverter(
        Charset.forName("UTF-8"));
    stringConverter.setSupportedMediaTypes(Arrays.asList(MediaType.TEXT_PLAIN, MediaType.TEXT_HTML,
        MediaType.APPLICATION_JSON));
    converters.add(stringConverter);
  }
  
  /**
   * Validators.
   */
  public Validator getValidator() {
    LocalValidatorFactoryBean factory = new LocalValidatorFactoryBean();
    // ReloadableResourceBundleMessageSource messageSource = new SPResourceBundleMessageSource();
    // messageSource.setCacheSeconds(-1);
    // messageSource.setDefaultEncoding("UTF-8");
    // messageSource.setBasename(Constants.MESSAGE_BASE_NAME);
    factory.setValidationMessageSource(messageSource());
    return factory;
  }
  
  // additional webmvc-related beans
  
  /**
   * ViewResolver configuration required to work with Tiles2-based views.
   */
  @Bean
  public ViewResolver viewResolver() {
    UrlBasedViewResolver viewResolver = new UrlBasedViewResolver();
    viewResolver.setViewClass(TilesView.class);
    return viewResolver;
  }
  
  /**
   * Configures Tiles at application startup.
   */
  @Bean
  public TilesConfigurer tilesConfigurer() {
    TilesConfigurer configurer = new TilesConfigurer();
    configurer.setDefinitions(new String[] { "/WEB-INF/layouts/tiles.xml",
        "/WEB-INF/views/**/tiles.xml" });
    configurer.setCheckRefresh(false);
    configurer.setDefinitionsFactoryClass(SPLocaleDefinitionsFactory.class);
    return configurer;
  }
  
  /**
   * Messages to support internationalization/localization.
   */
  @Bean
  public MessageSource messageSource() {
    ReloadableResourceBundleMessageSource messageSource = new SPResourceBundleMessageSource();
    int cacheTime = Integer.valueOf(environment.getProperty("propertiesCacheTime"));
    messageSource.setCacheSeconds(cacheTime);
    // messageSource.setBasename(Constants.MESSAGE_BASE_NAME);
    Set<String> messageFileToIncluded = helper.getMessageFileToIncluded();
    String[] array = messageFileToIncluded.toArray(new String[messageFileToIncluded.size()]);
    messageSource.setBasenames(array);
    messageSource.setDefaultEncoding("UTF-8");
    return messageSource;
  }
  
  /**
   * Supports FileUploads.
   */
  @Bean
  public MultipartResolver multipartResolver() {
    CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
    // Max file upload size is 10MB through Media Manager
    multipartResolver.setMaxUploadSize(Constants.MAX_FILE_UPLOAD_SIZE);
    return multipartResolver;
  }
  
  // custom argument resolver inner classes
  /*
   * private static class AccountHandlerMethodArgumentResolver implements
   * HandlerMethodArgumentResolver {
   * 
   * public boolean supportsParameter(MethodParameter parameter) { return
   * User.class.isAssignableFrom(parameter.getParameterType()); }
   * 
   * public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer
   * modelAndViewContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws
   * Exception { Authentication auth = (Authentication) webRequest.getUserPrincipal(); return auth
   * != null && auth.getPrincipal() instanceof User ? auth.getPrincipal() : null; }
   * 
   * }
   */
  private static class DateTimeZoneHandlerMethodArgumentResolver implements
      HandlerMethodArgumentResolver {
    
    public boolean supportsParameter(MethodParameter parameter) {
      return DateTimeZone.class.isAssignableFrom(parameter.getParameterType());
    }
    
    public Object resolveArgument(MethodParameter parameter,
        ModelAndViewContainer modelAndViewContainer, NativeWebRequest webRequest,
        WebDataBinderFactory binderFactory) throws Exception {
      return JodaTimeContextHolder.getJodaTimeContext().getTimeZone();
    }
    
  }
  
  private static class LocationHandlerMethodArgumentResolver implements
      HandlerMethodArgumentResolver {
    
    public boolean supportsParameter(MethodParameter parameter) {
      return Location.class.isAssignableFrom(parameter.getParameterType());
    }
    
    public Object resolveArgument(MethodParameter parameter,
        ModelAndViewContainer modelAndViewContainer, NativeWebRequest webRequest,
        WebDataBinderFactory binderFactory) throws Exception {
      return webRequest.getAttribute(UserLocationHandlerInterceptor.USER_LOCATION_ATTRIBUTE,
          WebRequest.SCOPE_REQUEST);
    }
    
  }
  
  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }
  
  @Bean
  public SurePeopleMongoThemeSource themeSource() {
    return new SurePeopleMongoThemeSource();
  }
  
  @Bean
  public CookieThemeResolver themeResolver() {
    CookieThemeResolver cookieThemeResolver = new CookieThemeResolver();
    cookieThemeResolver.setDefaultThemeName(Constants.DEFAULT_THEME_NAME);
    return cookieThemeResolver;
  }
  
  @Override
  public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
    configurer.setDefaultTimeout(-1);
    configurer.setTaskExecutor(asyncTaskExecutor());
  }
  
  @Bean
  public AsyncTaskExecutor asyncTaskExecutor() {
    return new SimpleAsyncTaskExecutor("async");
  }
  
  @Bean
  public CookieLocaleResolver localeResolver() {
    CookieLocaleResolver cookieLocaleResolver = new CookieLocaleResolver();
    cookieLocaleResolver.setCookieName("sp-locale");
    cookieLocaleResolver.setCookiePath("/");
    return cookieLocaleResolver;
  }
}