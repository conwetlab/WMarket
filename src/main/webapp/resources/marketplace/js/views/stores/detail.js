/**
 * Copyright (c) 2015, CoNWeT Lab., Universidad Politécnica de Madrid
 * Code licensed under BSD 3-Clause (https://github.com/conwetlab/WMarket/blob/master/LICENSE)
 */

(function (ns) {

    var baseArgs = {
        store: ns.storeName,
        //description: ns.descriptionName,
        //offering: ns.offeringName
    };

    ns.model = 'store';

    ns.urls = {
        entry: app.urls.get('store:entry', baseArgs),
        review_collection: app.urls.get('store:entry:review:collection', baseArgs),
        review_entry: app.urls.get('store:entry:review:entry', baseArgs)
    };

    ns.$ratingOverall = $('.rating-overall > .fa-star');
    ns.$reviewList = $('.store-reviews');

    ns.find = function find(next) {
        app.requests.find(ns.urls.entry, {
            success: next
        });
    };

})(app.view);
