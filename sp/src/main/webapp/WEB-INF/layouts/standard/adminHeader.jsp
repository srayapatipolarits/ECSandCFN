<%@ page session="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>

<c:set var="requestUrl"
	value="${requestScope['javax.servlet.forward.request_uri']}"></c:set>


<sec:authorize
	access="hasAnyRole('Administrator','AccountAdministrator','Demo')">

	<div class="x-container inner-nav profile-inner-nav bg-blue">
		<div class="container">
			<div class="row">
				<div class="col-md-12">
					<ul>
						<c:choose>
							<c:when test="${fn:containsIgnoreCase(requestUrl, 'member')}">
								<li class="first"><a href="/sp/admin/member#member"
									class="active">All Members </a></li>
							</c:when>
							<c:otherwise>
								<li class="first"><a href="/sp/admin/member#member">
										All Members </a></li>
							</c:otherwise>
						</c:choose>

						<c:choose>
							<c:when test="${fn:containsIgnoreCase(requestUrl, 'group')}">
								<li><a href="/sp/admin/group#group" class="active">
										All Groups </a></li>
							</c:when>
							<c:otherwise>
								<li><a href="/sp/admin/group#group"> All Groups </a></li>
							</c:otherwise>
						</c:choose>

						<sec:authorize access="hasAnyRole('AccountAdministrator','Demo')">
							<c:choose>
								<c:when test="${fn:containsIgnoreCase(requestUrl, 'business')}">
									<li><a href="/sp/admin/account/business" class="active">
											Account Details</a></li>
								</c:when>
								<c:otherwise>
									<li><a href="/sp/admin/account/business"> Account Details</a></li>
								</c:otherwise>
							</c:choose>

						</sec:authorize>
					</ul>
				</div>
			</div>
		</div>
	</div>
</sec:authorize>

<sec:authorize access="hasRole('IndividualAccountAdministrator')">
	<div class="x-container inner-nav profile-inner-nav">
		<div class="container">
			<div class="row">
				<div class="col-md-12">
					<ul>
						<li class="first"><a href="/sp/admin/account/individual" class="active">
								Account </a></li>
					</ul>
				</div>
			</div>
		</div>
	</div>
</sec:authorize>
<!-- Header Ends -->
