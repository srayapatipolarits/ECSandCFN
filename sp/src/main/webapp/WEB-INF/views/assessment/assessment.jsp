<%@ page session="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
    
<c:choose>
    <c:when test="${not empty feedbackUserId }">
        <div class="assessment-container0 hide" fix-container data-ng-if="inviteReady" data-ng-class="inviteReady?'show':'hide'" data-module="prismLens">   
    </c:when>
    <c:when test="${not empty hiringUserId }">
        <div class="assessment-container0 hide" fix-container data-ng-if="inviteReady" data-ng-class="inviteReady?'show':'hide'" data-module="candidatesEmployees" data-type="All">  
    </c:when>
    <c:otherwise>
        <div class="assessment-container0 hide" fix-container data-ng-if="inviteReady" data-ng-class="inviteReady?'show':'hide'" data-module="" data-type="">
    </c:otherwise>
</c:choose>
	<assessment url-src="home.html" class="new-assessment-container"></assessment>
</div>