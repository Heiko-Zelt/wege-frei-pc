WEGE_FREI_VERSION=1.0.3
export JAVA_HOME=/home/heiko/.jdks/corretto-17.0.6

export PATH=$JAVA_HOME/bin:$PATH
unset CLASSPATH

jpackage \
  --name WegeFrei \
  --app-version $WEGE_FREI_VERSION \
  --description 'Wege frei! Falschparker fotografieren, Formular ausfüllen und absenden!' \
  --vendor 'Heiko Zelt' \
  --about-url https://github.com/Heiko-Zelt/wege-frei-pc \
  --input build/libs \
  --main-jar wegefrei-$WEGE_FREI_VERSION.jar \
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
