<%@ page session="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
		<div id="content-container">
			<div id="">
			
		
			<c:if test="${not empty message}">
			<div class="${message.type.cssClass}">${message.text}</div>
			</c:if>
	
			<!-- Sign Up Template / Partial Starts Here -->
			<div class="x-container sign-up-wrapper" ng-controller="signUpController">
				<div class="container">
					<div class="row">
						<div class="col-md-7">
							<h2>Sign Up</h2>
							<p>
								Lorem ipsum dolor sit amet
							</p>
					  		<s:bind path="*">
					  		<c:choose>
					  		<c:when test="${status.error}">
					  			<div class="error">Unable to sign up. Please fix the errors below and resubmit.</div>
					  		</c:when>
					  		</c:choose>			
					  		</s:bind>
							
							<form name="spSignUp" ng-submit="processSignUp(signup)" novalidate>
								<p class="float">
									<label for="first_name">First Name</label>
									<input type="text" name="firstName" placeholder="First Name" ng-model="signup.firstName" required/>
									<span class="err-msg" ng-show="spSignUp.submitted && spSignUp.firstName.$error.required"><span class="icon">!</span> This field is required</span>
								</p>
								<p class="float right">
									<label for="last_name">Last Name</label>
									<input type="text" name="lastName" placeholder="Last Name" ng-model="signup.lastName" required/>
									<span class="err-msg" ng-show="spSignUp.submitted && spSignUp.lastName.$error.required"><span class="icon">!</span> This field is required</span>
								</p>
								<div class="clearfix"></div>
								<p class="clear match-width">
									<label for="title">Title</label>
									<input type="text" name="title" placeholder="Title (Optional)" ng-model="signup.title" />
								</p>
								<div class="sperator"></div>

								<p class="float">
									<label for="email">E-mail address</label>
									<input type="email" name="email" placeholder="E-mail address" ng-model="signup.email" required ng-pattern="emailRegex" />
									<span class="err-msg" ng-show="spSignUp.submitted && spSignUp.email.$error.required"><span class="icon">!</span> This field is required</span>
									<span class="err-msg" ng-show="spSignUp.submitted && spSignUp.email.$error.pattern"><span class="icon">!</span> Only letters, numbers, underscores (_), dots (.) are allowed.</span>
								</p>
								<p class="float right">
									<label for="confirmEmail">Email address</label>
									<input type="confirmEmail" name="confirmEmail" placeholder="Confirm Email address" ng-model="signup.confirmEmail" required confirm-email-with="signup.email" />
									<span class="err-msg" ng-show="spSignUp.submitted && spSignUp.confirmEmail.$error.required"><span class="icon">!</span> This field is required</span>
									<span class="err-msg" ng-show="spSignUp.submitted && spSignUp.confirmEmail.$error.confirmEmailWith">The email address don't match, please try again</span>
								</p>
								<div class="clearfix"></div>
								<p class="clear password">
									<label for="password">Password</label>
									<input type="password" name="password" placeholder="Password" ng-model="signup.password" required ng-hide="pwd.toggle" validate-password/>
									<input type="text" name="passwordText" placeholder="Password" ng-model="signup.password" required ng-show="pwd.toggle" validate-password/>
									<a href="#" class="show" ng-click="showPassword($event)"> <span class="icon" ng-class="(pwd.toggle === true) ? 'on' : ''"></span> <span class="text">Show</span> </a>
									<span class="clearfix"></span>
									<span class="msg">Please use: 8 to 32 characters</span>
									<span class="err-msg-group" ng-hide="pwd.toggle"> <span class="err-msg" ng-show="spSignUp.submitted && spSignUp.password.$error.required"><span class="icon">!</span> This field is required</span> <span class="err-msg" ng-show="(spSignUp.submitted || spSignUp.password.$dirty) && spSignUp.password.$error.validatePassword"><span class="icon">!</span> Password doesn't match the criteria!</span> </span>
									<span class="err-msg-group" ng-show="pwd.toggle"> <span class="err-msg" ng-show="spSignUp.submitted && spSignUp.passwordText.$error.required && pwd.toggle"><span class="icon">!</span> This field is required</span> <span class="err-msg" ng-show="(spSignUp.submitted || spSignUp.passwordText.$dirty) && spSignUp.passwordText.$error.validatePassword"><span class="icon">!</span> Password doesn't match the criteria!</span> </span>
								</p>
								<div class="sperator"></div>

								<p class="float">
									<label for="company">Company</label>
									<input type="text" name="company" placeholder="Company" ng-model="signup.company" required/>
									<span class="err-msg" ng-show="spSignUp.submitted && spSignUp.company.$error.required"><span class="icon">!</span> This field is required</span>
								</p>
								<div class="clearfix"></div>
								<p class="float top top-d select">
									<!--span class="dd-icon"></span-->
									<span class="select-wrapper"> <label for="industry">Industry</label>
										<select name="industry" ng-model="signup.industry" ng-init="signup.industry = 'default'" required ng-class="(spSignUp.industry.$modelValue == 'default') ? 'default' : ''">
											<option value="default">Industry</option>
											<option value="0">Somedata here</option>
											<option value="1">4 words can come here</option>
											<option value="2">cats and rats and elephants or more</option>
										</select> </span>
									<span class="err-msg" ng-show="spSignUp.submitted && (spSignUp.industry.$modelValue == 'default')"><span class="icon">!</span> This field is required</span>
								</p>
								<p class="float top top-d right short emp">
									<label for="noEmp">No of employees</label>
									<input type="text" name="noEmp" placeholder="# of employees" ng-model="signup.noEmp" required ng-pattern="/^[0-9\b]{1,6}$/" />
									<span class="err-msg" ng-show="spSignUp.submitted && spSignUp.noEmp.$error.required"><span class="icon">!</span> This field is required</span>
									<span class="err-msg" ng-show="spSignUp.submitted && spSignUp.noEmp.$error.pattern"><span class="icon">!</span> Please enter a valid number!</span>
								</p>
								<div class="sperator"></div>
							<p class="float top top-d left select">
								<!--span class="dd-icon"></span-->
								<span class="select-wrapper"> <label for="country">{{pullInterNationalization('country')}}</label>
<select name="country" ng-model="signup.country" ng-init="signup.country = 'default'" required ng-class="(spSignUp.country.$modelValue == 'default') ? 'default' : ''">
<option value="default">Country</option>										
<option value="Afghanistan">Afghanistan</option>
<option value="Åland Islands">Åland Islands</option>
<option value="Albania">Albania</option>
<option value="Algeria">Algeria</option>
<option value="American Samoa">American Samoa</option>
<option value="Andorra">Andorra</option>
<option value="Angola">Angola</option>
<option value="Anguilla">Anguilla</option>
<option value="Antarctica">Antarctica</option>
<option value="Antigua and Barbuda">Antigua and Barbuda</option>
<option value="Argentina">Argentina</option>
<option value="Armenia">Armenia</option>
<option value="Aruba">Aruba</option>
<option value="Australia">Australia</option>
<option value="Austria">Austria</option>
<option value="Azerbaijan">Azerbaijan</option>
<option value="Bahamas">Bahamas</option>
<option value="Bahrain">Bahrain</option>
<option value="Bangladesh">Bangladesh</option>
<option value="Barbados">Barbados</option>
<option value="Belarus">Belarus</option>
<option value="Belgium">Belgium</option>
<option value="Belize">Belize</option>
<option value="Benin">Benin</option>
<option value="Bermuda">Bermuda</option>
<option value="Bhutan">Bhutan</option>
<option value="Bolivia">Bolivia</option>
<option value="Bosnia and Herzegovina">Bosnia and Herzegovina</option>
<option value="Botswana">Botswana</option>
<option value="Bouvet Island">Bouvet Island</option>
<option value="Brazil">Brazil</option>
<option value="British Indian Ocean Territory">British Indian Ocean Territory</option>
<option value="Brunei Darussalam">Brunei Darussalam</option>
<option value="Bulgaria">Bulgaria</option>
<option value="Burkina Faso">Burkina Faso</option>
<option value="Burundi">Burundi</option>
<option value="Cambodia">Cambodia</option>
<option value="Cameroon">Cameroon</option>
<option value="Canada">Canada</option>
<option value="Cape Verde">Cape Verde</option>
<option value="Cayman Islands">Cayman Islands</option>
<option value="Central African Republic">Central African Republic</option>
<option value="Chad">Chad</option>
<option value="Chile">Chile</option>
<option value="China">China</option>
<option value="Christmas Island">Christmas Island</option>
<option value="Cocos (Keeling) Islands">Cocos (Keeling) Islands</option>
<option value="Colombia">Colombia</option>
<option value="Comoros">Comoros</option>
<option value="Congo">Congo</option>
<option value="Congo, The Democratic Republic of The">Congo, The Democratic Republic of The</option>
<option value="Cook Islands">Cook Islands</option>
<option value="Costa Rica">Costa Rica</option>
<option value="Cote D'ivoire">Cote D'ivoire</option>
<option value="Croatia">Croatia</option>
<option value="Cuba">Cuba</option>
<option value="Cyprus">Cyprus</option>
<option value="Czech Republic">Czech Republic</option>
<option value="Denmark">Denmark</option>
<option value="Djibouti">Djibouti</option>
<option value="Dominica">Dominica</option>
<option value="Dominican Republic">Dominican Republic</option>
<option value="Ecuador">Ecuador</option>
<option value="Egypt">Egypt</option>
<option value="El Salvador">El Salvador</option>
<option value="Equatorial Guinea">Equatorial Guinea</option>
<option value="Eritrea">Eritrea</option>
<option value="Estonia">Estonia</option>
<option value="Ethiopia">Ethiopia</option>
<option value="Falkland Islands (Malvinas)">Falkland Islands (Malvinas)</option>
<option value="Faroe Islands">Faroe Islands</option>
<option value="Fiji">Fiji</option>
<option value="Finland">Finland</option>
<option value="France">France</option>
<option value="French Guiana">French Guiana</option>
<option value="French Polynesia">French Polynesia</option>
<option value="French Southern Territories">French Southern Territories</option>
<option value="Gabon">Gabon</option>
<option value="Gambia">Gambia</option>
<option value="Georgia">Georgia</option>
<option value="Germany">Germany</option>
<option value="Ghana">Ghana</option>
<option value="Gibraltar">Gibraltar</option>
<option value="Greece">Greece</option>
<option value="Greenland">Greenland</option>
<option value="Grenada">Grenada</option>
<option value="Guadeloupe">Guadeloupe</option>
<option value="Guam">Guam</option>
<option value="Guatemala">Guatemala</option>
<option value="Guernsey">Guernsey</option>
<option value="Guinea">Guinea</option>
<option value="Guinea-bissau">Guinea-bissau</option>
<option value="Guyana">Guyana</option>
<option value="Haiti">Haiti</option>
<option value="Heard Island and Mcdonald Islands">Heard Island and Mcdonald Islands</option>
<option value="Holy See (Vatican City State)">Holy See (Vatican City State)</option>
<option value="Honduras">Honduras</option>
<option value="Hong Kong">Hong Kong</option>
<option value="Hungary">Hungary</option>
<option value="Iceland">Iceland</option>
<option value="India">India</option>
<option value="Indonesia">Indonesia</option>
<option value="Iran, Islamic Republic of">Iran, Islamic Republic of</option>
<option value="Iraq">Iraq</option>
<option value="Ireland">Ireland</option>
<option value="Isle of Man">Isle of Man</option>
<option value="Israel">Israel</option>
<option value="Italy">Italy</option>
<option value="Jamaica">Jamaica</option>
<option value="Japan">Japan</option>
<option value="Jersey">Jersey</option>
<option value="Jordan">Jordan</option>
<option value="Kazakhstan">Kazakhstan</option>
<option value="Kenya">Kenya</option>
<option value="Kiribati">Kiribati</option>
<option value="Korea, Democratic People's Republic of">Korea, Democratic People's Republic of</option>
<option value="Korea, Republic of">Korea, Republic of</option>
<option value="Kuwait">Kuwait</option>
<option value="Kyrgyzstan">Kyrgyzstan</option>
<option value="Lao People's Democratic Republic">Lao People's Democratic Republic</option>
<option value="Latvia">Latvia</option>
<option value="Lebanon">Lebanon</option>
<option value="Lesotho">Lesotho</option>
<option value="Liberia">Liberia</option>
<option value="Libyan Arab Jamahiriya">Libyan Arab Jamahiriya</option>
<option value="Liechtenstein">Liechtenstein</option>
<option value="Lithuania">Lithuania</option>
<option value="Luxembourg">Luxembourg</option>
<option value="Macao">Macao</option>
<option value="Macedonia, The Former Yugoslav Republic of">Macedonia, The Former Yugoslav Republic of</option>
<option value="Madagascar">Madagascar</option>
<option value="Malawi">Malawi</option>
<option value="Malaysia">Malaysia</option>
<option value="Maldives">Maldives</option>
<option value="Mali">Mali</option>
<option value="Malta">Malta</option>
<option value="Marshall Islands">Marshall Islands</option>
<option value="Martinique">Martinique</option>
<option value="Mauritania">Mauritania</option>
<option value="Mauritius">Mauritius</option>
<option value="Mayotte">Mayotte</option>
<option value="Mexico">Mexico</option>
<option value="Micronesia, Federated States of">Micronesia, Federated States of</option>
<option value="Moldova, Republic of">Moldova, Republic of</option>
<option value="Monaco">Monaco</option>
<option value="Mongolia">Mongolia</option>
<option value="Montenegro">Montenegro</option>
<option value="Montserrat">Montserrat</option>
<option value="Morocco">Morocco</option>
<option value="Mozambique">Mozambique</option>
<option value="Myanmar">Myanmar</option>
<option value="Namibia">Namibia</option>
<option value="Nauru">Nauru</option>
<option value="Nepal">Nepal</option>
<option value="Netherlands">Netherlands</option>
<option value="Netherlands Antilles">Netherlands Antilles</option>
<option value="New Caledonia">New Caledonia</option>
<option value="New Zealand">New Zealand</option>
<option value="Nicaragua">Nicaragua</option>
<option value="Niger">Niger</option>
<option value="Nigeria">Nigeria</option>
<option value="Niue">Niue</option>
<option value="Norfolk Island">Norfolk Island</option>
<option value="Northern Mariana Islands">Northern Mariana Islands</option>
<option value="Norway">Norway</option>
<option value="Oman">Oman</option>
<option value="Pakistan">Pakistan</option>
<option value="Palau">Palau</option>
<option value="Palestinian Territory, Occupied">Palestinian Territory, Occupied</option>
<option value="Panama">Panama</option>
<option value="Papua New Guinea">Papua New Guinea</option>
<option value="Paraguay">Paraguay</option>
<option value="Peru">Peru</option>
<option value="Philippines">Philippines</option>
<option value="Pitcairn">Pitcairn</option>
<option value="Poland">Poland</option>
<option value="Portugal">Portugal</option>
<option value="Puerto Rico">Puerto Rico</option>
<option value="Qatar">Qatar</option>
<option value="Reunion">Reunion</option>
<option value="Romania">Romania</option>
<option value="Russian Federation">Russian Federation</option>
<option value="Rwanda">Rwanda</option>
<option value="Saint Helena">Saint Helena</option>
<option value="Saint Kitts and Nevis">Saint Kitts and Nevis</option>
<option value="Saint Lucia">Saint Lucia</option>
<option value="Saint Pierre and Miquelon">Saint Pierre and Miquelon</option>
<option value="Saint Vincent and The Grenadines">Saint Vincent and The Grenadines</option>
<option value="Samoa">Samoa</option>
<option value="San Marino">San Marino</option>
<option value="Sao Tome and Principe">Sao Tome and Principe</option>
<option value="Saudi Arabia">Saudi Arabia</option>
<option value="Senegal">Senegal</option>
<option value="Serbia">Serbia</option>
<option value="Seychelles">Seychelles</option>
<option value="Sierra Leone">Sierra Leone</option>
<option value="Singapore">Singapore</option>
<option value="Slovakia">Slovakia</option>
<option value="Slovenia">Slovenia</option>
<option value="Solomon Islands">Solomon Islands</option>
<option value="Somalia">Somalia</option>
<option value="South Africa">South Africa</option>
<option value="South Georgia and The South Sandwich Islands">South Georgia and The South Sandwich Islands</option>
<option value="Spain">Spain</option>
<option value="Sri Lanka">Sri Lanka</option>
<option value="Sudan">Sudan</option>
<option value="Suriname">Suriname</option>
<option value="Svalbard and Jan Mayen">Svalbard and Jan Mayen</option>
<option value="Swaziland">Swaziland</option>
<option value="Sweden">Sweden</option>
<option value="Switzerland">Switzerland</option>
<option value="Syrian Arab Republic">Syrian Arab Republic</option>
<option value="Taiwan, Province of China">Taiwan, Province of China</option>
<option value="Tajikistan">Tajikistan</option>
<option value="Tanzania, United Republic of">Tanzania, United Republic of</option>
<option value="Thailand">Thailand</option>
<option value="Timor-leste">Timor-leste</option>
<option value="Togo">Togo</option>
<option value="Tokelau">Tokelau</option>
<option value="Tonga">Tonga</option>
<option value="Trinidad and Tobago">Trinidad and Tobago</option>
<option value="Tunisia">Tunisia</option>
<option value="Turkey">Turkey</option>
<option value="Turkmenistan">Turkmenistan</option>
<option value="Turks and Caicos Islands">Turks and Caicos Islands</option>
<option value="Tuvalu">Tuvalu</option>
<option value="Uganda">Uganda</option>
<option value="Ukraine">Ukraine</option>
<option value="United Arab Emirates">United Arab Emirates</option>
<option value="United Kingdom">United Kingdom</option>
<option value="United States">United States</option>
<option value="United States Minor Outlying Islands">United States Minor Outlying Islands</option>
<option value="Uruguay">Uruguay</option>
<option value="Uzbekistan">Uzbekistan</option>
<option value="Vanuatu">Vanuatu</option>
<option value="Venezuela">Venezuela</option>
<option value="Viet Nam">Viet Nam</option>
<option value="Virgin Islands, British">Virgin Islands, British</option>
<option value="Virgin Islands, U.S.">Virgin Islands, U.S.</option>
<option value="Wallis and Futuna">Wallis and Futuna</option>
<option value="Western Sahara">Western Sahara</option>
<option value="Yemen">Yemen</option>
<option value="Zambia">Zambia</option>
<option value="Zimbabwe">Zimbabwe</option>								
										</select>
								</span> <span class="err-msg"
									ng-show="spSignUp.submitted && (spSignUp.country.$modelValue == 'default')"><span
									class="icon">!</span> This field is required</span>
							</p>								
								<div class="clearfix"></div>
								<p class="float top">
									<label for="address1">Address 1</label>
									<input type="text" name="address1" placeholder="Address 1" ng-model="signup.address1" required/>
									<span class="err-msg" ng-show="spSignUp.submitted && spSignUp.address1.$error.required"><span class="icon">!</span> This field is required</span>
								</p>
								<p class="float top top-d right">
									<label for="address2">Address 2</label>
									<input type="text" name="address2" placeholder="Address 2 (Optional)" ng-model="signup.address2" />
									<span class="err-msg" ng-show="spSignUp.submitted && spSignUp.address2.$error.required"><span class="icon">!</span> This field is required</span>
								</p>
								<div class="clearfix"></div>
								<p class="float top">
									<label for="city">City</label>
									<input type="text" name="city" placeholder="City" ng-model="signup.city" required/>
									<span class="err-msg" ng-show="spSignUp.submitted && spSignUp.city.$error.required"><span class="icon">!</span> This field is required</span>
								</p>
								<p class="float top top-d right select short">
									<span class="select-wrapper"> <!--span class="dd-icon"></span--> <label for="state">State</label>
										<select name="state" ng-model="signup.state" ng-init="signup.state = 'default'" required ng-class="(spSignUp.state.$modelValue == 'default') ? 'default' : ''">
											<option value="default">State</option>
											<option value="0">Illonois</option>
											<option value="1">Minnesota</option>
										</select> </span>
									<span class="err-msg" ng-show="spSignUp.submitted && (spSignUp.state.$modelValue == 'default')"><span class="icon">!</span> This field is required</span>
								</p>
								<div class="clearfix"></div>
								<p class="float top top-d short">
									<label for="zip">Zip Code</label>
									<input type="text" name="zip" placeholder="Zip Code" ng-model="signup.zip" required ng-pattern="/(^\d{5}$)|(^\d{5}-\d{4}$)/"/>
									<span class="err-msg" ng-show="spSignUp.submitted && spSignUp.zip.$error.required"><span class="icon">!</span> This field is required</span>
								</p>
								<div class="sperator top-adjust"></div>
								<p class="float">
									<label for="phone">Phone Number</label>
									<input type="text" name="phone" placeholder="Phone Number (Optional)" ng-model="signup.phone"/>
									<!--span class="err-msg" ng-show="(spSignUp.submitted || spSignUp.phone.$dirty) && spSignUp.phone.$error.required"><span class="icon">!</span> This field is required</span-->
								</p>
								<div class="sperator"></div>
								<div class="sp-btn-wrapper sp-red">
									<div class="sp-btn">
										<input type="submit" value="continue" class="continueBtn" />
									</div>
								</div>
							</form>
						</div>
					</div>
				</div>
			</div>
			<!-- Sign Up Template / Partial Ends -->
		
	</div>
		</div>