package org.librarymanagement.service;

import org.librarymanagement.dto.response.ResponseObject;
import org.librarymanagement.dto.response.UserDetailReponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    Page<UserDetailReponse> getAllUsers(Pageable pageable);
    ResponseObject updateUserRole(Integer id, Integer role);
}
