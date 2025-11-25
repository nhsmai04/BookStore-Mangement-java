package org.librarymanagement.controller.admin;

import org.librarymanagement.constant.ApiEndpoints;
import org.librarymanagement.dto.response.ResponseObject;
import org.librarymanagement.dto.response.UserDetailReponse;
import org.librarymanagement.service.CurrentUserService;
import org.librarymanagement.service.NotificationService;
import org.librarymanagement.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(ApiEndpoints.ADMIN_USER)
public class UserController {
    private final UserService userService;
    private final CurrentUserService currentUserService;
    private final NotificationService notificationService;
    public UserController(UserService userService, CurrentUserService currentUserService, NotificationService notificationService) {
        this.userService = userService;
        this.currentUserService = currentUserService;
        this.notificationService = notificationService;
    }

    @GetMapping
    public String showUserlist(
            @RequestParam(value = "size", defaultValue = "20") int size,
            @RequestParam(defaultValue = "0") int page,
            Model model
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<UserDetailReponse> userPage = userService.getAllUsers(pageable);
        int totalPages = userPage.getTotalPages();

        if (userPage.isEmpty()) {
            model.addAttribute("message", "No users found.");
        } else {
            model.addAttribute("userPage", userPage);
        }
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);

        return "admin/users/index";
    }

    @GetMapping("/{id}")
    public String showUserdetail() {
        return "admin/users/detail";
    }

    @PreAuthorize("hasRole('MANAGER')")
    @PutMapping("/{id}/role")
    @ResponseBody
    public ResponseObject updateUserRole(
            @PathVariable("id") Integer id,
            @RequestBody Integer role
    ) {
        ResponseObject responseObject = userService.updateUserRole(id, role);
        if(responseObject.status().intValue() == 200) {
            notificationService.notify(
                    "Cập nhật vai trò người dùng",
                    "Vai trò đã được cập nhật thành công.",
                    "ROLE_UPDATE",
                    currentUserService.getCurrentUser()
                    );
        }
        return responseObject;
    }
}
