/**
 *  Copyright (c) 2015, CoNWeT Lab., Universidad Politécnica de Madrid
 *  Code licensed under BSD 3-Clause (https://github.com/conwetlab/WMarket/blob/master/LICENSE)
 */

WMarket.view = (function () {

    "use strict";

    var view = {};

    view.btnToggleBookmark = $('.btn-toggle-bookmark');

    view.toggleBookmark = function toggleBookmark() {
        WMarket.requests.create({
            namespace: "offerings:entry_bookmark",
            kwargs: {
                descriptionName: WMarket.descriptionName,
                storeName: WMarket.storeName,
                offeringName: WMarket.offeringName
            },
            onSuccess: function (entry) {
                if (view.btnToggleBookmark.hasClass('tab-danger')) {
                    view.btnToggleBookmark
                        .removeClass('tab-danger')
                        .find('.hidden-sm')
                        .text('Add bookmark');
                } else {
                    view
                        .btnToggleBookmark.addClass('tab-danger')
                        .find('.hidden-sm')
                        .text('Remove bookmark');
                }
            }
        });

        return this;
    };

    return view;

})();
