package api;

import io.restassured.response.Response;

public class ApiResponse {

    private final Response response;

    public ApiResponse(Response response) {
        this.response = response;
    }

    // Status 

    public int getStatusCode() {
        return response.getStatusCode();
    }

    // Field extraction 

    public String getString(String path) {
        return response.jsonPath().getString(path);
    }

    public int getInt(String path) {
        return response.jsonPath().getInt(path);
    }

    public <T> java.util.List<T> getList(String path, Class<T> type) {
        return response.jsonPath().getList(path, type);
    }

    public java.util.List<Object> getList(String path) {
        return response.jsonPath().getList(path);
    }

    public Object get(String path) {
        return response.jsonPath().get(path);
    }

    // Body 

    public String getBodyAsString() {
        return response.getBody().asString();
    }

    // Raw access — use sparingly 
    // Only if something cannot be expressed via above methods
    Response getRaw() {
        return response;
    }
}
