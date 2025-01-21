package api.tests;

import api.CourierApi;
import api.models.Courier;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.*;

public class CourierTest {
    private Courier courier;
    private String courierId;

    @Before
    @Step("Настройка тестовых данных")
    public void setUp() {
        // Устанавливаем базовый URI
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";

        // Создание курьера программно
        courier = new Courier("default_login", "default_password", "default_firstName");
    }

    @After
    @Step("Удаление тестовых данных")
    public void tearDown() {
        if (courierId != null) {
            deleteCourier(courierId);
        }
    }

    // Тесты
    @Test
    @DisplayName("Создание курьера")
    @Step("Тест: Создание курьера")
    public void testCreateCourier() {
        // Генерация уникального логина
        String uniqueLogin = generateUniqueLogin();
        courier.setLogin(uniqueLogin);

        // Отправка запроса на создание курьера
        Response response = CourierApi.createCourier(courier);

        // Проверка успешного создания курьера
        verifyCourierCreation(response);

        // Логинимся, чтобы получить ID курьера
        Response loginResponse = CourierApi.loginCourier(courier);

        // Сохраняем ID курьера для последующего удаления
        courierId = loginResponse.then().extract().path("id").toString();
    }

    @Test
    @DisplayName("Нельзя создать двух одинаковых курьеров")
    @Step("Тест: Нельзя создать двух одинаковых курьеров")
    public void testCreateDuplicateCourier() {
        // Создание курьера
        CourierApi.createCourier(courier);

        // Попытка создать дубликат курьера
        Response response = CourierApi.createCourier(courier);

        // Проверка ошибки при создании дубликата
        verifyDuplicateCourierError(response);
    }

    @Test
    @DisplayName("Создание курьера без логина")
    @Step("Тест: Создание курьера без логина")
    public void testCreateCourierWithoutLogin() {
        // Создание курьера без логина
        Courier invalidCourier = new Courier("", courier.getPassword(), courier.getFirstName());

        // Отправка запроса на создание курьера без логина
        Response response = CourierApi.createCourier(invalidCourier);

        // Проверка ошибки при отсутствии логина
        verifyRequiredFieldsError(response);
    }

    @Test
    @DisplayName("Создание курьера без пароля")
    @Step("Тест: Создание курьера без пароля")
    public void testCreateCourierWithoutPassword() {
        // Создание курьера без пароля
        Courier invalidCourier = new Courier(courier.getLogin(), "", courier.getFirstName());

        // Отправка запроса на создание курьера без пароля
        Response response = CourierApi.createCourier(invalidCourier);

        // Проверка ошибки при отсутствии пароля
        verifyRequiredFieldsError(response);
    }

    @Test
    @DisplayName("Логин курьера")
    @Step("Тест: Логин курьера")
    public void testLoginCourier() {
        // Создание курьера перед логином
        CourierApi.createCourier(courier);

        // Логин курьера
        Response response = CourierApi.loginCourier(courier);

        // Проверка успешного логина
        verifySuccessfulLogin(response);

        // Сохраняем ID курьера для последующего удаления
        courierId = response.then().extract().path("id").toString();
    }

    @Test
    @DisplayName("Логин курьера с неверным логином")
    @Step("Тест: Логин курьера с неверным логином")
    public void testLoginCourierWithInvalidLogin() {
        // Логин с неверным логином
        Courier invalidCourier = new Courier("invalid", courier.getPassword(), "");

        // Отправка запроса на логин с неверным логином
        Response response = CourierApi.loginCourier(invalidCourier);

        // Проверка ошибки при неверном логине
        verifyInvalidLoginError(response);
    }

    @Test
    @DisplayName("Логин курьера с неверным паролем")
    @Step("Тест: Логин курьера с неверным паролем")
    public void testLoginCourierWithInvalidPassword() {
        // Логин с неверным паролем
        Courier invalidCourier = new Courier(courier.getLogin(), "invalid", "");

        // Отправка запроса на логин с неверным паролем
        Response response = CourierApi.loginCourier(invalidCourier);

        // Проверка ошибки при неверном пароле
        verifyInvalidLoginError(response);
    }

    // Вспомогательные методы
    @Step("Удаление курьера с ID: {courierId}")
    private void deleteCourier(String courierId) {
        CourierApi.deleteCourier(courierId);
    }

    @Step("Генерация уникального логина")
    private String generateUniqueLogin() {
        return "courier_" + System.currentTimeMillis();
    }

    @Step("Проверка успешного создания курьера")
    private void verifyCourierCreation(Response response) {
        response.then().log().all()
                .assertThat().statusCode(SC_CREATED)
                .and()
                .body("ok", equalTo(true));
    }

    @Step("Проверка ошибки при создании дубликата курьера")
    private void verifyDuplicateCourierError(Response response) {
        response.then().log().all()
                .assertThat().statusCode(SC_CONFLICT)
                .and()
                .body("message", equalTo("Этот логин уже используется. Попробуйте другой."));
    }

    @Step("Проверка ошибки при отсутствии обязательных полей")
    private void verifyRequiredFieldsError(Response response) {
        response.then().log().all()
                .assertThat().statusCode(SC_BAD_REQUEST)
                .and()
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Step("Проверка успешного логина")
    private void verifySuccessfulLogin(Response response) {
        response.then().log().all()
                .assertThat().statusCode(SC_OK)
                .and()
                .body("id", notNullValue());
    }

    @Step("Проверка ошибки при неверных данных")
    private void verifyInvalidLoginError(Response response) {
        response.then().log().all()
                .assertThat().statusCode(SC_NOT_FOUND)
                .and()
                .body("message", equalTo("Учетная запись не найдена"));
    }
}