document.addEventListener("DOMContentLoaded", function () {
    const alertsToggle = document.getElementById("alertsDropdown");
    const alertsMenu = alertsToggle?.nextElementSibling;

    if (alertsToggle && alertsMenu) {
        alertsToggle.addEventListener("click", function (e) {
            e.preventDefault();

            // Đóng các menu khác
            document.querySelectorAll(".dropdown-menu.show").forEach(m => {
                if (m !== alertsMenu) m.classList.remove("show");
            });

            alertsMenu.classList.toggle("show");
        });

        document.addEventListener("click", function (e) {
            if (!alertsToggle.contains(e.target) && !alertsMenu.contains(e.target)) {
                alertsMenu.classList.remove("show");
            }
        });
    }
});
document.addEventListener("DOMContentLoaded", function () {
    const avatarToggle = document.getElementById("avatarDropdown");
    const avatarMenu = avatarToggle?.nextElementSibling;

    if (avatarToggle && avatarMenu) {
        avatarToggle.addEventListener("click", function (e) {
            e.preventDefault();

            document.querySelectorAll(".dropdown-menu.show").forEach(m => {
                if (m !== avatarMenu) m.classList.remove("show");
            });

            avatarMenu.classList.toggle("show");
        });

        document.addEventListener("click", function (e) {
            if (!avatarToggle.contains(e.target) && !avatarMenu.contains(e.target)) {
                avatarMenu.classList.remove("show");
            }
        });
    }
});
document.addEventListener("DOMContentLoaded", () => {
    loadNotificationCount();

    // click chuông mới load list
    const bell = document.getElementById("alertsDropdown");
    if (bell) {
        bell.addEventListener("click", loadNotifications);
    }

    // auto refresh badge mỗi 30s
    setInterval(loadNotificationCount, 30000);
});

function loadNotifications() {
    fetch("/admin/notifications")
        .then(res => res.json())
        .then(res => {
            const data = res.data;

            const list = document.getElementById("notificationList");
            const header = document.getElementById("notificationHeader");

            list.innerHTML = "";

            if (!data || data.length === 0) {
                header.innerText = "No new notifications";
                list.innerHTML = `
                <div class="text-center p-3 text-muted">
                    Không có thông báo mới
                </div>
            `;
                loadNotificationCount();
                return;
            }

            header.innerText = data.length + " New Notifications";

            data.forEach(n => {
                list.innerHTML += `
                <a href="#" class="list-group-item list-group-item-action">
                    <div class="row g-0 align-items-center">
                        <div class="col-2">
                            <i class="fa-solid ${getIcon(n.type)}"></i>
                        </div>
                        <div class="col-10">
                            <div class="text-dark">${n.title}</div>
                            <div class="text-muted small mt-1">${n.content}</div>
                            <div class="text-muted small mt-1">
                                ${formatTime(n.createdAt)}
                            </div>
                        </div>
                    </div>
                </a>
            `;
            });
            loadNotificationCount();
        })
        .catch(err => console.error("Load notifications failed", err));
}

function loadNotificationCount() {
    fetch("/admin/notifications/count")
        .then(res => res.text())
        .then(count => {
            const badge = document.getElementById("notificationCount");
            if (badge) badge.innerText = count;
        });
}

function getIcon(type) {
    switch (type) {
        case "CREATE": return "fa-circle-plus text-success";
        case "UPDATE": return "fa-pen-to-square text-warning";
        case "DELETE": return "fa-trash text-danger";
        default: return "fa-bell text-primary";
    }
}

function formatTime(dateStr) {
    return new Date(dateStr).toLocaleString();
}
