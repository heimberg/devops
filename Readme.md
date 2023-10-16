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
Die Applikation ist dann unter http://localhost:7000 erreichbar. Die Rest API ist unter http://localhost:7000/api/users erreichbar. 
Die Prometheus Metriken sind unter http://localhost:7000/metrics erreichbar.

## Docker
Die Applikation kann auch als lokaler Docker Container gestartet werden. Dazu muss das Projekt lokal ausgecheckt werden und im Terminal folgender Befehl ausgeführt werden:
```bash
./gradlew jibDockerBuild
```

## Deployment
Die Applikation wird als Docker Container via Google Cloud Registry auf Google Cloud Run deployed. Sie ist unter [https://devops-d4bqj7s2iq-ez.a.run.app](https://devops-d4bqj7s2iq-ez.a.run.app) erreichbar.

## Dokumentation
Die Dokumentation zur Semesterarbeit wird im Ordner `/Documentation` versioniert.
- [Aufgabe 1](./Documentation/Aufgabe_1.md)
- [Aufgabe 2](./Documentation/Aufgabe_2.md)
- [Aufgabe 3](./Documentation/Aufgabe_3.md)
- [Aufgabe 4](./Documentation/Aufgabe_4.md)
- [Aufgabe 5](./Documentation/Aufgabe_5.md)