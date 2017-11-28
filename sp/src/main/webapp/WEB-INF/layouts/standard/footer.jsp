<%@ page session="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<!-- Footer Starts -->
<div class="x-container footer-container visibilitynone" data-ng-controller="footerController as ctrl" data-ng-if="inviteReady">
		<div data-ng-include="ctrl.getAppropiateTemplate()">
		</div>
</div>
<!-- Footer Ends -->
