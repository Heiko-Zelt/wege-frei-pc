set WEGE_FREI_VERSION=1.0.4
set JAVA_HOME=C:\Users\Hi\.jdks\corretto-17.0.6
set WIX_HOME=C:\Program Files (x86)\WiX Toolset v3.11

set PATH=%JAVA_HOME%\bin;%WIX_HOME%\bin;%PATH%

rem Change code page to UTF-8
chcp 65001

jpackage ^
  --name WegeFrei ^
  --app-version %WEGE_FREI_VERSION% ^
  --description "Wege frei! Falschparker fotografieren, Formular ausfüllen und absenden!" ^
  --vendor "Heiko Zelt" ^
  --about-url https://github.com/Heiko-Zelt/wege-frei-pc ^
  --input build\libs ^
  --main-jar wegefrei-%WEGE_FREI_VERSION%.jar ^
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
