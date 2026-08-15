package ru.sshibko.backend_seblog.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.sshibko.backend_seblog.dto.ApiResponse;
import ru.sshibko.backend_seblog.dto.UserProfileDto;
import ru.sshibko.backend_seblog.dto.UserPublicProfileDto;
import ru.sshibko.backend_seblog.exception.SuccessCode;
import ru.sshibko.backend_seblog.service.MessageService;
import ru.sshibko.backend_seblog.service.UserProfileService;

import java.util.Locale;
import java.util.UUID;

@CrossOrigin(origins = "http:/localhost:4200", allowCredentials = "true")
@RestController
@RequestMapping(value = "/api/v1/users/profile", produces = "application/json")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "UserProfile", description = "User Profile Management API")
public class UserProfileController {

    private final UserProfileService userProfileService;

    private final MessageService messageService;

    private final Locale locale = LocaleContextHolder.getLocale();

    @GetMapping("/me")
    @Operation(summary = "Getting user details for owner or ADMIN")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN, ROLE_MODERATOR')")
    public ApiResponse<UserProfileDto> getCurrentUserProfile() {
        UserProfileDto userProfileDto = userProfileService.getCurrentUserProfile();
        String message = messageService.getSuccessMessage(SuccessCode.USER_PROFILE_RECEIVED, locale);

        return ApiResponse.success(userProfileDto, message);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Getting user details by id")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN, ROLE_MODERATOR')")
    public ApiResponse<UserProfileDto> getUserProfileById(@PathVariable UUID id) {
        UserProfileDto userProfileDto = userProfileService.getUserProfileById(id);
        String message = messageService.getSuccessMessage(SuccessCode.USER_PROFILE_RECEIVED, locale);

        return ApiResponse.success(userProfileDto, message);
    }

    @PutMapping("/me")
    @Operation(summary = "Getting user details for owner, ADMIN or visiters")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN', 'ROLE_MODERATOR')")
    public ApiResponse<UserProfileDto> updateUserProfile(@Valid @RequestBody UserProfileDto userProfileDto) {
        UserProfileDto updatedUserProfileDto = userProfileService.updateCurrentUserProfile(userProfileDto);
        String message = messageService.getSuccessMessage(SuccessCode.USER_PROFILE_UPDATED, locale);

        return ApiResponse.success(updatedUserProfileDto, message);
    }

    @PostMapping(value = "/me/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Uploading avatar for owner")
    @PreAuthorize("hasAnyRole('ROLE_USER')")
    public ApiResponse<String> uploadAvatar(
            @Parameter(description = "Avatar image file")
            @RequestParam("file") MultipartFile file) {
        String uploadedAvatar = userProfileService.uploadAvatar(file);
        String message = messageService.getSuccessMessage(SuccessCode.AVATAR_UPLOADED, locale);

        return ApiResponse.success(uploadedAvatar, message);
    }

    @GetMapping("/public/{userId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Публичный профиль пользователя")
    public ApiResponse<UserPublicProfileDto> getUserPublicProfile(
            @Parameter(description = "ID пользователя")
            @PathVariable UUID userId) {
        UserPublicProfileDto profileDto = userProfileService.getUserPublicProfile(userId);

        return ApiResponse.success(profileDto, "Public profile retrieved");
    }
}
