package org.librarymanagement.dto.response;

public record UserDetailReponse(
        Integer id,
        String name,
        String username,
        String password,
        Integer status,
        boolean activatedStatus,
        String email,
        String phone,
        Integer role
) {
}
