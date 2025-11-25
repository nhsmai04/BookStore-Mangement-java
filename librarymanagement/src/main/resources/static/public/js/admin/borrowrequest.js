// Khởi động button import file sách
document.getElementById("pdfInput").addEventListener("change", function () {
    if (this.files.length > 0) {
        document.getElementById("uploadPdf").submit();
    }
});