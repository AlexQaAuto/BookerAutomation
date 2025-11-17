package tests;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.clients.APIClient;
import core.models.Booking;
import core.models.CreatedBooking;
import io.restassured.response.Response;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class GetBookingById {

    private APIClient apiClient;
    private ObjectMapper objectMapper;
    private CreatedBooking createdBooking;

    //Инициализация API клиента перед каждым тестом
    @BeforeEach
    public void setup() {
        apiClient = new APIClient();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testGetBookingById() throws Exception {

        // Выполняем запрос к эндпоинту /booking/2 через APIClient
        Response response = apiClient.getBookingById(47);

        // Десериализуем тело ответа в список объектов Booking
        String responseBody = response.getBody().asString();
        Booking booking = objectMapper.readValue(responseBody, Booking.class);

        //Проверяем, что тело ответа содержит объекты Booking
        assertThat(booking).isNotNull();

        // Проверяем что статус-код ответа равен 200
        assertThat(response.getStatusCode()).isEqualTo(200);


    }

    @AfterEach
    public void tearDown() {
        if (createdBooking != null) {
            // создаём токен
            String token = apiClient.createToken("admin", "password123").jsonPath().getString("token");

            // удаляем бронирование
            Response deleteResponse = apiClient.deleteBooking(createdBooking.getBookingid(), token);
            AssertionsForClassTypes.assertThat(deleteResponse.getStatusCode()).isEqualTo(201);

            // проверяем, что бронирование больше не существует
            Response getResponse = apiClient.getBookingById(createdBooking.getBookingid());
            AssertionsForClassTypes.assertThat(getResponse.getStatusCode()).isEqualTo(404); // корректный код после удаления
        }
    }

}

