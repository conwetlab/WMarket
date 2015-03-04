/**
 *  Copyright (c) 2015, CoNWeT Lab., Universidad Politécnica de Madrid
 *  Code licensed under BSD 3-Clause (https://github.com/conwetlab/WMarket/blob/master/LICENSE)
 */


var Offering = (function () {

    var Offering = function Offering(data, options) {
        this.element = document.createElement('div');
        this.element.className = "offering";

        var offeringHeader = this.element.appendChild(document.createElement('div'));
            offeringHeader.className = "offering-heading";

        this.imageElement = offeringHeader.appendChild(document.createElement('div'));
        this.imageElement.className = "offering-image";

        var offeringImage = this.imageElement.appendChild(document.createElement('img'));
            offeringImage.setAttribute('src', data.imageUrl);

        var offeringBody = this.element.appendChild(document.createElement('div'));
            offeringBody.className = "offering-body";

        var offeringTitle = offeringBody.appendChild(document.createElement('div'));
            offeringTitle.className = "offering-title";

        this.nameElement = offeringTitle.appendChild(document.createElement('div'));
        this.nameElement.className = "offering-name";
        this.nameElement.appendChild(document.createTextNode(data.displayName));

        this.storeElement = offeringTitle.appendChild(document.createElement('div'));
        this.storeElement.className = "offering-store";
        this.storeElement.appendChild(document.createTextNode(data.store));

        this.descriptionElement = offeringBody.appendChild(document.createElement('div'));
        this.descriptionElement.className = "offering-description";
        this.descriptionElement.appendChild(document.createTextNode(data.description));

        this.element.addEventListener('click', function (event) {
            location.assign(location.href + '/' + data.store + '/' + data.describedIn + '/' + data.name + '/');
        });
    };

    return Offering;

})();
