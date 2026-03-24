package driver;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

public class FirefoxBrowserDriver implements BrowserDriver {

    @Override
    public WebDriver create() {
        WebDriverManager.firefoxdriver().setup();
        return new FirefoxDriver();
    }
}