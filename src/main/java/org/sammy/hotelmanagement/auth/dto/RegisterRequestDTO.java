package org.sammy.hotelmanagement.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequestDTO {
    @NotBlank public String fullName;
    @NotBlank public String username;
    @NotBlank @Email public String email;
    @NotBlank @Size(min = 6) public String password;
    public String phoneNumber;
}
