unset CLASSPATH
#export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
export PATH=/Library/Java/JavaVirtualMachines/jdk-17.0.5.jdk/Contents/Home/bin:$PATH

jpackage \
  --name WegeFrei \
  --app-version 1.0.1 \
  --description 'Wege frei! Falschparker fotografieren, Formular ausfüllen und absenden!' \
  --vendor 'Heiko Zelt' \
  --about-url https://github.com/Heiko-Zelt/wege-frei-pc \
  --input build/libs \
  --main-jar wegefrei-1.0.1.jar \
  --main-class de.heikozelt.wegefrei.WegeFrei \
  --dest build/package \
  --type dmg \
  --mac-package-name "Wege frei!" \
  --verbose