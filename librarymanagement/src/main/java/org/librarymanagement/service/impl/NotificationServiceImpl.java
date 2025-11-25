package org.librarymanagement.service.impl;

import org.librarymanagement.dto.response.NotificationDto;
import org.librarymanagement.dto.response.ResponseObject;
import org.librarymanagement.entity.Notification;
import org.librarymanagement.entity.User;
import org.librarymanagement.repository.NotificationRepository;
import org.librarymanagement.service.NotificationService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }
    @Override
    public void notify(String title, String content, String type, User user) {
        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setContent(content);
        notification.setType(type);
        notification.setUser(user);
        notification.setCreatedAt(java.time.LocalDateTime.now());

        notificationRepository.save(notification);
    }
    @Override
    public ResponseObject getUnreadNotifications(User user)
    {
        System.out.println("Da chay vao day");
        List<NotificationDto> data = notificationRepository
                        .findByUserAndIsReadFalseOrderByCreatedAtDesc(user)
                        .stream()
                        .map(n -> new NotificationDto(
                                n.getId(),
                                n.getTitle(),
                                n.getContent(),
                                n.getType(),
                                n.getCreatedAt()
                        ))
                        .toList();
        return new ResponseObject(
                "Success",
                200 ,
                data);
    }
    @Override
    public long countUnreadNotifications(User user)
    {
        Long count = notificationRepository.countByUserAndIsReadFalse(user);
        System.out.println(count);
        return count;
    }
}
