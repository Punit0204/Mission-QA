package steps.ui;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import pages.CartPage;
import pages.CheckoutPage;
import pages.LoginPage;
import pages.ProductsPage;

import java.util.List;

/**
 * UISteps — orchestrates UI scenarios via page objects only.
 */
public class UISteps {

    // Page objects — driver already initialised by Hooks @Before
    private final LoginPage    loginPage    = new LoginPage();
    private final ProductsPage productsPage = new ProductsPage();
    private final CartPage     cartPage     = new CartPage();
    private final CheckoutPage checkoutPage = new CheckoutPage();

    // LOGIN

    @Given("I am on the SauceDemo login page")
    public void iAmOnTheSauceDemoLoginPage() {
        loginPage.navigateToLoginPage();
    }

    @When("I login with username {string} and password {string}")
    public void iLoginWithUsernameAndPassword(String username, String password) {
        loginPage.login(username, password);
    }

    // LOGIN ASSERTIONS

    @Then("I should be on the products page")
    public void iShouldBeOnTheProductsPage() {
        productsPage.verifyOnProductsPage();
    }

    @Then("I should see the error message {string}")
    public void iShouldSeeLoginErrorMessage(String expectedMessage) {
        loginPage.verifyErrorMessage(expectedMessage);
    }

    // PRODUCTS PAGE — add / remove / badge

    @When("I add the following items to the basket")
    @Given("I have added the following items to the basket")
    public void iAddItemsToTheBasket(DataTable dataTable) {
        productsPage.addItemsToCart(dataTable.asList());
    }

    @Then("the shopping cart badge should show {int}")
    public void theShoppingCartBadgeShouldShow(int expectedCount) {
        productsPage.verifyCartBadgeCount(expectedCount);
    }

    @When("I remove {string} from the basket")
    public void iRemoveFromTheBasket(String itemName) {
        productsPage.removeItemFromCart(itemName);
    }

    // CART PAGE

    @When("I click on the shopping cart icon")
    public void iClickOnTheShoppingCartIcon() {
        productsPage.openCart();
    }

    @Then("I should be on the cart page")
    public void iShouldBeOnTheCartPage() {
        cartPage.verifyOnCartPage();
    }

    @Then("each item in the cart should have quantity {int}")
    public void eachItemInTheCartShouldHaveQuantity(int qty) {
        cartPage.verifyAllItemsHaveQuantity(qty);
    }

    @Then("{string} should not be present in the cart")
    public void itemShouldNotBePresentInTheCart(String itemName) {
        cartPage.verifyItemNotPresent(itemName);
    }

    // CHECKOUT

    @When("I click on the CHECKOUT button")
    public void iClickOnTheCheckoutButton() {
        cartPage.clickCheckout();
    }

    @Then("I should be on the checkout information page")
    public void iShouldBeOnTheCheckoutInformationPage() {
        checkoutPage.verifyOnCheckoutInfoPage();
    }

    @When("I enter checkout details")
    public void iEnterCheckoutDetails(DataTable dataTable) {
        List<List<String>> rows    = dataTable.asLists();
        List<String>       headers = rows.get(0);
        List<String>       values  = rows.get(1);
        checkoutPage.fillCheckoutInfo(
                values.get(headers.indexOf("first_name")),
                values.get(headers.indexOf("last_name")),
                values.get(headers.indexOf("zip_code")));
    }

    @When("I click on the CONTINUE button")
    public void iClickOnTheContinueButton() {
        checkoutPage.clickContinue();
    }

    @Then("I should be on the checkout overview page")
    public void iShouldBeOnTheCheckoutOverviewPage() {
        checkoutPage.verifyOnCheckoutOverviewPage();
    }

    @When("I submit the checkout form without entering any details")
    public void iSubmitTheCheckoutFormWithoutEnteringAnyDetails() {
        checkoutPage.clickContinue();
    }

    // PRICE VALIDATION

    @Given("I proceed to checkout with the following items")
    public void iProceedToCheckoutWithTheFollowingItems(DataTable dataTable) {
        productsPage.addItemsToCart(dataTable.asList());
        productsPage.openCart();
        cartPage.clickCheckout();
        checkoutPage.fillCheckoutInfo("John", "Doe", "EC1A 9JU");
        checkoutPage.clickContinue();
    }

    @Then("the item total should equal the sum of all selected item prices")
    public void theItemTotalShouldEqualTheSumOfAllSelectedItemPrices() {
        checkoutPage.verifyItemTotalMatchesSum();
    }

    @Then("a tax of {int} percent should be applied to the item total")
    public void aTaxOfPercentShouldBeAppliedToTheItemTotal(int taxPercent) {
        checkoutPage.verifyTaxAmount(taxPercent);
    }

    @Then("the order total should equal the item total plus tax")
    public void theOrderTotalShouldEqualTheItemTotalPlusTax() {
        checkoutPage.verifyOrderTotal();
    }
}