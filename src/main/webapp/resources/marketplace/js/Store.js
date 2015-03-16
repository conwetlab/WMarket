/**
 *  Copyright (c) 2015, CoNWeT Lab., Universidad Politécnica de Madrid
 *  Code licensed under BSD 3-Clause (https://github.com/conwetlab/WMarket/blob/master/LICENSE)
 */


var Store = (function () {

	"use strict";

    var Store = function Store(data, options) {
        this.element = document.createElement('a');
        this.element.className = "store-item";
        this.element.href = [WMarket.core.contextPath, 'stores', data.name, 'offerings'].join('/');

        var storeHeading = this.element.appendChild(document.createElement('div'));
            storeHeading.className = "store-heading";

        var avatarElement = storeHeading.appendChild(document.createElement('span'));
            avatarElement.className = "fa fa-building fa-fw";

        this.nameElement = storeHeading.appendChild(document.createElement('span'));
        this.nameElement.className = "store-name text-plain";
        this.nameElement.appendChild(document.createTextNode(data.displayName));

        this.displayName = data.displayName;
        this.name = data.name;

        var storeBody = this.element.appendChild(document.createElement('div'));
            storeBody.className = "store-body";

        this.urlElement = storeBody.appendChild(document.createElement('span'));
        this.urlElement.className = "store-url";
        this.urlElement.appendChild(document.createTextNode(data.url));
    };

    Store.prototype.addClass = function addClass(classString) {
        var classList, i;

        classList = classString.split(' ');

        for (i = 0; i < classList.length; i++) {
            this.element.classList.add(classList[i]);
        }

        return this;
    };

    Store.prototype.get = function get() {
        return this.element;
    };

    return Store;

})();
