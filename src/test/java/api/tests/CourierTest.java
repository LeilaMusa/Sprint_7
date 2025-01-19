package api.tests;

import api.CourierApi;
import api.models.Courier;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class CourierTest {
    private Courier courier;
    private String courierId;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Before
    @Step("Настройка тестовых данных")
    public void setUp() throws IOException {
        // Устанавливаем базовый URI
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";

        // Чтение JSON-файла для создания курьера
        courier = objectMapper.readValue(new File("src/test/resources/create_courier.json"), Courier.class);
    }

    @After
    @Step("Удаление тестовых данных")
    public void tearDown() {
        if (courierId != null) {
            deleteCourier(courierId);
        }
    }

    @Step("Удаление курьера с ID: {courierId}")
    private void deleteCourier(String courierId) {
        CourierApi.deleteCourier(courierId);
    }

    @Test
    @DisplayName("Создание курьера")
    @Step("Тест: Создание курьера")
    public void testCreateCourier() throws IOException {
        // Генерация уникального логина
        String uniqueLogin = generateUniqueLogin();
        courier.setLogin(uniqueLogin);

        // Отправка запроса на создание курьера
        Response response = createCourierRequest(courier);

        // Проверка успешного создания курьера
        verifyCourierCreation(response);
    }

    @Step("Генерация уникального логина")
    private String generateUniqueLogin() {
        return "courier_" + System.currentTimeMillis();
    }

    @Step("Отправка запроса на создание курьера")
    private Response createCourierRequest(Courier courier) {
        return given()
                .log().all()
                .header("Content-type", "application/json")
                .body(courier)
                .when()
                .post("/api/v1/courier");
    }

    @Step("Проверка успешного создания курьера")
    private void verifyCourierCreation(Response response) {
        response.then().log().all()
                .assertThat().statusCode(201)
                .and()
                .body("ok", equalTo(true));
    }

    @Test
    @DisplayName("Нельзя создать двух одинаковых курьеров")
    @Step("Тест: Нельзя создать двух одинаковых курьеров")
    public void testCreateDuplicateCourier() throws IOException {
        // Создание курьера
        createCourier(courier);

        // Попытка создать дубликат курьера
        Response response = createCourierRequest(courier);

        // Проверка ошибки при создании дубликата
        verifyDuplicateCourierError(response);
    }

    @Step("Создание курьера")
    private void createCourier(Courier courier) {
        CourierApi.createCourier(courier);
    }

    @Step("Проверка ошибки при создании дубликата курьера")
    private void verifyDuplicateCourierError(Response response) {
        response.then().log().all()
                .assertThat().statusCode(409)
                .and()
                .body("message", equalTo("Этот логин уже используется. Попробуйте другой."));
    }

    @Test
    @DisplayName("Создание курьера без обязательных полей")
    @Step("Тест: Создание курьера без обязательных полей")
    public void testCreateCourierWithoutRequiredFields() throws IOException {
        // Создание курьера без обязательных полей
        Courier invalidCourier = createInvalidCourier();

        // Отправка запроса на создание курьера без обязательных полей
        Response response = createCourierRequest(invalidCourier);

        // Проверка ошибки при отсутствии обязательных полей
        verifyRequiredFieldsError(response);
    }

    @Step("Создание курьера без обязательных полей")
    private Courier createInvalidCourier() {
        return new Courier("", "", "");
    }

    @Step("Проверка ошибки при отсутствии обязательных полей")
    private void verifyRequiredFieldsError(Response response) {
        response.then().log().all()
                .assertThat().statusCode(400)
                .and()
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Логин курьера")
    @Step("Тест: Логин курьера")
    public void testLoginCourier() throws IOException {
        // Создание курьера перед логином
        createCourier(courier);

        // Логин курьера
        Response response = loginCourierRequest(courier);

        // Проверка успешного логина
        verifySuccessfulLogin(response);

        // Сохраняем ID курьера для последующего удаления
        saveCourierId(response);
    }

    @Step("Отправка запроса на логин курьера")
    private Response loginCourierRequest(Courier courier) {
        return given()
                .log().all()
                .header("Content-type", "application/json")
                .body(courier)
                .when()
                .post("/api/v1/courier/login");
    }

    @Step("Проверка успешного логина")
    private void verifySuccessfulLogin(Response response) {
        response.then().log().all()
                .assertThat().statusCode(200)
                .and()
                .body("id", notNullValue());
    }

    @Step("Сохранение ID курьера")
    private void saveCourierId(Response response) {
        courierId = response.path("id").toString();
    }

    @Test
    @DisplayName("Логин курьера с неверными данными")
    @Step("Тест: Логин курьера с неверными данными")
    public void testLoginCourierWithInvalidData() throws IOException {
        // Логин с неверными данными
        Courier invalidCourier = createInvalidLoginCourier();

        // Отправка запроса на логин с неверными данными
        Response response = loginCourierRequest(invalidCourier);

        // Проверка ошибки при неверных данных
        verifyInvalidLoginError(response);
    }

    @Step("Создание курьера с неверными данными для логина")
    private Courier createInvalidLoginCourier() {
        return new Courier("invalid", "invalid", "");
    }

    @Step("Проверка ошибки при неверных данных")
    private void verifyInvalidLoginError(Response response) {
        response.then().log().all()
                .assertThat().statusCode(404)
                .and()
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Логин курьера без обязательных полей")
    @Step("Тест: Логин курьера без обязательных полей")
    public void testLoginCourierWithoutRequiredFields() throws IOException {
        // Логин без обязательных полей
        Courier invalidCourier = createInvalidCourier();

        // Отправка запроса на логин без обязательных полей
        Response response = loginCourierRequest(invalidCourier);

        // Проверка ошибки при отсутствии обязательных полей
        verifyRequiredFieldsLoginError(response);
    }

    @Step("Проверка ошибки при отсутствии обязательных полей для логина")
    private void verifyRequiredFieldsLoginError(Response response) {
        response.then().log().all()
                .assertThat().statusCode(400)
                .and()
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Логин курьера с отсутствующим паролем")
    @Step("Тест: Логин курьера с отсутствующим паролем")
    public void testLoginCourierWithoutPassword() throws IOException {
        // Логин без пароля
        Courier invalidCourier = createCourierWithoutPassword();

        // Отправка запроса на логин без пароля
        Response response = loginCourierRequest(invalidCourier);

        // Проверка ошибки при отсутствии пароля
        verifyMissingPasswordError(response);
    }

    @Step("Создание курьера без пароля")
    private Courier createCourierWithoutPassword() {
        return new Courier(courier.getLogin(), "", "");
    }

    @Step("Проверка ошибки при отсутствии пароля")
    private void verifyMissingPasswordError(Response response) {
        response.then().log().all()
                .assertThat().statusCode(400)
                .and()
                .body("message", equalTo("Недостаточно данных для входа"));
    }
}
