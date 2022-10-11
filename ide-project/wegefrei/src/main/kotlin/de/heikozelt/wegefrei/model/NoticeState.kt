package de.heikozelt.wegefrei.model

/**
 * <ul>
 *   <li>INCOMPLETE: Es sind (noch) nicht alle Pflichfelder ausgefüllt.</li>
 *   <li>COMPLETE: Es sind alle Pflichtfelder ausgefüllt.</li>
 *   <li>SENT: Es wurde mindestens eine E-Mail erfolgreich versendet.
 *       Es ist keine weitere Bearbeitung mehr möglich.</li>
 * </ul>
 */
enum class NoticeState {
    INCOMPLETE, COMPLETE, SENT
}