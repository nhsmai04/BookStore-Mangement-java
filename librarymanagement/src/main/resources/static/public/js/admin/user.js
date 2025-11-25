
    document.addEventListener("DOMContentLoaded", function () {

    document.querySelectorAll(".user-role-select").forEach(select => {

        select.addEventListener("change", function () {

            const userId = this.dataset.userId;
            const newRole = parseInt(this.value);
            const oldRole = parseInt(this.dataset.currentRole);

            if (!confirm("Bạn có chắc muốn đổi role?")) {
                this.value = oldRole;
                return;
            }

            fetch(`/admin/users/${userId}/role`, {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(newRole)
            })
                .then(res => {
                    if (!res.ok) throw new Error("Update failed");
                    return res.json();
                })
                .then(data => {
                    const toastEl = document.getElementById("successToast");
                    const toast = new bootstrap.Toast(toastEl);
                    toast.show();

                    this.dataset.currentRole = newRole;

                    this.classList.add("border-success");
                    setTimeout(() => this.classList.remove("border-success"), 1000);
                })
                .catch(() => {
                    alert("Không thể cập nhật role");
                    this.value = oldRole;
                });
        });

    });
});

