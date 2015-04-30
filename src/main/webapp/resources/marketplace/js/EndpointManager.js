/**
 *  Copyright (c) 2015, CoNWeT Lab., Universidad Politécnica de Madrid
 *  Code licensed under BSD 3-Clause (https://github.com/conwetlab/WMarket/blob/master/LICENSE)
 */


WMarket.requests = (function () {

    "use strict";

    /**
     * @function
     * @private
     *
     * @returns {HTMLElement}
     */
    var createLoadingSpinner = function createLoadingSpinner() {
        var container, spinner;

        spinner = document.createElement('span');
        spinner.className = 'fa fa-spinner fa-pulse';

        container = document.createElement('div');
        container.className = 'loading-state';
        container.appendChild(spinner);

        return container;
    };

    /**
     * @type {Object.<String, Object.>}
     * @private
     */
    var endpointList = {
        offerings: {
            collection: "/offerings/",
            store_collection: "/store/%(name)s/offering/"
        },
        stores: {
            collection: "/store/"
        }
    };

    /**
     * @function
     * @private
     *
     * @param {Object.<String, *>} requestOptions
     * @returns {Object.<String, *>}
     */
    var getResource = function getResource(requestOptions) {
        var endpointType, name, namespaceArgs, resourceName, resourceURL;

        namespaceArgs = requestOptions.namespace.split(':');

        resourceName = namespaceArgs[0];
        endpointType = namespaceArgs[1];

        if (!(endpointType in endpointList[resourceName])) {
            throw new CustomError('Resource Not Found', replaceByName('The endpoint type %(type)s was not registered.', {
                'type': endpointType
            }));
        }

        resourceURL = endpointList[resourceName][endpointType];

        if ('kwargs' in requestOptions) {
            resourceURL = replaceByName(resourceURL, requestOptions.kwargs);
        }

        return {
            'name': resourceName,
            'type': endpointType,
            'url': WMarket.core.contextPath + "/api/v2" + resourceURL
        };
    };

    /**
     * @function
     * @private
     *
     * @param {String} requestType
     * @param {Object.<String, *>} requestOptions
     */
    var makeRequest = function makeRequest(requestType, requestOptions) {
        var resource;

        resource = getResource(requestOptions);
        requestOptions.containment.empty().append(createLoadingSpinner());

        $.ajax({
            async: true,
            type: requestType,
            url: resource.url,
            dataType: 'json',
            success: function (data) {
                var collection, i;

                requestOptions.containment.empty();

                if (resource.type.indexOf('collection') != -1) {
                    collection = data[resource.name];

                    if (!collection.length) {
                        requestOptions.containment.append(requestOptions.alert);
                    } else {
                        requestOptions.onSuccess(collection, requestOptions.containment, data);
                    }

                    RequestManager.dispatch(requestOptions.namespace);
                } else {
                    // TODO: Support endpoints for entries.
                }
            },
            error: function (jqXHR, textStatus, errorThrown) {
                // TODO: Handle the callback onFailure given.
            }
        });
    };

    /**
     * @function
     * @private
     *
     * @param {String} text
     * @param {Object.<String, *>} kwargs
     * @returns {String}
     */
    var replaceByName = function replaceByName(text, kwargs) {
        var name;

        for (name in kwargs) {
            if (text.indexOf('%(' + name + ')s') != -1) {
                text = text.replace('%(' + name + ')s', kwargs[name]);
            }
        }

        return text;
    };

    /**
     * @type {Object.<String, Array.>}
     * @private
     */
    var requestList = {};

    /**
     * @type {Array.<String>}
     * @private
     */
    var requestTypeList = ['read'];

    /**
     * @namespace
     */
    var RequestManager = {

        /**
         * @function
         * @public
         *
         * @param {Object.<String, *>} requestOptions
         */
        read: function read(requestOptions) {
            makeRequest('GET', requestOptions);
        },

        /**
         * @function
         * @public
         *
         * @param {String} requestNamespace
         * @param {String} requestType
         * @param {Object.<String, *>} requestOptions
         */
        attach: function attach(requestNamespace, requestType, requestOptions) {
            if (!(requestNamespace in requestList)) {
                requestList[requestNamespace] = [];
            }

            if (requestTypeList.indexOf(requestType) == -1) {
                throw new CustomError('Request Not Allowed', replaceByName('The request %(type)s was not registered.', {
                    'type': requestType
                }));
            }

            requestList[requestNamespace].push({
                type: requestType,
                options: requestOptions
            });
        },

        /**
         * @function
         * @public
         *
         * @param {String} requestNamespace
         */
        dispatch: function dispatch(requestNamespace) {
            var i, request;

            if (!(requestNamespace in requestList)) {
                return;
            }

            for (i = 0; i < requestList[requestNamespace].length; i++) {
                request = requestList[requestNamespace][i];
                this[request.type](request.options);
            }
        }

    };

    return RequestManager;

})();
