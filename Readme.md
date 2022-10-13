# Users Rest API - Semesterarbeit DevOps
Die Webapplikation stellt eine Rest API mit CRUD Methoden zur Userverwaltung zur Verfügung. Die Applikation wird innerhalb der Semesterarbeit im Modul DevOps verwendet. Die Applikation ist in Java mit [Javalin](https://javalin.io) implementiert. 
## Endpoints
| Endpoint        | Methode | Beschreibung                                    |
|-----------------|---------|-------------------------------------------------|
| /api/users      | GET     | Liefert alle User                               |
| /api/users/{id} | GET     | Liefert den User mit der entsprechenden ID      |
| /api/users      | POST    | Erstellt einen neuen User                       |
| /api/users/{id} | PUT     | Aktualisiert den User mit der entsprechenden ID |
| /api/users/{id} | DELETE  | Löscht den User mit der entsprechenden ID       |
## User
| Attribut        | Typ | Beschreibung |
|-----------------|--| --- |
| id              | Integer | ID des Users |
| name            | String | Name des Users |
| email           | String | E-Mail Adresse des Users |
| birthYear       | Integer | Geburtsjahr des Users |

## Build
Das Projekt wird mittels Gradle gebaut. Dazu muss das Projekt lokal ausgecheckt werden und im Terminal folgender Befehl ausgeführt werden:
```bash
./gradlew build
```
## Starten der Applikation
Die Applikation kann lokal gestartet werden. Dazu muss das Projekt lokal ausgecheckt werden und im Terminal folgender Befehl ausgeführt werden:
```bash
./gradlew run
```
Die Applikation ist dann unter http://localhost:7000 erreichbar. Die Rest API ist unter http://localhost:7000/api/users erreichbar. Die Swagger UI ist unter http://localhost:7000/swagger-ui/ erreichbar.
## Dokumentation
Die Dokumentation zur Semesterarbeit wird im Ordner `/Documentation` versioniert.
- [Aufgabe 1](./Documentation/Aufgabe_1.md)


