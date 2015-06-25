/**
 * Copyright (c) 2015, CoNWeT Lab., Universidad Politécnica de Madrid
 * Code licensed under BSD 3-Clause (https://github.com/conwetlab/WMarket/blob/master/LICENSE)
 */

(function (ns, utils) {

    ns.review = {};

    ns.review.list = function list($target, next) {
        app.requests.list(ns.urls.review_collection, {
            $target: $target,
            queryString: { detailed: true, orderBy: 'lastModificationDate', desc: true },
            success: next,
            $alert: app.createAlert('warning', utils.format('This %(model)s does not have reviews yet. You can be the first.', {
                model: ns.model
            }))
        });
    };

    ns.review.create = function create(review, next) {
        app.requests.create(ns.urls.review_collection, {
            data: review,
            success: function (jqXHR) {
                review.id = jqXHR.getResponseHeader('location').split('/').pop();
                app.user.review = review;
                ns.review.refresh();
                next(review);
            }
        });
    };

    ns.review.update = function update(review, next) {
        app.requests.update(ns.urls.review_entry, {
            method: 'POST',
            kwargs: { review: app.user.review.id },
            data: review,
            success: function () {
                review.id = app.user.review.id;
                app.user.review = review;
                ns.review.refresh();
                next(review);
            }
        });
    };

    ns.review.destroy = function destroy(next) {
        app.requests.destroy(ns.urls.review_entry, {
            kwargs: { review: app.user.review.id },
            success: function () {
                app.user.review = null;
                ns.review.refresh();
                next();
            }
        });
    };

    ns.review.$rating = $('.rating[data-target]');

    ns.review.refresh = function refresh() {
        repaintRating();

        ns.find(function (data) {
            ns.$ratingOverall.text(data.averageScore.toFixed(1));
        });

        if (ns.$reviewList.length) {
            ns.review.list(ns.$reviewList, function (reviews, jqXHR, $target) {
                reviews.forEach(function (data) {
                    $target.append((new app.components.Review(data)).get());
                });
            });
        }
    };

    app.bindModal($('.rating[data-target] :radio'), ns.review.$rating.attr('data-target'), {
        context: { comment: new app.fields.LongTextField('comment', {
            label: "Comment",
            maxlength: 1000,
            controlAttrs: { rows: 4 }
        })},
        before: function (context, $source, $modal, next) {
            $modal.find('[name="rating"][value="' + $source.val() + '"]')
                .prop('checked', true);

            $('form[name="review_form"]').append(context.comment.get());

            if (app.user.review != null) {
                context.comment.addInitialValue(app.user.review.comment);
                $modal.find('.btn-delete').show();
                $modal.find('.btn-update').text('Save');
            } else {
                $modal.find('.btn-delete').hide();
                $modal.find('.btn-update').text('Submit');
            }
            next();
        },
        submit: function (context, $btn, next) {
            if ($btn.hasClass('btn-update')) {
                context.comment.validate();

                if (context.comment.state == 1) {
                    var review = {
                            score: $('form[name="review_form"] [name="rating"]:checked').val(),
                            comment: context.comment.value
                        },
                        action = app.user.review != null ? 'update' : 'create';

                    ns.review[action](review, function () {
                        context.comment.empty().remove();
                        next();
                    });
                }
            } else {
                ns.review.destroy(function () {
                    context.comment.empty().remove();
                    next();
                });
            }
        },
        after: function (context, next) {
            context.comment.empty().remove();
            repaintRating();
            next();
        }
    });

    ns.review.refresh();

    function repaintRating() {
        if (app.user.review != null) {
            ns.review.$rating
                .find('[name="rating"][value="' + app.user.review.score + '"]')
                .prop('checked', true);
        } else {
            ns.review.$rating
                .find('[name="rating"]:checked')
                .prop('checked', false);
        }
    }

})(app.view, app.utils);
