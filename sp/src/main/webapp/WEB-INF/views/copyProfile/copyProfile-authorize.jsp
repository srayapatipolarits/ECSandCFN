<%@ page session="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<div id="content" fix-container ng-controller="verifyEmailController">
	<div class="x-container">
		<!-- Extended Container -->
		<div class="container" fix-container>
			<!-- Bootstrap Container -->
			<div class="row">
				<!-- Bootstrap Row -->
				<div class="col-md-12 visitorContainer">
					<div class="return-visitor-wrapper">
						<p class="welcome-msg">
							<spring:message code="copyprofile.authorize.title"></spring:message>
						</p>
						<h2><spring:message code="copyprofile.authorize.heading"></spring:message></h2>
						<p><spring:message code="copyprofile.authorize.p1" arguments="${user.email}"></spring:message></p>
						<div class="sp-updated-btns">
						    <a class="btn-block btn-15px sp-btn-color" href="#" ng-click="authorizeCopyProfile()"><spring:message code="manageAccountContent.yes"></spring:message></a>
						    <a class="btn-block btn-15px sp-btn-color" href="/sp/signin"><spring:message code="manageAccountContent.no"></spring:message></a>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>