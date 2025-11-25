// Khởi động button import file sách
document.getElementById("fileInput").addEventListener("change", function () {
    if (this.files.length > 0) {
        document.getElementById("importForm").submit();
    }
});

// Tải template file sách
function downloadAndRedirect() {
    // Mở download trong tab ẩn
    const iframe = document.createElement('iframe');
    iframe.style.display = 'none';
    iframe.src = '/admin/book/download-template';
    document.body.appendChild(iframe);

    // Redirect sau 1s
    setTimeout(() => {
        window.location.href = '/admin/book';
    }, 1000);
}