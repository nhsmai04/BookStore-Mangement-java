package org.librarymanagement.dto.response;

import java.time.LocalDateTime;

public record ReviewResponse(
        Integer id,
        String comment,
        Integer star,
        LocalDateTime createdAt,
        UserResponse user
) {}
