package utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigManager {

    private static ConfigManager instance;
    private final Properties properties = new Properties();

    private ConfigManager() {
        try (InputStream input = getClass()
                .getClassLoader()
                .getResourceAsStream("config.properties")) {

            if (input == null) {
                throw new RuntimeException(
                        "config.properties not found in src/test/resources");
            }
            properties.load(input);

        } catch (IOException e) {
            throw new RuntimeException(
                    "Failed to load config.properties — " + e.getMessage(), e);
        }
    }
    // For Headless Run
    public boolean isHeadless() {
        return Boolean.parseBoolean(get("headless"));
    }

    // single access point — lazy init, thread-safe enough for test scope
    public static ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }

    // generic getter — throws if key missing, never returns null silently
    public String get(String key) {
        String value = properties.getProperty(key);
        if (value == null || value.trim().isEmpty()) {
            throw new RuntimeException(
                    "Missing or empty config key: [" + key + "]");
        }
        return value.trim();
    }

    // Some Convienvice Method Used in Framewrok
    public String getBrowser()           { return get("browser"); }
    public String getUiBaseUrl()         { return get("ui.base.url"); }
    public String getApiBaseUrl()        { return get("api.base.url"); }
    public int    getImplicitWait()      { return Integer.parseInt(get("implicit.wait")); }
    public int    getExplicitWait()      { return Integer.parseInt(get("explicit.wait")); }
    public int    getApiConnectTimeout() { return Integer.parseInt(get("api.connect.timeout")); }
    public String getScreenshotDir()     { return get("screenshot.dir"); }
    public String getLogDir() {return get("log.dir"); }
    public String getApiKey() {return get("api.key"); }
}