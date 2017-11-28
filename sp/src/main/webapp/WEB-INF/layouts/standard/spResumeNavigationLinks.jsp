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
                    <sec:authorize access="hasAnyRole('IndividualAccountAdministrator')">
					<li>
						<a href="/sp/spResume" <c:if test="${fn:containsIgnoreCase(requestUrl, 'spResume')}">class="active"</c:if>><spring:message code="navigation.spResume.spResume" /></a>
					</li>
					</sec:authorize>
                </ul>
            </div>
        </div>
    </div>
</div>
<!-- Inner Navigation Ends -->