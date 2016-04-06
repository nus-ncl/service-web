/*
 * Customised javascript
 * author: yeoteye
 * date: April 06 2016
 */

/* Register Form */
/* Hide all except first fieldset */
$(document).ready(function() {
    $('.btn-next').click(function() {

        current_fs = $(this).parent();
        next_fs = $(this).parent().next();

        $('.current').removeClass('current').hide()
            .next().show().addClass('current');
            //activate next step on progressbar using the index of next_fs
            $("#progressbar li").eq($("fieldset").index(next_fs)).addClass("active");
    });

    $('.btn-prev').click(function() {

        current_fs = $(this).parent();
         previous_fs = $(this).parent().prev();

        $('.current').removeClass('current').hide()
            .prev().show().addClass('current');
            //de-activate current step on progressbar
            $("#progressbar li").eq($("fieldset").index(current_fs)).removeClass("active");
    });
});