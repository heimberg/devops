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


## Probleme und deren Lösung
- `docker compose` lässt sich nicht starten, da ein Port bereits gelegt ist. Lösung: belegte Ports lassen sich unter Windows mittels des PoweShell-Befehls `Get-Process -Id (Get-NetTCPConnection -LocalPort <PORT>).OwningProcess` herausfinden. Anschliessend kann der entsprechende Prozess beendet werden.
- Das `performance plugin` steht im GUI nicht zur Verfügung. Das Plugin wird innerhalb des laufenden Jenkins Containers manuell via `jenkins-plugin-cli --plugins performance:3.20` installiert.
