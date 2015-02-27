/**
 *  Copyright (c) 2015, CoNWeT Lab., Universidad Politécnica de Madrid
 *  Code licensed under BSD 3-Clause (https://github.com/conwetlab/WMarket/blob/master/LICENSE)
 */


var wmarket = {

    navbar: {
        search: $('#search'),
        searchField: $('#search-field'),
        searchFilters: $('#toggle-left-sidebar'),
        userPreferences: $('#toggle-right-sidebar')
    },

    loading: $('.loading'),

    sidebars: {
        left: $('#left-sidebar'),
        right: $('#right-sidebar')
    },

    content: {
        storeList: $('#store-list'),
        searchResults: $('#search-results')
    }

};

$(function () {

    wmarket.navbar.search.attr('disabled', "disabled");
    wmarket.navbar.searchField.attr('disabled', "disabled");

    wmarket.navbar.searchFilters.on('click', function (event) {
        event.preventDefault();

        if (this.classList.contains('active')) {
            this.classList.remove('active');
            wmarket.sidebars.left.removeClass('active');
        } else {
            this.classList.add('active');
            wmarket.sidebars.left.addClass('active');
        }

        event.stopPropagation();
    });

    wmarket.navbar.userPreferences.on('click', function (event) {
        event.preventDefault();

        if (this.classList.contains('active')) {
            this.classList.remove('active');
            wmarket.sidebars.right.removeClass('active');
        } else {
            this.classList.add('active');
            wmarket.sidebars.right.addClass('active');
        }

        event.stopPropagation();
    });

    $.ajax({
        type: 'GET',
        url: "/FiwareMarketplace/api/v2/offerings/",
        dataType: 'json',
        success: function (data, textStatus, jqXHR) {
            var offering;

            wmarket.loading.remove();

            for (var i = 0; i < data.offerings.length; i++) {
                offering = new Offering(data.offerings[i]);
                wmarket.content.searchResults.append(offering.element);
            }
        }
    });

    $.ajax({
        type: 'GET',
        url: "/FiwareMarketplace/api/v2/store/",
        dataType: 'json',
        success: function (data, textStatus, jqXHR) {
            var store;

            for (var i = 0; i < data.stores.length; i++) {
                store = new Store(data.stores[i]);
                store.element.classList.add('list-item');
                wmarket.content.storeList.append(store.element);
            }
        }
    });

});
