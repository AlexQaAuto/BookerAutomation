package core.clients;
import core.settings.ApiEndpoints;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static core.settings.ApiEndpoints.BASE_URL;


public class APIClient {

    //Настройка базовых параметров HTTP-запросов
    private RequestSpecification getRequestSpec() {
        return RestAssured.given()
                .baseUri(BASE_URL)
                .header("Content-type", "application/json")
                .header("Accept", "application/json");
    }


    //GET запрос на эндпоинт /ping
    public Response ping() {
        return getRequestSpec()
                .when()
                .get(ApiEndpoints.PING.getPath()) //Используем ENUM для эндпоинта ping
                .then()
                .statusCode(201) // Ожидаемый статус-код 201 Created
                .extract()
                .response();
    }

    //GET запрос на эндпоинт /booking
    public Response getBooking() {
        return getRequestSpec()
                .when()
                .get(ApiEndpoints.BOOKING.getPath()) //Используем ENUM для эндпоинта booking
                .then()
                .statusCode(200) // Ожидаемый статус-код 200 OK
                .extract()
                .response();
    }

    //GET запрос на эндпоинт /booking/1
    public Response getBookingById(int id) {
        return getRequestSpec()
                .when()
                .get(ApiEndpoints.BOOKINGBYID.getPath()) //Используем ENUM для эндпоинта booking/1
                .then()
                .statusCode(200) // Ожидаемый статус-код 200 OK
                .extract()
                .response();

    }


}



