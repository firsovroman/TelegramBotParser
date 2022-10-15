package com.roman.telegram_bot_parser.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("application.properties")
public class BotConfig {

    @Value("${bot.name}")
    String bothName;

    @Value("${bot.key}")
    String token;


    public BotConfig() {
    }

    public BotConfig(String bothName, String token) {
        this.bothName = bothName;
        this.token = token;
    }

    public String getBothName() {
        return bothName;
    }

    public void setBothName(String bothName) {
        this.bothName = bothName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "BotConfig{" +
                "bothName='" + bothName + '\'' +
                ", token='" + token + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BotConfig botConfig = (BotConfig) o;

        if (getBothName() != null ? !getBothName().equals(botConfig.getBothName()) : botConfig.getBothName() != null)
            return false;
        return getToken() != null ? getToken().equals(botConfig.getToken()) : botConfig.getToken() == null;
    }

    @Override
    public int hashCode() {
        int result = getBothName() != null ? getBothName().hashCode() : 0;
        result = 31 * result + (getToken() != null ? getToken().hashCode() : 0);
        return result;
    }
}
