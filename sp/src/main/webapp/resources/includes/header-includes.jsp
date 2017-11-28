<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>

<tiles:importAttribute name="styles" ignore="true"/>

<!--[if lte IE 9]>
	<script type="text/javascript">
			//Redirecting if the Browser is LEss than IE9
		   window.location="/sp/resources/html/browser.html";		
	</script>
<![endif]-->

<link type="text/css" rel="stylesheet" href="/resources/css/css-min/vendor.min.css" />
<link rel="icon" type="image/x-icon" href="/resources/css/images/sure_people.ico" />
<link type="text/css" rel="stylesheet" href="/resources/css/css-min/global.min.121a33343a920d88851affb705780627.css" />
<c:forEach var="css" items="${styles}">
    <link type="text/css" rel="stylesheet" href="/<c:url value="${css}"/>?release=true" />
</c:forEach>
<script type="text/javascript" src="/resources/js-min/vendor.min.664a6165ba3e3118e03307490eff6621.js"></script>