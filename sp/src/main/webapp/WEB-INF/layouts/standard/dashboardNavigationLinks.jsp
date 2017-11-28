<%@ page session="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>

<c:set var="requestUrl"
	value="${requestScope['javax.servlet.forward.request_uri']}"></c:set>
<!-- Inner Navigation Starts -->
<div class="x-container inner-nav profile-inner-nav">
	<div class="container">
		<div class="row">
			<div class="col-md-12">
				<ul>
					<li class="first"><a href="/sp/dashboard"
						<c:if test="${fn:endsWith(requestUrl, 'dashboard')}">class="active"</c:if>><spring:message code="navigation.dashboard.member"/></a>
					</li>
					<sec:authorize access="hasRole('GroupLead')">
						<li>
							<a href="/sp/dashboard/groups"
								<c:if test="${fn:endsWith(requestUrl, 'groups') or fn:endsWith(requestUrl, 'dashboardUserAnalysis')}">class="active"</c:if>>
								<spring:message code="navigation.dashboard.group" />
							</a>
						</li>
					</sec:authorize>
					<sec:authorize access="hasAnyRole('Pulse','AccountAdministrator')">
                    <li class="last">
                        <a href="/sp/pulse" <c:if test="${fn:containsIgnoreCase(requestUrl, 'pulse')}">class="active"</c:if>>
                                <spring:message code="navigation.dashboard.pulse" />
						</a>
                    </li>
                    </sec:authorize>
					<sec:authorize access="hasRole('Spectrum')">
						<li class="last"><a href="#"
							<c:if test="${fn:containsIgnoreCase(requestUrl, 'dashboard')}">class="active"</c:if>><spring:message
									code="navigation.dashboard.spectrum" /></a></li>
					</sec:authorize>
				</ul>
			</div>
		</div>
	</div>
</div>
<!-- Inner Navigation Ends -->