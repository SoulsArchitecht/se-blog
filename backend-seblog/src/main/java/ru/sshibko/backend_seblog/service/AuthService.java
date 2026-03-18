package ru.sshibko.backend_seblog.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.sshibko.backend_seblog.dto.UserDto;
import ru.sshibko.backend_seblog.dto.security.AuthRequest;
import ru.sshibko.backend_seblog.dto.security.AuthResponse;
import ru.sshibko.backend_seblog.dto.security.RefreshTokenRequest;
import ru.sshibko.backend_seblog.dto.security.RegisterRequest;
import ru.sshibko.backend_seblog.exception.*;
import ru.sshibko.backend_seblog.mapper.UserMapperService;
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
    private final UserMapperService userMapperService;

    public AuthResponse login(AuthRequest authRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getEmail(),
                            authRequest.getPassword())
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String accessToken = jwtUtils.generateToken(userDetails);
            String refreshToken = jwtUtils.generateRefreshToken(userDetails);

            User user = userRepository.findByEmail(authRequest.getEmail())
                    .orElseThrow(() -> new NotFoundException(
                            ErrorCode.USER_NOT_FOUND,
                            "User",
                            authRequest.getEmail(),
                            "User not found with email: " + authRequest.getEmail())
                    );

            UserDto userDto = userMapperService.mapToUserDto(user);

            return new AuthResponse(accessToken, refreshToken, userDto);
        } catch (BadCredentialsException e) {
            throw new AuthenticationException(
                    ErrorCode.ACCESS_DENIED,
                    "Неправильное имя пользователя или пароль",
                    "Wrong password or username at login user with email: " + authRequest.getEmail());
        }
    }

    public AuthResponse refreshToken(RefreshTokenRequest  refreshTokenRequest) {
        try {
            String username = jwtUtils.extractUsername(refreshTokenRequest.getRefreshToken());
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new NotFoundException(
                            ErrorCode.USER_NOT_FOUND,
                            "User",
                            username,
                            "User not found with username: " + username)
                    );

            UserDetails userDetails = user.toUserDetails();

            if (!jwtUtils.validateToken(refreshTokenRequest.getRefreshToken())) {
                throw new InvalidTokenException(
                        ErrorCode.ACCESS_DENIED,
                        "Невалидный токен",
                        refreshTokenRequest.getRefreshToken()
                );
            }

            String newAccessToken = jwtUtils.generateToken(userDetails);
            String newRefreshToken = jwtUtils.generateRefreshToken(userDetails);

            UserDto userDto = userMapperService.mapToUserDto(user);

            return new AuthResponse(newAccessToken, newRefreshToken,  userDto);
        } catch (Exception e) {
            throw new InvalidTokenException(
                    ErrorCode.ACCESS_DENIED,
                    "Невалидный токен",
                    refreshTokenRequest.getRefreshToken()
            );
        }
    }

    public AuthResponse register(RegisterRequest registerRequest) {
        if (userRepository.findByUsername(registerRequest.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException(
                    ErrorCode.USER_ALREADY_EXISTS,
                    "Пользователь с данным именем уже существует",
                    "User already exists with username: " + registerRequest.getUsername()
            );
        }

        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException(
                    ErrorCode.USER_ALREADY_EXISTS,
                    "Пользователь с данной почтой уже существует",
                    "User already exists with email: " + registerRequest.getEmail()
            );
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

        UserDto userDto = userMapperService.mapToUserDto(savedUser);

        return new AuthResponse(accessToken, refreshToken, userDto);
    }

    public void validateToken(String token) {
        try {
            String username = jwtUtils.extractUsername(token);
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new InvalidTokenException(
                            ErrorCode.ACCESS_DENIED,
                            "Невалидный токен",
                            "Invalid token: " + token)
                    );

            if (!jwtUtils.validateToken(token)) {
                throw new InvalidTokenException(
                        ErrorCode.ACCESS_DENIED,
                        "Невалидный токен",
                        "Invalid token: " + token
                );
            }
        } catch (Exception e) {
            throw new InvalidTokenException(
                    ErrorCode.ACCESS_DENIED,
                    "Невалидный токен",
                    "Invalid token: " + token
            );
        }
    }
}
