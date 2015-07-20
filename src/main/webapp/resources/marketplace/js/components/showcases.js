/**
 * Copyright (c) 2015, CoNWeT Lab., Universidad Politécnica de Madrid
 * Code licensed under BSD 3-Clause (https://github.com/conwetlab/WMarket/blob/master/LICENSE)
 */

(function (ns, utils) {

    // ==================================================================================
    // CLASS DEFINITION - SHOWCASE
    // ==================================================================================

    ns.Showcase = function Showcase() {
        this.$back = $('<button class="btn btn-default slider-control">')
            .append($('<i class="fa fa-chevron-left">'));
        this.$next = $('<button class="btn btn-default slider-control">')
            .append($('<i class="fa fa-chevron-right">'));

        this.$list = $('<div class="row-sliding">');
        this.$showcase = $('<div class="slider-showcase">')
            .append(this.$list);

        this.$offeringList = $('<div class="row">')
            .append(this.$back, this.$showcase, this.$next);

        this.$element = $('<div class="row">')
            .append(this.$offeringList);
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

    ns.OfferingShowcase = function OfferingShowcase() {
        this.superClass();
    };

    utils.inherit(ns.OfferingShowcase, ns.Showcase);

    utils.members(ns.OfferingShowcase, {
        setUp: function setUp(offerings) {
            var customElements = offerings.map(function (info) {
                return new ns.Offering(info);
            });

            return ns.Showcase.prototype.setUp.call(this, customElements, 210);
        }
    });

    // ==================================================================================
    // CLASS DEFINITION - CATEGORY SHOWCASE
    // ==================================================================================

    ns.CategoryShowcase = function CategoryShowcase(data) {
        this.superClass();

        this.$title = $('<h3>')
            .append($('<a class="category-title">').attr('href', [
                    app.contextPath,
                    'category',
                    data.name]
                .join('/')).text(data.displayName));

        this.$offeringList.addClass("category-offerings");
        this.$element.addClass("category").prepend(this.$title);
    };

    utils.inherit(ns.CategoryShowcase, ns.OfferingShowcase);

})(app.components, app.utils);
