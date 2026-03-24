package driver;

import org.openqa.selenium.WebDriver;

// Contract for all browser implementations
// Add new browser: implement this, register in DriverFactory — zero other changes
public interface BrowserDriver {
    WebDriver create();
}