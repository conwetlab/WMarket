/**
 * Copyright (c) 2015, CoNWeT Lab., Universidad Politécnica de Madrid
 * Code licensed under BSD 3-Clause (https://github.com/conwetlab/WMarket/blob/master/LICENSE)
 */

(function (ns) {

    "use strict";

    var baseArgs = {
        store: ns.storeName,
        description: ns.descriptionName,
        offering: ns.offeringName
    };

    ns.model = 'offering';

    ns.urls = {
        entry: app.urls.get('offering:entry', baseArgs),
        review_collection: app.urls.get('offering:entry:review:collection', baseArgs),
        review_entry: app.urls.get('offering:entry:review:entry', baseArgs)
    };

    ns.$ratingOverall = $('.rating-overall > .fa-star');
    ns.$reviewList = $('.offering-reviews');

    ns.find = function find(next) {
        app.requests.find(ns.urls.entry, {
            success: next
        });
    };

    ns.btnToggleBookmark = $('.btn-toggle-bookmark');

    ns.toggleBookmark = function toggleBookmark() {
        app.requests.create({
            namespace: "offerings:entry_bookmark",
            kwargs: {
                descriptionName: ns.descriptionName,
                storeName: ns.storeName,
                offeringName: ns.offeringName
            },
            onSuccess: function () {
                if (ns.btnToggleBookmark.hasClass('tab-danger')) {
                    ns.btnToggleBookmark
                        .removeClass('tab-danger')
                        .find('.hidden-sm')
                        .text('Add bookmark');
                } else {
                    ns.btnToggleBookmark
                        .addClass('tab-danger')
                        .find('.hidden-sm')
                        .text('Remove bookmark');
                }
            }
        });
    };

})(app.view);
