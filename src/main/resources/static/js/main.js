/*
 * Customised javascript
 * author: yeoteye
 * date: April 06 2016
 */

$(document).ready(function() {
	

	/* Scroll to section */
	$('.tohash').click(function() {

		// animate
		$('html, body').animate({
			scrollTop: $("#joinUs").offset().top
		}, 'slow');

	});

	/* Scroll to section */
	$('.tohash1').click(function() {

		// animate
		$('html, body').animate({
			scrollTop: $("#home-slider").offset().top
		}, 'slow');

	});
		
	
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
    
    //------------------to show the news section--------------
    $('.wp4').waypoint(function() {
    	$('.wp4').addClass('animated fadeInDown');
    }, {
    	offset: '75%'
    });
    
});


jQuery(function($) {

	//Preloader
	var preloader = $('.preloader');
	$(window).load(function(){
		preloader.remove();
	});

	//#main-slider
	var slideHeight = $(window).height();
	$('#home-slider .item').css('height',slideHeight);

	$(window).resize(function(){'use strict',
		$('#home-slider .item').css('height',slideHeight);
	});
	
	/*
	$(window).on('scroll', function(){
		if( $(window).scrollTop()>slideHeight ){
			$('.main-nav').addClass('navbar-fixed-top');
		} else {
			$('.main-nav').removeClass('navbar-fixed-top');
		}
	});
	*/
	
	// Navigation Scroll
	$(window).scroll(function(event) {
		Scroll();
	});

	$('.navbar-collapse ul li a').on('click', function() {  
		$('html, body').animate({scrollTop: $(this.hash).offset().top - 5}, 1000);
		return false;
	});

	// User define function
	function Scroll() {
		var contentTop      =   [];
		var contentBottom   =   [];
		var winTop      =   $(window).scrollTop();
		var rangeTop    =   200;
		var rangeBottom =   500;
		$('.navbar-collapse').find('.scroll a').each(function(){
			contentTop.push( $( $(this).attr('href') ).offset().top);
			contentBottom.push( $( $(this).attr('href') ).offset().top + $( $(this).attr('href') ).height() );
		})
		$.each( contentTop, function(i){
			if ( winTop > contentTop[i] - rangeTop ){
				$('.navbar-collapse li.scroll')
				.removeClass('active')
				.eq(i).addClass('active');			
			}
		})
	};

	$('#tohash').on('click', function(){
		$('html, body').animate({scrollTop: $(this.hash).offset().top - 5}, 1000);
		return false;
	});
	
	//Initiat WOW JS
	new WOW().init();
	//smoothScroll
	smoothScroll.init();
	
	// Progress Bar
	$('#about-us').bind('inview', function(event, visible, visiblePartX, visiblePartY) {
		if (visible) {
			$.each($('div.progress-bar'),function(){
				$(this).css('width', $(this).attr('aria-valuetransitiongoal')+'%');
			});
			$(this).unbind('inview');
		}
	});

	//Countdown
	$('#features').bind('inview', function(event, visible, visiblePartX, visiblePartY) {
		if (visible) {
			$(this).find('.timer').each(function () {
				var $this = $(this);
				$({ Counter: 0 }).animate({ Counter: $this.text() }, {
					duration: 2000,
					easing: 'swing',
					step: function () {
						$this.text(Math.ceil(this.Counter));
					}
				});
			});
			$(this).unbind('inview');
		}
	});

	// Portfolio Single View
	$('#portfolio').on('click','.folio-read-more',function(event){
		event.preventDefault();
		var link = $(this).data('single_url');
		var full_url = '#portfolio-single-wrap',
		parts = full_url.split("#"),
		trgt = parts[1],
		target_top = $("#"+trgt).offset().top;

		$('html, body').animate({scrollTop:target_top}, 600);
		$('#portfolio-single').slideUp(500, function(){
			$(this).load(link,function(){
				$(this).slideDown(500);
			});
		});
	});

	// Close Portfolio Single View
	$('#portfolio-single-wrap').on('click', '.close-folio-item',function(event) {
		event.preventDefault();
		var full_url = '#portfolio',
		parts = full_url.split("#"),
		trgt = parts[1],
		target_offset = $("#"+trgt).offset(),
		target_top = target_offset.top;
		$('html, body').animate({scrollTop:target_top}, 600);
		$("#portfolio-single").slideUp(500);
	});

	// Contact form
	var form = $('#main-contact-form');
	form.submit(function(event){
		event.preventDefault();
		var form_status = $('<div class="form_status"></div>');
		$.ajax({
			url: $(this).attr('action'),
			beforeSend: function(){
				form.prepend( form_status.html('<p><i class="fa fa-spinner fa-spin"></i> Email is sending...</p>').fadeIn() );
			}
		}).done(function(data){
			form_status.html('<p class="text-success">Thank you for contact us. As early as possible  we will contact you</p>').delay(3000).fadeOut();
		});
	});
	
});

/***************** Flexsliders ******************/
$(window).load(function() {

	$('#portfolioSlider').flexslider({
		animation: "slide",
		directionNav: false,
		controlNav: true,
		touch: false,
		pauseOnHover: true,
		start: function() {
			$.waypoints('refresh');
		}
	});

	$('#servicesSlider').flexslider({
		animation: "slide",
		directionNav: false,
		controlNav: true,
		touch: true,
		pauseOnHover: true,
		start: function() {
			$.waypoints('refresh');
		}
	});

	$('#teamSlider').flexslider({
		animation: "slide",
		directionNav: false,
		controlNav: true,
		touch: true,
		pauseOnHover: true,
		start: function() {
			$.waypoints('refresh');
		}
	});

});

function PasswordWidget(divid,pwdname)
{
	this.maindivobj = document.getElementById(divid);
	this.pwdobjname = pwdname;

	this.MakePWDWidget=_MakePWDWidget;

	this.showing_pwd=1;
	this.txtShow = 'Show';
	this.txtMask = 'Mask';
	// this.txtGenerate = 'Generate';
	this.txtStrength = 'Strength: ';
	this.txtWeak='Weak';
	this.txtMedium='Medium';
	this.txtGood='Excellent';

	this.enableShowMask=false;
	// this.enableGenerate=true;
	this.enableShowStrength=true;
	this.enableShowStrengthStr=true;

}

function _MakePWDWidget()
{
	var code="";
	var pwdname = this.pwdobjname;

	this.pwdfieldid = pwdname+"_id";

	code += "<div class='form-group'><input th:field='*{password}' type='password' class='form-control pwdfield' name='"+pwdname+"' id='"+this.pwdfieldid+"'  placeholder='Password'></div>";

	code += "<div class='form-group'><input type='password' class='form-control' placeholder='Confirm Password' th:field='*{confirmPassword}' name='confirmPassword'/></div>";

	this.pwdtxtfield=pwdname+"_text";

	this.pwdtxtfieldid = this.pwdtxtfield+"_id";

	code += "<input type='text' class='pwdfield' name='"+this.pwdtxtfield+"' id='"+this.pwdtxtfieldid+"' style='display: none;'>";

	this.pwdshowdiv = pwdname+"_showdiv";

	this.pwdshow_anch = pwdname + "_show_anch";

	code += "<div class='pwdopsdiv' id='"+this.pwdshowdiv+"'><a href='#' id='"+this.pwdshow_anch+"'>"+this.txtShow+"</a></div>";

	this.pwdgendiv = pwdname+"_gendiv";

	this.pwdgenerate_anch = pwdname + "_gen_anch";

	code += "<div class='pwdopsdiv'id='"+this.pwdgendiv+"'><a href='#' id='"+this.pwdgenerate_anch+"'>"+this.txtGenerate+"</a></div>";

	this.pwdstrengthdiv = pwdname + "_strength_div";

	code += "<div class='pwdstrength' id='"+this.pwdstrengthdiv+"'>";

	this.pwdstrengthbar = pwdname + "_strength_bar";

	code += "<div class='pwdstrengthbar' id='"+this.pwdstrengthbar+"'></div>";

	this.pwdstrengthstr = pwdname + "_strength_str";

	code += "<div class='pwdstrengthstr' id='"+this.pwdstrengthstr+"'></div>";

	code += "</div>";

	this.maindivobj.innerHTML = code;

	this.pwdfieldobj = document.getElementById(this.pwdfieldid);

	this.pwdfieldobj.pwdwidget=this;

	this.pwdstrengthbar_obj = document.getElementById(this.pwdstrengthbar);

	this.pwdstrengthstr_obj = document.getElementById(this.pwdstrengthstr);

	this._showPasswordStrength = passwordStrength;

	this.pwdfieldobj.onkeyup=function(){ this.pwdwidget._onKeyUpPwdFields(); }

	this._showGeneatedPwd = showGeneatedPwd;

	this.generate_anch_obj = document.getElementById(this.pwdgenerate_anch);

	this.generate_anch_obj.pwdwidget=this;

	this.generate_anch_obj.onclick = function(){ this.pwdwidget._showGeneatedPwd(); }

	this._showpwdchars = showpwdchars;

	this.show_anch_obj = document.getElementById(this.pwdshow_anch);

	this.show_anch_obj.pwdwidget = this;

	this.show_anch_obj.onclick = function(){ this.pwdwidget._showpwdchars();}

	this.pwdtxtfield_obj = document.getElementById(this.pwdtxtfieldid);

	this.pwdtxtfield_obj.pwdwidget=this;

	this.pwdtxtfield_obj.onkeyup=function(){ this.pwdwidget._onKeyUpPwdFields(); }


	this._updatePwdFieldValues = updatePwdFieldValues;

	this._onKeyUpPwdFields=onKeyUpPwdFields;

	if(!this.enableShowMask)
	{ document.getElementById(this.pwdshowdiv).style.display='none';}

	if(!this.enableGenerate)
	{ document.getElementById(this.pwdgendiv).style.display='none';}

	if(!this.enableShowStrength)
	{ document.getElementById(this.pwdstrengthdiv).style.display='none';}

	if(!this.enableShowStrengthStr)
	{ document.getElementById(this.pwdstrengthstr).style.display='none';}
}

function onKeyUpPwdFields()
{
	this._updatePwdFieldValues();
	this._showPasswordStrength();
}

function updatePwdFieldValues()
{
	if(1 == this.showing_pwd)
	{
		this.pwdtxtfield_obj.value = this.pwdfieldobj.value;
	}
	else
	{
		this.pwdfieldobj.value = this.pwdtxtfield_obj.value;
	}
}

function showpwdchars()
{
	var innerText='';
	var pwdfield = this.pwdfieldobj;
	var pwdtxt = this.pwdtxtfield_obj;
	var field;
	if(1 == this.showing_pwd)
	{
		this.showing_pwd=0;
		innerText = this.txtMask;

		pwdtxt.value = pwdfield.value;
		pwdfield.style.display='none';
		pwdtxt.style.display='';
		pwdtxt.focus();
	}
	else
	{
		this.showing_pwd=1;
		innerText = this.txtShow;
		pwdfield.value = pwdtxt.value;
		pwdtxt.style.display='none';
		pwdfield.style.display='';
		pwdfield.focus();

	}
	this.show_anch_obj.innerHTML = innerText;

}

function passwordStrength()
{
	var colors = new Array();
	colors[0] = "#cccccc";
	colors[1] = "#ff0000";
	colors[2] = "#ff5f5f";
	colors[3] = "#56e500";
	colors[4] = "#4dcd00";
	colors[5] = "#399800";

	var pwdfield = this.pwdfieldobj;
	var password = pwdfield.value

	var score   = 0;

	if (password.length > 6) {score++;}

	if ( ( password.match(/[a-z]/) ) &&
		( password.match(/[A-Z]/) ) ) {score++;}

	if (password.match(/\d+/)){ score++;}

	if ( password.match(/[^a-z\d]+/) )	{score++};

	if (password.length > 12){ score++;}

	var color=colors[score];
	var strengthdiv = this.pwdstrengthbar_obj;

	strengthdiv.style.background=colors[score];

	if (password.length <= 0)
	{
		strengthdiv.style.width=0;
	}
	else
	{
		strengthdiv.style.width=(score+1)*10+'px';
	}

	var desc='';
	if(password.length < 1){desc='';}
	else if(score<3){ desc = this.txtStrength + this.txtWeak; }
	else if(score<4){ desc = this.txtStrength + this.txtMedium; }
	else if(score>=4){ desc= this.txtStrength + this.txtGood; }

	var strengthstrdiv = this.pwdstrengthstr_obj;
	strengthstrdiv.innerHTML = desc;
}

function getRand(max)
{
	return (Math.floor(Math.random() * max));
}

function shuffleString(mystr)
{
	var arrPwd=mystr.split('');

	for(i=0;i< mystr.length;i++)
	{
		var r1= i;
		var r2=getRand(mystr.length);

		var tmp = arrPwd[r1];
		arrPwd[r1] = arrPwd[r2];
		arrPwd[r2] = tmp;
	}

	return arrPwd.join("");
}

function showGeneatedPwd()
{
	var pwd = generatePWD();
	this.pwdfieldobj.value= pwd;
	this.pwdtxtfield_obj.value =pwd;

	this._showPasswordStrength();
}

function generatePWD()
{
	var maxAlpha = 26;
	var strSymbols="~!@#$%^&*(){}?><`=-|][";
	var password='';
	for(i=0;i<3;i++)
	{
		password += String.fromCharCode("a".charCodeAt(0) + getRand(maxAlpha));
	}
	for(i=0;i<3;i++)
	{
		password += String.fromCharCode("A".charCodeAt(0) + getRand(maxAlpha));
	}
	for(i=0;i<3;i++)
	{
		password += String.fromCharCode("0".charCodeAt(0) + getRand(10));
	}
	for(i=0;i<4;i++)
	{
		password += strSymbols.charAt(getRand(strSymbols.length));
	}

	password = shuffleString(password);
	password = shuffleString(password);
	password = shuffleString(password);

	return password;
}

$( document ).ready(function() {

	// let's Hide Items
	$( ".cc-contactpop form").hide();
	$( ".cc-contactpop i").hide();
	$( ".cc-contactpop p").hide();

	// Show on elements ".slideDown"
	$( ".cc-contactpop" ).click(function() {
		$( ".cc-contactpop form").slideDown(500)
		$( ".cc-contactpop p").slideDown(500)
		$( ".cc-contactpop i").slideDown(500)
	});

	// Close Hidden Part
	$( ".cc-contactpop i" ).click(function() {
		$(this).slideUp(500)
		$( ".cc-contactpop p").slideUp(500)
		$( ".cc-contactpop form").slideUp(500)
		return false
	});

});

// Add show/hide button
var sShowHideBtn = '<button class="contact-button-link show-hide-contact-bar"><span class="fa fa-angle-left"></span></button>';
oContainer.append(sShowHideBtn);

var i;
for ( i in settings.buttons ) {
	var bs = settings.buttons[i],
		sLink = bs.link,
		active = bs.use;

	// Check if element is active
	if (active) {

		// Change the link for phone and email when needed
		if (bs.type === 'phone') {
			sLink = 'tel:' + bs.link;
		} else if (bs.type === 'email') {
			sLink = 'mailto:' + bs.link;
		}

		// Insert the links
		var sIcon = '<span class="fa fa-' + bs.icon + '"></span>',
			sButton = '<a href="' + sLink +
				'" class="contact-button-link cb-ancor ' + bs.class + '" ' +
				(bs.title ? 'title="' + bs.title + '"' : '') +
				(bs.extras ? bs.extras : '') +
				'>' + sIcon + '</a>';
		oContainer.append(sButton);
	}
}

