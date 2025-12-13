package ru.sshibko.backend_seblog.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.sshibko.backend_seblog.exception.AuthenticationException;
import ru.sshibko.backend_seblog.exception.ResourceNotFoundException;
import ru.sshibko.backend_seblog.model.entity.User;
import ru.sshibko.backend_seblog.model.entity.enums.UserRole;
import ru.sshibko.backend_seblog.model.repository.UserRepository;
import ru.sshibko.backend_seblog.security.CustomUserDetailsService;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

/*    public User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthenticationException("User not found with email: " + email));
    }*/

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AuthenticationException("Пользователь не аутентифицирован");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof User user) {
            log.info("User: {}", user);
            return user;
        } else if (principal instanceof org.springframework.security.core.userdetails.User userDetails) {
            log.info("User from user Details: {}", userDetails);
            log.info("User converted: {}", convertUserDetailsToUser(userDetails));
            User user = convertUserDetailsToUser(userDetails);
            log.info("User: {} {} {} {}", user.getId(), user.getUsername(), user.getEmail(), user.getRole());
            return user;
        } else {
            log.error("Unknown user principal type: {}", principal.getClass().getName());
            throw new ResourceNotFoundException("Не удалось получить информацию о пользователе");
        }
    }

    private User convertUserDetailsToUser(org.springframework.security.core.userdetails.User userDetails) {
        String roleName = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse(null);

        UserRole role = UserRole.valueOf(roleName);
        return User.builder()
                .email(userDetails.getUsername())
                .passwordHash(userDetails.getPassword())
                .role(role)
                .build();
    }

    public User getCurrentUserOrNull() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof User user) {
            return user;
        }

        return null;
    }

    public boolean isAuthenticated() {
        return getCurrentUserOrNull() != null;
    }
}
