<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
        <%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
            <div class="training-library search-results hide" data-ng-controller="LibraryController" fix-container data-ng-init="getAllBookmarksWithTrainingSpotlight(true)" data-ng-if="inviteReady" data-ng-class="inviteReady?'show':'hide'">
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
                <div class="x-container">
                    <div class="container">
                        <div class="table-flex-cols">
                            <aside class="related-content table-col-hide">
                                <practice-areas-widget url-src="practice-areas-widget.html"  data-type="standard"></practice-areas-widget>
                                <!-- //.article-filter-tool -->
                                <section class="spotlight bg-white event-spotlight-articles">
                                    <header class="spotlight-header">
                                        <h2 class="spotlight-title-2 sp-header-accent" data-ng-bind="::pullInterNationalization('kcContent.spotlight')"></h2>
                                        <a href="/sp/trainingLibrary/article?{{trainingSpotlightContent.articleLinkUrl}}"><img data-ng-src="{{trainingSpotlightContent.imageUrl}}" alt="" /></a>
                                    </header>
                                    <div class="spotlight-description">
                                        <h3 class="spotlight-title-3"><span class="mock-link black-link" data-ng-bind="trainingSpotlightContent.author[0]"></span></h3>
                                        <a class="border-link" href="/sp/trainingLibrary/article?{{trainingSpotlightContent.articleLinkUrl}}"><h4 class="spotlight-title-4"><span class="mock-link black-link" data-ng-bind="trainingSpotlightContent.articleLinkLabel"></span></h4></a>
                                        <p> <span data-ng-bind-html="trainingSpotlightContent.content"></span><a class="border-link spotlight-block-link" data-ng-href="/sp/trainingLibrary/article?{{trainingSpotlightContent.articleLinkUrl}}"><span data-ng-bind="::pullInterNationalization('kcContent.playVid')"></span></a>
                                        </p>
                                    </div>
                                </section>
                                <nav class="related-article-tools" data-ng-init="trendingTopRatedArticles()">
                                    <section class="related-article-links">
                                        <h2 class="sp-header-title-color" data-ng-bind="::pullInterNationalization('kcContent.trend')"></h2>
                                        <ul class="spotlight-related-links event-trending-articles">
                                            <li data-ng-repeat="t in trendingList">
                                                <span class="kc-icons-themed sp-icon-color" data-ng-class="{'icon-icon_audio':t.articleType=='PODCAST','icon-icon_slideshow':t.articleType=='SLIDESHARE','icon-icon_video':t.articleType=='VIDEO','icon-icon_article':t.articleType=='TEXT'}"></span>
                                                <h3>
                                                        <a class="border-link" href="/sp/trainingLibrary/article?{{t.articleLinkUrl}}">
                                                            <span class="mock-link" data-ng-bind="::t.articleLinkLabel"></span>
                                                        </a>
                                                    </h3>
                                                <p class="training-tool-info" data-ng-if="t.author[0]"><span class="author"><span>By</span> <span data-ng-bind="::t.author[0]"></span></p>
                                            </li>
                                        </ul>
                                    </section>
                                    <!--  
                                        <section class="related-article-links hide">
                                            <h2 class="sp-header-title-color">Top Rated</h2>
                                            <ul class="spotlight-related-links event-top-rated-articles">
                                                <li data-ng-repeat="r in topRatedList">
                                                    <span class="kc-icons-themed sp-icon-color" data-ng-class="{'icon-icon_audio':r.articleType=='PODCAST','icon-icon_slideshow':r.articleType=='SLIDESHARE','icon-icon_video':r.articleType=='VIDEO','icon-icon_pdf':r.articleType=='TEXT'}"></span>
                                                    <h3>
                                                        <a class="border-link" href="/sp/trainingLibrary/article?{{r.articleLinkUrl}}">
                                                            <span class="mock-link" data-ng-bind="::r.articleLinkLabel"></span>
                                                        </a>
                                                    </h3>
                                                    <p class="training-tool-info" data-ng-if="r.author[0]"><span class="author"><span>By</span> <span data-ng-bind="::r.author[0]"></span></p>
                                                </li>
                                            </ul>
                                        </section>
                                        -->
                                </nav>
                            </aside>
                            <!-- // .related-content -->
                            <div class="main-content search-results space-adj">
                                <header class="results-header">
                                    <h1 class="search-result-title">
                                <span class="static">Showing all bookmarks 
                                <!-- 
                                <span class="search-category">{{currentArticleCount.length}}</span> results for</span> <span
                                    class="search-category" data-ng-bind-html="searchTerm"> -->
                                    </span>
                            </h1>
                                    <p class="count-result">
                                        <!-- 
                                <span class="count-shown">{{articleResultData.currentArtilcesCount}}</span>
                                <span class="static">out of</span> <span class="count-total">{{articleResultData.totalArticlesCount}}</span>
                                <span class="static">results</span>
                            -->
                                    </p>
                                    <div class="sort-controller">
                                        <h2 class="filter-by-title" data-ng-bind="::pullInterNationalization('kcContent.filterBy')"></h2>
                                        <!--  data-ng-if="articleFilters.length > 1" -->
                                        <ul class="result-category">
                                            <!--li data-ng-repeat="f in articleFilters" data-ng-class="tab === $index?'active':'no-active'"><a class="border-link" href="javascript:void(0)" data-ng-click="filterResults(f, $index, $event)"><span class="mock-link">{{f}}</span></a></li><!--  $parent.searchFilterVal=f;$parent.tab=$index;$event.preventDefault(); -->
                                            <li data-ng-class="tab == 0?'active':'no-active'"><a class="border-link" href="javascript:void(0)" data-ng-click="filterResults(null, 0, $event)" id="event_filter-by_all"><span class="mock-link" data-ng-bind="::pullInterNationalization('kcContent.filterAll')"></span></a></li>
                                            <li data-ng-class="articleFilters.indexOf('Article') === -1?'in-active':(tab == 1?'active':'no-active')"><a class="border-link" href="javascript:void(0)" data-ng-click="filterResults('Article', 1, $event)" id="event_filter-by_article"><span class="mock-link" data-ng-bind="::pullInterNationalization('kcContent.filterArticle')"></span></a></li>
                                            <li data-ng-class="articleFilters.indexOf('Video') === -1?'in-active':(tab == 2?'active':'no-active')"><a class="border-link" href="javascript:void(0)" data-ng-click="filterResults('Video', 2, $event)" id="event_filter-by_video"><span class="mock-link" data-ng-bind="::pullInterNationalization('kcContent.filterVid')"></span></a></li>
                                            <!--
                                            <li data-ng-class="articleFilters.indexOf('Podcast') === -1?'in-active':(tab == 3?'active':'no-active')"><a class="border-link" href="javascript:void(0)" data-ng-click="filterResults('Podcast', 3, $event)" id="event_filter-by_podcast"><span class="mock-link" data-ng-bind="::pullInterNationalization('kcContent.filterPod')"></span></a></li>
                                            <li data-ng-class="articleFilters.indexOf('Slideshare') === -1?'in-active':(tab == 4?'active':'no-active')"><a class="border-link" href="javascript:void(0)" data-ng-click="filterResults('Slideshare', 4, $event)" id="event_filter-by_slideshare"><span class="mock-link" data-ng-bind="::pullInterNationalization('kcContent.filterShare')"></span></a></li>
                                            -->
                                        </ul>
                                    </div>
                                </header>
                                <section class="search-result" data-ng-repeat="a in currentArticleCount=(recentBookmarkList | filter:searchFilterVal)">
                                    <!-- h2 class="theme-topic-title">PROFILE THEME TOPIC: {{a.articleType}}</h2-->
                                    <h3 class="content-of-theme-title">
                                            <a href="/sp/trainingLibrary/article?{{a.articleLinkUrl}}" class="border-link">
                                                <span data-ng-bind-html="a.articleLinkLabel"></span>
                                            </a>
                                        </h3>
                                    <p class="content-source-url" data-ng-if="a.goals.length > 0">
                                        <span data-ng-bind="pullInterNationalization('search.results.pa.title')"></span>
                                        <span data-ng-repeat="g in a.goals track by $index" data-ng-class="$last?'last':''">
                                                <span><span class="content-seperation" data-ng-class="$first?'hide':''">,&#160;</span><span data-ng-bind="g.name"></span></span>
                                        </span>
                                    </p>
                                    <p class="publisher-info">
                                        <a class="border-link" href="javascript:void(0)" data-ng-if="formattedAuthor(a.author)" id="event_source_category-results">{{::pullInterNationalization('dashboard.by')}} <span class="mock-link" data-ng-bind="formattedAuthor(a.author)"></span></a>
                                        <!--span class="pub-date">{{a.formattedDate}}</span-->
                                        <!--a class="border-link" href="javascript:void(0)" data-ng-click="refreshSearch('Source', a.articleSourceTypes, false)"-->
                                        <span class="pub-date source" data-ng-class="formattedAuthor(a.author)?'':'adjustSpace'" data-ng-bind-html="a.articleSourceTypes"></span>
                                        <!-- </a>  -->
                                        <span class="pub-date shares" data-ng-if="a.recommendationCount > 0" data-ng-bind="a.recommendationCount+' '+pullInterNationalization('traininglibrary.article.Recommendations')"></span>
                                        <span class="pub-date completed" data-ng-if="a.canAddToUserGoals===false && a.userArticleStatus == 'COMPLETED'"><span data-ng-bind="::pullInterNationalization('kcContent.completed')"></span></span>
                                        <!-- <span class="pub-date" data-ng-if="a.canAddToUserGoals===false && a.userArticleStatus == 'NOT_STARTED'">Added to Plan</span>
                                        <a class="border-link sp-orange" data-ng-if="a.canAddToUserGoals===true" href="javascript:void(0)" data-ng-click="addtoGoals(a.articleLinkUrl, true);"><span class="mock-link">Add to Plan</span></a> -->
                                    </p>
                                </section>
                            </div>
                            <!-- //.main-content -->
                            <aside class="related-content table-col-show">
                                <practice-areas-widget url-src="practice-areas-widget.html"  data-type="standard"></practice-areas-widget>
                                <!-- //.article-filter-tool -->
                                <section class="spotlight bg-white event-spotlight-articles">
                                    <header class="spotlight-header">
                                        <h2 class="spotlight-title-2 sp-header-accent" data-ng-bind="::pullInterNationalization('kcContent.spotlight')"></h2>
                                        <a href="/sp/trainingLibrary/article?{{trainingSpotlightContent.articleLinkUrl}}"><img data-ng-src="{{trainingSpotlightContent.imageUrl}}" alt="" /></a>
                                    </header>
                                    <div class="spotlight-description">
                                        <h3 class="spotlight-title-3"><span class="mock-link black-link" data-ng-bind="trainingSpotlightContent.author[0]"></span></h3>
                                        <a class="border-link" href="/sp/trainingLibrary/article?{{trainingSpotlightContent.articleLinkUrl}}"><h4 class="spotlight-title-4"><span class="mock-link black-link" data-ng-bind="trainingSpotlightContent.articleLinkLabel"></span></h4></a>
                                        <p> <span data-ng-bind-html="trainingSpotlightContent.content"></span><a class="border-link spotlight-block-link" data-ng-href="/sp/trainingLibrary/article?{{trainingSpotlightContent.articleLinkUrl}}"><span data-ng-bind="::pullInterNationalization('kcContent.playVid')"></span></a>
                                        </p>
                                    </div>
                                </section>
                                <nav class="related-article-tools" data-ng-init="trendingTopRatedArticles()">
                                    <section class="related-article-links">
                                        <h2 class="sp-header-title-color" data-ng-bind="::pullInterNationalization('kcContent.trend')"></h2>
                                        <ul class="spotlight-related-links event-trending-articles">
                                            <li data-ng-repeat="t in trendingList">
                                                <span class="kc-icons-themed sp-icon-color" data-ng-class="{'icon-icon_audio':t.articleType=='PODCAST','icon-icon_slideshow':t.articleType=='SLIDESHARE','icon-icon_video':t.articleType=='VIDEO','icon-icon_article':t.articleType=='TEXT'}"></span>
                                                <h3>
                                                        <a class="border-link" href="/sp/trainingLibrary/article?{{t.articleLinkUrl}}">
                                                            <span class="mock-link" data-ng-bind="::t.articleLinkLabel"></span>
                                                        </a>
                                                    </h3>
                                                <p class="training-tool-info" data-ng-if="t.author[0]"><span class="author"><span>By</span> <span data-ng-bind="::t.author[0]"></span></p>
                                            </li>
                                        </ul>
                                    </section>
                                    <!--  
                                        <section class="related-article-links hide">
                                            <h2 class="sp-header-title-color">Top Rated</h2>
                                            <ul class="spotlight-related-links event-top-rated-articles">
                                                <li data-ng-repeat="r in topRatedList">
                                                    <span class="kc-icons-themed sp-icon-color" data-ng-class="{'icon-icon_audio':r.articleType=='PODCAST','icon-icon_slideshow':r.articleType=='SLIDESHARE','icon-icon_video':r.articleType=='VIDEO','icon-icon_pdf':r.articleType=='TEXT'}"></span>
                                                    <h3>
                                                        <a class="border-link" href="/sp/trainingLibrary/article?{{r.articleLinkUrl}}">
                                                            <span class="mock-link" data-ng-bind="::r.articleLinkLabel"></span>
                                                        </a>
                                                    </h3>
                                                    <p class="training-tool-info" data-ng-if="r.author[0]"><span class="author"><span>By</span> <span data-ng-bind="::r.author[0]"></span></p>
                                                </li>
                                            </ul>
                                        </section>
                                        -->
                                </nav>
                            </aside>
                        </div>
                    </div>
                </div>
            </div>
