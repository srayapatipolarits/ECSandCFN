<%@ page session="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<!-- Reset Password Starts -->
<div class="x-container registration-login sign-up-wrapper sign-up-individual-wrapper hide" ng-controller="loginFormController" ng-init="setPageType('expiry')" data-ng-if="inviteReady" data-ng-class="inviteReady?'show':'hide'">
    <div class="container" fix-container>
        <div class="row">
            <div>
                <h2 class="sign-in-title sp-header-title-color" data-ng-bind="pullInterNationalization('resetpassword.title')"></h2>
                <p class="sign-in-instruction" data-ng-bind="pullInterNationalization('resetpassword.desc')"></p>
                <form name="resetPwdForm" class="reset-pwd-form" novalidate autocomplete="off">
                    <ol class="form-list-c">
                        <li class="password-rules-wrapper profile-pop rulesWS">
                            <p class="pass-rset">
                                <span data-ng-bind="::pullInterNationalization('home.passwordRules.heading1')"></span>
                                <span class="fw700" data-ng-bind="::pullInterNationalization('home.passwordRules.chars')"></span>
                            </p>
                            <p class="pass-rset rsmTop">
                                <span data-ng-bind="::pullInterNationalization('home.passwordRules.heading2')"></span>
                                <span class="fw700" data-ng-bind="::pullInterNationalization('home.passwordRules.lowerCase')"></span>
                                <span class="fw700" data-ng-bind="::pullInterNationalization('home.passwordRules.upperCase')"></span>
                                <span class="fw700" data-ng-bind="::pullInterNationalization('home.passwordRules.num')"></span>
                                <span><span class="fw700 flt dinl" data-ng-bind="::pullInterNationalization('home.passwordRules.spl')"></span> <span class="flt mlf5 dinl" data-ng-bind="::pullInterNationalization('home.passwordRules.splChars')"></span></span>
                            </p>
                        </li>
                        <li class="hideIt">
                        	<input type="text" name="email" ng-model="resetPwd.email" readonly value="${email}" ng-init="resetPwd.email='${email}'" />
                        </li>
                        <li>
                            <label for="oldPassword" data-ng-bind="::pullInterNationalization('resetpassword.label1')"></label>
                            <input class="pwdField" autocomplete="off" id="oldPassword" type="password" name="oldPassword" ng-model="resetPwd.oldPassword" required />
                            <span class="validate-error" ng-show="resetPwdForm.validationErrors && resetPwdForm.oldPassword.$error.required" data-ng-bind="::pullInterNationalization('signup.required.error')"></span>
                        </li>
                        <li>
                            <label for="newPassword" data-ng-bind="::pullInterNationalization('resetpassword.label2')"></label>
                            <input class="pwdField" id="changeNewPwd" autocomplete="off" id="newPassword" type="password" name="newPassword" ng-model="resetPwd.newPassword" validation-check="resetPwd.oldPassword != resetPwd.newPassword" required ng-minlength="8" ng-maxlength="14" />
                            <span class="validate-error" ng-show="resetPwdForm.validationErrors && resetPwdForm.newPassword.$error.required" data-ng-bind="::pullInterNationalization('signup.required.error')"></span>
                            <span class="validate-error" ng-show="resetPwdForm.validationErrors && !resetPwdForm.newPassword.$error.required && !resetPwdForm.oldPassword.$error.required && (resetPwd.oldPassword === resetPwd.newPassword)" data-ng-bind="::pullInterNationalization('resetpassword.err3')"></span>
                            <span class="validate-error" ng-show="resetPwdForm.validationErrors && !resetPwdForm.newPassword.$error.required && (resetPwd.oldPassword !== resetPwd.newPassword) && (!pwdFlag || resetPwdForm.newPassword.$error.minlength || resetPwdForm.newPassword.$error.maxlength)" data-ng-bind="::pullInterNationalization('resetpassword.err1')"></span>
                        </li>
                        <li class="password-rules-wrapper rulesSS">
                            <p class="pass-rset">
                                <span data-ng-bind="::pullInterNationalization('home.passwordRules.heading1')"></span>
                                <span class="fw700" data-ng-bind="::pullInterNationalization('home.passwordRules.chars')"></span>
                            </p>
                            <p class="pass-rset rsmTop">
                                <span data-ng-bind="::pullInterNationalization('home.passwordRules.heading2')"></span>
                                <span class="fw700" data-ng-bind="::pullInterNationalization('home.passwordRules.lowerCase')"></span>
                                <span class="fw700" data-ng-bind="::pullInterNationalization('home.passwordRules.upperCase')"></span>
                                <span class="fw700" data-ng-bind="::pullInterNationalization('home.passwordRules.num')"></span>
                                <span><span class="fw700 flt dinl" data-ng-bind="::pullInterNationalization('home.passwordRules.spl')"></span> <span class="flt mlf5 dinl" data-ng-bind="::pullInterNationalization('home.passwordRules.splChars')"></span></span>
                            </p>
                        </li>                        
                        <li>
                            <label for="repeatNewPassword" data-ng-bind="::pullInterNationalization('resetpassword.label3')"></label>
                            <input class="pwdField" autocomplete="off" id="repeatNewPassword" type="password" name="repeatNewPassword" ng-model="resetPwd.repeatNewPassword" required same-as="resetPwd.newPassword" />
                            <span class="validate-error" ng-show="resetPwdForm.validationErrors && resetPwdForm.repeatNewPassword.$error.required" data-ng-bind="::pullInterNationalization('signup.required.error')"></span>
                            <span class="validate-error" ng-show="resetPwdForm.validationErrors && !resetPwdForm.repeatNewPassword.$error.required && !resetPwdForm.newPassword.$invalid && resetPwdForm.repeatNewPassword.$invalid" data-ng-bind="::pullInterNationalization('resetpassword.err2')"></span>
                            <span class="validate-error" ng-show="resetPwdError" data-ng-bind="resetPwdErrorMsg"></span>
                        </li>
                        <li class="submit-cancel-btns">
                            <ul class="submit-cancel-list">
                                <li class="submit-charge">
                                    <input type="submit" value="{{::pullInterNationalization('admin.member.submit')}}" ng-click="processPasswordReset(resetPwd)" class="btn-block btn-15px sp-btn-color" />
                                </li>
                            </ul>
                        </li>
                    </ol>
                </form>
            </div>
        </div>
    </div>
</div>
<!-- Reset Password Ends -->
