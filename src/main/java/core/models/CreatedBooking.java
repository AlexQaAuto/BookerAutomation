package core.models;

public class CreatedBooking {

    private int bookingid;
    private NewBooking booking; // Тело бронирования соответствует NewBooking
    private String firstname;
    private  String lastname;
    private  int totalPrice;

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


    public String getFirstname() {
        return firstname;
    }


    public String getLastname() {
        return lastname;
    }


    public int getTotalprice() {
        return totalPrice;
    }
}
