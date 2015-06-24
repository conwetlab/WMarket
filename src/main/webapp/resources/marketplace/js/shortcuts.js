/**
 * Copyright (c) 2015, CoNWeT Lab., Universidad Politécnica de Madrid
 * Code licensed under BSD 3-Clause (https://github.com/conwetlab/WMarket/blob/master/LICENSE)
 */

(function (ns) {

    var stores = new app.components.StoreList();

    ns.createStore = function createStore(storeInfo) {
        return stores.add(storeInfo);
    };

    ns.createOffering = function createOffering(offeringInfo) {
        return new ns.components.Offering(offeringInfo);
    };

    ns.createAlert = function createAlert(alertType, alertMessage, extraClass) {
        return app.components.alerts[alertType](alertMessage, extraClass);
    };

    ns.getStore = function getStore(name) {
        return stores.get(name);
    };

    ns.createForm = function createForm(formName, formFields) {
        return new app.forms.FormValidator(formName, formFields);
    };

    ns.bindModal = function bindModal($source, selector, options) {
        $source.on('click', function (event) {
            findModal(selector, $(this), options.context, {
                before: options.before,
                submit: options.submit,
                after: options.after
            });
        });
    };

    function showModal() {
        this.addClass('slipped');
    }

    function hideModal() {
        this.removeClass('slipped');
    }

    function findModal(selector, $source, context, callbacks) {
        var $modal = $(selector);

        $modal.find('[data-cancel]').off('click').on('click', function (event) {
            callbacks.after(context, hideModal.bind($modal));
        });
        $modal.find('[data-submit]').off('click').on('click', function (event) {
            callbacks.submit(context, $(this), hideModal.bind($modal));
        });
        callbacks.before(context, $source, $modal, showModal.bind($modal));
    };

})(app);
