package com.sp.web.controller.email;

import com.sp.web.audit.Audit;
import com.sp.web.controller.notifications.NotificationType;
import com.sp.web.model.email.EmailManagement;
import com.sp.web.service.email.EmailManagementFactory;
import com.sp.web.service.email.EmailTemplateFactory;
import com.sp.web.service.stringtemplate.ResourceStringTemplateFactory;
import com.sp.web.service.stringtemplate.StringTemplateFactory;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.stringtemplate.v4.ST;

import java.util.Map;

/**
 * EmailTemplateController will provide the template of the email passed.
 * 
 * @author pradeepruhil
 *
 */
@Controller
public class EmailTemplateController {
  
  @Autowired
  private StringTemplateFactory stringTemplateFactory;
  
  @Autowired
  private EmailManagementFactory emailManagementFactory;
  
  @Autowired
  private EmailTemplateFactory emailTemplateFactory;
  
  @RequestMapping(value = "/template/email/{companyId}/{notificationType}", method = RequestMethod.GET, produces = { "text/html" })
  @ResponseBody
  @Audit(skip = true)
  public String getEmailTempalte(@RequestParam String templateName, @PathVariable String companyId,
      @PathVariable NotificationType notificationType, Authentication authentication) {
    if (StringUtils.isNotBlank(companyId)) {
      EmailManagement emailManagement = emailManagementFactory.get(companyId);
      if (emailManagement != null && emailManagement.getContent() != null) {
        Map<String, String> map = emailManagement.getContent().get(notificationType);
        String templateString = stringTemplateFactory.localizedTemplateString(templateName);
        ST stringTemplate = ResourceStringTemplateFactory.loadStringTemplate(templateString,
            templateName);
        map.forEach((k, v) -> {
          stringTemplate.add(k, v);
        });
        String render = stringTemplate.render();
        return render;
      }
      return stringTemplateFactory.localizedTemplateString(templateName);
      
    } else {
      return stringTemplateFactory.localizedTemplateString(templateName);
    }
    
  }
  
  @RequestMapping(value = "/template/email/{notificationType}", method = RequestMethod.GET, produces = { "text/html" })
  @ResponseBody
  @Audit(skip = true)
  public String getEmailTempalte(@RequestParam String templateName,
      @PathVariable NotificationType notificationType, Authentication authentication) {
    return stringTemplateFactory.localizedTemplateString(templateName);
    
  }
  
  @RequestMapping(value = "/template/updateToSmartling/{notificationType}", method = RequestMethod.GET, produces = { "text/html" })
  @ResponseBody
  @Audit(skip = true)
  public String updateToSmartling(String locale, @PathVariable NotificationType notificationType,
      Authentication authentication) {
    return emailTemplateFactory.getEmailTemplate(locale, notificationType.getTemplateName(),
        "default", notificationType.toString(), false);
  }

}
