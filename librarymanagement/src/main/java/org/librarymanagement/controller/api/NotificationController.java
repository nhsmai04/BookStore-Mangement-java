package org.librarymanagement.controller.api;

import org.librarymanagement.constant.ApiEndpoints;
import org.librarymanagement.dto.response.ResponseObject;
import org.librarymanagement.entity.User;
import org.librarymanagement.service.CurrentUserService;
import org.librarymanagement.service.NotificationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(ApiEndpoints.ADMIN_NOTIFICATIONS)
public class NotificationController {

    private final NotificationService notificationService;
    private final CurrentUserService currentUserService;

    public NotificationController(NotificationService notificationService,
                                  CurrentUserService currentUserService) {
        this.notificationService = notificationService;
        this.currentUserService = currentUserService;
    }
    @GetMapping
    public ResponseObject getNotifications() {
        System.out.println("Da chay vao day");
        User currentUser = currentUserService.getCurrentUser();
        if (currentUser == null) {
            System.out.println("User is null");
            return new ResponseObject("Unauthenticated", 401, null);
        }

        return notificationService.getUnreadNotifications(currentUser);
    }

    @GetMapping("/count")
    public long countUnread() {

        User currentUser = currentUserService.getCurrentUser();
        if (currentUser == null) {
            return 0;
        }
        return notificationService.countUnreadNotifications(currentUser);
    }
}
