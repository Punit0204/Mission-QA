package pages;

import driver.DriverFactory;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;
import utils.WaitUtils;

/**
 * BasePage — abstract base for all page objects.
 * Driver injected via constructor — decoupled from DriverFactory.
 */
public abstract class BasePage {

    protected final WebDriver driver;
    protected final WaitUtils wait;

    // no-arg constructor — used by all page objects in production
    // resolves driver from DriverFactory — single point of change
    protected BasePage() {
        this(DriverFactory.getDriver());
    }

    // injectable constructor — decoupled, testable, extensible
    // pass any WebDriver instance — RemoteWebDriver, mock, custom
    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WaitUtils(driver);
        PageFactory.initElements(driver, this);
    }

    // ── Reusable actions ──────────────────────────────────────

    protected void click(WebElement element) {
        wait.untilClickable(element);
        element.click();
    }

    protected void type(WebElement element, String text) {
        wait.untilVisible(element);
        element.clear();
        element.sendKeys(text);
    }

    protected String getText(WebElement element) {
        wait.untilVisible(element);
        return element.getText().trim();
    }

    protected void navigateTo(String url) {
        driver.get(url);
    }

    protected String getCurrentUrl() {
        return driver.getCurrentUrl();
    }
}