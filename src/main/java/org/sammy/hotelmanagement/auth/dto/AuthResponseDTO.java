package org.sammy.hotelmanagement.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.sammy.hotelmanagement.user.dto.UserDTO;

@Data
@AllArgsConstructor
public class AuthResponseDTO {
    private String token;
    private UserDTO user;
}
