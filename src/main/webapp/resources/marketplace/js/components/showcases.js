/**
 * Copyright (c) 2015, CoNWeT Lab., Universidad Politécnica de Madrid
 * Code licensed under BSD 3-Clause (https://github.com/conwetlab/WMarket/blob/master/LICENSE)
 */

(function (ns, utils) {

    // ==================================================================================
    // CLASS DEFINITION - SHOWCASE
    // ==================================================================================

    ns.Showcase = function Showcase($parent) {
        this.$back = $('<button class="btn btn-default slider-control">')
            .append($('<i class="fa fa-chevron-left">'));
        this.$next = $('<button class="btn btn-default slider-control">')
            .append($('<i class="fa fa-chevron-right">'));

        this.$list = $('<div class="row-sliding">');
        this.$showcase = $('<div class="slider-showcase">')
            .append(this.$list);

        this.$element = $parent != null ? $parent : $('<div class="row">');
        this.$element.append(this.$back, this.$showcase, this.$next);
    };

    ns.Showcase.prototype = {

        setUp: function setUp(customElements, itemWidth) {

            customElements.forEach(function (ce) {
                this.add(ce);
            }, this);

            this.$itemWidth = itemWidth;
            this.$listWidth = this.$list.children().length * this.$itemWidth;

            this.$list.css({width: this.$listWidth, left: 0});
            this.offsetWidth = 0;

            this.$back.on('click', this.back.bind(this));
            this.$next.on('click', this.next.bind(this));

            $(window).on('resize', this.refresh.bind(this));

            return this.refresh();
        },

        add: function add(customElement) {
            this.$list.append(customElement.addClass('slider-item').get());

            return this;
        },

        back: function back() {

            if (!this.$backEnabled) {
                return this;
            }

            var extraWidth = (- this.offsetWidth) % this.$itemWidth;
            this.offsetWidth += !extraWidth ? this.$itemWidth : extraWidth;

            return slide.call(this);
        },

        get: function get() {
            return this.$element;
        },

        next: function next() {

            if (!this.$nextEnabled) {
                return this;
            }

            this.offsetWidth -= (this.$itemWidth - ((this.$showcaseWidth - this.offsetWidth) % this.$itemWidth));

            return slide.call(this);
        },

        refresh: function refresh() {
            this.$showcaseWidth = this.$showcase.width();
            toggleControlBack.call(this);
            toggleControlNext.call(this);

            return this;
        }

    };

    function slide() {
        this.$list.css('left', this.offsetWidth);

        toggleControlBack.call(this);
        toggleControlNext.call(this);

        return this;
    }

    function toggleControlBack() {
        this.$backEnabled = this.offsetWidth < 0;
        this.$back.toggle(this.$backEnabled);
    }

    function toggleControlNext() {
        this.$nextEnabled = (this.$showcaseWidth - this.offsetWidth) < this.$listWidth;
        this.$next.toggle(this.$nextEnabled);
    }

    // ==================================================================================
    // CLASS DEFINITION - OFFERING SHOWCASE
    // ==================================================================================

    ns.OfferingShowcase = function OfferingShowcase($parent) {
        this.superClass($parent);
        this.offeringList = [];
    };

    utils.inherit(ns.OfferingShowcase, ns.Showcase);

    utils.members(ns.OfferingShowcase, {

        setUp: function setUp(offerings) {
            this.offeringList = offerings.map(function (info) {
                return new ns.Offering(info);
            });

            return ns.Showcase.prototype.setUp.call(this, this.offeringList, 210);
        }

    });

    // ==================================================================================
    // CLASS DEFINITION - CATEGORY SHOWCASE
    // ==================================================================================

    ns.CategoryShowcase = function CategoryShowcase(data, $parent) {
        this.$title = $('<h3>')
            .append($('<a class="category-title">').attr('href', [app.contextPath, 'category', data.name]
            .join('/')).text(data.displayName));

        this.superClass();

        if ($parent != null) {
            $parent.append(this.$title, this.$element);
        }

        this.$element.addClass("category-offerings");
    };

    utils.inherit(ns.CategoryShowcase, ns.OfferingShowcase);

    // ==================================================================================
    // CLASS DEFINITION - ROTATING SHOWCASE
    // ==================================================================================

    ns.OfferingRotatingShowcase = function RotatingShowcase($parent) {
        var topControls = $('<div class="row text-center">'),
            bottomControls = $('<div class="row text-center">');

        this.$back0 = $('<button class="btn btn-default rotating-control">')
            .append($('<i class="fa fa-chevron-left">'));
        this.$back1 = $('<button class="btn btn-default rotating-control">')
        .append($('<i class="fa fa-chevron-left">'));
        this.$next0 = $('<button class="btn btn-default rotating-control">')
            .append($('<i class="fa fa-chevron-right">'));
        this.$next1 = $('<button class="btn btn-default rotating-control">')
            .append($('<i class="fa fa-chevron-right">'));

        topControls.append(this.$back0, this.$next0);
        bottomControls.append(this.$back1, this.$next1);

        this.$title = $('<h3>').text("Compare offerings");

        this.lists = [];
        this.$showcase = $('<div class="row">');

        offeringDetailList.forEach(appendList, this);

        if ($parent != null) {
            this.hasParent = true;
        }

        this.$element = $parent != null ? $parent : $('<div class="row">');
        this.$element.append(this.$title, topControls, this.$showcase, bottomControls);

        this.$itemWidth = 210;

        this.$back0.on('click', this.back.bind(this));
        this.$back1.on('click', this.back.bind(this));
        this.$next0.on('click', this.next.bind(this));
        this.$next1.on('click', this.next.bind(this));

        this._handleRefresh = this.refresh.bind(this);

        $(window).on('resize', this._handleRefresh);
    };

    ns.OfferingRotatingShowcase.prototype = {

        addOffering: function addOffering(offering) {

            this.lists.forEach(function (list, index) {
                buildDetailView.call(this, list, index, offering);
                this.$listWidth = list.$items.length * this.$itemWidth;
                list.$list.css({width: this.$listWidth, left: 0});
            }, this);

            this.offsetWidth = 0;
            this.refresh();

            return this;
        },

        back: function back() {

            if (!this.$backEnabled) {
                return this;
            }

            var extraWidth = (- this.offsetWidth) % this.$itemWidth;
            this.offsetWidth += !extraWidth ? this.$itemWidth : extraWidth;

            return multipleSlide.call(this);
        },

        next: function next() {

            if (!this.$nextEnabled) {
                return this;
            }

            this.offsetWidth -= (this.$itemWidth - ((this.$showcaseWidth - this.offsetWidth) % this.$itemWidth));

            return multipleSlide.call(this);
        },

        refresh: function refresh() {
            this.$showcaseWidth = this.lists[0].$showcase.width();
            toggleMultipleControlBack.call(this);
            toggleMultipleControlNext.call(this);

            return this;
        }

    };

    var offeringDetailList = ["OFFERING", "DESCRIPTION", "CATEGORIES", /*"ADDITIONAL INFORMATION",*/ "PRICE PLANS", "SERVICES"];

    function appendList(title, index) {
        var $list = $('<div class="row-sliding">'),
            $showcase = $('<div class="row row-rotating">');

        if (index != 0) {
            this.$showcase.append($('<hr>'));
        }

        this.$showcase.append($('<div class="row rotating-showcase">').append($('<h4>').text(title), $showcase.append($list)));
        this.lists.push({
            $showcase: $showcase,
            $items: [],
            $list: $list
        });
    }

    function removeOffering($item, index) {
        var currentPosition = this.lists[index].$items.indexOf($item);

        this.lists.forEach(function (list) {
            list.$items[currentPosition].remove();
            list.$items.splice(currentPosition, 1);
            this.$listWidth = list.$items.length * this.$itemWidth;
            list.$list.css({width: this.$listWidth, left: 0});
        }, this);

        this.offsetWidth = 0;
        this.refresh();

        if (!this.$listWidth) {
            $(window).off('resize', this._handleRefresh);
            if (this.hasParent) {
                this.$element.empty();
            } else {
                this.$element.remove();
            }
        }
    }

    function makeAvailableOffering(offering) {
        offering.selected = false;
        offering.$heading.attr('data-layer-title', "+");
        offering.get().removeClass('active');
    }

    function buildDetailView(list, index, offering) {
        var $panel = $('<div class="rotating-item">');

        switch (index) {
        case 0:
            $panel.addClass("panel panel-default offering-removable")
                .on('click', function (event) {
                    removeOffering.call(this, $panel, index);
                    makeAvailableOffering.call(this, offering);
                }.bind(this));
            var $panelHeading = $('<div class="panel-heading text-center">');
            var $thumbnail = $('<div class="image-thumbnail thumbnail-bordered thumbnail-justified">').append(
                    $('<img class="image">').attr('src', offering.info.imageUrl),
                    $('<span class="rating-value rating-value-lighter">').append(
                        $('<span class="fa fa-star">').text(" " + offering.info.averageScore.toFixed(1))));
            var $title = $('<div class="panel-title text-truncate">').text(offering.info.displayName);
            $panelHeading.append($thumbnail, $title);
            $panel.append($panelHeading);
            break;
        case 1:
            $panel.text(offering.info.description);
            break;
        case 2:
            $panel.addClass("label-group-stacked");
            offering.info.categories.forEach(function (category) {
                $panel.append($('<div class="label label-success text-center">').text(category.displayName));
            });
            break;
        case 3:
            offering.info.pricePlans.forEach(function (priceplan) {
                var $panel0 = $('<div>').addClass("panel panel-default payment-plan");
                var $panelHeading = $('<div class="panel-heading text-center">');
                var $title = $('<div class="panel-title">').text(priceplan.title);
                var $description = $('<div class="payment-plan-description">').text(priceplan.comment);
                var $panelBody = $('<div class="panel-body">');
                $panelHeading.append($title, $description);
                $panel0.append($panelHeading, $panelBody);
                buildPriceComponents($panelBody, priceplan.priceComponents);
                $panel.append($panel0)
            });

            if (!offering.info.pricePlans.length) {
                $panel.append(app.createAlert('warning', "No price plan available"))
            }
            $panel.addClass("priceplan-group-stacked");
            break;
        default:
            offering.info.services.forEach(function (service) {
                var $item = $('<div class="service-item">').append(
                        $('<div class="service-name">').append($('<span>').text(service.displayName)),
                        $('<div class="service-content">').append(
                            $('<div class="service-categories">').append($('<span class="label label-success">').text(service.categories[0].displayName),
                            $('<div class="service-comment">').text(service.comment)))
                    );
                $panel.append($item);
            });

            if (!offering.info.services.length) {
                $panel.append(app.createAlert('warning', "No service available"))
            }
            $panel.addClass("service-group-stacked");
        }
        list.$list.prepend($panel);
        list.$items.unshift($panel);
    }

    function buildPriceComponent(priceComponent) {
        var $hr = $('<hr>'), $item = $('<div>');

        $item.addClass('list-group-item text-center tooltip');

        if (priceComponent.comment) {
            $item.attr('data-title', priceComponent.comment);
        }

        switch (priceComponent.unit) {
        case 'single payment':
            $item.addClass("list-group-item-info");
            break;
        case 'free':
            $item.addClass("list-group-item-success");
            break;
        default:
            $item.addClass("list-group-item-warning");
        }

        var units = "";
        var $currency = $('<span class="currency">');

        if (['eur'].indexOf(priceComponent.currency.toLowerCase()) != -1) {
            $currency.addClass('fa fa-' + priceComponent.currency.toLowerCase());
        } else {
            $currency.text(priceComponent.currency);
        }

        var $p0 = $('<p class="value">').append(priceComponent.value, $currency);
        var $p1 = $('<p class="units">');

        switch (priceComponent.unit) {
        case 'single payment':
            units = "1 payment";
            break;
        case 'free':
            units = "right now";
            break;
        default:
            units = "/ " + priceComponent.unit;
        }
        $p1.append(units);
        $item.append($p0, $p1);

        return $item;
    }

    function buildPriceComponents($target, priceComponents) {
        var $listGroup = $target.append($('<div class="list-group">'));

        priceComponents.forEach(function (priceComponent, index) {

            if (index != 0) {
                $listGroup.append($('<div class="text-center">').text("+"));
            }

            $listGroup.append(buildPriceComponent(priceComponent));
        });

        if (!priceComponents.length) {
            $listGroup.append(buildPriceComponent({
                currency: "Free",
                value: "",
                unit: "free"
            }));
        }
    }

    function toggleMultipleControlBack() {
        this.$backEnabled = this.offsetWidth < 0;
        this.$back0.prop('disabled', !this.$backEnabled);
        this.$back1.prop('disabled', !this.$backEnabled);
    }

    function toggleMultipleControlNext() {
        this.$nextEnabled = (this.$showcaseWidth - this.offsetWidth) < this.$listWidth;
        this.$next0.prop('disabled', !this.$nextEnabled);
        this.$next1.prop('disabled', !this.$nextEnabled);
    }

    function multipleSlide() {
        this.lists.forEach(function (data) {
            data.$list.css('left', this.offsetWidth);
        }, this);

        toggleMultipleControlBack.call(this);
        toggleMultipleControlNext.call(this);

        return this;
    }

})(app.components, app.utils);
