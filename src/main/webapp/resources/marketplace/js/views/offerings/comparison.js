/**
 * Copyright (c) 2015, CoNWeT Lab., Universidad Politécnica de Madrid
 * Code licensed under BSD 3-Clause (https://github.com/conwetlab/WMarket/blob/master/LICENSE)
 */


(function (ns, utils) {

    var $offeringList = $('[app-scope="offering-list"]');
    var $container = $('[app-scope="comparison-container"]');

    if ($offeringList.length) {

        ns.offeringController = {

            $scope: $offeringList,

            urls: {
                collection: app.urls.get('offering:collection')
            },

            list: function (next) {
                app.requests.list(this.urls.collection, {
                    $target: this.$scope,
                    $alert: app.createAlert('warning', "Sorry, no available offering."),
                    success: next
                });
            },

            $target: $container,

            rotatingShowcase: null

        };

        function makeSelectable(offering, index) {
            offering.get()
                .addClass('selectable')
            offering.$heading.attr('data-layer-title', "+").on('click', function (event) {
                if (!offering.selected) {
                    offering.selected = true;
                    offering.$heading.attr('data-layer-title', "Added");
                    refreshRotatingShowcase(offering);
                    offering.get().addClass('active');
                }
            });
        }

        function refreshRotatingShowcase(offering) {

            if (!ns.offeringController.$scope.find('.offering-item.active').length) {
                ns.offeringController.rotatingShowcase = new app.components.OfferingRotatingShowcase(ns.offeringController.$target);
            }

            ns.offeringController.rotatingShowcase.addOffering(offering);
        }

        app.requests.attach('stores:collection', function () {
            ns.offeringController.list(function (offerings) {
                var offeringShowcase = new app.components.OfferingShowcase(ns.offeringController.$scope);

                offeringShowcase.setUp(offerings);
                offeringShowcase.offeringList.forEach(makeSelectable, ns);
            });
        });

    }

})(app.view, app.utils);
