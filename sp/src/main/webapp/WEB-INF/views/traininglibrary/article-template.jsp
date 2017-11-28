<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
        <%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
        	<spring:eval expression="@environment.getProperty('base.serverUrl')" var="url" />
                <article-detail url-src="article-detail.html"></article-detail>
                <script type="text/javascript" src="//translate.google.com/translate_a/element.js?cb=googleTranslateElementInit"></script>