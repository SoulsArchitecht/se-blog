package ru.sshibko.backend_seblog.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.sshibko.backend_seblog.dto.ApiResponse;
import ru.sshibko.backend_seblog.dto.response.PostTypeResponse;
import ru.sshibko.backend_seblog.exception.SuccessCode;
import ru.sshibko.backend_seblog.service.MessageService;
import ru.sshibko.backend_seblog.service.PostTypeService;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/post-types")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@Tag(name = "PostType", description = "Справочник типов постов")
public class PostTypeController {

    private final PostTypeService postTypeService;

    private final MessageService messageService;

    private final Locale locale = LocaleContextHolder.getLocale();

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<List<PostTypeResponse>> getAllPostTypes() {
        List<PostTypeResponse> types = postTypeService.getAllPostTypes();
        String message = messageService.getSuccessMessage(
                SuccessCode.OPERATION_SUCCESSFUL, locale);
        return ApiResponse.success(types, message);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<PostTypeResponse> getPostTypeById(@PathVariable UUID id) {
        PostTypeResponse type = postTypeService.getPostTypeById(id);
        String message = messageService.getSuccessMessage(
                SuccessCode.OPERATION_SUCCESSFUL, locale);
        return ApiResponse.success(type, message);
    }
}
