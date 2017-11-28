<%@ page session="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<!-- Login Form Starts -->
<div class="x-container form-wrapper login-wrapper" ng-controller="loginFormController" fix-container>
	<div class="container">
		<div class="row">
			<div class="sign-in-default">
				<c:if test="${empty login_error}">
					<div class="error-msgs no-error" ng-init="showErrors=true">
						<h2 class="sign-in-title sp-header-title-color"><spring:message code="login.signin.account" /></h2>
            <c:if test="${not empty login_message}">
              <p class="login-error-msg">${login_message}</p>
            </c:if>
					</div>
				</c:if>
				
				<c:if test="${not empty login_error}">
					<div class="error-msgs" ng-init="showErrors=false;login.password=null;login.email=null">
						<h2 class="sign-in-title sp-header-title-color"><spring:message code="login.signin.error.sorry" /></h2>
						<p class="login-error-msg">${login_error}
						</p>
					</div>
				</c:if>
				


				<form name="spLogin" method="post" action="/sp/loginProcess" novalidate>
					<!-- ng-submit="validateCredentials()"  -->
					<ol class="form-list-c form-list-sign-in">

						<li>
							<label for="email"><spring:message code="login.username" /></label>
							<input type="email" 
								autoFocus name="email" 
								ng-init="login.email='${email}'" ng-enter="validateCredentials()" placeholder="<spring:message code="login.username" />"
								ng-model="login.email" required
								ng-pattern="emailRegex" ng-class="(spLogin.submitted && (spLogin.email.$error.required || spLogin.email.$error.pattern || spLogin.password.$error.required))?'err-border':''"/> 
						</li>

						<li>
							<label for="password">
								<spring:message
									code="login.password" /></label> <input ng-enter="validateCredentials()" type="password" name="password"
								placeholder="<spring:message code="login.password" />" ng-model="login.password" required ng-class="(spLogin.submitted && (spLogin.email.$error.required || spLogin.email.$error.pattern || spLogin.password.$error.required))?'err-border':''"/> 
								
							<span class="hide validate-error" ng-class="spLogin.submitted && (spLogin.email.$error.required || spLogin.email.$error.pattern || spLogin.password.$error.required) ? 'show':'hide'"><spring:message code="login.invalid.email" /></span>						
						</li>
						<li>
							<input type="hidden" name="targetParam" data-ng-value="targetParam"/> 
						</li>						
						<li class="remember-me">
							<input type="checkbox" id="rememberMe" name="remember-me" /> 
							<label for="rememberMe">
							<span class="checkedTick icon-icon_dev_strategy_check sp-icon-color"></span>
							<span><spring:message code="login.rememberme" /></span>
							</label> 	
						</li>

						<li class="sign-in-submit">
						        <a href="javascript:void(0)" class="btn-block btn-15px sp-btn-color" ng-click="validateCredentials()"><spring:message code="login.submit" /></a>
								<!--<input type="button" value="<spring:message code="login.submit" />" class="btn-block btn-15px loginBtn sp-btn-color" ng-click="validateCredentials()" />-->
								<!--  ng-disabled="spLogin.$invalid" -->
						</li>

					</ol>

				</form>

				<p class="password-reset">
					<a class="border-link" href="/sp/reset#/reset">
						<%--<spring:message code="login.need.to.reset.link" /> --%>
					    <span class="mock-link"><spring:message code="login.signin.forgetPassword" /></span>
					</a>
				</p>

				<p>
					<spring:message code="login.not.surepeople" />
				</p>	
				<spring:eval expression="@environment.getProperty('base.serverUrl')" var="baseUrl" />
				<p>	<a href="<c:url value="${baseUrl}" />"><spring:message code="login.signin.getstarted" /></a>
				</p>
				<%-- <p>
					<a href="<c:url value="/signup#signup_individual" />"><spring:message
							code="login.signup.individual.link" /></a>
				</p>
				
				<p>
					<a href="<c:url value="/signup#signup_business" />"><spring:message
							code="login.signup.business.link" /></a>
				</p>--%>
			</div>
		</div>
	</div>
</div>
<!-- Login Form Ends -->