package com.example.agenceservice.services;

import com.example.agenceservice.auth.AuthInterceptor;
import com.example.agenceservice.config.HotelsConfig;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
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

  private static final long INFO_TIMEOUT_SEC = 5;
  private static final long AVAIL_TIMEOUT_SEC = 10;
  private static final long RES_TIMEOUT_SEC = 30;
  private static final long ONLINE_TIMEOUT_SEC = 2;

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
                .intercept(new AuthInterceptor(agencyId, agencyPassword))
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

  public List<String> getAvailableHotels() {
    return new ArrayList<>(hotelStubs.keySet());
  }

  public int getHotelCount() {
    return hotelStubs.size();
  }

  /**
   * Get online status for all hotels
   */
  public Map<String, HotelStatus> getHotelsOnlineStatus() {
    Map<String, HotelStatus> status = new HashMap<>();
    for (String hotelName : hotelStubs.keySet()) {
      status.put(hotelName, getHotelStatus(hotelName));
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

  /**
   * GetHotelInfo - Récupère les informations d'un hôtel
   */
  public HotelInfo getHotelInfo(String hotelName) {
    logger.info("getHotelInfo({})", hotelName);

    try {
      return getStub(hotelName)
              .withDeadlineAfter(INFO_TIMEOUT_SEC, TimeUnit.SECONDS)
              .getHotelInfo(Empty.newBuilder().build());
    } catch (StatusRuntimeException e) {
      Status.Code code = e.getStatus().getCode();
      String desc = e.getStatus().getDescription();


      logger.debug("getHotelInfo failed (hotel={} code={} desc={})", hotelName, code, desc);

      throw e;
    }
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
            .setStartDate(startDate)
            .setEndDate(endDate)
            .setNumberOfGuests(numberOfGuests)
            .build();

    try {
      return getStub(hotelName)
              .withDeadlineAfter(AVAIL_TIMEOUT_SEC, TimeUnit.SECONDS)
              .checkAvailability(request);
    } catch (StatusRuntimeException e) {

      logger.debug("checkAvailability failed (hotel={} code={} desc={})",
              hotelName, e.getStatus().getCode(), e.getStatus().getDescription());
      throw e;
    }
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
            .setOfferId(offerId)
            .setMainGuest(guestInfo)
            .setPayment(paymentInfo)
            .setStartDate(startDate)
            .setEndDate(endDate)
            .build();

    try {
      return getStub(hotelName)
              .withDeadlineAfter(RES_TIMEOUT_SEC, TimeUnit.SECONDS)
              .makeReservation(request);
    } catch (StatusRuntimeException e) {

      logger.debug("makeReservation failed (hotel={} offerId={} code={} desc={})",
              hotelName, offerId, e.getStatus().getCode(), e.getStatus().getDescription());
      throw e;
    }
  }

  /**
   * GetHotelInfo pour tous les hôtels
   */
  public Map<String, HotelInfo> getAllHotelInfos() {
    Map<String, HotelInfo> results = new HashMap<>();

    for (String hotelName : hotelStubs.keySet()) {
      try {
        results.put(hotelName, getHotelInfo(hotelName));

      } catch (StatusRuntimeException e) {

        logger.debug("Could not get info for hotel={} (code={} desc={})",
                hotelName, e.getStatus().getCode(), e.getStatus().getDescription());
        results.put(hotelName, buildPlaceholderHotelInfo(hotelName));

      } catch (Exception e) {
        logger.debug("Could not get info for hotel={} (error={}): {}",
                hotelName, e.getClass().getSimpleName(), e.getMessage());
        results.put(hotelName, buildPlaceholderHotelInfo(hotelName));
      }
    }

    return results;
  }

  private HotelInfo buildPlaceholderHotelInfo(String hotelName) {
    return HotelInfo.newBuilder()
            .setHotelId(-1)
            .setName(hotelName)
            .setStars(0)
            .setAddress(Address.newBuilder()
                    .setCity("")
                    .setCountry("")
                    .setStreet("")
                    .setNumber("")
                    .build())
            .build();
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
      } catch (StatusRuntimeException e) {
        Status.Code code = e.getStatus().getCode();

        logger.debug("Could not check availability for hotel={} (code={} desc={})",
                hotelName, code, e.getStatus().getDescription());
      } catch (Exception e) {
        logger.debug("Could not check availability for hotel={} (error={}): {}",
                hotelName, e.getClass().getSimpleName(), e.getMessage());
      }
    }
    return results;
  }

  public HotelStatus getHotelStatus(String hotelName) {
    try {
      getStub(hotelName)
              .withDeadlineAfter(ONLINE_TIMEOUT_SEC, TimeUnit.SECONDS)
              .getHotelInfo(Empty.newBuilder().build());
      return HotelStatus.ONLINE;

    } catch (StatusRuntimeException e) {
      Status.Code code = e.getStatus().getCode();

      return switch (code) {
        case UNAUTHENTICATED -> HotelStatus.AUTH_FAILED;
        case UNAVAILABLE -> HotelStatus.OFFLINE;
        case DEADLINE_EXCEEDED -> HotelStatus.TIMEOUT;
        default -> HotelStatus.ERROR;
      };
    }
  }

  public Map<String, String> getHotelWarnings() {
    Map<String, String> warnings = new HashMap<>();

    for (String hotelName : hotelStubs.keySet()) {
      try {
        getStub(hotelName)
                .withDeadlineAfter(ONLINE_TIMEOUT_SEC, TimeUnit.SECONDS)
                .getHotelInfo(Empty.newBuilder().build());
      } catch (StatusRuntimeException e) {
        Status.Code code = e.getStatus().getCode();

        if (code == Status.Code.UNAVAILABLE) {
          warnings.put(hotelName, "Hotel unreachable (offline)");
        } else if (code == Status.Code.DEADLINE_EXCEEDED) {
          warnings.put(hotelName, "Hotel timeout");
        } else if (code == Status.Code.UNAUTHENTICATED) {
          warnings.put(hotelName, "Authentication failed");
        } else {
          warnings.put(hotelName, "Hotel error: " + code);
        }
      }
    }
    return warnings;
  }
}
