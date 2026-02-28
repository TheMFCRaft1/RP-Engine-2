# RP Engine 2 - Core Roleplay Mod

Ein leistungsstarkes Framework für Roleplay-Server in Minecraft (Forge 1.20.1). Diese Mod bietet alle essenziellen Systeme für eine tiefgreifende RP-Erfahrung.

## 🚀 Hauptfeatures

### 👤 Charakter-System
- **Charakter-Management**: Vollständige Erstellung von Charakteren mit Vorname, Nachname, Alter, Geschlecht und Hintergrundgeschichte.
- **Datenbank-Anbindung**: Alle Daten werden permanent in einer SQLite-Datenbank gespeichert.
- **ID-System**: Jede Charakter-ID ist einzigartig und mit der Spieler-UUID verknüpft.

### 💰 Wirtschaftssystem (Economy)
- **Mehrwährungs-System**: Unterstützung für virtuelles Bargeld, Bankguthaben sowie Materialwährungen (Diamanten/Smaragde).
- **Physisches Bargeld**: Geld-Items für direkten Handel zwischen Spielern.
- **ATM (Geldautomat)**: Ein spezieller Block für Ein- und Auszahlungen.
- **Firmeneigentum**: Verwaltung von Firmenkonten und Mitgliedern.
- **Shops & NPCs**: Flexibles Shop-System mit Anbindung an NPCs für den Handel.

### 💼 Job-System
- **Berufszweige**: Vordefinierte Jobs wie Polizei und Rettungsdienst mit Rang-Hierarchien.
- **Dienst-Status**: Umschalten zwischen Freizeit und Dienst (`onDuty`).
- **Gehalt-Automatisierung**: Automatische Gehaltszahlungen alle 20 Minuten für aktive Spieler im Dienst.

### 🚑 Medizinisches System
- **Downed State**: Spieler sterben nicht sofort, sondern fallen bei 0 HP in einen bewusstlosen Zustand.
- **Medizinische Ausrüstung**: Bandagen zur Heilung.
- **Schutz**: Bewusstlose Spieler sind vor weiterem Schaden geschützt, während sie auf Hilfe warten.

### 👮 Polizei & Illegales
- **Fahndung & Haft**: Systeme für Handschellen und Gefängnisstrafen mit persistenten Timern.
- **Durchsuchen**: Spieler können andere Spieler auf illegale Gegenstände (via NBT markiert) durchsuchen.
- **Dietrich (Lockpick)**: Werkzeug zum Aufbrechen von verschlossenen Objekten.

### 🛠 Interaktion & UI
- **Radial-Menü**: Ein modernes, kreisförmiges Auswahlmenü für schnelle Interaktionen (Taste `V`).
- **Objekt-Sicherung**: Türen und Truhen können mit Schlüsseln verriegelt werden.
- **ID-Card**: Zeige anderen Spielern deinen Ausweis, um deine Identität zu bestätigen.
- **Chat-System**: Lokaler Chat, Funkgeräte und OOC-Kommunikation.

## 🛠 Technische Details
- **Version**: Minecraft 1.20.1 (Forge)
- **Datenbank**: SQLite (lokal gespeichert unter `mods/rpengine/database.db`)
- **Netzwerk**: Effiziente Client-Server-Kommunikation via Custom Packets.

---
*Entwickelt für immersive Roleplay-Erlebnisse.*
