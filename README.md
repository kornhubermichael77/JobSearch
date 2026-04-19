# JobSearch Backend

Backend der JobSearch-Anwendung: eine Spring-Boot-API zum strukturierten Verwalten von Bewerbungen, Firmen, Kommunikationsverläufen und persönlichen Adressen. Das Projekt ist als Portfolio- und Lernprojekt aufgebaut, enthält aber bereits mehrere produktionsnahe Konzepte wie Multi-Tenancy, Datenbankmigrationen, Security mit Session-Login und konsistente Fehlerantworten.

## Highlights

- Multi-Tenant-Architektur mit eigener Datenbank pro User/Tenant
- Automatische Provisionierung neuer Tenant-Datenbanken bei der Registrierung
- Flyway-Migrationen für Auth-Datenbank und Tenant-Datenbanken
- Session-basierte Authentifizierung mit Spring Security, CSRF-Schutz und Remember-Me
- Passwort-Reset per Token-Workflow und Mail-Versand
- CRUD-API für Jobs, Firmen, Adressen und Kommunikationsereignisse
- Timeline-Endpoint mit Filtern, Paging und Sortierung
- Typisierte Kommunikationseinträge wie `MAIL`, `PHONE`, `INTERVIEW`, `TRIAL` und `WEBFORM`
- Saubere Trennung zwischen Controller, Service, Repository, Mapper und DTOs
- Zentrale, strukturierte Fehlerbehandlung für Validierung und Business-Regeln

## Projektziel

Die API unterstützt eine Single-Page-Application dabei, den gesamten Bewerbungsprozess an einer Stelle abzubilden:

- Firmen und Kontakte verwalten
- Bewerbungen und deren Status nachverfolgen
- Kommunikation chronologisch dokumentieren
- persönliche Adressen für Wegzeiten und Zuordnungen pflegen
- mehrere Benutzer technisch sauber voneinander isolieren

## Tech-Stack

| Bereich | Technologie |
|---|---|
| Sprache | Java 21 |
| Framework | Spring Boot 3.3 |
| Web/API | Spring Web |
| Security | Spring Security |
| Persistenz | Spring Data JPA / Hibernate |
| Datenbank | MariaDB |
| Migrationen | Flyway |
| Mail | Spring Mail |
| Boilerplate-Reduktion | Lombok |
| Build Tool | Maven Wrapper (`mvnw`, `mvnw.cmd`) |
| Tests | JUnit 5, Spring Test, MockMvc |

## Architektur

Das Backend folgt einer klassischen Schichtenarchitektur:

- `controller`: REST-Endpunkte und HTTP-spezifisches Verhalten
- `service`: Fachlogik, Validierung von Business-Regeln und Orchestrierung
- `domain.entity`: JPA-Entities für das Kernmodell
- `domain.repository`: Datenzugriff mit Spring Data JPA und projektionbasierten Queries
- `dto`: Request- und Response-Objekte für die API
- `mapper`: Umwandlung zwischen Entities und DTOs
- `auth`: Benutzer, Rollen, Passwort-Reset und aktuelle User-Kontexte
- `security`: Spring-Security-Konfiguration und UserDetails-Integration
- `multitenancy`: Tenant-Kontext, DataSource-Routing, DB-Provisionierung und Migrationen
- `config`: globale Fehlerbehandlung und Konfigurationsobjekte

### Architekturprinzipien

- API und Domänenmodell sind über DTOs entkoppelt.
- Fachlogik liegt in Services, nicht in den Controllern.
- Migrationslogik ist strikt von der Fachlogik getrennt.
- Tenant-Isolation wird technisch über eigene Datenbanken gelöst, nicht nur über Filterspalten.

## Domänenmodell

Zentrale fachliche Bausteine:

- `UserEntity`, `Role`, `PasswordResetTokenEntity`
- `Company`
- `Job`
- `Address`
- `Communication` als Basistyp mit Spezialisierungen:
  - `MailCommunication`
  - `PhoneCommunication`
  - `TalkCommunication`
  - `InterviewCommunication`
  - `TrialCommunication`
  - `WebformCommunication`

Ergänzt wird das Modell durch Status- und Typ-Enums wie `JobStatus`, `CommunicationStatus`, `CommunicationType` und `CommunicationDirection`.

## Interessante Features

### 1. Datenbankbasierte Multi-Tenancy

Jeder neu registrierte Benutzer erhält eine eigene Tenant-Datenbank. Die Anwendung:

- erzeugt beim Registrieren automatisch einen technischen Tenant-Namen
- legt die Datenbank an
- migriert das Schema mit Flyway
- speichert die Tenant-Zuordnung in der Auth-Datenbank
- setzt den Tenant pro Request über den authentifizierten Benutzer in den `TenantContext`

Zusätzlich werden beim Start der Anwendung alle bekannten Tenant-Datenbanken automatisch auf den neuesten Migrationsstand gebracht.

### 2. Security mit Session, CSRF und Remember-Me

Das Backend setzt nicht auf JWT, sondern auf klassische Session-Authentifizierung mit Spring Security. Enthalten sind:

- Login über `/api/auth/login`
- Logout über `/api/auth/logout`
- CSRF-Token-Endpunkt `/api/auth/csrf`
- Remember-Me-Cookie `JOBSEARCH_REMEMBER_ME`
- JSON-Antworten für Login-Erfolg, Login-Fehler und unauthentifizierte Requests

Das ist besonders passend für ein SPA-Setup, das mit Session-Cookies arbeitet.

### 3. Passwort-Reset-Workflow

Der Reset-Prozess ist mehrstufig umgesetzt:

- Anfrage über E-Mail-Adresse
- Generierung eines langen Raw-Tokens
- Speicherung nur als SHA-256-Hash
- Ablaufzeit von 30 Minuten
- Markierung als verwendet nach erfolgreichem Reset
- Mail-Versand über eigenen Service

### 4. Timeline für Kommunikationshistorie

Ein zentrales Feature der Anwendung ist die Timeline aller Kommunikationsereignisse. Der Endpoint unterstützt:

- Filter nach `jobId`
- Filter nach Kommunikationstyp
- Filter nach Person
- Filter nach Status
- Filter ab einem Datum
- Paging und Sortierung nach Datum absteigend

Damit lässt sich der Verlauf einer Bewerbung gut auswerten und im Frontend übersichtlich darstellen.

### 5. Typisierte Kommunikationsobjekte

Kommunikation wird nicht als generischer Freitext gespeichert, sondern als fachlich typisierte Einträge. Die Erstellung läuft über eine Factory und spezialisierte Creator-Klassen. Dadurch kann jeder Typ eigene Felder und Regeln haben, zum Beispiel:

- `MAIL`: Adresse, Betreff, Anhänge, Richtung
- `PHONE`: Nummer, Richtung
- `TALK`: Ort, Kontext
- `INTERVIEW` und `TRIAL`: Dauer, Fazit
- `WEBFORM`: URL, Screenshot

### 6. Projektionbasierte Listen-Endpunkte

Für Job- und Firmenlisten werden an einigen Stellen gezielt reduzierte oder angereicherte Daten zurückgegeben, etwa:

- Joblisten mit Anzahl zugeordneter Kommunikationseinträge
- Firmen mit aggregierter Anzahl an Jobs
- reduzierte Job-Daten für Filteroptionen im Frontend

Das reduziert Mapping-Overhead und unterstützt performante Listenansichten.

### 7. Konsistente Fehlerstruktur

Der `GlobalExceptionHandler` erzeugt strukturierte API-Fehlerantworten für:

- Bean-Validation-Fehler
- Constraint-Verletzungen
- ungültige Request-Parameter oder Enums
- fachliche Fehler wie `NotFound`, `Conflict`, `BadRequest`
- Security-Fehler
- generische Serverfehler

Dadurch ist das Frontend nicht auf uneinheitliche Fehlermeldungen angewiesen.

## API-Überblick

Wichtige Endpunkte des Backends:

### Auth

- `POST /api/auth/register`
- `POST /api/auth/login`
- `POST /api/auth/logout`
- `GET /api/auth/csrf`
- `GET /api/auth/me`
- `POST /api/auth/forgot-password`
- `POST /api/auth/reset-password`

### Companies

- `GET /api/companies`
- `GET /api/companies/{id}`
- `POST /api/companies`
- `PUT /api/companies/{id}`
- `DELETE /api/companies/{id}`

### Jobs

- `GET /api/jobs`
- `GET /api/jobs/options`
- `POST /api/jobs`
- `PUT /api/jobs/{id}`
- `PATCH /api/jobs/{id}/address`
- `DELETE /api/jobs/{id}`

### Addresses

- `POST /api/companies/{companyId}/addresses`
- `POST /api/jobs/{jobId}/addresses`
- `POST /api/users/me/addresses`
- `GET /api/users/me/addresses`
- `GET /api/addresses/{id}`
- `PUT /api/addresses/{id}`
- `DELETE /api/addresses/{id}`

### Communications

- `GET /api/communications/{id}`
- `POST /api/communications`
- `PUT /api/communications/{id}`
- `DELETE /api/communications/{id}`

### Timeline und Optionen

- `GET /api/timeline`
- `GET /api/job-status-options`
- `GET /api/communication-status-options`

## Datenbank-Setup

Die Anwendung verwendet zwei Datenbankkontexte:

- Auth-Datenbank für Benutzer, Rollen und Passwort-Reset-Tokens
- Tenant-Datenbanken für die eigentlichen Fachdaten pro Benutzer

### Lokale Konfiguration

1. Kopiere `src/main/resources/application-dev.example.properties` nach `src/main/resources/application-dev.properties`.
2. Trage lokale MariaDB-Zugangsdaten ein.
3. Stelle sicher, dass der verwendete DB-User Datenbanken anlegen darf, da neue Tenant-Datenbanken automatisch erstellt werden.

Beispielhafte Properties:

```properties
app.datasource.auth.url=jdbc:mariadb://localhost:3306/jobsearch_users?createDatabaseIfNotExist=true
app.datasource.auth.username=your_db_user
app.datasource.auth.password=your_db_password

app.datasource.tenant.base-url=jdbc:mariadb://localhost:3306/
app.datasource.tenant.username=your_db_user
app.datasource.tenant.password=your_db_password
```

## Starten des Projekts

### Voraussetzungen

- Java 21
- MariaDB
- Maven Wrapper nutzbar über das Repository

### Development-Start

```bash
./mvnw spring-boot:run
```

Unter Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

## Tests

Vorhanden sind bereits Tests für zentrale Controller- und Service-Bausteine, unter anderem:

- `JobController`
- `CommunicationController`
- `TimelineController`
- `GlobalExceptionHandler`
- `JobService`
- `CommunicationService`
- `TimelineService`

Tests starten:

```bash
./mvnw test
```

## Geeignet für GitHub und Präsentationen

Wenn das Projekt vorgestellt wird, sind diese Punkte besonders relevant:

- Eigenständige Multi-Tenant-Architektur mit automatischer Tenant-DB-Erstellung
- Sauberer Security-Stack mit Session, CSRF und Remember-Me
- Realistischer Passwort-Reset-Prozess mit gehashten Tokens
- Fachlich starkes Domänenmodell statt rein generischer CRUD-Struktur
- Timeline als zentrales UX- und Analyse-Feature
- Flyway-Migrationen für Auth- und Tenant-Schema
- Solide API-Struktur mit DTOs, Mappern und globalem Error-Handling

## Aktueller Stand

Das Repository zeigt ein bereits klar strukturiertes Backend mit produktionsnahen Konzepten. Besonders stark sind Tenant-Isolation, Security-Grundlagen, Migrationen und die modellierte Kommunikationshistorie. Für den nächsten Reifegrad wären typischerweise API-Dokumentation mit OpenAPI, breitere Integrationstests und Deployment-/Container-Setup sinnvolle nächste Schritte.
