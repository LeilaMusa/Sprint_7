package api.tests;

import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

public class OrderListTest {

    @Before
    @Step("Настройка тестовых данных")
    public void setUp() {
        // Устанавливаем базовый URI
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
    }

    @Test
    @DisplayName("Получение списка заказов через ручку /v1/orders")
    @Step("Тест: Получение списка заказов")
    public void testGetOrders() {
        // Отправляем запрос на получение списка заказов
        Response response = sendGetOrdersRequest();

        // Проверяем, что ответ успешный и тело ответа не пустое
        verifyGetOrdersResponse(response);
    }

    @Step("Отправка запроса на получение списка заказов")
    private Response sendGetOrdersRequest() {
        return given()
                .log().all() // Логирование запроса
                .when()
                .get("/v1/orders");
    }

    @Step("Проверка ответа на запрос списка заказов")
    private void verifyGetOrdersResponse(Response response) {
        response.then()
                .log().all() // Логирование ответа
                .assertThat()
                .statusCode(200)
                .and()
                .body(notNullValue()) // Проверяем, что тело ответа не пустое
                .body("orders", notNullValue()); // Проверяем, что в ответе есть поле orders
    }
}
