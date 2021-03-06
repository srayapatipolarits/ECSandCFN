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
						<a href="/sp/support" <c:if test="${fn:containsIgnoreCase(requestUrl, 'support')}">class="active"</c:if>>CUSTOMER SUPPORT</a>
					</li>
	            </ul>
            </div>
        </div>
    </div>
</div>
<!-- Inner Navigation Ends -->