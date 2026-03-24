package pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.testng.Assert;

import java.util.List;

public class CheckoutPage extends BasePage {

    // Locators
    // All declared here — respective Page wise
    @FindBy(id = "first-name")
    private WebElement firstNameField;

    @FindBy(id = "last-name")
    private WebElement lastNameField;

    @FindBy(id = "postal-code")
    private WebElement postalCodeField;

    @FindBy(id = "continue")
    private WebElement continueButton;

    @FindBy(css = "[data-test='error']")
    private WebElement errorMessage;

    // Checkout Step Two — order summary
    @FindBy(css = ".summary_subtotal_label")
    private WebElement itemTotalLabel;

    @FindBy(css = ".summary_tax_label")
    private WebElement taxLabel;

    @FindBy(css = ".summary_total_label")
    private WebElement orderTotalLabel;

    @FindBy(css = ".inventory_item_price")
    private List<WebElement> summaryItemPrices;

    // Constructor
    public CheckoutPage() {
        super();
    }

    public void verifyOnCheckoutInfoPage() {
        wait.untilUrlContains("checkout-step-one");
        Assert.assertTrue(
                getCurrentUrl().contains("checkout-step-one"),
                "Expected checkout info page but got: "
                        + getCurrentUrl());
    }

    public void fillCheckoutInfo(String firstName,
                                 String lastName,
                                 String zipCode) {
        type(firstNameField, firstName);
        type(lastNameField, lastName);
        type(postalCodeField, zipCode);
    }

    public void clickContinue() {
        click(continueButton);
    }

    public void verifyOnCheckoutOverviewPage() {
        wait.untilUrlContains("checkout-step-two");
        Assert.assertTrue(
                getCurrentUrl().contains("checkout-step-two"),
                "Expected checkout overview page but got: "
                        + getCurrentUrl());
    }

    // Verify error message on checkout form
    public void verifyErrorMessage(String expectedMessage) {
        Assert.assertEquals(
                getText(errorMessage),
                expectedMessage,
                "Checkout error message mismatch");
    }

    // Price validation
    // Item total = sum of all individual product prices on summary page
    public void verifyItemTotalMatchesSum() {
        double displayedTotal = extractPrice(itemTotalLabel);
        double calculatedSum  = summaryItemPrices.stream()
                .mapToDouble(e -> extractPrice(e))
                .sum();

        // round to 2 decimal places to avoid floating point drift
        Assert.assertEquals(
                Math.round(displayedTotal  * 100.0) / 100.0,
                Math.round(calculatedSum   * 100.0) / 100.0,
                "Item total does not match sum of individual prices. "
                        + "Displayed: $" + displayedTotal
                        + " | Calculated: $" + calculatedSum);
    }

    // Tax = 8% of item total — validated to 2 decimal places
    public void verifyTaxAmount(int taxPercent) {
        double itemTotal     = extractPrice(itemTotalLabel);
        double displayedTax  = extractPrice(taxLabel);
        double expectedTax   = Math.round(
                itemTotal * taxPercent / 100.0 * 100.0) / 100.0;
        double actualTax     = Math.round(
                displayedTax * 100.0) / 100.0;

        Assert.assertEquals(
                actualTax,
                expectedTax,
                "Tax amount mismatch. Expected " + taxPercent
                        + "% of $" + itemTotal + " = $" + expectedTax
                        + " but got: $" + actualTax);
    }

    // Order total = item total + tax — full end-to-end price check
    public void verifyOrderTotal() {
        double itemTotal   = extractPrice(itemTotalLabel);
        double tax         = extractPrice(taxLabel);
        double orderTotal  = extractPrice(orderTotalLabel);
        double expected    = Math.round(
                (itemTotal + tax) * 100.0) / 100.0;
        double actual      = Math.round(
                orderTotal * 100.0) / 100.0;

        Assert.assertEquals(
                actual,
                expected,
                "Order total mismatch. "
                        + "Item total: $" + itemTotal
                        + " + Tax: $" + tax
                        + " = Expected: $" + expected
                        + " | Displayed: $" + actual);
    }

    // Private helpers
    // Extracts numeric price from label text
    private double extractPrice(WebElement element) {
        String text = getText(element);
        // remove everything except digits, dot, minus
        String numeric = text.replaceAll("[^0-9.]", "");
        return Double.parseDouble(numeric);
    }
}