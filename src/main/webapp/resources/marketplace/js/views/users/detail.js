/**
 * Copyright (c) 2015, CoNWeT Lab., Universidad Politécnica de Madrid
 * Code licensed under BSD 3-Clause (https://github.com/conwetlab/WMarket/blob/master/LICENSE)
 */

(function (ns, utils) {

    "use strict";

    if ($('form[name="account_update_form"]').length) {
        ns.accountUpdateForm = app.createForm('account_update_form', [
            new app.fields.TextField('userName', {
                label: "Username",
                readonly: true
            }),
            new app.fields.TextField('displayName', {
                label: "Full name",
                minlength: 3,
                maxlength: 30,
                regexp: new RegExp(utils.patternDisplayName, "i"),
                errorMessages: {
                    invalid: "Enter at most one valid word."
                }
            }),
            new app.fields.EmailField('email', {
                label: "Email"
            }),
            new app.fields.TextField('company', {
                label: "Company",
                required: false,
                minlength: 3,
                maxlength: 30
            })
        ]);
    }

    if ($('.delete-account').length) {
        app.bindModal($('.delete-account'), '.modal-delete', {
            context: {},
            before: function (context, $source, $modal, next) {
                next();
            },
            submit: function (context, $btn, next) {
                document.forms.account_delete_form.submit();
            },
            after: function (context, next) {
                next();
            }
        });
    }

})(app.view, app.utils);
