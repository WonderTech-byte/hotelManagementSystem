package org.sammy.hotelmanagement.user;

import lombok.RequiredArgsConstructor;
import org.sammy.hotelmanagement.user.dto.UserDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // ADMIN — get all users
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // ADMIN — get user by id
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }


    @GetMapping("/type/{userType}")
    public ResponseEntity<List<UserDTO>> getUsersByType(@PathVariable UserType userType) {
        return ResponseEntity.ok(userService.getUsersByType(userType));
    }

    // ADMIN — deactivate a user
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<UserDTO> deactivateUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.deactivateUser(id));
    }

    // ADMIN — reactivate a user
    @PatchMapping("/{id}/activate")
    public ResponseEntity<UserDTO> activateUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.activateUser(id));
    }

    // ADMIN — change a user's role
    @PatchMapping("/{id}/role/{userType}")
    public ResponseEntity<UserDTO> changeUserType(
            @PathVariable Long id,
            @PathVariable UserType userType) {
        return ResponseEntity.ok(userService.changeUserType(id, userType));
    }

    @PatchMapping("/{id}/promote/admin")
    public ResponseEntity<UserDTO> promoteToAdmin(@PathVariable Long id) {
        return ResponseEntity.ok(userService.promoteToAdmin(id));
    }

    @PatchMapping("/{id}/promote/front-desk")
    public ResponseEntity<UserDTO> promoteToFrontDesk(@PathVariable Long id) {
        return ResponseEntity.ok(userService.promoteToFrontDesk(id));
    }
}
