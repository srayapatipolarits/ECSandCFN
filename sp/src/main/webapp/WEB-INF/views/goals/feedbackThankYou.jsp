<%@ page session="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>

    <div class="x-container hide" ng-if="inviteReady" ng-class="inviteReady?'show':'hide'" ng-controller="externalFeedbackThankYouCtrl as thanksCtrl">
        <!-- Extended Container -->
        <div class="container" fix-container>
            <!-- Bootstrap Container -->
            <div class="row">
                <!-- Bootstrap Row -->
                <div class="col-md-12 visitorContainer">
                    <div class="return-visitor-wrapper">
                        <p class="welcome-msg" data-ng-bind="pullInterNationalization('external.feedbackCompletedTitle')"></p>
                        <h2 data-ng-bind-html="pullInterNationalization('external.feedbackCompletedDesc1')"></h2>
                        <p class="less-spacing" data-ng-bind="thanksCtrl.getData.heading1"></p>
                        <p class="less-spacing" data-ng-if="thanksCtrl.getData.heading2 != null" data-ng-bind="thanksCtrl.getData.heading2"></p>
                    </div>
                </div>
            </div>
        </div>
    </div>