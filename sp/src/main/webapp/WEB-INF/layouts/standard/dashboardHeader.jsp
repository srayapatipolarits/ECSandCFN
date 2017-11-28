<%@ page session="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<!-- Header Starts -->
<c:set var="requestUrl" value="${requestScope['javax.servlet.forward.request_uri']}"></c:set>
<div class="x-container header-wrapper logged-in">

	<header class="container">
		<div class="row">
			<div class="col-xs-8 col-sm-8 col-md-10">
				<a href="/sp/" class="logo"> 
				<img  src="<spring:theme code="logoUrl" />" alt="SurePeople&trade;"/>
				</a>
			</div>
			<sec:authorize ifNotGranted="ROLE_ANONYMOUS">

				<sec:authentication property="principal" var="user" />
				<c:if test="${user.userStatus eq 'VALID' }">
					<div class="profile-menu-links hide" data-links="profileLogout">

						<ul>
							<!-- Enter assessemetn Assessmenet Completd thing  -->

							<sec:authorize access="!hasRole('IndividualAccountAdministrator')">
							<li><a href="/sp/admin/account/profile/details/"><spring:message code="navigation.dropDown.personalProfile" /></a></li>
							</sec:authorize>

							<sec:authorize access="hasRole('IndividualAccountAdministrator')">
								<li><a href="/sp/admin/account/individual"><spring:message code="navigation.dropDown.account" /></a></li>
							</sec:authorize>
							<sec:authorize
								access="hasAnyRole('Administrator','AccountAdministrator','Demo')">
								<li><a href="/sp/admin/member/#member"><spring:message code="navigation.dropDown.account" /></a></li>
							</sec:authorize>

							<li><a href="/sp/logout" class="highlight"><spring:message code="navigation.dropDown.logout" /></a></li>
						</ul>
					</div>
					<div class="col-xs-4 col-sm-4 col-md-2"
						ng-controller="signedInUserController">
						<a href="#" class="logout click-area" ng-click="logoutMenu('profileLogout')" title="Account Information">
							<span class="new-image click-area">
								<img ng-src="{{userProfile.smallProfileImage}}" alt="{{userProfile.userInitials}}" ng-if="userProfile.smallProfileImage" class="click-area"/>
								<span class="imgReplacementSmall click-area" ng-if="!userProfile.smallProfileImage" ng-bind="userProfile.userInitials"></span>
							</span> 
							<span class="sp360 click-area" data-ng-bind="userProfile.firstName"></span>
						</a>
						
					</div>
				</c:if>
				<c:if test="${user.userStatus ne 'VALID' }">
					<div class="profile-menu-links hide" data-links="profileLogout">

						<ul>
							<!-- Enter assessemetn Assessmenet Completd thing  -->
							<li><a href="/sp/logout" class="highlight"><spring:message code="navigation.dropDown.logout" /></a></li>
						</ul>
					</div>
				</c:if>

			</sec:authorize>
		</div>
	</header>
</div>


<sec:authorize ifNotGranted="ROLE_ANONYMOUS">

	<sec:authentication property="principal" var="user" />

	<c:if test="${user.userStatus eq 'VALID' }">

		<div class="x-container nav-wrapper">
			<nav class="container">
				<div class="row">
					<div class="col-xs-12">
						<ul>
							<li class="first"><a href="/sp/dashboard" <c:if test="${fn:containsIgnoreCase(requestUrl, 'dashboard') or fn:containsIgnoreCase(requestUrl, 'pulse')}">class="on"</c:if>> <span class="text"><spring:message code="navigation.dashboard" /></span>
									<span class="_icon"></span>
							</a></li>
							<li><a href="/sp/profile" <c:if test="${!fn:endsWith(requestUrl,'profile/details/') and (fn:containsIgnoreCase(requestUrl, 'profile') or fn:containsIgnoreCase(requestUrl, 'feedback') or fn:containsIgnoreCase(requestUrl, 'prism'))}">class="on"</c:if>> <span class="text"><spring:message code="navigation.prism" /></span>
									<span class="_icon"></span>
							</a></li>
							<li><a href="/sp/goals" <c:if test="${fn:containsIgnoreCase(requestUrl, 'goals') or fn:containsIgnoreCase(requestUrl, 'library') }">class="on"</c:if>> <span class="text"><spring:message code="navigation.curriculum" /></span> <span
									class="_icon"></span>
							</a></li>
							<li><a href="/sp/growth/growthListing" <c:if test="${fn:containsIgnoreCase(requestUrl, 'growth')}">class="on"</c:if>> <span
									class="text"><spring:message code="navigation.growth" /></span> <span class="_icon"></span>
							</a></li>
							<sec:authorize access="hasAnyRole('Hiring','AccountAdministrator','SuperAdministrator')">
							<li><a href="/sp/hiring" <c:if test="${fn:containsIgnoreCase(requestUrl, 'tools')}">class="on"</c:if>> <span
									class="text"><spring:message code="navigation.hiring" /></span> <span class="_icon"></span>
							</a></li>
							</sec:authorize>
							<sec:authorize access="hasRole('IndividualAccountAdministrator')">
							<li><a href="/sp/spResume" <c:if test="${fn:containsIgnoreCase(requestUrl, 'spResume')}">class="on"</c:if>> <span
									class="text"><spring:message code="navigation.spresume" /></span> <span class="_icon"></span>
							</a></li>
							</sec:authorize>
							<li class="last"><a href="/sp/support" <c:if test="${fn:containsIgnoreCase(requestUrl, 'support')}">class="on"</c:if>> <span
									class="text"><spring:message code="navigation.support" /></span> <span class="_icon"></span>
							</a></li>
						</ul>
					</div>
				</div>
			</nav>
		</div>
	</c:if>
</sec:authorize>

<tiles:insertAttribute name="adminHeader" />