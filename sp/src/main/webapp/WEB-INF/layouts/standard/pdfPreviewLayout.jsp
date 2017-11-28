<%@ page session="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<!DOCTYPE html>
<html lang="en">
<head>
<jsp:include page="/resources/includes/header-includes.jsp"></jsp:include>
</head>
<body>
	<tiles:insertAttribute name="content" />
</body>
</html>