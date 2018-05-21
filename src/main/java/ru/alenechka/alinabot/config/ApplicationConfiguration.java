package ru.alenechka.alinabot.config;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.telegram.telegrambots.ApiContext;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import ru.alenechka.alinabot.security.AnonymousChatTokenSecurityFilter;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.StringTokenizer;

@Configuration
public class ApplicationConfiguration {

    @Bean
    public AnonymousAuthenticationFilter anonymousAuthenticationFilter() {
        return new AnonymousChatTokenSecurityFilter("CHAT_TOKEN_FILTER");
    }

    @Bean
    public DefaultBotOptions proxyBotOptions(
        @Value("${http.proxy.url}") String proxyUrl) throws MalformedURLException {
        DefaultBotOptions botOptions = ApiContext.getInstance(DefaultBotOptions.class);

        if (!StringUtils.isEmpty(proxyUrl)) {
            URL url = new URL(proxyUrl);
            String proxyHost = url.getHost();
            int proxyPort = url.getPort();
            HttpHost httpHost = new HttpHost(proxyHost, proxyPort);

            String userInfo = url.getUserInfo();
            boolean isAuth = !StringUtils.isEmpty(userInfo);
            if (isAuth) {
                StringTokenizer tokenizer = new StringTokenizer(userInfo, ":");
                String username = tokenizer.nextToken();
                String password = tokenizer.nextToken();

                CredentialsProvider credsProvider = new BasicCredentialsProvider();
                credsProvider.setCredentials(
                        new AuthScope(proxyHost, proxyPort),
                        new UsernamePasswordCredentials(username, password));
                botOptions.setCredentialsProvider(credsProvider);

            }

            RequestConfig requestConfig = RequestConfig.custom()
                    .setProxy(httpHost)
                    .setAuthenticationEnabled(isAuth)
                    .build();

            botOptions.setRequestConfig(requestConfig);

            botOptions.setHttpProxy(httpHost);
        }
        return botOptions;
    }
}
