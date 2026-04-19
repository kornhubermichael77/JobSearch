# JobSearch Backend Präsentation

## Folie 1 - Projektüberblick

- Backend für eine Job-Search-SPA mit Fokus auf Struktur und Nachvollziehbarkeit
- Ziel: Bewerbungen, Firmen, Kommunikation und Adressen zentral verwalten
- Technische Umsetzung als REST-API mit Spring Boot
- Besonderheit: produktionsnahe Features statt reinem CRUD-Demo-Projekt

## Folie 2 - Problem und Nutzen

- Bewerbungsprozesse erzeugen viele verteilte Informationen
- Firmen, Kontakte, Status und Kommunikationsverläufe gehen schnell verloren
- Das Backend bündelt diese Daten in einem konsistenten Modell
- Ergebnis: bessere Übersicht, Nachverfolgung und Auswertbarkeit

## Folie 3 - Tech-Stack

- Java 21, Spring Boot 3, Maven
- Spring Web, Spring Data JPA, Hibernate
- Spring Security mit Session-Login und CSRF
- MariaDB als Datenbank, Flyway für Migrationen
- Tests mit JUnit und MockMvc

## Folie 4 - Architektur

- REST-Controller für die HTTP-Schnittstellen
- Services für Fachlogik und Business-Regeln
- Repositories für Datenzugriff und Projektionen
- DTOs und Mapper zur Trennung von API und Persistenz
- Separate Module für Auth, Security und Multi-Tenancy

## Folie 5 - Fachmodell

- Zentrale Entitäten: User, Company, Job, Address, Communication
- Communication ist ein Basistyp mit mehreren Untertypen
- Beispiele: Mail, Phone, Interview, Trial, Webform
- Dadurch bleibt die API fachlich präzise und erweiterbar

## Folie 6 - Highlight: Multi-Tenancy

- Jeder registrierte User erhält eine eigene Tenant-Datenbank
- Bei der Registrierung wird die Datenbank automatisch angelegt
- Flyway migriert neue und bestehende Tenant-Datenbanken
- Der aktive Tenant wird pro Request aus dem eingeloggten User aufgelöst
- Vorteil: starke technische Isolation der Fachdaten

## Folie 7 - Highlight: Security

- Session-basierte Authentifizierung statt JWT
- Geschützte API mit Spring Security
- CSRF-Schutz für SPA-kompatibles Cookie-Setup
- Remember-Me für persistente Anmeldung
- Passwort-Reset mit Token-Workflow und Mail-Versand

## Folie 8 - Highlight: Timeline

- Kommunikationsereignisse werden chronologisch zusammengeführt
- Filterbar nach Job, Typ, Person, Status und Datum
- Paging und Sortierung sind direkt im Endpoint integriert
- Das ist im Frontend besonders wertvoll für Übersicht und Verlauf

## Folie 9 - API und UX-Nutzen

- CRUD-Endpunkte für Firmen, Jobs, Adressen und Kommunikation
- Zusätzliche Options-Endpoints für Filter und Statuswerte
- Aggregierte Daten für Listenansichten, z. B. Kommunikationsanzahl pro Job
- Einheitliche Fehlerstruktur erleichtert Frontend-Handling

## Folie 10 - Technische Qualität

- Klare Schichtenarchitektur statt Logik im Controller
- Flyway für versioniertes Datenbankschema
- Globales Error-Handling für valide API-Antworten
- Tests für Controller, Services und Exception-Handling

## Folie 11 - Warum das Projekt interessant ist

- Mehr als ein Standard-CRUD-Projekt
- Multi-Tenancy und Security zeigen reale Architekturthemen
- Das Domänenmodell ist auf einen echten Anwendungsfall zugeschnitten
- Gute Basis für spätere Erweiterungen wie OpenAPI oder Docker

## Folie 12 - Mögliche Live-Demo oder Screenshots

- Login und Benutzerkontext
- Jobliste mit Status und Kommunikationsanzahl
- Timeline mit Filtern
- Firmen- oder Jobdetailansicht
- Passwort-Reset oder Tenant-Workflow als Architekturfolie

## Sprechernotizen / optionale Abschlussfolie

- Fokus im Vortrag: Architekturentscheidungen statt nur Endpunktliste
- Besonders stark sind Tenant-Isolation, Timeline und Security-Konzept
- Gute Screenshots: Timeline, Jobliste, Login, Datenbankdiagramm
- SVGs aus diesem Ordner können direkt in PowerPoint verwendet werden
