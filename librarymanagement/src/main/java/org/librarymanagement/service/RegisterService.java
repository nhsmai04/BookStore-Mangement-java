package org.librarymanagement.service;

import org.librarymanagement.dto.request.RegisterUserDto;
import org.librarymanagement.dto.response.ResponseObject;
import org.librarymanagement.entity.User;

import java.util.Locale;

public interface RegisterService {
    ResponseObject registerUser(RegisterUserDto registerUserDto, Locale locale);
    void saveUser(User user);
}
