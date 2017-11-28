/**
 * This script file stores all the commands to setup the mongo db.
 */

// The User indexes
db.user.ensureIndex({
	"email" : 1
}, {
	unique : true
})

db.user.ensureIndex({
	"companyId" : 1
});

// The product indexes
db.product.ensureIndex({
	"name" : 1
}, {
	unique : true
});

db.product.insert({
	name : "SurePeople Business",
	unitPrice : 10.0,
	productType : "BUSINESS",
	minEmployee : 20,
	minHiringEmployee : 10,
	titleKey : "Pay Annually <span class='offers'>and save 28%</span>",
	validityKey : "{0}",
	unitPriceKey : "per Employee per month",
	validityType : "YEARLY",
	status : "Active"
});

db.product.insert({
			name : "SurePeople Business Monthly Plan",
			unitPrice : 14.0,
			productType : "BUSINESS",
			minEmployee : 20,
			titleKey : "Pay Monthly <span class='tc'>A month is a 30 day period</span>",
			validityKey : "{0}",
			unitPriceKey : "per Employee per month",
			validityType : "MONTHLY",
			status : "Active"
		});

db.product.insert({
	"_id" : ObjectId("557030f9228d1d3fd114b0e8"),
	name : "SurePeople Business June 2015",
	unitPrice : 14.0,
	productType : "BUSINESS",
	minEmployee : 10,
	minHiringEmployee : 10,
	titleKey : "Pay Annually <span class='offers'>and save 22%</span>",
	validityKey : "{0}",
	unitPriceKey : "per Employee per month",
	validityType : "YEARLY",
	status : "Active"
});

db.product.insert({
		  "_id" : ObjectId("557030fa228d1d3fd114b0e9"),
			name : "SurePeople Business Monthly Plan June 2015",
			unitPrice : 16.0,
			productType : "BUSINESS",
			minEmployee : 10,
			titleKey : "Pay Monthly <span class='tc'>A month is a 30 day period</span>",
			validityKey : "{0}",
			unitPriceKey : "per Employee per month",
			validityType : "MONTHLY",
			status : "Active"
		});


db.product.insert({
	name : "SurePeople Individual",
	unitPrice : 10.75,
	productType : "INDIVIDUAL",
	minEmployee : 1,
	titleKey : "Pay Annually",
	validityKey : "{0}",
	unitPriceKey : "",
	validityType : "YEARLY",
	status : "Active"
});

/*
db.product.insert({
			name : "SurePeople Individual Plan Monthly",
			unitPrice : 12.0,
			productType : "INDIVIDUAL",
			minEmployee : 1,
			titleKey : "Pay Monthly <span class='tc'>A month is a 30 day period</span>",
			validityKey : "<strong>1-month contract:</strong> Expires on {0}",
			unitPriceKey : "",
			validityType : "MONTHLY",
			status : "Active"
		});
*/

db.product.insert({
	name : "Hiring",
	unitPrice : 400.0,
	productType : "HIRING",
	minEmployee : 5,
	titleKey : "",
	validityKey : "",
	unitPriceKey : "",
	status : "Active"
});

// The promotion indexes
db.promotion.ensureIndex({
	"code" : 1,
	"name" : 2
}, {
	unique : true
})

// The User Group Indexes
db.userGroup.ensureIndex({
	"name" : 1,
	"companyId" : 2
}, {
	unique : true
})

// The Hiring User
db.hiringUser.ensureIndex({
	"email" : 1,
	"companyId" : 1
}, {
	unique : true
})


// payment record
db.sequence.insert({
	_id : "orderNo",
	seq : 0,
	certSeq : 1213104
})

db.sequence.insert({
	_id : "accountNo",
	seq : 0,
})


// Pulse based constraints
db.pulseRequest.ensureIndex({
	companyId : 1,
	pulseQuestionSetId : 2
}, {
	unique : true
});

db.pulseAssessment.ensureIndex({
	pulseRequestId : 1,
	memberId : 2
}, {
	unique : true
});

//Book marking constraints
db.bookMarkTracking.ensureIndex({
	userId : 1,
	articleId : 2
}, {
	unique : true
});

db.sPGoal.ensureIndex({
	name : 1,
}, {
	unique : true
});

db.articles.ensureIndex({
	articleLinkLabel : 1,
}, {
	unique : true
});

db.trainingLibraryHomeArticle.insert({ "articleId" : "33a0be656cb753d8f36bfa523e9ca070", "shortDescription" : "Being the rookie is difficult, but it's not always a bad thing.", "articleLocation" : "Hero", "articlePosition" : 100 })
db.trainingLibraryHomeArticle.insert({ "articleId" : "62d24adc775e28931e9115c8130e7bd4", "shortDescription" : "If there's one force that can either sabotage or propel your message, it's your body.", "articleLocation" : "Hero", "articlePosition" : 101 })
db.trainingLibraryHomeArticle.insert({ "articleId" : "a9863f2dea2f81f0edef81de4438937d", "shortDescription" : "At a recent professional development retreat led by corporate trainer Dana Brownlee, a woman in her mid-50s stood up and starting citing a laundry list of communication conflicts on her mixed-age team.", "articleLocation" : "Content", "articlePosition" : 0 })
db.trainingLibraryHomeArticle.insert({ "articleId" : "f6627b9502eb45406ed13884f035859f", "shortDescription" : "Multitasking seems like such a great way to get so much done in so little time. Who wouldn't want to knock out some emails, have lunch, and listen in on a phone conference all at the same time?", "articleLocation" : "Content", "articlePosition" : 1 })
db.trainingLibraryHomeArticle.insert({ "articleId" : "c8d387d617066826d4e8a72f3f42699e", "shortDescription" : "Good news for executive coaches: A new study from Stanford and executive coaching firm The Miles Group shows that while two-thirds of chief executives don't get any coaching or leadership advice from outside their companies, nearly 100% of those bosses say they wish that they did.", "articleLocation" : "Content", "articlePosition" : 2 })
db.trainingLibraryHomeArticle.insert({ "articleId" : "f5dbdc8443999cc4ae7fa7392ae9cea2", "shortDescription" : "I'm not a big fan of New Year's resolutions. They're hard to keep, it's an emotional kick in the teeth when they don't work out, and they seldom make a lot of sense from a leadership perspective.", "articleLocation" : "Content", "articlePosition" : 3 })



/* Administrator User */
db.account.insert({ "_id" : ObjectId("54c3360cd4c6a5978c84d05c"), "_class" : "com.sp.web.model.Account", "startDate" : ISODate("2015-01-24T06:05:00.704Z"), "status" : "VALID", "products" : [ "54c3353aa0e33beec8363fc6", "54c3353aa0e33beec8363fc2" ], "availableSubscriptions" : 17, "hiringSubscription" : 25, "paymentInstrumentId" : "54c3360cd4c6a5978c84d05b", "billingCycleStartDate" : ISODate("2015-01-24T06:05:00.751Z"), "type" : "Business", "lastPaymentId" : "54c3360cd4c6a5978c84d05d", "expiresTime" : ISODate("2016-01-19T06:05:00.751Z"), "nextPaymentDate" : ISODate("2016-01-19T06:05:00.751Z"), "nextChargeAmount" : 1440 });
db.company.insert({ "_id" : ObjectId("54c3360cd4c6a5978c84d05e"), "_class" : "com.sp.web.model.Company", "name" : "Surepeople", "address" : { "country" : "USA", "addressLine1" : "12", "addressLine2" : "", "city" : "Chicago", "state" : "AZ", "zipCode" : "12121" }, "industry" : "1", "numberOfEmployees" : 0, "phoneNumber" : "90003000032", "accountId" : "54c3360cd4c6a5978c84d05c", "isBlockAllMembers" : false });
db.user.insert({ "_class" : "com.sp.web.model.User", "createdOn" : ISODate("2015-01-24T06:00:00Z"), "firstName" : "Administrator", "lastName" : "", "title" : "SurePeople Adminstrator", "email" : "adminstrator@surepeople.com", "password" : "$2a$10$ZF0szs1xGbrYcxibnLCNfuDzAlihD759jsphl8HILrUPp.cp2MBKe", "address" : { "country" : "USA", "addressLine1" : "Hose", "addressLine2" : "adf", "city" : "patan", "state" : "IL", "zipCode" : "41212" }, "phoneNumber" : "99392993992", "userStatus" : "VALID", "companyId" : "54c3360cd4c6a5978c84d05e", "analysis" : { "_class" : "com.sp.web.assessment.processing.AnalysisBeanDTO", "accuracy" : "74", "processing" : { "External" : "80", "Concrete" : "75", "Intuitive" : "25", "Cognitive" : "75", "Internal" : "20", "Affective" : "25", "Orderly" : "80", "Spontaneous" : "20" }, "conflictManagement" : { "Avoid" : "20", "Accommodate" : "10", "Compete" : "20", "Compromise" : "20", "Collaborate" : "30" }, "learningStyle" : { "Global" : "73", "Analytical" : "27" }, "motivationWhy" : { "Compliance" : "10", "AttainmentOfGoals" : "90", "RecognitionForEffort" : "10", "Power" : "90", "Affiliation" : "90", "Activity" : "10" }, "motivationHow" : { "TaskCompletion" : "25", "PrefersProcess" : "75", "ExchangeOfIdeas" : "58", "ReceiveDirection" : "42", "Freedom" : "42", "Consistency" : "58", "AffirmedByOthers" : "58", "SelfAffirmed" : "42" }, "motivationWhat" : { "Hygiene" : "50", "Accomplishment" : "50" }, "personality" : { "UnderPressure" : { "segmentScore" : 7314, "segmentGraph" : [ "1.00", "0.43", "0.14", "0.57" ], "isValid" : true, "personalityType" : "Visionary" }, "PerceivedByOthers" : { "segmentScore" : 7314, "segmentGraph" : [ "1.00", "0.43", "0.14", "0.57" ], "isValid" : true, "personalityType" : "Visionary" }, "Primary" : { "segmentScore" : 6425, "segmentGraph" : [ "0.86", "0.57", "0.29", "0.71" ], "isValid" : true, "personalityType" : "Navigator" } }, "fundamentalNeeds" : { "Security" : "23", "Control" : "35", "Significance" : "43" }, "decisionMaking" : { "Outward" : "67", "Rapid" : "33", "Inward" : "33", "Careful" : "67" } }, "roles" : ["SuperAdministrator" ], "gender" : "M", "dob" : ISODate("1980-04-04T06:00:00Z"), "groupAssociationList" : [ { "name" : "WorldCup - 2015", "isGroupLead" : false } ], "statusMessage" : "user.status.profile.incomplete" })

db.company.insert({ "_id" : "default", "_class" : "com.sp.web.model.Company", "name" : "SPAppDefault", "address" : { "country" : "USA", "addressLine1" : "12", "addressLine2" : "", "city" : "Chicago", "state" : "AZ", "zipCode" : "12121" }, "industry" : "1", "numberOfEmployees" : 0, "phoneNumber" : "90003000032", "accountId" : "54c3360cd4c6a5978c84d05c", "isBlockAllMembers" : false, "companyTheme" :{"stylesMap":{"spMainNavBackgroundColor" : "#000", "spIconColor" : "#f63", "spHeaderAccentbarColor" : "#f63", "spSigninAccentbarColor" : "#f63", "spLinkColor" : "#f63", "spHeaderTitleColor" : "#f63", "spFooterBackgroundColor" : "#666", "spButtonColor" : "#f63", "spButtonHoverColor" : "#cc3300", "spLinkHoverColor" : "#000", "spMainNavColor" : "#ffa200", "spPanelAccentbarColor" : "#cdcccb", "spSubNavLinkColor" : "#000", "spSubNavBackgroundColor" : "#ffa200", "spFooterNavigationTextColor" : "#fff", "spFooterNavigationHoverColor" : "#000" },"cssUrl":"{0}/theme/surepeople/defaultTheme.css","themeKey":"surepeople"},"companyThemeActive":false,"restrictRelationShipAdvisor":false,"blockAllMembers":false});
/** Billing user */

db.account.insert({ "_id" : "1", "_class" : "com.sp.web.model.Account", "startDate" : ISODate("2015-09-24T10:03:14.609Z"), "status" : "VALID", "accountNumber" : "SP000003", "spPlanMap" : { "Primary" : { "name" : "Oggy", "licensePrice" : "200", "unitMemberPrice" : "10", "overrideMemberPrice" : "0", "unitAdminPrice" : "100", "overrideAdminPrice" : "0", "numMember" : 9, "numAdmin" : 2, "features" : [ "PrismLens", "Pulse" ], "aggrementTerm" : 1, "aggreementEndDate" : ISODate("2016-09-24T10:03:14.609Z"), "planType" : "Primary", "planMemberNextChargeAmount" : "-1100.00", "planAdminNextChargeAmount" : "-2200.00", "bundle" : false, "active" : true } }, "availableSubscriptions" : 0, "hiringSubscription" : 0, "paymentInstrumentId" : "5603ca62d4c6ea2286ce7ab8", "billingCycleStartDate" : ISODate("2015-09-24T10:03:14.629Z"), "type" : "Business", "lastPaymentId" : "5603cc74d4c6ea2286ce7aca", "expiresTime" : ISODate("2016-09-18T10:03:14.629Z"), "nextPaymentDate" : ISODate("2015-10-24T10:03:14.629Z"), "nextChargeAmount" : -3100, "customerProfileId" : "SP000003", "agreementTerm" : 0, "paymentType" : "WIRE", "deactivated" : false, "billingCycle" : { "billingCycleType" : "Custom", "noOfMonths" : 7 }, "tagsKeywords" : [ "ad" ], "accountUpdateDetailHistory" : { "Primary" : [ { "createdOn" : ISODate("2015-09-24T10:03:14.704Z"), "noOfAccounts" : NumberLong(10), "unitPrice" : "10", "overridePrice" : "0", "accountUpdateType" : "PlanMember" }, { "createdOn" : ISODate("2015-09-24T10:03:14.704Z"), "noOfAccounts" : NumberLong(2), "unitPrice" : "100", "overridePrice" : "0", "accountUpdateType" : "PlanAdmin" } ] } });
db.company.insert({ "_id" : "1", "_class" : "com.sp.web.model.Company", "name" : "oggy", "address" : { "country" : "Canada", "addressLine1" : "23", "addressLine2" : "", "city" : "asdf", "state" : "asdf", "zipCode" : "23232" }, "industry" : "accounting", "numberOfEmployees" : 33, "phoneNumber" : "2342342342", "accountId" : "1", "isBlockAllMembers" : false, "featureList" : [ "Pulse", "PrismLens" ], "roleList" : [ "PrismLens" ], "restrictRelationShipAdvisior" : false });
db.user.insert({ "_id" : "billingAdminId", "_class" : "com.sp.web.model.User", "firstName" : "SPBilling", "lastName" : "Admin", "title" : "SPBillingAdmin", "email" : "spbillingadmin@surepeople.com", "password" : "$2a$10$UvJqvNQ2n5uSYglI1TWyRu..64OodB6aGX9hYumKi5qr0HBZLjA4y", "address" : { "country" : "United States", "addressLine1" : "32", "addressLine2" : "", "city" : "chicago", "state" : "IL", "zipCode" : "32323" }, "phoneNumber" : "3424242424", "userStatus" : "VALID", "companyId" : "1", "analysis" : { "_class" : "com.sp.web.assessment.processing.AnalysisBeanDTO", "accuracy" : "100", "processing" : { "Affective" : "100", "Cognitive" : "0", "Intuitive" : "100", "Orderly" : "0", "External" : "0", "Concrete" : "0", "Internal" : "100", "Spontaneous" : "100" }, "conflictManagement" : { "Accommodate" : "23", "Compromise" : "17", "Compete" : "17", "Avoid" : "23", "Collaborate" : "20" }, "learningStyle" : { "Analytical" : "0", "Global" : "100" }, "motivationWhy" : { "Power" : "40", "Affiliation" : "50", "Compliance" : "60", "RecognitionForEffort" : "60", "Activity" : "50", "AttainmentOfGoals" : "40" }, "motivationHow" : { "SelfAffirmed" : "0", "PrefersProcess" : "100", "Freedom" : "67", "AffirmedByOthers" : "100", "TaskCompletion" : "0", "Consistency" : "33", "ExchangeOfIdeas" : "67", "ReceiveDirection" : "33" }, "motivationWhat" : { "Accomplishment" : "83", "Hygiene" : "17" }, "personality" : { "UnderPressure" : { "segmentScore" : 6323, "segmentGraph" : [ "0.86", "0.43", "0.29", "0.43" ], "isValid" : true, "personalityType" : "Visionary" }, "PerceivedByOthers" : { "segmentScore" : 6424, "segmentGraph" : [ "0.86", "0.57", "0.29", "0.57" ], "isValid" : true, "personalityType" : "Visionary" }, "Primary" : { "segmentScore" : 5435, "segmentGraph" : [ "0.71", "0.57", "0.43", "0.71" ], "isValid" : true, "personalityType" : "Navigator" } }, "fundamentalNeeds" : { "Control" : "30", "Significance" : "38", "Security" : "32" }, "decisionMaking" : { "Careful" : "67", "Inward" : "92", "Outward" : "8", "Rapid" : "33" } }, "roles" : [ "BillingAdmin" ], "gender" : "F", "dob" : ISODate("1991-02-05T23:00:00Z"), "tagList" : [ "Asdf" ], "profileSettings" : { "isHiringAccessAllowed" : false, "is360ProfileAccessAllowed" : false, "isWorkspacePulseAllowed" : false, "token" : "6fe20bf5-26c2-48ed-ae22-0c43de6e67fe", "certificateProfilePublicView" : false, "autoUpdateLearning" : true }, "userGoalId" : "5603a389d4c607583a1c91c2", "tokenUrl" : "http://ec2-52-10-90-53.us-west-2.compute.amazonaws.com/sp/processToken/6cec3b5d-d38d-4fe4-8e59-60e8aa1dd744", "imageCount" : 0, "certificateNumber" : "60" })



/* Create Aduthentication user for mongo */
use admin;
db.createUser({user:"admin",pwd:"admin",roles:[{role:"userAdminAnyDatabase",db:"admin"}]});
use sp;
db.createUser({user:"spmongouser",pwd:"surepeople",roles:[{role:"readWrite",db:"sp"}]});

use sp-archive;
db.createUser({user:"spmongouser",pwd:"surepeople",roles:[{role:"readWrite",db:"sp-archive"}]});

use sp-deleted;
db.createUser({user:"spmongouser",pwd:"surepeople",roles:[{role:"readWrite",db:"sp-deleted"}]});

// COnnecting to mongo : command : mongo -u spmongouser -p surepeople -authenticationDatabase sp

db.pulseQuestionSet.ensureIndex({
	name : 1,
}, {
	unique : true
});

db.sequence.update({_id:"orderNo"}, {$set:{promoCodeSeq : 0}});

db.trainingLibraryHomeArticle.ensureIndex({
	"companyId" : 1
});

db.trainingLibraryHomeArticle.ensureIndex({
	articleId : 1,
	articlePosition : 2,
	companyId: 3
}, {
	unique : true
});

db.actionPlan.ensureIndex({
	name : 1,
	companyId : 2,
}, {
	unique : true
});

db.blueprintBackup.ensureIndex({
	blueprintId : 1
}, {
	unique : true
});

db.sPGoal.dropIndex("name_1");

db.competencyProfile.ensureIndex({
	name:1,
	companyId:2
},{
	unique : true
})

db.tipOfTheDay.insert({title:"Tip of the day", description: "Since you're working so diligently at [THEME], have you remembered to set up a Growth Team to track your progress and keep you accountable?", linkText: "SET UP GROWTH TEAM", linkUrl:"[baseUrl]sp/growth/growthListing", iconImage : "[baseUrl]images/Email_Growth.jpg"});
db.tipOfTheDay.insert({title:"Tip of the day", description: "Don't forget to put your knowledge to the test by using the Relationship Advisor with another member to get helpful tips for communicating with them.", linkText: "VISIT RELATIONSHIP ADVISOR", linkUrl:"[baseUrl]sp/prism/relationship-advisor", iconImage : "[baseUrl]images/Email_Advisor.jpg"});
db.tipOfTheDay.insert({title:"Tip of the day", description: "Make sure to find out how others view you by requesting them to do a PRISM 360 assessment for you.", linkText: "REQUEST PRISM 360", linkUrl:"[baseUrl]sp/feedback", iconImage : "[baseUrl]images/Email_Prism360.jpg"});


db.developmentStrategy.insert({"_class":"com.sp.web.model.goal.DevelopmentStrategy","description":"Accept the reality that you may not be good with details and may need the help of others.","isActive":true});
db.developmentStrategy.insert({"_class":"com.sp.web.model.goal.DevelopmentStrategy","description":"Ask for feedback from others on what you’ve communicated to Solicit help from a detail-oriented associate to considerwhat you might be overlooking; learn from how that person does things.","isActive":true});
db.developmentStrategy.insert({"_class":"com.sp.web.model.goal.DevelopmentStrategy","description":"few words what you want others to understand; start with the end result you desire, then cover the strategy to get Identify areas where you need to especially pay close attention to the details; focus intently and more detail-focused on those topics.","isActive":true});

/*
db.personalityPracticeArea.update({personalityType:"Supporter"}, {$set:{swotProfileMap:{"Strengths":["Personable","Dependable","Considerate","Competent","Calm","Loyal"],"Weakness":["Passive","Resistant to change","Sensitive","Avoids personal conflict","Risk-averse"],"Opportunities":["Highly focused","Follows through","Values team relationships","Mediates conflict","Pleasant and positive attitude","Adapts to the needs of work associates"],"Threats":["Conceals grievances","Does not adapt well to abrupt change","Prefers predicability","Resents aggressive personalities","Maintains status quo","Overly sensitive"]}}})
db.personalityPracticeArea.update({personalityType:"Ambassador"}, {$set:{swotProfileMap:{"Strengths":["Caring","Empathetic","Approachable","Dependable","Supportive","Positive"],"Weakness":["Disorganized","Overlooks details","Easily distracted","Avoids resolving conflict","Can be inaccurate"],"Opportunities":["Influential team player","Creates a positive working environment","Networks well","Exhibits a democratic leadership style","Seeks opportunities to provide support"],"Threats":["Has trouble making and meeting deadlines","Can lose focus on a given task","Overly tolerant of unproductive coworkers","Avoids confrontation","Does not work well under stress","May resent aggressive personalities"]}}})
db.personalityPracticeArea.update({personalityType:"Refiner"}, {$set:{swotProfileMap:{"Strengths":["Analytical","Detailed","Organized","Accurate","Dependable","Cooperative"],"Weakness":["Very task-oriented","Reserved","Can be overly cautious","Pessimistic","Judgmental","Picky"],"Opportunities":["Maintains high standards","Excels in work that requires precision","Plans well","Accurate","Dependable","Cooperative"],"Threats":["Does not delegate well","Tends to micromanage","Can be inflexible","Has trouble making quick decisions","Critical of others"]}}})
db.personalityPracticeArea.update({personalityType:"Motivator"}, {$set:{swotProfileMap:{"Strengths":["Confident","Positive","Persuasive","Enthusiastic","Independent","Energetic"],"Weakness":["Disorganized","Inaccurate","Can be self-centered","Misses details","Talks more than listens"],"Opportunities":["Highly influential and persuasive","Exhibits strong communication skills","An effective manager who balances relationships with tasks","Embraces change and new situations","Thrives on taking calculated risks","Helps others focus on the big picture"],"Threats":["Performs poorly in fixed environments","Can come across as inconsiderate","Often overlooks details","May resist working on weaknesses","Often disorganized","Often inaccurate with specific facts"]}}})
db.personalityPracticeArea.update({personalityType:"Promoter"}, {$set:{swotProfileMap:{"Strengths":["Optimistic","Positive","Persuasive","Enthusiastic","Social","Good listener"],"Weakness":["Can lose sight of the end goal","Disorganized","Overlooks details","Doesn't like routine"],"Opportunities":["Networks well","Highly influential","Exhibits strong communication skills","Creates positive relationships","Has a knack for relieving tension"],"Threats":["Becomes bored with detailed work","Can lose sight of tasks and objectives","Often disorganized and easily distracted","Can be inaccurate and exaggerate facts","Can overlook key details","Overly optimistic"]}}})
db.personalityPracticeArea.update({personalityType:"Examiner"}, {$set:{swotProfileMap:{"Strengths":["Persuasive","Enthusiastic","Analytical","Organized","Accurate","Influential"],"Weakness":["Can be impatient and critical under pressure","Competitive","Switches between being logical and emotional","Has high standards for self and others","Too indirect","Tends to be impatient"],"Opportunities":["Mobilizes others effectively","Diplomatic in social and business interactions","Possesses strong persuasive skills","Expresses opinions and critique","Communicates the big picture well","Involves others in tasks and projects"],"Threats":["Tends to lack follow-through","May take things personally","Often focuses too much on details","Can come across as rigid and inflexible","Can be insensitive when disagreeing with others"]}}})
db.personalityPracticeArea.update({personalityType:"Instructor"}, {$set:{swotProfileMap:{"Strengths":["Confident","Independent","Direct","Influential","Takes charge","Results-oriented"],"Weakness":["Not detail-oriented","Dominates other personalities","Prone to be argumentative","Passive aggressive under pressure","Disorganized"],"Opportunities":["Enjoys leadership roles and executes well","Mobilizes and inspires others","Welcomes challenges","Values open discussion and honesty","Tolerates pressure well","Has an entrepreneurial attitude"],"Threats":["Poor finisher","Overlooks details","Tends to be a poor listener","Can appear insensitive and aggressive","Struggles to stay organized","Can overwhelm others"]}}})
db.personalityPracticeArea.update({personalityType:"Navigator"}, {$set:{swotProfileMap:{"Strengths":["Detailed","Confident","Results-oriented","Natural problem solver","Independent","Analytical"],"Weakness":["Critical of self and others","Overly serious","Can appear aloof","Has incredibly high standards","Independent","Thorough"],"Opportunities":["Improves efficiency","Analyzes and initiates changes","Synthesizes facts and concepts for long-range strategies","A natural innovator and creative thinker","Gets things done","Notices details and nuances that others miss"],"Threats":["Can be critical and stubborn","Is often territorial","Can be demanding and overbearing","May isolate from the team when irritated or restrained","Tends to prioritize the goal over relationships","May create distance by being blunt and aloof"]}}})
db.personalityPracticeArea.update({personalityType:"Encourager"}, {$set:{swotProfileMap:{"Strengths":["Empathetic","Considerate","Diplomatic","Good listener","Problem solver","Patient"],"Weakness":["Nonconfrontational","Often easily offended","Can be reserved","Grows bored with a lack of variety","Often too trusting","Overly sensitive"],"Opportunities":["Desires to help others succeed in their roles","Welcomes spontaneity","Highly influential","Creates a positive and productive environment","Recognizes strengths and achievements of others","Maintains long-term relationships"],"Threats":["Avoids confrontation","Can be passive and indirect","May lose sight of tasks and objectives","May be lenient with unproductive coworkers","Dislikes giving direct orders","Dislikes rigid deadlines"]}}})
db.personalityPracticeArea.update({personalityType:"Designer"}, {$set:{swotProfileMap:{"Strengths":["Confident","Problem solver","Ambitious","Proactive","Persistent","Embraces challenges"],"Weakness":["Blunt","Forceful","Can appear insensitive","Aggressive","May come across as self-centered","Overly competitive"],"Opportunities":["Has a take-charge attitude","Rallies others toward a goal","Finds a way to succeed","Strong communication skills","Empowers coworkers","Takes on challenging opportunities"],"Threats":["Does not work well with direct control","Adversive to routine work and excessive details","Can overstep boundaries","May resist being a team player","Can be critical of others","Impatient if the work pace is too slow","May appear uncaring and forceful"]}}})
db.personalityPracticeArea.update({personalityType:"Pragmatist"}, {$set:{swotProfileMap:{"Strengths":["Good communicator","Conscientious","Dependable","Self-disciplined","Accurate","Logical"],"Weakness":["Perfectionistic","Comes across as a know-it-all","Sensitive to the perception of others","Easily disappointed","Can be impatient and critical","Vacillates between being logical and emotional"],"Opportunities":["Produces creative ideas and solutions","Can talk about anything with anyone","Can create and maintain a positive atmosphere","Thrives in a structured environment","Influences and involves colleagues","Develops proficiency in areas of specialization"],"Threats":["Can have unrealistic expectations of self and others","Prone to deliberate when making decisions","Tends to obsess over details","Resists change unless presented with specific data","Easily hurt when overlooked or unappreciated"]}}})
db.personalityPracticeArea.update({personalityType:"Researcher"}, {$set:{swotProfileMap:{"Strengths":["Accurate","Tenacious","Self-starter","Self-disciplined","Persistent","Objective"],"Weakness":["Very task-driven","Can be opinionated and stubborn","Holds very high standards","Prefers working alone","Can be hard to please"],"Opportunities":["Values facts and logic when making decisions","Enjoys difficult problems that require analysis","Focuses on one project and follows through","Explores all possible solutions","Is straightforward in communication","Adopts a systematic approach to work"],"Threats":["Tends to be blunt","Prone to interalize conflict and hold grudges","Uncomfortable in large groups","Dissatisfied with anything less than perfect","Can come across as judgmental and inflexible","Has a strong preference to work alone"]}}})
db.personalityPracticeArea.update({personalityType:"Actuary"}, {$set:{swotProfileMap:{"Strengths":["Conscientious","Analytical","Accurate","Logical","Polite","Independent"],"Weakness":["Perfectionistic","Internalizes conflict","Resistant to change","Slow to adapt","Risk-averse"],"Opportunities":["Detail-oriented","Produces quality work","Highly organized","Relies on facts and data","Thinks and acts objectively","Maintains a peaceful working environment"],"Threats":["Works at a slow pace","Can be critical of colleagues","Neglects work relationships","Holds others to unspoken standards","Resistant to new and untested ideas","Inflexible at times"]}}})
db.personalityPracticeArea.update({personalityType:"Innovator"}, {$set:{swotProfileMap:{"Strengths":["Confident","Independent","Influential","Welcomes challenges","Entrepreneurial","Independent"],"Weakness":["Stubborn","Excessively goal-oriented","Disorganized","Prone to jealousy","Overlooks details","Argumentative"],"Opportunities":["Inspires others toward a desired goal","Unafraid to try something new","Thrives under pressure","Flexible and innovative in achieving results","Keeps the big picture in mind","Welcomes challenges and obstacles"],"Threats":["Poor finisher","Can become argumentative under pressure","May become passive aggressive","Often disorganized and overlooks key details","Goal-oriented personality can exhaust colleagues","May charge ahead despite the advice of others"]}}})
db.personalityPracticeArea.update({personalityType:"Tough_N_Tender"}, {$set:{swotProfileMap:{"Strengths":["Persistent","Ambitious","Industrious","Personable","Direct"],"Weakness":["Blunt","Obstinate","Misses details","Unpredictable","Prefers to work independently"],"Opportunities":["Objective in analyzing problems","Strong commitment to goals","Holds high standards of quality and performance","Can be both results-oriented and people-focused","Consistently practical and data-driven"],"Threats":["Struggles to delegate responsibilities","Has difficulty balancing tasks with relationships","Tends to be stubbornly self-reliant","Prone to be frustrated and impatient with others","Often views changes with skepticism"]}}})
db.personalityPracticeArea.update({personalityType:"Visionary"}, {$set:{swotProfileMap:{"Strengths":["Forward-thinking","Ambitious","Innovative","Welcomes challenges","Authoritative"],"Weakness":["Can appear insensitive","Intimidating","Prefers to work alone","May have control issues"],"Opportunities":["Defines clear goals for the team","Delegates tasks","Unafraid to try new and innovative approaches","Tackles challenging situations"],"Threats":["Tends to be blunt and critical","A strong goal orientation at the expense of methodology","Feels confined by routine","Thrives in challenging situations","Highly authoritative"]}}})
*/

/* LENS Content */
db.personalityPracticeArea.update({personalityType:"Supporter"}, {$set:{swotProfileMap:{"Opportunities":["Maintain a high level of focus","Follow through"," Place value on team relationships","Mediate conflict","Keep a pleasant and positive attitude","Adapt to the needs of work associates"],"Threats":["Speak up","Speak with purpose","Be assertive","Take risks","Give feedback to others","Embrace changes","Seek feedback for yourself","Manage differences with others","Remain flexible"],"Weakness":["Passivity","Resistance to change","Sensitivity","Avoiding personal conflict","Risk-aversion","Concealing grievances","Maintaining the status quo"],"Strengths":["Prevail by being personable","Remain dependable","Advocate for others to be considerate","Showcase competency","Use calmness to ease others","Use loyalty effectively"]}}})
db.personalityPracticeArea.update({personalityType:"Ambassador"}, {$set:{swotProfileMap:{"Opportunities":["Be an influential team player","Create a positive working environment","Network productively","Exercise a democratic leadership style","Seek opportunities to provide support"],"Threats":["Pay attention to detail","Use forethought and planning","Manage differences with others","Manage time effectively","Give feedback to others","Seek feedback for yourself","Complete tasks","Be assertive","Speak up"],"Weakness":["Disorganization","Overlooking details","Distraction","Avoiding conflict resolution","Inaccuracies","Losing focus","Tolerance for unproductive workers","Missing deadlines"],"Strengths":["Empower others through caring","Use empathy strategically","Persist in being approachable","Remain dependable","Prevail by being supportive","Stay positive"]}}})
db.personalityPracticeArea.update({personalityType:"Refiner"}, {$set:{swotProfileMap:{"Opportunities":["Maintain high standards","Excel in work that requires precision","Use planning well","Be highly accurate","Be dependable","Be cooperative"],"Threats":["Take risks","Remain flexible","Give feedback to others","Drive action","Embrace changes","Manage emotions","Release control","Seek feedback for yourself","Delegate","Seek opportunities for personal fulfillment"],"Weakness":["Being overly task-oriented","Being overly reserved","Being overly cautious","Being overly critical","Pessimism","Passing judgment","Pickiness","Inflexibility","Micromanaging"],"Strengths":["Embrace being analytical","Enhance being attentive to detail","Help others to get organized","Use fine-tuned accuracy","Remain dependable","Embody cooperation"]}}})
db.personalityPracticeArea.update({personalityType:"Motivator"}, {$set:{swotProfileMap:{"Opportunities":["Be influential and persuasive","Use strong communication skills","Effectively leading by balancing relationships with tasks","Embrace change and new situations","Take calculated risks","Help others focus on the big picture"],"Threats":["Pay attention to detail","Actively listen","Show consideration","Use forethought and planning","Seek feedback for yourself","Manage differences with others","Manage time effectively","Complete tasks","Manage emotions","Build stability for yourself"],"Weakness":["Disorganization","Inaccuracies","Being self-centered","Missing details","Talking more than listening","Being inconsiderate","Resistance to personal change"],"Strengths":["Embrace confidence","Help others to be positive","Take advantage of being persuasive","Use enthusiasm effectively","Use independence well","Harness the power of being energetic"]}}})
db.personalityPracticeArea.update({personalityType:"Promoter"}, {$set:{swotProfileMap:{"Opportunities":["Network productively","Be influential","Use strong communication skills","Create positive relationships","Relieve tension"],"Threats":["Pay attention to detail"," Actively listen","Focus","Complete tasks","Manage time effectively","Give feedback to others","Seek feedback for yourself","Use forethought and planning","Use logic and rationale"],"Weakness":["Losing sight of the end goal","Disorganization","Overlooking details","Not embracing routines","Being overly optimistic","Inaccuracies or exaggerations","Boredom with detailed work","Distraction"],"Strengths":["Stay optimistic","Help others to be positive","Take advantage of being persuasive","Use enthusiasm effectively","Embrace being social","Develop listening skills in others"]}}})
db.personalityPracticeArea.update({personalityType:"Examiner"}, {$set:{swotProfileMap:{"Opportunities":["Mobilize others effectively","Practice diplomacy in interactions","Use strong persuasive skills","Express opinions and critique","Communicate the big picture","Involve others in tasks and projects"],"Threats":["Balance work & life","Get to the point quickly","Seek feedback for yourself","Manage differences with others","Give feedback to others","Set expectations with yourself and others","Manage emotions","Be aware of others' needs","Practice appreciation & recognition","Complete tasks"],"Weakness":["Being critical when under pressure","Competitiveness","Switching between logic and emotion","Having standards that are too high","Being indirect","Impatience","Taking things personally","Not focusing on the big picture","Not following through","Inflexibility"],"Strengths":["Be persuasive","Stay enthusiastic","Embrace being analytical","Help others to get organized","Use fine-tuned accuracy","Take advantage of being influential"]}}})
db.personalityPracticeArea.update({personalityType:"Instructor"}, {$set:{swotProfileMap:{"Opportunities":["Effectively lead and execute well","Mobilize and inspire others","Welcome challenges","Facilitate open discussions and honesty","Thrive under pressure","Keep an entrepreneurial attitude"],"Threats":["Pay attention to detail","Actively listen","Be compassionate with others","Seek feedback for yourself","Work on relationship building","Engage in teamwork","Use forethought and planning","Manage time effectively","Complete tasks"],"Weakness":["Overlooking details","Dominating other personalities","Arguing","Passive aggression under pressure","Disorganization","Insensitivity","Aggression","Overwhelming others","Not listening"],"Strengths":["Embrace confidence","Use independence well","Use directness responsibly","Take advantage of being influential","Take charge and drive action","Stay results-oriented"]}}})
db.personalityPracticeArea.update({personalityType:"Navigator"}, {$set:{swotProfileMap:{"Opportunities":["Improve efficiency","Analyze and initiate changes","Synthesize facts and concepts for long-range strategies","Innovate through creative thinking","Get things done","Point out details and nuances that others miss"],"Threats":["Engage in teamwork","Remain flexible","Work on relationship building","Show consideration to others","Practice appreciation & recognition","Take risks","Seek opportunities for personal fulfillment","Find compromises with others","Seek feedback for yourself"],"Weakness":["Being overly critical","Stubbornness","Being territorial","Bluntness","Being overly serious","Appearing detached","Demanding and being overbearing","Isolation when irritated or restrained","Prioritization of goals over relationships"],"Strengths":["Enhance being attentive to detail","Embrace confidence","Stay results-oriented","Be the problem solver","Use independence well","Be strategically analytical"]}}})
db.personalityPracticeArea.update({personalityType:"Encourager"}, {$set:{swotProfileMap:{"Opportunities":["Help others succeed in their roles","Be spontaneous","Be highly influential","Create a positive and productive environment","Recognize the strengths and achievements of others","Maintain long-term relationships"],"Threats":["Use forethought and planning","Manage time effectively","Pay attention to detail","Complete tasks","Make well informed decisions","Manage differences with others","Seek feedback for yourself","Set expectations with others","Delegate"],"Weakness":["Avoiding confrontation","Becoming easily offended","Being reserved","Boredom from lack of variety","Trusting too much","Sensitivity","Losing sight of task and objectives","Passivity and indirectness","Not delegating","Leniency"],"Strengths":["Use empathy strategically","Advocate for others to be considerate","Encourage diplomacy","Develop listening skills in others","Be the problem solver","Help others to be patient"]}}})
db.personalityPracticeArea.update({personalityType:"Designer"}, {$set:{swotProfileMap:{"Opportunities":["Maintain a take-charge attitude","Rally others toward a goal","Find a way to succeed","Utilize strong communication skills","Empower coworkers","Take on challenging opportunities"],"Threats":["Show consideration to others","Give feedback to others","Be aware of others' needs","Engage in teamwork","Find compromises with others","Seek feedback for yourself","Find opportunities for personal fulfillment","Make well informed decisions","Drive action"],"Weakness":["Being overly critical and blunt","Uncaring and being forceful","Insensitivity","Aggression","Self-centeredness","Competitiveness","Resistance to being a team player","Impatience","Overstepping boundaries","Aversion to routine and details"],"Strengths":["Embrace confidence","Be the problem solver","Capitalize on ambition","Encourage others to be proactive","Stay persistent","Embrace challenges openly"]}}})
db.personalityPracticeArea.update({personalityType:"Pragmatist"}, {$set:{swotProfileMap:{"Opportunities":["Produce creative ideas and solutions","Be sociable","Create and maintain a positive atmosphere","Thrive in structured environments","Influence and involve colleagues","Develop proficiency in areas of specialization"],"Threats":["Take risks","Make well informed decisions","Drive action","Focus","Manage time effectively","Seek feedback for yourself","Give feedback to others","Manage differences with others","Work on relationship building","Practice using appreciation & recognition"],"Weakness":["Perfectionism","Appearing as a know-it-all","Sensitivity to the perception of others","Disappointment","Impatience and criticalness","Switching between logic and emotion","Deliberating too much","Obsessing over details","Resisting change","Unrealistic expectation of self and others"],"Strengths":["Strategically communicate","Be conscientious","Capitalize on being dependable","Perfect self-discipline","Use fine-tuned accuracy","Keep Integrating logic"]}}})
db.personalityPracticeArea.update({personalityType:"Researcher"}, {$set:{swotProfileMap:{"Opportunities":["Use facts and logic when making decisions","Engage difficult problems that require analysis","Focus on one project and follow through","Explore all possible solutions","Remain straightforward in communication","Employ a systematic approach to work"],"Threats":["Remain flexible","Embrace changes","Balance work & life","Manage differences with others","Find opportunities for personal fulfillment","Be accepting of others","Practice using appreciation & recognition","Be persuasive","Work on relationship building"],"Weakness":["Overly task-orientated","Opinionation and stubbornness","High standards","Isolation","Being too blunt","Internalizing conflict and holding grudges","Perfectionism","Inflexibility","Judgement"],"Strengths":["Use fine-tuned accuracy","Exhibit tenacity","Keep being a self-starter","Perfect self-discipline","Stay persistent","Remain objective"]}}})
db.personalityPracticeArea.update({personalityType:"Actuary"}, {$set:{swotProfileMap:{"Opportunities":["Be detail-oriented","Keep producing quality work","Stay highly organized","Rely on facts and data","Think and act objectively","Maintain a peaceful working environment"],"Threats":["Employ strategic thinking","Be compassionate to others","Find compromises with others","Make well informed decisions","Manage differences with others","Drive action","Manage emotions","Give feedback to others","Seek feedback for yourself"],"Weakness":["Perfectionism","Internalizing conflict","Resistance to change","Slow adaptation","Risk-aversion","Slow pacing","Criticalness of others","Neglecting relationships","Resistance to new ideas","Inflexibility"],"Strengths":["Be conscientious","Embrace being analytical","Use fine-tuned accuracy","Keep integrating logic","Capitalize on politeness","Use independence well"]}}})
db.personalityPracticeArea.update({personalityType:"Innovator"}, {$set:{swotProfileMap:{"Opportunities":["Inspire others toward a desired goal","Stay unafraid to try something new","Perform well under pressure","Remain flexible and innovative in achieving results","Keep the big picture in mind","Welcome challenges and obstacles"],"Threats":["Pay attention to detail","Actively listen","Engage in teamwork","Seek feedback for yourself","Be compassionate with others","Build stability","Work on relationship building","Manage time effectively","Complete tasks","Use forethought and planning"],"Weakness":["Stubbornness","Excessive goal-orientation","Disorganization","Proneness to jealousy","Overlooking details","Arguing","Passive aggression","Not seeking feedback","Not following through","Overwhelming others"],"Strengths":["Embrace confidence","Use independence well","Take advantage of being influential","Welcome challenges openly","Keep an entrepreneurial attitude"]}}})
db.personalityPracticeArea.update({personalityType:"Tough_N_Tender"}, {$set:{swotProfileMap:{"Opportunities":["Be objective in analyzing problems","Keep a strong commitment to goals","Hold high standards for quality and performance","Thrive on being both results-oriented and people-focused","Be consistently practical and data-driven"],"Threats":["Seek feedback for yourself","Give feedback to others","Manage differences with others","Release control","Be receptive to new ideas","Remain flexible","Embrace changes","Balance work & life","Work on relationship building"],"Weakness":["Bluntness","Stubbornness","Overlooking details","Unpredictableness","Isolation","Self-reliance","Viewing change with skepticism","Not balancing tasks with relationships","Impatience and frustration with others"],"Strengths":["Be persistent","Capitalize on ambition","Stay diligent","Prevail by being personable","Use directness responsibly"]}}})
db.personalityPracticeArea.update({personalityType:"Visionary"}, {$set:{swotProfileMap:{"Opportunities":["Define clear goals for the team","Delegate tasks effectively","Stay unafraid to try new and innovative approaches","Mobilize and inspire others","Facilitate open discussions and honesty","Keep an entrepreneurial attitude"],"Threats":["Focus on team building","Engage in teamwork","Release control","Find compromises with others","Be compassionate with others","Practice appreciation & recognition","Show empathy to others","Be patient","Delegate","Manage differences with others"],"Weakness":["Insensitivity","Intimidating others","Working alone","Need for control","Being overly authoritative","Ignoring methodology","Being too blunt and criticism","Feeling confined by routine"],"Strengths":["Keep pushing forward","Capitalize on ambition","Keep innovating","Welcome challenges","Use authority responsibly","Tackle challenging situations head-on"]}}})

//Default Themes Update
db.company.update({},{$set:{"companyTheme" : { "stylesMap" : {"spMainNavBackgroundColor" : "#000", "spIconColor" : "#f63", "spHeaderAccentbarColor" : "#f63", "spSigninAccentbarColor" : "#f63", "spLinkColor" : "#f63", "spHeaderTitleColor" : "#f63", "spFooterBackgroundColor" : "#666", "spButtonColor" : "#f63", "spButtonHoverColor" : "#cc3300", "spLinkHoverColor" : "#000", "spMainNavColor" : "#ffa200", "spPanelAccentbarColor" : "#cdcccb", "spSubNavLinkColor" : "#000", "spSubNavBackgroundColor" : "#f9b858", "spFooterNavigationTextColor" : "#fff", "spFooterNavigationHoverColor" : "#000" }, "cssUrl" : "{0}/theme/spappdefault/default/6/default.css", "themeKey" : "surepeople" }, "companyThemeActive" : false}},{multi:true})

db.publicChannel.ensureIndex({
	"pcRefId" : 1,
	"companyId" : 2
}, {
	unique : true
})

db.companyActionPlanSettings.ensureIndex({
	"companyId" : 1,
	"actionPlanId" : 2
}, {
	unique : true
})

db.actionPlan.dropIndex("name_1_companyId_2");

db.userNotificationsSummary.ensureIndex({
	"userId" : 1,
}, {
	unique : true
})

db.userDevelopmentFeedbackResponse.ensureIndex({
	"userId" : 1,
}, {
	unique : true
})

db.userTutorialActivity.ensureIndex({
	"userId" : 1,
}, {
	unique : true
})

// Adding scripts for People Analytics
db.hiringGroup.ensureIndex({
	"companyId" : 1,
	"name" : 2
}, {
	unique : true
})

db.hiringRole.ensureIndex({
	"companyId" : 1,
	"name" : 2
}, {
	unique : true
})

db.hiringUserArchive.ensureIndex({
	"companyId" : 1,
	"email" : 2
}, {
	unique : true
})

db.hiringPortrait.ensureIndex({
	"name" : 1
}, {
	unique : true
})

db.userCompetency.ensureIndex({
	"userId" : 1,
	"companyId":2
}, {
	unique : true
})

db.competencyEvaluation.createIndex({
	"companyId" : 1,
	"endedOn":1
})

//---- Optimisation ---
db.userBadgeActivity.ensureIndex({
	"userId" : 1
}, {
	unique : true
})

db.userActivityTracking.ensureIndex({
	"userId" : 1,
	"date":2
}, {
	unique : true
})
