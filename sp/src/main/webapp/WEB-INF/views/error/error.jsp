<%@ page session="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<div class="x-container hide" data-ng-if="inviteReady" data-ng-class="inviteReady?'show':'hide'">
    <div class="container" fix-container>
        <div class="row">
            <div class="col-xs-12">
				<div id="error">
					<h2 class="error-head sp-header-title-color" data-ng-bind="pullInterNationalization('errorPg.Heading')"></h2>
					<p class="error-content" data-ng-bind-html="pullInterNationalization('errorPg.Content')"></p>
				</div>                
            </div>
        </div>
    </div>
</div>