<%@ page session="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>	
<div class="new-app-header-wrap non-logged" data-ng-controller="loginLangController as ctrl">
    <header class="new-app-header">
        <div class="header-icons-wrap widthAdj hide" data-ng-if="inviteReady" data-ng-class="inviteReady?'show':'hide'">
            <div class="i-cont header-table-layout">
                <div class="i-cont-wrap">
                    <div class="nav-langOpts-wrapper non-logged">
                        <h2 sp-condition="true" sp-click-outside="show">
							<span class="nav-lang desc-icn icon-icon_language"></span>
							<span class="nav-lang desc-text">
								<span class="mob-text-ellip" data-ng-bind-html="::pullInterNationalization('navigation.lang.'+ctrl.selectedLang.replace('_',''))"></span>
							</span>
							<span class="nav-lang desc-dd icon-icon_dropdown-arrow sp-icon-color"></span>
						</h2>
                        <div class="actions-callout-wrappper lang-module adj" data-ng-if="show">
                            <ul class="lang-links">
                                <li>
                                    <a href="javascript:void(0)" data-ng-click="ctrl.updateLang('en-US');" data-ng-bind-html="::pullInterNationalization('navigation.lang.enUS')"></a>
                                </li>
                                <li>
                                    <a href="javascript:void(0)" data-ng-click="ctrl.updateLang('es-LA');" data-ng-bind-html="::pullInterNationalization('navigation.lang.esLA')"></a>
                                </li>
                            </ul>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div class="header-logo-wrap">
            <div class="logo-cont header-table-layout">
                <h1>
				<a href="/"><img src="<spring:theme code="logoUrl" />" alt="SurePeople&trade;"/></a>
      		</h1>
            </div>
        </div>
    </header>
</div>
