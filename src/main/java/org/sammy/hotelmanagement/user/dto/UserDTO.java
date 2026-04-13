package org.sammy.hotelmanagement.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.sammy.hotelmanagement.user.UserType;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    public Long id;
    public String fullName;
    public String username;
    public String email;
    public String phoneNumber;
    public UserType userType;
    public boolean isActive;
    public LocalDateTime createdAt;
}
