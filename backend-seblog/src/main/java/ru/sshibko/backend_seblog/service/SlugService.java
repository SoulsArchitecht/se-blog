package ru.sshibko.backend_seblog.service;

import com.github.slugify.Slugify;
import com.optimaize.langdetect.LanguageDetectorBuilder;
import com.optimaize.langdetect.i18n.LdLocale;
import com.optimaize.langdetect.ngram.NgramExtractors;
import com.optimaize.langdetect.profiles.LanguageProfile;
import com.optimaize.langdetect.profiles.LanguageProfileReader;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import com.optimaize.langdetect.LanguageDetector;
import ru.sshibko.backend_seblog.config.LocaleConfig;

import java.io.IOException;
import java.util.List;
import java.util.function.Function;

@Service
@Slf4j
public class SlugService {

    private Slugify englishSlugify;

    private Slugify russianSlugify;

    private LanguageDetector languageDetector;

    @PostConstruct
    public void init() {
        try {
            List<LanguageProfile> languageProfiles = new LanguageProfileReader().readAllBuiltIn();
            languageDetector = LanguageDetectorBuilder.create(NgramExtractors.standard())
                    .withProfiles(languageProfiles)
                    .build();
        } catch (IOException e) {
            log.warn("Failed to initialize language detector, using fallback", e);
            languageDetector = null;
        }

        // Английский slugifier (без транслитерации)
        englishSlugify = Slugify.builder()
                .transliterator(false)
                .lowerCase(true)
                .underscoreSeparator(false)
                .customReplacement("'", "")
                .customReplacement("\"", "")
                .customReplacement("&", "and")
                .customReplacement("@", "at")
                .customReplacement("+", "plus")
                .build();

        // Русский slugifier (с транслитерацией)
        russianSlugify = Slugify.builder()
                .transliterator(true)
                .lowerCase(true)
                .underscoreSeparator(false)
                .customReplacement("'", "")
                .customReplacement("\"", "")
                .customReplacement("&", "and")
                .customReplacement("@", "at")
                .customReplacement("+", "plus")
                .build();
    }

    @Cacheable(value = "slugs", key = "#text")
    public String generateSlug(String text) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("Text cannot be null or blank");
        }

        LocaleConfig.AppLocale locale = detectLanguage(text);

        return generateSlug(text, locale);
    }

    public String generateSlug(String text, LocaleConfig.AppLocale locale) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("Text cannot be null or empty");
        }

        String slug;

        switch (locale) {
            case RU:
                slug = russianSlugify.slugify(text);
                break;
            case EN:
            default:
                slug = englishSlugify.slugify(text);
                break;
        }

        slug = normalizeSlug(slug);

        return slug;
    }

    public String generateUniqueSlug(String text, Function<String, Boolean> existenceChecker) {
        String baseSlug = generateSlug(text);
        String slug = baseSlug;
        int counter = 1;

        while (existenceChecker.apply(slug)) {
            slug = baseSlug + "-" + counter;
            counter++;

            // Защита от бесконечного цикла
            if (counter > 100) {
                slug = baseSlug + "-" + System.currentTimeMillis();
                break;
            }
        }

        return slug;
    }

    private LocaleConfig.AppLocale detectLanguage(String text) {
        if (languageDetector == null) {
            return containsCyrillic(text) ? LocaleConfig.AppLocale.RU : LocaleConfig.AppLocale.EN;
        }

        try {
            com.google.common.base.Optional<LdLocale> detected = languageDetector.detect(text);
            if (detected.isPresent()) {
                String lang = detected.get().getLanguage();
                if ("ru".equals(lang)) {
                    return LocaleConfig.AppLocale.RU;
                }
            }
        } catch (Exception e) {
            log.debug("Language detection failed for text: {}", text.substring(0, Math.min(50, text.length())), e);
        }

        return LocaleConfig.AppLocale.EN;
    }

    private boolean containsCyrillic(String text) {
        return text.chars().anyMatch(ch ->
                Character.UnicodeBlock.of(ch) == Character.UnicodeBlock.CYRILLIC
        );
    }

    private String normalizeSlug(String slug) {
        if (slug == null) return null;

        slug = slug.replaceAll("-+", "-");

        slug = slug.replaceAll("^-|-$", "");

        if (slug.length() > 100) {
            slug = slug.substring(0, 100);
            if (slug.endsWith("-")) {
                slug = slug.substring(0, slug.length() - 1);
            }
        }

        return slug;
    }

    public boolean isValidSlug(String slug) {
        if (slug == null || slug.isBlank()) return false;
        return slug.matches("^[a-z0-9]+(?:-[a-z0-9]+)*$");
    }
}
