package com.example.agenceservice.config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Configuration des hôtels depuis application.properties.
 * Permet de définir dynamiquement la liste des hôtels disponibles.
 */
@Component
@ConfigurationProperties(prefix = "")
public class HotelsConfig {

  private List<HotelConfig> hotels = new ArrayList<>();

  public List<HotelConfig> getHotels() {
    return hotels;
  }

  public void setHotels(List<HotelConfig> hotels) {
    this.hotels = hotels;
  }

  /**
   * Configuration d'un hôtel individuel.
   */
  public static class HotelConfig {
    private String name;
    private String address;

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getAddress() {
      return address;
    }

    public void setAddress(String address) {
      this.address = address;
    }

    @Override
    public String toString() {
      return name + " (" + address + ")";
    }
  }
}
