package com.example.agenceservice.controller;

import com.example.agenceservice.services.HotelServiceClient;
import org.hotel.grpc.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
public class AgenceController {

  private final HotelServiceClient hotelServiceClient;

  @Value("${agency.name}")
  private String agencyName;

  public AgenceController(HotelServiceClient hotelServiceClient) {
    this.hotelServiceClient = hotelServiceClient;
  }

  @GetMapping("/")
  public String index(Model model) {
    model.addAttribute("agencyName", agencyName);
    model.addAttribute("hotels", hotelServiceClient.getAvailableHotels());
    return "index";
  }

  @GetMapping("/hotels")
  public String hotels(Model model) {
    model.addAttribute("hotels", hotelServiceClient.getAllHotelInfos());
    model.addAttribute("onlineStatus", hotelServiceClient.getHotelsOnlineStatus());
    model.addAttribute("hotelWarnings", hotelServiceClient.getHotelWarnings());
    return "hotels";
  }

  @GetMapping("/search")
  public String searchForm(Model model) {
    model.addAttribute("hotels", hotelServiceClient.getAvailableHotels());
    return "search";
  }

  @PostMapping("/search")
  public String searchResults(
          @RequestParam String startDate,
          @RequestParam String endDate,
          @RequestParam int guests,
          @RequestParam(required = false) String hotelName,
          @RequestParam(required = false, defaultValue = "0") double minPrice,
          @RequestParam(required = false, defaultValue = "10000") double maxPrice,
          Model model) {

    model.addAttribute("startDate", startDate);
    model.addAttribute("endDate", endDate);
    model.addAttribute("guests", guests);
    model.addAttribute("minPrice", minPrice);
    model.addAttribute("maxPrice", maxPrice);

    model.addAttribute("hotelWarnings", hotelServiceClient.getHotelWarnings());

    if (hotelName != null && !hotelName.isEmpty()) {
      AvailabilityResponse response = hotelServiceClient.checkAvailability(
              hotelName, startDate, endDate, guests);

      model.addAttribute("singleResult", response);
      model.addAttribute("hotelName", hotelName);


      if (!response.getSuccess()) {
        model.addAttribute("errorMessage", response.getMessage());
      }

    } else {
      Map<String, AvailabilityResponse> results = hotelServiceClient.checkAvailabilityAllHotels(
              startDate, endDate, guests);

      model.addAttribute("results", results);


      boolean hasAnySuccess = results.values().stream().anyMatch(AvailabilityResponse::getSuccess);
      if (!hasAnySuccess) {
        model.addAttribute("errorMessage", "Aucune disponibilité trouvée pour ces dates.");
      }
    }

    return "results";
  }


  @GetMapping("/reserve/{hotelName}/{offerId}")
  public String reserveForm(
          @PathVariable String hotelName,
          @PathVariable long offerId,
          @RequestParam String startDate,
          @RequestParam String endDate,
          @RequestParam double price,
          @RequestParam String roomType,
          Model model) {

    model.addAttribute("hotelName", hotelName);
    model.addAttribute("offerId", offerId);
    model.addAttribute("startDate", startDate);
    model.addAttribute("endDate", endDate);
    model.addAttribute("price", price);
    model.addAttribute("roomType", roomType);
    return "reserve";
  }

  @PostMapping("/reserve")
  public String reserve(
          @RequestParam String hotelName,
          @RequestParam long offerId,
          @RequestParam String startDate,
          @RequestParam String endDate,
          @RequestParam String firstName,
          @RequestParam String lastName,
          @RequestParam String email,
          @RequestParam String phone,
          @RequestParam String cardNumber,
          @RequestParam String cardHolder,
          @RequestParam String expiryDate,
          @RequestParam String cvv,
          Model model) {

    GuestInfo guestInfo = GuestInfo.newBuilder()
            .setFirstName(firstName)
            .setLastName(lastName)
            .setEmail(email)
            .setPhone(phone)
            .build();

    CreditCardInfo paymentInfo = CreditCardInfo.newBuilder()
            .setCardNumber(cardNumber)
            .setCardHolderName(cardHolder)
            .setExpiryDate(expiryDate)
            .setCvv(cvv)
            .build();

    ReservationResponse response = hotelServiceClient.makeReservation(
            hotelName, offerId, guestInfo, paymentInfo, startDate, endDate);

    model.addAttribute("response", response);
    model.addAttribute("hotelName", hotelName);
    return "confirmation";
  }
}