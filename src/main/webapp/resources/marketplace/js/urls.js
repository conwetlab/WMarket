/**
 * Copyright (c) 2015, CoNWeT Lab., Universidad Politécnica de Madrid
 * Code licensed under BSD 3-Clause (https://github.com/conwetlab/WMarket/blob/master/LICENSE)
 */


(function (ns, utils) {

    var api = {

        offering: {
            collection: "/offering",
            entry: "/store/%(store)s/description/%(description)s/offering/%(offering)s"
        },

        store: {
            collection: "/store",
            entry: "/store/%(store)s"
        }

    };

    var service = {

        category: {
            collection: "/category",
            entry: "/category/%(category)s"
        },

        bookmark: {
            entry: "/bookmark"
        },

        review: {
            collection: "/review",
            entry: "/review/%(review)s"
        }

    };

    function findURL(namespace) {
        return namespace.split(':').map(function (value, index, list) {
            return index % 2 ? formatURL(list[index -1], value) : "";
        }).join('');
    }

    function formatURL(key, value) {
        return key in api ? api[key][value] : service[key][value];
    }

    // ==================================================================================
    // NAMESPACE DIFINITION
    // ==================================================================================

    ns.baseURL = app.contextPath + '/api/v2';

    ns.urls = {

        get: function get(namespace, kwargs) {
            return ns.baseURL + utils.format(findURL(namespace), kwargs);
        }

    };

})(app, app.utils);
