/**
 * Copyright (c) 2015, CoNWeT Lab., Universidad Politécnica de Madrid
 * Code licensed under BSD 3-Clause (https://github.com/conwetlab/WMarket/blob/master/LICENSE)
 */

(function (ns) {

    "use strict";

    ns.storeForm = app.createForm('store_form', [
        new app.fields.TextField('displayName', {
            label: "Name",
            minlength: 3,
            maxlength: 100,
            regexp: new RegExp("^[a-zA-Z0-9. -]+$", "i"),
            errorMessages: {
                invalid: "This field only accepts letters, numbers, white spaces, dots and hyphens."
            }
        }),
        new app.fields.URLField('url', {
            label: "Website URL"
        }),
        new app.fields.FileField('image', {
            label: "Image",
            required: false,
            maxvalue: {
                size: 3
            },
            controlAttrs: {
                accept: ".png"
            }
        }),
        new app.fields.LongTextField('comment', {
            label: "Comment",
            required: false,
            maxlength: 200,
            controlAttrs: {
                rows: 4
            }
        })
    ]);

})(app.view);
