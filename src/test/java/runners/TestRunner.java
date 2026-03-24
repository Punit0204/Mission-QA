// src/test/java/runners/TestRunner.java
package runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;

// TestRunner — runs both UI and  API scenarios.

@CucumberOptions(

        // scans api and ui feature folder
        features = "src/test/resources/features",
        glue = {
                "hooks",
                "steps.ui",
                "steps.api"
        },

        // reporting plugins
        plugin = {
                "pretty",
                "html:target/cucumber-reports/report.html",
                "json:target/cucumber-reports/report.json",
                "rerun:target/cucumber-reports/rerun.txt"
        },

        // clean console output
        monochrome = true,
        tags = " "

)
public class TestRunner extends AbstractTestNGCucumberTests {

        // parallel = false → sequential execution (safe default)
        // parallel = true  → each scenario gets own ThreadLocal driver
        @Override
        @DataProvider(parallel = false)
        public Object[][] scenarios() {
                return super.scenarios();
        }
}