package tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.clients.APIClient;
import core.models.BookingDates;
import core.models.CreatedBooking;
import core.models.NewBooking;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GetBookingsWithFiltersTest {

    private APIClient apiClient;
    private ObjectMapper objectMapper;

    private final List<Integer> createdIds = new ArrayList<>();

    @BeforeEach
    public void setUp() throws JsonProcessingException {
        apiClient = new APIClient();
        objectMapper = new ObjectMapper();

        createdIds.add(createBooking("Alex", "Smith", "2024-06-01", "2024-06-10"));
        createdIds.add(createBooking("Alex", "Brown", "2024-06-05", "2024-06-07"));
        createdIds.add(createBooking("John", "Smith", "2024-06-01", "2024-06-02"));
    }

    private int createBooking(String first, String last, String checkin, String checkout)
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
        return response.jsonPath().getInt("bookingid");
    }

    // Универсальный helper
    private List<Integer> getIdsByFilter(Map<String, String> filter) {
        Response r = apiClient.getBookings(filter);
        assertEquals(200, r.statusCode(), "Неверный статус при фильтрации");
        return r.jsonPath().getList("bookingid");
    }

    @Test
    public void testFilterByFirstname() {
        List<Integer> ids = getIdsByFilter(Map.of("firstname", "Alex"));

        assertThat(ids)
                .containsExactlyInAnyOrder(createdIds.get(0), createdIds.get(1));
    }

    @Test
    public void testFilterByLastname() {
        List<Integer> ids = getIdsByFilter(Map.of("lastname", "Smith"));

        assertThat(ids)
                .containsExactlyInAnyOrder(createdIds.get(0), createdIds.get(2));
    }

    @Test
    public void testFilterByCheckinDate() {
        List<Integer> ids = getIdsByFilter(Map.of("checkin", "2024-06-01"));

        assertThat(ids)
                .containsExactlyInAnyOrder(createdIds.get(0), createdIds.get(2));
    }

    @Test
    public void testFilterByFullDateRange() {
        Map<String, String> filter = Map.of(
                "checkin", "2024-06-05",
                "checkout", "2024-06-07"
        );

        List<Integer> ids = getIdsByFilter(filter);

        // Вариант A — проверяем только наличие бронирования
        assertThat(ids)
                .as("Список должен содержать бронирование с указанным диапазоном дат")
                .contains(createdIds.get(1));
    }

    @AfterEach
    public void tearDown() {
        String token = apiClient.createToken("admin", "password123")
                .jsonPath().getString("token");

        for (Integer id : createdIds) {
            apiClient.deleteBooking(id, token);
        }
    }
}
