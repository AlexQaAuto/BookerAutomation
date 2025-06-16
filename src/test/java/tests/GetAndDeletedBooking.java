package tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.clients.APIClient;
import core.models.Booking;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;


public class GetAndDeletedBooking {


    private APIClient apiClient;
    private ObjectMapper objectMapper;


    //Инициализация API клиента перед каждым тестом
    @BeforeEach
    public void setup() {
        apiClient = new APIClient();
        objectMapper = new ObjectMapper();
        apiClient.createToken("admin", "password123");
    }

    @Test
    public void testGetAndDeleteBooking() {

        // Получаем список ID всех бронирований
        Response response = apiClient.getBooking();

        // Десериализуем тело ответа в список объектов Booking
        String responseBody = response.getBody().asString();
        List<Booking> bookings = null;
        try {
            bookings = objectMapper.readValue(responseBody, new TypeReference<List<Booking>>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        //Проверяем, что тело ответа содержит объекты Booking
        assertThat(bookings).isNotEmpty(); //Проверяем, что список не пуст


        // Берём первый id из списка
        int bookingId = bookings.get(1).getBookingid();

        // Удаляем id полученный выше
        Response deleteResponse = apiClient.deleteBooking(bookingId);

        // Проверяем, что удаление прошло успешно
        assertThat(deleteResponse.getStatusCode()).isEqualTo(201); // или 200, в зависимости от API

    }
}
