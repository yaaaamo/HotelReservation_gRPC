package com.example.hotelservice.model;


public class Room {
    private Long id;
    private String roomType;
    private int numberOfBeds;
    private double basePrice;
    private boolean available;
    private byte[] image;
    
    public Room() {}
    
    public Room(Long id, String roomType, int numberOfBeds, double basePrice, boolean available) {
        this.id = id;
        this.roomType = roomType;
        this.numberOfBeds = numberOfBeds;
        this.basePrice = basePrice;
        this.available = available;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getRoomType() { return roomType; }
    public void setRoomType(String roomType) { this.roomType = roomType; }
    
    public int getNumberOfBeds() { return numberOfBeds; }
    public void setNumberOfBeds(int numberOfBeds) { this.numberOfBeds = numberOfBeds; }
    
    public double getBasePrice() { return basePrice; }
    public void setBasePrice(double basePrice) { this.basePrice = basePrice; }
    
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
    
    public byte[] getImage() { return image; }
    public void setImage(byte[] image) { this.image = image; }
    
    @Override
    public String toString() {
        return "Room{id=" + id + ", type='" + roomType + "', beds=" + numberOfBeds + 
               ", price=" + basePrice + ", available=" + available + "}";
    }
}
