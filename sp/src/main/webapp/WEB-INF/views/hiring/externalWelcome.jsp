<link type="text/css" rel="stylesheet" href="/resources/css/css-min/assessment-new.min.css" />
<!--div id="content" fix-container data-ng-if="inviteReady" class="hide" data-ng-class="inviteReady?'show':'hide'">
    <div class="x-container">
        <div class="container" fix-container>
            <div class="row">
                <div class="col-md-12 visitorContainer">
                    <div class="return-visitor-wrapper sp-panel-accent">
                        <p class="welcome-msg">{{pullInterNationalization('processToken.welcome.titleh1')}}</p>
                        <h2 class="sp-header-title-color" data-ng-bind-html="::pullInterNationalization('processToken.welcome.titleh2')"></h2>
                        <p data-ng-bind-html="pullInterNationalization('processToken.welcome.desc')"></p>
                        <div class="sp-btn-wrapper sp-red btn-small">
                            <div class="sp-btn">
                                <p>
                                    <a href="/sp/assessment360/${feedbackUserId }">
                                    {{pullInterNationalization('processToken.welcome.btn')}}
                                </a>
                                </p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div-->

<div class="assessment-start-page-wrapper hide" data-ng-if="inviteReady" data-ng-class="inviteReady?'show':'hide'">
	<div class="assessment-start-cols">
		<div class="start-left adj" fix-assessment-border>
			<h2 data-ng-bind="pullInterNationalization('processToken.welcome.titleh1')"></h2>
			<p class="desc" data-ng-bind-html="pullInterNationalization('processToken.welcome.titleh2')"></p>
			<p class="desc" data-ng-bind-html="pullInterNationalization('processToken.welcome.desc')"></p>
			<a href="/sp/assessment360/${feedbackUserId }" class="start-btn adj" data-ng-bind="pullInterNationalization('processToken.welcome.btn')"></a>
		</div>
	</div>
</div>  