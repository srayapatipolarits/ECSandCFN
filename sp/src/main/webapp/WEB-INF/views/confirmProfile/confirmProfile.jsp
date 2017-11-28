<%@ page session="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<!-- External User Login Form Starts -->

<div class="x-container registration-login sign-up-wrapper sign-up-individual-wrapper"
	ng-controller="externalUserFlow"
	<c:if test="${not empty hiringReferences or not empty feedbackFlow}"> ng-init="setOptions()"</c:if>>
	<div class="container" fix-container>
		<div class="sign-up-product">
		                    <h2 class="sign-in-title sp-header-title-color no-padding-bottom"><spring:message code="growth.external.title" /></h2>
                    <!--p class="form-pagination" class="indi_head">
                        <spring:message code="growth.external.description" />
                    </p-->
				<form name="spExtLogin" ng-submit="initExternalLogin(spExtLogin)" novalidate>
				<ol class="form-list-c form-register-account">					
					<li>
						<spring:message code="home.firstName" var="firstNameLabel" />
						<label for="first_name">${firstNameLabel}</label>
						<spring:bind path="firstName">
							<input type="text" name="firstName" value="${ firstName}" required />
						</spring:bind>
						<span class="err-msg validate-error" ng-show="spExtLogin.submitted && spExtLogin.firstName.$error.required" data-ng-bind="::pullInterNationalization('signup.required.error')"></span>
					</li>
					<li>
						<spring:message code="home.lastName" var="lastNameLabel" />
						<label for="last_name">${lastNameLabel}</label>
						<spring:bind path="lastName">
							<input type="text" name="lastName" value="${ lastName}" required />
						</spring:bind>
						<span class="err-msg validate-error" ng-show="spExtLogin.submitted && spExtLogin.lastName.$error.required" data-ng-bind="::pullInterNationalization('signup.required.error')"></span>
					</li>
					<li>
						<spring:message code="home.email" var="emailLabel" />
						<label for="email">${emailLabel}</label>
						<spring:bind path="email">
							<input type="text" name="email" value="${ email}" required  disabled="disabled" />
						</spring:bind>
						<span class="err-msg validate-error" ng-show="spExtLogin.submitted && spExtLogin.email.$error.required" data-ng-bind="::pullInterNationalization('signup.required.error')"></span>
					</li>
					<li>
						<spring:message code="manageAccountContent.linkedInUrl" var="linkedinLabel" />
						<label for="linkedin">${linkedinLabel}</label>
						<spring:bind path="linkedin">
							<input type="text" name="linkedin" value="${ linkedin}" required placeholder="${linkedinLabel }" />
						</spring:bind>
						 
					</li>
				    <li class="accept-terms limitUser" ng-show="!getExceeded">
				      <div class="tnc">
				        <div class="terms-check-input">
				          <input type="checkbox" name="accepted" value="true" ng-model="tnc" required /> 
				          <span class="terms-txt"><spring:message code="growth.external.iagree" /></span>
				        </div>
				        <span class="err-msg validate-error" ng-show="spExtLogin.submitted && spExtLogin.accepted.$error.required"><spring:message code="admin.account.renew.individual.error.tnc" /></span>
				      
				      </div>
				    </li>					
                    <li class="sign-up-product-continue">
                    <spring:message code="signup.continue.button" var="continueLabel" />
                        <input type="submit" value="{{pullInterNationalization('dashboard.groups.submit')}}" class="continueBtn btn-block btn-15px sp-btn-color" />
                    </li>
					</ol>
				</form>
		</div>
	</div>
</div>
<!-- Login Form Ends -->


