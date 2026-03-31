package ru.sshibko.backend_seblog.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.sshibko.backend_seblog.dto.UserProfileDto;
import ru.sshibko.backend_seblog.mapper.UserProfileMapperService;
import ru.sshibko.backend_seblog.model.entity.User;
import ru.sshibko.backend_seblog.model.entity.UserProfile;
import ru.sshibko.backend_seblog.model.repository.UserProfileRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;

    private final UserService userService;

    private final UserProfileMapperService  userProfileMapper;

    private final FileStorageService fileStorageService;

    @Transactional
    public UserProfileDto getCurrentUserProfile() {
        User user = userService.getCurrentUser();
        UserProfile userProfile = userProfileRepository.findByUserId(user.getId());
        if (userProfile == null) {
            userProfile = createDefaultProfile(user);
        }

        return userProfileMapper.mapToDto(userProfile);
    }

    private UserProfile createDefaultProfile(User user) {
        UserProfile userProfile = new UserProfile();
        userProfile.setUser(user);

        return userProfileRepository.save(userProfile);
    }

    @Transactional
    public UserProfileDto updateCurrentUserProfile(UserProfileDto userProfileDto) {
        return userProfileMapper.mapToDto(userProfileRepository.save(userProfileMapper.mapToEntity(userProfileDto)));
    }

    @Transactional
    public String uploadAvatar(MultipartFile file) {
        User user = userService.getCurrentUser();
        UserProfile userProfile = userProfileRepository.findByUserId(user.getId());
        if (userProfile == null) {
            userProfile = createDefaultProfile(user);
        }
        String fileName = fileStorageService.store(file);
        userProfile.setAvatarUrl(fileName);
        userProfileRepository.save(userProfile);

        return userProfile.getAvatarUrl();
    }
}
