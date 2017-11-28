<%@ page session="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>

<c:set var="requestUrl" value="${requestScope['javax.servlet.forward.request_uri']}"></c:set>

<!-- Inner Navigation Starts -->
<div class="x-container inner-nav profile-inner-nav">
    <div class="container">
        <div class="row">
            <div class="col-md-12">
                <ul>
					<li class="first">
						<a href="/sp/profile" <c:if test="${fn:containsIgnoreCase(requestUrl, 'profile')}">class="active"</c:if>><spring:message code="navigation.prism.profile" /></a>
					</li>
					<sec:authorize access="hasRole('IndividualAccountAdministrator')">
					<li class="last">
						<a href="/sp/feedback" <c:if test="${fn:containsIgnoreCase(requestUrl, 'feedback')}">class="active"</c:if>><spring:message code="navigation.prism.360" /></a>
					</li>
					</sec:authorize>
					<sec:authorize access="!hasRole('IndividualAccountAdministrator')">
					<li>
						<a href="/sp/feedback" <c:if test="${fn:containsIgnoreCase(requestUrl, 'feedback')}">class="active"</c:if>><spring:message code="navigation.prism.360" /></a>
					</li>
                    <li class="last">
                        <a href="/sp/prism/relationship-advisor" <c:if test="${fn:containsIgnoreCase(requestUrl, 'relationship-advisor')}">class="active"</c:if>>
                                <spring:message code="navigation.prism.relationship" />
						</a>
                    </li>
                    </sec:authorize>
                </ul>
            </div>
        </div>
    </div>
</div>
<!-- Inner Navigation Ends -->