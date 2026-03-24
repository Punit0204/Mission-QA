package driver;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;

public class EdgeBrowserDriver implements BrowserDriver {

    @Override
    public WebDriver create() {
        // Selenium Manager handles Edge — msedgedriver.azureedge.net blocked on corporate network
        return new EdgeDriver();
    }
}