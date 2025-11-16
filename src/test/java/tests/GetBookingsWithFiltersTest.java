package tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.clients.APIClient;
import core.models.BookingDates;
import core.models.CreatedBooking;
import core.models.NewBooking;
import io.restassured.response.Response;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GetBookingsWithFiltersTest {

    private APIClient apiClient;
    private ObjectMapper objectMapper;
    private CreatedBooking createdBooking;

    // храним ID всех созданных бронирований
    private final List<Integer> createdBookingIds = new ArrayList<>();

    @BeforeEach
    public void setUp() throws JsonProcessingException {
        apiClient = new APIClient();
        objectMapper = new ObjectMapper();

        // Создаём несколько бронирований
        createBooking("Alex", "Smith", "2024-06-01", "2024-06-10");
        createBooking("Alex", "Brown", "2024-06-05", "2024-06-07");
        createBooking("John", "Smith", "2024-06-01", "2024-06-02");
    }

    /** Упрощённый метод создания бронирования */
    private void createBooking(String first, String last, String checkin, String checkout)
            throws JsonProcessingException {

        NewBooking booking = new NewBooking();
        booking.setFirstname(first);
        booking.setLastname(last);
        booking.setTotalprice(200);
        booking.setDepositpaid(true);
        booking.setBookingdates(new BookingDates(checkin, checkout));
        booking.setAdditionalneeds("Test");

        String body = objectMapper.writeValueAsString(booking);

        Response response = apiClient.createBooking(body);
        assertEquals(200, response.statusCode(), "Бронирование должно быть создано");

        int id = response.jsonPath().getInt("bookingid");
        createdBookingIds.add(id);
    }

    /** Метод для выполнения GET с любыми фильтрами */
    private Response sendFilterRequest(Map<String, String> params) {
        return apiClient.getRequestSpec()
                .queryParams(params)
                .when()
                .get("/booking")
                .then()
                .extract()
                .response();
    }

    @Test
    public void testGetBookingsWithFilters() {

        //Фильтр по имени
        Map<String, String> nameFilter = Map.of("firstname", "Alex");
        Response respByFirstName = sendFilterRequest(nameFilter);
        assertEquals(200, respByFirstName.statusCode());
        assertThat(respByFirstName.jsonPath().getList("bookingid"));

        //Фильтр по фамилии
        Map<String, String> lastNameFilter = Map.of("lastname", "Smith");
        Response respByLastName = sendFilterRequest(lastNameFilter);
        assertEquals(200, respByLastName.statusCode());
        assertThat(respByLastName.jsonPath().getList("bookingid"));

        //Фильтр по checkin
        Map<String, String> checkinFilter = Map.of("checkin", "2024-06-01");
        Response respCheckin = sendFilterRequest(checkinFilter);
        assertEquals(200, respCheckin.statusCode());
        assertThat(respCheckin.jsonPath().getList("bookingid"));

        //Фильтр по checkin + checkout
        Map<String, String> fullDateFilter = new HashMap<>();
        fullDateFilter.put("checkin", "2024-06-05");
        fullDateFilter.put("checkout", "2024-06-07");

        Response respDates = sendFilterRequest(fullDateFilter);
        assertEquals(200, respDates.statusCode());
        assertThat(respDates.jsonPath().getList("bookingid")).hasSize(1);
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
