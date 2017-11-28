/* global jQuery */

jQuery(function($) {
    'use strict';

    var alertConfig = {
        alerts: [{
            code: "404",
            message: "<h2>We’re sorry, this link has expired.<\/h2><p>If you need further assistance, please contact us at <a href=\\'mailto:support@surepeople.com\\' target=\\'_blank\\'>support@surepeople.com<\/a>.<\/p>",
            timeout: 0,
            style: "error-alert"
        }, {
            code: "500",
            message: "<h2>The server encountered an unexpected condition which prevented it from fulfilling the request.<\/h2><p>If you need further assistance, please contact us at <a href=\\'mailto:support@surepeople.com\\' target=\\'_blank\\'>support@surepeople.com<\/a>.<\/p>",
            timeout: 0,
            style: "error-alert"
        }, {
            code: "success",
            message: "<h2>Thank you.<\/h2><p>Your request has been processed.<\/p>",
            timeout: 3000,
            style: "success-alert"
        }, {
            code: "growthDecline",
            message: "<h2>Thank you for your consideration<p>In being a growth team member.<\/p><\/h2>",
            timeout: 0,
            style: "error-alert"
        }, {
            code: "cookies",
            message: "<h2>Cookies on your browser are disabled!<p><\/h2><p>Please enable them in order to register/login.<\/p><\/h2>",
            timeout: 0,
            style: "error-alert"
        }, {
            code: "privateBrowsing",
            message: "<h2>Private Browsing is enabled.<p><\/h2><p>Please disable to register/login.<\/p><\/h2>",
            timeout: 0,
            style: "error-alert"
        }, {
            code: "feedbackDecline",
            message: "<h2>Thank you for your consideration.<\/h2><p>In providing development feedback.<\/p>",
            timeout: 0,
            style: "error-alert"
        }, {
            code: "feedbackAlreadyDecline",
            message: "<h2>Feedback has already been declined.<\/h2><p>This link has expired.<\/p>",
            timeout: 0,
            style: "error-alert"
        }, {
            code: "feedbackTokenUsed",
            message: "<h2>Feedback already provided.<\/h2><p>If you need further assistance, please contact us at <a href=\\'mailto:support@surepeople.com\\' target=\\'_blank\\'>support@surepeople.com<\/a>.<\/p>",
            timeout: 0,
            style: "error-alert"
        }]
    };


    function checkCookie() {
        console.log("checkCookie called");
        var cookieEnabled = (navigator.cookieEnabled) ? true : false;
        if (typeof navigator.cookieEnabled == "undefined" && !cookieEnabled) {
            document.cookie = "testcookie";
            cookieEnabled = (document.cookie.indexOf("testcookie") != -1) ? true : false;
        }
        console.log("cookieEnabled: " + cookieEnabled);
        return (cookieEnabled) ? true : false;
    }

    function closeAlert() {
        $(".alert").slideUp(500, function() {
            $(this).removeClass("active");
        });
    }

    function openAlert(status) {
        var max = alertConfig.alerts.length;

        for (var i = 0; i < max; i++) {
            var obj = alertConfig.alerts[i];

            if (status == obj.code) {

                $(".alert").addClass(obj.style);
                $(".alert .message").html(obj.message);
                $(".alert").addClass("active");
                $(".alert").fadeIn();
                if (obj.timeout > 0) {
                    window.setTimeout(closeAlert, obj.timeout);
                }
            }
        }
    }

    $(".alert .md-close").on('click', function() {
        closeAlert();
    });

    var statusCode = getQuerystring("status");
    console.log("statusCode: " + statusCode);
    if (statusCode != "" && statusCode != null) {
        openAlert(statusCode);
    } else if (!checkCookie()) {
        openAlert("cookies");
    }

});
