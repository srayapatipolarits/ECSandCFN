<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>

<tiles:importAttribute name="javascripts" ignore="true"/>
<tiles:importAttribute name="type" ignore="true"/>

<c:choose> 
<c:when test="${type=='pa'}">
	<script type="text/javascript" src="/resources/js-min/commonPA.min.6f3e6db3898b2c64e8afcdcf13562ae7.js"></script>
</c:when> 
<c:otherwise> 
	<script type="text/javascript" src="/resources/js-min/common.min.594f033f85139fa109f3757008775d54.js"></script>
</c:otherwise> 
</c:choose>

<c:forEach var="script" items="${javascripts}">
    <script type="text/javascript" src="/<c:url value="${script}"/>?release=true"></script>
</c:forEach>

	