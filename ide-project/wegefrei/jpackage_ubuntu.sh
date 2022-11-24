jpackage \
  --name WegeFrei \
  --app-version 1.0 \
  --description 'Wege frei! Falschparker fotografieren, Formular ausfüllen und absenden!' \
  --vendor 'Heiko Zelt' \
  --about-url https://github.com/Heiko-Zelt/wege-frei-pc \
  --input build/libs \
  --main-jar wegefrei-1.0-SNAPSHOT.jar \
  --main-class de.heikozelt.wegefrei.WegeFrei \
  --dest build/jpackage \
  --install-dir /opt \
  --type deb \
  --linux-deb-maintainer hz@heikozelt.de \
  --linux-shortcut \
  --icon jpackage-resource/ubuntu/WegeFrei.png \
  --resource-dir jpackage-resource/ubuntu \
  --verbose

# --icon src/main/resources/WegeFrei.png \
# --runtime-image build/jlink