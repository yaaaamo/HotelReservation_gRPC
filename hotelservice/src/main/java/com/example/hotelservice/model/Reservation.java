package com.example.hotelservice.model;

import java.time.LocalDate;
import java.util.UUID;

public class Reservation {
    private String referenceNumber;
    private Long roomId;
    private String agencyId;
    private String guestFirstName;
    private String guestLastName;
    private String guestEmail;
    private String guestPhone;
    private LocalDate startDate;
    private LocalDate endDate;
    private double totalPrice;
    private boolean confirmed;
    
    public Reservation() {
        this.referenceNumber = generateReference();
        this.confirmed = false;
    }
    
    private String generateReference() {
        return "RES-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }


    public String getReferenceNumber() { return referenceNumber; }
    public void setReferenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber; }
    
    public Long getRoomId() { return roomId; }
    public void setRoomId(Long roomId) { this.roomId = roomId; }
    
    public String getAgencyId() { return agencyId; }
    public void setAgencyId(String agencyId) { this.agencyId = agencyId; }
    
    public String getGuestFirstName() { return guestFirstName; }
    public void setGuestFirstName(String guestFirstName) { this.guestFirstName = guestFirstName; }
    
    public String getGuestLastName() { return guestLastName; }
    public void setGuestLastName(String guestLastName) { this.guestLastName = guestLastName; }
    
    public String getGuestEmail() { return guestEmail; }
    public void setGuestEmail(String guestEmail) { this.guestEmail = guestEmail; }
    
    public String getGuestPhone() { return guestPhone; }
    public void setGuestPhone(String guestPhone) { this.guestPhone = guestPhone; }
    
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    
    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }
    
    public boolean isConfirmed() { return confirmed; }
    public void setConfirmed(boolean confirmed) { this.confirmed = confirmed; }
    
    @Override
    public String toString() {
        return "Reservation{ref='" + referenceNumber + "', guest='" + guestFirstName + " " + guestLastName +
               "', dates=" + startDate + " to " + endDate + ", price=" + totalPrice + ", confirmed=" + confirmed + "}";
    }
}
