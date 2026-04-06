package org.sammy.hotelmanagement.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.sammy.hotelmanagement.user.UserType;

@Data
public class RegisterRequestDTO {
    @NotBlank public String fullName;
    @NotBlank public String username;
    @NotBlank @Email public String email;
    @NotBlank @Size(min = 6) public String password;
    public String phoneNumber;
    @NotNull public UserType userType;
}
