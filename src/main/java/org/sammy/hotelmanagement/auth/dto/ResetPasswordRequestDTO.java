package org.sammy.hotelmanagement.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResetPasswordRequestDTO {
    @NotBlank
    public String token;

    @NotBlank
    @Size(min = 6)
    public String newPassword;
}
