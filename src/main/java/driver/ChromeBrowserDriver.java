package driver;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import utils.ConfigManager;
import java.util.HashMap;
import java.util.Map;

public class ChromeBrowserDriver implements BrowserDriver {

    @Override
    public WebDriver create() {
        WebDriverManager.chromedriver().setup();

        Map<String, Object> prefs = new HashMap<>();
        prefs.put("credentials_enable_service", false);
        prefs.put("profile.password_manager_enabled", false);
        prefs.put("profile.password_manager_leak_detection", false);
        prefs.put("profile.default_content_setting_values.notifications", 2);

        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("prefs", prefs);
        options.addArguments(
                "--disable-notifications",
                "--disable-popup-blocking",
                "--disable-infobars",
                "--disable-save-password-bubble",
                "--no-default-browser-check",
                "--disable-extensions"
        );
        options.setExperimentalOption(
                "excludeSwitches", new String[]{"enable-automation"});

        //  HEADLESS SUPPORT
        if (ConfigManager.getInstance().isHeadless()) {
            options.addArguments("--headless=new");
            options.addArguments("--window-size=1920,1080");
        }

        return new ChromeDriver(options);
    }
}