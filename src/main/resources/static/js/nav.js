const showMenu = (toggleId, navId) => {
    const toggle = document.getElementById(toggleId),
    nav = document.getElementById(navId);
    const dropdownMenu = document.querySelector('.dropdown__menu');

    toggle.addEventListener('click', () => {
        // Add show-menu class to nav menu
        nav.classList.toggle('show-menu');
        // Add show-icon to show and hide the menu icon
        toggle.classList.toggle('show-icon');
        dropdownMenu.classList.remove('dropdown__hidden');
    });

    nav.addEventListener('click', () => {
          // Add show-menu class to nav menu
          //nav.classList.toggle('show-menu');
          // Add show-icon to show and hide the menu icon
          //toggle.classList.toggle('show-icon');
          dropdownMenu.classList.remove('dropdown__hidden');
    });

    nav.addEventListener("mouseover", () => {
       // Add show-menu class to nav menu
         //nav.classList.toggle('show-menu');
         // Add show-icon to show and hide the menu icon
         //toggle.classList.toggle('show-icon');
         dropdownMenu.classList.remove('dropdown__hidden');
    });

    nav.addEventListener("mouseenter", () => {
           // Add show-menu class to nav menu
             //nav.classList.toggle('show-menu');
             // Add show-icon to show and hide the menu icon
             //toggle.classList.toggle('show-icon');
             dropdownMenu.classList.remove('dropdown__hidden');
        });

    // Close menu when clicking outside
    document.addEventListener('click', (event) => {
        if (!nav.contains(event.target) && !toggle.contains(event.target)) {
            nav.classList.remove('show-menu');
            toggle.classList.remove('show-icon');
            dropdownMenu.classList.add('dropdown__hidden');
        }
    });
};

showMenu('nav-toggle', 'nav-menu');

/*==================== REMOVE MENU MOBILE ====================*/
const navLink = document.querySelectorAll('.nav__link');

function linkAction() {
    const navMenu = document.getElementById('nav-menu');
    const dropdownMenu = document.querySelector('.dropdown__menu');
    const toggle = document.getElementById('nav-toggle');

    // When we click on each nav__link, we remove the show-menu class
    navMenu.classList.remove('show-menu');
    dropdownMenu.classList.add('dropdown__hidden');
}

navLink.forEach(n => n.addEventListener('click', linkAction));

/*==================== REMOVE MENU MOBILE ====================*/
const dropLink = document.querySelectorAll('.dropdown__link');

function linkDropAction() {
    const navMenu = document.getElementById('nav-menu');
    const dropdownMenu = document.querySelector('.dropdown__menu');
    const toggle = document.getElementById('nav-toggle');

    // When we click on each nav__link, we remove the show-menu class
    navMenu.classList.remove('show-menu');
    dropdownMenu.classList.add('dropdown__hidden');
}

dropLink.forEach(n => n.addEventListener('click', linkDropAction));

/*==================== SCROLL SECTIONS ACTIVE LINK ====================*/
const sections = document.querySelectorAll('section[id]');

function scrollActive(){
    const scrollY = window.pageYOffset;

    sections.forEach(current =>{
        const sectionHeight = current.offsetHeight;
        const sectionTop = current.offsetTop - 50;
        const sectionId = current.getAttribute('id');
        var dropdownMenu = document.querySelector('.dropdown__menu');

        if(scrollY > sectionTop && scrollY <= sectionTop + sectionHeight){
            document.querySelector('.nav__menu a[href*=' + sectionId + ']').classList.add('active-link');
            // Close dropdown menu

            dropdownMenu.classList.add('dropdown__hidden'); // Hide dropdown

        }else{
            document.querySelector('.nav__menu a[href*=' + sectionId + ']').classList.remove('active-link');
            //dropdownMenu.classList.remove('dropdown__hidden');
        }
    });
}

window.addEventListener('scroll', scrollActive);

/*==================== SHOW SCROLL TOP ====================*/
/*
function scrollTop(){
    const scrollTop = document.getElementById('scroll-top');
    // When the scroll is higher than 560 viewport height, add the show-scroll class to the a tag with the scroll-top class
    if(this.scrollY >= 360) scrollTop.classList.add('show-scroll'); else scrollTop.classList.remove('show-scroll');
}
window.addEventListener('scroll', scrollTop);
*/
/*==================== SCROLL REVEAL ANIMATION ====================*/

const sr = ScrollReveal({
    origin: 'top',
    distance: '30px',
    duration: 2000,
    reset: true
});

sr.reveal(`.home__data, .home__img,
            .about__data, .about__img, .about__start, .about__vd,
            .services__content, .menu__content,.whatwedo__content,
            .app__data, .app__img,
            .contact__data, .contact__button,.wwd-anim,.wwd-section ,
            .footer__content`,  {
    interval: 200
});


//Rendering static content changes from github
function loadStaticPage(id, page) {
    $.ajax({
         dataType: "JSON",
         async: true,
         //production
         //url: "https://api.github.com/repos/nus-ncl/static-web-content/contents/"+page,

        //This url is for Test branch used for testing
         url: "https://api.github.com/repos/nus-ncl/static-web-content/contents/"+page+"?ref=DEV-1311",
         type: 'GET',
         success: function(result) {
             document.getElementById(id).innerHTML = decodeURIComponent(escape(window.atob(result.content)));
         }
    });
}

//Rendering static content changes from github
function loadfooterStaticPage(id, page) {
    $.ajax({
         dataType: "JSON",
         async: true,
         //production
         //url: "https://api.github.com/repos/nus-ncl/static-web-content/contents/"+page,

        //This url is for Test branch used for testing
         url: "https://api.github.com/repos/nus-ncl/static-web-content/footer/"+page+"?ref=DEV-1311",
         type: 'GET',
         success: function(result) {
             document.getElementById(id).innerHTML = decodeURIComponent(escape(window.atob(result.content)));
         }
    });
}

function loadImage(imgName, imgId, imgType) {
    $.ajax({
         dataType: "JSON",
         url: "https://api.github.com/repos/nus-ncl/static-web-content/contents/images/"+imgName,
         type: 'GET',
         success: function(result) {
            document.getElementById(imgId).src = "data:image/"+imgType+";base64,"+result.content;
         }
    });
}