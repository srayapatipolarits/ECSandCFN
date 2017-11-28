<%@ page session="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

			
<!-- Reset Password Starts -->
<div class="x-container form-wrapper" ng-controller="loginFormController" fix-container>
	<div class="container">
		<div class="row">
			<div class="col-md-6"><!-- col-sm-offset-3 col-sm-6 col-md-offset-3 col-md-6 -->
				<div class="error-msgs">
					<h2>Let's get you a new password.</h2>
					<p>
						Send us your email address that is connected to your SurePeople account and we will send you an email with directions to setup a new password.
					</p>
				</div>
				<form name="resetPassword" ng-submit="resetCredentials()" novalidate>
					<p>
						<label for="email">E-mail address</label>
						<input type="email" name="email" placeholder="E-mail address" ng-model="user.email" required ng-pattern="emailRegex" />
						<span ng-show="resetPassword.email.$error.required"><span class="icon">!</span> Please enter your email address</span>
						<span ng-show="resetPassword.email.$error.pattern"><span class="icon">!</span> Only letters, numbers, underscores (_), dots (.) are allowed.</span>
					</p>
					<div class="sp-btn-wrapper sp-red">
						<div class="sp-btn">
							<input type="submit" value="submit" class="loginBtn" />
						</div>
					</div>
				</form>
			</div>
		</div>
	</div>
</div>
<!-- Reset Password Ends -->