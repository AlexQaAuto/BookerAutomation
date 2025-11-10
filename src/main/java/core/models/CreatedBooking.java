package core.models;

public class CreatedBooking {

    private int bookingid;
    private NewBooking booking; // Тело бронирования соответствует NewBooking

    public CreatedBooking() { }

    public int getBookingid() {
        return bookingid;
    }
    public void setBookingid(int bookingid) {
        this.bookingid = bookingid;
    }

    public NewBooking getBooking() {
        return booking;
    }
    public void setBooking(NewBooking booking) {
        this.booking = booking;
    }
}
