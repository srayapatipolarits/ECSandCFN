<%@ page session="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="sec"
    uri="http://www.springframework.org/security/tags"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>   

<div class="new-assessment-header">
	<header class="assessment-wide-header">
		<div class="header-left-section logo">
            <h1>
				<a href="/sp/"><img src="/resources/images/sp-logo.svg" alt="SurePeople&trade;"/></a>
      		</h1>		
		</div>
		<sec:authorize access="hasRole('User')">
			<div class="header-right-section">
				<a href="/sp/logout" class="assessment-save">
					<span class="aIco icon-assessment_logout"></span>
					<span class="iTxt"><spring:message code="assessment.savelogout" /></span>
				</a>			
			</div>
		</sec:authorize>
	</header>
	<header class="assessment-responsive-header" assessment-responsive-header>
		<ol class="assessment-header-links">
			<li class="flL">
				<a href="javascript:void(0)" class="prev-ic icon-assessment_back" id="assessmentResponsivePrevious" data-ng-class="{'disabled':headerDisablePrevious, 'enabled': !headerDisablePrevious}"></a>
			</li>
			<li>
				<a href="/sp/" class="logo-ic icon-logo-badge" title="SurePeople&trade;"></a>
			</li>
			<li class="flR">
				<sec:authorize access="hasRole('User')">
					<a href="javascript:void(0)" class="ham-ic icon-assessment_hamburger" id="assessmentResponsiveMenu"></a>
				</sec:authorize>
			</li>
		</ol>
	</header>
</div>