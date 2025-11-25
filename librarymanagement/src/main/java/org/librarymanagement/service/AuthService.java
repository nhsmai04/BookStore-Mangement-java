package org.librarymanagement.service;

import org.librarymanagement.dto.response.LoginResponseDto;
import org.librarymanagement.dto.request.LoginUserDto;
import org.librarymanagement.dto.response.ResponseObject;

import java.util.Locale;

public interface AuthService {
    LoginResponseDto login(LoginUserDto loginUserDto);
    ResponseObject verifyEmail(String token, Locale locale);
}
