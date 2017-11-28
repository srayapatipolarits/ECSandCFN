<%@ page session="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<div class="notes-feedback hide"  ng-if="inviteReady" ng-class="inviteReady?'show':'hide'" ng-controller="externalFeedbackController as ctrl" fix-container data-ng-cloak>
    <div class="x-container">
        <div class="container">
            <div class="row">
                <div class="col-xs-12">
                    <div class="feedback-overlay">
                        <div class="feedback-overlay-cols">
                            <h2 data-ng-bind="pullInterNationalization('notesAndFeedback.notifications.overlay.title')"></h2>
                            <div class="note-content" data-ng-class="ctrl.noBlue?'fo-left':'padLeft0'">
                            <p class="mbot30px">
          					${heading }
                                <div class="pic-details">
                                <c:choose>
									    <c:when test="${not empty user.smallProfileImage}">
									       <img src="${user.smallProfileImage}" alt="">
									    </c:when>
									    <c:otherwise>
									    	<div class="imgReplacement no-override-margin">${user.userInitials}</div>
									    </c:otherwise>
									</c:choose>
                                
                                    <p style="margin-top:3px">
                 					<c:choose>
									    <c:when test="${not empty user.name }">
									       <span>${user.name}</span>
									    </c:when>
									    <c:otherwise>
									    	<span>${user.name}</span>
									    </c:otherwise>
									</c:choose>
                                     
                                    <span class="role">${user.title}</span>
                                    </p>
                                </div>             
                                
                                <h4 class="greyH addMtop15" data-ng-bind="pullInterNationalization('practiceArea.request')"></h4>                   
                                <p class="preserve-line-break">${feedbackComment}</p>
                                
                                <h4 class="greyH addMtop15" data-ng-bind="pullInterNationalization('practiceArea.reply')"></h4>
								<form name="ctrl.sendFeedbackForm" novalidate data-ng-submit="ctrl.sendFeedbackRequest({'feedbackId':ctrl.requestNotificationInfo.id,'feedbackResponse':ctrl.requestNotificationInfo.feedbackResponse}, 'external')">
                                <input type="text" data-ng-model="ctrl.requestNotificationInfo.id" ng-init="ctrl.requestNotificationInfo.id='${feedbackId}'" class="hidden-id-input"/>
                                <input type="hidden" name="category" id="category" value="${goal.category}"/>
                                <input type="hidden" name="feedbackId" id="feedbackId" value="${feedbackId}"/>
                                                                
                                    <p class="reply-textarea">
                                        <textarea data-ng-model="ctrl.requestNotificationInfo.feedbackResponse" data-ng-required="true" name="noteDesc" maxlength="500" class="ng-pristine ng-invalid-required ng-invalid" required placeholder="{{pullInterNationalization('notesAndFeedback.inputPH')}}"></textarea>
                                        <span class="err-msg validate-error" data-ng-if="ctrl.requestFormSubmitted && ctrl.sendFeedbackForm.noteDesc.$error.required" data-ng-bind="pullInterNationalization('notesAndFeedback.inputPH')"></span>
                                    </p>
                                    <p class="form-btns">
                                        <input type="submit" value="submit" class="btn-block btn-14px sp-btn-color" />
                                        <a href="#" data-ng-click="ctrl.cancelFeedbackRequest(ctrl.requestNotificationInfo.id)" class="btn-block btn-14px btn-grey-dark">DECLINE</a>
                                    </p>
                                </form>
                            </div>
                            <div class="fo-right note-content" data-ng-if="ctrl.noBlue">
                                <div class="feedback-dev-goals">
                                	<h4 class="greyH notop" data-ng-bind="pullInterNationalization('practiceArea.parea')"></h4>
                                    <h3>${goal.name}</h3>
                                    <p class="clrBlack fw100">${goal.description}</p>
                                </div>
                                <c:choose>
									    <c:when test="${not empty goal.developmentStrategyList }">
        		                	            <h4 class="greyH addMtop15" data-ng-bind="pullInterNationalization('notesAndFeedback.developmentStrategy')"></h4>
        		                	            <ol class="clrBlack fw100" type="1">
        		                	            <c:forEach var="ds" items="${devStrategyDesc}">
        		                	            	<ul>${ds.dsDescription}</ul>
        		                	            </c:forEach>	
        		                	            </ol>
        		                	            
                		            	        <p class="clrBlack fw100">${devStrategyDesc}</p>
									    </c:when>
									    <c:otherwise></c:otherwise>
									</c:choose>
									
                            </div>
                        </div>
                        
                        <div class="practive-area-view blueprint-content organization-area-detail spcAdj2" data-ng-if="ctrl.blueprintReady">
						    <!-- MISSION STATEMENT INFO STARTS -->
						    <div class="publish-mission-statement">
						        <div class="row">
						            <div class="col-xs-12">
						                <h3 class="key-title" data-ng-bind="ctrl.dataMap.mission.title"></h3>
						                <p class="fixfont18" data-ng-bind="ctrl.blueprint.missionStatement.text"></p>
						            </div>
						        </div>
						    </div>
						    <!-- MISSION STATEMENT INFO ENDS -->
						    
						    
						    <!--  OBJECTIVES + KI + CSM ACCORDIAN STARTS  -->
						    <div class="bp-pub-obj-wrap-2">
						        <div class="obj-normal" data-ng-repeat="list in ctrl.blueprint.devStrategyActionCategoryList">
						            <section class="objectives-section">
						            
						            
						                <!-- Objective Header Starts -->
						                <header class="obj-header-wrap-mode sp-background-color">
						                    <h2 class="obj-header title" data-ng-bind="ctrl.dataMap.objective.title"></h2>
						                    <h3 class="obj-header" data-ng-bind="list.title"></h3>
						                </header>
						                <!-- Objective Header Ends -->
						                
						                
						                <!-- Objective KI and CSM Details Starts -->
						                <div class="obj-content">
						                    <div class="obj-ki-wrap" data-ng-repeat="actionList in list.actionList" data-ng-init="parentIndex=$parent.$index;currentIndex = $index">
						                    	<h3 class="key-title" data-ng-bind="ctrl.dataMap.initiatives.title"></h3>
						                    	<p class="obj-ki-info">
						                            <input type="checkbox" data-ng-model="actionList.completed" data-ng-disabled="!actionList.permissions.Completion" class="iCheckBox" id="devStrategyActionCategoryList_{{parentIndex}}_{{$index}}">
						                            <label class="lCheckBox" data-ng-class="ctrl.stateControl(actionList)?'block':'blockCHeck'">
						                            	<span class="checkedTick icon-icon_dev_strategy_check sp-icon-color"></span>
						                            	<span class="black-txt-color font-weight100">
						                               		<span class="bp-nums f14" data-ng-bind="($index+1)+'. '"></span>
						                               		<span class="bp-txts" data-ng-bind="actionList.title"></span>                                      	
						                            	</span>
						                            </label>	
						                        </p>
						                        <article class="critical_success_measure">
						                        	<ol class="obj-csm-list">
						                        		<li>
						                        			<h3 class="key-title" data-ng-bind="ctrl.dataMap.csm.title"></h3>
						                    			</li>
						                                <li data-ng-repeat="actionData in actionList.actionData" data-ng-init="childParIndex=$parent.$index">
						                                    <p>
						                                        <input type="checkbox" data-ng-model="actionData.completed" class="iCheckBox" id="groupActionCategoryList_{{$index}}_{{childParIndex}}_{{parentIndex}}" data-ng-click="ctrl.changeStatus({'completed':!actionData.completed,'uid':actionData.uid},actionData)">
						                                        <label class="lCheckBox mp" data-ng-class="{'block':!actionData.permissions.Completion,'cursorHand':noSelf!==''}">
						                                        	<span class="checkedTick icon-icon_dev_strategy_check sp-icon-color topFix2"></span>
						                                        	<span class="black-txt-color">
						                                        		<span class="bp-nums" data-ng-bind="(childParIndex+1)+'.'+($index+1)+'. '"></span>
						                                        		<span class="bp-txts" data-ng-bind="actionData.title"></span>
						                                        	</span> 
						                                        </label>
						                                    </p>
						                                </li>
						                            </ol>
						                        </article>
						                    </div>
						                </div>
						                <!-- Objective KI and CSM Details Ends -->
						                
						                
						            </section>
						        </div>
						    </div>
						    <!--  OBJECTIVES + KI + CSM ACCORDIAN STARTS  -->
						    
						</div>
						                        
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
