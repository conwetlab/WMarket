/**
 *  Copyright (c) 2015, CoNWeT Lab., Universidad Politécnica de Madrid
 *  Code licensed under BSD 3-Clause (https://github.com/conwetlab/WMarket/blob/master/LICENSE)
 */


$(function () {

    "use strict";

    WMarket.layout.offeringList = $('#search-results');

    WMarket.requests.attach('stores:collection', 'read', {
        namespace: "offerings:collection",
        containment: WMarket.layout.offeringList,
        alert: WMarket.alerts.warning("No offering available.", 'col-sm-10'),
        onSuccess: function (collection, containment) {
            var i, offering;

            for (i = 0; i < collection.length; i++) {
                offering = new Offering(collection[i]);
                containment.append(offering.element);
            }
        },
        onFailure: function () {
            // TODO: code that identify what error was occurred.
        }
    });

});
