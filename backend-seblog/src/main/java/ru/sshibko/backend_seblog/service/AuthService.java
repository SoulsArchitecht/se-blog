package ru.sshibko.backend_seblog.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.sshibko.backend_seblog.dto.security.AuthRequest;
import ru.sshibko.backend_seblog.dto.security.AuthResponse;
import ru.sshibko.backend_seblog.dto.security.RefreshTokenRequest;
import ru.sshibko.backend_seblog.dto.security.RegisterRequest;
import ru.sshibko.backend_seblog.exception.AuthenticationException;
import ru.sshibko.backend_seblog.exception.InvalidTokenException;
import ru.sshibko.backend_seblog.exception.UserAlreadyExistsException;
import ru.sshibko.backend_seblog.exception.UsernameNotFoundException;
import ru.sshibko.backend_seblog.model.entity.User;
import ru.sshibko.backend_seblog.model.entity.enums.UserRole;
import ru.sshibko.backend_seblog.model.entity.enums.UserStatus;
import ru.sshibko.backend_seblog.model.repository.UserRepository;
import ru.sshibko.backend_seblog.security.JwtUtils;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;

    private final JwtUtils jwtUtils;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public AuthResponse login(AuthRequest authRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getEmail(),
                            authRequest.getPassword())
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String accessToken = jwtUtils.generateToken(userDetails);
            String refreshToken = jwtUtils.generateRefreshToken(userDetails);

            return new AuthResponse(accessToken, refreshToken);
        } catch (BadCredentialsException e) {
            throw new AuthenticationException("Invalid username or password");
        }
    }

    public AuthResponse refreshToken(RefreshTokenRequest  refreshTokenRequest) {
        try {
            String username = jwtUtils.extractUsername(refreshTokenRequest.getRefreshToken());
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found for token: "
                            + refreshTokenRequest.getRefreshToken()));

            UserDetails userDetails = user.toUserDetails();

            if (!jwtUtils.validateToken(refreshTokenRequest.getRefreshToken())) {
                throw new InvalidTokenException("Invalid refresh token");
            }

            String newAccessToken = jwtUtils.generateToken(userDetails);
            String newRefreshToken = jwtUtils.generateRefreshToken(userDetails);

            return new AuthResponse(newAccessToken, newRefreshToken);
        } catch (Exception e) {
            throw new InvalidTokenException("Invalid refresh token: " + e.getMessage());
        }
    }

    public AuthResponse register(RegisterRequest registerRequest) {
        if (userRepository.findByUsername(registerRequest.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("User with username " +
                    registerRequest.getUsername() + " already exists");
        }

        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("User with email " +
                    registerRequest.getEmail() + " already exists");
        }

        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setPasswordHash(passwordEncoder.encode(registerRequest.getPassword()));
        user.setEmail(registerRequest.getEmail());
        user.setRole(UserRole.ROLE_USER);
        user.setStatus(UserStatus.ACTIVE);
        User savedUser = userRepository.save(user);

        UserDetails userDetails = savedUser.toUserDetails();
        String accessToken = jwtUtils.generateToken(userDetails);
        String refreshToken = jwtUtils.generateRefreshToken(userDetails);

        return new AuthResponse(accessToken, refreshToken);
    }

    public void validateToken(String token) {
        try {
            String username = jwtUtils.extractUsername(token);
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new InvalidTokenException("User not found for token"));

            if (!jwtUtils.validateToken(token)) {
                throw new InvalidTokenException("Invalid token");
            }
        } catch (Exception e) {
            throw new InvalidTokenException("Token validation failed: " + e.getMessage());
        }
    }
}
