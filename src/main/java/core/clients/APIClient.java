package core.clients;

import core.settings.ApiEndpoints;
import io.restassured.RestAssured;
import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import io.restassured.specification.RequestSpecification;

import static core.settings.ApiEndpoints.BASE_URL;

public class APIClient {
    private String token;

    //Настройка базовых параметров HTTP-запросов
    private RequestSpecification getRequestSpec() {
        return RestAssured.given()
                .baseUri(BASE_URL) // Устанавливаем базовый URL
                .header("Content-Type", "application/json") // Заголовок, указывающий формат данных
                .header("Accept", "application/json") // Заголовок, указывающий на принимаемый формат
                .filter(addAuthTokenFilter()); // Фильтр для добавления токена
    }

    // Метод для получения токена
    public void createToken(String username, String password) {
        // Формирование JSON тела для запроса
        String requestBody = String.format("{ \"username\": \"%s\",\"password\": \"%s\" }", username, password);

        // Отправка POST-запроса на эндпоинт для аутентификации и получение токена
        Response response = getRequestSpec()
                .body(requestBody) // Устанавливаем тело запроса
                .when()
                .post(ApiEndpoints.AUTH.getPath()) // POST-запрос на эндпоинт аутентификации
                .then()
                .statusCode(200) // Проверяем, что статус ответа 200 (ОК)
                .extract()
                .response();

        // Извлечение токена из ответа и сохранение в переменной
        token = response.jsonPath().getString("token");
    }

    //Фильтр для добавления токена в заголовок Authorization
    private Filter addAuthTokenFilter() {
        return (FilterableRequestSpecification requestSpec, FilterableResponseSpecification responseSpec, FilterContext ctx) -> {
            if (token != null) {
                requestSpec.header("Cookie", "token=" + token);
            }
            return ctx.next(requestSpec, responseSpec); // Продолжает выполнение запроса
        };
    }

    // GET /ping
    public Response ping() {
        return getRequestSpec()
                .when()
                .get(ApiEndpoints.PING.getPath())
                .then()
                .statusCode(201)
                .extract()
                .response();
    }

    // GET /booking
    public Response getBooking() {
        return getRequestSpec()
                .when()
                .get(ApiEndpoints.BOOKING.getPath())
                .then()
                .log().all()
                .statusCode(200)
                .extract()
                .response();
    }

    // GET /booking/{id}
    public Response getBookingById(int id) {
        String path = String.format(ApiEndpoints.BOOKINGBYID.getPath(), id);
        return getRequestSpec()
                .when()
                .get(path)
                .then()
                .statusCode(200)
                .extract()
                .response();
    }

    // POST /booking
    public Response createBooking(String jsonBody) {
        return getRequestSpec()
                .body(jsonBody)
                .when()
                .post(ApiEndpoints.BOOKING.getPath())
                .then()
                .statusCode(200)
                .extract()
                .response();
    }

    // PUT /booking/{id}
    public Response updateBooking(int id, String jsonBody, String token) {
        String path = String.format(ApiEndpoints.BOOKINGBYID.getPath(), id);
        return getRequestSpec()
                .header("Cookie", "token=" + token)
                .body(jsonBody)
                .when()
                .put(path)
                .then()
                .statusCode(200)
                .extract()
                .response();
    }

    // PATCH /booking/{id}
    public Response partialUpdateBooking(int id, String jsonBody, String token) {
        String path = String.format(ApiEndpoints.BOOKINGBYID.getPath(), id);
        return getRequestSpec()
                .header("Cookie", "token=" + token)
                .body(jsonBody)
                .when()
                .patch(path)
                .then()
                .statusCode(200)
                .extract()
                .response();
    }

    // DELETE /booking/{id}
    public Response deleteBooking(int id, String token) {
        String path = String.format(ApiEndpoints.BOOKINGBYID.getPath(), id);
        return getRequestSpec()
                .header("Cookie", "token=" + token)
                .when()
                .delete(path)
                .then()
                .statusCode(201)
                .extract()
                .response();
    }

    // POST /auth для получения токена
    public Response auth(String jsonBody) {
        return getRequestSpec()
                .body(jsonBody)
                .when()
                .post(ApiEndpoints.AUTH.getPath())
                .then()
                .statusCode(200)
                .extract()
                .response();
    }
}
