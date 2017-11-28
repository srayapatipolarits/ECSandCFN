<%@ page session="false"%>
<%@ page pageEncoding="utf-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<!DOCTYPE html>
<html lang="${pageContext.request.locale.language}" ng-app="spApp">
<head>
<title><tiles:insertAttribute name="title"
		defaultValue="SurePeople" /></title>
<meta http-equiv="content-type" content="text/html;charset=utf-8" />
<meta name="viewport" content="width=device-width, initial-scale = 1.0" />
<meta name="description"
	content="<tiles:insertAttribute name="description" defaultValue="SurePeople" />" />
<meta name="keywords"
	content="<tiles:insertAttribute name="keywords" defaultValue="SurePeople" />" />

<jsp:include page="/resources/includes/header-includes.jsp"></jsp:include>     	
<link type="text/css" rel="stylesheet"
	href="<spring:theme code="cssStyle" />" />
</head>
<body>
	<!-- Google Tag Manager -->
<noscript><iframe src="//www.googletagmanager.com/ns.html?id=GTM-K7DMVG"
height="0" width="0" style="display:none;visibility:hidden"></iframe></noscript>
<script>(function(w,d,s,l,i){w[l]=w[l]||[];w[l].push({'gtm.start':
new Date().getTime(),event:'gtm.js'});var f=d.getElementsByTagName(s)[0],
j=d.createElement(s),dl=l!='dataLayer'?'&l='+l:'';j.async=true;j.src=
'//www.googletagmanager.com/gtm.js?id='+i+dl;f.parentNode.insertBefore(j,f);
})(window,document,'script','dataLayer','GTM-K7DMVG');</script>
<!-- End Google Tag Manager -->
	
	<div class="page-wrapper">
		<div id="header">
			<tiles:insertAttribute name="header" />
		</div>
		
		<div ui-view></div>
		<div id="content-container">
			<div id="content">
				<div id="loader">
					<div class="spinner">
					  <div class="double-bounce1"></div>
					  <div class="double-bounce2"></div>
					</div>	
				</div> 	
				<tiles:insertAttribute name="content" />
			</div>
		</div>
		
		<div id="footer">
			<tiles:insertAttribute name="footer" />
		</div>
	</div>

	<!-- Scripts Start -->
	<jsp:include page="/resources/includes/footer-includes.jsp"></jsp:include>
	<!-- Scripts End -->

</body>
</html>