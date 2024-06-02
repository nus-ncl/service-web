// Function to load static page content into a specified element
//function loadStaticPage(elementId, filePath, callback) {
//    fetch(filePath)
//        .then(response => response.text())
//        .then(data => {
//            document.getElementById(elementId).innerHTML = data;
//            if (callback) {
//                callback();
//            }
//        })
//        .catch(error => console.error('Error loading static page:', error));
//}
//
//function cidexloadStaticPage(id, page, callback) {
//    $.ajax({
//        dataType: "JSON",
//        async: true,
//        //production
//        // url: "https://api.github.com/repos/nus-ncl/static-web-content/contents/" + page,
//
//        // This url is for Test branch used for testing
//        url: "https://api.github.com/repos/nus-ncl/static-web-content/contents/" + page + "?ref=DEV-1311",
//        type: 'GET',
//        success: function(result) {
//            document.getElementById(id).innerHTML = decodeURIComponent(escape(window.atob(result.content)));
//            if (callback) {
//                callback();
//            }
//        },
//        error: function(xhr, status, error) {
//            console.error('Error loading static page:', status, error);
//        }
//    });
//}
//
//
////// Function to load static page content into a specified element after a delay
////function loadStaticPageWithDelay(elementId, filePath, delay, callback) {
////    setTimeout(() => loadStaticPage(elementId, filePath, callback), delay);
////}
//
//// Function to be called when the 'cidexyearFilter' element changes
//function showSelectedSection() {
//    var filterValue = document.getElementById('cidexyearFilter').value;
//    // Your selection logic here, for example:
//    console.log('Filter changed to:', filterValue);
//
//    // Example of additional logic based on the filter value
//    var sections = document.querySelectorAll('.section.event-cidex');
//    sections.forEach(function(section) {
//        if (section.id === filterValue) {
//            section.style.display = 'block';
//        } else {
//            section.style.display = 'none';
//        }
//    });
//}
//
//// Set up event listeners after DOM content is loaded
//document.addEventListener('DOMContentLoaded', function() {
//    // Load 'cidex' content and set up event listener
//    cidexloadStaticPage('cidex', 'Event/cidex.txt', function() {
//        var cidexyearFilter = document.getElementById('cidexyearFilter');
//        if (cidexyearFilter) {
//            cidexyearFilter.addEventListener('change', showSelectedSection);
//        } else {
//            console.error('Element with id "cidexyearFilter" not found');
//        }
//    });
//
//    // Load 'footer' content with a delay
//    loadStaticPageWithDelay('footer', 'Home/footer.txt', 1000);
//});
