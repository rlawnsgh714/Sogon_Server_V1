package com.stuent.dpply.common.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties("app")
public class AppProperties {
    private String DAUTH_SERVER;
    private String OPEN_API_SERVER;
    private String CLIENT_ID;
    private String CLIENT_SECRET;
    private String ACCESS_SECRET;
    private String REFRESH_SECRET;
    private String REDIRECT_URL;
    private String NO_IMAGE_URL;
}