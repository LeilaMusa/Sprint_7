package api;

import api.models.Courier;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class CourierApi {
    private static final String BASE_URL = "https://qa-scooter.praktikum-services.ru/api/v1/courier";

    /**
     * Создание курьера.
     *
     * @param courier Данные курьера.
     * @return Ответ сервера.
     */
    public static Response createCourier(Courier courier) {
        return given()
                .header("Content-type", "application/json")
                .body(courier)
                .post(BASE_URL);
    }

    /**
     * Логин курьера.
     *
     * @param courier Данные курьера.
     * @return Ответ сервера.
     */
    public static Response loginCourier(Courier courier) {
        return given()
                .header("Content-type", "application/json")
                .body(courier)
                .post(BASE_URL + "/login");
    }

    /**
     * Удаление курьера.
     *
     * @param id ID курьера.
     */
    public static void deleteCourier(String id) {
        given()
                .header("Content-type", "application/json")
                .delete(BASE_URL + "/" + id);
    }

    /**
     * Получение списка заказов.
     *
     * @return Ответ сервера.
     */
    public static Response getOrders() {
        return given()
                .when()
                .get("/v1/orders");
    }
}