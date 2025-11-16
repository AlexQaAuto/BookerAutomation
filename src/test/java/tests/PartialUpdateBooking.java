package tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.clients.APIClient;
import core.models.BookingDates;
import core.models.CreatedBooking;
import core.models.NewBooking;
import core.models.UpdatedBooking;
import io.restassured.response.Response;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PartialUpdateBooking {

    private APIClient apiClient;
    private ObjectMapper objectMapper;
    private int bookingId;
    private CreatedBooking createdBooking; // Храним созданное бронирование - это для ответа
    private NewBooking newBooking;

    @BeforeEach
    public void setUp() throws JsonProcessingException {
        apiClient = new APIClient();
        objectMapper = new ObjectMapper();

        // Создаём объект Booking c необходимыми данными
        newBooking = new NewBooking(); // Создали объект NewBooking
        newBooking.setFirstname("Alex");
        newBooking.setLastname("Smith");
        newBooking.setTotalprice(456);
        newBooking.setDepositpaid(true);
        newBooking.setBookingdates(new BookingDates("2024-06-01", "2024-05-12"));
        newBooking.setAdditionalneeds("Test");

        // Отправляем запрос на создание бронирования
        String bookingJson = new ObjectMapper().writeValueAsString(newBooking);
        Response response = apiClient.createBooking(bookingJson);

        // Получаем id созданного бронирования из ответа
        bookingId = response.jsonPath().getInt("bookingid");

        // Проверяем что бронирование создано успешно
        assertEquals(200, response.statusCode(), "Ожидаемый статус код - 200 для создания");

    }

    @Test
    public void PatchUpdateBooking() throws JsonProcessingException {
        // Получаем текущее состояние бронирования
        Response getResponse = apiClient.getBookingById(bookingId);
        assertEquals(200, getResponse.statusCode(), "Бронирование не найдено перед обновлением");

        // Обновляем данные
        newBooking.setFirstname("Mike");

        String requestBody = objectMapper.writeValueAsString(newBooking);
        // создаём токен
        String token = apiClient.createToken("admin", "password123").jsonPath().getString("token");
        Response response = apiClient.patchUpdateBooking(bookingId, requestBody, token);

        // Проверяем статус-код
        assertEquals(200, response.statusCode(), "Ожидаемый статус код - 200 для обновления");

        // Проверяем обновленные данные
        String responseBody = response.asString();

        UpdatedBooking updatedBooking = objectMapper.readValue(responseBody, UpdatedBooking.class);

        assertEquals("Mike", updatedBooking.getFirstname());
        assertEquals("Smith", updatedBooking.getLastname());
        assertEquals(456, updatedBooking.getTotalprice());
        assertEquals(true, updatedBooking.isDepositpaid());
        assertEquals("2024-06-01", updatedBooking.getBookingdates().getCheckin());
        assertEquals("2024-05-12", updatedBooking.getBookingdates().getCheckout());
        assertEquals("Test", updatedBooking.getAdditionalneeds());
    }


    @AfterEach
    public void tearDown() {
        if (createdBooking != null) {
            // создаём токен
            String token = apiClient.createToken("admin", "password123").jsonPath().getString("token");

            // удаляем бронирование
            Response deleteResponse = apiClient.deleteBooking(createdBooking.getBookingid(), token);
            assertThat(deleteResponse.getStatusCode()).isEqualTo(201);

            // проверяем, что бронирование больше не существует
            Response getResponse = apiClient.getBookingById(createdBooking.getBookingid());
            assertThat(getResponse.getStatusCode()).isEqualTo(404); // корректный код после удаления
        }
    }
}
