package org.sammy.hotelmanagement.auth;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sammy.hotelmanagement.auth.dto.AuthResponseDTO;
import org.sammy.hotelmanagement.auth.dto.CreateAdminRequestDto;
import org.sammy.hotelmanagement.auth.dto.ForgotPasswordRequestDTO;
import org.sammy.hotelmanagement.auth.dto.LoginRequestDTO;
import org.sammy.hotelmanagement.auth.dto.MessageResponseDTO;
import org.sammy.hotelmanagement.auth.dto.RegisterRequestDTO;
import org.sammy.hotelmanagement.auth.dto.ResetPasswordRequestDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody RegisterRequestDTO request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/bootstrap-admin")
    public ResponseEntity<AuthResponseDTO> createAdmin(@Valid @RequestBody CreateAdminRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.bootstrapAdmin(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<MessageResponseDTO> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequestDTO request) {
        return ResponseEntity.ok(authService.forgotPassword(request));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<MessageResponseDTO> resetPassword(
            @Valid @RequestBody ResetPasswordRequestDTO request) {
        return ResponseEntity.ok(authService.resetPassword(request));
    }
}
