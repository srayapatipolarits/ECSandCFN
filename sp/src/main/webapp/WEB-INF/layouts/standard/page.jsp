<%@ page session="false"%>
<%@ page pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<!DOCTYPE html>
<html lang="${pageContext.request.locale.language}" ng-app="spApp">
<head>
<title><tiles:insertAttribute name="title"
		defaultValue="SurePeople" /></title>
<meta http-equiv="content-type" content="text/html;charset=utf-8" />
<meta name="description"
	content="<tiles:insertAttribute name="description" defaultValue="SurePeople" />" />
<meta name="keywords"
	content="<tiles:insertAttribute name="keywords" defaultValue="SurePeople" />" />
<meta http-equiv="X-UA-Compatible" content="IE=edge"/>
<jsp:include page="/resources/includes/header-includes.jsp"></jsp:include>
<meta data-ng-if="selectedNav !== 'erti' && profileReady" name="viewport" content="width=1024">
<meta data-ng-if="(selectedNav === 'erti' && !mgrSummaryView) || !profileReady" name="viewport" content="width=device-width, initial-scale = 1.0" />
<meta data-ng-if="selectedNav === 'erti' && mgrSummaryView" name="viewport" content="width=1024">
<link type="text/css" rel="stylesheet" href="<spring:theme code="cssStyle" />" />
<script type="text/javascript">
(function(a){if(window.filepicker){return}var b=a.createElement("script");b.type="text/javascript";b.async=!0;b.src=("https:"===a.location.protocol?"https:":"http:")+"//api.filestackapi.com/filestack.js";var c=a.getElementsByTagName("script")[0];c.parentNode.insertBefore(b,c);var d={};d._queue=[];var e="pick,pickMultiple,pickAndStore,read,write,writeUrl,export,convert,store,storeUrl,remove,stat,setKey,constructWidget,makeDropPane".split(",");var f=function(a,b){return function(){b.push([a,arguments])}};for(var g=0;g<e.length;g++){d[e[g]]=f(e[g],d._queue)}window.filepicker=d})(document);
</script>
</head>
<body class="notranslate">
	<!-- Google Tag Manager -->
	<noscript>
		<iframe src="//www.googletagmanager.com/ns.html?id=GTM-K7DMVG"
			height="0" width="0" style="display: none; visibility: hidden"></iframe>
	</noscript>
	<script>
		(function(w, d, s, l, i) {
			w[l] = w[l] || [];
			w[l].push({
				'gtm.start' : new Date().getTime(),
				event : 'gtm.js'
			});
			var f = d.getElementsByTagName(s)[0], j = d.createElement(s), dl = l != 'dataLayer' ? '&l='
					+ l
					: '';
			j.async = true;
			j.src = '//www.googletagmanager.com/gtm.js?id=' + i + dl;
			f.parentNode.insertBefore(j, f);
		})(window, document, 'script', 'dataLayer', 'GTM-K7DMVG');
	</script>
	<!-- End Google Tag Manager -->

	<!-- End Google Tag Manager -->

	<div class="page-wrapper">
		<div id="header">
			<tiles:insertAttribute name="header" />
		</div>
		<div id="content-container">
			<div id="content" data-ng-if="!showFeedbackOverlay">
				<div id="loader">
					<div class="spinner">
						<div class="double-bounce1"></div>
						<div class="double-bounce2"></div>
					</div>
				</div>
				<tiles:insertAttribute name="content" />
			</div>
			<div id="content" data-ng-if="showFeedbackOverlay">
				<div id="loader">
					<div class="spinner">
						<div class="double-bounce1"></div>
						<div class="double-bounce2"></div>
					</div>
				</div>
				<!-- Feedback Reply -->
                <feedback-reply url-src="feedback/feedback-reply.html" parent-page="dashboard" data-ng-if="showFeedbackOverlay"></feedback-reply>
			</div>
			<ng-include src="'/sp/resources/html/common/sticky-toolbar.html'"></ng-include>
		</div>
		<div id="footer">
			<tiles:insertAttribute name="footer" />
		</div>
	</div>
	<!-- Notes Side Panel Directive -->
    <notes-tab url-src="notes/notes-tabs.html" class="side-panel" data-ng-if="showNotesPanel"></notes-tab>
    
    <!-- Feedback module side panel Directive -->
    <feedback-requests url-src="feedback/feedback-panel.html" data-ng-if="showFeedbackPanel" parent-page="sidePanel"></feedback-requests>
    
    <!-- Feedback Requests For Mobile Dashboard-->
    <feedback-requests data-ng-if="mobileDashboardFeedback" url-src="feedback/feedback-panel.html" parent-page="mobileDashboard"></feedback-requests>
    
    <!-- Media Manager modal -->
     <media-manager  url-src="common/media-modal.html" data-ng-if="showMediaManager" class="side-panel" parent-page="dialogView"></media-manager>
     
     <!-- Image modal -->
     <image-modal  url-src="common/image-modal.html" data-ng-if="showImageModal"></image-modal>
	
	<!-- Scripts Start -->
	<jsp:include page="/resources/includes/footer-includes.jsp"></jsp:include>
	<!-- Scripts End -->
	

</body>
</html>