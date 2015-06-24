/**
 * Copyright (c) 2015, CoNWeT Lab., Universidad Politécnica de Madrid
 * Code licensed under BSD 3-Clause (https://github.com/conwetlab/WMarket/blob/master/LICENSE)
 */

(function (ns, utils) {

    ns.Review = function Review(data) {

        var authorAvatar = $('<div class="author-avatar">').append(
            $('<span class="image-thumbnail image-thumbnail-sm">').append(
                $('<span class="image image-circle image-primary-ligther">').append(
                  $('<span class="fa fa-user fa-inverse"></span>'))));

        var reviewAuthor = $('<div class="review-author">').append(
            $('<div class="author-name">').text(data.user.displayName),
            $('<div class="author-rating">').append(
                $('<span class="fa fa-star">').text(data.score),
                $('<span class="review-date">').append(formatDate(data.lastModificationDate))));

        this.element = $('<div class="review">').append(
            $('<div class="review-heading">').append(authorAvatar, reviewAuthor),
            $('<div class="review-body">').text(data.comment));
    };

    ns.Review.prototype.get = function get() {
        return this.element;
    };

    var ONE_MINUTE = 60 * 1000;
    var ONE_HOUR = 60 * ONE_MINUTE; /* ms */
    var ONE_DAY  = 24 * ONE_HOUR; /* ms */

    function formatDate(updated) {
        return (new Date(updated)).toDateString() + isRecent(updated);
    }

    function formatHours(timeAgo) {
        var time, unit;

        if (timeAgo >= ONE_HOUR) {
            time = timeAgo / ONE_HOUR;
            unit = 'hour';
        } else if (timeAgo >= ONE_MINUTE) {
            time = timeAgo / ONE_MINUTE;
            unit = 'minute';
        } else {
            time = timeAgo / 1000;
            unit = 'second';

            if (Math.round(time) < 1) {
                return 'right now';
            }
        }

        time = Math.round(time);

        return time + ' ' + (time < 2 ? unit : unit + 's') + ' ago';
    }

    function isRecent(updated) {
        var timeAgo = $.now() - updated;

        return timeAgo < ONE_DAY ? '<span class="recent-review">' + formatHours(timeAgo) + '</span>' : '';
    }

})(app.components, app.utils);
