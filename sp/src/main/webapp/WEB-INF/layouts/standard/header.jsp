<%@ page session="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<!-- NEW Header HTML Starts -->
<div class="print-version-header">
	<img src="<spring:theme code="logoUrl" />" alt="Print Version Logo"/>
</div>
<!-- Added for testing -->
<div class="header-body-wrap hide"></div>
<div class="new-app-header-wrap hide" data-ng-if="inviteReady" data-ng-class="inviteReady?'show':'hide'">
<header class="new-app-header" data-ng-controller="siteHeaderController" data-ng-cloak>
	<div class="header-icons-wrap" data-ng-class="{'paAdjFix': selectedNav === 'pplAnalytics'}">
		<div class="i-cont header-table-layout">
			<div class="i-cont-wrap">
				<ul data-ng-show="navLoaded">
					<li class="h-icons i-notifify" data-ng-click="displayNotifications();" data-ng-if="showStickyFooter">
						<span class="mIcons icon-icon_bell sp-icon-color"></span>
						<span class="notify-counter" data-ng-bind-html="notifyCount" data-ng-class="notifyCount!==0?'show-counter':'hide-counter'"></span>
					</li>
					<li class="h-icons i-menu" data-ng-click="showMenu=!showMenu">
						<span class="mIcons icon-icon_menu sp-icon-color" data-ng-class="{'reduce-icon-margin':selectedNav === 'pplAnalytics'}"></span>
						<span class="menu-txt sp-icon-color" data-ng-bind-html="pullInterNationalization('navigation.menu')"></span>
					</li>
				</ul>
			</div>
		</div>	
	</div>
	
	<div class="header-logo-wrap">
		<div class="logo-cont header-table-layout">
            <h1>
				<a data-ng-if="selectedNav === 'pplAnalytics'" href="/sp/pa/dashboard"><img src="<spring:theme code="logoUrl" />" alt="SurePeople&trade;"/></a>
      		    <a data-ng-if="selectedNav !== 'pplAnalytics'" href="/sp/"><img src="<spring:theme code="logoUrl" />" alt="SurePeople&trade;"/></a>
      		</h1>		
		</div>
	</div>
	<!-- Notifications Module -->
    <notification-center url-src="common/notification-center.html" class="action-plan-section-panel" data-ng-if="showNotifications"></notification-center>
    
	<nav-module></nav-module>
</header>
</div>
<!-- NEW Header HTML Ends -->