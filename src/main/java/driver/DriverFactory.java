package driver;

import org.openqa.selenium.WebDriver;
import utils.ConfigManager;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * DriverFactory — manages WebDriver lifecycle.
 *
 * ThreadLocal ensures parallel execution safety — one driver per thread.
 * BrowserDriver registry decouples browser creation from this class.
 * To add Opera, Brave, or any browser: implement BrowserDriver, add one line to BROWSERS.
 * This class never needs to change for new browser support.
 */
public class DriverFactory {

    // one driver per thread — parallel execution safe
    private static final ThreadLocal<WebDriver> driverThread
            = new ThreadLocal<>();

    // registry — add new browser here, nothing else changes
    private static final Map<String, BrowserDriver> BROWSERS
            = new HashMap<>();

    static {
        BROWSERS.put("chrome",   new ChromeBrowserDriver());
        BROWSERS.put("firefox",  new FirefoxBrowserDriver());
        BROWSERS.put("edge",     new EdgeBrowserDriver());
    }

    private DriverFactory() {}

    public static WebDriver getDriver() {
        return driverThread.get();
    }

    public static void initDriver() {
        String browser = ConfigManager.getInstance()
                .getBrowser().toLowerCase();

        BrowserDriver browserDriver = BROWSERS.get(browser);

        if (browserDriver == null) {
            throw new IllegalArgumentException(
                    "Unsupported browser: [" + browser + "]. "
                            + "Valid values: " + BROWSERS.keySet());
        }

        WebDriver driver = browserDriver.create();

        // common setup applied to every browser
        driver.manage().window().maximize();
        driver.manage().deleteAllCookies();
        driver.manage().timeouts()
                .implicitlyWait(Duration.ofSeconds(
                        ConfigManager.getInstance().getImplicitWait()));
        driver.manage().timeouts()
                .pageLoadTimeout(Duration.ofSeconds(30));

        driverThread.set(driver);
    }

    public static void quitDriver() {
        WebDriver driver = driverThread.get();
        if (driver != null) {
            driver.quit();
            driverThread.remove(); // prevents memory leak in thread pools
        }
    }
}