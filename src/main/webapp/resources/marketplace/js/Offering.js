/**
 *  Copyright (c) 2015, CoNWeT Lab., Universidad Politécnica de Madrid
 *  Code licensed under BSD 3-Clause (https://github.com/conwetlab/WMarket/blob/master/LICENSE)
 */


var Offering = (function () {

	"use strict";

    var Offering = function Offering(data, options) {
        this.element = document.createElement('div');
        this.element.className = "panel panel-default offering";

        var offeringHeader = this.element.appendChild(document.createElement('div'));
            offeringHeader.className = "panel-heading";

        this.imageElement = offeringHeader.appendChild(document.createElement('div'));
        this.imageElement.className = "thumbnail thumbnail-sm";

        var offeringImage = this.imageElement.appendChild(document.createElement('img'));
            offeringImage.src = data.imageUrl;

        this.nameElement = offeringHeader.appendChild(document.createElement('a'));
        this.nameElement.className = "panel-title";
        this.nameElement.href = [WMarket.core.contextPath, 'offerings', data.describedIn.store, data.describedIn.name, data.name].join('/');
        this.nameElement.textContent = data.displayName;

        var offeringBody = this.element.appendChild(document.createElement('div'));
            offeringBody.className = "panel-body";

        var store = WMarket.resources.stores.getStoreByName(data.describedIn.store);

        this.storeElement = offeringBody.appendChild(document.createElement('a'));
        this.storeElement.className = "offering-store";
        this.storeElement.href = [WMarket.core.contextPath, 'stores', store.name, 'offerings'].join('/');
        this.storeElement.textContent = store.displayName;

        this.store = store;

        this.descriptionElement = offeringBody.appendChild(document.createElement('div'));
        this.descriptionElement.className = "offering-description";
        this.descriptionElement.textContent = data.description;
    };

    return Offering;

})();
