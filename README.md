# Wege frei! PC

Wege-frei! PC ist ein Programm für den PC/Mac/Raspberry Pi/Desktop/Laptop, um Falschparker_innen zu melden. Die Idee zu diesem Projekt basiert auf der Wegeheld-App und der Weg.li-Website.

![Screenshot](doc/screenshot1.png)

## Besonderheit

Alle Daten (Beweisfotos und Meldungen) werden auf dem eigenen PC gespeichert. Der Versand erfolgt über das persönliche E-Mail-Postfach direkt an das zuständige Ordnungsamt. Hierdurch ergibt sich ein Geschwindigkeitsvorteil und im Sinne der Datensparsamkeit werden die Daten nicht durch zusätzliche Dienstleister verarbeitet. Das Layout und Look & Feel ist flexibel an die Bedürfnisse der Anwender_in anpassbar.

## Typischer Ablauf

 1. Falsch parkende Fahrzeuge fotografiern. Es können mehrere Fotos mit Kfz-Kennzeichen, Verkehrszeichen, HU- & Umwelt-Plakette, Anfang und Ende der Beoabachtung, etc... gemacht werden.
 1. Fotos (z.B. via USB-Kabel) vom Smartphone in ein spezielles Verzeichnis auf den PC kopieren.
 1. Fotos nach Metadaten (GPS-Geo-Position, Datum und Uhrzeit) scannen.
 1. Meldungen erfassen. Einige Daten wie Tat-Datum, -Uhrzeit, -Dauer, -Ort und Adresse werden automatisch im Formular eingetragen. Kontrolle der Fotos durch integrierten Bildbetrachter und des Tatorts durch Kartenanzeige.
 1. Meldungen über das eigene E-Mail-Postfach an das Ordnungsamt senden.
 
## Installation

Installationsdateien werden in Kürze zur Verfügung gestellt. Für technisch begabte Anwender_innen, bitte das GIT-Repo clonen und mit Gradle eine .jar-Datei bauen.

Nach der Installation kannst du direkt Meldungen erfassen. Wenn du die Beweiss-Fotos von deinen Urlaubsfotos, etc... trennen möchtest, kannst du unter Einstellungen einen anderen Ordner angeben. Bevor Du Meldungen an das Ordnungsamt übertragen kannst, musst du deine Zeugen-Daten angeben. Annonyme Anzeigen werden nicht akzeptiert. Außerdem werden die Konfigurations-Daten eines Postausgangs-Servers (SMTP) benötigt.

![Screenshot Einstellungen](doc/screenshot_settings.png)


## Empfehlungen

 * Fotos verschlüsselt speichern (z.B. mit VeraCrypt)
 * Beim E-Mail-Versand TLS-Verschlüsselung verwenden
 
## Datenschutz

Die Software verwendet Web-Services/APIs. Bei der Benutzung wird deine IP-Adresse daher an folgende Websites übermittelt:
 * https://tile.openstreetmap.org/ für die Kartendartstellung (Download von Kachel-Bildern)
 * https://nominatim.openstreetmap.org/ für die Suche nach Post-Anschrifts-Adressen von Tatort-Geo-Koordinaten

Bei Nutzung des Internets wird deine IP-Adresse natürlich auch an den Internet-Zugangs-Provider übermittelt und beim Versand von E-Mails an den E-Mail-Provider.
