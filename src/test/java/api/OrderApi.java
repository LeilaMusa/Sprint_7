package api;

import api.models.Order;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class OrderApi {
    private static final String BASE_URL = "https://qa-scooter.praktikum-services.ru/api/v1/orders";

    public static Response createOrder(Order order) {
        return given()
                .header("Content-type", "application/json")
                .body(order)
                .post(BASE_URL);
    }

    public static Response getOrders() {
        return given()
                .header("Content-type", "application/json")
                .get("/v1/orders");
    }
}
