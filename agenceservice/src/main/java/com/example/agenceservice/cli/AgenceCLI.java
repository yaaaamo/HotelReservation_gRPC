package com.example.agenceservice.cli;

import com.example.agenceservice.services.HotelServiceClient;
import io.grpc.StatusRuntimeException;
import org.hotel.grpc.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Interface en ligne de commande pour tester le client gRPC multi-hôtels.
 */
@Component
public class AgenceCLI implements CommandLineRunner {

  @Autowired
  private HotelServiceClient hotelServiceClient;

  @Value("${agency.name}")
  private String agencyName;

  @Override
  public void run(String... args) throws Exception {
    Scanner scanner = new Scanner(System.in);
    String choice;

    System.out.println("\n========================================");
    System.out.println("  Bienvenue dans l'Agence de Voyage");
    System.out.println("========================================");
    System.out.println("Agence: " + agencyName);
    System.out.println("Hôtels connectés: " + hotelServiceClient.getHotelCount());

    do {
      printMenu();
      choice = scanner.nextLine().trim();

      switch (choice) {
        case "1":
          listHotels();
          break;
        case "2":
          getHotelInfo(scanner);
          break;
        case "3":
          checkAvailability(scanner);
          break;
        case "4":
          checkAvailabilityAllHotels(scanner);
          break;
        case "5":
          makeReservation(scanner);
          break;
        case "q":
          System.out.println("Au revoir!");
          System.exit(0);
          break;
        default:
          System.out.println("Option invalide. Veuillez réessayer.");
      }
    } while (!choice.equalsIgnoreCase("q"));
  }

  private void printMenu() {
    System.out.println("\n--- Menu Principal ---");
    System.out.println("1. Lister les hôtels disponibles");
    System.out.println("2. Voir les informations d'un hôtel");
    System.out.println("3. Consulter les disponibilités (1 hôtel)");
    System.out.println("4. Consulter les disponibilités (TOUS les hôtels)");
    System.out.println("5. Effectuer une réservation");
    System.out.println("q. Quitter");
    System.out.print("\nVotre choix: ");
  }

  private void listHotels() {
    System.out.println("\n--- Hôtels Disponibles ---");
    List<String> hotels = hotelServiceClient.getAvailableHotels();

    if (hotels.isEmpty()) {
      System.out.println("Aucun hôtel connecté.");
      return;
    }

    for (int i = 0; i < hotels.size(); i++) {
      System.out.printf("  %d. %s%n", i + 1, hotels.get(i));
    }
    System.out.println("\nTotal: " + hotels.size() + " hôtel(s)");
  }

  private String selectHotel(Scanner scanner) {
    List<String> hotels = hotelServiceClient.getAvailableHotels();

    if (hotels.isEmpty()) {
      System.out.println("Aucun hôtel connecté.");
      return null;
    }

    System.out.println("\nSélectionnez un hôtel:");
    for (int i = 0; i < hotels.size(); i++) {
      System.out.printf("  %d. %s%n", i + 1, hotels.get(i));
    }
    System.out.print("Numéro de l'hôtel: ");

    try {
      int index = Integer.parseInt(scanner.nextLine().trim()) - 1;
      if (index >= 0 && index < hotels.size()) {
        return hotels.get(index);
      } else {
        System.out.println("Numéro invalide.");
        return null;
      }
    } catch (NumberFormatException e) {
      System.out.println("Entrée invalide.");
      return null;
    }
  }

  private void getHotelInfo(Scanner scanner) {
    System.out.println("\n--- Informations de l'hôtel ---");

    String hotelName = selectHotel(scanner);
    if (hotelName == null) return;

    try {
      HotelInfo info = hotelServiceClient.getHotelInfo(hotelName);
      printHotelInfo(info);
    } catch (StatusRuntimeException e) {
      System.err.println("Erreur: " + e.getStatus().getDescription());
    } catch (IllegalArgumentException e) {
      System.err.println("Erreur: " + e.getMessage());
    }
  }

  private void printHotelInfo(HotelInfo info) {
    System.out.println("Nom: " + info.getName());
    System.out.println("Étoiles: " + "⭐".repeat(info.getStars()));
    System.out.println("Adresse: " + info.getAddress().getNumber() + " " +
            info.getAddress().getStreet() + ", " +
            info.getAddress().getCity() + ", " +
            info.getAddress().getCountry());
    System.out.println("Coordonnées: " + info.getAddress().getLatitude() +
            ", " + info.getAddress().getLongitude());
  }

  private void checkAvailability(Scanner scanner) {
    System.out.println("\n--- Consulter les disponibilités (1 hôtel) ---");

    String hotelName = selectHotel(scanner);
    if (hotelName == null) return;

    try {
      System.out.print("Date d'arrivée (YYYY-MM-DD): ");
      String startDate = scanner.nextLine().trim();

      System.out.print("Date de départ (YYYY-MM-DD): ");
      String endDate = scanner.nextLine().trim();

      System.out.print("Nombre de personnes: ");
      int guests = Integer.parseInt(scanner.nextLine().trim());

      AvailabilityResponse response = hotelServiceClient.checkAvailability(
              hotelName, startDate, endDate, guests);

      printAvailabilityResponse(hotelName, response);

    } catch (NumberFormatException e) {
      System.err.println("Erreur: Nombre de personnes invalide");
    } catch (StatusRuntimeException e) {
      System.err.println("Erreur gRPC: " + e.getStatus().getDescription());
    }
  }

  private void checkAvailabilityAllHotels(Scanner scanner) {
    System.out.println("\n--- Consulter les disponibilités (TOUS les hôtels) ---");

    try {
      System.out.print("Date d'arrivée (YYYY-MM-DD): ");
      String startDate = scanner.nextLine().trim();

      System.out.print("Date de départ (YYYY-MM-DD): ");
      String endDate = scanner.nextLine().trim();

      System.out.print("Nombre de personnes: ");
      int guests = Integer.parseInt(scanner.nextLine().trim());

      System.out.println("\n🔍 Recherche en cours sur " + hotelServiceClient.getHotelCount() + " hôtels...\n");

      Map<String, AvailabilityResponse> allResponses = hotelServiceClient.checkAvailabilityAllHotels(
              startDate, endDate, guests);

      int totalOffers = 0;
      for (Map.Entry<String, AvailabilityResponse> entry : allResponses.entrySet()) {
        printAvailabilityResponse(entry.getKey(), entry.getValue());
        totalOffers += entry.getValue().getOffersCount();
        System.out.println();
      }

      System.out.println("═══════════════════════════════════════");
      System.out.printf("📊 TOTAL: %d offre(s) trouvée(s) sur %d hôtel(s)%n",
              totalOffers, allResponses.size());

    } catch (NumberFormatException e) {
      System.err.println("Erreur: Nombre de personnes invalide");
    }
  }

  private void printAvailabilityResponse(String hotelName, AvailabilityResponse response) {
    System.out.println("═══════════════════════════════════════");
    System.out.println("🏨 " + hotelName);
    System.out.println("═══════════════════════════════════════");

    if (response.getSuccess()) {
      System.out.println("✅ " + response.getMessage());

      if (response.getOffersCount() > 0) {
        System.out.println("\nOffres disponibles:");
        System.out.println("-------------------");
        for (RoomOffer offer : response.getOffersList()) {
          System.out.printf("  ID: %d | Type: %s | Lits: %d | Prix: %.2f€%n",
                  offer.getOfferId(),
                  offer.getRoomType(),
                  offer.getNumberOfBeds(),
                  offer.getPrice());
        }
      }
    } else {
      System.out.println("❌ " + response.getMessage());
    }
  }

  private void makeReservation(Scanner scanner) {
    System.out.println("\n--- Effectuer une réservation ---");

    String hotelName = selectHotel(scanner);
    if (hotelName == null) return;

    try {
      System.out.print("ID de l'offre (chambre): ");
      long offerId = Long.parseLong(scanner.nextLine().trim());

      System.out.print("Date d'arrivée (YYYY-MM-DD): ");
      String startDate = scanner.nextLine().trim();

      System.out.print("Date de départ (YYYY-MM-DD): ");
      String endDate = scanner.nextLine().trim();

      System.out.println("\n-- Informations du client --");
      System.out.print("Prénom: ");
      String firstName = scanner.nextLine().trim();

      System.out.print("Nom: ");
      String lastName = scanner.nextLine().trim();

      System.out.print("Email: ");
      String email = scanner.nextLine().trim();

      System.out.print("Téléphone: ");
      String phone = scanner.nextLine().trim();

      // Construire GuestInfo (comme dans le proto)
      GuestInfo guestInfo = GuestInfo.newBuilder()
              .setFirstName(firstName)
              .setLastName(lastName)
              .setEmail(email)
              .setPhone(phone)
              .build();

      System.out.println("\n-- Informations de paiement --");
      System.out.print("Numéro de carte: ");
      String cardNumber = scanner.nextLine().trim();

      System.out.print("Titulaire: ");
      String cardHolder = scanner.nextLine().trim();

      System.out.print("Date d'expiration (MM/YY): ");
      String expiryDate = scanner.nextLine().trim();

      System.out.print("CVV: ");
      String cvv = scanner.nextLine().trim();


      CreditCardInfo paymentInfo = CreditCardInfo.newBuilder()
              .setCardNumber(cardNumber)
              .setCardHolderName(cardHolder)
              .setExpiryDate(expiryDate)
              .setCvv(cvv)
              .build();

      ReservationResponse response = hotelServiceClient.makeReservation(
              hotelName, offerId, guestInfo, paymentInfo, startDate, endDate);

      if (response.getConfirmed()) {
        System.out.println("\n✅ " + response.getMessage());
        System.out.println("🏨 Hôtel: " + hotelName);
        System.out.println("📋 Référence: " + response.getReservationReference());
        System.out.printf("💰 Prix total: %.2f€%n", response.getTotalPrice());
      } else {
        System.out.println("\n❌ " + response.getMessage());
      }

    } catch (NumberFormatException e) {
      System.err.println("Erreur: ID d'offre invalide");
    } catch (StatusRuntimeException e) {
      System.err.println("Erreur gRPC: " + e.getStatus().getDescription());
    } catch (IllegalArgumentException e) {
      System.err.println("Erreur: " + e.getMessage());
    }
  }
}