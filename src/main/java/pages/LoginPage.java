package pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.testng.Assert;
import utils.ConfigManager;

public class LoginPage extends BasePage {

    // Locators
    // All declared here — respective Page wise

    @FindBy(id = "user-name")
    private WebElement usernameField;

    @FindBy(id = "password")
    private WebElement passwordField;

    @FindBy(id = "login-button")
    private WebElement loginButton;

    @FindBy(css = "[data-test='error']")
    private WebElement errorMessage;

    // Constructor
    public LoginPage() {
        super(); // calls BasePage → PageFactory.initElements
    }

    public void navigateToLoginPage() {
        navigateTo(ConfigManager.getInstance().getUiBaseUrl());
    }

    public void login(String username, String password) {
        type(usernameField, username);
        type(passwordField, password);
        click(loginButton);
    }

    public void verifyErrorMessage(String expectedMessage) {
        Assert.assertEquals(
                getText(errorMessage),
                expectedMessage,
                "Error message mismatch on login page");
    }


    // Verifies the locked-out error message shown for locked_out_user
    public void verifyLockedError() {
        Assert.assertEquals(
                getText(errorMessage),
                "Epic sadface: Sorry, this user has been locked out.",
                "Locked-out error message mismatch");
    }
}