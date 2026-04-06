package org.sammy.hotelmanagement.auth;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sammy.hotelmanagement.dto.AuthResponseDTO;
import org.sammy.hotelmanagement.dto.LoginRequestDTO;
import org.sammy.hotelmanagement.dto.RegisterRequestDTO;
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

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
