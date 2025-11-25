package org.librarymanagement.controller.admin;

import org.librarymanagement.constant.ApiEndpoints;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(ApiEndpoints.ADMIN_USER)
public class UserController {
    @GetMapping
    public String showUserlist() {
        return "admin/users/index";
    }

    @GetMapping("/{id}")
    public String showUserdetail() {
        return "admin/users/detail";
    }
}
