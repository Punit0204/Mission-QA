package utils;

import driver.DriverFactory;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ScreenshotUtil {

    public static String capture(String scenarioName) {
        try {
            String timestamp = LocalDateTime.now()
                    .format(DateTimeFormatter
                            .ofPattern("yyyyMMdd_HHmmss"));

            String fileName = scenarioName
                    .replaceAll("[^a-zA-Z0-9]", "_")
                    + "_" + timestamp + ".png";

            String dir = ConfigManager.getInstance()
                    .getScreenshotDir();

            File directory = new File(dir);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            File src = ((TakesScreenshot) DriverFactory
                    .getDriver())
                    .getScreenshotAs(OutputType.FILE);

            String fullPath = dir + fileName;  // Stores Full Path
            FileUtils.copyFile(src, new File(fullPath));

            System.out.println("Screenshot saved: " + fullPath);

            return fullPath;

        } catch (IOException e) {
            System.err.println(
                    "Screenshot failed: " + e.getMessage());
            return "screenshot-failed";
        }
    }
}