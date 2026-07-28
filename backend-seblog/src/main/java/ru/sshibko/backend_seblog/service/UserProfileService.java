package ru.sshibko.backend_seblog.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.sshibko.backend_seblog.dto.UserProfileDto;
import ru.sshibko.backend_seblog.exception.ErrorCode;
import ru.sshibko.backend_seblog.exception.NotFoundException;
import ru.sshibko.backend_seblog.mapper.UserProfileMapperService;
import ru.sshibko.backend_seblog.model.entity.User;
import ru.sshibko.backend_seblog.model.entity.UserProfile;
import ru.sshibko.backend_seblog.model.repository.UserProfileRepository;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;

    private final UserService userService;

    private final UserProfileMapperService  userProfileMapper;

    private final FileStorageService fileStorageService;

    @Transactional(readOnly = true)
    public UserProfileDto getCurrentUserProfile() {
        User user = userService.getCurrentUser();
        UserProfile userProfile = userProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new NotFoundException(
                        ErrorCode.USER_PROFILE_NOT_FOUND,
                        "UserProfile",
                        user.getId(),
                        "UserProfile not found for user: " + user.getId()
                ));

        return userProfileMapper.mapToDto(userProfile);
    }

    @Transactional(readOnly = true)
    public UserProfileDto getUserProfileById(UUID id) {
        UserProfile userProfile = userProfileRepository.findByUserId(id)
                .orElseThrow(() -> new NotFoundException(
                        ErrorCode.USER_PROFILE_NOT_FOUND,
                        "UserProfile",
                        id,
                        "UserProfile not found with id: " + id));

        return userProfileMapper.mapToDto(userProfile);
    }

    @Transactional
    protected UserProfile createDefaultProfile(User user) {
/*        UserProfile userProfile = new UserProfile();
        userProfile.setUser(user);*/
        UserProfile userProfile = UserProfile.builder()
                //.id(user.getId())
                .user(user)
                .build();

        return userProfileRepository.save(userProfile);
    }

    @Transactional
    public UserProfileDto updateCurrentUserProfile(UserProfileDto userProfileDto) {
        User currentUser = userService.getCurrentUser();
        UUID userId = currentUser.getId();

        UserProfile userProfile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException(
                        ErrorCode.USER_PROFILE_NOT_FOUND,
                        "UserProfile",
                        userId,
                        "UserProfile not found with id: " + userId));
        if (userProfile == null) {
            userProfile = createDefaultProfile(currentUser);
        }

        if (userProfileDto.getDisplayName() != null) {
            userProfile.setDisplayName(userProfileDto.getDisplayName());
        }
        if (userProfileDto.getFirstName() != null) {
            userProfile.setFirstName(userProfileDto.getFirstName());
        }
        if (userProfileDto.getLastName() != null) {
            userProfile.setLastName(userProfileDto.getLastName());
        }
        if (userProfileDto.getBirthDate() != null) {
            userProfile.setBirthDate(userProfileDto.getBirthDate());
        }
        if (userProfileDto.getPhone() != null) {
            userProfile.setPhone(userProfileDto.getPhone());
        }
        if (userProfileDto.getAvatarUrl() != null) {
            userProfile.setAvatarUrl(userProfileDto.getAvatarUrl());
        }
        if (userProfileDto.getBio() != null) {
            userProfile.setBio(userProfileDto.getBio());
        }
        if (userProfileDto.getLocation() != null) {
            userProfile.setLocation(userProfileDto.getLocation());
        }
        if (userProfileDto.getOptionalEmail() != null) {
            userProfile.setOptionalEmail(userProfileDto.getOptionalEmail());
        }

        UserProfile updatedUserProfile = userProfileRepository.save(userProfile);

        return userProfileMapper.mapToDto(updatedUserProfile);
    }

    @Transactional
    public String uploadAvatar(MultipartFile file) {
        User user = userService.getCurrentUser();
        UserProfile userProfile = userProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new NotFoundException(
                        ErrorCode.USER_PROFILE_NOT_FOUND,
                        "UserProfile",
                        user.getId(),
                        "UserProfile not found with id: " + user.getId()));
        if (userProfile == null) {
            userProfile = createDefaultProfile(user);
        }
        String fileName = fileStorageService.store(file);
        userProfile.setAvatarUrl(fileName);
        userProfileRepository.save(userProfile);

        return userProfile.getAvatarUrl();
    }
}
