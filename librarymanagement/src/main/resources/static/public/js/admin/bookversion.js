document.getElementById('saveBookVersionStatusBtn').addEventListener('click', function () {
    const status = document.getElementById('status').value;
    const versionId = document.getElementById('editVersionModal').dataset.versionId; // ID của bookVersion cần sửa

    // Gửi trạng thái mới lên server
    fetch(`/admin/books/book-version/edit?bookId=${versionId}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            status: status // Chỉ gửi trạng thái mới của bookVersion
        })
    })
        .then(response => response.json())
        .then(data => {
            if (data.status === 200) {
                // Cập nhật trạng thái trên giao diện sau khi thành công
                alert("Cập nhật trạng thái thành công!");

                // Reload lại chỉ phần thông tin của BookVersion
                const row = document.querySelector(`[data-version-id="${versionId}"]`).closest('tr');
                const statusCell = row.querySelector('.status-cell');

                // Cập nhật lại trạng thái của BookVersion trong bảng
                switch (status) {
                    case "1":
                        statusCell.innerHTML = '<span class="badge bg-success">Có sẵn</span>';
                        break;
                    case "0":
                        statusCell.innerHTML = '<span class="badge bg-danger">Hỏng / Mất</span>';
                        break;
                    case "2":
                        statusCell.innerHTML = '<span class="badge bg-warning text-dark">Đang mượn</span>';
                        break;
                    case "3":
                        statusCell.innerHTML = '<span class="badge bg-primary">Đang đặt trước</span>';
                        break;
                    case "4":
                        statusCell.innerHTML = '<span class="badge bg-secondary">Đang sửa</span>';
                        break;
                    default:
                        statusCell.innerHTML = '<span class="badge bg-secondary">Không xác định</span>';
                }
            } else {
                alert("Cập nhật trạng thái thất bại!");
            }
        })
        .catch(error => {
            console.error("Error:", error);
            alert("Có lỗi xảy ra!");
        });
});

// Khi click vào nút "Sửa" của một phiên bản sách, mở modal và set ID phiên bản
document.querySelectorAll('.editVersionBtn').forEach(button => {
    button.addEventListener('click', function () {
        const versionId = this.getAttribute('data-version-id');
        document.getElementById('editVersionModal').dataset.versionId = versionId;

        // Load trạng thái hiện tại vào modal (nếu cần thiết)
        const currentStatus = this.getAttribute('data-current-status');
        document.getElementById('status').value = currentStatus;
    });
});