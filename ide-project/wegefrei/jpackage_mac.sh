WEGE_FREI_VERSION=1.0.2

unset CLASSPATH
#export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
export PATH=/Library/Java/JavaVirtualMachines/jdk-17.0.5.jdk/Contents/Home/bin:$PATH

jpackage \
  --name WegeFrei \
  --app-version $WEGE_FREI_VERSION \
  --description 'Wege frei! Falschparker fotografieren, Formular ausfüllen und absenden!' \
  --vendor 'Heiko Zelt' \
  --about-url https://github.com/Heiko-Zelt/wege-frei-pc \
  --input build/libs \
  --main-jar wegefrei-$WEGE_FREI_VERSION.jar \
  --main-class de.heikozelt.wegefrei.WegeFrei \
  --dest build/package \
  --type dmg \
  --mac-package-name "Wege frei!" \
  --verbose