package core.clients;

import core.settings.ApiEndpoints;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static core.settings.ApiEndpoints.BASE_URL;

public class APIClient {

    // Настройка базовых параметров HTTP-запросов
    private RequestSpecification getRequestSpec() {
        return RestAssured.given()
                .baseUri(BASE_URL)
                .header("Content-type", "application/json")
                .header("Accept", "application/json");
    }

    // POST /auth — получение токена
    public Response createToken(String username, String password) {
        String body = String.format("{\"username\":\"%s\",\"password\":\"%s\"}", username, password);
        return getRequestSpec()
                .body(body)
                .when()
                .post("/auth")
                .then()
                .extract()
                .response();
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
                .statusCode(200)
                .extract()
                .response();
    }

    // GET /booking/{id} — теперь не выбрасывает исключение при 404
    public Response getBookingById(int id) {
        String path = String.format(ApiEndpoints.BOOKINGBYID.getPath(), id);
        return getRequestSpec()
                .when()
                .get(path)
                .then()
                .extract()
                .response(); // не проверяем statusCode здесь, оставляем для теста
    }

    // POST /booking — создание брони
    public Response createBooking(String body) {
        return getRequestSpec()
                .body(body)
                .when()
                .post(ApiEndpoints.BOOKING.getPath())
                .then()
                .extract()
                .response();
    }

    // DELETE /booking/{id} — принимает токен и возвращает Response
    public Response deleteBooking(int id, String token) {
        String path = String.format(ApiEndpoints.BOOKINGBYID.getPath(), id);
        return getRequestSpec()
                .header("Cookie", "token=" + token)
                .when()
                .delete(path)
                .then()
                .extract()
                .response(); // не проверяем statusCode здесь, проверка делается в тесте
    }

    // PUT /booking/{id} — обновление бронирования
    public Response updateBooking(int id, String body, String token) {
        String path = String.format(ApiEndpoints.BOOKINGBYID.getPath(), id);
        return getRequestSpec()
                .header("Cookie", "token=" + token)
                .body(body)
                .when()
                .put(path)
                .then()
                .extract()
                .response();
    }


}
