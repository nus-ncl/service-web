// Function to initialize modal for existing images
function initModalForImages() {
  var modal = document.getElementById("myModal");
  var modalImg = document.getElementById("modalImg");
  var captionText = document.getElementById("caption");
  var close_Btn = document.querySelector(".close-btn");
  console.log(close_Btn);

  if (!modal || !close_Btn) return;

  // Avoid attaching multiple times
  var images = document.querySelectorAll(".grid img[id^='Image']");
  images.forEach(function (img) {
    if (img.dataset.modalAttached) return; 
    img.dataset.modalAttached = "true";
    
  img.addEventListener("click", function () {
    var cell = img.closest(".cell");
    var thumb = cell.querySelector(".thumb");
    var largeSrc = (thumb && thumb.getAttribute("data-large")) || img.src; 
    modal.style.display = "block";
    modalImg.src = img.src;
    captionText.textContent = img.parentElement.textContent.trim();
  });

  });

  close_Btn.addEventListener("click", function () {
    modal.style.display = "none";
  });

  modal.addEventListener("click", function (e) {
    if (e.target === modal) modal.style.display = "none";
  });
}

// Observe #RailwayMetroSystem for content added dynamically
var targetNode = document.getElementById("RailwayMetroSystem");
var observer = new MutationObserver(function (mutationsList, observer) {
  mutationsList.forEach(function (mutation) {
    if (mutation.addedNodes.length) {
      initModalForImages();
    }
  });
});

// Start observing
if (targetNode) {
  observer.observe(targetNode, { childList: true, subtree: true });
}
