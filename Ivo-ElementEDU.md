# ElementEDU - Ivo Quiring

## Einführung

In dieser BLL (Besonderen Lernleistung) haben Yonas Nieder Fernández und ich,
Falk Odin Ivo Quiring uns das Ziel gesetzt ein Schulportal zu entwickeln. In welchem
ich die folgenden Aufträge zu bearbeiten habe:

- Kurse & Klassen
- Einen übersichtlichen Stundenplan
- Nutzersystem & Verwaltung
- Hausaufgaben erstellen und abgeben
- Datenverwaltung und Sicherheit des Systems.
- Nutzerliste mit Filtern

Hierzu haben Yonas und ich uns auf die folgenden Spring Boot im Hintergrund und Angular im Vordergrund geeinigt, dazu
später mehr.

## Technologien
Um dieses Projekt zu starten, mussten wir uns erst mal auf Grundbausteine einigen, 
welche uns das Arbeiten an dem Projekt ermöglichen und vereinfachen. Angefangen mit dem Editor. 
Hierbei haben wir uns für [IntelliJ von Jetbrains](https://www.jetbrains.com/de-de/idea/) entschieden,
da diese sogenannte IDE (Integrated Development Environment), zu Deutsch Entwicklungsumgebung, vielerlei 
unterstützende Funktionen bietet, die das Entwickeln mit java, html, type-/javascript und vielem mehr um einen signifikanten 
Faktor vereinfacht. Anschließen einigten wir uns auf ein sogenanntes [Framework](#begriffe). Dies taten wir sowohl für den 
[Vordergrund](#vordergrund) als auch für den [Hintergrund](#hintergrund) des Programms.

> ### Vordergrund
>
> Damit eine Webseite **gut aussieht** und **funktional** ist, benötigt man einen sogenannten [Frontend-Server](#begriffe). Dieser wird benötigt, um sinnvoll mit dem **Backend** zu interagieren. Das Backend versteht nur **REST-Anfragen**, welche vom Frontend gesendet werden.
> 
> ---
> 
> **Beispiel:**
> 
> Ein Nutzer gibt einen **Nutzernamen** in ein **Suchfeld** ein.
> 
> - Der eingegebene String wird von dem **Frontend** in eine **POST-Request** umgewandelt und an das **Backend** gesendet.
> - Das Backend antwortet daraufhin mit einer Liste der **Nutzer**, die mit der Suche übereinstimmen.
> 
> ---
> 
> Durch diese Interaktion zwischen Frontend und Backend wird eine reibungslose Funktionalität der Webseite gewährleistet.

> ### Hintergrund
> Damit eine Webseite **funktionieren** kann, muss ein Server im Hintergrund die **logischen Anweisungen** ausführen. Dieser Server wird **Backend-Server** genannt. Er läuft ausschließlich auf dem **Server**, und kein Nutzer hat Zugriff auf dessen **Programmierung**.
> 
> In diesem System haben wir uns für [**Spring Boot**](https://spring.io/projects/spring-boot) entschieden, da es ein **performantes** und **benutzerfreundliches** [Framework](#begriffe) ist, das die Annahme von **REST-Anfragen** des [Frontend-Servers](#begriffe) erheblich vereinfacht.
> Ein weiterer Vorteil von Spring Boot ist die Möglichkeit, durch die Integration mit **Spring Security** robuste **Sicherheitslösungen** umzusetzen, um sensible Daten zuverlässig zu schützen.

### Begriffe

| Begriff   | Bedeutung                                                                             |
|-----------|---------------------------------------------------------------------------------------|
| Framework | Eine Grundstruktur, die einem System, Konzept oder Text zugrunde liegt                |
| Frontend  | Der Teil einer Webseite oder Anwendung, den die Nutzer direkt sehen und nutzen        |
| Backend   | Der unsichtbare Teil einer Anwendung, der die Logik, Datenbanken und Server verwaltet |
