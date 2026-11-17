package ru.sshibko.backend_seblog.service;

import com.github.slugify.Slugify;
import com.optimaize.langdetect.LanguageDetector;

import com.optimaize.langdetect.i18n.LdLocale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import java.util.function.Function;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests for SlugService")
class SlugServiceTest {

    private SlugService slugService;

    @Mock
    LanguageDetector languageDetector;

    @Mock
    private Function<String, Boolean> existenceChecker;

    private Slugify englishSlugify;
    private Slugify russianSlugify;

    @BeforeEach
    void setUp() {
        slugService = new SlugService();

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

        ReflectionTestUtils.setField(slugService, "languageDetector", languageDetector);
        ReflectionTestUtils.setField(slugService, "englishSlugify", englishSlugify);
        ReflectionTestUtils.setField(slugService, "russianSlugify", russianSlugify);
    }

    @Nested
    @DisplayName("Generate Slug Method")
    class GenerateSlugTests {

        @Test
        @DisplayName("Should transliterate Russian text and normalize")
        void generateSlugShouldTransliterateRussianWhenLanguageIsRu() {
            //Arrange
            String text = "Привет, Мир! Это тест & проверка.";
            mockLanguageDetection("ru");

            //Act
            String result = slugService.generateSlug(text);

            //Assert
            assertThat(result).isEqualTo("privet-mir-eto-test-and-proverka");
        }

        @Test
        @DisplayName("Should keep English text as is and normalize")
        void generateSlugShouldKeepEnglishWhenLanguageIsEn() {
            //Arrange
            String text = "Hello World & Test @ Home";
            mockLanguageDetection("en");

            //Act
            String result = slugService.generateSlug(text);

            //Assert
            assertThat(result).isEqualTo("hello-world-and-test-at-home");
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when text is null or blank")
        void generateSlugShouldThrowExceptionWhenTextIsNullOrBlank() {
            //Act & Assert
            assertThatThrownBy(() -> slugService.generateSlug(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Text cannot be null or blank");

            assertThatThrownBy(() -> slugService.generateSlug("   "))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Text cannot be null or blank");
        }

        @Test
        @DisplayName("Should normalize slug: remove multiple hyphens and trim edges")
        void generateSlugShouldNormalizeMultipleHyphensAndTrim() {
            //Arrange
            mockLanguageDetection("en");

            //Act
            String result = slugService.generateSlug("a---b-");

            //Assert
            assertThat(result).isEqualTo("a-b");
        }

        @Test
        @DisplayName("Should truncate slug to 100 characters and remove trailing hyphen")
        void generateSlugShouldTruncateTo100CharsWhenTooLong() {
            //Arrange
            String longText = "a".repeat(150);
            mockLanguageDetection("ru");

            //Act
            String result = slugService.generateSlug(longText);

            //Assert
            assertThat(result).hasSizeLessThanOrEqualTo(100);
            assertThat(result).doesNotEndWith("-");
        }
    }

    @Nested
    @DisplayName("Generate Unique Slug Method")
    class GenerateUniqueSlugTests {

        @Test
        @DisplayName("Should return base slug if it does not exist")
        void generateUniqueSlugShouldReturnBaseSlugWhenNotExists() {
            //Arrange
            mockLanguageDetection("en");
            //doReturn(false).when(existenceChecker.apply("test-slug"));
            when(existenceChecker.apply("test-slug")).thenReturn(false);

            //Act
            String result = slugService.generateUniqueSlug("Test Slug", existenceChecker);

            //Assert
            assertThat(result).isEqualTo("test-slug");
            verify(existenceChecker, times(1)).apply("test-slug");
        }

        @Test
        @DisplayName("Should append counter when base slug exists")
        void generateUniqueSlugShouldAppendCounterWhenBaseExists() {
            //Arrange
            mockLanguageDetection("en");
            when(existenceChecker.apply("test")).thenReturn(true);
            when(existenceChecker.apply("test-1")).thenReturn(false);

            //Act
            String result = slugService.generateUniqueSlug("Test", existenceChecker);

            //Assert
            assertThat(result).isEqualTo("test-1");
            verify(existenceChecker).apply("test");
            verify(existenceChecker).apply("test-1");
        }

        @Test
        @DisplayName("Should fallback to timestamp when counter exceeds 100")
        void generateUniqueSlugShouldFallbackToTimestampWhenCounterExceeds100() {
            //Arrange
            mockLanguageDetection("en");
            when(existenceChecker.apply(anyString())).thenReturn(true);

            //Act
            String result = slugService.generateUniqueSlug("Test", existenceChecker);

            //Assert
            assertThat(result).startsWith("test-");
            String suffix = result.substring(result.lastIndexOf("-") + 1);
            assertThat(Long.parseLong(suffix)).isGreaterThan(100L);

            verify(existenceChecker, atMost(102)).apply(anyString());
        }

        @Test
        @DisplayName("Should use 'post' as fallback when generated slug is blank")
        void generateUniqueSlugShouldUsePostFallbackWhenSlugIsBlank() {
            //Arrange
            mockLanguageDetection("en");
            when(existenceChecker.apply("post")).thenReturn(false);

            //Act
            when(existenceChecker.apply("post")).thenReturn(false);

            String result = slugService.generateUniqueSlug("!!!", existenceChecker);

            //Assert
            assertThat(result).isEqualTo("post");
        }
    }

    @Nested
    @DisplayName("Is Valid Slug Method")
    class IsValidSlugTests {

        @ParameterizedTest
        @ValueSource(strings = {
                "valid-slug",
                "slug-with-numbers-123",
                "my-awesome-post-title",
                "a1b2c3",
                "single"
        })
        @DisplayName("Should return true for correctly formatted slugs")
        void isValidSlugShouldReturnTrueWhenSlugIsValid(String validSlug) {
            //Act & Assert
            assertThat(slugService.isValidSlug(validSlug)).isTrue();
        }


        @ParameterizedTest
        @ValueSource(strings = {
                "Invalid-Slug",         // Заглавные буквы
                "slug with spaces",      // Пробелы
                "slug_with_underscore",  // Недопустимые символы
                "slug!",                 // Спецсимволы
                //"ab",                    // Слишком короткий,убрал ибо реджекс разрешает
                "-starts-with-hyphen",   // Начинается с дефиса
                "ends-with-hyphen-"      // Заканчивается дефисом
        })
        @DisplayName("Should return false for incorrectly formatted slugs")
        void isValidSlug_ShouldReturnFalse_WhenSlugIsInvalid(String invalidSlug) {
            // Act & Assert
            assertThat(slugService.isValidSlug(invalidSlug)).isFalse();
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   "})
        @DisplayName("Should return false for null, empty or blank slugs")
        void isValidSlug_ShouldReturnFalse_WhenSlugIsNullOrBlank(String invalidSlug) {
            // Act & Assert
            assertThat(slugService.isValidSlug(invalidSlug)).isFalse();
        }
    }

    @Nested
    @DisplayName("Language Detection Fallback")
    class LanguageDetectionFallbackTests {

        @Test
        @DisplayName("Should fallback to Cyrillic check when languageDetector is null")
        void detectLanguageShouldFallbackToCyrillicCheckWhenDetectorIsNull() {
            //Arrange
            ReflectionTestUtils.setField(slugService, "languageDetector", null);

            //Act & Assert
            String ruResult = slugService.generateSlug("Привет мир");
            assertThat(ruResult).isEqualTo("privet-mir");

            String enResult = slugService.generateSlug("Hello world");
            assertThat(enResult).isEqualTo("hello-world");
        }

        @Test
        @DisplayName("Should fallback to English when detection throws exception")
        void detectLanguageShouldFallbackToEnglishWhenDetectionThrowsException() {
            //Arrange
            when(languageDetector.detect(anyString())).thenThrow(new RuntimeException("Detection failed"));

            //Act
            String result = slugService.generateSlug("Some text that causes exception");

            //Assert
            assertThat(result).isEqualTo("some-text-that-causes-exception");
        }
    }

    private void mockLanguageDetection(String langCode) {
        LdLocale locale = LdLocale.fromString(langCode);
        com.google.common.base.Optional<LdLocale> detected =
                com.google.common.base.Optional.of(locale);

        doReturn(detected).when(languageDetector).detect(anyString());
    }

}