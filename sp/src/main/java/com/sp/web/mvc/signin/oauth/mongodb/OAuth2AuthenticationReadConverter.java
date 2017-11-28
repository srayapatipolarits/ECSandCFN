package com.sp.web.mvc.signin.oauth.mongodb;

import com.mongodb.DBObject;
import com.sp.web.authentication.SPAuthority;
import com.sp.web.model.User;
import com.sp.web.user.UserFactory;
import com.sp.web.utils.ApplicationContextUtils;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

//Hackery to deserialize back into an OAuth2Authentication Object made necessary because Spring Mongo can't map clientAuthentication to authorizationRequest
@ReadingConverter
public class OAuth2AuthenticationReadConverter implements Converter<DBObject, OAuth2Authentication> {
  
  @Override
  public OAuth2Authentication convert(DBObject source) {
    DBObject storedRequest = (DBObject) source.get("storedRequest");
    OAuth2Request oAuth2Request = new OAuth2Request(
        (Map<String, String>) storedRequest.get("requestParameters"),
        (String) storedRequest.get("clientId"), null, true, new HashSet(
            (List) storedRequest.get("scope")), null, null, null, null);
    DBObject userAuthorization = (DBObject) source.get("userAuthentication");
    Object principal = getPrincipalObject(userAuthorization.get("principal"));
    Authentication userAuthentication = new UsernamePasswordAuthenticationToken(principal,
        (String) userAuthorization.get("credentials"),
        getAuthorities((List) userAuthorization.get("authorities")));
    OAuth2Authentication authentication = new OAuth2Authentication(oAuth2Request,
        userAuthentication);
    return authentication;
  }
  
  private Object getPrincipalObject(Object principal) {
    if (principal instanceof DBObject) {
      DBObject principalDBObject = (DBObject) principal;
      String email = (String) principalDBObject.get("email");
      UserFactory userFactory = ApplicationContextUtils.getBean(UserFactory.class);
      User user = userFactory.getUserByEmail(email);
      return user;
    } else {
      return principal;
    }
  }
  
  private Collection<GrantedAuthority> getAuthorities(List<Map<String, String>> authorities) {
    Set<GrantedAuthority> grantedAuthorities = new HashSet<GrantedAuthority>(authorities.size());
    for (Map<String, String> authority : authorities) {
      grantedAuthorities.add(new SPAuthority(authority.get("authority")));
    }
    return grantedAuthorities;
  }
}