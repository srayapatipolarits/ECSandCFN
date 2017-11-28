<%@ page session="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
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
						<a href="/sp/growth/growthListing" <c:if test="${fn:containsIgnoreCase(requestUrl, 'growthListing') or fn:containsIgnoreCase(requestUrl, 'archive')}">class="active"</c:if>>GROWTH TEAM</a>
                    </li>
                    <sec:authorize access="!hasRole('IndividualAccountAdministrator')">
                    <li>
                    	<a href="/sp/growth/request" <c:if test="${fn:containsIgnoreCase(requestUrl, 'request')}">class="active"</c:if>>REQUESTS</a>
                    </li>
                    </sec:authorize>
                    <!-- <li>
                    	<a href="/sp/growth/archive" <c:if test="${fn:containsIgnoreCase(requestUrl, 'archive')}">class="active"</c:if>>ARCHIVES</a>
                    </li>-->
                </ul>
            </div>
        </div>
    </div>
</div>
<!-- Inner Navigation Ends -->