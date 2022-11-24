set JAVA_HOME=C:\Users\Hi\.jdks\temurin-17.0.5
set WIX_HOME=C:\Program Files (x86)\WiX Toolset v3.11
set PATH=%JAVA_HOME%\bin;%WIX_HOME%\bin;%PATH%

jpackage ^
  --name WegeFrei ^
  --app-version 1.0 ^
  --description "Wege frei! Falschparker fotografieren, Formular ausfüllen und absenden!" ^
  --vendor "Heiko Zelt" ^
  --about-url https://github.com/Heiko-Zelt/wege-frei-pc ^
  --input build\libs ^
  --main-jar wegefrei-1.0-SNAPSHOT.jar ^
  --main-class de.heikozelt.wegefrei.WegeFrei ^
  --dest build\jpackage ^
  --type msi ^
  --win-console ^
  --win-dir-chooser ^
  --win-menu ^
  --win-shortcut ^
  --icon jpackage-resource\windows\WegeFrei.ico ^
  --verbose

rem --runtime-image build\jlink