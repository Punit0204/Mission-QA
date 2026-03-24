package pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.testng.Assert;

import java.util.List;

public class CartPage extends BasePage {

    // Locators

    @FindBy(css = ".cart_quantity")
    private List<WebElement> itemQuantities;

    @FindBy(css = ".cart_item_label .inventory_item_name")
    private List<WebElement> cartItemNames;

    @FindBy(id = "checkout")
    private WebElement checkoutButton;

    // Constructor

    public CartPage() {
        super();
    }

    public void verifyOnCartPage() {
        wait.untilUrlContains("cart");
        Assert.assertTrue(
                getCurrentUrl().contains("cart"),
                "Expected cart page but got: " + getCurrentUrl());
    }

    // Verifies every item in cart has the expected quantity
    public void verifyAllItemsHaveQuantity(int expectedQty) {
        Assert.assertFalse(
                itemQuantities.isEmpty(),
                "No items found in cart");
        for (WebElement qty : itemQuantities) {
            Assert.assertEquals(
                    Integer.parseInt(getText(qty)),
                    expectedQty,
                    "Cart item quantity mismatch");
        }
    }

    // Verifies a specific item is NOT in the cart
    public void verifyItemNotPresent(String itemName) {
        boolean found = cartItemNames.stream()
                .anyMatch(e -> getText(e)
                        .equalsIgnoreCase(itemName));
        Assert.assertFalse(
                found,
                "Item should not be in cart but was found: "
                        + itemName);
    }

    public void clickCheckout() {
        click(checkoutButton);
    }
}