package org.librarymanagement.dto.response;

public record LoginResponseDto(
        boolean success,
        String message,
        String token,
        String username,
        int role
) {}
