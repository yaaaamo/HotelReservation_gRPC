package com.example.hotelservice.services;

import com.example.hotelservice.model.*;
import com.example.hotelservice.model.Hotel;
import com.example.hotelservice.repository.*;
import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;
import jakarta.transaction.Transactional;
import net.devh.boot.grpc.server.service.GrpcService;
import org.hotel.grpc.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.List;

@GrpcService
@Transactional
public class HotelServiceImpl extends HotelServiceGrpc.HotelServiceImplBase {

  private static final Logger logger = LoggerFactory.getLogger(HotelServiceImpl.class);
  private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  private final HotelRepository hotelRepository;
  private final AgenceRepository agenceRepository;
  private final AvailabilityWindowRepository availabilityWindowRepository;
  private final ReservationRepository reservationRepository;
  private final ChambreRepository chambreRepository;

  public HotelServiceImpl(HotelRepository hotelRepository,
                          AgenceRepository agenceRepository,
                          AvailabilityWindowRepository availabilityWindowRepository,
                          ReservationRepository reservationRepository,
                          ChambreRepository chambreRepository) {
    this.hotelRepository = hotelRepository;
    this.agenceRepository = agenceRepository;
    this.availabilityWindowRepository = availabilityWindowRepository;
    this.reservationRepository = reservationRepository;
    this.chambreRepository = chambreRepository;
  }

  /**
   * Service 1: Consulter les disponibilités
   */
  @Override
  public void checkAvailability(AvailabilityRequest request,
                                StreamObserver<AvailabilityResponse> responseObserver) {
    logger.info("CheckAvailability request received from agency: {}",
            request.getCredentials().getAgencyId());

    AvailabilityResponse.Builder responseBuilder = AvailabilityResponse.newBuilder();

    try {
      // 1. Vérifier les credentials de l'agence
      Agence agence = validateAgency(request.getCredentials());
      if (agence == null) {
        responseObserver.onNext(responseBuilder
                .setSuccess(false)
                .setMessage("Authentification échouée: identifiants invalides")
                .build());
        responseObserver.onCompleted();
        return;
      }

      // 2. Parser les dates
      LocalDate startDate;
      LocalDate endDate;
      try {
        startDate = LocalDate.parse(request.getStartDate(), DATE_FORMAT);
        endDate = LocalDate.parse(request.getEndDate(), DATE_FORMAT);
      } catch (DateTimeParseException e) {
        responseObserver.onNext(responseBuilder
                .setSuccess(false)
                .setMessage("Format de date invalide. Utilisez YYYY-MM-DD")
                .build());
        responseObserver.onCompleted();
        return;
      }

      // 3. Valider les dates
      if (startDate.isAfter(endDate) || startDate.isBefore(LocalDate.now())) {
        responseObserver.onNext(responseBuilder
                .setSuccess(false)
                .setMessage("Dates invalides: la date de début doit être avant la date de fin et dans le futur")
                .build());
        responseObserver.onCompleted();
        return;
      }

      // 4. Récupérer l'hôtel
      Hotel hotel = hotelRepository.findAll().stream().findFirst().orElse(null);
      if (hotel == null) {
        responseObserver.onNext(responseBuilder
                .setSuccess(false)
                .setMessage("Aucun hôtel configuré")
                .build());
        responseObserver.onCompleted();
        return;
      }

      // 5. Construire HotelInfo
      HotelInfo hotelInfo = buildHotelInfo(hotel);
      responseBuilder.setHotelInfo(hotelInfo);

      // 6. Chercher les disponibilités
      List<AvailabilityWindow> windows = availabilityWindowRepository
              .findByStartDateLessThanEqualAndEndDateGreaterThanEqual(endDate, startDate);

      int numberOfGuests = request.getNumberOfGuests();

      // 7. Construire les offres
      for (AvailabilityWindow window : windows) {
        Chambre chambre = window.getChambre();

        // Verifier si la chambre peut accueillir le nombre de personnes
        if (chambre.getNombreLits() < numberOfGuests) {
          continue;
        }

        // Verifier s'il reste de la disponibilite
        long existingReservations = reservationRepository.countOverlappingReservations(
                chambre, startDate, endDate);

        int availableQuantity = window.getQuantity() - (int) existingReservations;
        if (availableQuantity <= 0) {
          continue;
        }

        // Prix de base par nuit (sans reduction)
        double basePricePerNight = chambre.getPrixParNuit();

        // Calculer le prix avec reduction agence
        double discountedPricePerNight = agence.calculerPrix(basePricePerNight);
        long nights = ChronoUnit.DAYS.between(startDate, endDate);
        double totalPrice = discountedPricePerNight * nights;

        RoomOffer.Builder offerBuilder = RoomOffer.newBuilder()
                .setOfferId(chambre.getId())
                .setRoomType(chambre.getTypeChambre())
                .setNumberOfBeds(chambre.getNombreLits())
                .setAvailabilityDate(request.getStartDate())
                .setPrice(totalPrice)
                .setQuantity(availableQuantity)
                .setPricePerNight(basePricePerNight);

        // Ajouter l'image si disponible
        if (chambre.getImageUrl() != null && !chambre.getImageUrl().isEmpty()) {
          offerBuilder.setImage(ByteString.copyFromUtf8(chambre.getImageUrl()));
        }

        responseBuilder.addOffers(offerBuilder.build());
      }

      responseBuilder.setSuccess(true);
      if (responseBuilder.getOffersCount() == 0) {
        responseBuilder.setMessage("Aucune chambre disponible pour les critères demandés");
      } else {
        responseBuilder.setMessage(responseBuilder.getOffersCount() + " offre(s) trouvée(s)");
      }

      logger.info("CheckAvailability: {} offers found", responseBuilder.getOffersCount());

    } catch (Exception e) {
      logger.error("Error in checkAvailability", e);
      responseBuilder.setSuccess(false)
              .setMessage("Erreur serveur: " + e.getMessage());
    }

    responseObserver.onNext(responseBuilder.build());
    responseObserver.onCompleted();
  }

  /**
   * Service 2: Effectuer une réservation
   */
  @Override
  public void makeReservation(ReservationRequest request,
                              StreamObserver<ReservationResponse> responseObserver) {
    logger.info("MakeReservation request received from agency: {}",
            request.getCredentials().getAgencyId());

    ReservationResponse.Builder responseBuilder = ReservationResponse.newBuilder();

    try {
      // 1. Vérifier les credentials de l'agence
      Agence agence = validateAgency(request.getCredentials());
      if (agence == null) {
        responseObserver.onNext(responseBuilder
                .setConfirmed(false)
                .setMessage("Authentification échouée: identifiants invalides")
                .build());
        responseObserver.onCompleted();
        return;
      }

      // 2. Parser les dates
      LocalDate startDate;
      LocalDate endDate;
      try {
        startDate = LocalDate.parse(request.getStartDate(), DATE_FORMAT);
        endDate = LocalDate.parse(request.getEndDate(), DATE_FORMAT);
      } catch (DateTimeParseException e) {
        responseObserver.onNext(responseBuilder
                .setConfirmed(false)
                .setMessage("Format de date invalide. Utilisez YYYY-MM-DD")
                .build());
        responseObserver.onCompleted();
        return;
      }

      // 3. Récupérer la chambre (offer_id = chambre_id)
      Chambre chambre = chambreRepository.findById(request.getOfferId()).orElse(null);
      if (chambre == null) {
        responseObserver.onNext(responseBuilder
                .setConfirmed(false)
                .setMessage("Offre invalide: chambre non trouvée")
                .build());
        responseObserver.onCompleted();
        return;
      }

      // 4. Vérifier la disponibilité
      List<AvailabilityWindow> windows = availabilityWindowRepository
              .findByStartDateLessThanEqualAndEndDateGreaterThanEqual(endDate, startDate);

      AvailabilityWindow matchingWindow = windows.stream()
              .filter(w -> w.getChambre().getId().equals(chambre.getId()))
              .findFirst()
              .orElse(null);

      if (matchingWindow == null) {
        responseObserver.onNext(responseBuilder
                .setConfirmed(false)
                .setMessage("La chambre n'est pas disponible pour cette période")
                .build());
        responseObserver.onCompleted();
        return;
      }

      // 5. Vérifier qu'il reste de la capacité
      long existingReservations = reservationRepository.countOverlappingReservations(
              chambre, startDate, endDate);

      if (existingReservations >= matchingWindow.getQuantity()) {
        responseObserver.onNext(responseBuilder
                .setConfirmed(false)
                .setMessage("La chambre n'est plus disponible pour cette période")
                .build());
        responseObserver.onCompleted();
        return;
      }

      // 6. Valider les informations du client
      GuestInfo guestInfo = request.getMainGuest();
      if (guestInfo.getFirstName().isEmpty() || guestInfo.getLastName().isEmpty()) {
        responseObserver.onNext(responseBuilder
                .setConfirmed(false)
                .setMessage("Informations client incomplètes")
                .build());
        responseObserver.onCompleted();
        return;
      }

      // 7. Valider le paiement (simulation)
      CreditCardInfo cardInfo = request.getPayment();
      if (cardInfo.getCardNumber().isEmpty() || cardInfo.getCvv().isEmpty()) {
        responseObserver.onNext(responseBuilder
                .setConfirmed(false)
                .setMessage("Informations de paiement incomplètes")
                .build());
        responseObserver.onCompleted();
        return;
      }

      // 8. Calculer le prix total
      double pricePerNight = agence.calculerPrix(chambre.getPrixParNuit());
      long nights = ChronoUnit.DAYS.between(startDate, endDate);
      double totalPrice = pricePerNight * nights;

      // 9. Créer la réservation
      Reservation reservation = new Reservation();
      reservation.genererReference();
      reservation.setDateArrivee(startDate);
      reservation.setDateDepart(endDate);
      reservation.setNomClient(guestInfo.getLastName());
      reservation.setPrenomClient(guestInfo.getFirstName());
      reservation.setEmailClient(guestInfo.getEmail());
      reservation.setTelephoneClient(guestInfo.getPhone());
      reservation.setMontantTotal(totalPrice);
      reservation.setAgenceId(agence.getId());
      reservation.setChambre(chambre);

      reservationRepository.save(reservation);

      logger.info("Reservation created: {} for {} nights, total: {}€",
              reservation.getReference(), nights, totalPrice);

      // 10. Construire la réponse
      responseBuilder.setConfirmed(true)
              .setReservationReference(reservation.getReference())
              .setTotalPrice(totalPrice)
              .setMessage("Réservation confirmée avec succès");

    } catch (Exception e) {
      logger.error("Error in makeReservation", e);
      responseBuilder.setConfirmed(false)
              .setMessage("Erreur serveur: " + e.getMessage());
    }

    responseObserver.onNext(responseBuilder.build());
    responseObserver.onCompleted();
  }

  /**
   * Service supplémentaire: Obtenir les informations de l'hôtel
   */
  @Override
  public void getHotelInfo(Empty request, StreamObserver<HotelInfo> responseObserver) {
    logger.info("GetHotelInfo request received");

    try {
      Hotel hotel = hotelRepository.findAll().stream().findFirst().orElse(null);

      if (hotel == null) {
        responseObserver.onError(new IllegalStateException("Aucun hôtel configuré"));
        return;
      }

      HotelInfo hotelInfo = buildHotelInfo(hotel);
      responseObserver.onNext(hotelInfo);

    } catch (Exception e) {
      logger.error("Error in getHotelInfo", e);
      responseObserver.onError(e);
    }

    responseObserver.onCompleted();
  }



  /**
   * Valide les credentials d'une agence
   */
  private Agence validateAgency(AgencyCredentials credentials) {
    if (credentials == null || credentials.getAgencyId().isEmpty()) {
      return null;
    }

    return agenceRepository.findById(credentials.getAgencyId())
            .filter(a -> a.validateCredentials(credentials.getAgencyId(), credentials.getPassword()))
            .orElse(null);
  }

  /**
   * Construit un objet HotelInfo gRPC à partir d'un Hotel JPA
   */
  private HotelInfo buildHotelInfo(Hotel hotel) {
    Address address = Address.newBuilder()
            .setCountry(hotel.getPays() != null ? hotel.getPays() : "")
            .setCity(hotel.getVille() != null ? hotel.getVille() : "")
            .setStreet(hotel.getRue() != null ? hotel.getRue() : "")
            .setNumber(hotel.getNumero() != null ? hotel.getNumero() : "")
            .setLocality(hotel.getLieuDit() != null ? hotel.getLieuDit() : "")
            .setLatitude(hotel.getLatitude() != null ? hotel.getLatitude() : 0.0)
            .setLongitude(hotel.getLongitude() != null ? hotel.getLongitude() : 0.0)
            .build();

    return HotelInfo.newBuilder()
            .setHotelId(hotel.getId())
            .setName(hotel.getNom())
            .setAddress(address)
            .setStars(hotel.getNombreEtoiles())
            .build();
  }
}