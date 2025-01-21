package api;

import api.models.Courier;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class CourierApi {
    private static final String BASE_URL = "https://qa-scooter.praktikum-services.ru/api/v1/courier";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Создание курьера.
     *
     * @param courier Данные курьера.
     * @return Ответ сервера.
     */
    @Step("Создание курьера")
    public static Response createCourier(Courier courier) {
        return given()
                .filter(new io.qameta.allure.restassured.AllureRestAssured()) // Интеграция Allure с RestAssured
                .header("Content-type", "application/json")
                .body(serializeCourier(courier)) // Сериализация объекта в JSON
                .post(BASE_URL);
    }

    /**
     * Логин курьера.
     *
     * @param courier Данные курьера.
     * @return Ответ сервера.
     */
    @Step("Логин курьера")
    public static Response loginCourier(Courier courier) {
        return given()
                .filter(new io.qameta.allure.restassured.AllureRestAssured()) // Интеграция Allure с RestAssured
                .header("Content-type", "application/json")
                .body(serializeCourier(courier)) // Сериализация объекта в JSON
                .post(BASE_URL + "/login");
    }

    /**
     * Удаление курьера.
     *
     * @param id ID курьера.
     */
    @Step("Удаление курьера с ID: {id}")
    public static void deleteCourier(String id) {
        given()
                .filter(new io.qameta.allure.restassured.AllureRestAssured()) // Интеграция Allure с RestAssured
                .header("Content-type", "application/json")
                .delete(BASE_URL + "/" + id);
    }

    /**
     * Сериализация объекта курьера в JSON.
     *
     * @param courier Объект курьера.
     * @return JSON-строка.
     */
    private static String serializeCourier(Courier courier) {
        try {
            return objectMapper.writeValueAsString(courier);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при сериализации курьера", e);
        }
    }
}