<%@ page session="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<!-- Login Form Starts -->
<div class="x-container form-wrapper login-wrapper"
	ng-controller="loginFormController" fix-container>
	<div class="container">
		<div class="row">
			<div class="col-sm-6 col-md-4 "><!-- col-sm-offset-3 col-sm-6 col-md-offset-4 col-md-4 -->
				<form action="/">
				<div class="error-msgs">
					<h2 class="heading"><spring:message code="login.signin.error.sorry" /></h2>
					<p>
						<spring:message code="login.authentication.failed.4" />
					</p>
					<div class="sp-btn-wrapper sp-red">
						<div class="sp-btn">
							<input type="submit" class="loginBtn" value="<spring:message code="form.home"/>">
						</div>
					</div>
					</form>
				</div>
			</div>
		</div>
	</div>
</div>
<!-- Login Form Ends -->