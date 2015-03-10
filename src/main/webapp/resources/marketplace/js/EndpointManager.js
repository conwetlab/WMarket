/**
 *  Copyright (c) 2015, CoNWeT Lab., Universidad Politécnica de Madrid
 *  Code licensed under BSD 3-Clause (https://github.com/conwetlab/WMarket/blob/master/LICENSE)
 */

WMarket.requests = (function () {

    "use strict";

    var RequestManager = {

        'read': function read(options) {
            makeRequest('GET', options);
        }

    };

    var createLoadingSpinner = function createLoadingSpinner() {
        var spinner = $('<span>').addClass('fa fa-spinner fa-pulse');

        return $('<div>').addClass('loading-state').append(spinner);
    };

    var endpointList = {

        offerings: {
            collection: "/offerings/"
        },
        stores: {
            collection: "/store/"
        }

    };

    var getResource = function getResource(options) {
        var endpointType, namespaceArgs, resourceName, resourceURL;

        namespaceArgs = options.namespace.split(':');

        resourceName = namespaceArgs[0];
        endpointType = namespaceArgs[1];

        resourceURL = endpointList[resourceName][endpointType];

        if (typeof resourceURL !== 'string') {
            throw null;
        }

        // TODO: Handle URL kwargs.

        return {
            'name': resourceName,
            'type': endpointType,
            'url': WMarket.core.contextPath + "/api/v2" + resourceURL
        };
    };

    var makeRequest = function makeRequest(type, options) {
        var resource;

        try {
            resource = getResource(options);
        } catch(err) {
            return;
        }

        options.containment.empty().append(createLoadingSpinner());

        $.ajax({
            async: true,
            type: type,
            url: resource.url,
            dataType: 'json',
            success: function (data, textStatus, jqXHR) {
                var collection, i;

                options.containment.empty();

                if (resource.type == 'collection') {
                    collection = data[resource.name];
                    if (!collection.length) {
                        options.containment.append(options.alert);
                    } else {
                        options.onSuccess(collection, options.containment, data);
                    }
                } else {
                    // TODO: Support endpoints for entries.
                }
            },
            error: function (jqXHR, textStatus, errorThrown) {
                // TODO: Handle the callback onFailure given.
            }
        });
    };

    return RequestManager;

})();
