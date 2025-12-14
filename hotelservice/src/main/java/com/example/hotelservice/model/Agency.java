package com.example.hotelservice.model;


public class Agency {
    private String agencyId;
    private String password;
    private String name;
    private double discountPercentage;
    
    public Agency() {}
    
    public Agency(String agencyId, String password, String name, double discountPercentage) {
        this.agencyId = agencyId;
        this.password = password;
        this.name = name;
        this.discountPercentage = discountPercentage;
    }


    public String getAgencyId() { return agencyId; }
    public void setAgencyId(String agencyId) { this.agencyId = agencyId; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public double getDiscountPercentage() { return discountPercentage; }
    public void setDiscountPercentage(double discountPercentage) { this.discountPercentage = discountPercentage; }
    

    public double calculatePrice(double basePrice) {
        return basePrice * (1 - discountPercentage);
    }
    

    public boolean validateCredentials(String id, String pwd) {
        return this.agencyId.equals(id) && this.password.equals(pwd);
    }
    
    @Override
    public String toString() {
        return "Agency{id='" + agencyId + "', name='" + name + 
               "', discount=" + (discountPercentage * 100) + "%}";
    }
}
