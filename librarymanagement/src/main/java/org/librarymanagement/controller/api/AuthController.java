package org.librarymanagement.controller.api;

import jakarta.validation.Valid;
import org.librarymanagement.constant.ApiEndpoints;
import org.librarymanagement.dto.request.RegisterUserDto;
import org.librarymanagement.dto.response.LoginResponseDto;
import org.librarymanagement.dto.request.LoginUserDto;
import org.librarymanagement.dto.response.ResponseObject;
import org.librarymanagement.service.AuthService;
import org.librarymanagement.service.RegisterService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Locale;

@RestController("userAuthController")
@RequestMapping(ApiEndpoints.USER_AUTH)
public class AuthController {
    private final AuthService loginService;
    private final RegisterService registerService;

    public AuthController(AuthService loginService , RegisterService registerService) {
        this.loginService = loginService;
        this.registerService = registerService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginUserDto loginUserDto) {
        LoginResponseDto response = loginService.login(loginUserDto);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @PostMapping("/register")
    ResponseEntity<ResponseObject> registerUser(@Valid @RequestBody RegisterUserDto registerUserDto, Locale locale){
        ResponseObject responseObject =  registerService.registerUser(registerUserDto, locale);
        return ResponseEntity.status(responseObject.status())
                .body(responseObject);
    }

    @GetMapping("/register/verify")
    public ModelAndView verifyEmail(@RequestParam("token") String token, Locale locale) {
        ResponseObject result = loginService.verifyEmail(token, locale);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("message", result.message());

        if (result.status() == HttpStatus.OK.value()) {
            modelAndView.setViewName("email/verification-success");
        }

        else {
            modelAndView.setViewName("email/verification-error");
        }

        return modelAndView;
    }
}
