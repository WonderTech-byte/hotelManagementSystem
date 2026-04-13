package org.sammy.hotelmanagement.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequestDTO {
    @NotBlank public String username;
    @NotBlank public String password;
}
