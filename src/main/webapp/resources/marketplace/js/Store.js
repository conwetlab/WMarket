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

        var thumbnailElement = storeHeading.appendChild(document.createElement('span'));
            thumbnailElement.className = "image-thumbnail image-thumbnail-sm";

        this.imageElement = document.createElement('img');
        this.imageElement.className = "image image-circle";

        this.avatarElement = document.createElement('span');
        this.avatarElement.className = "image image-circle image-default-darker";

        var iconElement = this.avatarElement.appendChild(document.createElement('span'));
            iconElement.className = "fa fa-building fa-inverse";

        if (data.imagePath) {
            this.imageElement.src = [WMarket.core.contextPath, data.imagePath].join('/');
            thumbnailElement.appendChild(this.imageElement);
        } else {
            thumbnailElement.appendChild(this.avatarElement);
        }

        this.displayName = data.displayName;
        this.name = data.name;

        var storeBody = this.element.appendChild(document.createElement('div'));
            storeBody.className = "store-body";

        this.nameElement = storeBody.appendChild(document.createElement('span'));
        this.nameElement.className = "store-name";
        this.nameElement.appendChild(document.createTextNode(data.displayName));

        this.urlElement = storeBody.appendChild(document.createElement('span'));
        this.urlElement.className = "store-url";
        this.urlElement.appendChild(document.createTextNode(data.url));
    };

    Store.prototype.addClass = function addClass(classString) {
        var classList, i;

        classList = classString.split(' ');

        for (i = 0; i < classList.length; i++) {
            if (classList[i].length > 0) {
                this.element.classList.add(classList[i]);
            }
        }

        return this;
    };

    Store.prototype.get = function get() {
        return this.element;
    };

    return Store;

})();
