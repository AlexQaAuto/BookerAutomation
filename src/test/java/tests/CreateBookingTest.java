package tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.clients.APIClient;
import core.models.BookingDates;
import core.models.CreatedBooking;
import core.models.NewBooking;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CreateBookingTest {

    private APIClient apiClient;
    private ObjectMapper objectMapper;
    private CreatedBooking createdBooking; //Храним созданное бронирование - это для ответа
    private NewBooking newBooking; //Новый объект для создания бронирования - это для запроса

    // Инициализация API клиента и создание объекта Booking перед каждым тестом
    @BeforeEach
    public void setup() {
        apiClient = new APIClient();
        objectMapper = new ObjectMapper();

        //Создаём объект Booking c необходимыми данными
        newBooking = new NewBooking(); // Создали объект NewBooking -> через сеттеры подставляем значения
        newBooking.setFirstname("John");
        newBooking.setLastname("Doe");
        newBooking.setTotalprice(150);
        newBooking.setDepositpaid(true);
        newBooking.setBookingdates(new BookingDates("2024-01-01", "2024-01-05"));
        newBooking.setAdditionalneeds("Breakfast");
    }

    @Test
    public void CreateBooking() throws JsonProcessingException {
        // Выполняем запрос к эндпоинту /booking через APIClient
        String requestBody = objectMapper.writeValueAsString(newBooking); // Объект NewBooking превращаем в строку и складываем в requestBody
        Response response = apiClient.createBooking(requestBody); // requestBody передаём в метод createBooking

        //Проверяем, что статус код == 200
        assertThat(response.getStatusCode()).isEqualTo(200);


        // Десериализуем тело ответа в объект Booking
        String responseBody = response.asString();
        createdBooking = objectMapper.readValue(responseBody, CreatedBooking.class); // objectMapper читает значение responseBody и сопостовляет его с CreatedBooking

        // Проверяем, что тело ответа содержит объект нового бронирования
        assertThat(createdBooking).isNotNull();
        assertEquals(createdBooking.getBooking().getFirstname(), newBooking.getFirstname()); // Проверяем то что создалось с тем что мы ожидали получить
        assertEquals(createdBooking.getBooking().getLastname(), newBooking.getLastname());
        assertEquals(createdBooking.getBooking().getTotalprice(), newBooking.getTotalprice());
        assertEquals(createdBooking.getBooking().isDepositpaid(), newBooking.isDepositpaid());
        assertEquals(createdBooking.getBooking().getBookingdates().getCheckin(), newBooking.getBookingdates().getCheckin());
        assertEquals(createdBooking.getBooking().getBookingdates().getCheckout(), newBooking.getBookingdates().getCheckout());
        assertEquals(createdBooking.getBooking().getAdditionalneeds(), newBooking.getAdditionalneeds());
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






