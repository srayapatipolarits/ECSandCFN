<%@ page session="false"%>
<%@ page pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<!DOCTYPE html>
<html lang="${pageContext.request.locale.language}" data-ng-app="spApp">
<head>
    <title>SurePeople</title>
    <meta http-equiv="content-type" content="text/html;charset=utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale = 1.0" />
    <meta name="description" content="SurePeople" />
    <meta name="keywords" content="SurePeople" />
    <!--[if lte IE 8]>
    <script type="text/javascript">
            //Redirecting if the Browser is Less than IE8
           window.location="/sp/resources/html/browser.html";       
    </script>
<![endif]-->
	<link type="text/css" rel="stylesheet" href="<spring:theme code="cssStyle" />" />
	<jsp:include page="/resources/includes/header-includes.jsp"></jsp:include>
    <style type="text/css">
    body {
        margin: 0;
        padding: 0;
        width: 100%;
        -webkit-print-color-adjust: exact;
        color-adjust: exact;
    }
    </style>	
</head>
<body class="notranslate">

	<div class="page-wrapper">
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
	</div>
	
	<!-- Scripts Start -->
	<jsp:include page="/resources/includes/footer-includes.jsp"></jsp:include>
	<!-- Scripts End -->

</body>
</html>