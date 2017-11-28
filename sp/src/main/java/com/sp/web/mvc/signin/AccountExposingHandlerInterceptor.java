/*
 * Copyright 2010 the original author or authors.
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
package com.sp.web.mvc.signin;

import com.sp.web.model.SPUserDetail;
import com.sp.web.model.User;
import com.sp.web.utils.GenericUtils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Dax Abraham
 * 
 *         This is the Intercepter to help get the logged in user account easily in the controllers.
 *
 */
public class AccountExposingHandlerInterceptor implements HandlerInterceptor {
  
  /**
   * PreHandle to handle the authentication for the account.
   */
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth != null
        && (auth.getPrincipal() instanceof User || auth.getPrincipal() instanceof SPUserDetail)) {
      request.setAttribute("user", GenericUtils.getUserFromAuthentication(auth));
    }
    return true;
  }
  
  public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
      ModelAndView modelAndView) throws Exception {
  }
  
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
      Object handler, Exception ex) throws Exception {
  }
}
