/**
 *  Copyright (c) 2015, CoNWeT Lab., Universidad Politécnica de Madrid
 *  Code licensed under BSD 3-Clause (https://github.com/conwetlab/WMarket/blob/master/LICENSE)
 */

WMarket.requests = (function () {

    "use strict";

    var RequestManager = {

        'read': function read(options) {
            makeRequest('GET', options);
        },

        'register': function register(namespace, callback) {
            if (!(namespace in registerList)) {
                registerList[namespace] = [];
            }

            registerList[namespace].push(callback);
        },

        'ready': function ready(namespace) {
            var i;

            if (namespace in registerList) {
                for (i = 0; i < registerList[namespace].length; i++) {
                    registerList[namespace][i]();
                }
            }
        }

    };

    var createLoadingSpinner = function createLoadingSpinner() {
        var spinner = $('<span>').addClass('fa fa-spinner fa-pulse');

        return $('<div>').addClass('loading-state').append(spinner);
    };

    var endpointList = {

        offerings: {
            collection: "/offerings/",
            store_collection: "/store/%(name)s/offering/"
        },
        stores: {
            collection: "/store/"
        }

    };

    var registerList = {};

    var getResource = function getResource(options) {
        var endpointType, name, namespaceArgs, resourceName, resourceURL;

        namespaceArgs = options.namespace.split(':');

        resourceName = namespaceArgs[0];
        endpointType = namespaceArgs[1];

        resourceURL = endpointList[resourceName][endpointType];

        if (typeof resourceURL !== 'string') {
            throw null;
        }

        if ('kwargs' in options) {
            for (name in options.kwargs) {
                if (resourceURL.indexOf('%(' + name + ')s') != -1) {
                    resourceURL = resourceURL.replace('%(' + name + ')s', options.kwargs[name]);
                }
            }
        }

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

                if (resource.type.indexOf('collection') != -1) {
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
