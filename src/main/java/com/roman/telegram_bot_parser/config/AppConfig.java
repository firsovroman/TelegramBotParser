package com.roman.telegram_bot_parser.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;


@Configuration
@EnableScheduling
@PropertySource(value = "classpath:extra.def.properties", encoding = "UTF-8", ignoreResourceNotFound = true)
@PropertySource(value = "classpath:version.properties", encoding = "UTF-8")
public class AppConfig {

    private static final Logger logger = LoggerFactory.getLogger(AppConfig.class);

    public static final String APP_ID = "bot";
    public static final String APP_TITLE = "bot";

    private final String projectVersion;
    private final String buildProfile;
    private final String title;

    Environment environment;

    @Autowired
    public AppConfig(Environment environment) {

        this.environment = environment;

        logger.info("Application starting: {} ({})", APP_ID, APP_TITLE);

        this.buildProfile = environment.getProperty("build_profile");
        logger.info("build_profile: {}", buildProfile);

        this.projectVersion = environment.getProperty("project_version");
        logger.info("project_version: {}", projectVersion);

        this.title = String.format("%s %s (buildProfile:%s)",
                APP_TITLE, projectVersion, buildProfile);
    }

    public String getTitle() {
        return title;
    }

    @Bean
    public Environment configPropertyReader() {
        return this.environment;
    }

    @Bean("buildProfile")
    public String getBuildProfile() {
        return this.buildProfile;
    }


}
