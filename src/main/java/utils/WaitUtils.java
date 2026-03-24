package utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class WaitUtils {

    private final WebDriverWait wait;

    // timeout comes from config — not hardcoded
    public WaitUtils(WebDriver driver) {
        this.wait = new WebDriverWait(driver,
                Duration.ofSeconds(
                        ConfigManager.getInstance().getExplicitWait()));
    }

    // wait until element is visible in DOM and on screen
    public WebElement untilVisible(WebElement element) {
        return wait.until(
                ExpectedConditions.visibilityOf(element));
    }

    // wait until element is visible and can be clicked
    public WebElement untilClickable(WebElement element) {
        return wait.until(
                ExpectedConditions.elementToBeClickable(element));
    }

    // wait until URL contains a specific path segment
    public boolean untilUrlContains(String fragment) {
        return wait.until(
                ExpectedConditions.urlContains(fragment));
    }
}