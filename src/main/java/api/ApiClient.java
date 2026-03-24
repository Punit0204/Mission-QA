package api;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import utils.ConfigManager;

public class ApiClient {

    // volatile ensures visibility across threads without full synchronization
    private static volatile RequestSpecification requestSpec;

    private ApiClient() {}

    public static RequestSpecification getSpec() {
        // double-checked locking — safe with volatile
        if (requestSpec == null) {
            synchronized (ApiClient.class) {
                if (requestSpec == null) {
                    requestSpec = new RequestSpecBuilder()
                            .setBaseUri(ConfigManager.getInstance()
                                    .getApiBaseUrl())
                            .setContentType(ContentType.JSON)
                            .setRelaxedHTTPSValidation()
                            .addHeader("x-api-key",
                                    ConfigManager.getInstance()
                                            .get("api.key"))
                            .log(LogDetail.METHOD)
                            .log(LogDetail.URI)
                            .build();
                }
            }
        }
        return requestSpec;
    }
}