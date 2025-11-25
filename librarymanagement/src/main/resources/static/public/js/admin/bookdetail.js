
    renderSelectedAuthors = function (selected) {
    const container = document.getElementById('selectedAuthors');
    container.innerHTML = '';

    if (selected.length === 0) {
    container.innerHTML =
    '<small class="text-muted">Chưa chọn tác giả</small>';
    return;
}

    selected.forEach(name => {
    const badge = document.createElement('span');
    badge.className = 'badge bg-primary me-2 mb-2';

    badge.innerHTML = `
                ${name}
                <span style="cursor:pointer;margin-left:6px"
                      onclick="removeAuthor('${name}')">&times;</span>
            `;

    container.appendChild(badge);
});
};

    removeAuthor = function (name) {
    const selected = getSelectedAuthors().filter(n => n !== name);
    updateAuthors(selected);
};

    getSelectedAuthors = function () {
    const selected = [];
    document
    .querySelectorAll('#authorHiddenInputs input[name="authorName"]')
    .forEach(i => selected.push(i.value));
    return selected;
};

    updateAuthors = function (selected) {
    const hidden = document.getElementById('authorHiddenInputs');
    hidden.innerHTML = '';

    selected.forEach(name => {
    const input = document.createElement('input');
    input.type = 'hidden';
    input.name = 'authorName';
    input.value = name;
    hidden.appendChild(input);
});

    document.querySelectorAll('.author-checkbox').forEach(cb => {
    cb.checked = selected.includes(cb.value);
});

    renderSelectedAuthors(selected);
};

    applyAuthors = function () {
    const selected = [];
    document
    .querySelectorAll('.author-checkbox:checked')
    .forEach(cb => selected.push(cb.value));
    updateAuthors(selected);
};

    document.addEventListener('DOMContentLoaded', () => {
    const selected = getSelectedAuthors();
    updateAuthors(selected);
});



    function updateMainAuthorHighlight() {
        const checkboxes = document.querySelectorAll('.author-checkbox');
        let found = false;
        checkboxes.forEach(cb => {
            const label = cb.nextElementSibling;
            if (cb.checked && !found) {
                label.classList.add('main-author-label');
                found = true;
            } else {
                label.classList.remove('main-author-label');
            }
        });
    }

    document.querySelectorAll('.author-checkbox').forEach(cb => {
        cb.addEventListener('change', updateMainAuthorHighlight);
    });

    // Run on modal open to initialize
    document.getElementById('authorModal').addEventListener('shown.bs.modal', updateMainAuthorHighlight);




