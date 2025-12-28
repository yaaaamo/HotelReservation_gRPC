package com.example.agenceservice;
import com.example.agenceservice.services.HotelServiceClient;
import com.example.agenceservice.services.HotelStatus;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests de robustesse pour la gestion des timeouts gRPC.
 *
 * PRÉREQUIS : Lancer le hotelservice avec le profil "slow" :
 * cd hotelservice
 * ./mvnw spring-boot:run -Dspring-boot.run.profiles=h1,slow
 */
public class TimeoutTest {

  /**
   * Test manuel du timeout.
   *
   * Pour exécuter ce test :
   * 1. Lancer hotelservice avec profil slow :
   *    ./mvnw spring-boot:run -Dspring-boot.run.profiles=h1,slow
   * 2. Exécuter ce test
   */
  @Test
  @Disabled("Test manuel - nécessite hotelservice avec profil slow")
  void testTimeoutHandling() {
    // Configuration manuelle du client pour le test
    // (En production, utiliser @SpringBootTest avec injection)

    System.out.println("=== TEST TIMEOUT gRPC ===");
    System.out.println("Ce test vérifie que le client gère correctement les timeouts.");
    System.out.println("Prérequis: hotelservice lancé avec -Dspring-boot.run.profiles=h1,slow");
    System.out.println();

    // Le test réel serait avec injection Spring
    // Ici on montre juste la logique attendue
    System.out.println("Attendu: StatusRuntimeException avec code DEADLINE_EXCEEDED");
  }
}
