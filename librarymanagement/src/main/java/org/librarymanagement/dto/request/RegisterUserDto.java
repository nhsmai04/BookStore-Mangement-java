package org.librarymanagement.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegisterUserDto {

    @NotBlank(message = "Name is mandatory")
    private String name;

    @NotBlank(message = "Username is mandatory")
    private String username;

    @NotBlank(message = "Password cannot be null")
    @Size(min = 9 , max = 20 , message = "Password musth be between 8 and 20 characters")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character")
    private String password;

    @NotBlank(message = "Email cannot be null")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Phone number cannot be null")
    @Pattern(regexp = "^(\\+84|0)\\d{9}$", message = "Invalid phone number format")
    private String phone;

    private Integer role;
}
