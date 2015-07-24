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

    ns.OfferingServiceComparison = function OfferingServiceComparison($target) {
        this.$target = $target;
        this.titles  = {};

        this.rows = [];
        this.cols = [];
    };

    utils.members(ns.OfferingServiceComparison, {

        _addOfferingService: function _addOfferingService($row, service) {
            var $item = $('<div class="rotating-item service-item">');

            $item.append(
                $('<h4 class="text-bold service-name">').text(service.displayName),
                $('<div class="service-content">').append(
                    this._createCategoryList(service.categories),
                    $('<div class="service-comment auto-scroll-x">').append(this._replaceEOL(service.comment)))
            );
            $item.on('click', function (event) {
                $row.find('.service-item').toggleClass('expanded', !$item.hasClass('expanded'));
            });

            $row.append($item);

            return this;
        },

        _addHyphen: function _addHyphen($row) {
            var $item = $('<div class="rotating-item empty text-center">').text("_");

            $row.append($item);

            return this;
        },

        _createCategoryList: function _createCategoryList(categories) {
            var $list = $('<div class="service-categories label-group-stacked text-center">');

            categories.sort(this._orderByAlphabeticOrNumeric).forEach(function (category) {
                $list.append($('<span class="label label-success text-truncate">').text(category.displayName));
            });

            return $list;
        },

        _createRow: function _createRow() {
            var $row = $('<div class="subrow-sliding">');

            this.rows.push($row);
            this.$target.append($row);

            return $row;
        },

        _orderByAlphabeticOrNumeric: function _orderByAlphabeticOrNumeric(a, b) {
            var textA = (typeof a === 'string' ? a : a.displayName).toLowerCase();
            var textB = (typeof b === 'string' ? b : b.displayName).toLowerCase();

            return (textA < textB) ? -1 : (textA > textB) ? 1 : 0;
        },

        _replaceEOL: function _replaceEOL(text) {
            return text.replace(/\n/g, function () {
                return '<br>';
            });
        },

        removeCol: function removeCol(index) {
            var services = this.cols[index];

            services.forEach(function (service) {
                this.titles[service.displayName]--;

                if (!this.titles[service.displayName]) {
                    delete this.titles[service.displayName];
                }
            }, this);

            this.cols.splice(index, 1);
            this.refresh();

            return this;
        },

        prependCol: function prependCol(services) {

            if (services.length) {
                services.forEach(function (service) {
                    if (service.displayName in this.titles) {
                        this.titles[service.displayName]++;
                    } else {
                        this.titles[service.displayName] = 1;
                    }
                }, this);

                services = services.sort(this._orderByAlphabeticOrNumeric);
            }

            this.cols.unshift(services);
            this.refresh();

            return this;
        },

        refresh: function refresh() {
            var $row, titles = Object.keys(this.titles).sort(this._orderByAlphabeticOrNumeric);

            this.$target.empty();
            this.rows.length = 0;

            if (!titles.length) {
                $row = this._createRow();

                this.cols.forEach(function () {
                    $row.append(app.createAlert('warning', "No service available"));
                }, this);
            } else {
                titles.forEach(function (title) {
                    $row = this._createRow();

                    this.cols.forEach(function (services) {
                        var hasOfferingService = services.some(function (service) {

                                if (service.displayName == title) {
                                    this._addOfferingService($row, service);
                                    return true;
                                }

                                return false;
                            }, this);

                        if (!hasOfferingService) {
                            this._addHyphen($row);
                        }
                    }, this);
                }, this);
            }

            return this;
        }

    });

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

        this.categoryList = {};
        this.offeringList = [];
        $(window).on('resize', this._handleRefresh);
    };

    ns.OfferingRotatingShowcase.prototype = {

        addOffering: function addOffering(offering) {

            this.lists.forEach(function (list, index) {
                buildDetailView.call(this, list, index, offering);
                if (index == 0) {
                    this.$listWidth = list.$items.length * this.$itemWidth;
                }
                list.$list.css({width: this.$listWidth, left: 0});
                if (index == 4) {
                    list.$items.forEach(function ($subList) {
                        $subList.css({width: this.$listWidth});
                    }, this);
                }
            }, this);

            this.offeringList.unshift(offering);

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


        this.$showcase.append($('<div class="row rotating-showcase">').append($('<h4>').append($('<strong>').text(title)), $showcase.append($list)));
        this.lists.push({
            $showcase: $showcase,
            $items: [],
            $list: $list
        });

        if (index == 4) {
            this.serviceRow = new ns.OfferingServiceComparison($list);
        }

    }

    function removeOffering($item, index) {
        var currentPosition = this.lists[index].$items.indexOf($item);

        this.lists.forEach(function (list, index) {

            if (index == 4) {
                this.serviceRow.removeCol(currentPosition);
            } else {
                list.$items[currentPosition].remove();
                list.$items.splice(currentPosition, 1);
            }

            if (index == 2) { // CATEGORIES
                updateCategoryList.call(this, this.offeringList[currentPosition].info.categories, list);
            }

            if (index == 0) {
                this.$listWidth = list.$items.length * this.$itemWidth;
            }

            list.$list.css({width: this.$listWidth, left: 0});
        }, this);

        this.offeringList.splice(currentPosition, 1);

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

    function updateCategoryList(categories, list) {
        categories.forEach(function (category) {
            this.categoryList[category.displayName]--;
            if (this.categoryList[category.displayName] == 0) {
                delete this.categoryList[category.displayName];
            }
        }, this);

        refreshCategories.call(this, list);
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
            var $title = $('<h4 class="panel-title text-truncate">').text(offering.info.displayName);
            $panelHeading.append($thumbnail, $title);
            $panel.append($panelHeading);
            break;
        case 1:
            $panel.addClass("auto-scroll-x");
            $panel.text(offering.info.description);
            break;
        case 2: // CATEGORIES
            if (!offering.info.categories.length) {
                $panel.append(app.createAlert('warning', "No category available"))
                break;
            }

            offering.info.categories
            .sort(function (a, b) {
                return orderByAlphabeticOrNumeric('displayName', a, b);
            })
            .forEach(function (category) {
                if (category.displayName in this.categoryList) {
                    this.categoryList[category.displayName]++;
                } else {
                    this.categoryList[category.displayName] = 1;
                }
                $panel.append($('<div class="label label-success text-center">').text(category.displayName));
            }, this);

            $panel.addClass("label-group-stacked");

            list.$list.prepend($panel);
            list.$items.unshift($panel);
            refreshCategories.call(this, list);
            return;
        case 3:
            offering.info.pricePlans
            .sort(function (a, b) {
                return orderByAlphabeticOrNumeric('title', a, b);
            })
            .forEach(function (priceplan) {
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
        default: // SERVICES
            return this.serviceRow.prependCol(offering.info.services);
        }

        list.$list.prepend($panel);
        list.$items.unshift($panel);
    }

    function addEmptyItems(currentLength, list) {
        var maxLength  = list.$list.children().length,
            diffLength = maxLength - currentLength;

        if (diffLength > 0) {
            var i = 0;

            for (i = 0; i < diffLength; i++) {
                addEmptyItem(list.$items[currentLength + i]);
            }
        }

        list.$items.forEach(function ($sublist) {
            var i = 0, diffLength = maxLength - $sublist.children().length;

            if (diffLength > 0) {
                for (i = 0; i < diffLength; i++) {
                    $sublist.append($('<div class="rotating-item empty">'));
                }
            }
        });

    }

    function refreshCategories(list) {
        var categories = Object.keys(this.categoryList).sort(function (a, b) {
            var textA = a.toLowerCase();
            var textB = b.toLowerCase();

            return (textA < textB) ? -1 : (textA > textB) ? 1 : 0;
        });

        list.$items.forEach(function ($panel) {
            var _old, collection = $panel.find('.label').remove();

            if (collection.length) {
                _old = collection.filter('.label-success').map(function () {
                    return this.textContent;
                }).get();
                refreshCategoryList(categories, _old, $panel);
            }
        });
    }

    function refreshCategoryList(categories, offeringCategories, $target) {
        var i, j = 0;

        for (i = 0; i < categories.length && j < offeringCategories.length; i++) {
            if (categories[i] == offeringCategories[j]) {
                $target.append($('<div class="label label-success text-center">').text(categories[i]));
                j++;
            } else {
                $target.append($('<div class="label text-center">').text("-"));
            }
        }
    }

    function addEmptyItem($subList) {
        var $panel = $('<div class="rotating-item empty">');

        $subList.prepend($panel);
    }

    function addSubList(list) {
        var $row = $('<div class="rotating-sublist service-group-stacked">');
        list.$list.append($row);
        list.$items.push($row);
    }

    function buildService(list, service, index) {

        if (list.$items[index] == null) {
            addSubList(list);
        }

        var $panel = $('<div class="rotating-item">');

        var $item = $('<div class="service-item">').append(
                $('<div class="service-name">').append($('<span>').text(service.displayName)),
                $('<div class="service-content">').append(
                    buildCategoryList(service.categories),
                    $('<div class="service-comment">').append(replaceEOL(service.comment)))
            );

        list.$items[index].prepend($panel.append($item));
    }

    function buildCategoryList(categories) {
        var $list = $('<div class="service-categories label-group-stacked text-center">');

        categories
        .sort(function (a, b) {
            return orderByAlphabeticOrNumeric('displayName', a, b);
        })
        .forEach(function (category) {
            $list.append($('<span class="label label-success text-truncate">').text(category.displayName));
        });

        return $list;
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

        if (['gbp', 'eur', 'usd'].indexOf(priceComponent.currency.toLowerCase()) != -1) {
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

    function replaceEOL(text) {
        return text.replace(/\n/g, function () {
            return '<br>'
        });
    }

    function orderByAlphabeticOrNumeric(property, a, b) {
        var textA = a[property].toLowerCase();
        var textB = b[property].toLowerCase();

        return (textA < textB) ? -1 : (textA > textB) ? 1 : 0;
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
