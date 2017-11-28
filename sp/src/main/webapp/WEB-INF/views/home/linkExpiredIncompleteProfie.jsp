<%@ page session="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<!-- Login Form Starts -->
<div class="x-container form-wrapper login-wrapper"
	ng-controller="loginFormController" fix-container data-ng-if="inviteReady" class="hide" data-ng-class="inviteReady?'show':'hide'">
	<div class="container">
		<div class="row">
			<div class="col-sm-6 col-md-4 "><!-- col-sm-offset-3 col-sm-6 col-md-offset-4 col-md-4 -->
				<form action="/">
				<div class="error-msgs">
					<h2 class="heading"><spring:message code="login.signin.error.sorry" /></h2>
					<p>
						<spring:message code="login.authentication.failed.8" />
					</p>
						<div class="sp-btn">
							<a href="/sp/signin"
						class="btn-block btn-15px loginBtn sp-btn-color" data-ng-bind="::pullInterNationalization('reset.confirm.signin')"></a>
						</div>
					</form>
				</div>
			</div>
		</div>
	</div>
</div>
<!-- Login Form Ends -->