/**
 * Copyright (c) 2015, CoNWeT Lab., Universidad Politécnica de Madrid
 * Code licensed under BSD 3-Clause (https://github.com/conwetlab/WMarket/blob/master/LICENSE)
 */


(function (ns, utils) {

    ns.model = 'offering';

    var $categoryList = $('[app-group="category"]'),
        $offeringList = $('[app-filter="category"]'),
        $lastViewedList = $('[app-order="lastviewed"]')
        $viewedByOthersList = $('[app-order="viewedByOthers"]');

    if ($categoryList.length || $offeringList.length) {
        ns.category = {

            urls: {
                collection: app.urls.get('category:collection'),
                entry: app.urls.get('category:entry') + '/offering'
            },

            models: {},

            filter: function filter(category, next) {
                app.requests.list(ns.category.urls.entry, {
                    kwargs: { category: category },
                    success: next,
                    failure: function () {
                        if (ns.currentCategory != null) {
                            ns.$scope.empty().parent()
                                .append(app.createAlert('warning', "No offerings available in <strong>" + ns.currentCategory.displayName + "</strong>."))
                        } else {
                            ns.$scope.append(app.createAlert('warning', "No offerings available in <strong>" + ns.category.models[category].displayName + "</strong>."))
                        }
                    }
                });
            }

        };

    }

    // ==================================================================================
    // ORDER BY - LAST VIEWED
    // ==================================================================================

    if ($lastViewedList.length) {

        ns.lastViewedController = {

            $scope: $lastViewedList,

            urls: {
                collection: app.urls.get('offering:collection:lastviewed:collection')
            },

            orderBy: function orderBy(next) {
                app.requests.list(this.urls.collection, {
                    $target: this.$scope,
                    $alert: app.createAlert('warning', "No offerings available."),
                    success: next
                });
            }

        };

        app.requests.attach('stores:collection', function () {
            ns.lastViewedController.orderBy(function (offerings) {
                var offeringShowcase = new app.components.OfferingShowcase(ns.lastViewedController.$scope);

                offeringShowcase.setUp(offerings);
            });
        });
    }
    
    // ==================================================================================
    // ORDER BY - VIEWED BY OTHERS
    // ==================================================================================

    if ($viewedByOthersList.length) {

        ns.viewedByOthersController = {

            $scope: $viewedByOthersList,

            urls: {
                collection: app.urls.get('offering:collection:viewedbyothers:collection')
            },

            orderBy: function orderBy(next) {
                app.requests.list(this.urls.collection, {
                    $target: this.$scope,
                    $alert: app.createAlert('warning', "No offerings available."),
                    success: next
                });
            }

        };

        app.requests.attach('stores:collection', function () {
            ns.viewedByOthersController.orderBy(function (offerings) {
                var offeringShowcase = new app.components.OfferingShowcase(ns.viewedByOthersController.$scope);

                offeringShowcase.setUp(offerings);
            });
        });
    }

    // ==================================================================================
    // GROUP BY - CATEGORY
    // ==================================================================================

    if ($categoryList.length) {
        ns.$scope = $categoryList;

        ns.category.list = function list(next) {
            app.requests.list(ns.category.urls.collection, {
                success: next,
                failure: function () {
                    ns.$scope.empty().append(app.createAlert('warning', "No offerings available."));
                }
            });
        };

        app.requests.attach('stores:collection', function () {
            var $spinner = utils.createSpinner();
            ns.$scope.append($spinner);
            ns.category.list(function (categories) {
                $spinner.remove();
                categories.forEach(function (data) {
                    var offeringShowcase = new app.components.CategoryShowcase(data, ns.$scope);

                    ns.category.models[data.name] = data;
                    ns.category.filter(data.name, function (offerings) {
                        offeringShowcase.setUp(offerings);
                    });
                });
            });
        });
    }

    // ==================================================================================
    // FILTER BY - CATEGORY
    // ==================================================================================

    if ($offeringList.length) {
        ns.$scope = $offeringList;

        app.requests.attach('stores:collection', function () {
            var $spinner = utils.createSpinner();
            ns.$scope.append($spinner);
            ns.category.filter(ns.currentCategory.name, function (offerings) {
                $spinner.remove();
                offerings.forEach(function (data) {
                    ns.$scope.append(app.createOffering(data).get());
                });
            });
        });
    }

})(app.view, app.utils);
