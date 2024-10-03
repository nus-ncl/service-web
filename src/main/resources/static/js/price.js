
document.addEventListener('DOMContentLoaded', function() {

    const comBtn = document.getElementById('modalComBtn');
    const gpuBtn = document.getElementById('modalGpuBtn');

    var drCom = document.getElementById('com-option');
    var drGpu = document.getElementById('gpu-option');

    var type = document.getElementById('hideType');
    const typeOfBook = document.getElementById('typeofBook');

    comBtn.addEventListener('click', function() {
        const title = this.getAttribute('data-title');
        var eventModal = document.getElementById('BRModal');
        var modalTitle = eventModal.querySelector('.modal-title');
        $('#BRModal').modal('show');
        drCom.style.display = 'block';
        drGpu.style.display = 'none';
    });

    gpuBtn.addEventListener('click', function() {
        const title = this.getAttribute('data-title');
        var eventModal = document.getElementById('BRModal');
        var modalTitle = eventModal.querySelector('.modal-title');
        $('#BRModal').modal('show');
        drCom.style.display = 'none';
        drGpu.style.display = 'block';
    });
    // Add event listeners to both select elements
});


function validateForm() {

        var isValid = true;

        const username = document.getElementById('name');
        const userEmail = document.getElementById('email');
        const userMessage = document.getElementById('message');

        const startDate = document.getElementById('startDate');
        const endDate = document.getElementById('endDate');
        const selectoption = document.getElementById('name');

        if (!username.value) {
            username.classList.add('is-invalid');
            $(username).tooltip('dispose').tooltip({
                title: "Please enter your name.",
                placement: "right"
            }).tooltip('show');
            isValid = false;
        } else {
            username.classList.remove('is-invalid');
            $(username).tooltip('dispose');
        }

         if (!userEmail.value) {
            userEmail.classList.add('is-invalid');
            $(userEmail).tooltip('dispose').tooltip({
                title: "Please enter your email.",
                placement: "right"
            }).tooltip('show');
            isValid = false;
        } else {
            var emailPattern =/^([a-zA-Z0-9+_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
            var emailvalue = userEmail.value;

             if (!emailPattern.test(emailvalue)) {
                  userEmail.classList.add('is-invalid');
                  $(userEmail).tooltip('dispose').tooltip({
                     title: "Invalid email address!",
                     placement: "right"
                  }).tooltip('show');
                  isValid = false;
             }
             else{
              userEmail.classList.remove('is-invalid');
              $(userEmail).tooltip('dispose');
             }
        }

        if (!userMessage.value) {
            userMessage.classList.add('is-invalid');
            $(userMessage).tooltip('dispose').tooltip({
                title: "Please enter your email.",
                placement: "right"
            }).tooltip('show');
            isValid = false;
        } else {
            userMessage.classList.remove('is-invalid');
            $(userMessage).tooltip('dispose');
        }

        if (!startDate.value) {
            startDate.classList.add('is-invalid');
            $(startDate).tooltip('dispose').tooltip({
                title: "Please set the start date.",
                placement: "right"
            }).tooltip('show');
            isValid = false;
        } else {
            startDate.classList.remove('is-invalid');
            $(startDate).tooltip('dispose');
        }

        if (!endDate.value) {
            endDate.classList.add('is-invalid');
            $(endDate).tooltip('dispose').tooltip({
                title: "Please set the end date.",
                placement: "right"
            }).tooltip('show');
            isValid = false;
        } else {
            endDate.classList.remove('is-invalid');
            $(endDate).tooltip('dispose');
        }

        if (startDate.value && endDate.value) {
            if (new Date(startDate.value) > new Date(endDate.value)) {
                endDate.classList.add('is-invalid');
                $(endDate).tooltip('dispose').tooltip({
                title: "End Date must be after Start Date.",
                placement: "right"
                }).tooltip('show');
                isValid = false;
            }
            else{
                endDate.classList.remove('is-invalid');
                $(endDate).tooltip('dispose');
        }
    }
      return isValid;

}

$(document).ready(function () {
   $('[data-toggle="tooltip"]').tooltip();
});

    function closeForm() {
        const username = document.getElementById('name');
        username.classList.remove('is-invalid');
        username.value = '';
        $(username).tooltip('dispose');

        const userEmail = document.getElementById('email');
        userEmail.classList.remove('is-invalid');
        userEmail.value = '';
        $(userEmail).tooltip('dispose');

        const userMessage = document.getElementById('message');
        userMessage.classList.remove('is-invalid');
        userMessage.value = '';
        $(userMessage).tooltip('dispose');

        const startDate = document.getElementById('startDate');
        startDate.classList.remove('is-invalid');
        startDate.value = '';
        $(startDate).tooltip('dispose');

        const endDate = document.getElementById('endDate');
        endDate.classList.remove('is-invalid');
        endDate.value = '';
        $(endDate).tooltip('dispose');

         const comoption = document.getElementById('com-option');
         $('#com-option').val('').selectpicker('refresh');
         const gpuoption = document.getElementById('gpu-option');
         $('#gpu-option').val('').selectpicker('refresh');

    }

    function showMessageModal(content) {
        var contactModal = document.getElementById('priceMsgModal');
        var modalMessage =  document.getElementById('response-message');
        var btnClose = document.getElementById('close');

       modalMessage.innerHTML = content;
      //  closeForm();
       $('#priceMsgModal').modal('show');

       btnClose.addEventListener('click', function(event) {
            $('#priceMsgModal').modal('hide');
             closeForm();
      });
    }

document.addEventListener('DOMContentLoaded', function() {
    var btnSubmit = document.getElementById('btnSubmit');

    btnSubmit.addEventListener('click', function() {
      if(validateForm()){
       $('#spinner').show();
        $.ajax({
            url: '/sendPriceListMessage',
            data: {
                'compute': $('#com-option').val(),
                'gpu': $('#gpu-option').val(),
                'name': $('#name').val(),
                'email': $('#email').val(),
                'startDate': $('#startDate').val(),
                'endDate': $('#endDate').val(),
                'message': $('#message').val()
            },
            type: "post",
            cache: false,
            success: function(result) {
               	$('#spinner').hide();
                $('#BRModal').modal('hide');
                showMessageModal(result);
            },
            error: function(xhr, status, error) {
                console.error('AJAX Error:', status, error);
            }
        });
        }
    });
});





