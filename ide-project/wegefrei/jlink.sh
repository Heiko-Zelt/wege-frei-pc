jlink \
  --add-modules ALL-MODULE-PATH \
  --add-modules de.heikozelt.wegefrei \
  --module-path "$JAVA_HOME/jmods:build/runtime-libs:build/libs" \
  --output build/jlink \
  --launcher customjrelauncher=de.heikozelt.wegefrei/de.heikozelt.wegefrei