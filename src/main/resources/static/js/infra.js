 // scroll when click on href , li
 document.addEventListener("DOMContentLoaded", function() {
        // Add smooth scrolling behavior to anchor links within the navigation
        document.querySelectorAll('.scrollspy a[href^="#"]').forEach(anchor => {
            anchor.addEventListener('click', scrollToTarget);
        });

        // Add smooth scrolling behavior to the li elements within the navigation
        document.querySelectorAll('.scrollspy li').forEach(li => {
            li.addEventListener('click', function (e) {
                const anchor = li.querySelector('a[href^="#"]');
                anchor && scrollToTarget.call(anchor, e);
            });
        });

        function scrollToTarget(e) {
            e.preventDefault();
            const targetId = this.getAttribute('href').substring(1);
            const target = document.getElementById(targetId);
            target && window.scrollTo({ top: target.offsetTop - 120, behavior: 'smooth' });
        }
});
/* ****************** ************************************************ */
$(document).on('click', '.accordion-button', function(){
    // Toggle the accordion collapse
    $(this).next('.accordion-collapse').collapse('toggle');
});