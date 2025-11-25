package org.librarymanagement.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.librarymanagement.dto.request.RegisterUserDto;
import org.librarymanagement.dto.response.ResponseObject;
import org.librarymanagement.entity.EmailType;
import org.librarymanagement.entity.User;
import org.librarymanagement.exception.DuplicateFieldException;
import org.librarymanagement.repository.UserRepository;
import org.librarymanagement.service.EmailService;
import org.librarymanagement.service.RegisterService;
import org.librarymanagement.utils.JwtUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Service
@Slf4j
public class RegisterServiceImpl implements RegisterService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private EmailService emailService;
    private MessageSource messageSource;

    @Autowired
    public RegisterServiceImpl(UserRepository userRepository, ModelMapper modelMapper, EmailService emailService, MessageSource messageSource) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.emailService = emailService;
        this.messageSource = messageSource;
    }

    @Autowired
    @Lazy
    public PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public Map<String, String> checkDuplicated(RegisterUserDto registerUserDto) {
        Map<String, String> errors = new HashMap<>();

        Map<String,Boolean> checks = Map.of(
                "username" , userRepository.existsUserByUsername(registerUserDto.getUsername()),
                "email" , userRepository.existsUserByEmail(registerUserDto.getEmail()),
                "phone" , userRepository.existsUserByPhone(registerUserDto.getPhone())
        );

        Map<String,String> messages = Map.of(
                "username" , "Username is already taken!",
                "email" , "Email already exists",
                "phone" , "Phone already exists"
        );

        checks.forEach((error,exists) -> {
            if(exists)
            {
                errors.put(error,messages.get(error));
            }
        });
        return errors;
    }

    @Override
    public void saveUser(User user) {
        if (user.getRole() == null)
            user.setRole(0);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    @Override
    public ResponseObject registerUser(RegisterUserDto registerUserDto, Locale locale) {

        Map<String, String> errors = checkDuplicated(registerUserDto);
        if (!errors.isEmpty()) {
            throw new DuplicateFieldException(errors);
        }

        // Tạo người dùng mới
        User user = modelMapper.map(registerUserDto, User.class);
        String newToken = jwtUtil.generateVerificationToken(user.getEmail());
        user.setVerificationToken(newToken);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        emailService.sendEmail(user.getEmail(), newToken, EmailType.VERIFICATION);
        String successMessage = messageSource.getMessage("user.registration.success", null, locale);
        return new ResponseObject(successMessage, HttpStatus.CREATED.value(), registerUserDto);
    }
}
