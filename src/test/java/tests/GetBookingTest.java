package tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import core.clients.APIClient;
import core.models.Booking;
import core.models.BookingDates;
import core.models.CreatedBooking;
import core.models.NewBooking;
import io.restassured.response.Response;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;


public class GetBookingTest {

    private APIClient apiClient;
    private ObjectMapper objectMapper;
    private CreatedBooking createdBooking; // Храним созданное бронирование - это для ответа
    private NewBooking newBooking;

    //Инициализация API клиента перед каждым тестом
    @BeforeEach
    public void setup() throws JsonProcessingException {
        apiClient = new APIClient();
        objectMapper = new ObjectMapper();

        // Создаём объект Booking c необходимыми данными
        newBooking = new NewBooking(); // Создали объект NewBooking
        newBooking.setFirstname("Sir");
        newBooking.setLastname("Ferguson");
        newBooking.setTotalprice(333);
        newBooking.setDepositpaid(true);
        newBooking.setBookingdates(new BookingDates("2024-03-01", "2024-02-04"));
        newBooking.setAdditionalneeds("Breakfast");

        // Отправляем запрос на создание бронирования
        String bookingJson = new ObjectMapper().writeValueAsString(newBooking);
        Response response = apiClient.createBooking(bookingJson);

        // Проверяем что бронирование создано успешно
        assertEquals(200, response.statusCode(), "Ожидаемый статус код - 200 для создания");

        }

        @Test
        public void testGetBooking() throws Exception {

            // Выполняем запрос к эндпоинту /booking через APIClient
            Response response = apiClient.getBooking();

            // Проверяем что статус-код ответа равен 200
            assertThat(response.getStatusCode()).isEqualTo(200);

            // Десериализуем тело ответа в список объектов Booking
            String responseBody = response.getBody().asString();
            List<Booking> bookings = objectMapper.readValue(responseBody, new TypeReference<List<Booking>>() {});

            //Проверяем, что тело ответа содержит объекты Booking
            assertThat(bookings).isNotEmpty(); //Проверяем, что список не пуст

            // Проверяем, что каждый объект Booking содержит валидное значение bookingid
            for (Booking booking : bookings) {
                assertThat(booking.getBookingid()).isGreaterThan(0); // bookingid должен быть > 0
            }
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



