package org.sammy.hotelmanagement.auth;

import lombok.RequiredArgsConstructor;
import org.sammy.hotelmanagement.dto.AuthResponseDTO;
import org.sammy.hotelmanagement.dto.LoginRequestDTO;
import org.sammy.hotelmanagement.dto.RegisterRequestDTO;
import org.sammy.hotelmanagement.dto.UserDTO;
import org.sammy.hotelmanagement.user.User;
import org.sammy.hotelmanagement.user.UserRepository;
import org.sammy.hotelmanagement.user.UserType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

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
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username, request.password)
        );
        User user = userRepository.findByUsername(request.username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        String token = jwtService.generateToken(userDetails);
        return new AuthResponseDTO(token, toDTO(user));
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
