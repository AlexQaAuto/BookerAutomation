package core.models;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;


public class Booking {
    private int bookingid;
    private String firstname;
    private String lastname;
    private double totalprice;
    private boolean depositpaid;
    private BookingDates bookingdates;
    private String additionalneeds;


    //Конструктор
    @JsonCreator
    public Booking(
            @JsonProperty("firstname") String firstname,
            @JsonProperty("lastname") String lastname,
            @JsonProperty("totalprice") double totalprice,
            @JsonProperty("depositpaid") boolean depositpaid,
            @JsonProperty("bookingdates") BookingDates bookingdates,
            @JsonProperty("additionalneeds") String additionalneeds


    ) {


    }
    public String getFirstname () {
        return firstname;
    }

    public void setFirstname ( String firstname){
        this.firstname = firstname;
    }

    public String getLastname () {
        return lastname;
    }

    public void setLastname ( String lastname){
        this.lastname = lastname;
    }

    public double getTotalprice () {
        return totalprice;
    }

    public void setTotalprice ( double totalprice){
        this.totalprice = totalprice;
    }

    public boolean isDepositpaid () {
        return depositpaid;
    }

    public void setDepositpaid ( boolean depositpaid){
        this.depositpaid = depositpaid;
    }

    public BookingDates getBookingdates () {
        return bookingdates;
    }

    public void setBookingdates (BookingDates bookingdates){
        this.bookingdates = bookingdates;
    }

    public String getAdditionalneeds () {
        return additionalneeds;
    }

    public void setAdditionalneeds (String additionalneeds){
        this.additionalneeds = additionalneeds;
    }

    //Геттер и сеттер
    public int getBookingid () {

        return bookingid;
    }

    public void setBookingid ( int bookingid){

        this.bookingid = bookingid;
    }


    public static class BookingDates {
        private Date checkin;
        private Date checkout;


        //Геттер и сеттер
        public Date getCheckin() {
            return checkin;
        }

        public void setCheckin(Date checkin) {
            this.checkin = checkin;
        }

        public Date getCheckout() {
            return checkout;
        }

        public void setCheckout(Date checkout) {
            this.checkout = checkout;
        }
    }
}
