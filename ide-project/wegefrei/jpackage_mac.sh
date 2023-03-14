WEGE_FREI_VERSION=1.0.3
export JAVA_HOME=/Library/Java/JavaVirtualMachines/amazon-corretto-17.jdk/Contents/Home

unset CLASSPATH
export PATH=$JAVA_HOME/bin:$PATH

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
