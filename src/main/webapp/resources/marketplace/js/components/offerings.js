/**
 * Copyright (c) 2015, CoNWeT Lab., Universidad Politécnica de Madrid
 * Code licensed under BSD 3-Clause (https://github.com/conwetlab/WMarket/blob/master/LICENSE)
 */

(function (ns) {

    "use strict";

    ns.Offering = function Offering(data) {
        for (var name in data) {
            this[name] = data[name];
        }

        this.element = $('<div class="panel panel-default-lighter offering-item">');
        this.store   = app.getStore(this.describedIn.store);

        var thumbnailElement = $('<a class="image-thumbnail thumbnail-bordered">')
            .attr('href', [
                app.contextPath,
                'offerings',
                this.describedIn.store,
                this.describedIn.name,
                this.name
            ].join('/'));

        var ratingValue = $('<span>')
            .addClass('rating-value rating-value-lighter')
            .append($('<span>').addClass('fa fa-star'), " " + this.averageScore.toFixed(1));

        thumbnailElement.append(
            $('<img class="image">')
                .addClass('offering-image').attr('src', this.imageUrl),
            ratingValue);

        var offeringHeading = $('<div class="panel-heading text-center">').append(
            thumbnailElement,
            $('<a class="panel-title text-truncate">').text(this.displayName).attr('href', [
                app.contextPath,
                'offerings',
                this.describedIn.store,
                this.describedIn.name,
                this.name
            ].join('/')));

        var offeringBody = $('<div class="panel-body">').append(
            $('<a class="offering-store">').text(this.store ? this.store.displayName : this.describedIn.store).attr('href', [
                app.contextPath, 'stores', this.describedIn.store, 'offerings'
            ].join('/')),
            $('<div class="offering-description">').text(this.description));

        this.element.append(offeringHeading, offeringBody);
    };

    ns.Offering.prototype.addClass = function addClass(classList) {
        this.element.addClass(classList);
        return this;
    };

    ns.Offering.prototype.get = function get() {
        return this.element;
    };

})(app.components);
