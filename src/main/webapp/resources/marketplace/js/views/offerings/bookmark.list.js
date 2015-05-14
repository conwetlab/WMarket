/**
 *  Copyright (c) 2015, CoNWeT Lab., Universidad Politécnica de Madrid
 *  Code licensed under BSD 3-Clause (https://github.com/conwetlab/WMarket/blob/master/LICENSE)
 */


$(function () {

    "use strict";

    WMarket.requests.attach('stores:collection', 'read', {
        namespace: "offerings:collection",
        queryString: {
            bookmarked: true
        },
        containment: $('#search-results'),
        alert: WMarket.alerts.warning("No bookmark available.", 'col-sm-10'),
        onSuccess: function (collection, containment) {
            collection.forEach(function (offeringInfo) {
                containment.append((new Offering(offeringInfo).element));
            });
        }
    });

});
