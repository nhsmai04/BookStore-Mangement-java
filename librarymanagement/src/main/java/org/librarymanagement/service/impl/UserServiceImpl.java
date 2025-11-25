package org.librarymanagement.service.impl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.librarymanagement.constant.RoleConstants;
import org.librarymanagement.dto.response.ResponseObject;
import org.librarymanagement.dto.response.UserDetailReponse;
import org.librarymanagement.repository.UserRepository;
import org.librarymanagement.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


import org.springframework.security.access.AccessDeniedException;


@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Page<UserDetailReponse> getAllUsers(Pageable pageable) {
        return userRepository.findAllUser(pageable);
    }

    @Transactional
    public ResponseObject updateUserRole(Integer id, Integer role) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth.getAuthorities().stream()
                .noneMatch(a -> a.getAuthority().equals("ROLE_MANAGER"))) {
            throw new AccessDeniedException("Bạn không có quyền đổi role");
        }

        if(!RoleConstants.isValid(role)) {
            throw new IllegalArgumentException("Invalid role");
        }

        int rowsAffected = userRepository.updateUserRole(id, role);
        if (rowsAffected == 0) {
            throw new EntityNotFoundException("User not found with id: " + id);
        }

        return new ResponseObject("success", 200,"User role updated successfully");
    }

}
