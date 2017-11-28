package com.sp.web.test.setup;

import com.sp.web.authentication.SPAuthority;
import com.sp.web.model.RoleType;
import com.sp.web.model.SPUserDetail;
import com.sp.web.model.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpSession;

@Component
public class TestOauthHelper {
  @Autowired
  AuthorizationServerTokenServices tokenservice;
  
  public RequestPostProcessor addBearerToken(final User user, HttpSession session,
      String... authorities) {
    return mockRequest -> {
      // Create OAuth2 token
      OAuth2Request oauth2Request = new OAuth2Request(null, "test", null, true, null, null, null,
          null, null);
      
      Set<GrantedAuthority> grantedAuthoritiesList = new HashSet<GrantedAuthority>();
      if (user.getRoles() != null) {
        for (RoleType role : user.getRoles()) {
          grantedAuthoritiesList.add(new SPAuthority(role));
        }
      }
      SPUserDetail detail = new SPUserDetail(user, grantedAuthoritiesList);
      Authentication userauth = new UsernamePasswordAuthenticationToken(detail, "admin",
          grantedAuthoritiesList);
      
      OAuth2Authentication oauth2auth = new OAuth2Authentication(oauth2Request, userauth);
      OAuth2AccessToken token = tokenservice.createAccessToken(oauth2auth);
      mockRequest.setSession(session);
      // Set Authorization header to use Bearer
      mockRequest.addHeader("Authorization", "Bearer " + token.getValue());
      return mockRequest;
    };
  }
}
