package org.sammy.hotelmanagement.auth;

import lombok.RequiredArgsConstructor;
import org.sammy.hotelmanagement.auth.dto.AuthResponseDTO;
import org.sammy.hotelmanagement.auth.dto.CreateAdminRequestDto;
import org.sammy.hotelmanagement.auth.dto.ForgotPasswordRequestDTO;
import org.sammy.hotelmanagement.auth.dto.LoginRequestDTO;
import org.sammy.hotelmanagement.auth.dto.MessageResponseDTO;
import org.sammy.hotelmanagement.auth.dto.RegisterRequestDTO;
import org.sammy.hotelmanagement.auth.dto.ResetPasswordRequestDTO;
import org.sammy.hotelmanagement.exception.BadRequestException;
import org.sammy.hotelmanagement.exception.ConflictException;
import org.sammy.hotelmanagement.exception.NotFoundException;
import org.sammy.hotelmanagement.user.dto.UserDTO;
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
        User user = User.builder()
                .fullName(request.fullName)
                .username(request.username)
                .email(request.email)
                .password(passwordEncoder.encode(request.password))
                .phoneNumber(request.phoneNumber)
                .userType(UserType.GUEST)
                .isActive(true)
                .build();

        return createAuthenticatedResponse(user);
    }

    public AuthResponseDTO bootstrapAdmin(CreateAdminRequestDto request) {
        if (!userRepository.findByUserType(UserType.ADMIN).isEmpty()) {
            throw new ConflictException("An admin user already exists");
        }

        User user = User.builder()
                .fullName(request.fullName)
                .username(request.username)
                .email(request.email)
                .password(passwordEncoder.encode(request.password))
                .phoneNumber(request.phoneNumber)
                .userType(UserType.ADMIN)
                .isActive(true)
                .build();

        return createAuthenticatedResponse(user);
    }

    public AuthResponseDTO login(LoginRequestDTO request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.username, request.password)
            );
            User user = userRepository.findByUsername(request.username)
                    .orElseThrow(() -> new NotFoundException("User not found"));

            UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
            String token = jwtService.generateToken(userDetails);
            return new AuthResponseDTO(token, toDTO(user));
        } catch (Exception e) {
            throw new BadRequestException("Invalid username or password");
        }
    }

    public MessageResponseDTO forgotPassword(ForgotPasswordRequestDTO request) {
        User user = userRepository.findByEmail(request.email)
                .orElseThrow(() -> new NotFoundException("No user found with email: " + request.email));

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
                .orElseThrow(() -> new BadRequestException("Invalid password reset token"));

        if (user.getPasswordResetTokenExpiresAt() == null
                || user.getPasswordResetTokenExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Password reset token has expired");
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

    private AuthResponseDTO createAuthenticatedResponse(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new ConflictException("Username already exists: " + user.getUsername());
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new ConflictException("Email already exists: " + user.getEmail());
        }

        userRepository.save(user);

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        String token = jwtService.generateToken(userDetails);
        return new AuthResponseDTO(token, toDTO(user));
    }
}
