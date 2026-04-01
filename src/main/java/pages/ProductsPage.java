package pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.testng.Assert;

import java.util.List;
import java.util.stream.Collectors;

public class ProductsPage extends BasePage {

    // Locators
    // All declared here — respective Page wise
    @FindBy(css = ".shopping_cart_badge")
    private WebElement cartBadge;

    @FindBy(css = ".shopping_cart_link")
    private WebElement cartIcon;

    @FindBy(css = ".inventory_item_name")
    private List<WebElement> itemNames;

    @FindBy(css = ".inventory_item_price")
    private List<WebElement> itemPrices;


    // Constructor

    public ProductsPage() {
        super();
    }

    public void verifyOnProductsPage() {
        wait.untilUrlContains("inventory");
        Assert.assertTrue(
                getCurrentUrl().contains("inventory"),
                "Expected inventory page but got: " + getCurrentUrl());
    }

    // Adds each item in the list by finding its Add to Cart button
    public void addItemsToCart(List<String> itemNames) {
        for (String itemName : itemNames) {
            // build data-test attribute from item name
            String dataTest = "add-to-cart-"
                    + itemName.toLowerCase()
                    .replace(" ", "-");
            WebElement addButton = driver.findElement(
                    org.openqa.selenium.By.cssSelector(
                            "[data-test='" + dataTest + "']"));
            click(addButton);
        }
    }

    public void verifyCartBadgeCount(int expected) {
        wait.untilVisible(cartBadge);
        int actual = Integer.parseInt(getText(cartBadge));
        Assert.assertEquals(
                actual,
                expected,
                "Cart badge count mismatch");
    }

    // Removes item from cart using the Remove button on products page
    public void removeItemFromCart(String itemName) {
        String dataTest = "remove-"
                + itemName.toLowerCase()
                .replace(" ", "-");
        WebElement removeButton = driver.findElement(
                org.openqa.selenium.By.cssSelector(
                        "[data-test='" + dataTest + "']"));
        click(removeButton);
    }

    public void openCart() {
        click(cartIcon);
        wait.untilUrlContains("cart");
    }
}