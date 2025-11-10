package core.settings;

public enum ApiEndpoints {
    PING("/ping"),
    BOOKING("/booking"),
    BOOKINGBYID("/booking/%d"),
    AUTH("/auth");

    private final String path;

    ApiEndpoints(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public static final String BASE_URL = "https://restful-booker.herokuapp.com";
}
