package com.sp.web.config;

import com.sp.web.Constants;

import org.apache.tiles.Definition;
import org.apache.tiles.definition.LocaleDefinitionsFactory;
import org.apache.tiles.definition.NoSuchDefinitionException;
import org.apache.tiles.request.Request;

import java.util.Locale;

/**
 * SPLocaleDefinitationsFactory class is added to remove the localization for the tiles framework
 * causing GC overhead limit error in JVM. To enable the localization, need to create specific
 * tiles_locale specific files and comment the definiation factory initialized in WebConfig.
 * 
 * @author pradeepruhil
 *
 */
public class SPLocaleDefinitionsFactory extends LocaleDefinitionsFactory {
  
  /** {@inheritDoc} */
  @Override
  public Definition getDefinition(String name, Request tilesContext) {
    Definition retValue;
    Locale locale = null;
    
    /* using tiles_lcoale specific for support page, so getting the locale for support page only */
    if ("supportHome".equalsIgnoreCase(name) || "paSupportHome".equalsIgnoreCase(name)) {
      if (tilesContext != null) {
        locale = localeResolver.resolveLocale(tilesContext);
        if (locale.toString().equalsIgnoreCase(Constants.DEFAULT_LOCALE)) {
          locale = null;
        }
      }
    }
    retValue = definitionDao.getDefinition(name, locale);
    if (retValue != null) {
      retValue = new Definition(retValue);
      String parentDefinitionName = retValue.getExtends();
      while (parentDefinitionName != null) {
        Definition parent = definitionDao.getDefinition(parentDefinitionName, locale);
        if (parent == null) {
          throw new NoSuchDefinitionException("Cannot find definition '" + parentDefinitionName
              + "' ancestor of '" + retValue.getName() + "'");
        }
        retValue.inherit(parent);
        parentDefinitionName = parent.getExtends();
      }
    }
    
    return retValue;
  }
}
