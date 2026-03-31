package ru.sshibko.backend_seblog.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.sshibko.backend_seblog.dto.ApiResponse;
import ru.sshibko.backend_seblog.dto.UserProfileDto;
import ru.sshibko.backend_seblog.exception.SuccessCode;
import ru.sshibko.backend_seblog.service.MessageService;
import ru.sshibko.backend_seblog.service.UserProfileService;

import java.util.Locale;

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
}
