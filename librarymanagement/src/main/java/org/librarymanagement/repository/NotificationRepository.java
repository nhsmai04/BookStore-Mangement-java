package org.librarymanagement.repository;

import org.librarymanagement.entity.Notification;
import org.librarymanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    @Query("SELECT n FROM Notification n WHERE n.user = :user AND n.isRead = false ORDER BY n.createdAt DESC")
    List<Notification> findByUserAndIsReadFalseOrderByCreatedAtDesc(User user);

    @Query("SELECT COUNT(n) FROM Notification n WHERE n.user = :user AND n.isRead = false")
    long countByUserAndIsReadFalse(User user);
}
