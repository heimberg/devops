---
documentclass: scrartcl
mainfont: Roboto
title: Semesterarbeit Aufgabe 3
author: Matthias Heimberg
---

# Aufgabe 3: JMeter und ZAP

Im Rahmen der Aufgabe 3 soll die Webapplikation mit JMeter und ZAP getestet werden (vgl. [Aufgabe 3](https://moodle.ffhs.ch/mod/assign/view.php?id=4133081)).

## JMeter
Zunächst wird, wie in der [Jenkins-Dokumentation](https://www.jenkins.io/doc/book/using/using-jmeter-with-jenkins/) beschrieben, das [Performance Plugin](https://plugins.jenkins.io/performance) installiert. 
### Docker Image
In dieser Arbeit wird, wie bereits in Aufgabe 2 beschrieben, versucht, möglichst alle benötigten Services als Docker Container zu starten. Dazu wird ein Docker Image für JMeter verwendet. Das Docker Image wird von [https://hub.docker.com/r/justb4/jmeter](https://hub.docker.com/r/justb4/jmeter) bezogen, es ist kein offizielles Docker Image von JMeter. Für ein produktives Setup mit Docker sollte ein eigenes Image erstellt und gewartet werden, dies wird aus Zeitgründen in dieser Arbeit nicht gemacht. 

### Zugriff auf Docker Socket
Um Docker Container aus Jenkins (welches hier selbst in einem Container läuft) starten zu können, gibt es unterschiedliche Möglichkeiten:
- Docker in Docker (DinD)
- Zugriff auf Docker Socket (Docker Socket Bind Mount)
In [https://jpetazzo.github.io/2015/09/03/do-not-use-docker-in-docker-for-ci/](https://jpetazzo.github.io/2015/09/03/do-not-use-docker-in-docker-for-ci/) wird aus verschiedenen Gründen (Sicherheit, Kompatibilität) empfohlen, auf Docker in Docker zu verzichten und stattdessen auf den Docker Socket zu binden. Dieser Zugriff ist nicht ganz trivial, da der Jenkins Container nicht als `root` läuft und der Docker Socket nur für `root` zugänglich ist. Dazu wurden folgende Ergänzungen an der `docker-compose.yml` Datei vorgenommen:
1. Ein spezifisches Docker Network wurde erstellt, damit die Container untereinander kommunizieren können (vgl. [https://docs.docker.com/network/bridge/](https://docs.docker.com/network/bridge/))
2. Der Docker Socket wird an den Jenkins Container gebunden (vgl. [https://docs.docker.com/engine/security/rootless/](https://docs.docker.com/engine/security/rootless/))

Damit sieht die `docker-compose.yml` Datei wie folgt aus:
```yaml
version: "3.8"
services:
  jenkins:
      image: jenkins/jenkins:lts
      container_name: jenkins
      user: root
      ports:
          - "8080:8080"
          - "50000:50000"
      volumes:
          - ./jenkins-data:/var/jenkins_home
          - //var/run/docker.sock://var/run/docker.sock
      restart: always
      networks:
          - jenkins

  sonarqube:
      image: sonarqube:latest
      container_name: sonarqube
      ports:
          - "9000:9000"
          - "9092:9092"
      volumes:
          - ./sonarqube-data:/opt/sonarqube/data
          - ./sonarqube-extensions:/opt/sonarqube/extensions
          - ./sonarqube-logs:/opt/sonarqube/logs
          - ./sonarqube-temp:/opt/sonarqube/temp
      restart: always
      networks:
          - jenkins

networks:
    jenkins:
        driver: bridge
```
Wird `docker compose` auf einem Windows Host ausgeführt, muss zunächst die Variable `COMPOSE_CONVERT_WINDOWS_PATHS=1` gesetzt werden, damit der Pfad korrekt gemountet wird. Hierzu wird eine `.env` Datei erstellt, welche folgenden Inhalt hat:
```env
COMPOSE_CONVERT_WINDOWS_PATHS=1
```
Diese Datei muss im selben Verzeichnis wie die `docker-compose.yml` Datei liegen. Zudem muss das `Docker` Plugin in Jenkins installiert werden. Über `Dashboard > Jenkins verwalten > Configure Clouds` muss Docker wie folgt eingerichtet werden:

![](./img/Docker1.png)

Unter `Docker Host URI` muss der Pfad zum Docker Socket eingetragen werden, der in der `docker-compose.yml` Datei definiert wurde. Mittels `Test Connection` kann die Verbindung getestet werden. 

Ist Docker im Jenkins Container installiert, kann nun aus dem Jenkins Container auf den Docker Socket zugegriffen werden. Damit lässt sich JMeter in einem Docker Container parallel zum Jenkins Container starten und ausführen. Die Test-Scripte müssen im Verzeichnis `/jmeter-data/scripts` liegen, damit sie vom Jenkins Container aus gefunden werden können.

### JMeter Test-Scripts
Die Test Scripts wurden mit einer lokalen Installation von JMeter erstellt und als `.jmx` Dateien exportiert. Die Test-Scripts werden im Verzeichnis `/jmeter-data/scripts` abgelegt. Die Test-Scripts sind in der `docker-compose.yml` Datei als Volume gemountet. 

## Probleme und deren Lösung
- `docker compose` lässt sich nicht starten, da ein Port bereits gelegt ist. Lösung: belegte Ports lassen sich unter Windows mittels des PoweShell-Befehls `Get-Process -Id (Get-NetTCPConnection -LocalPort <PORT>).OwningProcess` herausfinden. Anschliessend kann der entsprechende Prozess beendet werden.
- Das `performance plugin` steht im Jenkins-GUI nicht zur Verfügung. Das Plugin wird innerhalb des laufenden Jenkins Containers manuell via über den CLI Befehl `jenkins-plugin-cli --plugins performance:3.20` installiert.
- Docker ist im Jenkins Container nicht installiert. Lösung: Mittels `docker exec -u root -it jenkins /bin/bash` als `root` in den Jenkins Container wechseln und mit `curl https://get.docker.com/ > dockerinstall && chmod 777 dockerinstall && ./dockerinstall` das Docker-CLI installieren. 
- Im Jenkins Container fehlen die Berchtigungen, um `docker` auszuführen. Lösung: `docker-compose.yml` anpassen, damit der Jenkins Container als `root` läuft (als schneller Fix innerhalb dieser Modulaufgabe, für den produktiven Einsatz nicht empfohlen).
- Sonarqube lässt sich nicht mehr starten, nachdem der Container auf zwei unterschiedlichen Maschinen welche auf die gleichen persistenten Daten zugreifen, gestartet wurde. Lösung: Die persistenten Daten löschen und den Container neu starten. Darauf muss Sonarqube gemäss Aufgabe 1 neu konfiguriert werden (Account, Secret, Webhook, Quality Gate).
- Fehlende Persmissions auf dem Docker Socket des Hosts. Lösung: `user: root` in der `docker-compose.yml` Datei hinzufügen. Dies sorgt dafür, dass der Jenkins Container als `root` läuft. Dies ist jedoch nicht empfohlen, da dadurch die Sicherheit des Containers beeinträchtigt wird (temporärer Fix für die Modulaufgabe, für den produktiven Einsatz nicht empfohlen)
- 

// https://davelms.medium.com/run-jenkins-in-a-docker-container-part-3-run-as-root-user-12b9624a340b