package de.heikozelt.wegefrei.model

/**
 * Status, welchen den Lebenszyklus einer Meldung widerspiegelt.
 * <ol>
 *   <li>INCOMPLETE: Es sind (noch) nicht alle Pflichtfelder ausgefüllt.</li>
 *   <li>COMPLETE: Es sind alle Pflichtfelder ausgefüllt, die Anwenderin hat die Nachricht aber noch nicht gesendet.</li>
 *   <li>FINALIZED: Die Meldung/E-Mail liegt im Postausgang. Es ist keine weitere Bearbeitung mehr möglich.</li>
 *   <li>SENT: Es wurde (mindestens) eine E-Mail-Nachricht erfolgreich versendet.</li>
 * </ol>
 * todo Prio 4: maybe add NEW: Das Notice-Objekt wurde angelegt, aber noch nicht geändert. Speichern ist sinnlos.
 */
enum class NoticeState {
    INCOMPLETE, COMPLETE, FINALIZED, SENT
}