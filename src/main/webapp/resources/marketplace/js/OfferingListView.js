/**
 *  Copyright (c) 2015, CoNWeT Lab., Universidad Politécnica de Madrid
 *  Code licensed under BSD 3-Clause (https://github.com/conwetlab/WMarket/blob/master/LICENSE)
 */


$(function () {

    "use strict";

    WMarket.layout.toggleFilters.attr('disabled', false);

    WMarket.layout.toggleFilters.on('click', function (event) {
        event.preventDefault();

        if (this.classList.contains('active')) {
            this.classList.remove('active');
            WMarket.layout.menuFilters.removeClass('active');
        } else {
            this.classList.add('active');
            WMarket.layout.menuFilters.addClass('active');
        }

        event.stopPropagation();
    });

    WMarket.requests.read({
        namespace: "offerings:collection",
        containment: WMarket.layout.offeringList,
        alert: WMarket.alerts.warning("No offering available.", 'col-sm-10 col-md-8 col-lg-6'),
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

    WMarket.requests.read({
        namespace: "stores:collection",
        containment: WMarket.layout.storeList,
        alert: WMarket.alerts.warning("No web store available."),
        onSuccess: function (collection, containment) {
            var i, store;

            for (i = 0; i < collection.length; i++) {
                store = new Store(collection[i]);
                containment.append(store.element);
            }
        },
        onFailure: function () {
            // TODO: code that identify what error was occurred.
        }
    });

});
