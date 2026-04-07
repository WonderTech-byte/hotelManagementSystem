package org.sammy.hotelmanagement.auth;

import lombok.RequiredArgsConstructor;
import org.sammy.hotelmanagement.dto.AuthResponseDTO;
import org.sammy.hotelmanagement.dto.ForgotPasswordRequestDTO;
import org.sammy.hotelmanagement.dto.LoginRequestDTO;
import org.sammy.hotelmanagement.dto.MessageResponseDTO;
import org.sammy.hotelmanagement.dto.RegisterRequestDTO;
import org.sammy.hotelmanagement.dto.ResetPasswordRequestDTO;
import org.sammy.hotelmanagement.dto.UserDTO;
import org.sammy.hotelmanagement.notification.EmailNotificationService;
import org.sammy.hotelmanagement.user.User;
import org.sammy.hotelmanagement.user.UserRepository;
import org.sammy.hotelmanagement.user.UserType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final EmailNotificationService emailNotificationService;

    @Value("${app.password-reset.base-url:http://localhost:8080/reset-password}")
    private String passwordResetBaseUrl;

    @Value("${app.password-reset.expiration-minutes:30}")
    private long passwordResetExpirationMinutes;

    public AuthResponseDTO register(RegisterRequestDTO request) {
        if (userRepository.existsByUsername(request.username)) {
            throw new RuntimeException("Username already taken");
        }
        if (userRepository.existsByEmail(request.email)) {
            throw new RuntimeException("Email already registered");
        }

        User user = User.builder()
                .fullName(request.fullName)
                .username(request.username)
                .email(request.email)
                .password(passwordEncoder.encode(request.password))
                .phoneNumber(request.phoneNumber)
                .userType(request.userType != null ? request.userType : UserType.GUEST)
                .isActive(true)
                .build();

        userRepository.save(user);

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        String token = jwtService.generateToken(userDetails);
        return new AuthResponseDTO(token, toDTO(user));
    }

    public AuthResponseDTO login(LoginRequestDTO request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.username, request.password)
            );
            User user = userRepository.findByUsername(request.username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
            String token = jwtService.generateToken(userDetails);
            return new AuthResponseDTO(token, toDTO(user));
        } catch (Exception e) {
            throw new RuntimeException("Invalid username or password");
        }
    }

    public MessageResponseDTO forgotPassword(ForgotPasswordRequestDTO request) {
        User user = userRepository.findByEmail(request.email)
                .orElseThrow(() -> new RuntimeException("User with this email was not found"));

        String resetToken = UUID.randomUUID().toString();
        user.setPasswordResetToken(resetToken);
        user.setPasswordResetTokenExpiresAt(LocalDateTime.now().plusMinutes(passwordResetExpirationMinutes));
        userRepository.save(user);

        String resetLink = passwordResetBaseUrl + "?token=" + resetToken;
        emailNotificationService.sendPasswordResetEmail(user, resetLink);

        return new MessageResponseDTO("Password reset instructions have been sent to your email");
    }

    public MessageResponseDTO resetPassword(ResetPasswordRequestDTO request) {
        User user = userRepository.findByPasswordResetToken(request.token)
                .orElseThrow(() -> new RuntimeException("Invalid password reset token"));

        if (user.getPasswordResetTokenExpiresAt() == null
                || user.getPasswordResetTokenExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Password reset token has expired");
        }

        user.setPassword(passwordEncoder.encode(request.newPassword));
        user.setPasswordResetToken(null);
        user.setPasswordResetTokenExpiresAt(null);
        userRepository.save(user);

        return new MessageResponseDTO("Password has been reset successfully");
    }

    private UserDTO toDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .username(user.getUsername())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .userType(user.getUserType())
                .isActive(user.isActive())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
