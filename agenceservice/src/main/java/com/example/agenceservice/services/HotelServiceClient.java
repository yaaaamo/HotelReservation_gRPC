package com.example.agenceservice.services;

import com.example.agenceservice.config.HotelsConfig;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.hotel.grpc.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Client gRPC pour consommer les services de plusieurs hôtels.
 * Les noms des méthodes correspondent aux noms définis dans le fichier .proto
 */
@Service
public class HotelServiceClient {

  private static final Logger logger = LoggerFactory.getLogger(HotelServiceClient.class);

  private final HotelsConfig hotelsConfig;

  @Value("${agency.id}")
  private String agencyId;

  @Value("${agency.password}")
  private String agencyPassword;

  private final Map<String, HotelServiceGrpc.HotelServiceBlockingStub> hotelStubs = new HashMap<>();
  private final Map<String, ManagedChannel> channels = new HashMap<>();

  public HotelServiceClient(HotelsConfig hotelsConfig) {
    this.hotelsConfig = hotelsConfig;
  }

  @PostConstruct
  public void init() {
    logger.info("Initializing gRPC connections to {} hotels...", hotelsConfig.getHotels().size());

    for (HotelsConfig.HotelConfig hotelConfig : hotelsConfig.getHotels()) {
      try {
        String address = hotelConfig.getAddress();
        String target = address.replace("static://", "");
        String[] parts = target.split(":");
        String host = parts[0];
        int port = Integer.parseInt(parts[1]);

        ManagedChannel channel = ManagedChannelBuilder
                .forAddress(host, port)
                .usePlaintext()
                .build();

        HotelServiceGrpc.HotelServiceBlockingStub stub = HotelServiceGrpc.newBlockingStub(channel);

        channels.put(hotelConfig.getName(), channel);
        hotelStubs.put(hotelConfig.getName(), stub);

        logger.info("Connected to hotel: {} at {}", hotelConfig.getName(), target);
      } catch (Exception e) {
        logger.error("Failed to connect to hotel: {}", hotelConfig.getName(), e);
      }
    }
  }

  @PreDestroy
  public void shutdown() {
    logger.info("Shutting down gRPC connections...");
    for (Map.Entry<String, ManagedChannel> entry : channels.entrySet()) {
      try {
        entry.getValue().shutdown().awaitTermination(5, TimeUnit.SECONDS);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    }
  }

  // ============= Utility Methods =============

  public List<String> getAvailableHotels() {
    return new ArrayList<>(hotelStubs.keySet());
  }

  public int getHotelCount() {
    return hotelStubs.size();
  }

  /**
   * Check if hotel is online/reachable
   */
  public boolean isHotelOnline(String hotelName) {
    try {
      getStub(hotelName).getHotelInfo(Empty.newBuilder().build());
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * Get online status for all hotels
   */
  public Map<String, Boolean> getHotelsOnlineStatus() {
    Map<String, Boolean> status = new HashMap<>();
    for (String hotelName : hotelStubs.keySet()) {
      status.put(hotelName, isHotelOnline(hotelName));
    }
    return status;
  }

  private HotelServiceGrpc.HotelServiceBlockingStub getStub(String hotelName) {
    HotelServiceGrpc.HotelServiceBlockingStub stub = hotelStubs.get(hotelName);
    if (stub == null) {
      throw new IllegalArgumentException("Hotel not found: " + hotelName);
    }
    return stub;
  }

  private AgencyCredentials getCredentials() {
    return AgencyCredentials.newBuilder()
            .setAgencyId(agencyId)
            .setPassword(agencyPassword)
            .build();
  }

  // ============= gRPC Methods (same names as proto) =============

  /**
   * GetHotelInfo - Récupère les informations d'un hôtel
   */
  public HotelInfo getHotelInfo(String hotelName) {
    logger.info("getHotelInfo({})", hotelName);
    return getStub(hotelName).getHotelInfo(Empty.newBuilder().build());
  }

  /**
   * CheckAvailability - Vérifie les disponibilités
   */
  public AvailabilityResponse checkAvailability(String hotelName,
                                                String startDate,
                                                String endDate,
                                                int numberOfGuests) {
    logger.info("checkAvailability({}, {}, {}, {})", hotelName, startDate, endDate, numberOfGuests);

    AvailabilityRequest request = AvailabilityRequest.newBuilder()
            .setCredentials(getCredentials())
            .setStartDate(startDate)
            .setEndDate(endDate)
            .setNumberOfGuests(numberOfGuests)
            .build();

    return getStub(hotelName).checkAvailability(request);
  }

  /**
   * MakeReservation - Effectue une réservation
   */
  public ReservationResponse makeReservation(String hotelName,
                                             long offerId,
                                             GuestInfo guestInfo,
                                             CreditCardInfo paymentInfo,
                                             String startDate,
                                             String endDate) {
    logger.info("makeReservation({}, offerId={})", hotelName, offerId);

    ReservationRequest request = ReservationRequest.newBuilder()
            .setCredentials(getCredentials())
            .setOfferId(offerId)
            .setMainGuest(guestInfo)
            .setPayment(paymentInfo)
            .setStartDate(startDate)
            .setEndDate(endDate)
            .build();

    return getStub(hotelName).makeReservation(request);
  }

  // ============= Convenience Methods for All Hotels =============

  /**
   * GetHotelInfo pour tous les hôtels
   */
  public Map<String, HotelInfo> getAllHotelInfos() {
    Map<String, HotelInfo> results = new HashMap<>();
    for (String hotelName : hotelStubs.keySet()) {
      try {
        results.put(hotelName, getHotelInfo(hotelName));
      } catch (Exception e) {
        logger.warn("Could not get info for hotel: {}", hotelName);
      }
    }
    return results;
  }

  /**
   * CheckAvailability pour tous les hôtels
   */
  public Map<String, AvailabilityResponse> checkAvailabilityAllHotels(String startDate,
                                                                      String endDate,
                                                                      int numberOfGuests) {
    Map<String, AvailabilityResponse> results = new HashMap<>();
    for (String hotelName : hotelStubs.keySet()) {
      try {
        results.put(hotelName, checkAvailability(hotelName, startDate, endDate, numberOfGuests));
      } catch (Exception e) {
        logger.warn("Could not check availability for hotel: {}", hotelName);
      }
    }
    return results;
  }
}