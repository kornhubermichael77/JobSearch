package info.kornhuber.jobsearch.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.security.remember-me")
public record RememberMeProperties(
        String key,
        boolean secureCookie,
        int validitySeconds
) {}
