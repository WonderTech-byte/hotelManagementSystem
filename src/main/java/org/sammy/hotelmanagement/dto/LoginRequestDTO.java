package org.sammy.hotelmanagement.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequestDTO {
    @NotBlank public String username;
    @NotBlank public String password;
}
