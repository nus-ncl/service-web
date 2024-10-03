
    document.addEventListener('DOMContentLoaded', function() {
        const tables = [
            { tableId: 'pendingUser', paginationId: 'pendingUserPagination' },
            { tableId: 'approvedUser', paginationId: 'approvedUserPagination' }
        ];
        const rowsPerPage = 20; // Adjust as needed

        tables.forEach(({ tableId, paginationId }) => {
            const table = document.getElementById(tableId);
            const pagination = document.getElementById(paginationId);
            const rows = Array.from(table.getElementsByTagName('tbody')[0].getElementsByTagName('tr'));
            let currentPage = 1;

            function displayPage(page) {
                const startIndex = (page - 1) * rowsPerPage;
                const endIndex = startIndex + rowsPerPage;

                rows.forEach((row, index) => {
                    if (index >= startIndex && index < endIndex) {
                        row.style.display = '';
                    } else {
                        row.style.display = 'none';
                    }
                });
            }

            function setupPagination() {
                pagination.innerHTML = '';

                const pageCount = Math.ceil(rows.length / rowsPerPage);

                if (pageCount > 1) {
                    // Previous button
                    const prevButton = document.createElement('li');
                    prevButton.classList.add('page-item');
                    prevButton.innerHTML = `<a class="page-link" href="#" tabindex="-1">Previous</a>`;
                    prevButton.addEventListener('click', function(event) {
                        event.preventDefault();
                        if (currentPage > 1) {
                            currentPage--;
                            displayPage(currentPage);
                            updatePaginationButtons();
                        }
                    });
                    pagination.appendChild(prevButton);
                }

                for (let i = 1; i <= pageCount; i++) {
                    const li = document.createElement('li');
                    li.className = 'page-item';
                    li.innerHTML = `<a class="page-link" href="#">${i}</a>`;
                    li.addEventListener('click', function(event) {
                        event.preventDefault();
                        currentPage = i;
                        displayPage(currentPage);
                        updatePaginationButtons();
                    });
                    pagination.appendChild(li);
                }

                if (pageCount > 1) {
                    // Next button
                    const nextButton = document.createElement('li');
                    nextButton.classList.add('page-item');
                    nextButton.innerHTML = `<a class="page-link" href="#">Next</a>`;
                    nextButton.addEventListener('click', function(event) {
                        event.preventDefault();
                        if (currentPage < pageCount) {
                            currentPage++;
                            displayPage(currentPage);
                            updatePaginationButtons();
                        }
                    });
                    pagination.appendChild(nextButton);
                }

                // Initially display the first page
                displayPage(currentPage);
                updatePaginationButtons();
            }

            function updatePaginationButtons() {
                const pageItems = Array.from(pagination.getElementsByTagName('li'));
                pageItems.forEach(item => item.classList.remove('active'));
                pageItems[currentPage].classList.add('active');
            }

            // Setup pagination when the page loads
            setupPagination();
        });
    });

