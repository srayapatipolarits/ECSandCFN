<%@ page session="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
	<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>	
<!-- Header Starts -->

<div class="x-container header-wrapper">

	<header class="container">
		<div class="row">
			<div class="col-xs-8 col-sm-8 col-md-10">
				<a href="/" src="<spring:theme code="logoUrl" />" alt="SurePeople&trade;"/>
				</a>
			</div>
			<sec:authorize ifNotGranted="ROLE_ANONYMOUS">

				<sec:authentication property="principal" var="user" />

				<c:if test="${user.userStatus eq 'VALID' }">

					<!-- Enter assessemetn Assessmenet Completd thing  -->
				</c:if>
				<div class="col-xs-4 col-sm-4 col-md-2">

					<a href="/sp/logout" class="logout m-hide"> <span
						class="assessment">save &amp;</span> <span class="sp360">&#160;logout</span>
					</a>
				</div>

			</sec:authorize>
		</div>
	</header>
</div>
<sec:authorize ifNotGranted="ROLE_ANONYMOUS">

	<sec:authentication property="principal" var="user" />

	<c:if test="${user.userStatus eq 'VALID' }">

		<div class="x-container nav-wrapper">
			<nav class="container">
				<div class="row">
					<div class="col-xs-12">
						<ul>
							<li class="first"><a href="#"> <span class="text">Dashboard</span>
									<span class="_icon"></span>
							</a></li>
							<li><a href="#" class="on"> <span class="text">Profile</span>
									<span class="_icon"></span>
							</a></li>
							<li><a href="#"> <span class="text">Feedback</span> <span
									class="_icon"></span>
							</a></li>
							<li><a href="#"> <span class="text">Goals</span> <span
									class="_icon"></span>
							</a></li>
							<li><a href="#"> <span class="text">Training
										library</span> <span class="_icon"></span>
							</a></li>
							<li><a href="#"> <span class="text">Tools</span> <span
									class="_icon"></span>
							</a></li>
							<li class="last"><a href="#"> <span class="text">Growth
										team</span> <span class="_icon"></span>
							</a></li>
						</ul>
					</div>
				</div>
			</nav>
		</div>
	</c:if>
</sec:authorize>
<!-- Header Ends -->
