/*
 * Customised javascript
 * author: yeoteye
 * date: April 06 2016
 */

$(document).ready(function() {
	/* Register Form */
	/* Hide all except first fieldset */
    $('.btn-next').click(function() {

        current_fs = $(this).parent();
        next_fs = $(this).parent().next();

        $('.current').removeClass('current').hide()
            .next().show().addClass('current');
            //activate next step on progressbar using the index of next_fs
            // $("#progressbar li").eq($("fieldset").index(next_fs)).addClass("active");
    });

    $('.btn-prev').click(function() {

        current_fs = $(this).parent();
         previous_fs = $(this).parent().prev();

        $('.current').removeClass('current').hide()
            .prev().show().addClass('current');
            //de-activate current step on progressbar
            // $("#progressbar li").eq($("fieldset").index(current_fs)).removeClass("active");
    });
    
    $('.apply-create-team').click(function() {
        current_fs = $(this).parent();
        previous_fs = $(this).parent().prev();

       $('.current').removeClass('current').hide()
           $('#step4_1').show().addClass('current');
    });
    
    $('.apply-join-team').click(function() {
        current_fs = $(this).parent();
        previous_fs = $(this).parent().prev();

       $('.current').removeClass('current').hide()
           $('#step4_2').show().addClass('current');
    });
    
    $('.btn-prev-selection').click(function() {

        current_fs = $(this).parent();
         previous_fs = $(this).parent().prev();

        $('.current').removeClass('current').hide()
            $('#step3').show().addClass('current');
        
        // clear fields for Create New Team
        var teamNameField = document.getElementById('teamName');
        teamNameField.value = teamNameField.defaultValue;
        
        // clear fields for Join Existing Team
        var joinTeamNameField = document.getElementById('joinTeamName');
        joinTeamNameField.value = joinTeamNameField.defaultValue;
    });
    
    //-----------------experiment page---------------------
    // tooltip hover
    $('[data-toggle="tooltip"]').tooltip();
    
    //-----------------team page-----------------
    // withdraw
    $('#confirm-withdraw').on('click', '.withdraw-ok', function(e) {
    	var $modalDiv = $(e.delegateTarget);
    	var teamName = $(this).data('teamName');
    });
    $('#confirm-withdraw').on('show.bs.modal', function(e) {
    	var data = $(e.relatedTarget).data();
    	$('.teamName', this).text(data.teamName);
    	$('.withdraw-ok', this).data('teamName', data.teamName);
    });
});