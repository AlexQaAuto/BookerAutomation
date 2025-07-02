package core.clients;
import core.settings.ApiEndpoints;
import io.restassured.RestAssured;
import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
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

    //DELETE запрос на эндпоинт /booking
    public Response deleteBooking(int bookingId) {
        return getRequestSpec()
                .pathParam("id", bookingId) //Указываем path parameter для ID
                .when()
                .delete(ApiEndpoints.BOOKING.getPath() + "/{id}") //Используем параметр пути в запросе
                .then()
                .log().all() //Логируем то что возвращается в ответе в консоль
                .statusCode(201) // Ожидаемый статус-код 200 OK
                .extract()
                .response();

    }



}



