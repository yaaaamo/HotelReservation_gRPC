package com.example.agenceservice.controller;
import com.example.agenceservice.services.HotelServiceClient;
import org.hotel.grpc.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
          Model model) {

    model.addAttribute("startDate", startDate);
    model.addAttribute("endDate", endDate);
    model.addAttribute("guests", guests);

    if (hotelName != null && !hotelName.isEmpty()) {
      // Single hotel search
      AvailabilityResponse response = hotelServiceClient.checkAvailability(
              hotelName, startDate, endDate, guests);
      model.addAttribute("singleResult", response);
      model.addAttribute("hotelName", hotelName);
    } else {
      // All hotels search
      Map<String, AvailabilityResponse> results = hotelServiceClient.checkAvailabilityAllHotels(
              startDate, endDate, guests);
      model.addAttribute("results", results);
    }

    return "results";
  }

  @GetMapping("/reserve/{hotelName}/{offerId}")
  public String reserveForm(
          @PathVariable String hotelName,
          @PathVariable long offerId,
          @RequestParam String startDate,
          @RequestParam String endDate,
          Model model) {

    model.addAttribute("hotelName", hotelName);
    model.addAttribute("offerId", offerId);
    model.addAttribute("startDate", startDate);
    model.addAttribute("endDate", endDate);
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
