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
