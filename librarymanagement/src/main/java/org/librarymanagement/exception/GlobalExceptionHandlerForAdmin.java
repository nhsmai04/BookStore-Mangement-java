package org.librarymanagement.exception;

import org.librarymanagement.constant.ApiEndpoints;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice(basePackages = "org.librarymanagement.controller.admin")
public class GlobalExceptionHandlerForAdmin {
    @ExceptionHandler(Exception.class)
    public String handleException(Exception ex, RedirectAttributes redirectAttributes) {
        // Ghi log để debug
        ex.printStackTrace();

        redirectAttributes.addFlashAttribute("message", "Có lỗi xảy ra: " + ex.getMessage());
        redirectAttributes.addFlashAttribute("alertType", "danger");

        // Redirect về trang thống kê
        return "redirect:" +  ApiEndpoints.ADMIN_DASHBOARD;
    }
}
