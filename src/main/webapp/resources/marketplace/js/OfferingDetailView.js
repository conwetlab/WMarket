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

    sidebars: {
        right: $('#right-sidebar')
    }

};

$(function () {

    wmarket.navbar.search.attr('disabled', "disabled");
    wmarket.navbar.searchField.attr('disabled', "disabled");
    wmarket.navbar.searchFilters.attr('disabled', "disabled");

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

});
