/**
 * Copyright (c) 2015, CoNWeT Lab., Universidad Politécnica de Madrid
 * Code licensed under BSD 3-Clause (https://github.com/conwetlab/WMarket/blob/master/LICENSE)
 */


(function (ns, utils) {

    ns.model = 'offering';

    var $categoryList = $('[app-group="category"]'),
        $offeringList = $('[app-filter="category"]');

    if ($categoryList.length || $offeringList.length) {
        ns.category = {

            urls: {
                collection: app.urls.get('category:collection'),
                entry: app.urls.get('category:entry') + '/offering'
            },

            filter: function filter(category, next) {
                app.requests.list(ns.category.urls.entry, {
                    kwargs: { category: category },
                    success: next
                });
            }

        };
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
                    ns.$scope.empty().append(app.createAlert('warning', "No available offerings."));
                }
            });
        };

        app.requests.attach('stores:collection', function () {
            var $spinner = utils.createSpinner();
            ns.$scope.append($spinner);
            ns.category.list(function (categories) {
                $spinner.remove();
                categories.forEach(function (data) {
                    ns.category.filter(data.name, function (offerings) {
                        var category = new app.components.Category(data, offerings);
                        ns.$scope.append(category.get());
                        category.setUp();
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
            ns.category.filter(ns.categoryName, function (offerings) {
                $spinner.remove();
                offerings.forEach(function (data) {
                    ns.$scope.append(app.createOffering(data).get());
                });
            });
        });
    }

})(app.view, app.utils);
