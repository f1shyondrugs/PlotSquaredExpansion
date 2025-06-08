# PlotSquared Expansion Plugin

Ein PlotSquared-Erweiterungsplugin, das eine benutzerfreundliche GUI für die Verwaltung von Plot-Einstellungen bietet.

## Features

- **GUI-basierte Plot-Verwaltung**: Einfache Bedienung über eine grafische Benutzeroberfläche
- **Fly-Berechtigung**: Aktiviere/Deaktiviere das Fliegen auf deinem Plot
- **PvP-Einstellungen**: Kontrolliere PvP auf deinem Plot
- **Mob-Angriffe**: Verwalte Tier- und Monster-Angriffe
- **Explosionen**: Kontrolliere Explosionen auf deinem Plot
- **Feuer-Ausbreitung**: Verwalte die Ausbreitung von Feuer
- **Flüssigkeits-Fluss**: Kontrolliere Wasser- und Lava-Fluss
- **Redstone**: Aktiviere/Deaktiviere Redstone-Funktionalität
- **Wetter & Zeit**: Setze benutzerdefiniertes Wetter und Zeit für deinen Plot
- **Musik**: Wähle Hintergrundmusik für deinen Plot

## Anforderungen

- **Minecraft**: 1.21+
- **PlotSquared**: 7.3.8+
- **Java**: 21+

## Installation

1. Lade die neueste Version der `PlotSquaredExpansion.jar` herunter
2. Platziere die Datei in deinem `plugins/` Ordner
3. Stelle sicher, dass PlotSquared installiert ist
4. Starte den Server neu

## Verwendung

### Befehle

- `/plot settings` oder `/p settings` - Öffnet die Plot-Einstellungen GUI

### Berechtigung

- Nur Plot-Besitzer können die Einstellungen ihres Plots bearbeiten
- Du musst auf deinem Plot stehen, um die GUI zu öffnen

### GUI-Navigation

Die GUI zeigt verschiedene Plot-Flags als Items an:

- **Feder/Elytra**: Fly-Berechtigung umschalten
- **Schwert**: PvP ein-/ausschalten
- **Fleisch**: Tier-Angriffe kontrollieren
- **Zombie-Kopf**: Monster-Angriffe kontrollieren
- **TNT**: Explosionen kontrollieren
- **Feuerzeug**: Feuer-Ausbreitung kontrollieren
- **Wassereimer**: Flüssigkeits-Fluss kontrollieren
- **Redstone**: Redstone-Funktionalität kontrollieren
- **Uhr**: Wetter und Zeit einstellen
- **Jukebox**: Musik auswählen

Klicke einfach auf ein Item, um die entsprechende Einstellung zu ändern!

## Entwicklung

### Kompilierung

```bash
mvn clean package
```

Die kompilierte JAR-Datei findest du im `target/` Ordner.

### Abhängigkeiten

- Paper API 1.21.1
- PlotSquared Core 7.3.8
- PlotSquared Bukkit 7.3.8

## Support

Bei Problemen oder Fragen erstelle ein Issue auf GitHub.

## Lizenz

Dieses Projekt steht unter der MIT-Lizenz.

---

**Entwickelt von Das_F1sHy312** 