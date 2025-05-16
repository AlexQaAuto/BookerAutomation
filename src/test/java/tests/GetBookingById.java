package tests;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.clients.APIClient;
import core.models.Booking;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class GetBookingById {

    private APIClient apiClient;
    private ObjectMapper objectMapper;

    //Инициализация API клиента перед каждым тестом
    @BeforeEach
    public void setup() {
        apiClient = new APIClient();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testGetBookingById() throws Exception {

        // Выполняем запрос к эндпоинту /booking/1 через APIClient
        Response response = apiClient.getBookingById();

        // Проверяем что статус-код ответа равен 200
        assertThat(response.getStatusCode()).isEqualTo(200);

        // Десериализуем тело ответа в список объектов Booking
        String responseBody = response.getBody().asString();
        Booking booking = objectMapper.readValue(responseBody, Booking.class);

        //Проверяем, что тело ответа содержит объекты Booking
        assertThat(booking).isNotNull();

        //Проверяем, что список не пуст
        assertThat(booking.getFirstname()).isNotNull();
        assertThat(booking.getLastname()).isNotNull();
        assertThat(booking.getTotalprice()).isGreaterThan(0);
        assertThat(booking.isDepositpaid()).isIn(true, false);

        assertThat(booking.getBookingdates()).isNotNull();
        assertThat(booking.getBookingdates().getCheckin()).isNotNull();
        assertThat(booking.getBookingdates().getCheckout()).isNotNull();

        assertThat(booking.getAdditionalneeds()).isNotNull();


        }
    }

