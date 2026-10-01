package ru.sshibko.backend_seblog.dto.security;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class HtmlSanitizer {

    private final Safelist safelist;

    public HtmlSanitizer() {
        this.safelist = Safelist.relaxed()
                .addTags("figure", "figcaption")
                .addTags("iframe")
                //for youTube
                .addAttributes(
                        "iframe", "src", "width", "height", "frameBorder", "allow", "allowFullscreen")
                .addProtocols("iframe", "src", "https")
                .addEnforcedAttribute("iframe", "sandbox", "allow-scripts allow-same-origin")
                //for hiper links
                .addAttributes("a", "href", "target", "rel")
                .addProtocols("a", "href", "http", "https", "mailto")
                .addEnforcedAttribute("a", "rel", "noOpener noReferrer nofollow")
                //for pics
                .addAttributes("img", "src", "alt", "title", "width", "height")
                .addProtocols("img", "src", "http", "https", "data");
    }

    public String sanitize(String html) {
        if (html == null || html.isBlank()) {
            return html;
        }
        try {
            return Jsoup.clean(html, safelist);
        } catch (Exception e) {
            log.error("Ошибка при санитизации HTML: {}", e.getMessage());
            return "";
        }
    }
}
