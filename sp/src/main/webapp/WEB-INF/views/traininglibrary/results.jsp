<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
        <%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
            <div class="training-library search-results hide" fix-container data-ng-if="inviteReady" data-ng-class="inviteReady?'show':'hide'">
                <spring:eval expression="@environment.getProperty('base.serverUrl')" var="url" />
                <!-- Knowledge Center Header Starts -->                
                <div class="x-container">
                    <div class="container">
                        <div class="header-info-container">
                            <div class="header-info-content">
                                <div class="header-icon-wrap adj0">
                                    <p class="hinfo-wrap">
                                        <span class="hinfo-icons icon-icon_module-default sp-icon-color"></span>
                                    </p>
                                </div>
                                <div class="header-content-wrap mobAdj kc-searc-adj p5">
                                    <p class="hinfo-wrap">
                                        <a class="hinfo-link" href="/sp/trainingLibrary">
                                            <span class="info-txt ml0 mb8" data-ng-bind="::pullInterNationalization('kcContent.title')"></span>
                                        </a>
                                    </p>
                                </div>
                                <search-widget url-src="search-widget.html"></search-widget>                                  
                            </div>
                        </div>
                    </div>
                </div>
                <!-- Knowledge Center Header Ends -->
                <article-results url-src="article-results.html"></article-results>
            </div>
