package hooks;

import driver.DriverFactory;
import io.cucumber.java.Scenario;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.AfterAll;
import utils.LogUtil;
import utils.ScreenshotUtil;

/**
 * Hooks — Cucumber lifecycle manager.
 * Handles browser setup/teardown for @ui scenarios and
 * skips driver operations entirely for @api scenarios.
 */
public class Hooks {

    // records scenario start time — used to calculate duration in tearDown
    private long startTime;

    //Suite Level
    @BeforeAll
    public static void suiteSetUp() {
        LogUtil.init();
        LogUtil.log("Test Execution started");
    }

    // Runs once after the entire suite — writes summary and closes log file
    @AfterAll
    public static void suiteTearDown() {
        LogUtil.log("Test Execution finished");
        LogUtil.close();
    }

/**
    Scenario Level
    @ui  → launches browser and clears leftover localStorage/cookie state
    @api → no browser needed, skips all driver setup
 */
    @Before
    public void setUp(Scenario scenario) {
        startTime = System.currentTimeMillis();

        LogUtil.scenarioStart(
                scenario.getName(),
                scenario.getSourceTagNames().toString());

        if (scenario.getSourceTagNames().contains("@ui")) {
            DriverFactory.initDriver();

            // clear any leftover cart/session state from a previous run
            try {
                DriverFactory.getDriver().get(
                        utils.ConfigManager.getInstance().getUiBaseUrl());
                ((org.openqa.selenium.JavascriptExecutor)
                        DriverFactory.getDriver())
                        .executeScript(
                                "localStorage.clear(); sessionStorage.clear();");
                DriverFactory.getDriver().manage().deleteAllCookies();
            } catch (Exception ignored) {}

            System.out.println("[UI] Browser launched for: "
                    + scenario.getName());
        } else {
            System.out.println("[API] No browser needed for: "
                    + scenario.getName());
        }
    }

    // On failure - captures screenshot before closing browser
    // Always - quits browser (UI only), logs status and duration
    @After
    public void tearDown(Scenario scenario) {
        long duration = System.currentTimeMillis() - startTime;

        if (DriverFactory.getDriver() != null) {
            if (scenario.isFailed()) {
                String path = ScreenshotUtil
                        .capture(scenario.getName());
                System.out.println("[UI] Screenshot captured for failed scenario: "
                        + scenario.getName());
                LogUtil.screenshot(path);
            }

            // clear state after scenario completes
            try {
                DriverFactory.getDriver().manage().deleteAllCookies();
                ((org.openqa.selenium.JavascriptExecutor)
                        DriverFactory.getDriver())
                        .executeScript(
                                "localStorage.clear(); sessionStorage.clear();");
            } catch (Exception ignored) {}

            DriverFactory.quitDriver();
            System.out.println("[UI] Browser closed for: "
                    + scenario.getName());
        }

        System.out.println("Scenario: ["
                + scenario.getStatus() + "] "
                + scenario.getName());

        // logs scenario result and increments API/UI pass-fail counters
        LogUtil.scenarioEnd(
                scenario.getName(),
                scenario.getStatus().toString(),
                duration,
                scenario.getSourceTagNames().toString());
    }
}