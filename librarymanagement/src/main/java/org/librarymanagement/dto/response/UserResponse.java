package org.librarymanagement.dto.response;

public record UserResponse(
        Integer id,
        String name,
        String username
) {}
