<%@ page language="java" contentType="text/html; charset=UTF-8"
  pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="sec"
  uri="http://www.springframework.org/security/tags"%>

<spring:eval expression="@environment.getProperty('base.serverUrl')" var="url" />
<c:set var="requestUrl"
  value="${requestScope['javax.servlet.forward.request_uri']}"></c:set>

<jsp:include page="/WEB-INF/views/support/pa-support_es_LA.html"></jsp:include>