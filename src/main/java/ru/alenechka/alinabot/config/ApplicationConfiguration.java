package ru.alenechka.alinabot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import ru.alenechka.alinabot.security.AnonymousChatTokenSecurityFilter;

@Configuration
public class ApplicationConfiguration {

    @Bean
    public AnonymousAuthenticationFilter anonymousAuthenticationFilter() {
        return new AnonymousChatTokenSecurityFilter("CHAT_TOKEN_FILTER");
    }

}
