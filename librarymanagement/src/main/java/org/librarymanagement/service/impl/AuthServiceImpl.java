package org.librarymanagement.service.impl;

import org.librarymanagement.dto.request.LoginUserDto;
import org.librarymanagement.dto.response.LoginResponseDto;
import org.librarymanagement.dto.response.ResponseObject;
import org.librarymanagement.entity.User;
import org.librarymanagement.repository.UserRepository;
import org.librarymanagement.service.AuthService;
import org.librarymanagement.utils.JwtUtil;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Locale;

@Service
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final MessageSource messageSource;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, MessageSource messageSource) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.messageSource = messageSource;
    }

    public LoginResponseDto login(LoginUserDto loginUserDto) {
        // Tìm user theo username
        User user = userRepository.findByUsername(loginUserDto.getUsername())
                .orElse(null);

        if (user == null) {
            return new LoginResponseDto(false, "Tài khoản không tồn tại", null, null, -1);

        }

        // Kiểm tra mật khẩu
        if (!passwordEncoder.matches(loginUserDto.getPassword(), user.getPassword())) {
            return new LoginResponseDto(false, "Sai mật khẩu", null, null, -1);
        }

        // Tạo JWT token
        String token = jwtUtil.generateLoginToken(user.getUsername());

        return new LoginResponseDto(true, "Đăng nhập thành công", token, user.getUsername(), user.getRole());
    }

    @Override
    public ResponseObject verifyEmail(String token, Locale locale) {
        try {
            //Mail xác thực quá hạn
            if (!jwtUtil.validateToken(token)) {
                String message = messageSource.getMessage("email.verification.expired", null, locale);

                return new ResponseObject(message, HttpStatus.FORBIDDEN.value(), token);
            }

            String emailString = jwtUtil.extractEmail(token);
            User user = userRepository.findByEmail(emailString);

            //Không tìm thấy người dùng
            if (user == null) {
                String message = messageSource.getMessage("user.notexist", null, locale);

                return new ResponseObject(message, HttpStatus.NOT_FOUND.value(), null);
            }

            //Email đã được xác thực
            if (user.getVerificationToken() == null) {
                String message = messageSource.getMessage("user.verified.email", null, locale);

                return new ResponseObject(message, HttpStatus.BAD_REQUEST.value(), null);
            }

            //Kiểm tra token có khớp với token trong DB không.
            if (user.getVerificationToken() == null || !user.getVerificationToken().equals(token)) {
                String message = messageSource.getMessage("email.verification.invalid", null, locale);

                return new ResponseObject(message, HttpStatus.BAD_REQUEST.value(), null);
            }

            user.setVerificationToken(null);
            user.setActivatedStatus(true);
            userRepository.save(user);

            //Mail xác thực thành công
            String message = messageSource.getMessage("email.verification.success", null, locale);

            return new ResponseObject(message, HttpStatus.OK.value(), token);
        } catch (Exception e) {
            e.printStackTrace();
            String message = messageSource.getMessage("email.verification.error", null, locale);
            return new ResponseObject(message, HttpStatus.INTERNAL_SERVER_ERROR.value(), token);
        }
    }
}
