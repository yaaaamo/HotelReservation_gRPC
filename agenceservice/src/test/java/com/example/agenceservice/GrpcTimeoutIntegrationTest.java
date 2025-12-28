package com.example.agenceservice;
import com.example.agenceservice.auth.AuthInterceptor;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.hotel.grpc.AvailabilityRequest;
import org.hotel.grpc.AvailabilityResponse;
import org.hotel.grpc.HotelServiceGrpc;
import org.junit.jupiter.api.*;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test d'intégration pour valider la gestion des timeouts gRPC.
 *
 * PRÉREQUIS avant d'exécuter ces tests :
 *
 * 1. Lancer hotelservice avec le profil "slow" pour simuler un serveur lent :
 *    cd hotelservice
 *    ./mvnw spring-boot:run -Dspring-boot.run.profiles=h1,slow
 *
 * 2. Exécuter les tests :
 *    cd agenceservice
 *    ./mvnw test -Dtest=GrpcTimeoutIntegrationTest
 */
@Disabled("Test manuel - nécessite hotelservice avec profil slow")
public class GrpcTimeoutIntegrationTest {

  private ManagedChannel channel;
  private HotelServiceGrpc.HotelServiceBlockingStub stub;

  // Configuration
  private static final String HOST = "localhost";
  private static final int PORT = 9091;
  private static final String AGENCY_ID = "AGENCE1";
  private static final String AGENCY_PASSWORD = "secret1";
  private static final int SHORT_TIMEOUT_SECONDS = 2;

  @BeforeEach
  void setUp() {

    System.out.println("Initialisation du client gRPC de test");
    System.out.println("Serveur cible: " + HOST + ":" + PORT);


    channel = ManagedChannelBuilder
            .forAddress(HOST, PORT)
            .usePlaintext()
            .intercept(new AuthInterceptor(AGENCY_ID, AGENCY_PASSWORD))
            .build();

    stub = HotelServiceGrpc.newBlockingStub(channel);
  }

  @AfterEach
  void tearDown() throws InterruptedException {
    if (channel != null) {
      channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }
  }

  @Test
  @DisplayName("Test: Timeout déclenché quand le serveur est trop lent")
  void testDeadlineExceeded() {
    System.out.println("=== TEST: DEADLINE_EXCEEDED ===");
    System.out.println("Configuration: timeout de " + SHORT_TIMEOUT_SECONDS + " secondes");
    System.out.println("Attendu: Le serveur (avec profil slow) met 15s à répondre");
    System.out.println("         -> Le client doit recevoir DEADLINE_EXCEEDED\n");

    AvailabilityRequest request = AvailabilityRequest.newBuilder()
            .setStartDate("2027-12-15")
            .setEndDate("2027-12-18")
            .setNumberOfGuests(2)
            .build();

    long startTime = System.currentTimeMillis();

    try {
      System.out.println("Envoi de la requête CheckAvailability...");

      AvailabilityResponse response = stub
              .withDeadlineAfter(SHORT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
              .checkAvailability(request);

      // Si on arrive ici, le serveur a répondu à temps (pas de profil slow)
      long duration = System.currentTimeMillis() - startTime;
      System.out.println("  Réponse reçue en " + duration + "ms");
      System.out.println("️  Le serveur n'est probablement PAS lancé avec le profil 'slow'");
      System.out.println("    Relancez avec: ./mvnw spring-boot:run -Dspring-boot.run.profiles=h1,slow");

      fail("Le timeout aurait dû être déclenché. Lancez hotelservice avec le profil 'slow'.");

    } catch (StatusRuntimeException e) {
      long duration = System.currentTimeMillis() - startTime;
      Status.Code code = e.getStatus().getCode();

      System.out.println("Exception capturée après " + duration + "ms");
      System.out.println("Code de statut: " + code);
      System.out.println("Description: " + e.getStatus().getDescription());

      if (code == Status.Code.DEADLINE_EXCEEDED) {
        System.out.println("\nTEST RÉUSSI: Timeout correctement géré!");
        System.out.println("   Le client a interrompu l'appel après " + SHORT_TIMEOUT_SECONDS + "s");
      } else if (code == Status.Code.UNAVAILABLE) {
        System.out.println("\nLe serveur est injoignable.");
        System.out.println("   Lancez: ./mvnw spring-boot:run -Dspring-boot.run.profiles=h1,slow");
        fail("Serveur non disponible. Lancez hotelservice avec le profil 'h1,slow'.");
      }

      assertEquals(Status.Code.DEADLINE_EXCEEDED, code,
              "Le code de statut devrait être DEADLINE_EXCEEDED");

      // Vérifier que le timeout a bien été respecté (avec marge de 500ms)
      assertTrue(duration < (SHORT_TIMEOUT_SECONDS * 1000) + 500,
              "Le timeout devrait être respecté (duration: " + duration + "ms)");
    }
  }

  @Test
  @DisplayName("Test: Serveur répond normalement sans profil slow")
  void testNormalResponse() {
    System.out.println("=== TEST: RÉPONSE NORMALE ===");
    System.out.println("Ce test vérifie qu'un serveur normal répond dans les temps.\n");

    AvailabilityRequest request = AvailabilityRequest.newBuilder()
            .setStartDate("2027-12-15")
            .setEndDate("2027-12-18")
            .setNumberOfGuests(2)
            .build();

    try {
      long startTime = System.currentTimeMillis();

      AvailabilityResponse response = stub
              .withDeadlineAfter(10, TimeUnit.SECONDS)
              .checkAvailability(request);

      long duration = System.currentTimeMillis() - startTime;

      System.out.println("Réponse reçue en " + duration + "ms");
      System.out.println("Success: " + response.getSuccess());
      System.out.println("Message: " + response.getMessage());
      System.out.println("Nombre d'offres: " + response.getOffersCount());

      assertTrue(response.getSuccess(), "La requête devrait réussir");

    } catch (StatusRuntimeException e) {
      System.out.println("Erreur: " + e.getStatus().getCode());
      System.out.println("   " + e.getStatus().getDescription());

      if (e.getStatus().getCode() == Status.Code.UNAVAILABLE) {
        fail("Serveur non disponible. Lancez hotelservice avec: ./mvnw spring-boot:run -Dspring-boot.run.profiles=h1");
      }
      throw e;
    }
  }

  @Test
  @DisplayName("Test: Vérification du statut TIMEOUT via getHotelStatus")
  void testHotelStatusTimeout() {
    System.out.println("=== TEST: STATUT HOTEL TIMEOUT ===\n");

    try {
      // Appel avec timeout très court
      stub.withDeadlineAfter(1, TimeUnit.SECONDS)
              .getHotelInfo(org.hotel.grpc.Empty.newBuilder().build());

      System.out.println("⚠️  Serveur a répondu - pas de timeout");

    } catch (StatusRuntimeException e) {
      Status.Code code = e.getStatus().getCode();
      System.out.println("Code reçu: " + code);

      // Mapper le code vers le statut applicatif
      String appStatus = switch (code) {
        case DEADLINE_EXCEEDED -> "TIMEOUT";
        case UNAVAILABLE -> "OFFLINE";
        case UNAUTHENTICATED -> "AUTH_FAILED";
        default -> "ERROR";
      };

      System.out.println("Statut applicatif: " + appStatus);

      if (code == Status.Code.DEADLINE_EXCEEDED) {
        System.out.println("\n✅ Timeout correctement détecté");
      }
    }
  }
}
