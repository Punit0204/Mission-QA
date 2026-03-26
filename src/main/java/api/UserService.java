package api;

import org.testng.Assert;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static io.restassured.RestAssured.given;

public class UserService {

    // HTTP Methods 

    public ApiResponse getUsers(int page) {
        return new ApiResponse(
                given(ApiClient.getSpec())
                        .queryParam("page", page)
                        .when()
                        .get("/users")
                        .then()
                        .extract().response());
    }

    public List<Integer> getAllUserIds() {
        List<Integer> allIds = new ArrayList<>();
        int page = 1;
        int total;
        int perPage;

        do {
            ApiResponse response = getUsers(page);
            total   = response.getInt("total");
            perPage = response.getInt("per_page");

            List<Integer> pageIds = response.getList(
                    "data.id", Integer.class);
            allIds.addAll(pageIds);
            page++;

        } while ((page - 1) * perPage < total);

        return allIds;
    }

    public ApiResponse getUsersWithDelay(int delaySeconds) {
        return new ApiResponse(
                given(ApiClient.getSpec())
                        .queryParam("delay", delaySeconds)
                        .when()
                        .get("/users")
                        .then()
                        .extract().response());
    }

    public ApiResponse getUserById(int userId) {
        return new ApiResponse(
                given(ApiClient.getSpec())
                        .when()
                        .get("/users/" + userId)
                        .then()
                        .extract().response());
    }

    public ApiResponse createUser(String name, String job) {
        String body = String.format(
                "{\"name\": \"%s\", \"job\": \"%s\"}", name, job);
        return new ApiResponse(
                given(ApiClient.getSpec())
                        .body(body)
                        .when()
                        .post("/users")
                        .then()
                        .extract().response());
    }

    public ApiResponse updateUser(int userId, String name, String job) {
        String body = String.format(
                "{\"name\": \"%s\", \"job\": \"%s\"}", name, job);
        return new ApiResponse(
                given(ApiClient.getSpec())
                        .body(body)
                        .when()
                        .put("/users/" + userId)
                        .then()
                        .extract().response());
    }

    public ApiResponse deleteUser(int userId) {
        return new ApiResponse(
                given(ApiClient.getSpec())
                        .when()
                        .delete("/users/" + userId)
                        .then()
                        .extract().response());
    }

    public ApiResponse login(String email, String password) {
        String body = String.format(
                "{\"email\": \"%s\", \"password\": \"%s\"}",
                email, password);
        return new ApiResponse(
                given(ApiClient.getSpec())
                        .body(body)
                        .when()
                        .post("/login")
                        .then()
                        .extract().response());
    }

    // Assertion Helpers
    public void verifyStatusCode(ApiResponse response, int expectedCode) {
        Assert.assertEquals(
                response.getStatusCode(),
                expectedCode,
                "Status code mismatch. Response body: "
                        + response.getBodyAsString());
    }

    public void verifyResponseContainsFields(ApiResponse response,
                                             List<String> fields) {
        for (String field : fields) {
            Assert.assertNotNull(
                    response.get(field),
                    "Expected field missing: [" + field + "]");
        }
    }

    public void verifyNonEmptyFields(ApiResponse response,
                                     List<String> fields) {
        for (String field : fields) {
            String value = response.getString(field);
            Assert.assertNotNull(value,
                    "Field [" + field + "] is null");
            Assert.assertFalse(value.trim().isEmpty(),
                    "Field [" + field + "] is empty");
        }
    }

    public void verifyUserDetails(ApiResponse response,
                                  List<String> headers,
                                  List<String> values) {
        for (int i = 0; i < headers.size(); i++) {
            String field    = headers.get(i);
            String expected = values.get(i);
            String actual   = response.getString("data." + field);
            Assert.assertEquals(actual, expected,
                    "User field [" + field + "] mismatch");
        }
    }

    public void verifyCreatedUser(ApiResponse response,
                                  String expectedName,
                                  String expectedJob) {
        Assert.assertEquals(response.getString("name"),
                expectedName, "Created user name mismatch");
        Assert.assertEquals(response.getString("job"),
                expectedJob, "Created user job mismatch");
    }

    public void verifyTotalCountMatchesUniqueIds(int totalCount,
                                                 List<Integer> allIds) {
        Set<Integer> uniqueIds = new HashSet<>(allIds);
        Assert.assertEquals(uniqueIds.size(), totalCount,
                "Total declared: " + totalCount
                        + " | Unique IDs found: " + uniqueIds.size());
    }

    public void verifyUniqueUserIds(ApiResponse response) {
        List<Integer> ids = response.getList("data.id", Integer.class);
        Set<Integer> uniqueIds = new HashSet<>(ids);
        Assert.assertEquals(uniqueIds.size(), ids.size(),
                "Duplicate user IDs found in response");
    }

    public void verifyAllUsersHaveNonEmptyFields(ApiResponse response,
                                                 List<String> fields) {
        List<Object> users = response.getList("data");
        Assert.assertFalse(users.isEmpty(),
                "No users found in response data array");

        for (String field : fields) {
            List<String> values = response.getList(
                    "data." + field, String.class);
            for (int i = 0; i < values.size(); i++) {
                String value = values.get(i);
                Assert.assertNotNull(value,
                        "User [" + i + "] null [" + field + "]");
                Assert.assertFalse(value.trim().isEmpty(),
                        "User [" + i + "] empty [" + field + "]");
            }
        }
    }
    public int extractTotal(ApiResponse response) {
        return response.getInt("total");
    }

    public void verifyErrorMessage(ApiResponse response,
                                   String expectedError) {
        Assert.assertEquals(response.getString("error"),
                expectedError, "Error message mismatch");
    }
}
