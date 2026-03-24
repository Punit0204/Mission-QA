package steps.api;

import api.ApiResponse;
import api.UserService;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.java.en.And;

import java.util.List;

public class APISteps {

    // UserService owns all HTTP calls and assertions — steps only orchestrate
    private final UserService userService = new UserService();

    // Shared state — set by @When, read by @Then
    private ApiResponse   response;
    private List<Integer> allUserIds;
    private int           totalUserCount;

    // ── LIST USERS ────────────────────────────────────────────

    // Empty by design — base URI configured in ApiClient, no connection to establish
    @Given("I am connected to the Reqres API")
    public void iAmConnectedToTheReqresAPI() {}

    @When("I request the list of users on page {int}")
    public void iRequestTheListOfUsersOnPage(int page) {
        response       = userService.getUsers(page);
        totalUserCount = userService.extractTotal(response); // ← moved to service
    }

    @And("I retrieve users from all available pages")
    public void iRetrieveUsersFromAllAvailablePages() {
        allUserIds = userService.getAllUserIds();
    }

    @Then("the total user count should equal the number of unique user IDs")
    public void theTotalUserCountShouldEqualTheNumberOfUniqueUserIDs() {
        userService.verifyTotalCountMatchesUniqueIds(totalUserCount, allUserIds);
    }

    // ── SINGLE USER ───────────────────────────────────────────

    @When("I request details for user with ID {int}")
    public void iRequestDetailsForUserWithID(int userId) {
        response = userService.getUserById(userId);
    }

    @Then("the response should contain the following user details")
    public void theResponseShouldContainTheFollowingUserDetails(DataTable dataTable) {
        List<List<String>> rows    = dataTable.asLists();
        List<String>       headers = rows.get(0);
        List<String>       values  = rows.get(1);
        userService.verifyUserDetails(response, headers, values);
    }

    // ── CREATE USER ───────────────────────────────────────────

    @When("I create a user with name {string} and job {string}")
    public void iCreateAUserWithNameAndJob(String name, String job) {
        response = userService.createUser(name, job);
    }

    @Then("the response should contain name {string} and job {string}")
    public void theResponseShouldContainNameAndJob(String name, String job) {
        userService.verifyCreatedUser(response, name, job);
    }

    @Then("the response should contain a non-empty id and createdAt")
    public void theResponseShouldContainANonEmptyIdAndCreatedAt() {
        userService.verifyNonEmptyFields(response, List.of("id", "createdAt"));
    }

    // ── UPDATE USER ───────────────────────────────────────────

    // PUT /users/{id} — verifies updatedAt is returned
    @When("I update user with ID {int} with name {string} and job {string}")
    public void iUpdateUserWithIdWithNameAndJob(int userId, String name, String job) {
        response = userService.updateUser(userId, name, job);
    }

    @Then("the response should contain a non-empty updatedAt")
    public void theResponseShouldContainNonEmptyUpdatedAt() {
        userService.verifyNonEmptyFields(response, List.of("updatedAt"));
    }

    // ── DELETE USER ───────────────────────────────────────────

    // 204 No Content — response body must be empty, no JSON returned
    @When("I delete user with ID {int}")
    public void iDeleteUserWithId(int userId) {
        response = userService.deleteUser(userId);
    }

    // ── LOGIN ─────────────────────────────────────────────────

    @When("I login to the API with email {string} and password {string}")
    public void iLoginToTheAPIWithEmailAndPassword(String email, String password) {
        response = userService.login(email, password);
    }

    // Empty password simulates missing password — triggers 400 from API
    @When("I login to the API with email {string} and empty password")
    public void iLoginToTheAPIWithEmailAndEmptyPassword(String email) {
        response = userService.login(email, "");
    }

    @Then("the response should contain a non-empty authentication token")
    public void theResponseShouldContainANonEmptyAuthenticationToken() {
        userService.verifyNonEmptyFields(response, List.of("token"));
    }

    @Then("the response should contain error message {string}")
    public void theResponseShouldContainErrorMessage(String expectedError) {
        userService.verifyErrorMessage(response, expectedError);
    }

    // ── DELAYED RESPONSE ──────────────────────────────────────

    // Validates API still returns valid data under artificial server delay
    @When("I request the user list with a response delay of {int} seconds")
    public void iRequestTheUserListWithAResponseDelayOfSeconds(int delaySeconds) {
        response = userService.getUsersWithDelay(delaySeconds);
    }

    @Then("every user in the response should have a unique ID")
    public void everyUserInTheResponseShouldHaveAUniqueID() {
        userService.verifyUniqueUserIds(response);
    }

    @Then("every user should have non-empty fields")
    public void everyUserShouldHaveNonEmptyFields(DataTable dataTable) {
        userService.verifyAllUsersHaveNonEmptyFields(response, dataTable.asList());
    }

    // ── SHARED ────────────────────────────────────────────────

    // Single binding reused across all 11 scenarios
    @Then("the response status code should be {int}")
    public void theResponseStatusCodeShouldBe(int expectedCode) {
        userService.verifyStatusCode(response, expectedCode);
    }

    @Then("the response should contain the following fields")
    public void theResponseShouldContainTheFollowingFields(DataTable dataTable) {
        userService.verifyResponseContainsFields(response, dataTable.asList());
    }
}