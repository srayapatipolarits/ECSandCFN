<%@ page session="false"%>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
        <%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
            <div id="content-container hide" data-ng-if="inviteReady" data-ng-class="inviteReady?'show':'hide'">
                <!-- Sign Up Individudal Template / Partial Starts Here -->
                <div class="x-container registration-login sign-up-wrapper sign-up-individual-wrapper" data-ng-controller="signUpController">
                    <div class="container minimumHeight" fix-container>
                        <div class="row">
                            <div class="sign-up-product">
                                <h2 class="sign-in-title sp-header-title-color"><spring:message code="signup.addmember.welcome"></spring:message></h2>
                                <p class="form-pagination" class="indi_head">
                                    <spring:message code="signup.addmember.heading1" arguments="${companyName}"></spring:message>
                                </p>
                                <form name="addMemberSignUp" data-ng-submit="addmemberSignUpIndividual(signup)" method="post" novalidate autocomplete="off">
                                    <ol class="form-list-c form-register-account">
                                        <li>
                                            <spring:message code="signup.firstName" var="firstNameLabel" />
                                            <label for="first_name">${firstNameLabel}</label>
                                            <spring:bind path="userProfileForm.firstName">
                                                <input autoFocus type="text" name="firstName" data-ng-model="signup.firstName" data-ng-init="signup.firstName='${ userProfileForm.firstName}'" placeholder="${firstNameLabel}" data-ng-value="${ userProfileForm.firstName}" autoFocus required />
                                            </spring:bind>
                                            <span class="err-msg validate-error" data-ng-show="addMemberSignUp.submitted && addMemberSignUp.firstName.$error.required" data-ng-bind="::pullInterNationalization('signup.required.error')"></span>
                                            <span class="err-msg validate-error" data-ng-show="addMemberSignUp.submitted && addMemberSignUp.firstName.$error.pattern" data-ng-bind="::pullInterNationalization('signup.invalid.email')"></span>
                                        </li>
                                        <li>
                                            <spring:message code="signup.lastName" var="lastNameLabel" />
                                            <label for="last_name">${lastNameLabel }</label>
                                            <spring:bind path="userProfileForm.lastName">
                                                <input type="text" name="lastName" data-ng-model="signup.lastName" data-ng-init="signup.lastName='${ userProfileForm.lastName}'" placeholder="${lastNameLabel}" value="${ userProfileForm.lastName}" required />
                                            </spring:bind>
                                            <span class="err-msg validate-error" data-ng-show="addMemberSignUp.submitted && addMemberSignUp.lastName.$error.required" data-ng-bind="::pullInterNationalization('signup.required.error')"></span>
                                            <span class="err-msg validate-error" data-ng-show="addMemberSignUp.submitted && addMemberSignUp.lastName.$error.pattern" data-ng-bind="::pullInterNationalization('signup.invalid.email')"></span>
                                        </li>
                                        <li>
                                            <spring:message code="signup.Title" var="titleLabel" />
                                            <label for="title"> ${titleLabel }</label>
                                            <spring:bind path="userProfileForm.title">
                                                <input type="text" name="title" data-ng-init="signup.title='${ userProfileForm.title}'" placeholder="${titleLabel }" data-ng-model="signup.title" required value="${ userProfileForm.title}" />
                                            </spring:bind>
                                            <span class="err-msg validate-error" data-ng-show="addMemberSignUp.submitted && addMemberSignUp.title.$error.required" data-ng-bind="::pullInterNationalization('signup.required.error')"></span>
                                        </li>
                                        <li>
                                            <spring:message code="signup.email" var="emailLabel" />
                                            <label for="email">${emailLabel }</label>
                                            <spring:bind path="userProfileForm.email">
                                                <input type="hidden" name="email" value="${userProfileForm.email}">
                                                <input type="email" class="disableThis" readonly name="email" data-ng-model="signup.email" placeholder="${emailLabel }" data-ng-init="signup.email='${ userProfileForm.email}'" required data-ng-pattern="emailRegex" value="${ userProfileForm.email}" />
                                            </spring:bind>
                                            <span class="err-msg validate-error" data-ng-show="addMemberSignUp.submitted && addMemberSignUp.email.$error.required" data-ng-bind="::pullInterNationalization('signup.required.error')"></span>
                                            <span class="err-msg validate-error" data-ng-show="addMemberSignUp.submitted && addMemberSignUp.email.$error.pattern">Only letters, numbers, underscores (_), dots (.) are allowed.</span>
                                        </li>
                                        <li class="password">
                                            <spring:message code="signup.password" var="passwordLabel" />
                                            <label for="password">${passwordLabel }</label>
                                            <div class="view-password">
                                                <input id="reg_pwd" type="{{pwd.inputType}}" name="password" placeholder="${passwordLabel }" data-ng-model="signup.password" required data-ng-minlength="8" data-ng-maxlength="14" />
                                                <a href="#" class="password-toggle show border-link" data-ng-click="showPassword($event)">
                                                    <span class="icon" data-ng-class="(pwd.toggle === true) ? 'on' : ''"></span> <span class="mock-link">Show</span>
                                                </a>
                                            </div>
                                            <span class="err-msg validate-error" data-ng-show="addMemberSignUp.submitted && addMemberSignUp.password.$error.required" data-ng-bind="::pullInterNationalization('signup.required.error')"></span>
                                            <span class="err-msg validate-error" data-ng-show="addMemberSignUp.submitted && !addMemberSignUp.password.$error.required && (!pwdFlag || addMemberSignUp.password.$error.minlength || addMemberSignUp.password.$error.maxlength)" data-ng-bind="::pullInterNationalization('resetpassword.err1')"></span>
                                        </li>
                                        <li class="password-rules-wrapper mb45">
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
                                            <h3 class="label"><spring:message code="signup.addmember.dateofbirth" /></h3>
                                            <ul class="dob-cols-3">
                                                <li class="month">
                                                    <div class="select-wrapper">
                                                        <spring:message code="signup.month" var="monthLabel" />
                                                        <label for="month">${monthLabel }</label>
                                                        <select name="month" data-ng-model="signup.month" data-ng-init="signup.month = '${userProfileForm.month==0?'default':userProfileForm.month}'" required data-ng-class="(addMemberSignUp.month.$modelValue == 'default') ? 'default' : ''">
                                                            <option value="default" data-ng-bind="::pullInterNationalization('home.month')"></option>
                                                            <option value="1" selected data-ng-bind="::pullInterNationalization('sysadmin.organization.month.1')"></option>
                                                            <option value="2" data-ng-bind="::pullInterNationalization('sysadmin.organization.month.2')"></option>
                                                            <option value="3" data-ng-bind="::pullInterNationalization('sysadmin.organization.month.3')"></option>
                                                            <option value="4" data-ng-bind="::pullInterNationalization('sysadmin.organization.month.4')"></option>
                                                            <option value="5" data-ng-bind="::pullInterNationalization('sysadmin.organization.month.5')"></option>
                                                            <option value="6" data-ng-bind="::pullInterNationalization('sysadmin.organization.month.6')"></option>
                                                            <option value="7" data-ng-bind="::pullInterNationalization('sysadmin.organization.month.7')"></option>
                                                            <option value="8" data-ng-bind="::pullInterNationalization('sysadmin.organization.month.8')"></option>
                                                            <option value="9" data-ng-bind="::pullInterNationalization('sysadmin.organization.month.9')"></option>
                                                            <option value="10" data-ng-bind="::pullInterNationalization('sysadmin.organization.month.10')"></option>
                                                            <option value="11" data-ng-bind="::pullInterNationalization('sysadmin.organization.month.11')"></option>
                                                            <option value="12" data-ng-bind="::pullInterNationalization('sysadmin.organization.month.12')"></option>
                                                        </select>
                                                    </div>
                                                    <span class="err-msg validate-error" data-ng-show="addMemberSignUp.submitted && (addMemberSignUp.month.$modelValue == 'default')" data-ng-bind="::pullInterNationalization('signup.required.error')"></span>
                                                </li>
                                                <li class="day">
                                                    <div class="select-wrapper">
                                                        <spring:message code="signup.day" var="dayLabel" />
                                                        <label for="day">${dayLabel }</label>
                                                        <select name="day" data-ng-model="signup.day" data-ng-init="signup.day = '${userProfileForm.day==0?'default':userProfileForm.day}'" required data-ng-class="(addMemberSignUp.day.$modelValue == 'default') ? 'default' : ''">
                                                            <option value="default">${dayLabel }</option>
                                                            <option value="1">1</option>
                                                            <option value="2">2</option>
                                                            <option value="3">3</option>
                                                            <option value="4">4</option>
                                                            <option value="5">5</option>
                                                            <option value="6">6</option>
                                                            <option value="7">7</option>
                                                            <option value="8">8</option>
                                                            <option value="9">9</option>
                                                            <option value="10">10</option>
                                                            <option value="11">11</option>
                                                            <option value="12">12</option>
                                                            <option value="13">13</option>
                                                            <option value="14">14</option>
                                                            <option value="15">15</option>
                                                            <option value="16">16</option>
                                                            <option value="17">17</option>
                                                            <option value="18">18</option>
                                                            <option value="19">19</option>
                                                            <option value="20">20</option>
                                                            <option value="21">21</option>
                                                            <option value="22">22</option>
                                                            <option value="23">23</option>
                                                            <option value="24">24</option>
                                                            <option value="25">25</option>
                                                            <option value="26">26</option>
                                                            <option value="27">27</option>
                                                            <option value="28">28</option>
                                                            <option value="29">29</option>
                                                            <option value="30">30</option>
                                                            <option value="31">31</option>
                                                        </select>
                                                    </div>
                                                    <span class="err-msg validate-error" data-ng-show="addMemberSignUp.submitted && (addMemberSignUp.day.$modelValue == 'default')" data-ng-bind="::pullInterNationalization('signup.required.error')"></span>
                                                </li>
                                                <li class="year">
                                                    <div class="select-wrapper">
                                                        <spring:message code="signup.year" var="yearLabel" />
                                                        <label for="year">${yearLabel}</label>
                                                        <select name="year" data-ng-model="signup.year" data-ng-init="signup.year = '${userProfileForm.year==0?'default':userProfileForm.year}'" required data-ng-class="(addMemberSignUp.year.$modelValue == 'default') ? 'default' : ''">
                                                            <option value="default">${yearLabel}</option>
                                                            <option value="1998">1998</option>
                                                            <option value="1997">1997</option>
                                                            <option value="1996">1996</option>
                                                            <option value="1995">1995</option>
                                                            <option value="1994">1994</option>
                                                            <option value="1993">1993</option>
                                                            <option value="1992">1992</option>
                                                            <option value="1991">1991</option>
                                                            <option value="1990">1990</option>
                                                            <option value="1989">1989</option>
                                                            <option value="1988">1988</option>
                                                            <option value="1987">1987</option>
                                                            <option value="1986">1986</option>
                                                            <option value="1985">1985</option>
                                                            <option value="1984">1984</option>
                                                            <option value="1983">1983</option>
                                                            <option value="1982">1982</option>
                                                            <option value="1981">1981</option>
                                                            <option value="1980">1980</option>
                                                            <option value="1979">1979</option>
                                                            <option value="1978">1978</option>
                                                            <option value="1977">1977</option>
                                                            <option value="1976">1976</option>
                                                            <option value="1975">1975</option>
                                                            <option value="1974">1974</option>
                                                            <option value="1973">1973</option>
                                                            <option value="1972">1972</option>
                                                            <option value="1971">1971</option>
                                                            <option value="1970">1970</option>
                                                            <option value="1969">1969</option>
                                                            <option value="1968">1968</option>
                                                            <option value="1967">1967</option>
                                                            <option value="1966">1966</option>
                                                            <option value="1965">1965</option>
                                                            <option value="1964">1964</option>
                                                            <option value="1963">1963</option>
                                                            <option value="1962">1962</option>
                                                            <option value="1961">1961</option>
                                                            <option value="1960">1960</option>
                                                            <option value="1959">1959</option>
                                                            <option value="1958">1958</option>
                                                            <option value="1957">1957</option>
                                                            <option value="1956">1956</option>
                                                            <option value="1955">1955</option>
                                                            <option value="1954">1954</option>
                                                            <option value="1953">1953</option>
                                                            <option value="1952">1952</option>
                                                            <option value="1951">1951</option>
                                                            <option value="1950">1950</option>
                                                            <option value="1949">1949</option>
                                                            <option value="1948">1948</option>
                                                            <option value="1947">1947</option>
                                                            <option value="1946">1946</option>
                                                            <option value="1945">1945</option>
                                                            <option value="1944">1944</option>
                                                            <option value="1943">1943</option>
                                                            <option value="1942">1942</option>
                                                            <option value="1941">1941</option>
                                                            <option value="1940">1940</option>
                                                            <option value="1939">1939</option>
                                                            <option value="1938">1938</option>
                                                            <option value="1937">1937</option>
                                                            <option value="1936">1936</option>
                                                            <option value="1935">1935</option>
                                                            <option value="1934">1934</option>
                                                            <option value="1933">1933</option>
                                                            <option value="1932">1932</option>
                                                            <option value="1931">1931</option>
                                                            <option value="1930">1930</option>
                                                            <option value="1929">1929</option>
                                                            <option value="1928">1928</option>
                                                            <option value="1927">1927</option>
                                                            <option value="1926">1926</option>
                                                            <option value="1925">1925</option>
                                                            <option value="1924">1924</option>
                                                            <option value="1923">1923</option>
                                                            <option value="1922">1922</option>
                                                            <option value="1921">1921</option>
                                                            <option value="1920">1920</option>
                                                            <option value="1919">1919</option>
                                                            <option value="1918">1918</option>
                                                            <option value="1917">1917</option>
                                                            <option value="1916">1916</option>
                                                            <option value="1915">1915</option>
                                                            <option value="1914">1914</option>
                                                            <option value="1913">1913</option>
                                                            <option value="1912">1912</option>
                                                            <option value="1911">1911</option>
                                                            <option value="1910">1910</option>
                                                            <option value="1909">1909</option>
                                                            <option value="1908">1908</option>
                                                            <option value="1907">1907</option>
                                                            <option value="1906">1906</option>
                                                            <option value="1905">1905</option>
                                                        </select>
                                                    </div>
                                                    <span class="err-msg validate-error" data-ng-show="addMemberSignUp.submitted && (addMemberSignUp.year.$modelValue == 'default')" data-ng-bind="::pullInterNationalization('signup.required.error')"></span>
                                                    <span class="err-msg validate-error" data-ng-show="addMemberSignUp.submitted && ageError" data-ng-bind="::pullInterNationalization('home.ageError')"></span>
                                                </li>
                                            </ul>
                                        </li>
                                        <li class="radio-group-gender">
                                            <h3 class="label" data-ng-bind="::pullInterNationalization('home.gender')"></h3>
                                            <ul class="radio-group-list" data-ng-init="signup.gender = '${ userProfileForm.gender}'">
                                                <li class="radio-btn">
                                                    <input id="gender-male" type="radio" name="gender" data-ng-model="signup.gender" value="M" required />
                                                    <label for="gender">
                                                        <spring:message code="signup.male" />
                                                    </label>
                                                </li>
                                                <li class="radio-btn">
                                                    <input id="gender-female" type="radio" name="gender" data-ng-model="signup.gender" value="F" required />
                                                    <label for="gender-female">
                                                        <spring:message code="home.female" />
                                                    </label>
                                                </li>
                                            </ul>
                                            <p class="radio-group-error-msg">
                                                <span class="err-msg validate-error" data-ng-show="addMemberSignUp.submitted && addMemberSignUp.gender.$error.required" data-ng-bind="::pullInterNationalization('signup.required.error')"></span>
                                            </p>
                                        </li>
                                        <li>
                                            <span class="select-wrapper"> 
                                                <label for="country">{{pullInterNationalization('signup.country')}}</label>
                                                <select name="country" data-ng-model="signup.country" data-ng-init="signup.country='${address.country==null?'default':address.country}'" required data-ng-class="(addMemberSignUp.country.$modelValue == 'default') ? 'default' : ''" data-ng-change="stateCheck.forSelectedCountry=signup.country">
                                                    <option value="default" data-ng-bind="::pullInterNationalization('home.selectCountry')"></option>
                                                    <option value="{{cList.code}}" data-ng-if="cList.code === 'US'" data-ng-repeat="cList in countries" data-ng-bind="cList.name"></option>
                                                    <option value="{{cList.code}}" data-ng-if="cList.code === 'CA'" data-ng-repeat="cList in countries" data-ng-bind="cList.name"></option>
                                                    <option value="default1">--------------------------------------------------</option>
                                                    <option value="{{cList.code}}" data-ng-if="cList.code !== 'US' && cList.code !== 'CA'" data-ng-repeat="cList in countries" data-ng-bind="cList.name"></option>                                               
                                                </select>
                                          </span>
                                          <span class="err-msg validate-error" data-ng-show="addMemberSignUp.submitted && (signup.country == 'default' || signup.country == 'default1')" data-ng-bind="::pullInterNationalization('signup.required.error')"></span>
                                        </li>
                                        <li>
                                            <spring:message code="signup.address1" var="address1Label" />
                                            <label for="address1">${address1Label }</label>
                                            <input type="text" name="address1" placeholder="${address1Label }" data-ng-model="signup.address1" data-ng-init="signup.address1 = '${ address.addressLine1}'" required />
                                            <span class="err-msg validate-error" data-ng-show="addMemberSignUp.submitted && addMemberSignUp.address1.$error.required" data-ng-bind="::pullInterNationalization('signup.required.error')"></span>
                                        </li>
                                        <li class="">
                                            <spring:message code="signup.address2" var="address2Label" />
                                            <label for="address2">${address2Label}</label>
                                            <input type="text" name="address2" placeholder="${address2Label}" data-ng-model="signup.address2" data-ng-init="signup.address2 = '${ address.addressLine2}'" />
                                            <!--span class="err-msg" data-ng-show="addMemberSignUp.submitted && addMemberSignUp.address2.$error.required" data-ng-bind="::pullInterNationalization('signup.required.error')"></span-->
                                        </li>
                                        <li>
                                            <ul class="local-region-cols-3">
                                                <li class="city">
                                                    <spring:message code="signup.city" var="cityLabel" />
                                                    <label for="city">${cityLabel }</label>
                                                    <input type="text" name="city" placeholder="${cityLabel }" data-ng-model="signup.city" data-ng-init="signup.city = '${ address.city}'" required />
                                                    <span class="err-msg validate-error" data-ng-show="addMemberSignUp.submitted && addMemberSignUp.city.$error.required" data-ng-bind="::pullInterNationalization('signup.required.error')"></span>
                                                </li>
                                                <li class="state" data-ng-if="stateCheck.forSelectedCountry!='US' && stateCheck.forSelectedCountry!='CA'" data-ng-init="signup.state=''">
                                                    <input type="text" name="state" data-ng-model="signup.state" data-ng-init="signup.state = '${ address.state}'" placeholder="{{pullInterNationalization('admin.account.member.details.region')}}" />
                                                    <!--span class="err-msg validate-error" data-ng-show="addMemberSignUp.submitted && addMemberSignUp.state.$error.required" data-ng-bind="::pullInterNationalization('signup.required.error')"></span-->
                                                </li>
                                                <li class="state" data-ng-if="stateCheck.forSelectedCountry!='US' && stateCheck.forSelectedCountry=='CA'" data-ng-init="signup.state=''">
                                                    <input type="text" name="state" data-ng-model="signup.state" data-ng-init="signup.state = '${ address.state}'" required placeholder="{{pullInterNationalization('admin.account.profile.details.province')}}" />
                                                    <span class="err-msg validate-error" data-ng-show="addMemberSignUp.submitted && addMemberSignUp.state.$error.required" data-ng-bind="::pullInterNationalization('signup.required.error')"></span>
                                                </li>
                                                <li class="state" data-ng-if="stateCheck.forSelectedCountry=='US'">
                                                    <span class="select-wrapper">
                                                        <label for="state" data-ng-bind="pullInterNationalization('home.selectState')"></label>
                                                        <select name="state" data-ng-model="signup.state"
                                                        data-ng-init="signup.state='${address.state==null?'default':address.state}'" required
                                                        data-ng-class="(addMemberSignUp.state.$modelValue == 'default') ? 'default' : ''">
                                                          <option value="default" data-ng-bind="pullInterNationalization('home.selectState')"></option>
                                                          <option value="AL" label="Alabama">Alabama</option>
                                                          <option value="AK" label="Alaska">Alaska</option>
                                                          <option value="AS" label="American Samoa">American Samoa</option>
                                                          <option value="AZ" label="Arizona">Arizona</option>
                                                          <option value="AR" label="Arkansas">Arkansas</option>
                                                          <option value="AE" label="Armed Forces Middle East">Armed
                                                            Forces Middle East</option>
                                                          <option value="AA" label="Armed Forces Americas (not Canada)">Armed
                                                            Forces Americas (not Canada)</option>
                                                          <option value="AP" label="Armed Forces Pacific">Armed
                                                            Forces Pacific</option>
                                                          <option value="CA" label="California">California</option>
                                                          <option value="CO" label="Colorado">Colorado</option>
                                                          <option value="CT" label="Connecticut">Connecticut</option>
                                                          <option value="DE" label="Delaware">Delaware</option>
                                                          <option value="DC" label="District of Columbia">District of Columbia</option>
                                                          <option value="FL" label="Florida">Florida</option>
                                                          <option value="GA" label="Georgia">Georgia</option>
                                                          <option value="GU" label="Guam">Guam</option>
                                                          <option value="HI" label="Hawaii">Hawaii</option>
                                                          <option value="ID" label="Idaho">Idaho</option>
                                                          <option value="IL" label="Illinois">Illinois</option>
                                                          <option value="IN" label="Indiana">Indiana</option>
                                                          <option value="IA" label="Iowa">Iowa</option>
                                                          <option value="KS" label="Kansas">Kansas</option>
                                                          <option value="KY" label="Kentucky">Kentucky</option>
                                                          <option value="LA" label="Louisiana">Louisiana</option>
                                                          <option value="ME" label="Maine">Maine</option>
                                                          <option value="MD" label="Maryland">Maryland</option>
                                                          <option value="MA" label="Massachusetts">Massachusetts</option>
                                                          <option value="MI" label="Michigan">Michigan</option>
                                                          <option value="MN" label="Minnesota">Minnesota</option>
                                                          <option value="MS" label="Mississippi">Mississippi</option>
                                                          <option value="MO" label="Missouri">Missouri</option>
                                                          <option value="MT" label="Montana">Montana</option>
                                                          <option value="NE" label="Nebraska">Nebraska</option>
                                                          <option value="NV" label="Nevada">Nevada</option>
                                                          <option value="NH" label="New Hampshire">New Hampshire</option>
                                                          <option value="NJ" label="New Jersey">New Jersey</option>
                                                          <option value="NM" label="New Mexico">New Mexico</option>
                                                          <option value="NY" label="New York">New York</option>
                                                          <option value="NC" label="North Carolina">North Carolina</option>
                                                          <option value="ND" label="North Dakota">North Dakota</option>
                                                          <option value="MP" label="Northern Mariana Islands">Northern Mariana Islands</option>
                                                          <option value="OH" label="Ohio">Ohio</option>
                                                          <option value="OK" label="Oklahoma">Oklahoma</option>
                                                          <option value="OR" label="Oregon">Oregon</option>
                                                          <option value="PA" label="Pennsylvania">Pennsylvania</option>
                                                          <option value="PR" label="Puerto Rico">Puerto Rico</option>
                                                          <option value="RI" label="Rhode Island">Rhode Island</option>
                                                          <option value="SC" label="South Carolina">South Carolina</option>
                                                          <option value="SD" label="South Dakota">South Dakota</option>
                                                          <option value="TN" label="Tennessee">Tennessee</option>
                                                          <option value="TX" label="Texas">Texas</option>
                                                          <option value="UT" label="Utah">Utah</option>
                                                          <option value="VT" label="Vermont">Vermont</option>
                                                          <option value="VI" label="Virgin Islands">Virgin Islands</option>
                                                          <option value="VA" label="Virginia">Virginia</option>
                                                          <option value="WA" label="Washington">Washington</option>
                                                          <option value="WV" label="West Virginia">West Virginia</option>
                                                          <option value="WI" label="Wisconsin">Wisconsin</option>
                                                          <option value="WY" label="Wyoming">Wyoming</option>
                                                        </select>
                                                      </span>
                                                    <span class="err-msg validate-error" data-ng-show="addMemberSignUp.submitted && (addMemberSignUp.state.$modelValue == 'default')" data-ng-bind="::pullInterNationalization('signup.required.error')"></span>
                                                </li>
                                                <li class="postal-code">
                                                    <spring:message code="signup.zipcode" var="zipLabel" />
                                                    <label for="zip">${zipLabel}</label>
                                                    <input type="text" name="zip" placeholder="${zipLabel}" data-ng-model="signup.zip" data-ng-init="signup.zip='${address.zipCode}'" required data-ng-pattern="/(^\d{5}$)|(^\d{5}-\d{4}$)/" data-ng-if="stateCheck.forSelectedCountry=='US'" />
                                                    <input type="text" name="zip" placeholder="{{pullInterNationalization('home.postalcode')}}" data-ng-model="signup.zip" data-ng-init="signup.zip = '${ address.zipCode}'" required data-ng-if="stateCheck.forSelectedCountry!='US'" />
                                                    <span class="err-msg validate-error" data-ng-show="addMemberSignUp.submitted && addMemberSignUp.zip.$error.required" data-ng-bind="::pullInterNationalization('signup.required.error')"></span>
                                                    <span class="err-msg validate-error" data-ng-show="addMemberSignUp.submitted && addMemberSignUp.zip.$error.pattern && !addMemberSignUp.zip.$error.required" data-ng-bind="::pullInterNationalization('signup.invalid.email')"></span>
                                                </li>
                                            </ul>
                                        </li>
                                        <li>
                                            <spring:message code="signup.phoneNumber" var="phNoLabel" />
                                            <label for="phone">${phNoLabel}</label>
                                            <input type="text" name="phone" placeholder="${phNoLabel}" data-ng-model="signup.phone" data-ng-init="signup.phone = '${ userProfileForm.phone}'" required/>
                                            <span class="err-msg validate-error" data-ng-show="addMemberSignUp.submitted && addMemberSignUp.phone.$error.required" data-ng-bind="::pullInterNationalization('signup.required.error')"></span>
                                            <span class="err-msg validate-error" data-ng-show="addMemberSignUp.submitted && addMemberSignUp.phone.$error.pattern" data-ng-bind="::pullInterNationalization('signup.invalid.email')"></span>
                                        </li>
                                        <li data-ng-show="sfailure">
                                            <span class="err-msg validate-error" data-ng-repeat="(key, value) in failure">{{key}} {{value}}</span>
                                        </li>
                                        <li class="sign-up-product-continue">
                                            <spring:message code="signup.continue.button" var="continueLabel" />
                                            <input type="submit" value="${continueLabel }" class="continueBtn btn-block btn-15px sp-btn-color" />
                                        </li>
                                    </ol>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
            </div>