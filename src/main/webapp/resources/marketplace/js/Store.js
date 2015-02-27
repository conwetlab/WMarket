/**
 *  Copyright (c) 2015, CoNWeT Lab., Universidad Politécnica de Madrid
 *  Code licensed under BSD 3-Clause (https://github.com/conwetlab/WMarket/blob/master/LICENSE)
 */


var Store = (function () {

    var Store = function Store(data, options) {
        this.element = document.createElement('div');
        this.element.className = "store";

        var storeHeader = this.element.appendChild(document.createElement('div'));
            storeHeader.className = "store-heading";

        var stackElement = storeHeader.appendChild(document.createElement('span'));
            stackElement.className = "fa-stack fa-lg";

        var circleElement = stackElement.appendChild(document.createElement('span'));
            circleElement.className = "fa fa-circle fa-stack-2x";

        var avatarElement = stackElement.appendChild(document.createElement('span'));
            avatarElement.className = "fa fa-building fa-stack-1x fa-inverse";

        var storeBody = this.element.appendChild(document.createElement('div'));
            storeBody.className = "store-body";

        this.nameElement = storeBody.appendChild(document.createElement('div'));
        this.nameElement.className = "store-name";
        this.nameElement.appendChild(document.createTextNode(data.displayName));

        this.urlElement = storeBody.appendChild(document.createElement('div'));
        this.urlElement.className = "store-url";
        this.urlElement.appendChild(document.createTextNode(data.url));

        var plusElement = storeBody.appendChild(document.createElement('span'));
            plusElement.className = "store-add fa fa-plus-circle";
    };

    return Store;

})();
