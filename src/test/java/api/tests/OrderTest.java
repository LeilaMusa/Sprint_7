package api.tests;

import api.OrderApi;
import api.models.Order;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@RunWith(Parameterized.class) // Включаем поддержку параметризации
public class OrderTest {

    private String[] colors;

    // Конструктор для параметризации
    public OrderTest(String[] colors) {
        this.colors = colors;
    }

    @Before
    @Step("Настройка тестовых данных")
    public void setUp() {
        // Устанавливаем базовый URI
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
    }

    // Метод для предоставления параметров
    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {new String[]{"BLACK"}}, // Один цвет
                {new String[]{"GREY"}},  // Один цвет
                {new String[]{"BLACK", "GREY"}}, // Оба цвета
                {new String[]{}} // Без цвета
        });
    }

    @Test
    @DisplayName("Создание заказа с разными вариантами цветов")
    @Step("Тест: Создание заказа с разными вариантами цветов")
    public void testCreateOrderWithDifferentColors() {
        // Создаём заказ с указанными цветами
        Order order = createOrderWithColors(colors);

        // Отправляем запрос на создание заказа
        Response response = sendCreateOrderRequest(order);

        // Проверяем, что заказ создан успешно и тело ответа содержит track
        verifyOrderCreation(response);
    }

    @Step("Создание заказа с цветами: {colors}")
    private Order createOrderWithColors(String[] colors) {
        return new Order("Ван", "Ким", "Москва", "Маяковская", "+79168887788", 1, "26.01.2025", "Комментарий", colors);
    }

    @Step("Отправка запроса на создание заказа")
    private Response sendCreateOrderRequest(Order order) {
        return OrderApi.createOrder(order);
    }

    @Step("Проверка успешного создания заказа")
    private void verifyOrderCreation(Response response) {
        response.then()
                .statusCode(201)
                .body("track", notNullValue());
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
