package org.sammy.hotelmanagement.user;

import lombok.RequiredArgsConstructor;
import org.sammy.hotelmanagement.exception.NotFoundException;
import org.sammy.hotelmanagement.user.dto.UserDTO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    public List<UserDTO> getAllUsers() {
        return userRepository.findAll()
                .stream().map(this::toDTO).collect(Collectors.toList());
    }


    public UserDTO getUserById(Long id) {
        return toDTO(userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found: " + id)));
    }


    public List<UserDTO> getUsersByType(UserType userType) {
        return userRepository.findByUserType(userType)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }


    @Transactional
    public UserDTO deactivateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found: " + id));
        user.setActive(false);
        return toDTO(userRepository.save(user));
    }


    @Transactional
    public UserDTO activateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found: " + id));
        user.setActive(true);
        return toDTO(userRepository.save(user));
    }


    @Transactional
    public UserDTO changeUserType(Long id, UserType newType) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found: " + id));
        user.setUserType(newType);
        return toDTO(userRepository.save(user));
    }

    @Transactional
    public UserDTO promoteToAdmin(Long id) {
        return changeUserType(id, UserType.ADMIN);
    }

    @Transactional
    public UserDTO promoteToFrontDesk(Long id) {
        return changeUserType(id, UserType.FRONT_DESK);
    }



    public UserDTO toDTO(User user) {
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
