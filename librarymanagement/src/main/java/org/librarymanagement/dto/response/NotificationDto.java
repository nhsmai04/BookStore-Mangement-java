package org.librarymanagement.dto.response;

import java.time.LocalDateTime;

public record NotificationDto(
    Integer id,
    String title,
    String content,
    String type,
    LocalDateTime createdAt
) {
}
