/**
 *  Copyright (c) 2015, CoNWeT Lab., Universidad Politécnica de Madrid
 *  Code licensed under BSD 3-Clause (https://github.com/conwetlab/WMarket/blob/master/LICENSE)
 */

WMarket.alerts = (function () {

    "use strict";

    var AlertManager = {

        'info': function info(messageContent, extraClass) {
            var defaultIcon = $('<span>').addClass("fa fa-info-circle");

            return createAlert('info', defaultIcon, messageContent, extraClass);
        },

        'warning': function warning(messageContent, extraClass) {
            var defaultIcon = $('<span>').addClass("fa fa-exclamation-circle");

            return createAlert('warning', defaultIcon, messageContent, extraClass);
        }

    };

    var createAlert = function createAlert(type, icon, content, extraClass) {
        var alert = $('<div>').addClass('alert alert-' + type);

        return alert.addClass(extraClass).append(icon, " ", content);
    };

    return AlertManager;

})();
