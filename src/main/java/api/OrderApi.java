package api;

import io.restassured.response.Response;
import api.models.Order;
import static io.restassured.RestAssured.given;

public class OrderApi {
    private static final String BASE_URL = "https://qa-scooter.praktikum-services.ru";

    public static Response createOrder(Order order) {
        return given()
                .header("Content-type", "application/json")
                .body(order)
                .post(BASE_URL + "/api/v1/orders");
    }

    public static Response getOrders() {
        return given()
                .header("Content-type", "application/json")
                .get(BASE_URL + "/v1/orders");
    }
}