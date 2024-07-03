function loadStaticEventPage(id, page) {
        $.ajax({
            dataType: "json",
            async: true,
            // URL for production
            // url: "https://api.github.com/repos/nus-ncl/static-web-content/contents/" + page,
            
            // URL for test branch
            url: "https://api.github.com/repos/nus-ncl/static-web-content/contents/" + page + "?ref=DEV-1311",
            type: 'GET',
            success: function(result) {
                document.getElementById(id).innerHTML = decodeURIComponent(escape(window.atob(result.content)));
                console.log('Entering to sucess');
                if(id=='cidex'){
                    const yearFilter = document.getElementById('cidexyearFilter');
                    const sections = document.querySelectorAll('section.event-cidex');
                    executeyearFilter(yearFilter,sections);
                }
                else if(id=='lockedshields'){
                    const yearFilter = document.getElementById('lsyearFilter');
                    const sections = document.querySelectorAll('section.event-ls');
                    executeyearFilter(yearFilter,sections);
                }
                else if(id=='pastevents'){
                    const yearFilter = document.getElementById('yearFilter');
                    const sections = document.querySelectorAll('section.event-section');
                    executeyearFilter(yearFilter,sections);
                    setupEventListeners();
                }
                else if(id=='recentevents'){
                const yearFilter = document.getElementById('yearFilter');
                const sections = document.querySelectorAll('section.event-section');
                executeyearFilter(yearFilter,sections);
                setupEventListeners();
                }
            },
            error: function(xhr, status, error) {
                console.error('Error loading static page:', error);
            }
        });
    }

function executeyearFilter(yearfilter,sections){
    yearfilter.addEventListener('change', showSelectedSection);
    // Initialize by showing the section corresponding to the default selected option
    showSelectedSection();

    function showSelectedSection() {
        const selectedYear = yearfilter.value;
        sections.forEach(section => {
            if (section.id === selectedYear) {
                section.style.display = 'block';
            } else {
                section.style.display = 'none';
            }
        });
    }
}

/********************************** pastevents ************************************** */

function executeyearFilter(yearfilter,sections){
    yearfilter.addEventListener('change', showSelectedSection);
    // Initialize by showing the section corresponding to the default selected option
    showSelectedSection();

    function showSelectedSection() {
        const selectedYear = yearfilter.value;
        sections.forEach(section => {
            if (section.id === selectedYear) {
                section.style.display = 'block';
            } else {
                section.style.display = 'none';
            }
        });
    }
}

function setupEventListeners() {

    const links = document.querySelectorAll('.event-modal a');
    links.forEach(link => {
        link.addEventListener('click', function(event) {
            event.preventDefault();
            const title = this.getAttribute('data-title');
            const content = this.getAttribute('data-content');
            const image = this.getAttribute('data-image');
            showModal(title, content, image);
        });
    });
}

function showModal(title, content, image) {
        var eventModal = document.getElementById('eventDtlModal');
        var modalTitle = eventModal.querySelector('.modal-title');
        var modalBodyInput = eventModal.querySelector('.modal-body p');
        var modalImage = eventModal.querySelector('.modal-body img');

        modalTitle.textContent = title;
        modalBodyInput.innerHTML = content;
        modalImage.src = image;
        $('#eventDtlModal').modal('show');
}
