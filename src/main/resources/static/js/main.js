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


	//-----------------registration page -----------------
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

	$('#registerApplyNewTeamBtn').on("click", function () {
		// clear fields for Join Existing Team
		var joinTeamNameField = document.getElementById('joinTeamName');
		joinTeamNameField.value = '';
	});

	$('#registerApplyJoinTeamBtn').on("click", function() {
		// clear fields for Create New Team
		var teamNameField = document.getElementById('teamName');
		teamNameField.value = '';
		document.getElementById('teamDescription').value = '';
	});

	// make the tab in the tab group active
	// when join existing team is selected
	if (document.getElementById('joinTeamName') != null && document.getElementById('joinTeamName').value) {
		// change the tab to join team being selected
		$('#registerApplyJoinTeamTab').addClass('active');
		$('#registerApplyNewTeamTab').removeClass('active');

		// change the tab-content to the join team
		$('#registerNewTeamDiv').removeClass('in active');
		$('#registerJoinTeamDiv').addClass('in active');
	}
    
    //-----------------experiment page---------------------
    // tooltip hover
    $('[data-toggle="tooltip"]').tooltip();

	// prevent start and delete button from multi clicks
	$(".click-once-button").click(function() {
		$(".click-once-button").prop("disabled", true).addClass("disabled");
		// TODO: refresh the page after click
	});

	// declare another new class in case if click on button on exp page also disables button on admin page
	// prevent accept and reject for admin pending team approvals from multi clicks
	$(".admin-click-once-button").click(function() {
		$(".admin-click-once-button").prop("disabled", true).addClass("disabled");
	});

	//start experiment
    $('#startExperiment').on('show.bs.modal', function (event) {
    	var anchor = $(event.relatedTarget);
		var startExperiment = anchor.data('start');
        var experimentName = anchor.data('name');
		var modal = $(this);
        modal.find('#experimentName').text(experimentName);
        modal.find('#startButton').attr('href', startExperiment);
    })

	//stop experiment
    $('#stopExperiment').on('show.bs.modal', function (event) {
        var anchor = $(event.relatedTarget);
        var stopExperiment = anchor.data('stop');
        var experimentName = anchor.data('name');
        var modal = $(this);
        modal.find('#experimentName').text(experimentName);
        modal.find('#stopButton').attr('href', stopExperiment);
    })

    //remove experiment
    $('#removeExperiment').on('show.bs.modal', function (event) {
        var anchor = $(event.relatedTarget);
        var removeExperiment = anchor.data('remove');
        var experimentName = anchor.data('name');
        var modal = $(this);
        modal.find('#experimentName').text(experimentName);
        modal.find('#removeButton').attr('href', removeExperiment);
    })
    
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
	

    //-----------------data page-----------------
    // modal to show dataset details
    $('#datasetModal').on('show.bs.modal', function (event) {
        var anchor = $(event.relatedTarget);
        var datasetLabel = anchor.data('name');
        var datasetDesc = anchor.data('desc');
        var datasetKeywords = anchor.data('keywords');
        var datasetCategory = anchor.data('category');
        var datasetLicense = anchor.data('license');
        var datasetOwner = anchor.data('owner');
        var datasetRelease = anchor.data('release');
        var datasetVisible = anchor.data('visible');
        var datasetAccess = anchor.data('access');
		var datasetEdit = anchor.data('edit');
        var modal = $(this);
		modal.find('#datasetButton').hide();
        modal.find('#datasetLabel').text(datasetLabel);
        modal.find('#datasetDesc').text(datasetDesc);
        if (typeof datasetKeywords === "undefined" || !datasetKeywords.trim()) {
            modal.find('#datasetKeywords').text("");
        } else {
            modal.find('#datasetKeywords').text(datasetKeywords);
        }
        modal.find('#datasetCategory').text(datasetCategory);
        modal.find('#datasetLicense').text(datasetLicense);
        modal.find('#datasetOwner').text(datasetOwner);
        modal.find('#datasetRelease').text(datasetRelease);
        modal.find('#datasetVisible').text(datasetVisible);
        modal.find('#datasetAccess').text(datasetAccess);
        if (typeof datasetEdit === "undefined" || !datasetEdit.trim()) {
            //empty string
        } else {
            modal.find('#datasetButton').attr('href', datasetEdit);
            modal.find('#datasetButton').show();
        }
    });

    // modal to download dataset resource
    $('#downloadModal').on('show.bs.modal', function (event) {
        var anchor = $(event.relatedTarget);
        var dataId = anchor.data('id');
        var downloadLabel = anchor.data('name');
        var license = anchor.data('license');
        var link = anchor.data('link');
        var resourceuris = anchor.data('resourceuris');
        var resourceids = anchor.data('resourceids');
        var resourcemalicious = anchor.data('resourcemalicious');
        var upload = anchor.data('upload');
        var modal = $(this);
        modal.find('#uploadButton').hide();
        modal.find('#downloadLabel').text("Resources of " + downloadLabel);
<<<<<<< HEAD
        modal.find('#dataset').text(downloadLabel);
        modal.find('#license').text(license);
        modal.find('#link').text(link);
        modal.find('#link').attr('href', link);
=======

        // display if a collection of resources is malicious
        // change the information to display accordingly
        if (resourcemalicious) {
            modal.find('#downloadMaliciousLabel').addClass("malicious_resources");
        	modal.find('#downloadMaliciousLabel').html("This dataset contains <strong>malicious</strong> resources as detected by our deployed scanner, ClamAV.<br/> Please handle with care.");
        } else {
        	modal.find('#downloadMaliciousLabel').addClass("clean_resources");
            modal.find('#downloadMaliciousLabel').html("All resources listed below are <strong>clean</strong>. NCL has made the best effort and cannot guarantee that any resources within this dataset is free of viruses. Remember to use your personal anti-virus software after downloading the resources.");
		}

>>>>>>> set is data entity contains malicious resources or not
        modal.find('ul').empty();
        for (i = 0; i < resourceids.length; i++) {
            modal.find('ul').append("<li><a href='/data/" + dataId + "/resources/" + resourceids[i] + "'>" + resourceuris[i] + "</a></li>");
        }
        if (typeof upload === "undefined" || !upload.trim()) {
            //empty string
        } else {
            modal.find('#uploadButton').attr('href', upload);
            modal.find('#uploadButton').show();
        }
    });

    // modal to request dataset resource
    $('#requestModal').on('show.bs.modal', function (event) {
        var anchor = $(event.relatedTarget);
        var requestLabel = anchor.data('name');
        var owner = anchor.data('owner');
        var request = anchor.data('request');
        var modal = $(this);
        modal.find('#requestLabel').text("Resources of " + requestLabel);
        modal.find('#owner').text(owner);
        modal.find('#dataset').text(requestLabel);
        modal.find('#requestForm').attr('action', request);
    });

    // modal to confirm dataset delete
    $('#deleteModal').on('show.bs.modal', function (event) {
        var anchor = $(event.relatedTarget);
        var deleteLabel = anchor.data('name');
        var datasetDesc = anchor.data('desc');
        var datasetKeywords = anchor.data('keywords');
        var datasetCategory = anchor.data('category');
        var datasetLicense = anchor.data('license');
        var datasetOwner = anchor.data('owner');
        var datasetRelease = anchor.data('release');
        var datasetVisible = anchor.data('visible');
        var datasetAccess = anchor.data('access');
        var deleteButton = anchor.data('delete');
        var modal = $(this);
        modal.find('#deleteLabel').text("Delete " + deleteLabel + "?");
        modal.find('#datasetDesc').text(datasetDesc);
        modal.find('#datasetKeywords').text(datasetKeywords);
        modal.find('#datasetCategory').text(datasetCategory);
        modal.find('#datasetLicense').text(datasetLicense);
        modal.find('#datasetOwner').text(datasetOwner);
        modal.find('#datasetRelease').text(datasetRelease);
        modal.find('#datasetVisible').text(datasetVisible);
        modal.find('#datasetAccess').text(datasetAccess);
        modal.find('#deleteButton').attr('href', deleteButton);
    });

    // modal to confirm dataset resource delete
    $('#deleteResourceModal').on('show.bs.modal', function (event) {
        var anchor = $(event.relatedTarget);
        var deleteLabel = anchor.data('id');
        var resourceUri = anchor.data('uri');
        var deleteButton = anchor.data('delete');
        var modal = $(this);
        modal.find('#deleteLabel').text("Delete " + deleteLabel + "?");
        modal.find('#resourceUri').text(resourceUri);
        modal.find('#deleteButton').attr('href', deleteButton);
    });

    //-----------------get topology image-----------------
    $('#topoModal').on('shown.bs.modal', function (event) {
        var anchor = $(event.relatedTarget);
        var title = anchor.data('title');
        var imgsrc = anchor.data('imgsrc');
        var modal = $(this);
        modal.find('.modal-title').text(title);
        modal.find('#loading').show();
        $.get(imgsrc, function(data) {
            modal.find('#loading').hide();
            modal.find('#topoThumb').attr('src', data);
        });
    });

	//-----------------admin page-----------------
	// modal to show admin - pending teams approval details
	$('#pendingTeamDetailsModal').on('show.bs.modal', function (event) {
		var anchor = $(event.relatedTarget);
		var pendingTeamName = anchor.data('name');
		var pendingTeamDescription = anchor.data('description');
		var pendingTeamOrganisationType = anchor.data('organisation');
		var pendingTeamVisibility = anchor.data('visibility');
		var modal = $(this);
		modal.find('#pendingTeamDetailsLabel').text(pendingTeamName);
		modal.find('#pendingTeamDescription').text(pendingTeamDescription);
		modal.find('#pendingTeamOrganisationType').text(pendingTeamOrganisationType);
		modal.find('#pendingTeamVisibility').text(pendingTeamVisibility);
	});
});

// display the ns file content when users select the predefined scenarios
function displayNsContent() {
	var basic1 = "# This is a simple experiment, containing only one node\r\n\r\nset ns [new Simulator]\r\nsource tb_compat.tcl\r\n\r\n# Add a new node\r\nset n0 [$ns node]\r\n\r\n# Set node OS\r\ntb-set-node-os $n0 Ubuntu1404-64-STD\r\n\r\n$ns rtproto Static\r\n\r\n# Go!\r\n$ns run";
	var basic2 = "# This is a simple experiment with 2 nodes and 10Gb link\r\n\r\nset ns [new Simulator]\r\nsource tb_compat.tcl\r\n\r\n# Add nodes\r\nset n0 [$ns node]\r\nset n1 [$ns node]\r\n\r\n# Add link (use default values)\r\nset link0 [$ns duplex-link $n0 $n1 10Gb 0ms DropTail]\r\n\r\n$ns rtproto Static\r\n\r\n# Go!\r\n$ns run";
	var basic3 = "# This is a simple experiment with 3 nodes in a LAN\r\n\r\nset ns [new Simulator]\r\nsource tb_compat.tcl\r\n\r\n# Add nodes\r\nset node0 [$ns node]\r\nset node1 [$ns node]\r\nset node2 [$ns node]\r\n\r\n# Make LAN\r\nset lan0 [$ns make-lan \"$node0 $node1 $node2\" 10Gb 0ms]\r\n\r\n$ns rtproto Static\r\n$ns run";
	var basic4 = "# This is a simple experiment with 2 nodes and customized link specification\r\n\r\nset ns [new Simulator]\r\nsource tb_compat.tcl\r\n\r\n# Add nodes\r\nset n0 [$ns node]\r\nset n1 [$ns node]\r\n\r\n# Add link and specify link properties\r\nset link0 [$ns duplex-link $n0 $n1 100Mb 200ms DropTail]\r\n\r\n$ns rtproto Static\r\n\r\n# Go!\r\n$ns run";
    var basic5 = "#Start of the script\r\n\r\n# Create a new experiment\r\nset ns [new Simulator]\r\n# Enable tb commands (an extension to TCL)\r\nsource tb_compat.tcl\r\n\r\n# Define a node (a server node)\r\nset node1 [$ns node]\r\n# Same as the above\r\nset node2 [$ns node]\r\n\r\n# Define a switch\r\nset switch0 [$ns node]\r\n# Declare that the switch0 is an OpenFlow Enabled switch\r\ntb-set-hardware $switch0 ofswitch\r\n\r\n# Connect node1 to switch0\r\nset link1 [$ns duplex-link $switch0 $node1 1Gb 0ms DropTail]\r\n# Connect node2 to switch0\r\nset link2 [$ns duplex-link $switch0 $node2 1Gb 0ms DropTail]\r\n\r\n# [Optional] Define a node named \"controller\"\r\n# If you define a controller you can set a custom image\r\n# otherwise the default image for the controller will be loaded\r\nset controller [$ns node]\r\n# If you have defined a controller you have to declare it as below\r\n# Declare that \"controller\" is an SDN Controller \r\ntb-set-hardware $controller ofcontrol\r\n\r\n# Boiler plates\r\n$ns rtproto Static \r\n$ns run \r\n### End of the script";
    var basic6 = "#Start of the script\r\n\r\n# Starting a simulator\r\nset ns [new Simulator]\r\nsource tb_compat.tcl\r\n\r\n# Defining nodes\r\nset node1 [$ns node]\r\nset node2 [$ns node]\r\nset node3 [$ns node]\r\nset node4 [$ns node]\r\n\r\n# Defining Switches\r\nset switch1 [$ns node]\r\ntb-set-hardware $switch1 ofswitch\r\nset switch2 [$ns node]\r\ntb-set-hardware $switch2 ofswitch\r\nset switch3 [$ns node]\r\ntb-set-hardware $switch3 ofswitch\r\nset switch4 [$ns node]\r\ntb-set-hardware $switch4 ofswitch\r\nset switch5 [$ns node]\r\ntb-set-hardware $switch5 ofswitch\r\nset switch6 [$ns node]\r\ntb-set-hardware $switch6 ofswitch\r\nset switch7 [$ns node]\r\ntb-set-hardware $switch7 ofswitch\r\n\r\n# Connecting Links between leaf switches and server nodes 1-4\r\nset link1 [$ns duplex-link $switch1 $node1 1Gb 0ms DropTail]\r\nset link2 [$ns duplex-link $switch2 $node2 1Gb 0ms DropTail]\r\nset link3 [$ns duplex-link $switch3 $node3 1Gb 0ms DropTail]\r\nset link4 [$ns duplex-link $switch4 $node4 1Gb 0ms DropTail]\r\n\r\n# Connecting switches together\r\nset link5 [$ns duplex-link $switch1 $switch5 1Gb 0ms DropTail]\r\nset link6 [$ns duplex-link $switch2 $switch5 1Gb 0ms DropTail]\r\nset link7 [$ns duplex-link $switch3 $switch6 1Gb 0ms DropTail]\r\nset link8 [$ns duplex-link $switch4 $switch6 1Gb 0ms DropTail]\r\nset link9 [$ns duplex-link $switch5 $switch7 1Gb 0ms DropTail]\r\nset link10 [$ns duplex-link $switch6 $switch7 1Gb 0ms DropTail]\r\n\r\n# [Optional] Define a node named \"controller\" \r\n# and declare it as an SDN Controller\r\nset controller [$ns node]\r\ntb-set-hardware $controller ofcontrol\r\n\r\n$ns rtproto Static\r\n$ns run\r\n\r\n### End of the script";

    var x = document.getElementById("selectExpScenario").value;
	var file = "";
	switch(x) {
		case "Scenario 1 - Experiment with a single node":
			file = basic1;
			break;
		case "Scenario 2 - Experiment with 2 nodes and 10Gb link":
			file = basic2;
			break;
		case "Scenario 3 - Experiment with 3 nodes in a LAN":
			file = basic3;
			break;
		case "Scenario 4 - Experiment with 2 nodes and customized link property":
			file = basic4;
			break;
		case "Scenario 5 - Single SDN switch connected to two nodes":
			file = basic5;
			break;
		case "Scenario 6 - Tree Topology with configurable SDN switches":
			file = basic6;
			break;
		default:
			file = basic1;
			break;
	}

	document.getElementById("networkConfig").innerHTML = file;
}

// display the default ns file content
function displayDefaultNsContent() {
	var basic = "# This is a simple experiment, containing only one node\r\n\r\nset ns [new Simulator]\r\nsource tb_compat.tcl\r\n\r\n# Add a new node\r\nset n0 [$ns node]\r\n\r\n# Set node OS\r\ntb-set-node-os $n0 Ubuntu1404-64-STD\r\n\r\n$ns rtproto Static\r\n\r\n# Go!\r\n$ns run";
	document.getElementById("networkConfig").innerHTML = basic;
}


jQuery(function($) {

	//Preloader
	var preloader = $('.preloader');
	$(window).load(function(){
		preloader.remove();
	});

	$("#submit-loader-button").click(function() {
		$(".spinner").show();
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

	this.pwdfieldid = pwdname;

	code += "<div class='form-group'><input th:field='*{password}' type='password' class='form-control pwdfield' name='"+pwdname+"' id='"+this.pwdfieldid+"'  placeholder='Password' ></div>";

	code += "<div class='form-group'><input type='password' class='form-control' placeholder='Confirm Password' th:field='*{confirmPassword}' name='confirmPassword' id='confirmPassword' onkeyup='checkPass();'/></div>";

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

var _gaq = _gaq || [];
_gaq.push(['_setAccount', 'UA-36251023-1']);
_gaq.push(['_setDomainName', 'jqueryscript.net']);
_gaq.push(['_trackPageview']);

(function() {
	var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
	ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
	var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
})();


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

	(function($){
		$.fn.extend({

			//pass the options variable to the function
			accordion: function(options) {

				var defaults = {
					accordion: 'true',
					speed: 300,
					closedSign: '[+]',
					openedSign: '[-]'
				};

				// Extend our default options with those provided.
				var opts = $.extend(defaults, options);
				//Assign current element to variable, in this case is UL element
				var $this = $(this);

				//add a mark [+] to a multilevel menu
				$this.find("li").each(function() {
					if($(this).find("ul").size() != 0){
						//add the multilevel sign next to the link
						$(this).find("a:first").append("<span>"+ opts.closedSign +"</span>");

						//avoid jumping to the top of the page when the href is an #
						if($(this).find("a:first").attr('href') == "#"){
							$(this).find("a:first").click(function(){return false;});
						}
					}
				});

				//open active level
				$this.find("li.active").each(function() {
					$(this).parents("ul").slideDown(opts.speed);
					$(this).parents("ul").parent("li").find("span:first").html(opts.openedSign);
				});

				$this.find("li a").click(function() {
					if($(this).parent().find("ul").size() != 0){
						if(opts.accordion){
							//Do nothing when the list is open
							if(!$(this).parent().find("ul").is(':visible')){
								parents = $(this).parent().parents("ul");
								visible = $this.find("ul:visible");
								visible.each(function(visibleIndex){
									var close = true;
									parents.each(function(parentIndex){
										if(parents[parentIndex] == visible[visibleIndex]){
											close = false;
											return false;
										}
									});
									if(close){
										if($(this).parent().find("ul") != visible[visibleIndex]){
											$(visible[visibleIndex]).slideUp(opts.speed, function(){
												$(this).parent("li").find("span:first").html(opts.closedSign);
											});

										}
									}
								});
							}
						}
						if($(this).parent().find("ul:first").is(":visible")){
							$(this).parent().find("ul:first").slideUp(opts.speed, function(){
								$(this).parent("li").find("span:first").delay(opts.speed).html(opts.closedSign);
							});


			}else{
				$(this).parent().find("ul:first").slideDown(opts.speed, function(){
			$(this).parent("li").find("span:first").delay(opts.speed).html(opts.openedSign);
		});
	}
	}
	});
		}
	});
})(jQuery);

// $(document).ready(function() {
// 	$(".topnav").accordion({
// 		accordion:false,
// 			speed: 500,
// 			closedSign: '[+]',
// 			openedSign: '[-]'
// 		});
// 	});
//
//
// 	!function($){var e=$.fn.jquery.split("."),t=parseFloat(e[0]),n=parseFloat(e[1]);2>t&&5>n?($.expr[":"].filterTableFind=function(e,t,n){return $(e).text().toUpperCase().indexOf(n[3].toUpperCase().replace(/"""/g,'"').replace(/"\\"/g,"\\"))>=0},$.expr[":"].filterTableFindAny=function(e,t,n){var i=n[3].split(/[\s,]/),r=[];return $.each(i,function(e,t){var n=t.replace(/^\s+|\s$/g,"");n&&r.push(n)}),r.length?function(e){var t=!1;return $.each(r,function(n,i){return $(e).text().toUpperCase().indexOf(i.toUpperCase().replace(/"""/g,'"').replace(/"\\"/g,"\\"))>=0?(t=!0,!1):void 0}),t}:!1},$.expr[":"].filterTableFindAll=function(e,t,n){var i=n[3].split(/[\s,]/),r=[];return $.each(i,function(e,t){var n=t.replace(/^\s+|\s$/g,"");n&&r.push(n)}),r.length?function(e){var t=0;return $.each(r,function(n,i){$(e).text().toUpperCase().indexOf(i.toUpperCase().replace(/"""/g,'"').replace(/"\\"/g,"\\"))>=0&&t++}),t===r.length}:!1}):($.expr[":"].filterTableFind=jQuery.expr.createPseudo(function(e){return function(t){return $(t).text().toUpperCase().indexOf(e.toUpperCase().replace(/"""/g,'"').replace(/"\\"/g,"\\"))>=0}}),$.expr[":"].filterTableFindAny=jQuery.expr.createPseudo(function(e){var t=e.split(/[\s,]/),n=[];return $.each(t,function(e,t){var i=t.replace(/^\s+|\s$/g,"");i&&n.push(i)}),n.length?function(e){var t=!1;return $.each(n,function(n,i){return $(e).text().toUpperCase().indexOf(i.toUpperCase().replace(/"""/g,'"').replace(/"\\"/g,"\\"))>=0?(t=!0,!1):void 0}),t}:!1}),$.expr[":"].filterTableFindAll=jQuery.expr.createPseudo(function(e){var t=e.split(/[\s,]/),n=[];return $.each(t,function(e,t){var i=t.replace(/^\s+|\s$/g,"");i&&n.push(i)}),n.length?function(e){var t=0;return $.each(n,function(n,i){$(e).text().toUpperCase().indexOf(i.toUpperCase().replace(/"""/g,'"').replace(/"\\"/g,"\\"))>=0&&t++}),t===n.length}:!1})),$.fn.filterTable=function(e){var t={autofocus:!1,callback:null,containerClass:"filter-table fa fa-search",containerTag:"p",filterExpression:"filterTableFind",hideTFootOnFilter:!1,highlightClass:"alt",ignoreClass:"",ignoreColumns:[],inputSelector:null,inputName:"", inputType:"search", label:"  Filter : ",minChars:1,minRows:6,placeholder:"Search...",preventReturnKey:!0,quickList:[],quickListClass:"quick",quickListGroupTag:"",quickListTag:"a",visibleClass:"visible"},n=function(e){return e.replace(/&/g,"&amp;").replace(/"/g,"&quot;").replace(/</g,"&lt;").replace(/>/g,"&gt;")},i=$.extend({},t,e),r=function(e,t){var n=e.find("tbody");if(""===t||t.length<i.minChars)n.find("tr").show().addClass(i.visibleClass),n.find("td").removeClass(i.highlightClass),i.hideTFootOnFilter&&e.find("tfoot").show();else{var r=n.find("td");if(n.find("tr").hide().removeClass(i.visibleClass),r.removeClass(i.highlightClass),i.hideTFootOnFilter&&e.find("tfoot").hide(),i.ignoreColumns.length){var a=[];i.ignoreClass&&(r=r.not("."+i.ignoreClass)),a=r.filter(":"+i.filterExpression+'("'+t+'")'),a.each(function(){var e=$(this),t=e.parent().children().index(e);-1===$.inArray(t,i.ignoreColumns)&&e.addClass(i.highlightClass).closest("tr").show().addClass(i.visibleClass)})}else i.ignoreClass&&(r=r.not("."+i.ignoreClass)),r.filter(":"+i.filterExpression+'("'+t+'")').addClass(i.highlightClass).closest("tr").show().addClass(i.visibleClass)}i.callback&&i.callback(t,e)};return this.each(function(){var e=$(this),t=e.find("tbody"),a=null,l=null,s=null,o=!0;"TABLE"===e[0].nodeName&&t.length>0&&(0===i.minRows||i.minRows>0&&t.find("tr").length>=i.minRows)&&!e.prev().hasClass(i.containerClass)&&(i.inputSelector&&1===$(i.inputSelector).length?(s=$(i.inputSelector),a=s.parent(),o=!1):(a=$("<"+i.containerTag+" />"),""!==i.containerClass&&a.addClass(i.containerClass),a.prepend(i.label+" "),s=$('<input type="'+i.inputType+'" placeholder="'+i.placeholder+'" name="'+i.inputName+'" />'),i.preventReturnKey&&s.on("keydown",function(e){return 13===(e.keyCode||e.which)?(e.preventDefault(),!1):void 0})),i.autofocus&&s.attr("autofocus",!0),$.fn.bindWithDelay?s.bindWithDelay("keyup",function(){r(e,$(this).val())},200):s.bind("keyup",function(){r(e,$(this).val())}),s.bind("click search input paste blur",function(){r(e,$(this).val())}),o&&a.append(s),i.quickList.length>0&&(l=i.quickListGroupTag?$("<"+i.quickListGroupTag+" />"):a,$.each(i.quickList,function(e,t){var r=$("<"+i.quickListTag+' class="'+i.quickListClass+'" />');r.text(n(t)),"A"===r[0].nodeName&&r.attr("href","#"),r.bind("click",function(e){e.preventDefault(),s.val(t).focus().trigger("click")}),l.append(r)}),l!==a&&a.append(l)),o&&e.before(a))})}}(jQuery);
// 	$(document).ready(function() {
// 		$('table').filterTable(); // apply filterTable to all tables on this page
// 	});

});

// Add show/hide button
var sShowHideBtn = '<button class="contact-button-link show-hide-contact-bar"><span class="fa fa-angle-left"></span></button>';
// oContainer.append(sShowHideBtn);

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
		Container.append(sButton);
	}
}

// display the ns file content when users select the predefined scenarios
function displayNsContent() {
	var basic1 = "# This is a simple experiment, containing only one node\r\n\r\nset ns [new Simulator]\r\nsource tb_compat.tcl\r\n\r\n# Add a new node\r\nset n0 [$ns node]\r\n\r\n# Set node OS\r\ntb-set-node-os $n0 Ubuntu1404-64-STD\r\n\r\n$ns rtproto Static\r\n\r\n# Go!\r\n$ns run";
	var basic2 = "# This is a simple experiment with 2 nodes and 10Gb link\r\n\r\nset ns [new Simulator]\r\nsource tb_compat.tcl\r\n\r\n# Add nodes\r\nset n0 [$ns node]\r\nset n1 [$ns node]\r\n\r\n# Add link (use default values)\r\nset link0 [$ns duplex-link $n0 $n1 10Gb 0ms DropTail]\r\n\r\n$ns rtproto Static\r\n\r\n# Go!\r\n$ns run";
	var basic3 = "# This is a simple experiment with 3 nodes in a LAN\r\n\r\nset ns [new Simulator]\r\nsource tb_compat.tcl\r\n\r\n# Add nodes\r\nset node0 [$ns node]\r\nset node1 [$ns node]\r\nset node2 [$ns node]\r\n\r\n# Make LAN\r\nset lan0 [$ns make-lan \"$node0 $node1 $node2\" 10Gb 0ms]\r\n\r\n$ns rtproto Static\r\n$ns run";
	var basic4 = "# This is a simple experiment with 2 nodes and customized link specification\r\n\r\nset ns [new Simulator]\r\nsource tb_compat.tcl\r\n\r\n# Add nodes\r\nset n0 [$ns node]\r\nset n1 [$ns node]\r\n\r\n# Add link and specify link properties\r\nset link0 [$ns duplex-link $n0 $n1 100Mb 200ms DropTail]\r\n\r\n$ns rtproto Static\r\n\r\n# Go!\r\n$ns run";
	var basic5 = "# Start of the script\r\n\r\n# Create a new experiment\r\nset ns [new Simulator]\r\n# Enable tb commands (an extension to TCL)\r\nsource tb_compat.tcl\r\n\r\n# Define a node (a server node)\r\nset node1 [$ns node]\r\n# Same as the above\r\nset node2 [$ns node]\r\n\r\n# Define a switch\r\nset switch0 [$ns node]\r\n# Declare that the switch0 is an OpenFlow Enabled switch\r\ntb-set-hardware $switch0 ofswitch\r\n\r\n# Connect node1 to switch0\r\nset link1 [$ns duplex-link $switch0 $node1 1Gb 0ms DropTail]\r\n# Connect node2 to switch0\r\nset link2 [$ns duplex-link $switch0 $node2 1Gb 0ms DropTail]\r\n\r\n# [Optional] Define a node named \"controller\"\r\n# If you define a controller you can set a custom image\r\n# otherwise the default image for the controller will be loaded\r\nset controller [$ns node]\r\n# If you have defined a controller you have to declare it as below\r\n# Declare that \"controller\" is an SDN Controller \r\ntb-set-hardware $controller ofcontrol\r\n\r\n# Boiler plates\r\n$ns rtproto Static \r\n$ns run \r\n### End of the script";
	var basic6 = "# Start of the script\r\n\r\n# Starting a simulator\r\nset ns [new Simulator]\r\nsource tb_compat.tcl\r\n\r\n# Defining nodes\r\nset node1 [$ns node]\r\nset node2 [$ns node]\r\nset node3 [$ns node]\r\nset node4 [$ns node]\r\n\r\n# Defining Switches\r\nset switch1 [$ns node]\r\ntb-set-hardware $switch1 ofswitch\r\nset switch2 [$ns node]\r\ntb-set-hardware $switch2 ofswitch\r\nset switch3 [$ns node]\r\ntb-set-hardware $switch3 ofswitch\r\nset switch4 [$ns node]\r\ntb-set-hardware $switch4 ofswitch\r\nset switch5 [$ns node]\r\ntb-set-hardware $switch5 ofswitch\r\nset switch6 [$ns node]\r\ntb-set-hardware $switch6 ofswitch\r\nset switch7 [$ns node]\r\ntb-set-hardware $switch7 ofswitch\r\n\r\n# Connecting Links between leaf switches and server nodes 1-4\r\nset link1 [$ns duplex-link $switch1 $node1 1Gb 0ms DropTail]\r\nset link2 [$ns duplex-link $switch2 $node2 1Gb 0ms DropTail]\r\nset link3 [$ns duplex-link $switch3 $node3 1Gb 0ms DropTail]\r\nset link4 [$ns duplex-link $switch4 $node4 1Gb 0ms DropTail]\r\n\r\n# Connecting switches together\r\nset link5 [$ns duplex-link $switch1 $switch5 1Gb 0ms DropTail]\r\nset link6 [$ns duplex-link $switch2 $switch5 1Gb 0ms DropTail]\r\nset link7 [$ns duplex-link $switch3 $switch6 1Gb 0ms DropTail]\r\nset link8 [$ns duplex-link $switch4 $switch6 1Gb 0ms DropTail]\r\nset link9 [$ns duplex-link $switch5 $switch7 1Gb 0ms DropTail]\r\nset link10 [$ns duplex-link $switch6 $switch7 1Gb 0ms DropTail]\r\n\r\n# [Optional] Define a node named \"controller\" \r\n# and declare it as an SDN Controller\r\nset controller [$ns node]\r\ntb-set-hardware $controller ofcontrol\r\n\r\n$ns rtproto Static\r\n$ns run\r\n\r\n### End of the script";

	var x = document.getElementById("selectExpScenario").value;
	var file = "";
	switch(x) {
		case "Scenario 1 - Experiment with a single node":
			file = basic1;
			break;
		case "Scenario 2 - Experiment with 2 nodes and 10Gb link":
			file = basic2;
			break;
		case "Scenario 3 - Experiment with 3 nodes in a LAN":
			file = basic3;
			break;
		case "Scenario 4 - Experiment with 2 nodes and customized link property":
			file = basic4;
			break;
		case "Scenario 5 - Single SDN switch connected to two nodes":
			file = basic5;
			break;
		case "Scenario 6 - Tree Topology with configurable SDN switches":
			file = basic6;
			break;
		default:
			file = basic1;
			break;
	}

	document.getElementById("networkConfig").innerHTML = file;
}

// display the default ns file content
function displayDefaultNsContent() {
	var basic = "# This is a simple experiment, containing only one node\r\n\r\nset ns [new Simulator]\r\nsource tb_compat.tcl\r\n\r\n# Add a new node\r\nset n0 [$ns node]\r\n\r\n# Set node OS\r\ntb-set-node-os $n0 Ubuntu1404-64-STD\r\n\r\n$ns rtproto Static\r\n\r\n# Go!\r\n$ns run";
	document.getElementById("networkConfig").innerHTML = basic;
}




