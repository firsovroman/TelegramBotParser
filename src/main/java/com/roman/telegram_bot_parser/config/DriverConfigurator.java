package com.roman.telegram_bot_parser.config;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class DriverConfigurator {

    private final WebDriver chromeDriver;

    @Autowired
    public DriverConfigurator(String buildProfile) {
        if(buildProfile.equals("dev") ) {
            System.setProperty("webdriver.chrome.driver", "build_profiles/"+ buildProfile + "/chromedriver.exe");
        }
        if(buildProfile.equals("prod")) {
            System.setProperty("webdriver.chrome.driver", "chromedriver");
        }

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-infobars");
        options.addArguments("--start-maximized");

        WebDriver browser = new ChromeDriver(options);
        browser.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

        this.chromeDriver = browser;
    }

    public WebDriver getChromeDriver() {
        return chromeDriver;
    }
}
