package org.librarymanagement.service;

import org.librarymanagement.dto.response.ResponseObject;
import org.librarymanagement.entity.Notification;
import org.librarymanagement.entity.User;

import java.util.List;

public interface NotificationService {
    void notify(String title, String content, String type, User user);
    ResponseObject getUnreadNotifications(User user);
    long countUnreadNotifications(User user);
}
