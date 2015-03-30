/**
 *  Copyright (c) 2015, CoNWeT Lab., Universidad Politécnica de Madrid
 *  Code licensed under BSD 3-Clause (https://github.com/conwetlab/WMarket/blob/master/LICENSE)
 */


$(function () {

    "use strict";

    $('.panel .opt-collapse').on('click', function (event) {
        var panelElement = this.parentElement.parentElement.parentElement;

        event.preventDefault();

        if (panelElement.classList.contains('collapsed')) {
          panelElement.classList.remove('collapsed');
          this.firstElementChild.classList.add('fa-caret-up');
          this.firstElementChild.classList.remove('fa-caret-down');
        } else {
          panelElement.classList.add('collapsed');
          this.firstElementChild.classList.add('fa-caret-down');
          this.firstElementChild.classList.remove('fa-caret-up');
        }

        event.stopPropagation();
    });

    $('.panel .opt-remove').on('click', function (event) {
        var formName = this.getAttribute('data-resource');

        event.preventDefault();
        document[formName].submit();
        event.stopPropagation();
    });

});
