# ReadMe - Exécution du Projet

## Prérequis

- Java 17 ou supérieur
- Maven 3.9+ (ou utiliser le wrapper `mvnw` inclus)
- Ports disponibles : 8091, 8092 (HTTP agences), 9091, 9092 (gRPC hôtels)

## Compilation du Projet

Compiler d'abord le module `commons` qui contient les classes générées à partir du fichier `.proto` :
```bash
# Compiler le module commons (génère les stubs gRPC)
cd commons
mvn clean install

# Compiler le service hôtel
cd ../hotelservice
mvn clean compile

# Compiler le service agence
cd ../agenceservice
mvn clean compile
```

## Lancement des Services

### Étape 1 : Démarrer les Services Hôtel

Ouvrir deux terminaux pour lancer les deux hôtels :

**Terminal 1 - Grand Hotel Paris (port gRPC 9091) :**
```bash
cd hotelservice
mvn spring-boot:run -Dspring-boot.run.profiles=h1
```

**Terminal 2 - Grand Hotel Occitanie (port gRPC 9092) :**
```bash
cd hotelservice
mvn spring-boot:run -Dspring-boot.run.profiles=h2
```

### Étape 2 : Démarrer les Services Agence

Ouvrir deux terminaux supplémentaires :

**Terminal 3 - Agence Travel2GO (port HTTP 8091) :**
```bash
cd agenceservice
mvn spring-boot:run -Dspring-boot.run.profiles=a1
```

**Terminal 4 - Agence VoyagePLUS (port HTTP 8092) :**
```bash
cd agenceservice
mvn spring-boot:run -Dspring-boot.run.profiles=a2
```

## Accès aux Interfaces Web

Une fois tous les services démarrés, accéder aux interfaces via un navigateur :

| Service | URL | Description |
|---------|-----|-------------|
| Agence Travel2GO | http://localhost:8091 | Agence avec réduction 10% |
| Agence VoyagePLUS | http://localhost:8092 | Agence avec réduction 20% |

## Tests de Robustesse (Timeout)

Pour tester la gestion des timeouts :

**Terminal 1 - Lancer l'hôtel avec le profil slow :**
```bash
cd hotelservice
mvn spring-boot:run -Dspring-boot.run.profiles=h1,slow
```

**Terminal 2 - Exécuter les tests :**
```bash
cd agenceservice
mvn test -Dtest=GrpcTimeoutIntegrationTest#testDeadlineExceeded
```

**Note :** Pour exécuter ces tests, il faut s'assurer que l'annotation `@Disabled` est retirée des méthodes de test. Cette annotation est parfois utilisée pendant le développement pour éviter l'exécution automatique des tests d'intégration qui nécessitent un serveur actif.

## Arrêt des Services

Pour arrêter proprement tous les services, utiliser `Ctrl+C` dans chaque terminal. Les connexions gRPC seront fermées proprement grâce à la méthode `@PreDestroy`.

## Données de Test Préconfigurées

Le système est initialisé avec des données de test pour faciliter la démonstration. Ces données sont créées automatiquement au démarrage via la classe `DataInitializer`.

### Chambres Disponibles

Chaque hôtel dispose de 6 chambres de types différents :

| Numéro | Type | Lits | Prix/nuit (base) |
|--------|------|------|------------------|
| 101 | SIMPLE | 1 | 80 EUR |
| 102 | SIMPLE | 1 | 80 EUR |
| 201 | DOUBLE | 2 | 120 EUR |
| 202 | DOUBLE | 2 | 120 EUR |
| 301 | SUITE | 2 | 200 EUR |
| 401 | FAMILIALE | 4 | 250 EUR |

### Fenêtres de Disponibilité

Les disponibilités sont configurées pour la période de décembre 2027 :

| Chambres | Date début | Date fin | Quantité |
|----------|------------|----------|----------|
| 101, 102 (SIMPLE) | 10/12/2027 | 20/12/2027 | 1 |
| 201, 202 (DOUBLE) | 10/12/2027 | 20/12/2027 | 2 |
| 301 (SUITE) | 20/12/2027 | 30/12/2027 | 3 |
| 401 (FAMILIALE) | 20/12/2027 | 30/12/2027 | 4 |

**Important :** Pour tester le système, utiliser des dates dans ces plages :
- **10-20 décembre 2027** : chambres SIMPLE et DOUBLE disponibles
- **20-30 décembre 2027** : chambres SUITE et FAMILIALE disponibles
