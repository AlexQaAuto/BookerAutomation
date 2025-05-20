package core.settings;

public enum ApiEndpoints {
    PING("/ping"),
    BOOKING("/booking"), //Новый эндпоинт booking
    BOOKINGBYID("/booking/%d");


    public static final String BASE_URL = "https://restful-booker.herokuapp.com";

    private final String path;

    ApiEndpoints(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
